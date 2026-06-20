package com.gianmarco.soa.auth.auth.dto;

public class LoginResponseDTO {

    private String token;
    private Long userId;
    private String email;
    private String nombre;
    private String rol;

    public LoginResponseDTO(String token, Long userId, String email, String nombre, String rol) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getters
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
}