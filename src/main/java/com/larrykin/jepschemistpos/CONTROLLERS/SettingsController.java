package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.UTILITIES.SceneManager;
import com.larrykin.jepschemistpos.UTILITIES.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class SettingsController {

    @FXML
    private ToggleButton themeToggleButton;

    @FXML
    public void initialize() {
        // Toggle the theme when the button is clicked
        themeToggleButton.setOnAction(event -> {
            boolean darkMode = ThemeManager.loadThemeState();
            boolean isDarkMode = !darkMode;
            applyThemeToAllScenes(isDarkMode);
            ThemeManager.saveThemeState(isDarkMode);
            if (darkMode) {
                themeToggleButton.setText("Dark Mode");
            } else {
                themeToggleButton.setText("Light Mode");
            }
        });
    }

    // Apply the theme to all scenes
    private void applyThemeToAllScenes(boolean isDarkMode) {
        Platform.runLater(() -> {
            if (themeToggleButton.getScene() != null) {
                Stage stage = (Stage) themeToggleButton.getScene().getWindow();
                Scene scene = stage.getScene();
                ThemeManager.applyTheme(scene, isDarkMode);

                // Apply theme to other scenes if they are already created
                for (Scene otherScene : SceneManager.getAllScenes()) {
                    ThemeManager.applyTheme(otherScene, isDarkMode);
                }
            }
        });
    }
}