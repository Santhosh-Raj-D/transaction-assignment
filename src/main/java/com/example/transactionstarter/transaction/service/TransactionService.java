package com.example.transactionstarter.transaction.service;

import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionStatus;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.exception.DuplicateTransactionException;
import com.example.transactionstarter.transaction.exception.InvalidStatusTransitionException;
import com.example.transactionstarter.transaction.exception.TransactionNotFoundException;
import com.example.transactionstarter.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TransactionService {

    /**
     * Legal status transitions. Anything not listed here as a valid
     * destination for the current status is rejected.
     *
     *   PENDING   -> COMPLETED, FAILED
     *   FAILED    -> PENDING   (retry)
     *   COMPLETED -> REVERSED  (refund)
     *   REVERSED  -> (terminal - no further transitions)
     */
    private static final Map<TransactionStatus, Set<TransactionStatus>> ALLOWED_TRANSITIONS =
            new EnumMap<>(TransactionStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(TransactionStatus.PENDING,
                EnumSet.of(TransactionStatus.COMPLETED, TransactionStatus.FAILED));
        ALLOWED_TRANSITIONS.put(TransactionStatus.FAILED,
                EnumSet.of(TransactionStatus.PENDING));
        ALLOWED_TRANSITIONS.put(TransactionStatus.COMPLETED,
                EnumSet.of(TransactionStatus.REVERSED));
        ALLOWED_TRANSITIONS.put(TransactionStatus.REVERSED,
                EnumSet.noneOf(TransactionStatus.class));
    }

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction create(CreateTransactionRequest request) {
        if (repository.existsById(request.getTransactionId())) {
            throw new DuplicateTransactionException(request.getTransactionId());
        }
        Transaction transaction = new Transaction(
                request.getTransactionId(),
                request.getCustomerId(),
                request.getAmount(),
                request.getCurrency(),
                request.getType()
        );
        return repository.save(transaction);
    }

    public Transaction getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public List<Transaction> getByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    /** Filters by status only, by type only, by both, or returns all when neither is given. */
    public List<Transaction> search(TransactionStatus status, TransactionType type) {
        if (status != null && type != null) {
            return repository.findByStatus(status).stream()
                    .filter(t -> t.getType() == type)
                    .toList();
        }
        if (status != null) {
            return repository.findByStatus(status);
        }
        if (type != null) {
            return repository.findByType(type);
        }
        return repository.findAll();
    }

    /** Dedicated refund action: reuses the existing status state-machine, no separate refund logic. */
    public Transaction refund(String id) {
        return updateStatus(id, TransactionStatus.REVERSED);
    }

    public Transaction updateStatus(String id, TransactionStatus newStatus) {
        Transaction transaction = getById(id);
        TransactionStatus currentStatus = transaction.getStatus();

        Set<TransactionStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.get(currentStatus);
        if (allowedNextStatuses == null || !allowedNextStatuses.contains(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        transaction.setStatus(newStatus);
        return repository.save(transaction);
    }
}
