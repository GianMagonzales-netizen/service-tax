package com.gianmarco.soa.auth.audit.repository;

import com.gianmarco.soa.auth.audit.entity.AuditEntity;
import com.gianmarco.soa.auth.audit.enums.AuditEventType;
import com.gianmarco.soa.auth.audit.enums.AuditEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, Long> {

    // ===== QUERIES BY ENTITY =====

    List<AuditEntity> findByEntityTypeAndEntityIdOrderByTimestampDesc(AuditEntityType entityType, Long entityId);

    Page<AuditEntity> findByEntityTypeAndEntityId(AuditEntityType entityType, Long entityId, Pageable pageable);

    // ===== QUERIES BY USER =====

    List<AuditEntity> findByUserIdOrderByTimestampDesc(Long userId);

    Page<AuditEntity> findByUserId(Long userId, Pageable pageable);

    // ===== QUERIES BY DATE =====

    List<AuditEntity> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    // ===== QUERIES BY EVENT TYPE =====

    List<AuditEntity> findByEventTypeOrderByTimestampDesc(AuditEventType eventType);

    long countByEventType(AuditEventType eventType);

    // ===== COMPLEX QUERIES =====

    @Query("SELECT a FROM AuditEntity a WHERE " +
            "(:eventType IS NULL OR a.eventType = :eventType) AND " +
            "(:entityType IS NULL OR a.entityType = :entityType) AND " +
            "(:entityId IS NULL OR a.entityId = :entityId) AND " +
            "(:userId IS NULL OR a.userId = :userId) AND " +
            "(:ipAddress IS NULL OR a.ipAddress = :ipAddress) AND " +
            "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR a.timestamp <= :endDate)")
    List<AuditEntity> findByFilters(@Param("eventType") AuditEventType eventType,
                                    @Param("entityType") AuditEntityType entityType,
                                    @Param("entityId") Long entityId,
                                    @Param("userId") Long userId,
                                    @Param("ipAddress") String ipAddress,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    // ===== STATISTICS =====

    @Query("SELECT a.eventType, COUNT(a) FROM AuditEntity a WHERE a.timestamp >= :since GROUP BY a.eventType")
    List<Object[]> countEventsByTypeSince(@Param("since") LocalDateTime since);

    @Query("SELECT a.entityType, COUNT(a) FROM AuditEntity a WHERE a.timestamp >= :since GROUP BY a.entityType")
    List<Object[]> countEventsByEntitySince(@Param("since") LocalDateTime since);

    @Query("SELECT FUNCTION('DATE', a.timestamp), COUNT(a) FROM AuditEntity a " +
            "WHERE a.timestamp BETWEEN :start AND :end GROUP BY FUNCTION('DATE', a.timestamp)")
    List<Object[]> getDailyEventCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // ===== CLEANUP =====

    @Modifying
    @Transactional
    @Query("DELETE FROM AuditEntity a WHERE a.timestamp < :limitDate")
    int deleteOldAuditLogs(@Param("limitDate") LocalDateTime limitDate);
}