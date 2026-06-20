package com.gianmarco.soa.auth.ride_assignment.dto;

import com.gianmarco.soa.auth.ride_assignment.entity.RideAssignmentEntity;
import com.gianmarco.soa.auth.ride_assignment.enums.AssignmentStatus;
import java.time.LocalDateTime;

public class AssignmentResponseDTO {

    private Long id;
    private Long rideRequestId;
    private Long driverId;
    private String driverName;
    private String driverVehiclePlate;
    private Double driverRatingAtTime;
    private AssignmentStatus status;
    private Double distanceKm;
    private Double estimatedFare;
    private Double finalFare;
    private Integer assignmentAttempt;
    private String rejectionReason;
    private LocalDateTime assignedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime expiresAt;

    // Default constructor
    public AssignmentResponseDTO() {}

    // Constructor with all fields
    public AssignmentResponseDTO(Long id, Long rideRequestId, Long driverId, String driverName,
                                 String driverVehiclePlate, Double driverRatingAtTime, AssignmentStatus status,
                                 Double distanceKm, Double estimatedFare, Double finalFare,
                                 Integer assignmentAttempt, String rejectionReason,
                                 LocalDateTime assignedAt, LocalDateTime respondedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.rideRequestId = rideRequestId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverVehiclePlate = driverVehiclePlate;
        this.driverRatingAtTime = driverRatingAtTime;
        this.status = status;
        this.distanceKm = distanceKm;
        this.estimatedFare = estimatedFare;
        this.finalFare = finalFare;
        this.assignmentAttempt = assignmentAttempt;
        this.rejectionReason = rejectionReason;
        this.assignedAt = assignedAt;
        this.respondedAt = respondedAt;
        this.expiresAt = expiresAt;
    }

    // Factory method from Entity
    public static AssignmentResponseDTO fromEntity(RideAssignmentEntity entity, String driverName, String driverVehiclePlate) {
        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(entity.getId());
        dto.setRideRequestId(entity.getRideRequestId());
        dto.setDriverId(entity.getDriverId());
        dto.setDriverName(driverName);
        dto.setDriverVehiclePlate(driverVehiclePlate);
        dto.setDriverRatingAtTime(entity.getDriverRatingAtTime());
        dto.setStatus(entity.getStatus());
        dto.setDistanceKm(entity.getDistanceKm());
        dto.setEstimatedFare(entity.getEstimatedFare());
        dto.setFinalFare(entity.getFinalFare());
        dto.setAssignmentAttempt(entity.getAssignmentAttempt());
        dto.setRejectionReason(entity.getRejectionReason());
        dto.setAssignedAt(entity.getAssignedAt());
        dto.setRespondedAt(entity.getRespondedAt());
        dto.setExpiresAt(entity.getExpiresAt());
        return dto;
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

    @Override
    public String toString() {
        return "AssignmentResponseDTO{" +
                "id=" + id +
                ", rideRequestId=" + rideRequestId +
                ", driverId=" + driverId +
                ", driverName='" + driverName + '\'' +
                ", driverVehiclePlate='" + driverVehiclePlate + '\'' +
                ", status=" + status +
                ", distanceKm=" + distanceKm +
                ", estimatedFare=" + estimatedFare +
                ", finalFare=" + finalFare +
                ", assignmentAttempt=" + assignmentAttempt +
                '}';
    }
}