package com.gianmarco.soa.auth.audit.dto;

import com.gianmarco.soa.auth.audit.entity.AuditEntity;
import com.gianmarco.soa.auth.audit.enums.AuditEventType;
import com.gianmarco.soa.auth.audit.enums.AuditEntityType;
import java.time.LocalDateTime;

public class AuditResponseDTO {

    private Long id;
    private AuditEventType eventType;
    private AuditEntityType entityType;
    private Long entityId;
    private Long userId;
    private String userEmail;
    private String userRole;
    private String oldValue;
    private String newValue;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;

    // Constructors
    public AuditResponseDTO() {}

    public AuditResponseDTO(Long id, AuditEventType eventType, AuditEntityType entityType,
                            Long entityId, Long userId, String userEmail, String userRole,
                            String oldValue, String newValue, String details,
                            String ipAddress, LocalDateTime timestamp) {
        this.id = id;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.details = details;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    // Factory method from Entity
    public static AuditResponseDTO fromEntity(AuditEntity entity) {
        return new AuditResponseDTO(
                entity.getId(),
                entity.getEventType(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getUserId(),
                entity.getUserEmail(),
                entity.getUserRole(),
                entity.getOldValue(),
                entity.getNewValue(),
                entity.getDetails(),
                entity.getIpAddress(),
                entity.getTimestamp()
        );
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }

    public AuditEntityType getEntityType() { return entityType; }
    public void setEntityType(AuditEntityType entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}