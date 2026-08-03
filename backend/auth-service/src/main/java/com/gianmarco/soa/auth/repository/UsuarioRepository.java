package com.gianmarco.soa.auth.repository;

import com.gianmarco.soa.auth.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    // ==========================
    // CONSULTAS POR EMAIL
    // ==========================
    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    // ==========================
    // CONSULTAS PARA VERIFICACIÓN
    // ==========================
    Optional<UsuarioEntity> findByCodigoVerificacion(String codigoVerificacion);

    boolean existsByCodigoVerificacion(String codigoVerificacion);

    // ==========================
    // CONSULTAS COMBINADAS
    // ==========================
    Optional<UsuarioEntity> findByEmailAndCodigoVerificacion(
            String email,
            String codigoVerificacion
    );
}