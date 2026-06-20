package com.gianmarco.soa.auth.orchestrator.controller;

import com.gianmarco.soa.auth.orchestrator.dto.OrchestratorResponseDTO;
import com.gianmarco.soa.auth.orchestrator.dto.RideRequestDTO;
import com.gianmarco.soa.auth.orchestrator.dto.TripStatusDTO;
import com.gianmarco.soa.auth.orchestrator.service.TaxiOrchestratorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestrator")
public class TaxiOrchestratorController {

    @Autowired
    private TaxiOrchestratorService orchestratorService;

    /**
     * MAIN ENDPOINT - PROCESS COMPLETE RIDE REQUEST
     * This endpoint orchestrates all services:
     * 1. Creates the ride request
     * 2. Assigns a driver using 4 criteria (matching)
     * 3. Driver accepts
     * 4. Processes payment
     * 5. Completes the ride
     * 6. Sends notifications
     * 7. Registers audit
     */
    @PostMapping("/request-ride")
    public ResponseEntity<OrchestratorResponseDTO> processRideRequest(
            @Valid @RequestBody RideRequestDTO request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        OrchestratorResponseDTO response = orchestratorService.processCompleteRideRequest(request, clientIp);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * REASSIGN SERVICE
     * When a driver rejects a ride request, find another driver
     */
    @PostMapping("/reassign/{rideRequestId}")
    public ResponseEntity<OrchestratorResponseDTO> reassignRide(
            @PathVariable Long rideRequestId,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        OrchestratorResponseDTO response = orchestratorService.reassignRide(rideRequestId, clientIp);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * DRIVER ACCEPTS RIDE
     */
    @PostMapping("/{rideRequestId}/driver/accept")
    public ResponseEntity<OrchestratorResponseDTO> driverAcceptRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long driverId,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        OrchestratorResponseDTO response = orchestratorService.driverAcceptRide(rideRequestId, driverId, clientIp);
        return ResponseEntity.ok(response);
    }

    /**
     * DRIVER REJECTS RIDE
     */
    @PostMapping("/{rideRequestId}/driver/reject")
    public ResponseEntity<OrchestratorResponseDTO> driverRejectRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long driverId,
            @RequestParam(required = false) String reason,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        OrchestratorResponseDTO response = orchestratorService.driverRejectRide(rideRequestId, driverId, reason, clientIp);
        return ResponseEntity.ok(response);
    }

    /**
     * COMPLETE RIDE
     */
    @PostMapping("/{rideRequestId}/complete")
    public ResponseEntity<OrchestratorResponseDTO> completeRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long driverId,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        OrchestratorResponseDTO response = orchestratorService.completeRide(rideRequestId, driverId, clientIp);
        return ResponseEntity.ok(response);
    }

    /**
     * CANCEL RIDE
     */
    @PostMapping("/{rideRequestId}/cancel")
    public ResponseEntity<OrchestratorResponseDTO> cancelRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long clientId,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        OrchestratorResponseDTO response = orchestratorService.cancelRide(rideRequestId, clientId, clientIp);
        return ResponseEntity.ok(response);
    }

    /**
     * GET COMPLETE TRIP STATUS
     * Retrieves all information related to a ride request:
     * - Ride request data
     * - Current assignment
     * - Assignment history
     * - Payment info
     * - Driver info
     */
    @GetMapping("/status/{rideRequestId}")
    public ResponseEntity<TripStatusDTO> getCompleteTripStatus(@PathVariable Long rideRequestId) {
        return ResponseEntity.ok(orchestratorService.getCompleteTripStatus(rideRequestId));
    }

    /**
     * GET RIDE STATUS SIMPLE
     */
    @GetMapping("/{rideRequestId}/status")
    public ResponseEntity<String> getRideStatus(@PathVariable Long rideRequestId) {
        return ResponseEntity.ok(orchestratorService.getRideStatus(rideRequestId));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}