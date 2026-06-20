package com.gianmarco.soa.auth.ride_request.repository;

import com.gianmarco.soa.auth.ride_request.entity.RideRequestEntity;
import com.gianmarco.soa.auth.ride_request.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequestEntity, Long> {

    // ===== QUERIES BY CLIENT =====

    List<RideRequestEntity> findByClientId(Long clientId);

    List<RideRequestEntity> findByClientIdAndStatus(Long clientId, RideStatus status);

    Optional<RideRequestEntity> findFirstByClientIdOrderByCreatedAtDesc(Long clientId);

    // ===== QUERIES BY DRIVER =====

    List<RideRequestEntity> findByDriverId(Long driverId);

    List<RideRequestEntity> findByDriverIdAndStatus(Long driverId, RideStatus status);

    long countByDriverIdAndStatus(Long driverId, RideStatus status);

    Optional<RideRequestEntity> findFirstByDriverIdAndStatusOrderByAssignedAtDesc(Long driverId, RideStatus status);

    // ===== QUERIES BY STATUS =====

    List<RideRequestEntity> findByStatus(RideStatus status);

    List<RideRequestEntity> findByStatusAndExpiresAtBefore(RideStatus status, LocalDateTime now);

    List<RideRequestEntity> findByStatusAndCreatedAtBefore(RideStatus status, LocalDateTime createdAt);

    // ===== FOR ORCHESTRATOR / MATCHING =====

    Optional<RideRequestEntity> findFirstByStatusOrderByCreatedAtAsc(RideStatus status);

    @Query("SELECT r FROM RideRequestEntity r WHERE r.status = 'ASSIGNED' AND r.expiresAt < :now")
    List<RideRequestEntity> findExpiredAssignments(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM RideRequestEntity r WHERE r.driverId = :driverId AND r.status IN ('ASSIGNED', 'IN_PROGRESS')")
    List<RideRequestEntity> findActiveRidesByDriver(@Param("driverId") Long driverId);

    @Query("SELECT r FROM RideRequestEntity r WHERE r.clientId = :clientId AND r.status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS')")
    List<RideRequestEntity> findActiveRidesByClient(@Param("clientId") Long clientId);

    // ===== UPDATE OPERATIONS =====

    @Modifying
    @Transactional
    @Query("UPDATE RideRequestEntity r SET r.status = 'EXPIRED' WHERE r.id = :id AND r.status = 'ASSIGNED'")
    int expireAssignment(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE RideRequestEntity r SET r.status = 'EXPIRED' WHERE r.status = 'ASSIGNED' AND r.expiresAt < :now")
    int expireAllExpiredAssignments(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE RideRequestEntity r SET r.status = 'CANCELLED', r.cancelledAt = :now WHERE r.id = :id AND r.clientId = :clientId AND r.status IN ('PENDING', 'ASSIGNED')")
    int cancelRideByClient(@Param("id") Long id, @Param("clientId") Long clientId, @Param("now") LocalDateTime now);

    // ===== STATISTICS =====

    long countByStatus(RideStatus status);

    long countByServiceTypeAndStatus(com.gianmarco.soa.auth.driver.enums.ServiceType serviceType, RideStatus status);

    @Query("SELECT COUNT(r) FROM RideRequestEntity r WHERE r.status = 'COMPLETED' AND r.completedAt BETWEEN :start AND :end")
    long countCompletedRidesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT AVG(r.finalFare) FROM RideRequestEntity r WHERE r.status = 'COMPLETED'")
    Double getAverageFare();

    // ===== EXISTENCE CHECKS =====

    boolean existsByClientIdAndStatusIn(Long clientId, List<RideStatus> statuses);

    boolean existsByDriverIdAndStatusIn(Long driverId, List<RideStatus> statuses);
}