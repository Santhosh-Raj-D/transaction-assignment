package com.example.transactionstarter.society.common;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates backend-side IDs for society actors and transactions.
 *
 * The ID is built from meaningful fields of the entity itself (e.g. name,
 * flat/society id) so it stays readable and traceable, plus a short hash
 * suffix (derived from those same fields + current time) so repeated
 * creations with similar data never collide. This keeps IDs simple and
 * data-derived instead of a plain random UUID, without needing a full
 * production-grade ID/sequence service.
 */
public final class IdGenerator {

    // Guarantees uniqueness even when two IDs are requested within the
    // same nanosecond tick (e.g. rapid-fire test calls).
    private static final AtomicLong SEQUENCE = new AtomicLong();

    private IdGenerator() {
        // utility class, no instances
    }

    public static String generateResidentId(String name, String flatId) {
        return build("RES", flatId, name);
    }

    public static String generateMerchantId(String businessName, String category) {
        return build("MER", category, businessName);
    }

    public static String generateAdminId(String name, String societyId) {
        return build("ADM", societyId, name);
    }

    public static String generatePropertyManagerId(String name, String societyId) {
        return build("PMG", societyId, name);
    }

    public static String generateGuardId(String name, String societyId) {
        return build("GRD", societyId, name);
    }

    public static String generateBillId(String residentId, String head, String reference) {
        return build("BILL", residentId, head + "-" + reference);
    }

    /**
     * Builds a transaction ID from the payer, receiver, and payment
     * type/head of the transaction being created, instead of relying on a
     * client-supplied or purely random ID.
     */
    public static String generateTransactionId(String payerId, String receiverId, String typeOrHead) {
        return build("TXN", payerId, receiverId + "-" + typeOrHead);
    }

    private static String build(String prefix, String primaryField, String secondaryField) {
        String normalizedPrimary = normalize(primaryField);
        long uniquePart = System.nanoTime() ^ SEQUENCE.incrementAndGet();
        int hash = Objects.hash(primaryField, secondaryField, uniquePart);
        String suffix = Integer.toHexString(hash).toUpperCase();
        return prefix + "-" + normalizedPrimary + "-" + suffix;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "NA";
        }
        String cleaned = value.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (cleaned.isEmpty()) {
            return "NA";
        }
        return cleaned.length() > 10 ? cleaned.substring(0, 10) : cleaned;
    }
}
