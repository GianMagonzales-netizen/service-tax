package com.gianmarco.soa.orchestrator.exception;

public class OrquestadorException extends RuntimeException {

    public OrquestadorException(String message) {
        super(message);
    }

    public OrquestadorException(String message, Throwable cause) {
        super(message, cause);
    }
}