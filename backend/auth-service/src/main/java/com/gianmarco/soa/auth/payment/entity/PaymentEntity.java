package com.gianmarco.soa.auth.payment.entity;

import com.gianmarco.soa.auth.payment.enums.PaymentMethod;
import com.gianmarco.soa.auth.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_ride_request", columnList = "ride_request_id"),
        @Index(name = "idx_client", columnList = "client_id"),
        @Index(name = "idx_driver", columnList = "driver_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_transaction", columnList = "transaction_code")
})
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ride_request_id", nullable = false)
    private Long rideRequestId;

    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    private String paymentMethodDetail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_code", unique = true)
    private String transactionCode;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    private String currency = "PEN";
    private Double fee = 0.0;
    private Double finalAmount;

    private String description;
    private String observations;

    private Boolean notified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        paymentDate = LocalDateTime.now();
        if (status == null) status = PaymentStatus.PENDING;
        if (currency == null) currency = "PEN";
        if (fee == null) fee = 0.0;
        if (finalAmount == null) finalAmount = amount;
        notified = false;
        // Generate simulated transaction code
        if (transactionCode == null) {
            transactionCode = "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
        }
    }

    // Business methods
    public void complete() {
        this.status = PaymentStatus.COMPLETED;
        this.processedDate = LocalDateTime.now();
    }

    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.observations = reason;
        this.processedDate = LocalDateTime.now();
    }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
        this.processedDate = LocalDateTime.now();
    }

    public void markNotified() {
        this.notified = true;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDateTime processedDate) { this.processedDate = processedDate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }

    public Double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public Boolean getNotified() { return notified; }
    public void setNotified(Boolean notified) { this.notified = notified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}