package com.gianmarco.soa.auth.orchestrator.dto;

import com.gianmarco.soa.auth.driver.enums.ServiceType;
import jakarta.validation.constraints.NotNull;

public class RideRequestDTO {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @NotNull(message = "Pickup latitude is required")
    private Double pickupLat;

    @NotNull(message = "Pickup longitude is required")
    private Double pickupLng;

    private String pickupAddress;

    @NotNull(message = "Destination latitude is required")
    private Double destinationLat;

    @NotNull(message = "Destination longitude is required")
    private Double destinationLng;

    private String destinationAddress;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    // Default constructor
    public RideRequestDTO() {}

    // Constructor with required fields
    public RideRequestDTO(Long clientId, ServiceType serviceType, Double pickupLat, Double pickupLng,
                          Double destinationLat, Double destinationLng, String paymentMethod) {
        this.clientId = clientId;
        this.serviceType = serviceType;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public Double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public Double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(Double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "RideRequestDTO{" +
                "clientId=" + clientId +
                ", serviceType=" + serviceType +
                ", pickupLat=" + pickupLat +
                ", pickupLng=" + pickupLng +
                ", pickupAddress='" + pickupAddress + '\'' +
                ", destinationLat=" + destinationLat +
                ", destinationLng=" + destinationLng +
                ", destinationAddress='" + destinationAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}