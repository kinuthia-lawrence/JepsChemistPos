module com.larrykin.jepschemistpos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires flexmark;
    requires flexmark.util.ast;
    requires jakarta.mail;
    requires java.desktop;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
    requires spring.security.crypto;
    requires kernel;
    requires layout;


    opens com.larrykin.jepschemistpos to javafx.fxml;
    exports com.larrykin.jepschemistpos;
    exports com.larrykin.jepschemistpos.CONTROLLERS;
    opens com.larrykin.jepschemistpos.CONTROLLERS to javafx.fxml;
    opens com.larrykin.jepschemistpos.MODELS to javafx.base;
}