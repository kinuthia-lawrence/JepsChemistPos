package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Sales;
import com.larrykin.jepschemistpos.MODELS.UtilsData;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
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
    private LineChart<String, Number> salesLineChart;

    @FXML
    private Label servicesTotalCashLabel;

    @FXML
    private Label servicesTotalMpesaLabel;

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
        initializeTable();
        initializeSpinners();
        withdrawButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure you want to withdraw cash?");
            alert.setContentText("Please confirm");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    withdrawCash();
                }
                alert.close();
            });

        });
        initializeSalesChart();
    }


    DatabaseConn databaseConn = new DatabaseConn();
    TableView<UtilsData> utilsTableView = new TableView<>();


    private void initializeSalesChart() {
        salesLineChart.setTitle("Sales Trends");
        salesLineChart.getYAxis().setLabel("Sales Amount (Ksh.)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales Data");

        List<Sales> salesData = getSalesFromDatabase();
        for (Sales sale : salesData) {
            series.getData().add(new XYChart.Data<>(sale.getDate(), sale.getTotalAmount()));
        }

        salesLineChart.getData().add(series);
    }

    private List<Sales> getSalesFromDatabase() {
        List<Sales> sales = new ArrayList<>();
        try {
            Connection connection = databaseConn.getConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT strftime('%Y-%m-%d', date) as date, SUM(total_amount) as total_amount FROM sales GROUP BY strftime('%Y-%m-%d', date)";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Sales sale = new Sales();
                sale.setDate(resultSet.getString("date"));
                sale.setTotalAmount(resultSet.getDouble("total_amount"));
                sales.add(sale);
            }
            connection.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error fetching sales");
            alert.setContentText("Error fetching sales from the database");
            alert.showAndWait();
            System.out.println("Error fetching sales: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

    private void initializeSpinners() {
        SpinnerValueFactory<Double> cashValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000000, 0);
        SpinnerValueFactory<Double> mpesaValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000000, 0);

        withdrawCashSpinner.setEditable(true);
        withdrawMpesaSpinner.setEditable(true);
        withdrawCashSpinner.setValueFactory(cashValueFactory);
        withdrawMpesaSpinner.setValueFactory(mpesaValueFactory);
    }

    private void withdrawCash() {
        if (withdrawCashSpinner.getValue() != null || withdrawMpesaSpinner.getValue() != null) {
            double cash = withdrawCashSpinner.getValue();
            double mpesa = withdrawMpesaSpinner.getValue();
            String description = descriptionTextArea.getText();
            if (description.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Description Error");
                alert.setContentText("Please enter a description");
                alert.showAndWait();
                return;
            }

            if (cash < 0 && mpesa < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please enter a valid amount to withdraw");
                alert.showAndWait();
                return;
            }
            //? add the transaction to the money table
            try {
                Connection conn = databaseConn.getConnection();
                Statement statement = conn.createStatement();
                String sql = "INSERT INTO money (date, cash_withdrawal, mpesa_withdrawal, description) VALUES (datetime('now'),?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setDouble(1, cash);
                preparedStatement.setDouble(2, mpesa);
                preparedStatement.setString(3, description);

                int rowAffected = preparedStatement.executeUpdate();

                if (rowAffected > 0) {
                    conn.close();
                    descriptionTextArea.clear();
                } else {
                    conn.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText("Error adding transaction to money table");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                System.out.println("Error adding transaction to money table: " + e.getMessage());
                e.printStackTrace();
            }

            //? update utils table
            try {
                Connection conn = databaseConn.getConnection();
                Statement statement = conn.createStatement();
                int rowAffected = statement.executeUpdate("UPDATE utils SET current_cash = current_cash - " + cash + ", current_mpesa = current_mpesa - " + mpesa + " WHERE id = 1");
                if (rowAffected > 0) {
                    conn.close();
                    populateTableView();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Success");
                    alert.setContentText("successfully withdrawn cash :" + cash + " and mpesa: " + mpesa);
                    alert.showAndWait();
                    withdrawCashSpinner.getValueFactory().setValue(0.0);
                    withdrawMpesaSpinner.getValueFactory().setValue(0.0);
                } else {
                    conn.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText("Error withdrawing cash");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                System.out.println("Error withdrawing cash: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

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

        try {
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

                servicesTotalCashLabel.setText(String.valueOf(resultSet.getDouble("services_total_cash")));
                servicesTotalMpesaLabel.setText(String.valueOf(resultSet.getDouble("services_total_mpesa")));

                utilsData.add(data);
            }
            conn.close();

        } catch (Exception e) {
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