package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.UtilsData;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private void initializeTable() {
        TableColumn<UtilsData, Object> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UtilsData, Boolean> themeColumn = new TableColumn<>("Light Theme");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("lightTheme"));
        TableColumn<UtilsData, Double> currentCashColumn = new TableColumn<>("Current Cash");
        currentCashColumn.setCellValueFactory(new PropertyValueFactory<>("currentCash"));
        TableColumn<UtilsData, Double> currentMpesaColumn = new TableColumn<>("Current Mpesa");
        currentMpesaColumn.setCellValueFactory(new PropertyValueFactory<>("currentMpesa"));
        TableColumn<UtilsData, Double> currentStockColumn = new TableColumn<>("Current Stock");
        currentStockColumn.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        TableColumn<UtilsData, Integer> servicesNumberColumn = new TableColumn<>("Services Number");
        servicesNumberColumn.setCellValueFactory(new PropertyValueFactory<>("servicesNumber"));
        TableColumn<UtilsData, Double> totalCashFromSalesColumn = new TableColumn<>("Total Cash From Sales");
        totalCashFromSalesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCashFromSales"));
        TableColumn<UtilsData, Double> servicesRevenueColumn = new TableColumn<>("Services Revenue");
        servicesRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("servicesRevenue"));
        TableColumn<UtilsData, Double> totalValueOfAddedStockColumn = new TableColumn<>("Total Value Of Added Stock");
        totalValueOfAddedStockColumn.setCellValueFactory(new PropertyValueFactory<>("totalValueOfAddedStock"));
        TableColumn<UtilsData, Double> totalMpesaFromSalesColumn = new TableColumn<>("Total Mpesa From Sales");
        totalMpesaFromSalesColumn.setCellValueFactory(new PropertyValueFactory<>("totalMpesaFromSales"));

        utilsTableView.getColumns().addAll(idColumn, themeColumn, currentCashColumn, currentMpesaColumn, currentStockColumn, servicesNumberColumn, totalCashFromSalesColumn, servicesRevenueColumn, totalValueOfAddedStockColumn, totalMpesaFromSalesColumn);

        populateTableView();
    }
    //? load data from utils to the tableView
    public void populateTableView() {
        ObservableList<UtilsData> utilsData = FXCollections.observableArrayList();
        utilsData.addAll(getUtilsDataFromDatabase());
        utilsTableView.setItems(utilsData);
        loadLabels(utilsData.get(0));
    }
    //? get data from the database
    private List<UtilsData> getUtilsDataFromDatabase() {
        List<UtilsData> utilsData = new ArrayList<>();

        try{
            Connection conn = databaseConn.getConnection();

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM utils");

            while (resultSet.next()) {
                UtilsData data = new UtilsData();
                data.setId(resultSet.getObject("id"));
                data.setLightTheme(resultSet.getBoolean("light_theme"));
                data.setCurrentCash(resultSet.getDouble("current_cash"));
                data.setCurrentMpesa(resultSet.getDouble("current_mpesa"));
                data.setCurrentStock(resultSet.getDouble("current_stock_value"));
                data.setServicesNumber(resultSet.getInt("services_number"));
                data.setTotalCashFromSales(resultSet.getDouble("total_cash_from_sales"));
                data.setServicesRevenue(resultSet.getDouble("services_revenue"));
                data.setTotalValueOfAddedStock(resultSet.getDouble("total_value_of_added_stock"));
                data.setTotalMpesaFromSales(resultSet.getDouble("total_mpesa_from_sales"));

                utilsData.add(data);
            }

        }catch (Exception e) {
            System.out.println("Error getting utils data from database: " + e.getMessage());
            e.printStackTrace();
        }
        return utilsData;
    }
    //? load the data from the table to the labels
    private void loadLabels(UtilsData utilsData) {
        Platform.runLater(() -> {
            currentCashLabel.setText(String.valueOf(utilsData.getCurrentCash()));
            currentMpesaLabel.setText(String.valueOf(utilsData.getCurrentMpesa()));
            currentStockLabel.setText(String.valueOf(utilsData.getCurrentStock()));
            numberOfServicesLabel.setText(String.valueOf(utilsData.getServicesNumber()));
            totalCashLabel.setText(String.valueOf(utilsData.getTotalCashFromSales()));
            totalServiceRevenueLabel.setText(String.valueOf(utilsData.getServicesRevenue()));
            totalStockAddedLabel.setText(String.valueOf(utilsData.getTotalValueOfAddedStock()));
            TotalMpesa.setText(String.valueOf(utilsData.getTotalMpesaFromSales()));
        });
    }
}