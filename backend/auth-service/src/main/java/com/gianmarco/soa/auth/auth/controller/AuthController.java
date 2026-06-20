package com.gianmarco.soa.auth.auth.controller;

import com.gianmarco.soa.auth.auth.dto.LoginRequestDTO;
import com.gianmarco.soa.auth.auth.dto.LoginResponseDTO;
import com.gianmarco.soa.auth.auth.dto.RegisterRequestDTO;
import com.gianmarco.soa.auth.auth.dto.RegisterResponseDTO;
import com.gianmarco.soa.auth.auth.entity.UsuarioEntity;
import com.gianmarco.soa.auth.auth.service.AuthService;
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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UsuarioEntity> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<UsuarioEntity> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(authService.getUserByEmail(email));
    }

    @PutMapping("/user/{id}/role")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long id, @RequestParam String role) {
        authService.changeUserRole(id, role);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        authService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        authService.activateUser(id);
        return ResponseEntity.ok().build();
    }
}