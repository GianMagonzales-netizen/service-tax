package com.gianmarco.soa.auth.driver.service;

import com.gianmarco.soa.auth.driver.dto.DriverRequestDTO;
import com.gianmarco.soa.auth.driver.dto.DriverResponseDTO;
import com.gianmarco.soa.auth.driver.dto.DriverLocationDTO;
import com.gianmarco.soa.auth.driver.entity.DriverEntity;
import com.gianmarco.soa.auth.driver.enums.DriverStatus;
import com.gianmarco.soa.auth.driver.enums.ServiceType;
import com.gianmarco.soa.auth.driver.repository.DriverRepository;
import com.gianmarco.soa.auth.location.service.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DistanceService distanceService;

    // ===== CRUD OPERATIONS =====

    @Transactional
    public DriverResponseDTO registerDriver(DriverRequestDTO request) {
        // Check if driver already exists for this user
        if (driverRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("User is already registered as a driver");
        }

        DriverEntity driver = new DriverEntity();
        driver.setUserId(request.getUserId());
        driver.setName(request.getName());
        driver.setVehiclePlate(request.getVehiclePlate());
        driver.setVehicleModel(request.getVehicleModel());
        driver.setServiceType(request.getServiceType());
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setPhone(request.getPhone());
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());

        DriverEntity saved = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public DriverResponseDTO getDriverById(Long id) {
        DriverEntity driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));
        return DriverResponseDTO.fromEntity(driver);
    }

    @Transactional(readOnly = true)
    public DriverResponseDTO getDriverByUserId(Long userId) {
        DriverEntity driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found for user: " + userId));
        return DriverResponseDTO.fromEntity(driver);
    }

    @Transactional
    public DriverResponseDTO updateDriver(Long id, DriverRequestDTO request) {
        DriverEntity driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setName(request.getName());
        driver.setVehiclePlate(request.getVehiclePlate());
        driver.setVehicleModel(request.getVehicleModel());
        driver.setServiceType(request.getServiceType());
        driver.setPhone(request.getPhone());
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());

        DriverEntity updated = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void deleteDriver(Long id) {
        DriverEntity driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setStatus(DriverStatus.OFFLINE);
        driverRepository.save(driver);
    }

    // ===== LOCATION MANAGEMENT (Simulated - within project limits) =====

    @Transactional
    public DriverResponseDTO updateDriverLocation(Long id, DriverLocationDTO locationDTO) {
        DriverEntity driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setLatitude(locationDTO.getLatitude());
        driver.setLongitude(locationDTO.getLongitude());
        DriverEntity updated = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(updated);
    }

    // ===== STATUS MANAGEMENT =====

    @Transactional
    public DriverResponseDTO updateDriverStatus(Long id, DriverStatus status) {
        DriverEntity driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setStatus(status);
        DriverEntity updated = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(updated);
    }

    // ===== MATCHING SERVICE (HEART OF THE SYSTEM - 4 CRITERIA) =====

    /**
     * Criteria 1 & 2: Availability + Service Type
     */
    @Transactional(readOnly = true)
    public List<DriverResponseDTO> findAvailableDriversByServiceType(ServiceType serviceType) {
        List<DriverEntity> drivers = driverRepository.findByStatusAndServiceType(DriverStatus.AVAILABLE, serviceType);
        return drivers.stream()
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Criteria 4: Best rating (tie-breaker)
     */
    @Transactional(readOnly = true)
    public List<DriverResponseDTO> findBestDriversByServiceType(ServiceType serviceType, int limit) {
        List<DriverEntity> drivers = driverRepository.findAvailableByServiceTypeOrderByRating(serviceType, DriverStatus.AVAILABLE);
        return drivers.stream()
                .limit(limit)
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ALL 4 CRITERIA for taxi matching:
     * 1. Availability - Driver must be AVAILABLE
     * 2. Service Type - Must match requested (STANDARD or CONFORT)
     * 3. Proximity - Closest driver to pickup location (calculated with Haversine)
     * 4. Best Rating - Tie-breaker (higher rating wins)
     * Returns DriverResponseDTO for API consumption
     */
    @Transactional(readOnly = true)
    public DriverResponseDTO findBestMatchDriver(ServiceType serviceType, double pickupLat, double pickupLng) {
        DriverEntity bestDriver = findBestMatchDriverEntity(serviceType, pickupLat, pickupLng);
        return DriverResponseDTO.fromEntity(bestDriver);
    }

    /**
     * ALL 4 CRITERIA for taxi matching:
     * Returns DriverEntity for internal service communication (used by RideRequestService)
     */
    @Transactional(readOnly = true)
    public DriverEntity findBestMatchDriverEntity(ServiceType serviceType, double pickupLat, double pickupLng) {
        List<DriverEntity> availableDrivers = driverRepository.findByStatusAndServiceType(DriverStatus.AVAILABLE, serviceType);

        if (availableDrivers.isEmpty()) {
            throw new RuntimeException("No drivers available for " + serviceType);
        }

        return availableDrivers.stream()
                .map(driver -> {
                    double distance = distanceService.calculateDistance(
                            pickupLat, pickupLng,
                            driver.getLatitude(), driver.getLongitude()
                    );
                    return new DriverWithDistance(driver, distance);
                })
                .sorted(Comparator
                        .comparingDouble(DriverWithDistance::getDistance)  // Closest first
                        .thenComparingDouble(d -> d.getDriver().getAvgRating()) // Higher rating as tie-breaker
                )
                .findFirst()
                .map(DriverWithDistance::getDriver)
                .orElseThrow(() -> new RuntimeException("No suitable driver found"));
    }

    @Transactional(readOnly = true)
    public DriverEntity getDriverEntityById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));
    }

    // Helper class for matching
    private static class DriverWithDistance {
        private final DriverEntity driver;
        private final double distance;

        public DriverWithDistance(DriverEntity driver, double distance) {
            this.driver = driver;
            this.distance = distance;
        }
        public DriverEntity getDriver() { return driver; }
        public double getDistance() { return distance; }
    }

    // ===== RATING MANAGEMENT =====

    @Transactional
    public DriverResponseDTO updateDriverRating(Long id, Double rating) {
        DriverEntity driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Calculate new average rating
        double newRating = (driver.getAvgRating() + rating) / 2;
        driver.setAvgRating(Math.round(newRating * 10) / 10.0);

        DriverEntity updated = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(updated);
    }

    @Transactional
    public DriverResponseDTO incrementCompletedRides(Long id) {
        DriverEntity driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setCompletedRides(driver.getCompletedRides() + 1);
        DriverEntity updated = driverRepository.save(driver);
        return DriverResponseDTO.fromEntity(updated);
    }

    // ===== PAGINATED LISTS =====

    @Transactional(readOnly = true)
    public Page<DriverResponseDTO> findDriversByServiceType(ServiceType serviceType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverEntity> drivers = driverRepository.findByServiceType(serviceType, pageable);
        return drivers.map(DriverResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DriverResponseDTO> findAvailableDrivers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverEntity> drivers = driverRepository.findByStatus(DriverStatus.AVAILABLE, pageable);
        return drivers.map(DriverResponseDTO::fromEntity);
    }

    // ===== STATISTICS =====

    @Transactional(readOnly = true)
    public long countDriversByServiceType(ServiceType serviceType) {
        return driverRepository.countByServiceTypeAndStatus(serviceType, DriverStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public long countAvailableDrivers() {
        return driverRepository.countByStatus(DriverStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingByServiceType(ServiceType serviceType) {
        return driverRepository.averageRatingByServiceType(serviceType);
    }

    @Transactional(readOnly = true)
    public long countAllDrivers() {
        return driverRepository.count();
    }
}