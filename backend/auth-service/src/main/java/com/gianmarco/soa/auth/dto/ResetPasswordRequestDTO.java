package com.gianmarco.soa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequestDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    private String email;

    @NotBlank(message = "El código es obligatorio")
    @Pattern(
            regexp = "\\d{6}",
            message = "El código debe contener exactamente 6 números"
    )
    private String codigo;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(
            min = 8,
            message = "La contraseña debe tener al menos 8 caracteres"
    )
    private String nuevaPassword;

    public ResetPasswordRequestDTO() {
    }

    public ResetPasswordRequestDTO(
            String email,
            String codigo,
            String nuevaPassword
    ) {
        this.email = email;
        this.codigo = codigo;
        this.nuevaPassword = nuevaPassword;
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

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}