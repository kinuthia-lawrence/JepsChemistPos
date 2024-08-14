package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.UtilsData;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    private Spinner<Double> withdrawCashSpinner;

    @FXML
    private Spinner<Double> withdrawMpesaSpinner;

    @FXML
    public void initialize() {
     initializeTable();
    }



    DatabaseConn databaseConn = new DatabaseConn();
    TableView<UtilsData> utilsTableView = new TableView<>();


    //TODO  create a tableView with all the columns of the utils table
    private void initializeTable() {

        populateTableView();
    }
    //? load data from utils to the tableView
    public void populateTableView() {
        ObservableList<UtilsData> utilsData = FXCollections.observableArrayList();
        utilsData.addAll(getUtilsDataFromDatabase());
        utilsTableView.setItems(utilsData);
    }
    private List<UtilsData> getUtilsDataFromDatabase() {
        List<UtilsData> utilsData = new ArrayList<>();

        return utilsData;
    }
    //? load the data from the table to the labels
    private void loadLabels(UtilsData utilsData) {
    }
    public void loadStats() throws SQLException {
        try {
            System.out.println("Loading stats");
            Connection conn = databaseConn.getConnection();
            String query = "SELECT * FROM utils";

            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                String currentCash = resultSet.getString("current_cash");
                String currentMpesa = resultSet.getString("current_mpesa");
                String currentStock = resultSet.getString("current_stock_value");
                String servicesNumber = resultSet.getString("services_number");
                String totalCashFromSales = resultSet.getString("total_cash_from_sales");
                String servicesRevenue = resultSet.getString("services_revenue");
                String totalValueOfAddedStock = resultSet.getString("total_value_of_added_stock");
                String totalMpesaFromSales = resultSet.getString("total_mpesa_from_sales");

                Platform.runLater(() -> {
                    currentCashLabel.setText(currentCash);
                    currentMpesaLabel.setText(currentMpesa);
                    currentStockLabel.setText(currentStock);
                    numberOfServicesLabel.setText(servicesNumber);
                    totalCashLabel.setText(totalCashFromSales);
                    totalServiceRevenueLabel.setText(servicesRevenue);
                    totalStockAddedLabel.setText(totalValueOfAddedStock);
                    TotalMpesa.setText(totalMpesaFromSales);
                });
            }

        } catch (SQLException e) {
            System.out.println("Error loading stats: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}