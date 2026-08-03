package com.gianmarco.soa.notification.exception;

public class NotificacionException extends RuntimeException {

    public NotificacionException(String message) {
        super(message);
    }

    public NotificacionException(String message, Throwable cause) {
        super(message, cause);
    }
}