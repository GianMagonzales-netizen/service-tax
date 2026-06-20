package com.gianmarco.soa.auth.ride_request.dto;

import jakarta.validation.constraints.*;

public class RatingRequestDTO {

    @NotNull(message = "Ride request ID is required")
    private Long rideRequestId;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
    private Double rating;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    // Constructors
    public RatingRequestDTO() {}

    public RatingRequestDTO(Long rideRequestId, Long clientId, Long driverId, Double rating, String comment) {
        this.rideRequestId = rideRequestId;
        this.clientId = clientId;
        this.driverId = driverId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getRideRequestId() {
        return rideRequestId;
    }

    public void setRideRequestId(Long rideRequestId) {
        this.rideRequestId = rideRequestId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "RatingRequestDTO{" +
                "rideRequestId=" + rideRequestId +
                ", clientId=" + clientId +
                ", driverId=" + driverId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}