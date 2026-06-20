package com.gianmarco.soa.auth.ride_assignment.dto;

import jakarta.validation.constraints.NotNull;

public class AssignmentRequestDTO {

    @NotNull(message = "Ride request ID is required")
    private Long rideRequestId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    // Default constructor
    public AssignmentRequestDTO() {}

    // Constructor with all fields
    public AssignmentRequestDTO(Long rideRequestId, Long driverId) {
        this.rideRequestId = rideRequestId;
        this.driverId = driverId;
    }

    // Getters and Setters
    public Long getRideRequestId() {
        return rideRequestId;
    }

    public void setRideRequestId(Long rideRequestId) {
        this.rideRequestId = rideRequestId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    @Override
    public String toString() {
        return "AssignmentRequestDTO{" +
                "rideRequestId=" + rideRequestId +
                ", driverId=" + driverId +
                '}';
    }
}