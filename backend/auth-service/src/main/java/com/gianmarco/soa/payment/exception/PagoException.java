package com.gianmarco.soa.payment.exception;

public class PagoException extends RuntimeException {

    public PagoException(String message) {
        super(message);
    }

    public PagoException(String message, Throwable cause) {
        super(message, cause);
    }
}