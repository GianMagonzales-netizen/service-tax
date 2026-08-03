package com.gianmarco.soa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyEmailRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Verification code is required")
    @Pattern(
            regexp = "\\d{6}",
            message = "Verification code must contain exactly 6 digits"
    )
    private String codigo;

    public VerifyEmailRequestDTO() {
    }

    public VerifyEmailRequestDTO(String email, String codigo) {
        this.email = email;
        this.codigo = codigo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}