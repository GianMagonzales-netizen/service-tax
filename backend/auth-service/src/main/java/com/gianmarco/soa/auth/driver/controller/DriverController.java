package com.gianmarco.soa.auth.driver.controller;

import com.gianmarco.soa.auth.driver.dto.DriverRequestDTO;
import com.gianmarco.soa.auth.driver.dto.DriverResponseDTO;
import com.gianmarco.soa.auth.driver.dto.DriverLocationDTO;
import com.gianmarco.soa.auth.driver.enums.DriverStatus;
import com.gianmarco.soa.auth.driver.enums.ServiceType;
import com.gianmarco.soa.auth.driver.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    // ===== CRUD OPERATIONS =====

    @PostMapping
    public ResponseEntity<DriverResponseDTO> register(@Valid @RequestBody DriverRequestDTO request) {
        DriverResponseDTO response = driverService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DriverResponseDTO> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(driverService.getDriverByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody DriverRequestDTO request) {
        return ResponseEntity.ok(driverService.updateDriver(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    // ===== LOCATION MANAGEMENT (Simulated - within project limits) =====

    @PatchMapping("/{id}/location")
    public ResponseEntity<DriverResponseDTO> updateLocation(@PathVariable Long id,
                                                            @Valid @RequestBody DriverLocationDTO locationDTO) {
        return ResponseEntity.ok(driverService.updateDriverLocation(id, locationDTO));
    }

    // ===== STATUS MANAGEMENT =====

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponseDTO> updateStatus(@PathVariable Long id,
                                                          @RequestParam DriverStatus status) {
        return ResponseEntity.ok(driverService.updateDriverStatus(id, status));
    }

    // ===== MATCHING SERVICE (HEART OF THE SYSTEM - 4 CRITERIA) =====

    /**
     * Get available drivers by service type (STANDARD or CONFORT)
     * Criteria 1 & 2: Availability + Service Type
     */
    @GetMapping("/available/{serviceType}")
    public ResponseEntity<List<DriverResponseDTO>> getAvailableDrivers(@PathVariable ServiceType serviceType) {
        return ResponseEntity.ok(driverService.findAvailableDriversByServiceType(serviceType));
    }

    /**
     * Get best rated drivers by service type
     * Criteria 4: Best rating
     */
    @GetMapping("/best/{serviceType}")
    public ResponseEntity<List<DriverResponseDTO>> getBestDrivers(@PathVariable ServiceType serviceType,
                                                                  @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(driverService.findBestDriversByServiceType(serviceType, limit));
    }

    /**
     * Get the single best driver for assignment (used by MatchingService)
     * Combines all 4 criteria: Availability + Service Type + Proximity + Rating
     */
    @GetMapping("/best-match/{serviceType}")
    public ResponseEntity<DriverResponseDTO> getBestMatchDriver(@PathVariable ServiceType serviceType,
                                                                @RequestParam Double pickupLat,
                                                                @RequestParam Double pickupLng) {
        return ResponseEntity.ok(driverService.findBestMatchDriver(serviceType, pickupLat, pickupLng));
    }

    // ===== RATING MANAGEMENT =====

    @PostMapping("/{id}/rate")
    public ResponseEntity<DriverResponseDTO> rateDriver(@PathVariable Long id,
                                                        @RequestParam Double rating) {
        return ResponseEntity.ok(driverService.updateDriverRating(id, rating));
    }

    @PostMapping("/{id}/ride-completed")
    public ResponseEntity<DriverResponseDTO> registerCompletedRide(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.incrementCompletedRides(id));
    }

    // ===== PAGINATED LISTS =====

    @GetMapping("/service-type/{serviceType}/page")
    public ResponseEntity<Page<DriverResponseDTO>> getByServiceTypePaginated(@PathVariable ServiceType serviceType,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(driverService.findDriversByServiceType(serviceType, page, size));
    }

    @GetMapping("/available/page")
    public ResponseEntity<Page<DriverResponseDTO>> getAvailableDriversPaginated(@RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(driverService.findAvailableDrivers(page, size));
    }

    // ===== STATISTICS =====

    @GetMapping("/stats/available/count")
    public ResponseEntity<Long> countAvailableDrivers() {
        return ResponseEntity.ok(driverService.countAvailableDrivers());
    }

    @GetMapping("/stats/service-type/{serviceType}/count")
    public ResponseEntity<Long> countByServiceType(@PathVariable ServiceType serviceType) {
        return ResponseEntity.ok(driverService.countDriversByServiceType(serviceType));
    }

    @GetMapping("/stats/service-type/{serviceType}/avg-rating")
    public ResponseEntity<Double> getAverageRatingByServiceType(@PathVariable ServiceType serviceType) {
        Double average = driverService.getAverageRatingByServiceType(serviceType);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/stats/total/count")
    public ResponseEntity<Long> getTotalDrivers() {
        return ResponseEntity.ok(driverService.countAllDrivers());
    }
}