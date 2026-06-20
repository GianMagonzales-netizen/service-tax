package com.gianmarco.soa.auth.payment.service;

import com.gianmarco.soa.auth.payment.dto.PaymentRequestDTO;
import com.gianmarco.soa.auth.payment.dto.PaymentResponseDTO;
import com.gianmarco.soa.auth.payment.entity.PaymentEntity;
import com.gianmarco.soa.auth.payment.enums.PaymentStatus;
import com.gianmarco.soa.auth.payment.repository.PaymentRepository;
import com.gianmarco.soa.auth.ride_request.entity.RideRequestEntity;
import com.gianmarco.soa.auth.ride_request.enums.RideStatus;
import com.gianmarco.soa.auth.ride_request.repository.RideRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    // ===== PROCESS PAYMENT (Simulated - within project limits) =====

    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        // Verify ride request exists
        RideRequestEntity rideRequest = rideRequestRepository.findById(request.getRideRequestId())
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        // ✅ CORREGIDO: Permitir pago para rides en IN_PROGRESS o COMPLETED
        if (rideRequest.getStatus() != RideStatus.IN_PROGRESS && rideRequest.getStatus() != RideStatus.COMPLETED) {
            throw new RuntimeException("Payment can only be processed for rides in progress or completed. Current status: " + rideRequest.getStatus());
        }

        // Check if payment already exists for this ride
        if (paymentRepository.existsByRideRequestIdAndStatus(request.getRideRequestId(), PaymentStatus.COMPLETED)) {
            throw new RuntimeException("Payment already processed for this ride");
        }

        // Create payment entity
        PaymentEntity payment = new PaymentEntity();
        payment.setRideRequestId(request.getRideRequestId());
        payment.setAssignmentId(request.getAssignmentId());
        payment.setClientId(request.getClientId());
        payment.setDriverId(request.getDriverId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentMethodDetail(request.getPaymentMethodDetail());
        payment.setCurrency(request.getCurrency());
        payment.setFee(request.getFee());
        payment.setDescription(request.getDescription());

        // Calculate final amount (amount - fee)
        double finalAmount = request.getAmount() - request.getFee();
        payment.setFinalAmount(finalAmount);

        // Simulate successful payment processing
        payment.complete();

        PaymentEntity saved = paymentRepository.save(payment);
        return PaymentResponseDTO.fromEntity(saved);
    }

    @Transactional
    public PaymentResponseDTO processSimulatedPayment(PaymentRequestDTO request) {
        return processPayment(request);
    }

    private String generateTransactionCode() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ===== QUERIES =====

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByRideRequest(Long rideRequestId) {
        PaymentEntity payment = paymentRepository.findByRideRequestId(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Payment not found for ride request: " + rideRequestId));
        return PaymentResponseDTO.fromEntity(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByClient(Long clientId) {
        return paymentRepository.findByClientIdOrderByPaymentDateDesc(clientId)
                .stream()
                .map(PaymentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByClientPaginated(Long clientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentEntity> payments = paymentRepository.findByClientIdOrderByPaymentDateDesc(clientId, pageable);
        return payments.map(PaymentResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByDriver(Long driverId) {
        return paymentRepository.findByDriverIdOrderByPaymentDateDesc(driverId)
                .stream()
                .map(PaymentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByDriverPaginated(Long driverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentEntity> payments = paymentRepository.findByDriverIdOrderByPaymentDateDesc(driverId, pageable);
        return payments.map(PaymentResponseDTO::fromEntity);
    }

    // ===== REFUNDS =====

    @Transactional
    public PaymentResponseDTO refundPayment(Long paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        payment.refund();
        PaymentEntity updated = paymentRepository.save(payment);
        return PaymentResponseDTO.fromEntity(updated);
    }

    // ===== STATISTICS =====

    @Transactional(readOnly = true)
    public Double getTotalRevenue() {
        Double total = paymentRepository.sumTotalAmountCompleted();
        return total != null ? total : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTotalByClient(Long clientId) {
        Double total = paymentRepository.sumAmountByClient(clientId);
        return total != null ? total : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTotalByDriver(Long driverId) {
        Double total = paymentRepository.sumAmountByDriver(driverId);
        return total != null ? total : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTotalFees() {
        Double total = paymentRepository.sumTotalFee();
        return total != null ? total : 0.0;
    }

    @Transactional(readOnly = true)
    public long countCompletedPayments() {
        return paymentRepository.countByStatus(PaymentStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getDailyReport(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.getDailyReport(start, end);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getReportByPaymentMethod(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.getReportByPaymentMethod(start, end);
    }

    @Transactional(readOnly = true)
    public long countCompletedPaymentsBetween(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.countCompletedPaymentsBetween(start, end);
    }
}