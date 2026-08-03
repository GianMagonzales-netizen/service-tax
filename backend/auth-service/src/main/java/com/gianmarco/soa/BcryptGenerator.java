package com.gianmarco.soa;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "929631693";

        String hash = encoder.encode(password);

        System.out.println("Contraseña: " + password);
        System.out.println("Hash:");
        System.out.println(hash);

        System.out.println("Coincide: " +
                encoder.matches(password, hash));
    }
}
