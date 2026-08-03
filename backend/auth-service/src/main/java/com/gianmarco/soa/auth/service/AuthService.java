package com.gianmarco.soa.auth.service;

import com.gianmarco.soa.auth.dto.ForgotPasswordRequestDTO;
import com.gianmarco.soa.auth.dto.LoginRequestDTO;
import com.gianmarco.soa.auth.dto.LoginResponseDTO;
import com.gianmarco.soa.auth.dto.RegisterRequestDTO;
import com.gianmarco.soa.auth.dto.RegisterResponseDTO;
import com.gianmarco.soa.auth.dto.ResendCodeRequestDTO;
import com.gianmarco.soa.auth.dto.ResetPasswordRequestDTO;
import com.gianmarco.soa.auth.dto.VerifyEmailRequestDTO;
import com.gianmarco.soa.auth.entity.UsuarioEntity;
import com.gianmarco.soa.auth.repository.UsuarioRepository;
import com.gianmarco.soa.auth.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import com.gianmarco.soa.audit.service.AuditService;
import com.gianmarco.soa.audit.enums.AuditEventType;
import com.gianmarco.soa.audit.enums.AuditEntityType;
import com.gianmarco.soa.audit.dto.AuditRequestDTO;

@Service
public class AuthService {

    private static final int MAX_VERIFICATION_ATTEMPTS = 5;
    private static final int MAX_RECOVERY_ATTEMPTS = 5;
    private static final int CODE_EXPIRATION_MINUTES = 10;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    private final SecureRandom secureRandom =
            new SecureRandom();

    // =====================================================
    // REGISTRO
    // =====================================================

    @Transactional
    public RegisterResponseDTO register(
            RegisterRequestDTO request
    ) {
        String email =
                normalizeEmail(request.getEmail());

        String rol =
                normalizeRole(request.getRol());

        if (
                !rol.equals("CLIENT")
                        && !rol.equals("DRIVER")
        ) {
            throw new RuntimeException(
                    "El registro público solo permite CLIENT o DRIVER"
            );
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException(
                    "El correo ya se encuentra registrado"
            );
        }

        if (
                request.getPassword() == null
                        || request.getPassword().length() < 8
        ) {
            throw new RuntimeException(
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }

        if (
                request.getNombre() == null
                        || request.getNombre().trim().isEmpty()
        ) {
            throw new RuntimeException(
                    "El nombre es obligatorio"
            );
        }

        String verificationCode =
                generateVerificationCode();

        UsuarioEntity user =
                new UsuarioEntity();

        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setNombre(
                request.getNombre().trim()
        );

        user.setApellido(
                normalizeOptionalText(
                        request.getApellido()
                )
        );

        user.setTelefono(
                normalizeOptionalText(
                        request.getTelefono()
                )
        );

        user.setRol(rol);
        user.setActivo(true);

        user.setEmailVerificado(false);

        user.setCodigoVerificacion(
                verificationCode
        );

        user.setExpiracionCodigo(
                LocalDateTime.now()
                        .plusMinutes(
                                CODE_EXPIRATION_MINUTES
                        )
        );

        user.setIntentosVerificacion(0);

        UsuarioEntity saved =
                usuarioRepository.save(user);

        try {
            emailService.sendVerificationCode(
                    saved.getEmail(),
                    getRecipientName(saved),
                    verificationCode
            );
        } catch (Exception exception) {
            throw new RuntimeException(
                    "No se pudo enviar el código de verificación",
                    exception
            );
        }

        return new RegisterResponseDTO(
                saved.getId(),
                saved.getEmail(),
                saved.getNombre(),
                saved.getRol()
        );
    }

    // =====================================================
    // VERIFICAR CORREO
    // =====================================================

    @Transactional(noRollbackFor = RuntimeException.class)
    public void verifyEmail(
            VerifyEmailRequestDTO request
    ) {
        String email =
                normalizeEmail(request.getEmail());

        String codigo =
                normalizeCode(request.getCodigo());

        UsuarioEntity user =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        if (
                Boolean.TRUE.equals(
                        user.getEmailVerificado()
                )
        ) {
            throw new RuntimeException(
                    "El correo ya fue verificado"
            );
        }

        int intentos =
                user.getIntentosVerificacion() == null
                        ? 0
                        : user.getIntentosVerificacion();

        if (
                intentos
                        >= MAX_VERIFICATION_ATTEMPTS
        ) {
            throw new RuntimeException(
                    "Se superó el máximo de intentos. Solicita un nuevo código"
            );
        }

        if (
                user.getCodigoVerificacion() == null
                        || user.getExpiracionCodigo() == null
        ) {
            throw new RuntimeException(
                    "No existe un código de verificación activo"
            );
        }

        if (
                LocalDateTime.now().isAfter(
                        user.getExpiracionCodigo()
                )
        ) {
            throw new RuntimeException(
                    "El código de verificación ha expirado"
            );
        }

        if (
                !user.getCodigoVerificacion()
                        .equals(codigo)
        ) {
            int nuevosIntentos =
                    intentos + 1;

            user.setIntentosVerificacion(
                    nuevosIntentos
            );

            usuarioRepository.save(user);

            int intentosRestantes =
                    MAX_VERIFICATION_ATTEMPTS
                            - nuevosIntentos;

            throw new RuntimeException(
                    "Código incorrecto. Intentos restantes: "
                            + intentosRestantes
            );
        }

        user.setEmailVerificado(true);
        user.setCodigoVerificacion(null);
        user.setExpiracionCodigo(null);
        user.setIntentosVerificacion(0);

        usuarioRepository.save(user);
    }

    // =====================================================
    // REENVIAR CÓDIGO DE VERIFICACIÓN
    // =====================================================

    @Transactional
    public void resendVerificationCode(
            ResendCodeRequestDTO request
    ) {
        String email =
                normalizeEmail(request.getEmail());

        UsuarioEntity user =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        if (
                Boolean.TRUE.equals(
                        user.getEmailVerificado()
                )
        ) {
            throw new RuntimeException(
                    "El correo ya fue verificado"
            );
        }

        String newCode =
                generateVerificationCode();

        user.setCodigoVerificacion(
                newCode
        );

        user.setExpiracionCodigo(
                LocalDateTime.now()
                        .plusMinutes(
                                CODE_EXPIRATION_MINUTES
                        )
        );

        user.setIntentosVerificacion(0);

        usuarioRepository.save(user);

        try {
            emailService.sendVerificationCode(
                    user.getEmail(),
                    getRecipientName(user),
                    newCode
            );
        } catch (Exception exception) {
            throw new RuntimeException(
                    "No se pudo reenviar el código de verificación",
                    exception
            );
        }
    }

    // =====================================================
    // SOLICITAR RECUPERACIÓN DE CONTRASEÑA
    // =====================================================

    @Transactional
    public void forgotPassword(
            ForgotPasswordRequestDTO request
    ) {
        String email =
                normalizeEmail(request.getEmail());

        UsuarioEntity user =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "No existe una cuenta registrada con ese correo"
                                )
                        );

        if (
                !Boolean.TRUE.equals(
                        user.getActivo()
                )
        ) {
            throw new RuntimeException(
                    "La cuenta está deshabilitada"
            );
        }

        String recoveryCode =
                generateVerificationCode();

        user.setCodigoRecuperacion(
                recoveryCode
        );

        user.setExpiracionCodigoRecuperacion(
                LocalDateTime.now()
                        .plusMinutes(
                                CODE_EXPIRATION_MINUTES
                        )
        );

        user.setIntentosRecuperacion(0);

        usuarioRepository.save(user);

        try {
            emailService.sendPasswordResetCode(
                    user.getEmail(),
                    getRecipientName(user),
                    recoveryCode
            );
        } catch (Exception exception) {
            throw new RuntimeException(
                    "No se pudo enviar el código de recuperación",
                    exception
            );
        }
    }

    // =====================================================
    // RESTABLECER CONTRASEÑA
    // =====================================================

    @Transactional(noRollbackFor = RuntimeException.class)
    public void resetPassword(
            ResetPasswordRequestDTO request
    ) {
        String email =
                normalizeEmail(request.getEmail());

        String codigo =
                normalizeCode(request.getCodigo());

        String nuevaPassword =
                request.getNuevaPassword();

        if (
                nuevaPassword == null
                        || nuevaPassword.length() < 8
        ) {
            throw new RuntimeException(
                    "La nueva contraseña debe tener al menos 8 caracteres"
            );
        }

        UsuarioEntity user =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        if (
                !Boolean.TRUE.equals(
                        user.getActivo()
                )
        ) {
            throw new RuntimeException(
                    "La cuenta está deshabilitada"
            );
        }

        int intentos =
                user.getIntentosRecuperacion() == null
                        ? 0
                        : user.getIntentosRecuperacion();

        if (
                intentos
                        >= MAX_RECOVERY_ATTEMPTS
        ) {
            throw new RuntimeException(
                    "Se superó el máximo de intentos. Solicita un nuevo código"
            );
        }

        if (
                user.getCodigoRecuperacion() == null
                        || user.getExpiracionCodigoRecuperacion() == null
        ) {
            throw new RuntimeException(
                    "No existe un código de recuperación activo"
            );
        }

        if (
                LocalDateTime.now().isAfter(
                        user.getExpiracionCodigoRecuperacion()
                )
        ) {
            throw new RuntimeException(
                    "El código de recuperación ha expirado"
            );
        }

        if (
                !user.getCodigoRecuperacion()
                        .equals(codigo)
        ) {
            int nuevosIntentos =
                    intentos + 1;

            user.setIntentosRecuperacion(
                    nuevosIntentos
            );

            usuarioRepository.save(user);

            int intentosRestantes =
                    MAX_RECOVERY_ATTEMPTS
                            - nuevosIntentos;

            throw new RuntimeException(
                    "Código incorrecto. Intentos restantes: "
                            + intentosRestantes
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        nuevaPassword
                )
        );

        user.setCodigoRecuperacion(null);

        user.setExpiracionCodigoRecuperacion(
                null
        );

        user.setIntentosRecuperacion(0);

        usuarioRepository.save(user);
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @Transactional
    public LoginResponseDTO login(
            LoginRequestDTO request
    ) {
        String email =
                normalizeEmail(request.getEmail());

        UsuarioEntity user =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Correo o contraseña incorrectos"
                                )
                        );

        if (
                request.getPassword() == null
                        || !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                )
        ) {
            throw new RuntimeException(
                    "Correo o contraseña incorrectos"
            );
        }

        if (
                !Boolean.TRUE.equals(
                        user.getActivo()
                )
        ) {
            throw new RuntimeException(
                    "La cuenta está deshabilitada"
            );
        }

        if (
                !Boolean.TRUE.equals(
                        user.getEmailVerificado()
                )
        ) {
            throw new RuntimeException(
                    "Debes verificar tu correo antes de iniciar sesión"
            );
        }

        String token =
                jwtTokenProvider.generateToken(
                        user.getEmail(),
                        user.getRol()
                );
        AuditRequestDTO audit = new AuditRequestDTO();

        audit.setEventType(AuditEventType.LOGIN);

        audit.setEntityType(AuditEntityType.USER);

        audit.setEntityId(user.getId());

        audit.setUserId(user.getId());

        audit.setUserEmail(user.getEmail());

        audit.setUserRole(user.getRol());

        audit.setDetails("Inicio de sesión exitoso");

        audit.setIpAddress("LOCALHOST");

        auditService.registerEvent(audit);

        return new LoginResponseDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getRol()
        );
    }

    // =====================================================
    // CONSULTAR USUARIO POR ID
    // =====================================================

    @Transactional(readOnly = true)
    public UsuarioEntity getUserById(
            Long id
    ) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Usuario no encontrado"
                        )
                );
    }

    // =====================================================
    // CONSULTAR USUARIO POR EMAIL
    // =====================================================

    @Transactional(readOnly = true)
    public UsuarioEntity getUserByEmail(
            String email
    ) {
        return usuarioRepository
                .findByEmail(
                        normalizeEmail(email)
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Usuario no encontrado"
                        )
                );
    }

    // =====================================================
    // CAMBIAR ROL
    // =====================================================

    @Transactional
    public void changeUserRole(
            Long userId,
            String newRole
    ) {
        String role =
                normalizeRole(newRole);

        if (
                !role.equals("CLIENT")
                        && !role.equals("DRIVER")
                        && !role.equals("ADMIN")
        ) {
            throw new RuntimeException(
                    "Rol inválido"
            );
        }

        UsuarioEntity user =
                getUserById(userId);

        user.setRol(role);

        usuarioRepository.save(user);
    }

    // =====================================================
    // DESACTIVAR USUARIO
    // =====================================================

    @Transactional
    public void deactivateUser(
            Long userId
    ) {
        UsuarioEntity user =
                getUserById(userId);

        user.setActivo(false);

        usuarioRepository.save(user);
    }

    // =====================================================
    // ACTIVAR USUARIO
    // =====================================================

    @Transactional
    public void activateUser(
            Long userId
    ) {
        UsuarioEntity user =
                getUserById(userId);

        user.setActivo(true);

        usuarioRepository.save(user);
    }

    // =====================================================
    // GENERAR CÓDIGO DE SEIS DÍGITOS
    // =====================================================

    private String generateVerificationCode() {
        int code =
                secureRandom.nextInt(900000)
                        + 100000;

        return String.valueOf(code);
    }

    // =====================================================
    // NORMALIZAR CÓDIGO
    // =====================================================

    private String normalizeCode(
            String codigo
    ) {
        if (codigo == null) {
            throw new RuntimeException(
                    "El código es obligatorio"
            );
        }

        String normalized =
                codigo.trim();

        if (!normalized.matches("\\d{6}")) {
            throw new RuntimeException(
                    "El código debe contener exactamente 6 números"
            );
        }

        return normalized;
    }

    // =====================================================
    // NORMALIZAR EMAIL
    // =====================================================

    private String normalizeEmail(
            String email
    ) {
        if (
                email == null
                        || email.trim().isEmpty()
        ) {
            throw new RuntimeException(
                    "El correo es obligatorio"
            );
        }

        return email
                .trim()
                .toLowerCase();
    }

    // =====================================================
    // NORMALIZAR ROL
    // =====================================================

    private String normalizeRole(
            String role
    ) {
        if (
                role == null
                        || role.trim().isEmpty()
        ) {
            throw new RuntimeException(
                    "El rol es obligatorio"
            );
        }

        return role
                .trim()
                .toUpperCase();
    }

    // =====================================================
    // NORMALIZAR TEXTO OPCIONAL
    // =====================================================

    private String normalizeOptionalText(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    // =====================================================
    // NOMBRE PARA EL CORREO
    // =====================================================

    private String getRecipientName(
            UsuarioEntity user
    ) {
        if (
                user.getNombre() == null
                        || user.getNombre().trim().isEmpty()
        ) {
            return "usuario";
        }

        return user.getNombre().trim();
    }
}