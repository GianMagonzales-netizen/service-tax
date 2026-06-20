package com.gianmarco.soa.auth.ride_request.entity;

import com.gianmarco.soa.auth.driver.enums.ServiceType;
import com.gianmarco.soa.auth.ride_request.enums.RideStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride_requests", indexes = {
        @Index(name = "idx_client_id", columnList = "client_id"),
        @Index(name = "idx_driver_id", columnList = "driver_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_client_status", columnList = "client_id, status")
})
public class RideRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "driver_id")
    private Long driverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;  // STANDARD or CONFORT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status = RideStatus.PENDING;

    // Location data
    @Column(name = "pickup_lat", nullable = false)
    private Double pickupLat;

    @Column(name = "pickup_lng", nullable = false)
    private Double pickupLng;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "destination_lat", nullable = false)
    private Double destinationLat;

    @Column(name = "destination_lng", nullable = false)
    private Double destinationLng;

    @Column(name = "destination_address")
    private String destinationAddress;

    // Ride details
    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "estimated_fare")
    private Double estimatedFare;

    @Column(name = "final_fare")
    private Double finalFare;

    @Column(name = "payment_method")
    private String paymentMethod;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Assignment tracking
    @Column(name = "assignment_attempts")
    private Integer assignmentAttempts = 0;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expiresAt = createdAt.plusMinutes(5);  // 5 minutes to accept
        if (status == null) status = RideStatus.PENDING;
        if (assignmentAttempts == null) assignmentAttempts = 0;
    }

    // Business methods
    public void assignDriver(Long driverId) {
        this.driverId = driverId;
        this.status = RideStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
        this.assignmentAttempts++;
    }

    public void acceptByDriver() {
        this.status = RideStatus.IN_PROGRESS;
        this.acceptedAt = LocalDateTime.now();
    }

    public void rejectByDriver(String reason) {
        this.status = RideStatus.PENDING;
        this.driverId = null;
        this.rejectionReason = reason;
        this.assignedAt = null;
    }

    public void startRide() {
        this.status = RideStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void completeRide(Double finalFare) {
        this.status = RideStatus.COMPLETED;
        this.finalFare = finalFare;
        this.completedAt = LocalDateTime.now();
    }

    public void cancelRide() {
        this.status = RideStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = RideStatus.EXPIRED;
        this.driverId = null;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean canBeReassigned() {
        return status == RideStatus.PENDING ||
                status == RideStatus.EXPIRED ||
                (status == RideStatus.ASSIGNED && isExpired());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public Double getPickupLat() { return pickupLat; }
    public void setPickupLat(Double pickupLat) { this.pickupLat = pickupLat; }

    public Double getPickupLng() { return pickupLng; }
    public void setPickupLng(Double pickupLng) { this.pickupLng = pickupLng; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public Double getDestinationLat() { return destinationLat; }
    public void setDestinationLat(Double destinationLat) { this.destinationLat = destinationLat; }

    public Double getDestinationLng() { return destinationLng; }
    public void setDestinationLng(Double destinationLng) { this.destinationLng = destinationLng; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(Double estimatedFare) { this.estimatedFare = estimatedFare; }

    public Double getFinalFare() { return finalFare; }
    public void setFinalFare(Double finalFare) { this.finalFare = finalFare; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getAssignmentAttempts() { return assignmentAttempts; }
    public void setAssignmentAttempts(Integer assignmentAttempts) { this.assignmentAttempts = assignmentAttempts; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}