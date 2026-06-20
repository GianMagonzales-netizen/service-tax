package com.gianmarco.soa.auth.audit.dto;

import com.gianmarco.soa.auth.audit.enums.AuditEventType;
import com.gianmarco.soa.auth.audit.enums.AuditEntityType;
import java.time.LocalDateTime;

public class AuditFilterDTO {

    private AuditEventType eventType;
    private AuditEntityType entityType;
    private Long entityId;
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String ipAddress;

    // Getters and Setters
    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }

    public AuditEntityType getEntityType() { return entityType; }
    public void setEntityType(AuditEntityType entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}