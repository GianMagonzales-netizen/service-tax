package com.gianmarco.soa.auth.driver.exception;

public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(String message) {
        super(message);
    }

    public DriverNotFoundException(Long id) {
        super("Proveedor no encontrado con ID: " + id);
    }

    public DriverNotFoundException(String campo, String valor) {
        super("Proveedor no encontrado con " + campo + ": " + valor);
    }
}