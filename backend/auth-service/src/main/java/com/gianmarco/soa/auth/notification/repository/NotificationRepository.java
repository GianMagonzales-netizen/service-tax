package com.gianmarco.soa.auth.notification.repository;

import com.gianmarco.soa.auth.notification.entity.NotificationEntity;
import com.gianmarco.soa.auth.notification.enums.NotificationStatus;
import com.gianmarco.soa.auth.notification.enums.NotificationType;
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
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    // ===== QUERIES BY USER =====

    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<NotificationEntity> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);

    // ===== QUERIES BY STATUS =====

    List<NotificationEntity> findByStatus(NotificationStatus status);

    List<NotificationEntity> findByStatusAndCreatedAtBefore(NotificationStatus status, LocalDateTime date);

    // ===== UNREAD NOTIFICATIONS =====

    List<NotificationEntity> findByUserIdAndStatus(Long userId, NotificationStatus status);

    long countByUserIdAndStatus(Long userId, NotificationStatus status);

    // ===== QUERIES BY TYPE =====

    List<NotificationEntity> findByUserIdAndType(Long userId, NotificationType type);

    List<NotificationEntity> findByType(NotificationType type);

    // ===== QUERIES BY RELATED ID =====

    List<NotificationEntity> findByRelatedId(Long relatedId);

    List<NotificationEntity> findByUserIdAndRelatedId(Long userId, Long relatedId);

    // ===== UPDATE OPERATIONS =====

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.status = :status, n.readAt = :readAt WHERE n.id = :id AND n.userId = :userId")
    int updateNotificationStatus(@Param("id") Long id,
                                 @Param("userId") Long userId,
                                 @Param("status") NotificationStatus status,
                                 @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.status = 'LEIDA', n.readAt = :readAt WHERE n.userId = :userId AND n.status = 'ENVIADA'")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    // ===== DELETE OPERATIONS =====

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.status = 'ELIMINADA' WHERE n.id = :id AND n.userId = :userId")
    int deleteNotification(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Transactional
    void deleteByUserIdAndStatus(Long userId, NotificationStatus status);

    // ===== EXISTENCE CHECKS =====

    boolean existsByUserIdAndId(Long userId, Long id);

    boolean existsByUserIdAndStatus(Long userId, NotificationStatus status);

    // ===== STATISTICS =====

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.status = 'ENVIADA'")
    long countUnreadByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.createdAt BETWEEN :start AND :end")
    long countNotificationsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT n.type, COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId GROUP BY n.type")
    List<Object[]> countByTypeForUser(@Param("userId") Long userId);
}