package com.gianmarco.soa.auth.auth.service;

import com.gianmarco.soa.auth.auth.dto.LoginRequestDTO;
import com.gianmarco.soa.auth.auth.dto.LoginResponseDTO;
import com.gianmarco.soa.auth.auth.dto.RegisterRequestDTO;
import com.gianmarco.soa.auth.auth.dto.RegisterResponseDTO;
import com.gianmarco.soa.auth.auth.entity.UsuarioEntity;
import com.gianmarco.soa.auth.auth.repository.UsuarioRepository;
import com.gianmarco.soa.auth.auth.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        // Check if email already exists
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        UsuarioEntity user = new UsuarioEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setRol(request.getRol()); // CLIENT, DRIVER, ADMIN
        user.setActivo(true);

        UsuarioEntity saved = usuarioRepository.save(user);

        return new RegisterResponseDTO(
                saved.getId(),
                saved.getEmail(),
                saved.getNombre(),
                saved.getRol()
        );
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Find user by email
        UsuarioEntity user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Check if user is active
        if (!user.getActivo()) {
            throw new RuntimeException("Account is disabled");
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRol());

        return new LoginResponseDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getRol()
        );
    }

    @Transactional(readOnly = true)
    public UsuarioEntity getUserById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public UsuarioEntity getUserByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void changeUserRole(Long userId, String newRole) {
        UsuarioEntity user = getUserById(userId);
        user.setRol(newRole);
        usuarioRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        UsuarioEntity user = getUserById(userId);
        user.setActivo(false);
        usuarioRepository.save(user);
    }

    @Transactional
    public void activateUser(Long userId) {
        UsuarioEntity user = getUserById(userId);
        user.setActivo(true);
        usuarioRepository.save(user);
    }
}