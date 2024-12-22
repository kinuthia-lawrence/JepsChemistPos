package com.larrykin.jepschemistpos.UTILITIES;

import javafx.scene.control.Alert;

public class CustomAlert {
    public void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
