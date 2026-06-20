package com.gianmarco.soa.auth.orchestrator.service;

import com.gianmarco.soa.auth.driver.entity.DriverEntity;
import com.gianmarco.soa.auth.driver.enums.DriverStatus;
import com.gianmarco.soa.auth.driver.service.DriverService;
import com.gianmarco.soa.auth.location.service.DistanceService;
import com.gianmarco.soa.auth.orchestrator.dto.OrchestratorResponseDTO;
import com.gianmarco.soa.auth.orchestrator.dto.RideRequestDTO;
import com.gianmarco.soa.auth.orchestrator.dto.TripStatusDTO;
import com.gianmarco.soa.auth.payment.dto.PaymentRequestDTO;
import com.gianmarco.soa.auth.payment.dto.PaymentResponseDTO;
import com.gianmarco.soa.auth.payment.service.PaymentService;
import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentResponseDTO;
import com.gianmarco.soa.auth.ride_assignment.service.RideAssignmentService;
import com.gianmarco.soa.auth.ride_request.dto.RatingRequestDTO;
import com.gianmarco.soa.auth.ride_request.dto.RideResponseDTO;
import com.gianmarco.soa.auth.ride_request.entity.RideRequestEntity;
import com.gianmarco.soa.auth.ride_request.enums.RideStatus;
import com.gianmarco.soa.auth.ride_request.repository.RideRequestRepository;
import com.gianmarco.soa.auth.ride_request.service.RideRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaxiOrchestratorService {

    @Autowired
    private RideRequestService rideRequestService;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private RideAssignmentService rideAssignmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DistanceService distanceService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * MAIN METHOD - PROCESS COMPLETE RIDE REQUEST
     * Orchestrates all services:
     * 1. Creates ride request
     * 2. Finds best driver (4 criteria)
     * 3. Creates assignment
     * 4. Processes payment
     * 5. Completes ride
     * 6. Sends notifications (simulated)
     * 7. Registers audit (simulated)
     */
    @Transactional
    public OrchestratorResponseDTO processCompleteRideRequest(RideRequestDTO request, String clientIp) {
        try {
            // CONVERTIR DTO DEL ORQUESTADOR AL DTO DEL SERVICIO DE RIDE_REQUEST
            com.gianmarco.soa.auth.ride_request.dto.RideRequestDTO rideReqDTO =
                    new com.gianmarco.soa.auth.ride_request.dto.RideRequestDTO();

            rideReqDTO.setClientId(request.getClientId());
            rideReqDTO.setServiceType(request.getServiceType());
            rideReqDTO.setPickupLat(request.getPickupLat());
            rideReqDTO.setPickupLng(request.getPickupLng());
            rideReqDTO.setPickupAddress(request.getPickupAddress());
            rideReqDTO.setDestinationLat(request.getDestinationLat());
            rideReqDTO.setDestinationLng(request.getDestinationLng());
            rideReqDTO.setDestinationAddress(request.getDestinationAddress());
            rideReqDTO.setPaymentMethod(request.getPaymentMethod());

            // Step 1: Create ride request
            RideResponseDTO rideResponse = rideRequestService.createRideRequest(rideReqDTO);
            Long rideRequestId = rideResponse.getId();

            // Step 2: Find best driver using 4 criteria
            DriverEntity bestDriver = driverService.findBestMatchDriverEntity(
                    request.getServiceType(),
                    request.getPickupLat(),
                    request.getPickupLng()
            );

            // Step 3: Create assignment
            com.gianmarco.soa.auth.ride_assignment.dto.AssignmentRequestDTO assignmentRequest =
                    new com.gianmarco.soa.auth.ride_assignment.dto.AssignmentRequestDTO(rideRequestId, bestDriver.getId());
            AssignmentResponseDTO assignmentResponse = rideAssignmentService.createAssignment(assignmentRequest);

            // Step 4: Simulate driver acceptance (auto-accept for demo)
            assignmentResponse = rideAssignmentService.driverAcceptAssignment(assignmentResponse.getId(), bestDriver.getId());

            // Step 5: Calculate distance and fare
            double distance = distanceService.calculateDistance(
                    request.getPickupLat(), request.getPickupLng(),
                    request.getDestinationLat(), request.getDestinationLng()
            );
            double estimatedFare = distanceService.calculateEstimatedFare(distance, request.getServiceType().name());

            // Step 6: Process payment
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
            paymentRequest.setRideRequestId(rideRequestId);
            paymentRequest.setClientId(request.getClientId());
            paymentRequest.setDriverId(bestDriver.getId());
            paymentRequest.setAmount(estimatedFare);
            paymentRequest.setPaymentMethod(com.gianmarco.soa.auth.payment.enums.PaymentMethod.valueOf(request.getPaymentMethod()));
            PaymentResponseDTO paymentResponse = paymentService.processPayment(paymentRequest);

            // Step 7: Complete ride
            rideAssignmentService.completeAssignment(assignmentResponse.getId(), bestDriver.getId(), estimatedFare);

            // Step 8: Get updated ride status
            RideResponseDTO finalRide = rideRequestService.getRideRequestById(rideRequestId);

            // Step 9: Simulate notifications
            sendSimulatedNotifications(rideRequestId, bestDriver.getName(), "RIDE_COMPLETED");

            // Step 10: Simulate audit
            registerSimulatedAudit(rideRequestId, "RIDE_COMPLETED", clientIp);

            return new OrchestratorResponseDTO(
                    true,
                    "Ride completed successfully",
                    rideRequestId,
                    assignmentResponse.getId(),
                    paymentResponse.getId(),
                    finalRide.getStatus(),
                    bestDriver.getName(),
                    bestDriver.getVehiclePlate(),
                    request.getServiceType(),
                    estimatedFare,
                    finalRide.getFinalFare(),
                    paymentResponse.getTransactionCode(),
                    distance,
                    distanceService.calculateEstimatedTimeMinutes(distance)
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(false, "Error processing ride: " + e.getMessage());
        }
    }

    /**
     * REASSIGN RIDE - Find another driver when previous driver rejects
     */
    @Transactional
    public OrchestratorResponseDTO reassignRide(Long rideRequestId, String clientIp) {
        try {
            RideResponseDTO rideResponse = rideRequestService.reassignDriverToRide(rideRequestId);
            RideRequestEntity ride = rideRequestRepository.findById(rideRequestId)
                    .orElseThrow(() -> new RuntimeException("Ride request not found"));

            // Find new driver
            DriverEntity newDriver = driverService.findBestMatchDriverEntity(
                    ride.getServiceType(),
                    ride.getPickupLat(),
                    ride.getPickupLng()
            );

            // Create new assignment
            com.gianmarco.soa.auth.ride_assignment.dto.AssignmentRequestDTO assignmentRequest =
                    new com.gianmarco.soa.auth.ride_assignment.dto.AssignmentRequestDTO(rideRequestId, newDriver.getId());
            AssignmentResponseDTO assignmentResponse = rideAssignmentService.createAssignment(assignmentRequest);

            // Simulate notifications
            sendSimulatedNotifications(rideRequestId, newDriver.getName(), "RIDE_REASSIGNED");

            // Simulate audit
            registerSimulatedAudit(rideRequestId, "RIDE_REASSIGNED", clientIp);

            return new OrchestratorResponseDTO(
                    true,
                    "Ride reassigned successfully to " + newDriver.getName(),
                    rideRequestId,
                    assignmentResponse.getId(),
                    null,
                    rideResponse.getStatus(),
                    newDriver.getName(),
                    newDriver.getVehiclePlate(),
                    ride.getServiceType(),
                    null,
                    null,
                    null,
                    null,
                    null
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(false, "Error reassigning ride: " + e.getMessage());
        }
    }

    /**
     * DRIVER ACCEPTS RIDE
     */
    @Transactional
    public OrchestratorResponseDTO driverAcceptRide(Long rideRequestId, Long driverId, String clientIp) {
        try {
            RideResponseDTO rideResponse = rideRequestService.driverAcceptRide(rideRequestId, driverId);

            // Get assignment
            AssignmentResponseDTO assignment = rideAssignmentService.getAcceptedAssignmentByRideRequest(rideRequestId)
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));

            // Simulate notifications
            sendSimulatedNotifications(rideRequestId, driverId.toString(), "RIDE_ACCEPTED");

            // Simulate audit
            registerSimulatedAudit(rideRequestId, "RIDE_ACCEPTED", clientIp);

            return new OrchestratorResponseDTO(
                    true,
                    "Driver accepted the ride",
                    rideRequestId,
                    assignment.getId(),
                    null,
                    rideResponse.getStatus(),
                    assignment.getDriverName(),
                    assignment.getDriverVehiclePlate(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(false, "Error: " + e.getMessage());
        }
    }

    /**
     * DRIVER REJECTS RIDE
     */
    @Transactional
    public OrchestratorResponseDTO driverRejectRide(Long rideRequestId, Long driverId, String reason, String clientIp) {
        try {
            rideRequestService.driverRejectRide(rideRequestId, driverId, reason);

            // Simulate notifications
            sendSimulatedNotifications(rideRequestId, driverId.toString(), "RIDE_REJECTED");

            // Simulate audit
            registerSimulatedAudit(rideRequestId, "RIDE_REJECTED", clientIp);

            // Auto-reassign
            return reassignRide(rideRequestId, clientIp);

        } catch (Exception e) {
            return new OrchestratorResponseDTO(false, "Error: " + e.getMessage());
        }
    }

    /**
     * COMPLETE RIDE
     */
    @Transactional
    public OrchestratorResponseDTO completeRide(Long rideRequestId, Long driverId, String clientIp) {
        try {
            RideResponseDTO rideResponse = rideRequestService.completeRide(rideRequestId, driverId);

            // Simulate notifications
            sendSimulatedNotifications(rideRequestId, driverId.toString(), "RIDE_COMPLETED");

            // Simulate audit
            registerSimulatedAudit(rideRequestId, "RIDE_COMPLETED", clientIp);

            return new OrchestratorResponseDTO(
                    true,
                    "Ride completed successfully",
                    rideRequestId,
                    null,
                    null,
                    rideResponse.getStatus(),
                    null,
                    null,
                    null,
                    null,
                    rideResponse.getFinalFare(),
                    null,
                    null,
                    null
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(false, "Error: " + e.getMessage());
        }
    }

    /**
     * CANCEL RIDE
     */
    @Transactional
    public OrchestratorResponseDTO cancelRide(Long rideRequestId, Long clientId, String clientIp) {
        try {
            RideResponseDTO rideResponse = rideRequestService.cancelRide(rideRequestId, clientId);

            // Simulate notifications
            sendSimulatedNotifications(rideRequestId, clientId.toString(), "RIDE_CANCELLED");

            // Simulate audit
            registerSimulatedAudit(rideRequestId, "RIDE_CANCELLED", clientIp);

            return new OrchestratorResponseDTO(
                    true,
                    "Ride cancelled successfully",
                    rideRequestId,
                    null,
                    null,
                    rideResponse.getStatus(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(false, "Error: " + e.getMessage());
        }
    }

    /**
     * GET COMPLETE TRIP STATUS
     */
    @Transactional(readOnly = true)
    public TripStatusDTO getCompleteTripStatus(Long rideRequestId) {
        RideResponseDTO ride = rideRequestService.getRideRequestById(rideRequestId);
        List<AssignmentResponseDTO> assignments = rideAssignmentService.getAssignmentsByRideRequest(rideRequestId);
        AssignmentResponseDTO currentAssignment = rideAssignmentService.getAcceptedAssignmentByRideRequest(rideRequestId).orElse(null);
        PaymentResponseDTO payment = null;

        try {
            payment = paymentService.getPaymentByRideRequest(rideRequestId);
        } catch (Exception e) {
            // Payment may not exist yet
        }

        String estimatedArrival = null;
        if (currentAssignment != null && currentAssignment.getDistanceKm() != null) {
            int minutes = distanceService.calculateEstimatedTimeMinutes(currentAssignment.getDistanceKm());
            estimatedArrival = LocalDateTime.now().plusMinutes(minutes).format(TIME_FORMATTER);
        }

        return new TripStatusDTO.Builder()
                .rideRequest(ride)
                .rideStatus(ride.getStatus().name())
                .createdAt(ride.getCreatedAt())
                .completedAt(ride.getCompletedAt())
                .currentAssignment(currentAssignment)
                .assignmentHistory(assignments)
                .driverName(currentAssignment != null ? currentAssignment.getDriverName() : null)
                .driverVehiclePlate(currentAssignment != null ? currentAssignment.getDriverVehiclePlate() : null)
                .serviceType(ride.getServiceType())
                .pickupLat(ride.getPickupLat())
                .pickupLng(ride.getPickupLng())
                .pickupAddress(ride.getPickupAddress())
                .destinationLat(ride.getDestinationLat())
                .destinationLng(ride.getDestinationLng())
                .destinationAddress(ride.getDestinationAddress())
                .distanceKm(ride.getDistanceKm())
                .estimatedFare(ride.getEstimatedFare())
                .finalFare(ride.getFinalFare())
                .paymentMethod(ride.getPaymentMethod())
                .transactionCode(payment != null ? payment.getTransactionCode() : null)
                .currentStatus(ride.getStatus().name())
                .lastUpdate(LocalDateTime.now().format(TIME_FORMATTER))
                .assignmentAttempts(ride.getAssignmentAttempts())
                .estimatedArrivalTime(estimatedArrival)
                .build();
    }

    /**
     * GET SIMPLE RIDE STATUS
     */
    @Transactional(readOnly = true)
    public String getRideStatus(Long rideRequestId) {
        RideResponseDTO ride = rideRequestService.getRideRequestById(rideRequestId);
        return ride.getStatus().name();
    }

    /**
     * RATE DRIVER
     */
    @Transactional
    public void rateDriver(Long rideRequestId, Long clientId, Long driverId, Double rating, String comment) {
        RatingRequestDTO ratingRequest = new RatingRequestDTO();
        ratingRequest.setRideRequestId(rideRequestId);
        ratingRequest.setClientId(clientId);
        ratingRequest.setDriverId(driverId);
        ratingRequest.setRating(rating);
        ratingRequest.setComment(comment);
        rideRequestService.rateDriver(ratingRequest);
    }

    // ===== SIMULATED METHODS (within project limits) =====

    private void sendSimulatedNotifications(Long rideRequestId, String target, String eventType) {
        // Simulated notification - no real email/SMS within project limits
        System.out.println("[NOTIFICATION] Ride " + rideRequestId + " - Event: " + eventType + " - Target: " + target);
    }

    private void registerSimulatedAudit(Long rideRequestId, String action, String ipAddress) {
        // Simulated audit - no real audit service within project limits
        System.out.println("[AUDIT] Ride " + rideRequestId + " - Action: " + action + " - IP: " + ipAddress + " - Time: " + LocalDateTime.now());
    }
}