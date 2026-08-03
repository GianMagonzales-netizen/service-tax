package com.gianmarco.soa.orchestrator.dto;

import com.gianmarco.soa.driver.enums.ServiceType;
import com.gianmarco.soa.ride_request.enums.RideStatus;

public class OrchestratorResponseDTO {

    private boolean success;
    private String message;

    private Long rideRequestId;
    private Long assignmentId;
    private Long paymentId;

    // NUEVO
    private Long driverId;

    private RideStatus status;

    private String driverName;
    private String driverVehiclePlate;

    private ServiceType serviceType;

    private Double estimatedFare;
    private Double finalFare;

    private String transactionCode;

    private Double distanceKm;
    private Integer estimatedTimeMinutes;

    // ==========================================
    // ERROR RESPONSE
    // ==========================================

    public OrchestratorResponseDTO(
            boolean success,
            String message
    ) {
        this.success = success;
        this.message = message;
    }

    // ==========================================
    // SUCCESS RESPONSE
    // ==========================================

    public OrchestratorResponseDTO(
            boolean success,
            String message,
            Long rideRequestId,
            Long assignmentId,
            Long paymentId,
            Long driverId,
            RideStatus status,
            String driverName,
            String driverVehiclePlate,
            ServiceType serviceType,
            Double estimatedFare,
            Double finalFare,
            String transactionCode,
            Double distanceKm,
            Integer estimatedTimeMinutes
    ) {

        this.success = success;
        this.message = message;

        this.rideRequestId = rideRequestId;
        this.assignmentId = assignmentId;
        this.paymentId = paymentId;
        this.driverId = driverId;

        this.status = status;

        this.driverName = driverName;
        this.driverVehiclePlate = driverVehiclePlate;

        this.serviceType = serviceType;

        this.estimatedFare = estimatedFare;
        this.finalFare = finalFare;

        this.transactionCode = transactionCode;

        this.distanceKm = distanceKm;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRideRequestId() {
        return rideRequestId;
    }

    public void setRideRequestId(Long rideRequestId) {
        this.rideRequestId = rideRequestId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    // NUEVO

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverVehiclePlate() {
        return driverVehiclePlate;
    }

    public void setDriverVehiclePlate(String driverVehiclePlate) {
        this.driverVehiclePlate = driverVehiclePlate;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(Double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public Double getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(Double finalFare) {
        this.finalFare = finalFare;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    @Override
    public String toString() {
        return "OrchestratorResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", rideRequestId=" + rideRequestId +
                ", assignmentId=" + assignmentId +
                ", paymentId=" + paymentId +
                ", driverId=" + driverId +
                ", status=" + status +
                ", driverName='" + driverName + '\'' +
                ", driverVehiclePlate='" + driverVehiclePlate + '\'' +
                ", serviceType=" + serviceType +
                ", estimatedFare=" + estimatedFare +
                ", finalFare=" + finalFare +
                ", transactionCode='" + transactionCode + '\'' +
                ", distanceKm=" + distanceKm +
                ", estimatedTimeMinutes=" + estimatedTimeMinutes +
                '}';
    }
}