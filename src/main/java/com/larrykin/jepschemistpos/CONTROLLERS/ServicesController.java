package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Service;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesController {

    @FXML
    private TextField cashPaymentTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField mpesaPaymentTextField;

    @FXML
    private Button saveButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField serviceNameTextField;

    @FXML
    private AnchorPane tableAnchorPane;


    @FXML
    public void initialize() {
        initializeTable();
        instantiateHomeLoader();
        saveButton.setOnAction(event -> {
            saveService();
        });
    }


    //? Instantiate the HomeController class
    private HomeController homeController;

    private void instantiateHomeLoader() {
        try {
            //? Instantiate the HomeController class
            FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/FXML/home.fxml"));
            Scene homeScene = new Scene(homeLoader.load());
            homeController = homeLoader.getController();
        } catch (Exception e) {
            System.out.println("Error instantiating homeLoader: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //database connection
    DatabaseConn databaseConn = new DatabaseConn();

    TableView<Service> historyTableView = new TableView<>();

    private void saveService() {
        if (!serviceNameTextField.getText().isBlank() && !descriptionTextArea.getText().isBlank()) {
            if (!cashPaymentTextField.getText().isBlank() || !mpesaPaymentTextField.getText().isBlank()) {
                if (cashPaymentTextField.getText().isBlank()) {
                    cashPaymentTextField.setText("0");
                }
                if (mpesaPaymentTextField.getText().isBlank()) {
                    mpesaPaymentTextField.setText("0");
                }

                String sql = "INSERT INTO service_history (date, service_name, description, cash_payment, mpesa_payment) VALUES (datetime('now'), ?, ?, ?, ?)";
                try {
                    Connection conn = databaseConn.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, serviceNameTextField.getText());
                    pstmt.setString(2, descriptionTextArea.getText());
                    pstmt.setDouble(3, Double.parseDouble(cashPaymentTextField.getText()));
                    pstmt.setDouble(4, Double.parseDouble(mpesaPaymentTextField.getText()));

                    int rowAffected = pstmt.executeUpdate();


                    if (rowAffected > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Service saved successfully");
                        alert.setContentText("Service saved successfully");
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                        timeline.setCycleCount(1);
                        timeline.play();
                        alert.showAndWait();

                        //? On Success methods
                        Double totalCashFromService = Double.parseDouble(cashPaymentTextField.getText()) + Double.parseDouble(mpesaPaymentTextField.getText());
                        updateStats(totalCashFromService);
                        populateTable();

                        //clear fields
                        serviceNameTextField.clear();
                        descriptionTextArea.clear();
                        cashPaymentTextField.clear();
                        mpesaPaymentTextField.clear();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error saving service");
                        alert.setContentText("Error saving service");
                        alert.showAndWait();
                    }

                } catch (SQLException e) {
                    System.out.println("Error(sql) saving service: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error saving service");
                    alert.setContentText("Error saving service: " + e.getMessage());
                    alert.showAndWait();
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error saving service");
                alert.setContentText("Please fill in at least one payment method");
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving service");
            alert.setContentText("Service name and description cannot be empty!. Please fill in the required fields.");
            alert.showAndWait();
        }
    }

    private void updateStats(Double totalCashFromService) {
        try {
            Connection conn = databaseConn.getConnection();
            String query = "SELECT * FROM utils";

            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                double currentCash = resultSet.getDouble("services_revenue");
                int numberOfServices = resultSet.getInt("services_number");

                double newServicesRevenue = currentCash + totalCashFromService;
                int newNumberOfServices = numberOfServices + 1;

                String updateQuery = "UPDATE utils SET services_revenue = ?, services_number = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setDouble(1, newServicesRevenue);
                pstmt.setInt(2, newNumberOfServices);

                int rowAffected = pstmt.executeUpdate();

                if (rowAffected > 0) {
                    homeController.populateTableView();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error updating stats");
                    alert.setContentText("Error updating stats");
                    alert.showAndWait();
                }
            }

        } catch (Exception e) {
            System.out.println("Error updating stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeTable() {
        // Set TableView properties
//        historyTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        //id column
        TableColumn<Service, Object> serviceIDColumn = new TableColumn<>("ID");
        serviceIDColumn.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        serviceIDColumn.setPrefWidth(30);  // Set a fixed width

        //date column
        TableColumn<Service, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(125);  // Set a fixed width

        //service name column
        TableColumn<Service, String> serviceNameColumn = new TableColumn<>("Service Name");
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        serviceNameColumn.setPrefWidth(100);  // Set a fixed width

        //description column
        TableColumn<Service, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(350);  // Set an initial preferred width, larger for text
        descriptionColumn.setMinWidth(200);   // Set a minimum width
        descriptionColumn.setCellFactory(tc -> {
            TableCell<Service, String> cell = new TableCell<>() {
                private final Text text = new Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(descriptionColumn.widthProperty().subtract(5));  // Adjust wrapping width
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        //cash payment column
        TableColumn<Service, Double> cashPaymentColumn = new TableColumn<>("Cash");
        cashPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("cashPayment"));
        cashPaymentColumn.setPrefWidth(50);  // Set a fixed width

        //mpesa payment column
        TableColumn<Service, Double> mpesaPaymentColumn = new TableColumn<>("Mpesa");
        mpesaPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("mpesaPayment"));
        mpesaPaymentColumn.setPrefWidth(50);  // Set a fixed width

        //edit column
        TableColumn<Service, String> editColumn = new TableColumn<>("Edit");
        editColumn.setPrefWidth(45);  // Set a fixed width
        editColumn.setCellFactory(param -> new TableCell<Service, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    editRow(service);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        //delete column
        TableColumn<Service, String> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setPrefWidth(65);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Service, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    deleteRow(service);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        historyTableView.getColumns().addAll(serviceIDColumn, dateColumn, serviceNameColumn, descriptionColumn, cashPaymentColumn, mpesaPaymentColumn, editColumn, deleteColumn);
        historyTableView.setPrefHeight(520);

        scrollPane.setContent(historyTableView);
        populateTable();
    }

    private void deleteRow(Service service) {
        Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        deleteConfirmation.setTitle("Delete Service");
        deleteConfirmation.setHeaderText("Are you sure you want to delete this service?");
        deleteConfirmation.setContentText("This action cannot be undone");
        deleteConfirmation.showAndWait();

        if (deleteConfirmation.getResult() != ButtonType.OK) {
            deleteConfirmation.close();
            return;
        }

        String sql = "DELETE FROM service_history WHERE id = ?";
        try {
            Connection conn = databaseConn.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setObject(1, service.getServiceID());
            int rowAffected = pstmt.executeUpdate();

            if (rowAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Service deleted successfully");
                alert.setContentText("Service deleted successfully");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                timeline.setCycleCount(1);
                timeline.play();
                alert.showAndWait();
                populateTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error deleting service");
                alert.setContentText("Error deleting service");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            System.out.println("Error(sql) deleting service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editRow(Service service) {
        if (service != null) {
            try {
                String oldServiceName = service.getServiceName();
                String oldServiceDescription = service.getDescription();
                double oldCashPayment = service.getCashPayment();
                double oldMpesaPayment = service.getMpesaPayment();

                serviceNameTextField.setText(oldServiceName);
                descriptionTextArea.setText(oldServiceDescription);
                cashPaymentTextField.setText(String.valueOf(oldCashPayment));
                mpesaPaymentTextField.setText(String.valueOf(oldMpesaPayment));

                saveButton.setText("UPDATE");
                saveButton.setOnAction(event -> {
                    if (!serviceNameTextField.getText().isBlank() && !descriptionTextArea.getText().isBlank()) {
                        if (!cashPaymentTextField.getText().isBlank() || !mpesaPaymentTextField.getText().isBlank()) {
                            if (cashPaymentTextField.getText().isBlank()) {
                                cashPaymentTextField.setText("0");
                            }
                            if (mpesaPaymentTextField.getText().isBlank()) {
                                mpesaPaymentTextField.setText("0");
                            }

                            String sql = "UPDATE service_history SET service_name = ?, description = ?, cash_payment = ?, mpesa_payment = ? WHERE id = ?";
                            try {
                                Connection conn = databaseConn.getConnection();
                                PreparedStatement pstmt = conn.prepareStatement(sql);
                                pstmt.setString(1, serviceNameTextField.getText());
                                pstmt.setString(2, descriptionTextArea.getText());
                                pstmt.setDouble(3, Double.parseDouble(cashPaymentTextField.getText()));
                                pstmt.setDouble(4, Double.parseDouble(mpesaPaymentTextField.getText()));
                                pstmt.setObject(5, service.getServiceID());

                                int rowAffected = pstmt.executeUpdate();


                                if (rowAffected > 0) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Success");
                                    alert.setHeaderText("Service Updated successfully");
                                    alert.setContentText("Service Updated successfully");
                                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                                    timeline.setCycleCount(1);
                                    timeline.play();
                                    alert.showAndWait();
                                    populateTable();

                                    //clear fields
                                    serviceNameTextField.clear();
                                    descriptionTextArea.clear();
                                    cashPaymentTextField.clear();
                                    mpesaPaymentTextField.clear();
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Error Updating service");
                                    alert.setContentText("Error Updating service");
                                    alert.showAndWait();
                                }

                            } catch (SQLException e) {
                                System.out.println("Error(sql) Updating service: " + e.getMessage());
                                e.printStackTrace();
                            } catch (Exception e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Error Updating service");
                                alert.setContentText("Error Updating service: " + e.getMessage());
                                alert.showAndWait();
                                e.printStackTrace();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error Updating service");
                            alert.setContentText("Please fill in at least one payment method");
                            alert.showAndWait();
                        }

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error Updating service");
                        alert.setContentText("Service name and description cannot be empty!. Please fill in the required fields.");
                        alert.showAndWait();
                    }
                });
            } catch (Exception e) {
                System.out.println("Error editing service: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void populateTable() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        services.addAll(getServicesFromDatabase());
        historyTableView.setItems(services);
    }

    private List<Service> getServicesFromDatabase() {
        List<Service> services = new ArrayList<>();
        try {
            Connection conn = databaseConn.getConnection();

            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM service_history");

            while (resultSet.next()) {
                Service service = new Service();
                service.setServiceID(resultSet.getObject("id"));
                service.setDate(resultSet.getString("date"));
                service.setServiceName(resultSet.getString("service_name"));
                service.setDescription(resultSet.getString("description"));
                service.setCashPayment(resultSet.getDouble("cash_payment"));
                service.setMpesaPayment(resultSet.getDouble("mpesa_payment"));
                services.add(service);
            }

        } catch (Exception e) {
            System.out.println("Error fetching services: " + e.getMessage());
            e.printStackTrace();
        }
        return services;
    }
}
