package com.gianmarco.soa.auth.driver.dto;

/**
 * DTO for updating driver's current location
 * Within project limits: simulated location, no real GPS integration
 */
public class DriverLocationDTO {

    private Long driverId;
    private Double latitude;
    private Double longitude;

    // Constructors
    public DriverLocationDTO() {}

    public DriverLocationDTO(Long driverId, Double latitude, Double longitude) {
        this.driverId = driverId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "DriverLocationDTO{" +
                "driverId=" + driverId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}