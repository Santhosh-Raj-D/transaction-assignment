package com.example.transactionstarter.society.billing.exception;

import com.example.transactionstarter.society.actor.exception.ActorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles society-specific exceptions only. Deliberately does not extend
 * {@code ResponseEntityExceptionHandler} so it does not clash with the
 * existing transaction package's {@code GlobalExceptionHandler}, which
 * already covers validation and malformed-body errors application-wide.
 */
@RestControllerAdvice
public class SocietyExceptionHandler {

    @ExceptionHandler(BillNotPaidException.class)
    public ResponseEntity<Object> handleBillNotPaid(BillNotPaidException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(BillNotFoundException.class)
    public ResponseEntity<Object> handleBillNotFound(BillNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BillAlreadyPaidException.class)
    public ResponseEntity<Object> handleBillAlreadyPaid(BillAlreadyPaidException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ActorNotFoundException.class)
    public ResponseEntity<Object> handleActorNotFound(ActorNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Object> errorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
