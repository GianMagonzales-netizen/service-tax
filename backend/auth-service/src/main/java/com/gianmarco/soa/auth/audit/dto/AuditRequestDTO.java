package com.gianmarco.soa.auth.audit.dto;

import com.gianmarco.soa.auth.audit.enums.AuditEventType;
import com.gianmarco.soa.auth.audit.enums.AuditEntityType;
import jakarta.validation.constraints.NotNull;

public class AuditRequestDTO {

    @NotNull(message = "Event type is required")
    private AuditEventType eventType;

    @NotNull(message = "Entity type is required")
    private AuditEntityType entityType;

    @NotNull(message = "Entity ID is required")
    private Long entityId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String userEmail;
    private String userRole;
    private String oldValue;
    private String newValue;
    private String details;
    private String ipAddress;

    // Constructors
    public AuditRequestDTO() {}

    public AuditRequestDTO(AuditEventType eventType, AuditEntityType entityType,
                           Long entityId, Long userId, String details, String ipAddress) {
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
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
}
