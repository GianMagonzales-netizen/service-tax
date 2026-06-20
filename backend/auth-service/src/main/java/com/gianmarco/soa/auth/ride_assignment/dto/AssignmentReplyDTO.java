package com.gianmarco.soa.auth.ride_assignment.dto;

import jakarta.validation.constraints.NotNull;

public class AssignmentReplyDTO {

    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Accept flag is required")
    private Boolean accept;

    private String rejectionReason;

    // Default constructor
    public AssignmentReplyDTO() {}

    // Constructor with all fields
    public AssignmentReplyDTO(Long assignmentId, Long driverId, Boolean accept, String rejectionReason) {
        this.assignmentId = assignmentId;
        this.driverId = driverId;
        this.accept = accept;
        this.rejectionReason = rejectionReason;
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

    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public String toString() {
        return "AssignmentReplyDTO{" +
                "assignmentId=" + assignmentId +
                ", driverId=" + driverId +
                ", accept=" + accept +
                ", rejectionReason='" + rejectionReason + '\'' +
                '}';
    }
}
