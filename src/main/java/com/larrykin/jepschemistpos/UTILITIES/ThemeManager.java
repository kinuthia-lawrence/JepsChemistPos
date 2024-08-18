package com.larrykin.jepschemistpos.UTILITIES;

import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class ThemeManager {
    private static final String LIGHT_MODE = "/STYLES/light-mode.css";
    private static final String DARK_MODE = "/STYLES/dark-mode.css";
//    public static Boolean isDarkMode = false;

    private static final DatabaseConn databaseConn = new DatabaseConn();

    // Apply the theme to the scene
    public static void applyTheme(Scene scene, boolean isDarkMode) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(DARK_MODE)).toExternalForm());
            System.out.println("inside applyTheme if, dark mode is " + isDarkMode);
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(LIGHT_MODE)).toExternalForm());
            System.out.println("inside applyTheme else, dark mode is " + isDarkMode);
        }
    }


    public static void saveThemeState(boolean isDarkMode) {
        String updateSQL = "UPDATE utils SET light_theme = ?";
        try {
            Connection connection = databaseConn.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setBoolean(1, isDarkMode);
            statement.executeUpdate();
            System.out.println("inside saveThemeState, dark mode is " + isDarkMode);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static boolean loadThemeState() {
        String querySQL = "SELECT light_theme FROM utils";
        try (Connection connection = databaseConn.getConnection();
             PreparedStatement statement = connection.prepareStatement(querySQL);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return !resultSet.getBoolean("light_theme");
            }
            System.out.println("inside loadThemeState if, dark mode is " + resultSet.getBoolean("light_theme"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to light mode if there's an error
    }
}
