package com.larrykin.jepschemistpos.MODELS;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email){
        this.email = email;
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public String getEmail(){
        return email;
    }
}
