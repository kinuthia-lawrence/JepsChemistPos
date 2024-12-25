package com.larrykin.jepschemistpos.UTILITIES;

import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ThemeManager {
    private static final String LIGHT_MODE = "/STYLES/light-mode.css";
    private static final String DARK_MODE = "/STYLES/dark-mode.css";


    private static final DatabaseConn databaseConn = new DatabaseConn();
    private static final Logger log = LoggerFactory.getLogger(ThemeManager.class);

    // Apply the theme to the scene
    public static void applyTheme(Scene scene, boolean isDarkMode) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(DARK_MODE)).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(LIGHT_MODE)).toExternalForm());
        }
    }


    public static void saveThemeState(boolean isDarkMode) {
        String updateSQL = "UPDATE utils SET dark_theme = ?";
        try {
            Connection connection = databaseConn.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setBoolean(1, isDarkMode);
            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            log.error("Error saving theme state{}", e.getMessage());
            e.printStackTrace();
        }


    }

    public static boolean loadThemeState() {
        String querySQL = "SELECT dark_theme FROM utils";
        try (Connection connection = databaseConn.getConnection();
             PreparedStatement statement = connection.prepareStatement(querySQL);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getBoolean("dark_theme");
            }
        } catch (SQLException e) {
            log.error("Error loading theme state{}", e.getMessage());
            e.printStackTrace();
        }
        return false; // Default to light mode if there's an error
    }
}
