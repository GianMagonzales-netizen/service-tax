package com.gianmarco.soa.auth.notification.dto;

import com.gianmarco.soa.auth.notification.enums.NotificationType;

public class NotificationEventDTO {

    private Long userId;
    private String eventType;
    private String message;
    private Long relatedId;

    // Constructors
    public NotificationEventDTO() {}

    public NotificationEventDTO(Long userId, String eventType, String message, Long relatedId) {
        this.userId = userId;
        this.eventType = eventType;
        this.message = message;
        this.relatedId = relatedId;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
}