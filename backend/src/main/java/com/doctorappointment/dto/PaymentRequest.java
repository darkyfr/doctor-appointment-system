package com.doctorappointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    private Long appointmentId;
    @NotBlank
    private String bkashTransactionId;
    @NotNull
    private Double amount;

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getBkashTransactionId() {
        return bkashTransactionId;
    }

    public void setBkashTransactionId(String bkashTransactionId) {
        this.bkashTransactionId = bkashTransactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
