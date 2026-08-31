package com.example.transactionstarter.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Payment-specific error handling. General validation/malformed-request
 * handling is already covered globally by
 * {@link com.example.transactionstarter.transaction.exception.GlobalExceptionHandler}.
 */
@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(PaymentOrderNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnsupportedPaymentMethodException.class)
    public ResponseEntity<Object> handleUnsupportedMethod(UnsupportedPaymentMethodException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentInstrumentException.class)
    public ResponseEntity<Object> handleInvalidInstrument(InvalidPaymentInstrumentException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Object> errorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
