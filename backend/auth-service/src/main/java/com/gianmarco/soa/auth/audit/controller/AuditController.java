package com.gianmarco.soa.auth.audit.controller;

import com.gianmarco.soa.auth.audit.dto.AuditFilterDTO;
import com.gianmarco.soa.auth.audit.dto.AuditRequestDTO;
import com.gianmarco.soa.auth.audit.dto.AuditResponseDTO;
import com.gianmarco.soa.auth.audit.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/event")
    public ResponseEntity<Void> registerEvent(@Valid @RequestBody AuditRequestDTO request) {
        auditService.registerEvent(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditResponseDTO>> getHistoryByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getHistoryByEntity(entityType, entityId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditResponseDTO>> getHistoryByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getHistoryByUser(userId));
    }

    @GetMapping("/dates")
    public ResponseEntity<List<AuditResponseDTO>> getHistoryByDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getHistoryByDates(start, end));
    }

    @PostMapping("/filters")
    public ResponseEntity<List<AuditResponseDTO>> getHistoryWithFilters(@RequestBody AuditFilterDTO filters) {
        return ResponseEntity.ok(auditService.getHistoryWithFilters(filters));
    }

    @GetMapping("/history/{entityType}/{entityId}/full")
    public ResponseEntity<List<AuditResponseDTO>> getFullHistory(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getFullHistory(entityType, entityId));
    }

    @GetMapping("/stats/event/{eventType}/count")
    public ResponseEntity<Long> countEventsByType(@PathVariable String eventType) {
        return ResponseEntity.ok(auditService.countEventsByType(eventType));
    }

    @GetMapping("/stats/summary/{since}")
    public ResponseEntity<List<Object[]>> getEventSummarySince(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return ResponseEntity.ok(auditService.getEventSummarySince(since));
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupOldAudit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime limitDate) {
        auditService.cleanupOldAudit(limitDate);
        return ResponseEntity.ok().build();
    }
}