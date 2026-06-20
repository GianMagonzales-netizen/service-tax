package com.gianmarco.soa.auth.ride_request.controller;

import com.gianmarco.soa.auth.ride_request.dto.RideRequestDTO;
import com.gianmarco.soa.auth.ride_request.dto.RideResponseDTO;
import com.gianmarco.soa.auth.ride_request.dto.RatingRequestDTO;
import com.gianmarco.soa.auth.ride_request.service.RideRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ride-requests")
public class RideRequestController {

    @Autowired
    private RideRequestService rideRequestService;

    // ===== CREATE RIDE REQUEST =====

    @PostMapping
    public ResponseEntity<RideResponseDTO> createRideRequest(@Valid @RequestBody RideRequestDTO request) {
        RideResponseDTO response = rideRequestService.createRideRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===== MATCHING SERVICE (HEART OF THE SYSTEM - 4 CRITERIA) =====

    @PostMapping("/{rideRequestId}/assign")
    public ResponseEntity<RideResponseDTO> assignDriver(@PathVariable Long rideRequestId) {
        RideResponseDTO response = rideRequestService.assignDriverToRide(rideRequestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideRequestId}/reassign")
    public ResponseEntity<RideResponseDTO> reassignDriver(@PathVariable Long rideRequestId) {
        RideResponseDTO response = rideRequestService.reassignDriverToRide(rideRequestId);
        return ResponseEntity.ok(response);
    }

    // ===== DRIVER RESPONSE (Flow 3.2.3) =====

    @PostMapping("/{rideRequestId}/driver/accept")
    public ResponseEntity<RideResponseDTO> driverAcceptRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long driverId) {
        RideResponseDTO response = rideRequestService.driverAcceptRide(rideRequestId, driverId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideRequestId}/driver/reject")
    public ResponseEntity<RideResponseDTO> driverRejectRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long driverId,
            @RequestParam(required = false) String reason) {
        RideResponseDTO response = rideRequestService.driverRejectRide(rideRequestId, driverId, reason);
        return ResponseEntity.ok(response);
    }

    // ===== RIDE COMPLETION (Flow 3.2.4) =====

    @PostMapping("/{rideRequestId}/complete")
    public ResponseEntity<RideResponseDTO> completeRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long driverId) {
        RideResponseDTO response = rideRequestService.completeRide(rideRequestId, driverId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rideRequestId}/cancel")
    public ResponseEntity<RideResponseDTO> cancelRide(
            @PathVariable Long rideRequestId,
            @RequestParam Long clientId) {
        RideResponseDTO response = rideRequestService.cancelRide(rideRequestId, clientId);
        return ResponseEntity.ok(response);
    }

    // ===== RATING =====

    @PostMapping("/rate")
    public ResponseEntity<Void> rateDriver(@Valid @RequestBody RatingRequestDTO ratingRequest) {
        rideRequestService.rateDriver(ratingRequest);
        return ResponseEntity.ok().build();
    }

    // ===== QUERIES =====

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDTO> getRideRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(rideRequestService.getRideRequestById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<RideResponseDTO>> getRideRequestsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(rideRequestService.getRideRequestsByClient(clientId));
    }

    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<List<RideResponseDTO>> getActiveRidesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(rideRequestService.getActiveRidesByClient(clientId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideResponseDTO>> getRideRequestsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideRequestService.getRideRequestsByDriver(driverId));
    }

    @GetMapping("/driver/{driverId}/active")
    public ResponseEntity<List<RideResponseDTO>> getActiveRidesByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideRequestService.getActiveRidesByDriver(driverId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RideResponseDTO>> getRideRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(rideRequestService.getRideRequestsByStatus(status));
    }

    // ===== STATISTICS =====

    @GetMapping("/stats/pending/count")
    public ResponseEntity<Long> countPendingRides() {
        return ResponseEntity.ok(rideRequestService.countPendingRides());
    }

    @GetMapping("/stats/in-progress/count")
    public ResponseEntity<Long> countInProgressRides() {
        return ResponseEntity.ok(rideRequestService.countInProgressRides());
    }

    @GetMapping("/stats/completed/count")
    public ResponseEntity<Long> countCompletedRides() {
        return ResponseEntity.ok(rideRequestService.countCompletedRides());
    }
}