package com.gianmarco.soa.auth.payment.dto;

import com.gianmarco.soa.auth.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequestDTO {

    @NotNull(message = "Ride request ID is required")
    private Long rideRequestId;

    private Long assignmentId;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    private Long driverId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String paymentMethodDetail;

    private String currency = "PEN";
    private Double fee = 0.0;
    private String description;

    // Default constructor
    public PaymentRequestDTO() {}

    // Constructor with required fields
    public PaymentRequestDTO(Long rideRequestId, Long clientId, Double amount, PaymentMethod paymentMethod) {
        this.rideRequestId = rideRequestId;
        this.clientId = clientId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Long getRideRequestId() { return rideRequestId; }
    public void setRideRequestId(Long rideRequestId) { this.rideRequestId = rideRequestId; }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentMethodDetail() { return paymentMethodDetail; }
    public void setPaymentMethodDetail(String paymentMethodDetail) { this.paymentMethodDetail = paymentMethodDetail; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "PaymentRequestDTO{" +
                "rideRequestId=" + rideRequestId +
                ", clientId=" + clientId +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", currency='" + currency + '\'' +
                '}';
    }
}