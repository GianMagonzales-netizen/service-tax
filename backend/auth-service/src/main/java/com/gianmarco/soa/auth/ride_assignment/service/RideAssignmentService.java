package com.gianmarco.soa.auth.ride_assignment.service;
import java.util.Optional;

import com.gianmarco.soa.auth.driver.entity.DriverEntity;
import com.gianmarco.soa.auth.driver.enums.DriverStatus;
import com.gianmarco.soa.auth.driver.service.DriverService;
import com.gianmarco.soa.auth.location.service.DistanceService;
import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentRequestDTO;
import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentResponseDTO;
import com.gianmarco.soa.auth.ride_assignment.entity.RideAssignmentEntity;
import com.gianmarco.soa.auth.ride_assignment.enums.AssignmentStatus;
import com.gianmarco.soa.auth.ride_assignment.repository.RideAssignmentRepository;
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
public class RideAssignmentService {

    @Autowired
    private RideAssignmentRepository rideAssignmentRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DistanceService distanceService;

    // ===== CREATE ASSIGNMENT =====

    @Transactional
    public AssignmentResponseDTO createAssignment(AssignmentRequestDTO request) {
        // Verify ride request exists and is pending
        RideRequestEntity rideRequest = rideRequestRepository.findById(request.getRideRequestId())
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (rideRequest.getStatus() != RideStatus.PENDING) {
            throw new RuntimeException("Ride request is not in PENDING state");
        }

        // Verify driver exists and is available
        DriverEntity driver = driverService.getDriverEntityById(request.getDriverId());
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available");
        }

        // Check if there's already an active assignment for this ride
        if (rideAssignmentRepository.existsByRideRequestIdAndStatusIn(
                request.getRideRequestId(),
                List.of(AssignmentStatus.PENDING, AssignmentStatus.ACCEPTED))) {
            throw new RuntimeException("Ride request already has an active assignment");
        }

        // Calculate distance
        double distance = distanceService.calculateDistance(
                rideRequest.getPickupLat(), rideRequest.getPickupLng(),
                driver.getLatitude(), driver.getLongitude()
        );

        // Calculate estimated fare
        double estimatedFare = distanceService.calculateEstimatedFare(distance, rideRequest.getServiceType().name());

        // Get current assignment attempt count
        long attemptCount = rideAssignmentRepository.countByRideRequestIdAndStatus(
                request.getRideRequestId(), AssignmentStatus.PENDING);

        // Create assignment
        RideAssignmentEntity assignment = new RideAssignmentEntity();
        assignment.setRideRequestId(request.getRideRequestId());
        assignment.setDriverId(request.getDriverId());
        assignment.setDriverName(driver.getName());
        assignment.setDriverVehiclePlate(driver.getVehiclePlate());
        assignment.setDriverRatingAtTime(driver.getAvgRating());
        assignment.setDistanceKm(distance);
        assignment.setEstimatedFare(estimatedFare);
        assignment.setAssignmentAttempt((int) attemptCount + 1);

        RideAssignmentEntity saved = rideAssignmentRepository.save(assignment);

        // Update ride request status
        rideRequest.setStatus(RideStatus.ASSIGNED);
        rideRequest.setDriverId(request.getDriverId());
        rideRequest.setAssignedAt(LocalDateTime.now());
        rideRequestRepository.save(rideRequest);

        // Update driver status to BUSY
        driverService.updateDriverStatus(request.getDriverId(), DriverStatus.BUSY);

        return AssignmentResponseDTO.fromEntity(saved, driver.getName(), driver.getVehiclePlate());
    }

    // ===== DRIVER RESPONSE =====

    @Transactional
    public AssignmentResponseDTO driverAcceptAssignment(Long assignmentId, Long driverId) {
        RideAssignmentEntity assignment = rideAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getDriverId().equals(driverId)) {
            throw new RuntimeException("Driver is not assigned to this assignment");
        }

        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new RuntimeException("Assignment is not in PENDING state");
        }

        if (assignment.isExpired()) {
            assignment.expire();
            rideAssignmentRepository.save(assignment);
            // Free the driver
            driverService.updateDriverStatus(driverId, DriverStatus.AVAILABLE);
            throw new RuntimeException("Assignment has expired");
        }

        // Accept assignment
        assignment.accept();
        rideAssignmentRepository.save(assignment);

        // Update ride request status
        RideRequestEntity rideRequest = rideRequestRepository.findById(assignment.getRideRequestId())
                .orElseThrow(() -> new RuntimeException("Ride request not found"));
        rideRequest.setStatus(RideStatus.IN_PROGRESS);
        rideRequest.setAcceptedAt(LocalDateTime.now());
        rideRequestRepository.save(rideRequest);

        DriverEntity driver = driverService.getDriverEntityById(driverId);
        return AssignmentResponseDTO.fromEntity(assignment, driver.getName(), driver.getVehiclePlate());
    }

    @Transactional
    public AssignmentResponseDTO driverRejectAssignment(Long assignmentId, Long driverId, String reason) {
        RideAssignmentEntity assignment = rideAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getDriverId().equals(driverId)) {
            throw new RuntimeException("Driver is not assigned to this assignment");
        }

        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new RuntimeException("Assignment is not in PENDING state");
        }

        // Reject assignment
        assignment.reject(reason);
        rideAssignmentRepository.save(assignment);

        // Update ride request status back to PENDING
        RideRequestEntity rideRequest = rideRequestRepository.findById(assignment.getRideRequestId())
                .orElseThrow(() -> new RuntimeException("Ride request not found"));
        rideRequest.setStatus(RideStatus.PENDING);
        rideRequest.setDriverId(null);
        rideRequest.setAssignedAt(null);
        rideRequestRepository.save(rideRequest);

        // Free the driver
        driverService.updateDriverStatus(driverId, DriverStatus.AVAILABLE);

        DriverEntity driver = driverService.getDriverEntityById(driverId);
        return AssignmentResponseDTO.fromEntity(assignment, driver.getName(), driver.getVehiclePlate());
    }

    // ===== COMPLETE ASSIGNMENT =====

    @Transactional
    public AssignmentResponseDTO completeAssignment(Long assignmentId, Long driverId, Double finalFare) {
        RideAssignmentEntity assignment = rideAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getDriverId().equals(driverId)) {
            throw new RuntimeException("Driver is not assigned to this assignment");
        }

        if (assignment.getStatus() != AssignmentStatus.ACCEPTED) {
            throw new RuntimeException("Assignment is not in ACCEPTED state");
        }

        // Complete assignment
        assignment.setFinalFare(finalFare);
        assignment.complete();
        rideAssignmentRepository.save(assignment);

        // Update ride request status
        RideRequestEntity rideRequest = rideRequestRepository.findById(assignment.getRideRequestId())
                .orElseThrow(() -> new RuntimeException("Ride request not found"));
        rideRequest.setStatus(RideStatus.COMPLETED);
        rideRequest.setCompletedAt(LocalDateTime.now());
        rideRequest.setFinalFare(finalFare);
        rideRequestRepository.save(rideRequest);

        // Update driver statistics
        driverService.incrementCompletedRides(driverId);
        driverService.updateDriverStatus(driverId, DriverStatus.AVAILABLE);

        DriverEntity driver = driverService.getDriverEntityById(driverId);
        return AssignmentResponseDTO.fromEntity(assignment, driver.getName(), driver.getVehiclePlate());
    }

    // ===== QUERIES =====

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByRideRequest(Long rideRequestId) {
        return rideAssignmentRepository.findByRideRequestId(rideRequestId).stream()
                .map(a -> AssignmentResponseDTO.fromEntity(a, a.getDriverName(), a.getDriverVehiclePlate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAssignmentsByDriver(Long driverId) {
        return rideAssignmentRepository.findByDriverId(driverId).stream()
                .map(a -> AssignmentResponseDTO.fromEntity(a, a.getDriverName(), a.getDriverVehiclePlate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AssignmentResponseDTO> getAcceptedAssignmentByRideRequest(Long rideRequestId) {
        return rideAssignmentRepository.findAcceptedAssignmentByRideRequest(rideRequestId)
                .map(a -> AssignmentResponseDTO.fromEntity(a, a.getDriverName(), a.getDriverVehiclePlate()));
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getActiveAssignmentsByDriver(Long driverId) {
        return rideAssignmentRepository.findActiveAssignmentsByDriver(driverId).stream()
                .map(a -> AssignmentResponseDTO.fromEntity(a, a.getDriverName(), a.getDriverVehiclePlate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentResponseDTO getAssignmentById(Long id) {
        RideAssignmentEntity assignment = rideAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        return AssignmentResponseDTO.fromEntity(assignment, assignment.getDriverName(), assignment.getDriverVehiclePlate());
    }

    // ===== BATCH OPERATIONS =====

    @Transactional
    public void expireExpiredAssignments() {
        int expired = rideAssignmentRepository.expireAllExpiredAssignments(LocalDateTime.now());
        if (expired > 0) {
            List<RideAssignmentEntity> expiredAssignments = rideAssignmentRepository.findExpiredPendingAssignments(LocalDateTime.now());
            for (RideAssignmentEntity assignment : expiredAssignments) {
                // Free the driver
                driverService.updateDriverStatus(assignment.getDriverId(), DriverStatus.AVAILABLE);

                // Update ride request back to PENDING
                RideRequestEntity rideRequest = rideRequestRepository.findById(assignment.getRideRequestId()).orElse(null);
                if (rideRequest != null && rideRequest.getStatus() == RideStatus.ASSIGNED) {
                    rideRequest.setStatus(RideStatus.PENDING);
                    rideRequest.setDriverId(null);
                    rideRequest.setAssignedAt(null);
                    rideRequestRepository.save(rideRequest);
                }
            }
        }
    }

    // ===== STATISTICS =====

    @Transactional(readOnly = true)
    public long countPendingAssignments() {
        return rideAssignmentRepository.countByStatus(AssignmentStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public long countAcceptedAssignments() {
        return rideAssignmentRepository.countByStatus(AssignmentStatus.ACCEPTED);
    }

    @Transactional(readOnly = true)
    public long countCompletedAssignments() {
        return rideAssignmentRepository.countByStatus(AssignmentStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public long countRejectedAssignments() {
        return rideAssignmentRepository.countByStatus(AssignmentStatus.REJECTED);
    }

    @Transactional(readOnly = true)
    public Double getAverageDistance() {
        return rideAssignmentRepository.getAverageDistance();
    }

    @Transactional(readOnly = true)
    public Double getAverageFare() {
        return rideAssignmentRepository.getAverageFare();
    }
}