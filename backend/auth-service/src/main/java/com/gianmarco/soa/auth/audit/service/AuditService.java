package com.gianmarco.soa.auth.audit.service;

import com.gianmarco.soa.auth.audit.dto.AuditFilterDTO;
import com.gianmarco.soa.auth.audit.dto.AuditRequestDTO;
import com.gianmarco.soa.auth.audit.dto.AuditResponseDTO;
import com.gianmarco.soa.auth.audit.entity.AuditEntity;
import com.gianmarco.soa.auth.audit.enums.AuditEventType;
import com.gianmarco.soa.auth.audit.enums.AuditEntityType;
import com.gianmarco.soa.auth.audit.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    @Autowired
    private AuditRepository auditRepository;

    // ===== REGISTER EVENT =====

    @Transactional
    public void registerEvent(AuditRequestDTO request) {
        AuditEntity audit = new AuditEntity();
        audit.setEventType(request.getEventType());
        audit.setEntityType(request.getEntityType());
        audit.setEntityId(request.getEntityId());
        audit.setUserId(request.getUserId());
        audit.setUserEmail(request.getUserEmail());
        audit.setUserRole(request.getUserRole());
        audit.setOldValue(request.getOldValue());
        audit.setNewValue(request.getNewValue());
        audit.setDetails(request.getDetails());
        audit.setIpAddress(request.getIpAddress());

        auditRepository.save(audit);
    }

    // Convenience method for quick event registration
    @Transactional
    public void registerEvent(AuditEventType eventType, AuditEntityType entityType,
                              Long entityId, Long userId, String details, String ipAddress) {
        AuditRequestDTO request = new AuditRequestDTO(eventType, entityType, entityId, userId, details, ipAddress);
        registerEvent(request);
    }

    // ===== QUERIES =====

    @Transactional(readOnly = true)
    public List<AuditResponseDTO> getHistoryByEntity(String entityType, Long entityId) {
        AuditEntityType type = AuditEntityType.valueOf(entityType.toUpperCase());
        return auditRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(type, entityId)
                .stream()
                .map(AuditResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditResponseDTO> getHistoryByUser(Long userId) {
        return auditRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(AuditResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditResponseDTO> getHistoryByDates(LocalDateTime start, LocalDateTime end) {
        return auditRepository.findByTimestampBetweenOrderByTimestampDesc(start, end)
                .stream()
                .map(AuditResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditResponseDTO> getHistoryWithFilters(AuditFilterDTO filters) {
        return auditRepository.findByFilters(
                        filters.getEventType(),
                        filters.getEntityType(),
                        filters.getEntityId(),
                        filters.getUserId(),
                        filters.getIpAddress(),
                        filters.getStartDate(),
                        filters.getEndDate()
                ).stream()
                .map(AuditResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditResponseDTO> getFullHistory(String entityType, Long entityId) {
        return getHistoryByEntity(entityType, entityId);
    }

    // ===== STATISTICS =====

    @Transactional(readOnly = true)
    public long countEventsByType(String eventType) {
        AuditEventType type = AuditEventType.valueOf(eventType.toUpperCase());
        return auditRepository.countByEventType(type);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getEventSummarySince(LocalDateTime since) {
        return auditRepository.countEventsByTypeSince(since);
    }

    // ===== MAINTENANCE =====

    @Transactional
    public void cleanupOldAudit(LocalDateTime limitDate) {
        int deleted = auditRepository.deleteOldAuditLogs(limitDate);
        System.out.println("[AUDIT] Deleted " + deleted + " old audit logs before " + limitDate);
    }
}