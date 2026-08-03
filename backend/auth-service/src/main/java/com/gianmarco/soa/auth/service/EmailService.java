package com.gianmarco.soa.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // =====================================================
    // VERIFICACIÓN DE CORREO
    // =====================================================

    public void sendVerificationCode(
            String recipientEmail,
            String recipientName,
            String verificationCode
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(recipientEmail);

        message.setSubject(
                "Código de verificación - TAXI"
        );

        message.setText(
                "Hola " + recipientName + ",\n\n"
                        + "Gracias por registrarte en TAXI.\n\n"
                        + "Tu código de verificación es:\n\n"
                        + verificationCode + "\n\n"
                        + "Este código vencerá en 10 minutos.\n\n"
                        + "Si tú no solicitaste este registro, "
                        + "puedes ignorar este correo.\n\n"
                        + "Equipo TAXI"
        );

        mailSender.send(message);
    }

    // =====================================================
    // RECUPERACIÓN DE CONTRASEÑA
    // =====================================================

    public void sendPasswordResetCode(
            String recipientEmail,
            String recipientName,
            String resetCode
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(recipientEmail);

        message.setSubject(
                "Recuperación de contraseña - TAXI"
        );

        message.setText(
                "Hola " + recipientName + ",\n\n"
                        + "Recibimos una solicitud para recuperar "
                        + "la contraseña de tu cuenta.\n\n"
                        + "Tu código de recuperación es:\n\n"
                        + resetCode + "\n\n"
                        + "Este código vencerá en 10 minutos.\n\n"
                        + "Si tú no solicitaste este cambio, "
                        + "puedes ignorar este correo.\n\n"
                        + "Equipo TAXI"
        );

        mailSender.send(message);
    }
}