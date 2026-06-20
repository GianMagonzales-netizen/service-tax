package com.gianmarco.soa.auth.payment.controller;

import com.gianmarco.soa.auth.payment.dto.PaymentRequestDTO;
import com.gianmarco.soa.auth.payment.dto.PaymentResponseDTO;
import com.gianmarco.soa.auth.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ===== PROCESS PAYMENT (Simulated - within project limits) =====

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/simulate")
    public ResponseEntity<PaymentResponseDTO> simulatePayment(@Valid @RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.processSimulatedPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===== QUERIES =====

    @GetMapping("/ride-request/{rideRequestId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByRideRequest(@PathVariable Long rideRequestId) {
        return ResponseEntity.ok(paymentService.getPaymentByRideRequest(rideRequestId));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(paymentService.getPaymentsByClient(clientId));
    }

    @GetMapping("/client/{clientId}/page")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentsByClientPaginated(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.getPaymentsByClientPaginated(clientId, page, size));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDriver(driverId));
    }

    @GetMapping("/driver/{driverId}/page")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentsByDriverPaginated(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.getPaymentsByDriverPaginated(driverId, page, size));
    }

    // ===== REFUNDS =====

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }

    // ===== STATISTICS =====

    @GetMapping("/stats/total-revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(paymentService.getTotalRevenue());
    }

    @GetMapping("/stats/client/{clientId}/total")
    public ResponseEntity<Double> getTotalByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(paymentService.getTotalByClient(clientId));
    }

    @GetMapping("/stats/driver/{driverId}/total")
    public ResponseEntity<Double> getTotalByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(paymentService.getTotalByDriver(driverId));
    }

    @GetMapping("/stats/total-fees")
    public ResponseEntity<Double> getTotalFees() {
        return ResponseEntity.ok(paymentService.getTotalFees());
    }

    @GetMapping("/stats/completed/count")
    public ResponseEntity<Long> countCompletedPayments() {
        return ResponseEntity.ok(paymentService.countCompletedPayments());
    }

    @GetMapping("/stats/daily-report")
    public ResponseEntity<List<Object[]>> getDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(paymentService.getDailyReport(start, end));
    }

    @GetMapping("/stats/payment-method-report")
    public ResponseEntity<List<Object[]>> getReportByPaymentMethod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(paymentService.getReportByPaymentMethod(start, end));
    }
}
