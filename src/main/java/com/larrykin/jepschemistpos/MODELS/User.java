package com.larrykin.jepschemistpos.MODELS;

import com.larrykin.jepschemistpos.ENUMS.ROLE;

public class User {
    private final String username;
    private final String email;
    private final ROLE role;

    public User(String username, String email, ROLE role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public ROLE getRole() {
        return role;
    }
}
