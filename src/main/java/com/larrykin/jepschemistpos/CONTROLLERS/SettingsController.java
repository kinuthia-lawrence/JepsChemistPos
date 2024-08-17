package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.UTILITIES.SceneManager;
import com.larrykin.jepschemistpos.UTILITIES.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.List;

public class SettingsController {

    @FXML
    private ToggleButton themeToggleButton;


    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            //? registering the scene
            Stage stage = (Stage) themeToggleButton.getScene().getWindow();
            Scene scene = stage.getScene();
            SceneManager.addScene(scene);
            //? Apply the theme to the current scene
            boolean isDarkModeTrue = ThemeManager.loadThemeState();
            ThemeManager.applyTheme(scene, isDarkModeTrue);
        });



        //? Load the current theme state
        boolean isDarkMode = ThemeManager.loadThemeState();
        themeToggleButton.setSelected(isDarkMode);
        applyThemeToAllScenes(isDarkMode);

        //? Toggle the theme when the button is clicked
        themeToggleButton.setOnAction(event -> {
            boolean darkMode = themeToggleButton.isSelected();
            applyThemeToAllScenes(darkMode);
            ThemeManager.saveThemeState(darkMode);
        });
    }

    //? Apply the theme to all scenes
    private void applyThemeToAllScenes(boolean isDarkMode) {
        Platform.runLater(() -> {
        Stage stage = (Stage) themeToggleButton.getScene().getWindow();
        Scene scene = stage.getScene();
        ThemeManager.applyTheme(scene, isDarkMode);

        //? Apply theme to other scenes if they are already created
        for (Scene otherScene : SceneManager.getAllScenes()) {
            ThemeManager.applyTheme(otherScene, isDarkMode);
        }

        });
    }


}
