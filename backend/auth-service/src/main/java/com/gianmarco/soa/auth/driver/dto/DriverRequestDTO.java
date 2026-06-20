package com.gianmarco.soa.auth.driver.dto;

import com.gianmarco.soa.auth.driver.enums.ServiceType;
import jakarta.validation.constraints.*;

public class DriverRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Vehicle plate is required")
    @Pattern(regexp = "^[A-Z0-9]{3}-[A-Z0-9]{3}$", message = "Invalid plate format (ex: ABC-123)")
    private String vehiclePlate;

    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;  // STANDARD or CONFORT

    @Pattern(regexp = "^[0-9]{9,15}$", message = "Invalid phone number")
    private String phone;

    // Simulated location (within project limits - no real GPS)
    private Double latitude;
    private Double longitude;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}