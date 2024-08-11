package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Products;
import com.larrykin.jepschemistpos.MODELS.Service;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class StockController {

    @FXML
    private Button addStockButton;

    @FXML
    private Spinner<?> buyingPriceSpinner;

    @FXML
    private ComboBox<?> categoryCombobox;

    @FXML
    private DatePicker expiryDatePicker;

    @FXML
    private ComboBox<?> nameComboBox;

    @FXML
    private TextArea optionalDescription;

    @FXML
    private Spinner<?> quantitySpinner;

    @FXML
    private Button saveButton;

    @FXML
    private Spinner<?> sellingPriceSpinner;

    @FXML
    private TableView<Products> stockTable;

    @FXML
    private ComboBox<?> supplierComboBox;

    @FXML
    public void initialize (){
        initializeTable();
        loadComboBoxes();
    }

    //database instance
    DatabaseConn conn = new DatabaseConn();

    private void loadComboBoxes() {
    }

    private void initializeTable() {
        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //id column
        TableColumn<Products, Object> serviceIDColumn = new TableColumn<>("ID");
        serviceIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        serviceIDColumn.setPrefWidth(30);
        //ProductName
        TableColumn<Products, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setPrefWidth(100);
        //Category
        TableColumn<Products, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        categoryColumn.setPrefWidth(100);
        //Quantity
        TableColumn<Products, Double> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        quantityColumn.setPrefWidth(75);
        //Buying Price
        TableColumn<Products, Double> buyingPriceColumn = new TableColumn<>("B.Price");
        buyingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        buyingPriceColumn.setPrefWidth(50);
        //Selling Price
        TableColumn<Products, Double> sellingPriceColumn = new TableColumn<>("S.Price");
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingPriceColumn.setPrefWidth(50);
        //supplier name
        TableColumn<Products, String> supplierNameColumn = new TableColumn<>("Supplier");
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierNameColumn.setPrefWidth(100);
        //date added
        TableColumn<Products, String> dateAddedColumn = new TableColumn<>("Date Added");
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        dateAddedColumn.setPrefWidth(100);
        //expiry date
        TableColumn<Products, String> expiryDateColumn = new TableColumn<>("Expiry Date");
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        expiryDateColumn.setPrefWidth(100);
        //Description
        TableColumn<Products, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("productDescription"));
        descriptionColumn.setPrefWidth(200);
        descriptionColumn.setMinWidth(100);

        //editButton
        TableColumn<Products, String> editColumn = new TableColumn<>("Edit");
        editColumn.setPrefWidth(45);  // Set a fixed width
        editColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
//                    editRow(product);
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
        TableColumn<Products, String> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setPrefWidth(65);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
//                    deleteRow(product);
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

        stockTable.getColumns().addAll(serviceIDColumn, productNameColumn, categoryColumn, quantityColumn, buyingPriceColumn, sellingPriceColumn, supplierNameColumn, dateAddedColumn, expiryDateColumn, descriptionColumn, editColumn, deleteColumn);
    }

}
