package com.example.transactionstarter.payment.strategy;

/**
 * Shared, deterministic "processing" simulation used by every strategy.
 * A sender account reference starting with "FAIL" (case-insensitive)
 * simulates a declined payment; anything else that passes format
 * validation succeeds. Prefix (not exact match) so the trigger still fits
 * each method's own reference format (e.g. "fail@bank", "FAIL1234").
 * This keeps behavior deterministic for tests without any real gateway.
 */
final class PaymentSimulation {

    private PaymentSimulation() {
        // utility class, no instances
    }

    static boolean isSimulatedFailure(String accountRef) {
        return accountRef != null && accountRef.trim().toUpperCase().startsWith("FAIL");
    }
}
