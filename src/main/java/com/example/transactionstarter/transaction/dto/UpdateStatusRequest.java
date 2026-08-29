package com.example.transactionstarter.transaction.dto;

import com.example.transactionstarter.transaction.domain.TransactionStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "status is required")
    private TransactionStatus status;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(TransactionStatus status) {
        this.status = status;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
