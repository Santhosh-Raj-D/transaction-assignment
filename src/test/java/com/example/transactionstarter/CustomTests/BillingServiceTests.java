package com.example.transactionstarter.CustomTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.transactionstarter.society.billing.domain.Bill;
import com.example.transactionstarter.society.billing.domain.BillStatus;
import com.example.transactionstarter.society.billing.domain.PaymentHead;
import com.example.transactionstarter.society.billing.dto.CreateBillRequest;
import com.example.transactionstarter.society.billing.exception.BillAlreadyPaidException;
import com.example.transactionstarter.society.billing.repository.BillRepository;
import com.example.transactionstarter.society.billing.service.BillingService;
import com.example.transactionstarter.transaction.domain.CurrencyCode;
import com.example.transactionstarter.transaction.domain.Transaction;
import com.example.transactionstarter.transaction.dto.CreateTransactionRequest;
import com.example.transactionstarter.transaction.service.TransactionService;

/**
 * Covers the bill lifecycle (create -> pay) and the transaction-ID
 * generation used when a bill is paid.
 */
@ExtendWith(MockitoExtension.class)
class BillingServiceTests {

    @Mock
    private BillRepository billRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BillingService billingService;

    @Test
    void createBill_generatesIdAndStartsPending() {
        CreateBillRequest request = new CreateBillRequest(
                "RES-A101-1", "ADM-S1-1", PaymentHead.MAINTENANCE, "March maintenance", new BigDecimal("1500.00"), CurrencyCode.INR);

        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill bill = billingService.createBill(request);

        assertNotNull(bill.getId());
        assertEquals(BillStatus.PENDING, bill.getStatus());
    }

    @Test
    void pay_onPendingBill_createsTransactionWithGeneratedIdAndMarksPaid() {
        Bill bill = new Bill("RES-A101-1", "ADM-S1-1", PaymentHead.MAINTENANCE, "March maintenance",
                new BigDecimal("1500.00"), CurrencyCode.INR);

        when(billRepository.findById(bill.getId())).thenReturn(Optional.of(bill));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction createdTransaction = new Transaction(
                "TXN-SOMETHING", bill.getResidentId(), bill.getAmount(), bill.getCurrency(),
                com.example.transactionstarter.transaction.domain.TransactionType.PAYMENT);
        when(transactionService.create(any(CreateTransactionRequest.class))).thenReturn(createdTransaction);

        Bill paid = billingService.pay(bill.getId());

        assertEquals(BillStatus.PAID, paid.getStatus());
        assertNotNull(paid.getTransactionId());
    }

    @Test
    void pay_onAlreadyPaidBill_throwsException() {
        Bill bill = new Bill("RES-A101-1", "ADM-S1-1", PaymentHead.MAINTENANCE, "March maintenance",
                new BigDecimal("1500.00"), CurrencyCode.INR);
        bill.markPaid("TXN-ALREADY-PAID");

        when(billRepository.findById(bill.getId())).thenReturn(Optional.of(bill));

        assertThrows(BillAlreadyPaidException.class, () -> billingService.pay(bill.getId()));
    }
}
