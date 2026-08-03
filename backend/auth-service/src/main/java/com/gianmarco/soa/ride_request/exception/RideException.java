package com.gianmarco.soa.ride_request.exception;

public class RideException extends RuntimeException {

    public RideException(String message) {
        super(message);
    }

    public RideException(String message, Throwable cause) {
        super(message, cause);
    }
}