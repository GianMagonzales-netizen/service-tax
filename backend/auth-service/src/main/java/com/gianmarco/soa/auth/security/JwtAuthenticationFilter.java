package com.gianmarco.soa.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Evita ejecutar el filtro JWT en:
     * - Peticiones OPTIONS de CORS
     * - Login
     * - Registro
     * - Cualquier endpoint público de /api/auth/**
     * - Consola H2
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        return HttpMethod.OPTIONS.matches(method)
                || path.startsWith("/api/auth/")
                || path.startsWith("/h2-console/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info(
                "🔍 JWT FILTER EXECUTING for: {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        try {
            String token = extractToken(request);

            /*
             * Si no existe token, la petición continúa.
             * Spring Security decidirá después si la ruta necesita autenticación.
             */
            if (token == null) {
                log.debug("No JWT token found in Authorization header");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug(
                    "JWT token found. Length: {}",
                    token.length()
            );

            boolean isValid = jwtTokenProvider.validateToken(token);

            if (!isValid) {
                log.warn("❌ Invalid or expired JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            /*
             * Evita reemplazar una autenticación que ya exista.
             */
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtTokenProvider.getEmailFromToken(token);

                if (email == null || email.isBlank()) {
                    log.warn("❌ JWT token does not contain a valid email");
                    filterChain.doFilter(request, response);
                    return;
                }

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                log.info("🔓 Authentication established for: {}", email);
            }

        } catch (Exception exception) {
            /*
             * No respondemos directamente con 401 o 403 aquí.
             * Dejamos que Spring Security gestione el acceso.
             */
            SecurityContextHolder.clearContext();

            log.error(
                    "❌ Error processing JWT for {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    exception.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el JWT del header:
     *
     * Authorization: Bearer eyJhbGciOi...
     */
    private String extractToken(HttpServletRequest request) {
        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || authorizationHeader.isBlank()
                || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authorizationHeader.substring(7).trim();

        return token.isEmpty() ? null : token;
    }
}