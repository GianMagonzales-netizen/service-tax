package com.gianmarco.soa.auth.audit.enums;

public enum AuditEventType {
    CREATE,
    UPDATE,
    DELETE,
    ASSIGN,
    REASSIGN,
    ACCEPT,
    REJECT,
    COMPLETE,
    CANCEL,
    PAYMENT_PROCESSED,
    PAYMENT_REFUNDED,
    LOGIN,
    LOGOUT,
    REGISTER
}