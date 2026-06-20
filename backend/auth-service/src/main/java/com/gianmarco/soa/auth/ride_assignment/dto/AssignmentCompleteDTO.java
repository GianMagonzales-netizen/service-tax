package com.gianmarco.soa.auth.ride_assignment.dto;

import jakarta.validation.constraints.NotNull;

public class AssignmentCompleteDTO {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Final fare is required")
    private Double finalFare;

    // Default constructor
    public AssignmentCompleteDTO() {}

    // Constructor with all fields
    public AssignmentCompleteDTO(Long assignmentId, Long driverId, Double finalFare) {
        this.assignmentId = assignmentId;
        this.driverId = driverId;
        this.finalFare = finalFare;
    }

    // Getters and Setters
    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Double getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(Double finalFare) {
        this.finalFare = finalFare;
    }

    @Override
    public String toString() {
        return "AssignmentCompleteDTO{" +
                "assignmentId=" + assignmentId +
                ", driverId=" + driverId +
                ", finalFare=" + finalFare +
                '}';
    }
}