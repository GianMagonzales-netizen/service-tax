package com.gianmarco.soa;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class fiter {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "991813987";

        String hash = encoder.encode(password);

        System.out.println(hash);
    }
}
