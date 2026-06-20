package com.gianmarco.soa.auth.notification.service;

import com.gianmarco.soa.auth.notification.dto.NotificationEventDTO;
import com.gianmarco.soa.auth.notification.dto.NotificationRequestDTO;
import com.gianmarco.soa.auth.notification.dto.NotificationResponseDTO;
import com.gianmarco.soa.auth.notification.entity.NotificationEntity;
import com.gianmarco.soa.auth.notification.enums.NotificationStatus;
import com.gianmarco.soa.auth.notification.enums.NotificationType;
import com.gianmarco.soa.auth.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // ===== CREATE NOTIFICATION =====

    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(request.getUserId());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setRelatedId(request.getRelatedId());
        notification.setStatus(NotificationStatus.ENVIADA);

        NotificationEntity saved = notificationRepository.save(notification);
        return NotificationResponseDTO.fromEntity(saved);
    }

    // ===== EVENT NOTIFICATIONS =====

    @Transactional
    public void notifyEvent(NotificationEventDTO event) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserId(event.getUserId());
        request.setTitle(event.getEventType());
        request.setMessage(event.getMessage());
        request.setType(NotificationType.valueOf(event.getEventType()));
        request.setRelatedId(event.getRelatedId());

        createNotification(request);
    }

    @Transactional
    public void notifyRideCreated(Long clientId, Long rideRequestId) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserId(clientId);
        request.setTitle("RIDE_CREATED");
        request.setMessage("Your ride request #" + rideRequestId + " has been created successfully. We are looking for a driver.");
        request.setType(NotificationType.RIDE_CREATED);
        request.setRelatedId(rideRequestId);

        createNotification(request);
    }

    @Transactional
    public void notifyDriverAssigned(Long clientId, Long driverId, Long rideRequestId) {
        // Notify client
        NotificationRequestDTO clientRequest = new NotificationRequestDTO();
        clientRequest.setUserId(clientId);
        clientRequest.setTitle("DRIVER_ASSIGNED");
        clientRequest.setMessage("A driver has been assigned to your ride #" + rideRequestId + ". The driver is on the way.");
        clientRequest.setType(NotificationType.DRIVER_ASSIGNED);
        clientRequest.setRelatedId(rideRequestId);

        createNotification(clientRequest);

        // Notify driver
        NotificationRequestDTO driverRequest = new NotificationRequestDTO();
        driverRequest.setUserId(driverId);
        driverRequest.setTitle("NEW_RIDE_ASSIGNED");
        driverRequest.setMessage("You have been assigned to a new ride #" + rideRequestId + ". Please check the details.");
        driverRequest.setType(NotificationType.DRIVER_ASSIGNED);
        driverRequest.setRelatedId(rideRequestId);

        createNotification(driverRequest);
    }

    @Transactional
    public void notifyDriverResponse(Long clientId, Long rideRequestId, boolean accepted) {
        String status = accepted ? "accepted" : "rejected";
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserId(clientId);
        request.setTitle(accepted ? "RIDE_ACCEPTED" : "RIDE_REJECTED");
        request.setMessage("The driver has " + status + " your ride request #" + rideRequestId + ".");
        request.setType(accepted ? NotificationType.RIDE_ACCEPTED : NotificationType.RIDE_REJECTED);
        request.setRelatedId(rideRequestId);

        createNotification(request);
    }

    @Transactional
    public void notifyRideCompleted(Long clientId, Long driverId, Long rideRequestId) {
        // Notify client
        NotificationRequestDTO clientRequest = new NotificationRequestDTO();
        clientRequest.setUserId(clientId);
        clientRequest.setTitle("RIDE_COMPLETED");
        clientRequest.setMessage("Your ride #" + rideRequestId + " has been completed. Thank you for using our service!");
        clientRequest.setType(NotificationType.RIDE_COMPLETED);
        clientRequest.setRelatedId(rideRequestId);

        createNotification(clientRequest);

        // Notify driver
        NotificationRequestDTO driverRequest = new NotificationRequestDTO();
        driverRequest.setUserId(driverId);
        driverRequest.setTitle("RIDE_COMPLETED");
        driverRequest.setMessage("Ride #" + rideRequestId + " has been completed. You can now rate the client.");
        driverRequest.setType(NotificationType.RIDE_COMPLETED);
        driverRequest.setRelatedId(rideRequestId);

        createNotification(driverRequest);
    }

    @Transactional
    public void notifyRideCancelled(Long clientId, Long rideRequestId) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserId(clientId);
        request.setTitle("RIDE_CANCELLED");
        request.setMessage("Your ride #" + rideRequestId + " has been cancelled.");
        request.setType(NotificationType.RIDE_CANCELLED);
        request.setRelatedId(rideRequestId);

        createNotification(request);
    }

    @Transactional
    public void notifyPaymentConfirmed(Long clientId, Long rideRequestId, String transactionCode) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setUserId(clientId);
        request.setTitle("PAYMENT_CONFIRMED");
        request.setMessage("Your payment for ride #" + rideRequestId + " has been confirmed. Transaction: " + transactionCode);
        request.setType(NotificationType.PAYMENT_CONFIRMED);
        request.setRelatedId(rideRequestId);

        createNotification(request);
    }

    // ===== QUERIES =====

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getNotificationsPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationEntity> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(NotificationResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.ENVIADA).stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.ENVIADA);
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long id) {
        NotificationEntity notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return NotificationResponseDTO.fromEntity(notification);
    }

    // ===== UPDATE OPERATIONS =====

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        if (!notificationRepository.existsByUserIdAndId(userId, notificationId)) {
            throw new RuntimeException("Notification not found for this user");
        }

        int updated = notificationRepository.updateNotificationStatus(
                notificationId, userId, NotificationStatus.LEIDA, LocalDateTime.now());

        if (updated == 0) {
            throw new RuntimeException("Could not mark notification as read");
        }
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        if (!notificationRepository.existsByUserIdAndId(userId, notificationId)) {
            throw new RuntimeException("Notification not found for this user");
        }

        int deleted = notificationRepository.deleteNotification(notificationId, userId);

        if (deleted == 0) {
            throw new RuntimeException("Could not delete notification");
        }
    }

    @Transactional
    public void deleteAllReadNotifications(Long userId) {
        notificationRepository.deleteByUserIdAndStatus(userId, NotificationStatus.LEIDA);
    }

    // ===== STATISTICS =====

    @Transactional(readOnly = true)
    public long getUnreadCountByUser(Long userId) {
        return notificationRepository.countUnreadByUser(userId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getNotificationStatsByType(Long userId) {
        return notificationRepository.countByTypeForUser(userId);
    }
}