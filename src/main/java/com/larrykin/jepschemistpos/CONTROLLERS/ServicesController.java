package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Service;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

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
    private TextField serviceNameTextField;
    @FXML
    private AnchorPane tableAnchorPane;

    @FXML
    public void initialize() {
        initializeTable();
    }

    private void initializeTable() {
        TableView<Service> historyTableView = new TableView<>();

        //id column
        TableColumn<Service, Object> serviceIDColumn = new TableColumn<>("ID");
        serviceIDColumn.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        //date column
        TableColumn<Service, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setResizable(true);
        //service name column
        TableColumn<Service, String> serviceNameColumn = new TableColumn<>("Service Name");
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        serviceNameColumn.setResizable(true);
        //description column
        TableColumn<Service, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setResizable(true);
        descriptionColumn.setPrefWidth(200);  // Set an initial preferred width
        descriptionColumn.setMinWidth(150);   // Set a minimum width


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
        TableColumn<Service, Double> cashPaymentColumn = new TableColumn<>("Cash Payment");
        cashPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("cashPayment"));
        cashPaymentColumn.setResizable(true);
        //mpesa payment column
        TableColumn<Service, Double> mpesaPaymentColumn = new TableColumn<>("Mpesa Payment");
        mpesaPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("mpesaPayment"));
        mpesaPaymentColumn.setResizable(true);
        //edit column
        TableColumn<Service, String> editColumn = new TableColumn<>("Edit");
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
        ScrollPane scrollPane = new ScrollPane(historyTableView);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        tableAnchorPane.getChildren().add(scrollPane);
    }

}
