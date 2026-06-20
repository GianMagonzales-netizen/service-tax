package com.gianmarco.soa.auth.notification.dto;

import com.gianmarco.soa.auth.notification.entity.NotificationEntity;
import com.gianmarco.soa.auth.notification.enums.NotificationType;
import com.gianmarco.soa.auth.notification.enums.NotificationStatus;
import java.time.LocalDateTime;

public class NotificationResponseDTO {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private Long relatedId;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    // Constructors
    public NotificationResponseDTO() {}

    public NotificationResponseDTO(Long id, Long userId, String title, String message,
                                   NotificationType type, NotificationStatus status,
                                   Long relatedId, LocalDateTime readAt, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = status;
        this.relatedId = relatedId;
        this.readAt = readAt;
        this.createdAt = createdAt;
    }

    // Factory method from Entity
    public static NotificationResponseDTO fromEntity(NotificationEntity entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setRelatedId(entity.getRelatedId());
        dto.setReadAt(entity.getReadAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}