package com.gianmarco.soa.auth.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "email",
            unique = true,
            nullable = false
    )
    private String email;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono")
    private String telefono;

    @Column(
            name = "rol",
            nullable = false
    )
    private String rol;

    @Column(
            name = "activo",
            nullable = false
    )
    private Boolean activo = true;

    // =====================================================
    // VERIFICACIÓN DE CORREO
    // =====================================================

    @Column(
            name = "email_verificado",
            nullable = false
    )
    private Boolean emailVerificado = false;

    @Column(
            name = "codigo_verificacion",
            length = 6
    )
    private String codigoVerificacion;

    @Column(name = "expiracion_codigo")
    private LocalDateTime expiracionCodigo;

    @Column(
            name = "intentos_verificacion",
            nullable = false
    )
    private Integer intentosVerificacion = 0;

    // =====================================================
    // RECUPERACIÓN DE CONTRASEÑA
    // =====================================================

    @Column(
            name = "codigo_recuperacion",
            length = 6
    )
    private String codigoRecuperacion;

    @Column(name = "expiracion_codigo_recuperacion")
    private LocalDateTime expiracionCodigoRecuperacion;

    @Column(
            name = "intentos_recuperacion",
            nullable = false
    )
    private Integer intentosRecuperacion = 0;

    // =====================================================
    // FECHA DE REGISTRO
    // =====================================================

    @Column(
            name = "fecha_registro",
            nullable = false,
            updatable = false
    )
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }

        if (activo == null) {
            activo = true;
        }

        if (emailVerificado == null) {
            emailVerificado = false;
        }

        if (intentosVerificacion == null) {
            intentosVerificacion = 0;
        }

        if (intentosRecuperacion == null) {
            intentosRecuperacion = 0;
        }
    }

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(
            String nombre
    ) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(
            String apellido
    ) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(
            String telefono
    ) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(
            String rol
    ) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(
            Boolean activo
    ) {
        this.activo = activo;
    }

    public Boolean getEmailVerificado() {
        return emailVerificado;
    }

    public void setEmailVerificado(
            Boolean emailVerificado
    ) {
        this.emailVerificado = emailVerificado;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(
            String codigoVerificacion
    ) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public LocalDateTime getExpiracionCodigo() {
        return expiracionCodigo;
    }

    public void setExpiracionCodigo(
            LocalDateTime expiracionCodigo
    ) {
        this.expiracionCodigo = expiracionCodigo;
    }

    public Integer getIntentosVerificacion() {
        return intentosVerificacion;
    }

    public void setIntentosVerificacion(
            Integer intentosVerificacion
    ) {
        this.intentosVerificacion = intentosVerificacion;
    }

    public String getCodigoRecuperacion() {
        return codigoRecuperacion;
    }

    public void setCodigoRecuperacion(
            String codigoRecuperacion
    ) {
        this.codigoRecuperacion = codigoRecuperacion;
    }

    public LocalDateTime getExpiracionCodigoRecuperacion() {
        return expiracionCodigoRecuperacion;
    }

    public void setExpiracionCodigoRecuperacion(
            LocalDateTime expiracionCodigoRecuperacion
    ) {
        this.expiracionCodigoRecuperacion =
                expiracionCodigoRecuperacion;
    }

    public Integer getIntentosRecuperacion() {
        return intentosRecuperacion;
    }

    public void setIntentosRecuperacion(
            Integer intentosRecuperacion
    ) {
        this.intentosRecuperacion = intentosRecuperacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(
            LocalDateTime fechaRegistro
    ) {
        this.fechaRegistro = fechaRegistro;
    }
}