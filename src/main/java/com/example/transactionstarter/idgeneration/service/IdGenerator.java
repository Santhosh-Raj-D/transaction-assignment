package com.example.transactionstarter.idgeneration.service;

import com.example.transactionstarter.idgeneration.exception.InvalidIdInputException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates backend-side IDs from meaningful entity/business fields
 * instead of a plain random UUID.
 *
 * Standalone, top-level package so it can be reused by {@code clientflow},
 * {@code society}, or any future flow - without living inside the
 * assigned {@code transaction} package.
 *
 * Each ID is built from the caller-supplied fields (so it stays
 * traceable back to real data) plus a short hash suffix derived from
 * those fields and a nanosecond/sequence value, so repeated creations
 * with similar data never collide.
 */
public final class IdGenerator {

    // Guarantees uniqueness even when two IDs are requested within the
    // same nanosecond tick (e.g. rapid-fire calls/tests).
    private static final AtomicLong SEQUENCE = new AtomicLong();

    private IdGenerator() {
        // utility class, no instances
    }

    public static String generateResidentId(String name, String flatId) {
        requireAtLeastOne("resident", name, flatId);
        return build("RES", flatId, name);
    }

    public static String generateMerchantId(String businessName, String category) {
        requireAtLeastOne("merchant", businessName, category);
        return build("MER", category, businessName);
    }

    public static String generateAdminId(String name, String societyId) {
        requireAtLeastOne("admin", name, societyId);
        return build("ADM", societyId, name);
    }

    public static String generatePropertyManagerId(String name, String societyId) {
        requireAtLeastOne("property manager", name, societyId);
        return build("PMG", societyId, name);
    }

    public static String generateGuardId(String name, String societyId) {
        requireAtLeastOne("guard", name, societyId);
        return build("GRD", societyId, name);
    }

    public static String generateBillId(String residentId, String head, String reference) {
        requireAtLeastOne("bill", residentId, head);
        return build("BILL", residentId, head + "-" + reference);
    }

    /**
     * Builds a transaction ID from the payer, receiver, and payment
     * type/head of the transaction being created, instead of relying on a
     * client-supplied or purely random ID.
     */
    public static String generateTransactionId(String payerId, String receiverId, String typeOrHead) {
        requireAtLeastOne("transaction", payerId, receiverId);
        return build("TXN", payerId, receiverId + "-" + typeOrHead);
    }

    /** Ensures at least one of the two most meaningful fields for this ID was actually supplied. */
    private static void requireAtLeastOne(String idKind, String fieldA, String fieldB) {
        if (isBlank(fieldA) && isBlank(fieldB)) {
            throw new InvalidIdInputException(
                    "Cannot generate " + idKind + " id: no meaningful fields were provided");
        }
    }

    private static String build(String prefix, String primaryField, String secondaryField) {
        String normalizedPrimary = normalize(primaryField);
        long uniquePart = System.nanoTime() ^ SEQUENCE.incrementAndGet();
        int hash = Objects.hash(primaryField, secondaryField, uniquePart);
        String suffix = Integer.toHexString(hash).toUpperCase();
        return prefix + "-" + normalizedPrimary + "-" + suffix;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalize(String value) {
        if (isBlank(value)) {
            return "NA";
        }
        String cleaned = value.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (cleaned.isEmpty()) {
            return "NA";
        }
        return cleaned.length() > 10 ? cleaned.substring(0, 10) : cleaned;
    }
}
