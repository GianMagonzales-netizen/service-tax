package com.gianmarco.soa.auth.payment.dto;

import com.gianmarco.soa.auth.payment.entity.PaymentEntity;
import com.gianmarco.soa.auth.payment.enums.PaymentMethod;
import com.gianmarco.soa.auth.payment.enums.PaymentStatus;
import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private Long id;
    private Long rideRequestId;
    private Long assignmentId;
    private Long clientId;
    private Long driverId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private String paymentMethodDetail;
    private PaymentStatus status;
    private String transactionCode;
    private LocalDateTime paymentDate;
    private LocalDateTime processedDate;
    private String currency;
    private Double fee;
    private Double finalAmount;
    private String description;
    private LocalDateTime createdAt;

    // Default constructor
    public PaymentResponseDTO() {}

    // Constructor with all fields
    public PaymentResponseDTO(Long id, Long rideRequestId, Long assignmentId, Long clientId,
                              Long driverId, Double amount, PaymentMethod paymentMethod,
                              String paymentMethodDetail, PaymentStatus status, String transactionCode,
                              LocalDateTime paymentDate, LocalDateTime processedDate, String currency,
                              Double fee, Double finalAmount, String description, LocalDateTime createdAt) {
        this.id = id;
        this.rideRequestId = rideRequestId;
        this.assignmentId = assignmentId;
        this.clientId = clientId;
        this.driverId = driverId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentMethodDetail = paymentMethodDetail;
        this.status = status;
        this.transactionCode = transactionCode;
        this.paymentDate = paymentDate;
        this.processedDate = processedDate;
        this.currency = currency;
        this.fee = fee;
        this.finalAmount = finalAmount;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Factory method from Entity
    public static PaymentResponseDTO fromEntity(PaymentEntity entity) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(entity.getId());
        dto.setRideRequestId(entity.getRideRequestId());
        dto.setAssignmentId(entity.getAssignmentId());
        dto.setClientId(entity.getClientId());
        dto.setDriverId(entity.getDriverId());
        dto.setAmount(entity.getAmount());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentMethodDetail(entity.getPaymentMethodDetail());
        dto.setStatus(entity.getStatus());
        dto.setTransactionCode(entity.getTransactionCode());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setProcessedDate(entity.getProcessedDate());
        dto.setCurrency(entity.getCurrency());
        dto.setFee(entity.getFee());
        dto.setFinalAmount(entity.getFinalAmount());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "PaymentResponseDTO{" +
                "id=" + id +
                ", rideRequestId=" + rideRequestId +
                ", clientId=" + clientId +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", transactionCode='" + transactionCode + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}