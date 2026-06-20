package com.gianmarco.soa.auth.ride_request.service;

import com.gianmarco.soa.auth.driver.entity.DriverEntity;
import com.gianmarco.soa.auth.driver.enums.DriverStatus;
import com.gianmarco.soa.auth.driver.enums.ServiceType;
import com.gianmarco.soa.auth.driver.service.DriverService;
import com.gianmarco.soa.auth.location.service.DistanceService;
import com.gianmarco.soa.auth.ride_request.dto.RideRequestDTO;
import com.gianmarco.soa.auth.ride_request.dto.RideResponseDTO;
import com.gianmarco.soa.auth.ride_request.dto.RatingRequestDTO;
import com.gianmarco.soa.auth.ride_request.entity.RideRequestEntity;
import com.gianmarco.soa.auth.ride_request.enums.RideStatus;
import com.gianmarco.soa.auth.ride_request.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideRequestService {

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DistanceService distanceService;

    // ===== CREATE RIDE REQUEST =====

    @Transactional
    public RideResponseDTO createRideRequest(RideRequestDTO request) {
        // Calculate distance between pickup and destination
        double distance = distanceService.calculateDistance(
                request.getPickupLat(), request.getPickupLng(),
                request.getDestinationLat(), request.getDestinationLng()
        );

        // Calculate estimated fare
        double estimatedFare = distanceService.calculateEstimatedFare(distance, request.getServiceType().name());

        // Create ride request entity
        RideRequestEntity ride = new RideRequestEntity();
        ride.setClientId(request.getClientId());
        ride.setServiceType(request.getServiceType());
        ride.setPickupLat(request.getPickupLat());
        ride.setPickupLng(request.getPickupLng());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDestinationLat(request.getDestinationLat());
        ride.setDestinationLng(request.getDestinationLng());
        ride.setDestinationAddress(request.getDestinationAddress());
        ride.setDistanceKm(distance);
        ride.setEstimatedFare(estimatedFare);
        ride.setPaymentMethod(request.getPaymentMethod());
        ride.setStatus(RideStatus.PENDING);

        RideRequestEntity saved = rideRequestRepository.save(ride);
        return RideResponseDTO.fromEntity(saved);
    }

    // ===== MATCHING SERVICE (4 CRITERIA) =====

    @Transactional
    public RideResponseDTO assignDriverToRide(Long rideRequestId) {
        RideRequestEntity ride = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (ride.getStatus() != RideStatus.PENDING) {
            throw new RuntimeException("Ride request is not in PENDING state");
        }

        // Find best driver using 4 criteria
        DriverEntity bestDriver = driverService.findBestMatchDriverEntity(
                ride.getServiceType(),
                ride.getPickupLat(),
                ride.getPickupLng()
        );

        // Assign driver to ride
        ride.assignDriver(bestDriver.getId());
        ride.setDistanceKm(distanceService.calculateDistance(
                ride.getPickupLat(), ride.getPickupLng(),
                bestDriver.getLatitude(), bestDriver.getLongitude()
        ));

        // Update driver status to BUSY
        driverService.updateDriverStatus(bestDriver.getId(), DriverStatus.BUSY);

        RideRequestEntity saved = rideRequestRepository.save(ride);
        return RideResponseDTO.fromEntityWithDriverDetails(saved, bestDriver.getName(), bestDriver.getVehiclePlate());
    }

    @Transactional
    public RideResponseDTO reassignDriverToRide(Long rideRequestId) {
        RideRequestEntity ride = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (!ride.canBeReassigned()) {
            throw new RuntimeException("Ride request cannot be reassigned");
        }

        // Free previous driver if exists
        if (ride.getDriverId() != null && ride.getStatus() == RideStatus.ASSIGNED) {
            driverService.updateDriverStatus(ride.getDriverId(), DriverStatus.AVAILABLE);
        }

        // Reset for new assignment
        ride.setStatus(RideStatus.PENDING);
        ride.setDriverId(null);
        ride.setAssignedAt(null);
        ride.setExpiresAt(null);
        rideRequestRepository.save(ride);

        // Assign new driver
        return assignDriverToRide(rideRequestId);
    }

    // ===== DRIVER RESPONSE (Flow 3.2.3) =====

    @Transactional
    public RideResponseDTO driverAcceptRide(Long rideRequestId, Long driverId) {
        RideRequestEntity ride = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (ride.getDriverId() == null || !ride.getDriverId().equals(driverId)) {
            throw new RuntimeException("Driver is not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new RuntimeException("Ride is not in ASSIGNED state");
        }

        if (ride.isExpired()) {
            ride.expire();
            rideRequestRepository.save(ride);
            // Free the driver
            driverService.updateDriverStatus(driverId, DriverStatus.AVAILABLE);
            throw new RuntimeException("Assignment has expired");
        }

        ride.acceptByDriver();
        ride.setStartedAt(LocalDateTime.now());

        RideRequestEntity saved = rideRequestRepository.save(ride);

        DriverEntity driver = driverService.getDriverEntityById(driverId);
        return RideResponseDTO.fromEntityWithDriverDetails(saved, driver.getName(), driver.getVehiclePlate());
    }

    @Transactional
    public RideResponseDTO driverRejectRide(Long rideRequestId, Long driverId, String reason) {
        RideRequestEntity ride = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (ride.getDriverId() == null || !ride.getDriverId().equals(driverId)) {
            throw new RuntimeException("Driver is not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new RuntimeException("Ride is not in ASSIGNED state");
        }

        // Record rejection
        ride.rejectByDriver(reason);
        rideRequestRepository.save(ride);

        // Free the rejecting driver
        driverService.updateDriverStatus(driverId, DriverStatus.AVAILABLE);

        // Reassign to another driver
        return reassignDriverToRide(rideRequestId);
    }

    // ===== RIDE COMPLETION (Flow 3.2.4) =====

    @Transactional
    public RideResponseDTO completeRide(Long rideRequestId, Long driverId) {
        RideRequestEntity ride = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (ride.getDriverId() == null || !ride.getDriverId().equals(driverId)) {
            throw new RuntimeException("Driver is not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new RuntimeException("Ride is not in IN_PROGRESS state");
        }

        // Calculate final fare (same as estimated for now)
        double finalFare = ride.getEstimatedFare();
        ride.completeRide(finalFare);

        // Update driver statistics
        driverService.incrementCompletedRides(driverId);
        driverService.updateDriverStatus(driverId, DriverStatus.AVAILABLE);

        RideRequestEntity saved = rideRequestRepository.save(ride);

        DriverEntity driver = driverService.getDriverEntityById(driverId);
        return RideResponseDTO.fromEntityWithDriverDetails(saved, driver.getName(), driver.getVehiclePlate());
    }

    @Transactional
    public RideResponseDTO cancelRide(Long rideRequestId, Long clientId) {
        RideRequestEntity ride = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (!ride.getClientId().equals(clientId)) {
            throw new RuntimeException("Client is not the owner of this ride");
        }

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed ride");
        }

        // Free driver if assigned and not yet started
        if (ride.getDriverId() != null && ride.getStatus() == RideStatus.ASSIGNED) {
            driverService.updateDriverStatus(ride.getDriverId(), DriverStatus.AVAILABLE);
        }

        ride.cancelRide();

        RideRequestEntity saved = rideRequestRepository.save(ride);
        return RideResponseDTO.fromEntity(saved);
    }

    // ===== RATING =====

    @Transactional
    public void rateDriver(RatingRequestDTO ratingRequest) {
        RideRequestEntity ride = rideRequestRepository.findById(ratingRequest.getRideRequestId())
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (!ride.getClientId().equals(ratingRequest.getClientId())) {
            throw new RuntimeException("Client is not the owner of this ride");
        }

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new RuntimeException("Cannot rate a ride that is not completed");
        }

        if (ride.getDriverId() == null || !ride.getDriverId().equals(ratingRequest.getDriverId())) {
            throw new RuntimeException("Driver mismatch");
        }

        // Update driver rating
        driverService.updateDriverRating(ratingRequest.getDriverId(), ratingRequest.getRating());
    }

    // ===== QUERIES =====

    @Transactional(readOnly = true)
    public RideResponseDTO getRideRequestById(Long id) {
        RideRequestEntity ride = rideRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));
        return RideResponseDTO.fromEntity(ride);
    }

    @Transactional(readOnly = true)
    public List<RideResponseDTO> getRideRequestsByClient(Long clientId) {
        return rideRequestRepository.findByClientId(clientId).stream()
                .map(RideResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RideResponseDTO> getActiveRidesByClient(Long clientId) {
        return rideRequestRepository.findActiveRidesByClient(clientId).stream()
                .map(RideResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RideResponseDTO> getRideRequestsByDriver(Long driverId) {
        return rideRequestRepository.findByDriverId(driverId).stream()
                .map(RideResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RideResponseDTO> getActiveRidesByDriver(Long driverId) {
        return rideRequestRepository.findActiveRidesByDriver(driverId).stream()
                .map(RideResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RideResponseDTO> getRideRequestsByStatus(String status) {
        RideStatus rideStatus = RideStatus.valueOf(status.toUpperCase());
        return rideRequestRepository.findByStatus(rideStatus).stream()
                .map(RideResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ===== STATISTICS =====

    @Transactional(readOnly = true)
    public long countPendingRides() {
        return rideRequestRepository.countByStatus(RideStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public long countInProgressRides() {
        return rideRequestRepository.countByStatus(RideStatus.IN_PROGRESS);
    }

    @Transactional(readOnly = true)
    public long countCompletedRides() {
        return rideRequestRepository.countByStatus(RideStatus.COMPLETED);
    }

    @Transactional
    public void expireExpiredAssignments() {
        int expired = rideRequestRepository.expireAllExpiredAssignments(LocalDateTime.now());
        if (expired > 0) {
            // Free drivers from expired assignments
            List<RideRequestEntity> expiredRides = rideRequestRepository.findExpiredAssignments(LocalDateTime.now());
            for (RideRequestEntity ride : expiredRides) {
                if (ride.getDriverId() != null) {
                    driverService.updateDriverStatus(ride.getDriverId(), DriverStatus.AVAILABLE);
                }
            }
        }
    }
}