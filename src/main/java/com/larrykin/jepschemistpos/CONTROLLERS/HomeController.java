package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class HomeController {

    @FXML
    private Label TotalMpesa;

    @FXML
    private Label currentCashLabel;

    @FXML
    private Label currentMpesaLabel;

    @FXML
    private Label currentStockLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Label numberOfServicesLabel;

    @FXML
    private Label totalCashLabel;

    @FXML
    private Label totalServiceRevenueLabel;

    @FXML
    private Label totalStockAddedLabel;

    @FXML
    private Button withdrawButton;

    @FXML
    private Spinner<Double> withdrawCashSpinner;

    @FXML
    private Spinner<Double> withdrawMpesaSpinner;

    @FXML
    public void initialize() {
        loadStats();
    }

    DatabaseConn databaseConn = new DatabaseConn();

    public void loadStats() {
        // Load stats from utils table in the database
        try {
            Connection conn = databaseConn.getConnection();
            String query = "SELECT * FROM utils";

            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                currentCashLabel.setText(resultSet.getString("current_cash"));
                currentMpesaLabel.setText(resultSet.getString("current_mpesa"));
                currentStockLabel.setText(resultSet.getString("current_stock_value"));
                numberOfServicesLabel.setText(resultSet.getString("services_number"));
                totalCashLabel.setText(resultSet.getString("total_cash_from_sales"));
                totalServiceRevenueLabel.setText(resultSet.getString("services_revenue"));
                totalStockAddedLabel.setText(resultSet.getString("total_value_of_added_stock"));
                TotalMpesa.setText(resultSet.getString("total_mpesa_from_sales"));
            }

        } catch (Exception e) {
            System.out.println("Error loading stats" + e.getMessage());
            e.printStackTrace();
        }
    }

}
