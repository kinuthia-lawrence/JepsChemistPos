package com.larrykin.jepschemistpos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {

        String password = "password";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        log.info("Hashed password: {}", hashedPassword);
        System.out.println(
                passwordEncoder.matches(password, hashedPassword) ? "Password matched" : "Password not matched"
        );
    }
}