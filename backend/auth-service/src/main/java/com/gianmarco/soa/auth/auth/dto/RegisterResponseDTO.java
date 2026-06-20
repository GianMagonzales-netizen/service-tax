package com.gianmarco.soa.auth.auth.dto;

public class RegisterResponseDTO {

    private Long id;
    private String email;
    private String nombre;
    private String rol;

    public RegisterResponseDTO(Long id, String email, String nombre, String rol) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
}