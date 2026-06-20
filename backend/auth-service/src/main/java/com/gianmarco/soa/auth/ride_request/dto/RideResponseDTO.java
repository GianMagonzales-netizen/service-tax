package com.gianmarco.soa.auth.ride_request.dto;

import com.gianmarco.soa.auth.driver.enums.ServiceType;
import com.gianmarco.soa.auth.ride_request.entity.RideRequestEntity;
import com.gianmarco.soa.auth.ride_request.enums.RideStatus;
import java.time.LocalDateTime;

public class RideResponseDTO {

    private Long id;
    private Long clientId;
    private Long driverId;
    private String driverName;
    private String driverVehiclePlate;
    private ServiceType serviceType;
    private RideStatus status;
    private Double pickupLat;
    private Double pickupLng;
    private String pickupAddress;
    private Double destinationLat;
    private Double destinationLng;
    private String destinationAddress;
    private Double distanceKm;
    private Double estimatedFare;
    private Double finalFare;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private Integer assignmentAttempts;

    // Constructor from Entity
    public static RideResponseDTO fromEntity(RideRequestEntity entity) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.setId(entity.getId());
        dto.setClientId(entity.getClientId());
        dto.setDriverId(entity.getDriverId());
        dto.setServiceType(entity.getServiceType());
        dto.setStatus(entity.getStatus());
        dto.setPickupLat(entity.getPickupLat());
        dto.setPickupLng(entity.getPickupLng());
        dto.setPickupAddress(entity.getPickupAddress());
        dto.setDestinationLat(entity.getDestinationLat());
        dto.setDestinationLng(entity.getDestinationLng());
        dto.setDestinationAddress(entity.getDestinationAddress());
        dto.setDistanceKm(entity.getDistanceKm());
        dto.setEstimatedFare(entity.getEstimatedFare());
        dto.setFinalFare(entity.getFinalFare());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setAssignedAt(entity.getAssignedAt());
        dto.setAcceptedAt(entity.getAcceptedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setAssignmentAttempts(entity.getAssignmentAttempts());
        return dto;
    }

    // Constructor with driver name and vehicle plate (for enriched responses)
    public static RideResponseDTO fromEntityWithDriverDetails(RideRequestEntity entity, String driverName, String driverVehiclePlate) {
        RideResponseDTO dto = fromEntity(entity);
        dto.setDriverName(driverName);
        dto.setDriverVehiclePlate(driverVehiclePlate);
        return dto;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverVehiclePlate() { return driverVehiclePlate; }
    public void setDriverVehiclePlate(String driverVehiclePlate) { this.driverVehiclePlate = driverVehiclePlate; }

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

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Integer getAssignmentAttempts() { return assignmentAttempts; }
    public void setAssignmentAttempts(Integer assignmentAttempts) { this.assignmentAttempts = assignmentAttempts; }
}