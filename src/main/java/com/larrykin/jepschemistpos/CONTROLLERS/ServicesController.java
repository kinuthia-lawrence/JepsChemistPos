package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Service;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
    }

    //database connection
    DatabaseConn databaseConn = new DatabaseConn();

    private void initializeTable() {
        TableView<Service> historyTableView = new TableView<>();

        // Set TableView properties
        historyTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        //id column
        TableColumn<Service, Object> serviceIDColumn = new TableColumn<>("ID");
        serviceIDColumn.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        serviceIDColumn.setPrefWidth(30);  // Set a fixed width

        //date column
        TableColumn<Service, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(75);  // Set a fixed width

        //service name column
        TableColumn<Service, String> serviceNameColumn = new TableColumn<>("Service Name");
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        serviceNameColumn.setPrefWidth(100);  // Set a fixed width

        //description column
        TableColumn<Service, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(400);  // Set an initial preferred width, larger for text
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
                        text.wrappingWidthProperty().bind(descriptionColumn.widthProperty().subtract(10));  // Adjust wrapping width
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
                editButton.setOnAction(event -> {
                    Service project = getTableView().getItems().get(getIndex());
                    System.out.println("edit " + project.getServiceName());
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
                deleteButton.setOnAction(event -> {
                    Service project = getTableView().getItems().get(getIndex());
                    System.out.println("delete " + project.getServiceName());
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

        scrollPane.setContent(historyTableView);
        populateTable(historyTableView);
    }

    private void populateTable(TableView<Service> historyTableView) {
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

            while(resultSet.next()) {
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
