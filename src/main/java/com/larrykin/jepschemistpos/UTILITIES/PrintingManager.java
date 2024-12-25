package com.larrykin.jepschemistpos.UTILITIES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PrintingManager {

    private static final Logger log = LoggerFactory.getLogger(PrintingManager.class);

    private static final DatabaseConn databaseConn = new DatabaseConn();

    //save Printer State
    public static void savePrinterState(boolean isPrintEnabled) {
        String updateSQL = "UPDATE utils SET is_print_enabled = ?";
        try {
            Connection connection = databaseConn.getConnection();
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setBoolean(1, isPrintEnabled);
            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            log.error("Error saving printer state{}", e.getMessage());
            e.printStackTrace();
        }
    }

    //load Printer State
    public static boolean loadPrinterState() {
        String querySQL = "SELECT is_print_enabled FROM utils";
        try (Connection connection = databaseConn.getConnection();
             PreparedStatement statement = connection.prepareStatement(querySQL);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getBoolean("is_print_enabled");
            }
        } catch (SQLException e) {
            log.error("Error loading printer state{}", e.getMessage());
            e.printStackTrace();
        }
        return false; //default value
    }

}
