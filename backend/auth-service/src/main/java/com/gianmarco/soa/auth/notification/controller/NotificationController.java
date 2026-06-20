package com.gianmarco.soa.auth.notification.controller;

import com.gianmarco.soa.auth.notification.dto.NotificationEventDTO;
import com.gianmarco.soa.auth.notification.dto.NotificationRequestDTO;
import com.gianmarco.soa.auth.notification.dto.NotificationResponseDTO;
import com.gianmarco.soa.auth.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // ===== CREATE NOTIFICATION =====

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(@Valid @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/event")
    public ResponseEntity<Void> notifyEvent(@RequestBody NotificationEventDTO event) {
        notificationService.notifyEvent(event);
        return ResponseEntity.ok().build();
    }

    // ===== SPECIFIC EVENT NOTIFICATIONS =====

    @PostMapping("/ride-created/{clientId}/{rideRequestId}")
    public ResponseEntity<Void> notifyRideCreated(@PathVariable Long clientId, @PathVariable Long rideRequestId) {
        notificationService.notifyRideCreated(clientId, rideRequestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/driver-assigned/{clientId}/{driverId}/{rideRequestId}")
    public ResponseEntity<Void> notifyDriverAssigned(@PathVariable Long clientId,
                                                     @PathVariable Long driverId,
                                                     @PathVariable Long rideRequestId) {
        notificationService.notifyDriverAssigned(clientId, driverId, rideRequestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/driver-response/{clientId}/{rideRequestId}/{accepted}")
    public ResponseEntity<Void> notifyDriverResponse(@PathVariable Long clientId,
                                                     @PathVariable Long rideRequestId,
                                                     @PathVariable boolean accepted) {
        notificationService.notifyDriverResponse(clientId, rideRequestId, accepted);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ride-completed/{clientId}/{driverId}/{rideRequestId}")
    public ResponseEntity<Void> notifyRideCompleted(@PathVariable Long clientId,
                                                    @PathVariable Long driverId,
                                                    @PathVariable Long rideRequestId) {
        notificationService.notifyRideCompleted(clientId, driverId, rideRequestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ride-cancelled/{clientId}/{rideRequestId}")
    public ResponseEntity<Void> notifyRideCancelled(@PathVariable Long clientId,
                                                    @PathVariable Long rideRequestId) {
        notificationService.notifyRideCancelled(clientId, rideRequestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment-confirmed/{clientId}/{rideRequestId}/{transactionCode}")
    public ResponseEntity<Void> notifyPaymentConfirmed(@PathVariable Long clientId,
                                                       @PathVariable Long rideRequestId,
                                                       @PathVariable String transactionCode) {
        notificationService.notifyPaymentConfirmed(clientId, rideRequestId, transactionCode);
        return ResponseEntity.ok().build();
    }

    // ===== QUERIES =====

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<NotificationResponseDTO>> getNotificationsPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getNotificationsPaginated(userId, page, size));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    // ===== UPDATE OPERATIONS =====

    @PutMapping("/user/{userId}/read/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long userId, @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Integer> markAllAsRead(@PathVariable Long userId) {
        int updated = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(updated);
    }

    // ===== DELETE OPERATIONS =====

    @DeleteMapping("/user/{userId}/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long userId, @PathVariable Long notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> deleteAllReadNotifications(@PathVariable Long userId) {
        notificationService.deleteAllReadNotifications(userId);
        return ResponseEntity.noContent().build();
    }

    // ===== STATISTICS =====

    @GetMapping("/stats/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCountByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCountByUser(userId));
    }

    @GetMapping("/stats/user/{userId}/by-type")
    public ResponseEntity<List<Object[]>> getNotificationStatsByType(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationStatsByType(userId));
    }
}