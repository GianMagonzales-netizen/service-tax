package com.gianmarco.soa.auth.ride_assignment.repository;

import com.gianmarco.soa.auth.ride_assignment.entity.RideAssignmentEntity;
import com.gianmarco.soa.auth.ride_assignment.enums.AssignmentStatus;
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
public interface RideAssignmentRepository extends JpaRepository<RideAssignmentEntity, Long> {

    // ===== QUERIES BY RIDE REQUEST =====

    List<RideAssignmentEntity> findByRideRequestId(Long rideRequestId);

    List<RideAssignmentEntity> findByRideRequestIdOrderByAssignmentAttemptDesc(Long rideRequestId);

    Optional<RideAssignmentEntity> findFirstByRideRequestIdOrderByAssignedAtDesc(Long rideRequestId);

    Optional<RideAssignmentEntity> findFirstByRideRequestIdAndStatusOrderByAssignedAtDesc(Long rideRequestId, AssignmentStatus status);

    // ===== QUERIES BY DRIVER =====

    List<RideAssignmentEntity> findByDriverId(Long driverId);

    List<RideAssignmentEntity> findByDriverIdAndStatus(Long driverId, AssignmentStatus status);

    Optional<RideAssignmentEntity> findFirstByDriverIdAndStatusOrderByAssignedAtDesc(Long driverId, AssignmentStatus status);

    // ===== QUERIES BY STATUS =====

    List<RideAssignmentEntity> findByStatus(AssignmentStatus status);

    List<RideAssignmentEntity> findByStatusAndExpiresAtBefore(AssignmentStatus status, LocalDateTime now);

    // ===== FOR ORCHESTRATOR / MATCHING =====

    @Query("SELECT a FROM RideAssignmentEntity a WHERE a.rideRequestId = :rideRequestId AND a.status = 'ACCEPTED'")
    Optional<RideAssignmentEntity> findAcceptedAssignmentByRideRequest(@Param("rideRequestId") Long rideRequestId);

    @Query("SELECT a FROM RideAssignmentEntity a WHERE a.rideRequestId = :rideRequestId AND a.status IN ('PENDING', 'ACCEPTED')")
    List<RideAssignmentEntity> findActiveAssignmentsByRideRequest(@Param("rideRequestId") Long rideRequestId);

    @Query("SELECT a FROM RideAssignmentEntity a WHERE a.driverId = :driverId AND a.status IN ('PENDING', 'ACCEPTED')")
    List<RideAssignmentEntity> findActiveAssignmentsByDriver(@Param("driverId") Long driverId);

    @Query("SELECT a FROM RideAssignmentEntity a WHERE a.status = 'PENDING' AND a.expiresAt < :now")
    List<RideAssignmentEntity> findExpiredPendingAssignments(@Param("now") LocalDateTime now);

    // ===== CHECK EXISTENCE =====

    boolean existsByRideRequestIdAndStatus(Long rideRequestId, AssignmentStatus status);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM RideAssignmentEntity a WHERE a.rideRequestId = :rideRequestId AND a.status IN :statuses")
    boolean existsByRideRequestIdAndStatusIn(@Param("rideRequestId") Long rideRequestId, @Param("statuses") List<AssignmentStatus> statuses);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM RideAssignmentEntity a WHERE a.driverId = :driverId AND a.status IN :statuses")
    boolean existsByDriverIdAndStatusIn(@Param("driverId") Long driverId, @Param("statuses") List<AssignmentStatus> statuses);

    // ===== UPDATE OPERATIONS =====

    @Modifying
    @Transactional
    @Query("UPDATE RideAssignmentEntity a SET a.status = 'EXPIRED' WHERE a.id = :id AND a.status = 'PENDING'")
    int expireAssignment(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE RideAssignmentEntity a SET a.status = 'EXPIRED' WHERE a.status = 'PENDING' AND a.expiresAt < :now")
    int expireAllExpiredAssignments(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE RideAssignmentEntity a SET a.finalFare = :finalFare, a.status = 'COMPLETED' WHERE a.id = :id AND a.status = 'ACCEPTED'")
    int completeAssignment(@Param("id") Long id, @Param("finalFare") Double finalFare);

    // ===== STATISTICS =====

    long countByStatus(AssignmentStatus status);

    long countByRideRequestIdAndStatus(Long rideRequestId, AssignmentStatus status);

    @Query("SELECT AVG(a.distanceKm) FROM RideAssignmentEntity a WHERE a.status = 'COMPLETED'")
    Double getAverageDistance();

    @Query("SELECT AVG(a.estimatedFare) FROM RideAssignmentEntity a WHERE a.status = 'COMPLETED'")
    Double getAverageFare();

    @Query("SELECT COUNT(a) FROM RideAssignmentEntity a WHERE a.status = 'REJECTED' AND a.respondedAt BETWEEN :start AND :end")
    long countRejectionsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // ===== BATCH OPERATIONS =====

    @Modifying
    @Transactional
    @Query("UPDATE RideAssignmentEntity a SET a.status = 'EXPIRED' WHERE a.rideRequestId = :rideRequestId AND a.status = 'PENDING'")
    int expireAllPendingByRideRequest(@Param("rideRequestId") Long rideRequestId);
}