package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.transactionstarter.payment.domain.Bank;
import com.example.transactionstarter.payment.domain.BankAccount;
import com.example.transactionstarter.payment.domain.PaymentMethod;
import com.example.transactionstarter.payment.domain.PaymentOrder;
import com.example.transactionstarter.payment.domain.PaymentStatus;
import com.example.transactionstarter.payment.domain.PaymentTransaction;
import com.example.transactionstarter.payment.dto.CreatePaymentRequest;
import com.example.transactionstarter.payment.dto.PaymentResponse;
import com.example.transactionstarter.payment.exception.UnsupportedPaymentMethodException;
import com.example.transactionstarter.payment.repository.BankAccountRepository;
import com.example.transactionstarter.payment.repository.BankRepository;
import com.example.transactionstarter.payment.repository.PaymentOrderRepository;
import com.example.transactionstarter.payment.repository.PaymentTransactionRepository;
import com.example.transactionstarter.payment.service.PaymentService;
import com.example.transactionstarter.payment.strategy.UpiPaymentStrategy;
import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.domain.TransactionStatus;
import com.example.transactionstarter.transaction.domain.TransactionType;
import com.example.transactionstarter.transaction.service.TransactionService;

/**
 * Verifies PaymentService orchestrates its collaborators correctly - it
 * does not duplicate transaction-processing logic, only reuses the
 * existing TransactionService.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {

    @Mock
    private PaymentOrderRepository paymentOrderRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionService transactionService;

    private CreatePaymentRequest upiRequest() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPayerId("RES-A101-1");
        request.setPayeeId("MER-GROC-1");
        request.setAmount(new BigDecimal("250.00"));
        request.setCurrency(CurrencyCode.INR);
        request.setMethod(PaymentMethod.UPI);
        request.setSenderBankName("HDFC");
        request.setSenderAccountRef("resident@hdfc");
        request.setReceiverBankName("ICICI");
        request.setReceiverAccountRef("merchant@icici");
        return request;
    }

    @Test
    void createAndProcessPayment_successfulUpiPayment_reusesTransactionServiceAndReturnsSuccess() {
        PaymentService service = new PaymentService(paymentOrderRepository, paymentTransactionRepository,
                bankRepository, bankAccountRepository, transactionService, List.of(new UpiPaymentStrategy()));

        CreatePaymentRequest request = upiRequest();

        Bank hdfc = new Bank("HDFC");
        Bank icici = new Bank("ICICI");
        when(bankRepository.findByName("HDFC")).thenReturn(Optional.of(hdfc));
        when(bankRepository.findByName("ICICI")).thenReturn(Optional.of(icici));

        BankAccount senderAccount = new BankAccount(hdfc.getId(), PaymentMethod.UPI, "resident@hdfc");
        BankAccount receiverAccount = new BankAccount(icici.getId(), PaymentMethod.UPI, "merchant@icici");
        when(bankAccountRepository.findByBankIdAndMethodAndAccountRef(hdfc.getId(), PaymentMethod.UPI, "resident@hdfc"))
                .thenReturn(Optional.of(senderAccount));
        when(bankAccountRepository.findByBankIdAndMethodAndAccountRef(icici.getId(), PaymentMethod.UPI, "merchant@icici"))
                .thenReturn(Optional.of(receiverAccount));

        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction pendingTransaction = new Transaction("TXN-1", "RES-A101-1", request.getAmount(),
                CurrencyCode.INR, TransactionType.PAYMENT);
        when(transactionService.create(any())).thenReturn(pendingTransaction);

        Transaction completedTransaction = new Transaction("TXN-1", "RES-A101-1", request.getAmount(),
                CurrencyCode.INR, TransactionType.PAYMENT);
        completedTransaction.setStatus(TransactionStatus.COMPLETED);
        when(transactionService.updateStatus("TXN-1", TransactionStatus.COMPLETED)).thenReturn(completedTransaction);

        PaymentResponse response = service.createAndProcessPayment(request);

        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals("TXN-1", response.getTransactionId());
    }

    @Test
    void createAndProcessPayment_failedProcessing_marksTransactionFailedNotSuccessful() {
        PaymentService service = new PaymentService(paymentOrderRepository, paymentTransactionRepository,
                bankRepository, bankAccountRepository, transactionService, List.of(new UpiPaymentStrategy()));

        CreatePaymentRequest request = upiRequest();
        request.setSenderAccountRef("fail@hdfc"); // triggers the deterministic simulated failure

        Bank hdfc = new Bank("HDFC");
        Bank icici = new Bank("ICICI");
        when(bankRepository.findByName("HDFC")).thenReturn(Optional.of(hdfc));
        when(bankRepository.findByName("ICICI")).thenReturn(Optional.of(icici));

        BankAccount senderAccount = new BankAccount(hdfc.getId(), PaymentMethod.UPI, "fail@hdfc");
        BankAccount receiverAccount = new BankAccount(icici.getId(), PaymentMethod.UPI, "merchant@icici");
        when(bankAccountRepository.findByBankIdAndMethodAndAccountRef(hdfc.getId(), PaymentMethod.UPI, "fail@hdfc"))
                .thenReturn(Optional.of(senderAccount));
        when(bankAccountRepository.findByBankIdAndMethodAndAccountRef(icici.getId(), PaymentMethod.UPI, "merchant@icici"))
                .thenReturn(Optional.of(receiverAccount));

        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction pendingTransaction = new Transaction("TXN-2", "RES-A101-1", request.getAmount(),
                CurrencyCode.INR, TransactionType.PAYMENT);
        when(transactionService.create(any())).thenReturn(pendingTransaction);

        Transaction failedTransaction = new Transaction("TXN-2", "RES-A101-1", request.getAmount(),
                CurrencyCode.INR, TransactionType.PAYMENT);
        failedTransaction.setStatus(TransactionStatus.FAILED);
        when(transactionService.updateStatus("TXN-2", TransactionStatus.FAILED)).thenReturn(failedTransaction);

        PaymentResponse response = service.createAndProcessPayment(request);

        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

    @Test
    void createAndProcessPayment_unsupportedMethod_throwsBeforeTouchingRepositories() {
        // Only a UPI strategy is registered, so requesting CARD must fail fast.
        PaymentService service = new PaymentService(paymentOrderRepository, paymentTransactionRepository,
                bankRepository, bankAccountRepository, transactionService, List.of(new UpiPaymentStrategy()));

        CreatePaymentRequest request = upiRequest();
        request.setMethod(PaymentMethod.CARD);

        assertThrows(UnsupportedPaymentMethodException.class, () -> service.createAndProcessPayment(request));
    }
}
