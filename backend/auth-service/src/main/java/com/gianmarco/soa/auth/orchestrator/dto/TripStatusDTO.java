package com.gianmarco.soa.auth.orchestrator.dto;

import com.gianmarco.soa.auth.driver.enums.ServiceType;
import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentResponseDTO;
import com.gianmarco.soa.auth.ride_request.dto.RideResponseDTO;
import com.gianmarco.soa.auth.payment.dto.PaymentResponseDTO;
import java.time.LocalDateTime;
import java.util.List;

public class TripStatusDTO {

    // Ride information
    private RideResponseDTO rideRequest;
    private String rideStatus;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // Driver information
    private AssignmentResponseDTO currentAssignment;
    private List<AssignmentResponseDTO> assignmentHistory;
    private String driverName;
    private String driverVehiclePlate;
    private Double driverRating;
    private ServiceType serviceType;

    // Trip details
    private Double pickupLat;
    private Double pickupLng;
    private String pickupAddress;
    private Double destinationLat;
    private Double destinationLng;
    private String destinationAddress;
    private Double distanceKm;
    private Integer estimatedTimeMinutes;
    private String estimatedArrivalTime;

    // Payment information
    private PaymentResponseDTO payment;
    private Double estimatedFare;
    private Double finalFare;
    private String paymentMethod;
    private String transactionCode;

    // Additional info
    private String currentStatus;
    private String lastUpdate;
    private Integer assignmentAttempts;

    // Default constructor
    public TripStatusDTO() {}

    // Constructor with all fields
    public TripStatusDTO(RideResponseDTO rideRequest, String rideStatus, LocalDateTime createdAt,
                         LocalDateTime completedAt, AssignmentResponseDTO currentAssignment,
                         List<AssignmentResponseDTO> assignmentHistory, String driverName,
                         String driverVehiclePlate, Double driverRating, ServiceType serviceType,
                         Double pickupLat, Double pickupLng, String pickupAddress,
                         Double destinationLat, Double destinationLng, String destinationAddress,
                         Double distanceKm, Integer estimatedTimeMinutes, String estimatedArrivalTime,
                         PaymentResponseDTO payment, Double estimatedFare, Double finalFare,
                         String paymentMethod, String transactionCode, String currentStatus,
                         String lastUpdate, Integer assignmentAttempts) {
        this.rideRequest = rideRequest;
        this.rideStatus = rideStatus;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.currentAssignment = currentAssignment;
        this.assignmentHistory = assignmentHistory;
        this.driverName = driverName;
        this.driverVehiclePlate = driverVehiclePlate;
        this.driverRating = driverRating;
        this.serviceType = serviceType;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.pickupAddress = pickupAddress;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.destinationAddress = destinationAddress;
        this.distanceKm = distanceKm;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.payment = payment;
        this.estimatedFare = estimatedFare;
        this.finalFare = finalFare;
        this.paymentMethod = paymentMethod;
        this.transactionCode = transactionCode;
        this.currentStatus = currentStatus;
        this.lastUpdate = lastUpdate;
        this.assignmentAttempts = assignmentAttempts;
    }

    // Builder pattern for easy construction
    public static class Builder {
        private TripStatusDTO dto = new TripStatusDTO();

        public Builder rideRequest(RideResponseDTO rideRequest) { dto.rideRequest = rideRequest; return this; }
        public Builder rideStatus(String rideStatus) { dto.rideStatus = rideStatus; return this; }
        public Builder createdAt(LocalDateTime createdAt) { dto.createdAt = createdAt; return this; }
        public Builder completedAt(LocalDateTime completedAt) { dto.completedAt = completedAt; return this; }
        public Builder currentAssignment(AssignmentResponseDTO currentAssignment) { dto.currentAssignment = currentAssignment; return this; }
        public Builder assignmentHistory(List<AssignmentResponseDTO> assignmentHistory) { dto.assignmentHistory = assignmentHistory; return this; }
        public Builder driverName(String driverName) { dto.driverName = driverName; return this; }
        public Builder driverVehiclePlate(String driverVehiclePlate) { dto.driverVehiclePlate = driverVehiclePlate; return this; }
        public Builder driverRating(Double driverRating) { dto.driverRating = driverRating; return this; }
        public Builder serviceType(ServiceType serviceType) { dto.serviceType = serviceType; return this; }
        public Builder pickupLat(Double pickupLat) { dto.pickupLat = pickupLat; return this; }
        public Builder pickupLng(Double pickupLng) { dto.pickupLng = pickupLng; return this; }
        public Builder pickupAddress(String pickupAddress) { dto.pickupAddress = pickupAddress; return this; }
        public Builder destinationLat(Double destinationLat) { dto.destinationLat = destinationLat; return this; }
        public Builder destinationLng(Double destinationLng) { dto.destinationLng = destinationLng; return this; }
        public Builder destinationAddress(String destinationAddress) { dto.destinationAddress = destinationAddress; return this; }
        public Builder distanceKm(Double distanceKm) { dto.distanceKm = distanceKm; return this; }
        public Builder estimatedTimeMinutes(Integer estimatedTimeMinutes) { dto.estimatedTimeMinutes = estimatedTimeMinutes; return this; }
        public Builder estimatedArrivalTime(String estimatedArrivalTime) { dto.estimatedArrivalTime = estimatedArrivalTime; return this; }
        public Builder payment(PaymentResponseDTO payment) { dto.payment = payment; return this; }
        public Builder estimatedFare(Double estimatedFare) { dto.estimatedFare = estimatedFare; return this; }
        public Builder finalFare(Double finalFare) { dto.finalFare = finalFare; return this; }
        public Builder paymentMethod(String paymentMethod) { dto.paymentMethod = paymentMethod; return this; }
        public Builder transactionCode(String transactionCode) { dto.transactionCode = transactionCode; return this; }
        public Builder currentStatus(String currentStatus) { dto.currentStatus = currentStatus; return this; }
        public Builder lastUpdate(String lastUpdate) { dto.lastUpdate = lastUpdate; return this; }
        public Builder assignmentAttempts(Integer assignmentAttempts) { dto.assignmentAttempts = assignmentAttempts; return this; }

        public TripStatusDTO build() { return dto; }
    }

    // Getters and Setters
    public RideResponseDTO getRideRequest() { return rideRequest; }
    public void setRideRequest(RideResponseDTO rideRequest) { this.rideRequest = rideRequest; }

    public String getRideStatus() { return rideStatus; }
    public void setRideStatus(String rideStatus) { this.rideStatus = rideStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public AssignmentResponseDTO getCurrentAssignment() { return currentAssignment; }
    public void setCurrentAssignment(AssignmentResponseDTO currentAssignment) { this.currentAssignment = currentAssignment; }

    public List<AssignmentResponseDTO> getAssignmentHistory() { return assignmentHistory; }
    public void setAssignmentHistory(List<AssignmentResponseDTO> assignmentHistory) { this.assignmentHistory = assignmentHistory; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverVehiclePlate() { return driverVehiclePlate; }
    public void setDriverVehiclePlate(String driverVehiclePlate) { this.driverVehiclePlate = driverVehiclePlate; }

    public Double getDriverRating() { return driverRating; }
    public void setDriverRating(Double driverRating) { this.driverRating = driverRating; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

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

    public Integer getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }

    public String getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(String estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }

    public PaymentResponseDTO getPayment() { return payment; }
    public void setPayment(PaymentResponseDTO payment) { this.payment = payment; }

    public Double getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(Double estimatedFare) { this.estimatedFare = estimatedFare; }

    public Double getFinalFare() { return finalFare; }
    public void setFinalFare(Double finalFare) { this.finalFare = finalFare; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }

    public Integer getAssignmentAttempts() { return assignmentAttempts; }
    public void setAssignmentAttempts(Integer assignmentAttempts) { this.assignmentAttempts = assignmentAttempts; }

    @Override
    public String toString() {
        return "TripStatusDTO{" +
                "rideStatus='" + rideStatus + '\'' +
                ", driverName='" + driverName + '\'' +
                ", driverVehiclePlate='" + driverVehiclePlate + '\'' +
                ", serviceType=" + serviceType +
                ", distanceKm=" + distanceKm +
                ", currentStatus='" + currentStatus + '\'' +
                '}';
    }
}