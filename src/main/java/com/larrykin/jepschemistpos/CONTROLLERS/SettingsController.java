package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.UTILITIES.SceneManager;
import com.larrykin.jepschemistpos.UTILITIES.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class SettingsController {

    @FXML
    private ImageView iconTheme;

    @FXML
    private ToggleButton themeToggleButton;

    @FXML
    public void initialize() {

        setIcon();
        // Toggle the theme when the button is clicked
        themeToggleButton.setOnAction(event -> {
            boolean theme = ThemeManager.loadThemeState();
            boolean isDarkMode = !theme;
            applyThemeToAllScenes(isDarkMode);
            ThemeManager.saveThemeState(isDarkMode);
            setIcon();
        });
    }

    private void setIcon() {
        boolean darkMode = ThemeManager.loadThemeState();
        if (!darkMode) {
            themeToggleButton.setText("Dark Mode");
            Image dark_theme = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/dark_theme.png")));
            iconTheme.setImage(dark_theme);
        } else {
            themeToggleButton.setText("Light Mode");
            Image light_theme = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/light_theme.png")));
            iconTheme.setImage(light_theme);
        }
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