package com.gianmarco.soa.auth.payment.repository;

import com.gianmarco.soa.auth.payment.entity.PaymentEntity;
import com.gianmarco.soa.auth.payment.enums.PaymentMethod;
import com.gianmarco.soa.auth.payment.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    // ===== QUERIES BY RIDE REQUEST =====

    Optional<PaymentEntity> findByRideRequestId(Long rideRequestId);

    List<PaymentEntity> findByRideRequestIdIn(List<Long> rideRequestIds);

    // ===== QUERIES BY CLIENT =====

    List<PaymentEntity> findByClientIdOrderByPaymentDateDesc(Long clientId);

    Page<PaymentEntity> findByClientIdOrderByPaymentDateDesc(Long clientId, Pageable pageable);

    // ===== QUERIES BY DRIVER =====

    List<PaymentEntity> findByDriverIdOrderByPaymentDateDesc(Long driverId);

    Page<PaymentEntity> findByDriverIdOrderByPaymentDateDesc(Long driverId, Pageable pageable);

    // ===== QUERIES BY STATUS =====

    List<PaymentEntity> findByStatus(PaymentStatus status);

    List<PaymentEntity> findByStatusAndPaymentDateBefore(PaymentStatus status, LocalDateTime date);

    List<PaymentEntity> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime date);

    // ===== QUERIES BY PAYMENT METHOD =====

    List<PaymentEntity> findByPaymentMethod(PaymentMethod paymentMethod);

    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM PaymentEntity p WHERE p.status = 'COMPLETED' GROUP BY p.paymentMethod")
    List<Object[]> sumAmountByPaymentMethod();

    // ===== STATISTICS =====

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.status = 'COMPLETED'")
    Double sumTotalAmountCompleted();

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.clientId = :clientId AND p.status = 'COMPLETED'")
    Double sumAmountByClient(@Param("clientId") Long clientId);

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.driverId = :driverId AND p.status = 'COMPLETED'")
    Double sumAmountByDriver(@Param("driverId") Long driverId);

    @Query("SELECT SUM(p.fee) FROM PaymentEntity p WHERE p.status = 'COMPLETED'")
    Double sumTotalFee();

    long countByStatus(PaymentStatus status);

    @Query("SELECT FUNCTION('DATE', p.paymentDate), COUNT(p), SUM(p.amount) FROM PaymentEntity p " +
            "WHERE p.paymentDate BETWEEN :start AND :end AND p.status = 'COMPLETED' " +
            "GROUP BY FUNCTION('DATE', p.paymentDate) ORDER BY FUNCTION('DATE', p.paymentDate)")
    List<Object[]> getDailyReport(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM PaymentEntity p " +
            "WHERE p.paymentDate BETWEEN :start AND :end AND p.status = 'COMPLETED' " +
            "GROUP BY p.paymentMethod")
    List<Object[]> getReportByPaymentMethod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // ===== ADDITIONAL QUERIES =====

    boolean existsByRideRequestIdAndStatus(Long rideRequestId, PaymentStatus status);

    @Query("SELECT COUNT(p) FROM PaymentEntity p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :start AND :end")
    long countCompletedPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.status = 'COMPLETED'")
    double getTotalRevenue();

    @Query("SELECT COALESCE(SUM(p.fee), 0) FROM PaymentEntity p WHERE p.status = 'COMPLETED'")
    double getTotalFees();
}