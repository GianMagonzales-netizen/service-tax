package com.gianmarco.soa.auth.controller;

import com.gianmarco.soa.auth.dto.ForgotPasswordRequestDTO;
import com.gianmarco.soa.auth.dto.LoginRequestDTO;
import com.gianmarco.soa.auth.dto.LoginResponseDTO;
import com.gianmarco.soa.auth.dto.MessageResponseDTO;
import com.gianmarco.soa.auth.dto.RegisterRequestDTO;
import com.gianmarco.soa.auth.dto.RegisterResponseDTO;
import com.gianmarco.soa.auth.dto.ResendCodeRequestDTO;
import com.gianmarco.soa.auth.dto.ResetPasswordRequestDTO;
import com.gianmarco.soa.auth.dto.VerifyEmailRequestDTO;
import com.gianmarco.soa.auth.entity.UsuarioEntity;
import com.gianmarco.soa.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // =====================================================
    // REGISTRO
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        RegisterResponseDTO response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // VERIFICAR CORREO
    // =====================================================

    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponseDTO> verifyEmail(
            @Valid @RequestBody VerifyEmailRequestDTO request
    ) {
        authService.verifyEmail(request);

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Correo verificado correctamente"
                )
        );
    }

    // =====================================================
    // REENVIAR CÓDIGO DE VERIFICACIÓN
    // =====================================================

    @PostMapping("/resend-code")
    public ResponseEntity<MessageResponseDTO> resendCode(
            @Valid @RequestBody ResendCodeRequestDTO request
    ) {
        authService.resendVerificationCode(request);

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Código de verificación reenviado correctamente"
                )
        );
    }

    // =====================================================
    // SOLICITAR RECUPERACIÓN DE CONTRASEÑA
    // =====================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request
    ) {
        authService.forgotPassword(request);

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Código de recuperación enviado correctamente"
                )
        );
    }

    // =====================================================
    // RESTABLECER CONTRASEÑA
    // =====================================================

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request
    ) {
        authService.resetPassword(request);

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Contraseña actualizada correctamente"
                )
        );
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        LoginResponseDTO response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // CONSULTAR USUARIO POR ID
    // =====================================================

    @GetMapping("/user/{id}")
    public ResponseEntity<UsuarioEntity> getUserById(
            @PathVariable Long id
    ) {
        UsuarioEntity user =
                authService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    // =====================================================
    // CONSULTAR USUARIO POR EMAIL
    // =====================================================

    @GetMapping("/user/email/{email}")
    public ResponseEntity<UsuarioEntity> getUserByEmail(
            @PathVariable String email
    ) {
        UsuarioEntity user =
                authService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    // =====================================================
    // CAMBIAR ROL
    // =====================================================

    @PutMapping("/user/{id}/role")
    public ResponseEntity<MessageResponseDTO> changeUserRole(
            @PathVariable Long id,
            @RequestParam String role
    ) {
        authService.changeUserRole(id, role);

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Rol actualizado correctamente"
                )
        );
    }

    // =====================================================
    // DESACTIVAR USUARIO
    // =====================================================

    @PostMapping("/user/{id}/deactivate")
    public ResponseEntity<MessageResponseDTO> deactivateUser(
            @PathVariable Long id
    ) {
        authService.deactivateUser(id);

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Usuario desactivado correctamente"
                )
        );
    }

    // =====================================================
    // ACTIVAR USUARIO
    // =====================================================

    @PostMapping("/user/{id}/activate")
    public ResponseEntity<MessageResponseDTO> activateUser(
            @PathVariable Long id
    ) {
        authService.activateUser(id);

        return ResponseEntity.ok(
                new MessageResponseDTO(
                        "Usuario activado correctamente"
                )
        );
    }
}