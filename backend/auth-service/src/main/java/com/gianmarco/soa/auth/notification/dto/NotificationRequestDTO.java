package com.gianmarco.soa.auth.notification.dto;

import com.gianmarco.soa.auth.notification.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotificationRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotNull(message = "Message is required")
    @Size(min = 3, max = 500, message = "Message must be between 3 and 500 characters")
    private String message;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    private Long relatedId;

    // Constructors
    public NotificationRequestDTO() {}

    public NotificationRequestDTO(Long userId, String title, String message, NotificationType type, Long relatedId) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedId = relatedId;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
}