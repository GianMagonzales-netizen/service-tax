package com.gianmarco.soa.auth.driver.dto;

import com.gianmarco.soa.auth.driver.enums.DriverStatus;
import com.gianmarco.soa.auth.driver.enums.ServiceType;
import java.time.LocalDateTime;

public class DriverResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String vehiclePlate;
    private String vehicleModel;
    private ServiceType serviceType;
    private DriverStatus status;
    private Double avgRating;
    private Integer completedRides;
    private String phone;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    // Constructor completo
    public DriverResponseDTO(Long id, Long userId, String name, String vehiclePlate,
                             String vehicleModel, ServiceType serviceType, DriverStatus status,
                             Double avgRating, Integer completedRides, String phone,
                             Double latitude, Double longitude, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.vehiclePlate = vehiclePlate;
        this.vehicleModel = vehicleModel;
        this.serviceType = serviceType;
        this.status = status;
        this.avgRating = avgRating;
        this.completedRides = completedRides;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    // Constructor desde Entity (método estático recomendado)
    public static DriverResponseDTO fromEntity(com.gianmarco.soa.auth.driver.entity.DriverEntity entity) {
        return new DriverResponseDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getVehiclePlate(),
                entity.getVehicleModel(),
                entity.getServiceType(),
                entity.getStatus(),
                entity.getAvgRating(),
                entity.getCompletedRides(),
                entity.getPhone(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getCreatedAt()
        );
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public Integer getCompletedRides() { return completedRides; }
    public void setCompletedRides(Integer completedRides) { this.completedRides = completedRides; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "DriverResponseDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", vehiclePlate='" + vehiclePlate + '\'' +
                ", serviceType=" + serviceType +
                ", status=" + status +
                ", avgRating=" + avgRating +
                ", completedRides=" + completedRides +
                '}';
    }
}