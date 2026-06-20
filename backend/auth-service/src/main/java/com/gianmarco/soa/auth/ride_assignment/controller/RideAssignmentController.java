package com.gianmarco.soa.auth.ride_assignment.controller;

import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentRequestDTO;
import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentResponseDTO;
import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentReplyDTO;
import com.gianmarco.soa.auth.ride_assignment.dto.AssignmentCompleteDTO;
import com.gianmarco.soa.auth.ride_assignment.service.RideAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
public class RideAssignmentController {

    @Autowired
    private RideAssignmentService rideAssignmentService;

    // ===== CREATE ASSIGNMENT =====

    @PostMapping
    public ResponseEntity<AssignmentResponseDTO> createAssignment(@Valid @RequestBody AssignmentRequestDTO request) {
        AssignmentResponseDTO response = rideAssignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===== DRIVER RESPONSE =====

    @PostMapping("/respond")
    public ResponseEntity<AssignmentResponseDTO> driverRespond(@Valid @RequestBody AssignmentReplyDTO request) {
        if (request.getAccept()) {
            AssignmentResponseDTO response = rideAssignmentService.driverAcceptAssignment(
                    request.getAssignmentId(), request.getDriverId());
            return ResponseEntity.ok(response);
        } else {
            AssignmentResponseDTO response = rideAssignmentService.driverRejectAssignment(
                    request.getAssignmentId(), request.getDriverId(), request.getRejectionReason());
            return ResponseEntity.ok(response);
        }
    }

    // ===== COMPLETE ASSIGNMENT =====

    @PostMapping("/complete")
    public ResponseEntity<AssignmentResponseDTO> completeAssignment(@Valid @RequestBody AssignmentCompleteDTO request) {
        AssignmentResponseDTO response = rideAssignmentService.completeAssignment(
                request.getAssignmentId(), request.getDriverId(), request.getFinalFare());
        return ResponseEntity.ok(response);
    }

    // ===== QUERIES =====

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponseDTO> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(rideAssignmentService.getAssignmentById(id));
    }

    @GetMapping("/ride-request/{rideRequestId}")
    public ResponseEntity<List<AssignmentResponseDTO>> getAssignmentsByRideRequest(@PathVariable Long rideRequestId) {
        return ResponseEntity.ok(rideAssignmentService.getAssignmentsByRideRequest(rideRequestId));
    }

    @GetMapping("/ride-request/{rideRequestId}/accepted")
    public ResponseEntity<AssignmentResponseDTO> getAcceptedAssignmentByRideRequest(@PathVariable Long rideRequestId) {
        Optional<AssignmentResponseDTO> assignment = rideAssignmentService.getAcceptedAssignmentByRideRequest(rideRequestId);
        return assignment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<AssignmentResponseDTO>> getAssignmentsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideAssignmentService.getAssignmentsByDriver(driverId));
    }

    @GetMapping("/driver/{driverId}/active")
    public ResponseEntity<List<AssignmentResponseDTO>> getActiveAssignmentsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideAssignmentService.getActiveAssignmentsByDriver(driverId));
    }

    // ===== STATISTICS =====

    @GetMapping("/stats/pending/count")
    public ResponseEntity<Long> countPendingAssignments() {
        return ResponseEntity.ok(rideAssignmentService.countPendingAssignments());
    }

    @GetMapping("/stats/accepted/count")
    public ResponseEntity<Long> countAcceptedAssignments() {
        return ResponseEntity.ok(rideAssignmentService.countAcceptedAssignments());
    }

    @GetMapping("/stats/completed/count")
    public ResponseEntity<Long> countCompletedAssignments() {
        return ResponseEntity.ok(rideAssignmentService.countCompletedAssignments());
    }

    @GetMapping("/stats/rejected/count")
    public ResponseEntity<Long> countRejectedAssignments() {
        return ResponseEntity.ok(rideAssignmentService.countRejectedAssignments());
    }

    @GetMapping("/stats/average-distance")
    public ResponseEntity<Double> getAverageDistance() {
        Double average = rideAssignmentService.getAverageDistance();
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/stats/average-fare")
    public ResponseEntity<Double> getAverageFare() {
        Double average = rideAssignmentService.getAverageFare();
        return ResponseEntity.ok(average != null ? average : 0.0);
    }
}