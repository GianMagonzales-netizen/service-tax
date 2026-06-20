package com.gianmarco.soa.auth.ride_assignment.entity;

import com.gianmarco.soa.auth.ride_assignment.enums.AssignmentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride_assignments", indexes = {
        @Index(name = "idx_ride_request_id", columnList = "ride_request_id"),
        @Index(name = "idx_driver_id", columnList = "driver_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_ride_request_status", columnList = "ride_request_id, status")
})
public class RideAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ride_request_id", nullable = false)
    private Long rideRequestId;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "driver_vehicle_plate")
    private String driverVehiclePlate;

    @Column(name = "driver_rating_at_time")
    private Double driverRatingAtTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "estimated_fare")
    private Double estimatedFare;

    @Column(name = "final_fare")
    private Double finalFare;

    @Column(name = "assignment_attempt")
    private Integer assignmentAttempt = 1;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        expiresAt = assignedAt.plusMinutes(5);  // 5 minutes to respond
        if (status == null) status = AssignmentStatus.PENDING;
        if (assignmentAttempt == null) assignmentAttempt = 1;
    }

    // Business methods
    public void accept() {
        this.status = AssignmentStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = AssignmentStatus.REJECTED;
        this.rejectionReason = reason;
        this.respondedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = AssignmentStatus.COMPLETED;
    }

    public void expire() {
        this.status = AssignmentStatus.EXPIRED;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean canBeReassigned() {
        return status == AssignmentStatus.REJECTED ||
                status == AssignmentStatus.EXPIRED ||
                (status == AssignmentStatus.PENDING && isExpired());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRideRequestId() { return rideRequestId; }
    public void setRideRequestId(Long rideRequestId) { this.rideRequestId = rideRequestId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverVehiclePlate() { return driverVehiclePlate; }
    public void setDriverVehiclePlate(String driverVehiclePlate) { this.driverVehiclePlate = driverVehiclePlate; }

    public Double getDriverRatingAtTime() { return driverRatingAtTime; }
    public void setDriverRatingAtTime(Double driverRatingAtTime) { this.driverRatingAtTime = driverRatingAtTime; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(Double estimatedFare) { this.estimatedFare = estimatedFare; }

    public Double getFinalFare() { return finalFare; }
    public void setFinalFare(Double finalFare) { this.finalFare = finalFare; }

    public Integer getAssignmentAttempt() { return assignmentAttempt; }
    public void setAssignmentAttempt(Integer assignmentAttempt) { this.assignmentAttempt = assignmentAttempt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}