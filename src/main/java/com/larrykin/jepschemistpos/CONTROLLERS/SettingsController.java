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
        Platform.runLater(() -> {
            // Ensure the scene is fully initialized before accessing it
            if (themeToggleButton.getScene() != null) {
                // Register the scene
                Stage stage = (Stage) themeToggleButton.getScene().getWindow();
                Scene scene = stage.getScene();
                SceneManager.addScene(scene);

                // Apply the theme to the current scene
                boolean isDarkModeTrue = ThemeManager.loadThemeState();
                ThemeManager.applyTheme(scene, isDarkModeTrue);
            }
        });

        // Load the current theme state
        boolean isDarkMode = ThemeManager.loadThemeState();
        themeToggleButton.setSelected(isDarkMode);
        applyThemeToAllScenes(isDarkMode);

        // Toggle the theme when the button is clicked
        themeToggleButton.setOnAction(event -> {
            boolean darkMode = themeToggleButton.isSelected();
            applyThemeToAllScenes(darkMode);
            ThemeManager.saveThemeState(darkMode);
            System.out.println("toggle button clicked, dark mode is " + darkMode);
        });
    }

    // Apply the theme to all scenes
    private void applyThemeToAllScenes(boolean isDarkMode) {
        Platform.runLater(() -> {
            if (themeToggleButton.getScene() != null) {
                Stage stage = (Stage) themeToggleButton.getScene().getWindow();
                Scene scene = stage.getScene();
                ThemeManager.applyTheme(scene, isDarkMode);
                System.out.println("apply theme to all scenes, dark mode is " + isDarkMode);

                // Apply theme to other scenes if they are already created
                for (Scene otherScene : SceneManager.getAllScenes()) {
                    ThemeManager.applyTheme(otherScene, isDarkMode);
                }
            }
        });
    }
}