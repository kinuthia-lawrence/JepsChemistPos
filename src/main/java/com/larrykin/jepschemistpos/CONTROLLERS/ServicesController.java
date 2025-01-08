package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Service;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesController {

    private static final Logger log = LoggerFactory.getLogger(ServicesController.class);
    @FXML
    private Spinner<Double> ageSpinner;

    @FXML
    private TextField cashPaymentTextField;

    @FXML
    private TextArea contactInfoTextArea;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private RadioButton femaleRadioButton;

    @FXML
    private ToggleGroup genderToggleGroup;

    @FXML
    private RadioButton maleRadioButton;

    @FXML
    private TextField mpesaPaymentTextField;

    @FXML
    private RadioButton otherRadioButton;

    @FXML
    private TextField patientName;

    @FXML
    private TextField residenceTextField;

    @FXML
    private Button saveButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane tableAnchorPane;

    @FXML
    private Label title;


    @FXML
    public void initialize() {
        initializeTable();
        saveButton.setOnAction(event -> {
            saveService();
        });
    }


    //database connection
    DatabaseConn databaseConn = new DatabaseConn();

    TableView<Service> historyTableView = new TableView<>();

    private void saveService() {
        if (!patientName.getText().isBlank() && !descriptionTextArea.getText().isBlank() && !ageSpinner.getEditor().getText().isBlank() && !residenceTextField.getText().isBlank() && !contactInfoTextArea.getText().isBlank()) {
            if (!cashPaymentTextField.getText().isBlank() || !mpesaPaymentTextField.getText().isBlank()) {
                if (cashPaymentTextField.getText().isBlank()) {
                    cashPaymentTextField.setText("0");
                }
                if (mpesaPaymentTextField.getText().isBlank()) {
                    mpesaPaymentTextField.setText("0");
                }

                RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();


                String sql = "INSERT INTO service_history (date, patient_name,age,gender,residence,contact_info, description, cash_payment, mpesa_payment) VALUES (datetime('now'), ?, ?, ?, ?, ?, ?, ?, ?)";
                try (
                        Connection conn = databaseConn.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                ) {
                    pstmt.setString(1, patientName.getText());
                    pstmt.setString(2, ageSpinner.getEditor().getText());
                    pstmt.setString(3, selectedRadioButton.getText());
                    pstmt.setString(4, residenceTextField.getText());
                    pstmt.setString(5, contactInfoTextArea.getText());
                    pstmt.setString(6, descriptionTextArea.getText());
                    pstmt.setDouble(7, Double.parseDouble(cashPaymentTextField.getText()));
                    pstmt.setDouble(8, Double.parseDouble(mpesaPaymentTextField.getText()));

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
                        conn.close();


                        Double totalCashFromService = Double.parseDouble(cashPaymentTextField.getText()) + Double.parseDouble(mpesaPaymentTextField.getText());
                        Double cash = Double.parseDouble(cashPaymentTextField.getText());
                        Double mpesa = Double.parseDouble(mpesaPaymentTextField.getText());
                        updateStats(totalCashFromService, cash, mpesa, true);
                        populateTable();

                        //clear fields
                        patientName.clear();
                        ageSpinner.getEditor().setText("");
                        residenceTextField.clear();
                        contactInfoTextArea.clear();
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
            alert.setContentText("Fields cannot be empty!. Please fill in the required fields.");
            alert.showAndWait();
        }
    }

    private void updateStats(Double totalCashFromService, Double cash, Double mpesa, Boolean isAddNew) {
        String query = "SELECT * FROM utils";
        try (
                Connection conn = databaseConn.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
        ) {

            if (resultSet.next()) {
                double currentCash = resultSet.getDouble("services_revenue");
                int numberOfServices = resultSet.getInt("services_number");

                double newServicesRevenue = currentCash + totalCashFromService;
                int newNumberOfServices = isAddNew ? (numberOfServices + 1) : (numberOfServices);
                conn.close();
                stmt.close();
                resultSet.close();
                String updateQuery = "UPDATE utils SET services_revenue = ?, services_number = ?, current_cash = current_cash + ?, current_mpesa = current_mpesa + ?, services_total_cash = services_total_cash + ? , services_total_mpesa = services_total_mpesa + ? ";
                try (
                        Connection conn1 = databaseConn.getConnection();
                        Statement stmt1 = conn1.createStatement();
                        PreparedStatement pstmt = conn1.prepareStatement(updateQuery);
                ) {
                    pstmt.setDouble(1, newServicesRevenue);
                    pstmt.setInt(2, newNumberOfServices);
                    pstmt.setDouble(3, cash);
                    pstmt.setDouble(4, mpesa);
                    pstmt.setDouble(5, cash);
                    pstmt.setDouble(6, mpesa);

                    int rowAffected = pstmt.executeUpdate();
                    if (rowAffected > 0) {
                        ;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error updating stats");
                        alert.setContentText("Error updating stats");
                        alert.showAndWait();

                    }
                } catch (Exception e) {
                    log.error("Error updating stats -- Inner catch: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error fetching stats --Outer catch: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeTable() {
        // Set TableView properties
//        historyTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        //id column
        TableColumn<Service, Object> serviceIDColumn = new TableColumn<>("ID");
        serviceIDColumn.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        serviceIDColumn.setPrefWidth(30);

        //date column
        TableColumn<Service, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(125);

        //patient name column
        TableColumn<Service, String> patientNameColumn = new TableColumn<>("Patient Name");
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        patientNameColumn.setPrefWidth(150);

        //age column
        TableColumn<Service, Double> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageColumn.setPrefWidth(50);

        //gender column
        TableColumn<Service, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderColumn.setPrefWidth(50);

        //residence column
        TableColumn<Service, String> residenceColumn = new TableColumn<>("Residence");
        residenceColumn.setCellValueFactory(new PropertyValueFactory<>("residence"));
        residenceColumn.setPrefWidth(100);

        //contact info column
        TableColumn<Service, String> contactInfoColumn = new TableColumn<>("Contact Info");
        contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        contactInfoColumn.setPrefWidth(200);
        contactInfoColumn.setMinWidth(150);
        contactInfoColumn.setCellFactory(tc -> {
            TableCell<Service, String> cell = new TableCell<>() {
                private final Text text = new Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(contactInfoColumn.widthProperty().subtract(5));  // Adjust wrapping width
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        //description column
        TableColumn<Service, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(200);
        descriptionColumn.setMinWidth(150);
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

        //print button column
        TableColumn<Service, String> printColumn = new TableColumn<>("Print");
        printColumn.setPrefWidth(50);  // Set a fixed width
        printColumn.setCellFactory(param -> new TableCell<Service, String>() {
            private final Button printButton = new Button("Print");

            {
                printButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                printButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    printRow(service);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(printButton);
                }
            }
        });

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

        historyTableView.getColumns().addAll(serviceIDColumn, dateColumn, patientNameColumn, ageColumn, genderColumn, residenceColumn, contactInfoColumn, descriptionColumn, cashPaymentColumn, mpesaPaymentColumn, printColumn, editColumn, deleteColumn);
        historyTableView.setPrefHeight(600);

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
        try (
                Connection conn = databaseConn.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

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

                conn.close();
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
                String oldPatientName = service.getPatientName();
                double oldAge = service.getAge();
                String oldGender = service.getGender();
                String oldResidence = service.getResidence();
                String oldContactInfo = service.getContactInfo();
                String oldServiceDescription = service.getDescription();
                double oldCashPayment = service.getCashPayment();
                double oldMpesaPayment = service.getMpesaPayment();

                patientName.setText(oldPatientName);
                ageSpinner.getEditor().setText(String.valueOf(oldAge));
                if (oldGender.equalsIgnoreCase("M")) {
                    maleRadioButton.setSelected(true);
                } else if (oldGender.equalsIgnoreCase("F")) {
                    femaleRadioButton.setSelected(true);
                } else {
                    otherRadioButton.setSelected(true);
                }
                residenceTextField.setText(oldResidence);
                contactInfoTextArea.setText(oldContactInfo);
                descriptionTextArea.setText(oldServiceDescription);
                cashPaymentTextField.setText(String.valueOf(oldCashPayment));
                mpesaPaymentTextField.setText(String.valueOf(oldMpesaPayment));

                saveButton.setText("UPDATE");
                saveButton.setOnAction(event -> {
                    if (!patientName.getText().isBlank() && !ageSpinner.getEditor().getText().isBlank() && !residenceTextField.getText().isBlank() && !contactInfoTextArea.getText().isBlank() && !descriptionTextArea.getText().isBlank()) {
                        if (!cashPaymentTextField.getText().isBlank() || !mpesaPaymentTextField.getText().isBlank()) {
                            if (cashPaymentTextField.getText().isBlank()) {
                                cashPaymentTextField.setText("0");
                            }
                            if (mpesaPaymentTextField.getText().isBlank()) {
                                mpesaPaymentTextField.setText("0");
                            }

                            RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();

                            String sql = "UPDATE service_history SET patient_name = ?, age = ?, gender = ?, residence = ?, contact_info = ?, description = ?, cash_payment = ?, mpesa_payment = ? WHERE id = ?";
                            try (
                                    Connection conn = databaseConn.getConnection();
                                    PreparedStatement pstmt = conn.prepareStatement(sql);
                            ) {

                                pstmt.setString(1, patientName.getText());
                                pstmt.setString(2, ageSpinner.getEditor().getText());
                                pstmt.setString(3, selectedRadioButton.getText());
                                pstmt.setString(4, residenceTextField.getText());
                                pstmt.setString(5, contactInfoTextArea.getText());
                                pstmt.setString(6, descriptionTextArea.getText());
                                pstmt.setDouble(7, Double.parseDouble(cashPaymentTextField.getText()));
                                pstmt.setDouble(8, Double.parseDouble(mpesaPaymentTextField.getText()));
                                pstmt.setObject(9, service.getServiceID());

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

                                    conn.close();

                                    Double totalCashFromService = Double.parseDouble(cashPaymentTextField.getText()) + Double.parseDouble(mpesaPaymentTextField.getText()) - oldCashPayment - oldMpesaPayment;
                                    Double cash = Double.parseDouble(cashPaymentTextField.getText()) - oldCashPayment;
                                    Double mpesa = Double.parseDouble(mpesaPaymentTextField.getText()) - oldMpesaPayment;
                                    updateStats(totalCashFromService, cash, mpesa, false);
                                    populateTable();

                                    //clear fields
                                    patientName.clear();
                                    ageSpinner.getEditor().setText("");
                                    residenceTextField.clear();
                                    contactInfoTextArea.clear();
                                    descriptionTextArea.clear();
                                    cashPaymentTextField.clear();
                                    mpesaPaymentTextField.clear();
                                    saveButton.setText("SAVE");
                                    saveButton.setOnAction(e -> {
                                        saveService();
                                    });
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
                        alert.setContentText("Fields cannot be empty!. Please fill in the required fields.");
                        alert.showAndWait();
                    }
                });
            } catch (Exception e) {
                System.out.println("Error editing service: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void printRow(Service service) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Service");
        alert.setHeaderText("Printing service");
        alert.setContentText("Printing service: " + service.getPatientName());
        alert.showAndWait();
    }

    private void populateTable() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        services.addAll(getServicesFromDatabase());
        historyTableView.setItems(services);
    }

    private List<Service> getServicesFromDatabase() {
        List<Service> services = new ArrayList<>();
        try (
                Connection conn = databaseConn.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT * FROM service_history");
        ) {
            while (resultSet.next()) {
                Service service = new Service();
                service.setServiceID(resultSet.getObject("id"));
                service.setDate(resultSet.getString("date"));
                service.setPatientName(resultSet.getString("patient_name"));
                service.setAge(resultSet.getDouble("age"));
                service.setGender(resultSet.getString("gender"));
                service.setResidence(resultSet.getString("residence"));
                service.setContactInfo(resultSet.getString("contact_info"));
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
