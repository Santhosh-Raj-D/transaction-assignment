package com.example.transactionstarter.society.billing.service;

import com.example.transactionstarter.society.billing.domain.Bill;
import com.example.transactionstarter.society.billing.domain.BillStatus;
import com.example.transactionstarter.society.billing.dto.CreateBillRequest;
import com.example.transactionstarter.society.billing.exception.BillAlreadyPaidException;
import com.example.transactionstarter.society.billing.exception.BillNotFoundException;
import com.example.transactionstarter.society.billing.repository.BillRepository;
import com.example.transactionstarter.idgeneration.service.IdGenerator;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Raises bills for residents and settles them by reusing the existing
 * {@link TransactionService} rather than duplicating money-movement logic.
 */
@Service
public class BillingService {

    private final BillRepository billRepository;
    private final TransactionService transactionService;

    public BillingService(BillRepository billRepository, TransactionService transactionService) {
        this.billRepository = billRepository;
        this.transactionService = transactionService;
    }

    public Bill createBill(CreateBillRequest request) {
        Bill bill = new Bill(request.getResidentId(), request.getRaisedBy(), request.getHead(),
                request.getDescription(), request.getAmount(), request.getCurrency());
        return billRepository.save(bill);
    }

    public Bill getById(String id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new BillNotFoundException(id));
    }

    public List<Bill> getByResidentId(String residentId) {
        return billRepository.findByResidentId(residentId);
    }

    /**
     * Pays a pending bill: generates a transaction ID from the payer,
     * receiver and payment head (not manually entered), creates the
     * transaction via the existing transaction package, then marks the
     * bill PAID and links it to that transaction.
     */
    public Bill pay(String billId) {
        Bill bill = getById(billId);
        if (bill.getStatus() != BillStatus.PENDING) {
            throw new BillAlreadyPaidException(billId);
        }

        String transactionId = IdGenerator.generateTransactionId(
                bill.getResidentId(), bill.getRaisedBy(), bill.getHead().name());

        CreateTransactionRequest transactionRequest = new CreateTransactionRequest(
                transactionId, bill.getResidentId(), bill.getAmount(), bill.getCurrency(), TransactionType.PAYMENT);
        Transaction transaction = transactionService.create(transactionRequest);

        bill.markPaid(transaction.getId());
        return billRepository.save(bill);
    }
}
