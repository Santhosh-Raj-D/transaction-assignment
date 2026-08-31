package com.example.transactionstarter.payment.service;

import com.example.transactionstarter.idgeneration.service.IdGenerator;
import com.example.transactionstarter.payment.domain.Bank;
import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.domain.PaymentOrder;
import com.example.transactionstarter.payment.domain.PaymentStatus;
import com.example.transactionstarter.payment.domain.PaymentTransaction;
import com.example.transactionstarter.payment.dto.CreatePaymentRequest;
import com.example.transactionstarter.payment.dto.PaymentResponse;
import com.example.transactionstarter.payment.exception.PaymentOrderNotFoundException;
import com.example.transactionstarter.payment.exception.UnsupportedPaymentMethodException;
import com.example.transactionstarter.payment.repository.BankAccountRepository;
import com.example.transactionstarter.payment.repository.BankRepository;
import com.example.transactionstarter.payment.repository.PaymentOrderRepository;
import com.example.transactionstarter.payment.repository.PaymentTransactionRepository;
import com.example.transactionstarter.payment.strategy.PaymentStrategy;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionStatus;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orchestrates the payment flow:
 *
 *   CreatePaymentRequest -&gt; PaymentOrder (INITIATED)
 *                        -&gt; PaymentStrategy resolved by PaymentMethod
 *                        -&gt; strategy.process() simulates the method-specific check
 *                        -&gt; existing {@link TransactionService#create} reused, unchanged
 *                        -&gt; PaymentTransaction records the result
 *
 * This class owns PaymentOrder/PaymentTransaction/Bank/BankAccount
 * persistence but never duplicates transaction-processing logic - it
 * only calls the existing {@link TransactionService}.
 */
@Service
public class PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BankRepository bankRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionService transactionService;
    private final Map<PaymentMethod, PaymentStrategy> strategiesByMethod;

    public PaymentService(PaymentOrderRepository paymentOrderRepository,
                           PaymentTransactionRepository paymentTransactionRepository,
                           BankRepository bankRepository,
                           BankAccountRepository bankAccountRepository,
                           TransactionService transactionService,
                           List<PaymentStrategy> strategies) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.bankRepository = bankRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionService = transactionService;
        this.strategiesByMethod = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::supportedMethod, Function.identity()));
    }

    @Transactional
    public PaymentResponse createAndProcessPayment(CreatePaymentRequest request) {
        PaymentStrategy strategy = strategiesByMethod.get(request.getMethod());
        if (strategy == null) {
            throw new UnsupportedPaymentMethodException(request.getMethod());
        }

        BankAccount senderAccount = resolveBankAccount(
                request.getSenderBankName(), request.getMethod(), request.getSenderAccountRef());
        BankAccount receiverAccount = resolveBankAccount(
                request.getReceiverBankName(), request.getMethod(), request.getReceiverAccountRef());

        PaymentOrder order = new PaymentOrder(
                request.getPayerId(), request.getPayeeId(), request.getAmount(), request.getCurrency(),
                request.getMethod(), senderAccount.getId(), receiverAccount.getId());
        order = paymentOrderRepository.save(order);

        // Format validation happens inside process(); an InvalidPaymentInstrumentException
        // here rolls back the PaymentOrder save above via @Transactional.
        boolean success = strategy.process(senderAccount, receiverAccount, request.getAmount());

        String transactionId = IdGenerator.generateTransactionId(
                request.getPayerId(), request.getPayeeId(), request.getMethod().name());
        Transaction transaction = transactionService.create(new CreateTransactionRequest(
                transactionId, request.getPayerId(), request.getAmount(), request.getCurrency(), TransactionType.PAYMENT));
        transaction = transactionService.updateStatus(
                transaction.getId(), success ? TransactionStatus.COMPLETED : TransactionStatus.FAILED);

        PaymentStatus paymentStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        PaymentTransaction paymentTransaction =
                paymentTransactionRepository.save(new PaymentTransaction(order.getId(), transaction.getId(), paymentStatus));

        order.setStatus(paymentStatus);
        order = paymentOrderRepository.save(order);

        return PaymentResponse.from(order, paymentTransaction);
    }

    public PaymentOrder getOrderById(String id) {
        return paymentOrderRepository.findById(id)
                .orElseThrow(() -> new PaymentOrderNotFoundException(id));
    }

    public List<PaymentTransaction> getTransactionsForOrder(String paymentOrderId) {
        getOrderById(paymentOrderId);
        return paymentTransactionRepository.findByPaymentOrderId(paymentOrderId);
    }

    /** Reuses an existing (bank, method, accountRef) instrument, or creates one - and its Bank - on first use. */
    private BankAccount resolveBankAccount(String bankName, PaymentMethod method, String accountRef) {
        Bank bank = bankRepository.findByName(bankName)
                .orElseGet(() -> bankRepository.save(new Bank(bankName)));
        return bankAccountRepository.findByBankIdAndMethodAndAccountRef(bank.getId(), method, accountRef)
                .orElseGet(() -> bankAccountRepository.save(new BankAccount(bank.getId(), method, accountRef)));
    }
}
