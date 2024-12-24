package com.larrykin.jepschemistpos.MODELS;

public class Todo {
    private String title;
    private String description;

    //constructor
    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
    }
    public Todo() {
        this.title = "";
        this.description = "";
    }
    //getters
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    //setters
    public String setTitle(String title) {
        return this.title = title;
    }
    public String setDescription(String description) {
        return this.description = description;
    }
}
