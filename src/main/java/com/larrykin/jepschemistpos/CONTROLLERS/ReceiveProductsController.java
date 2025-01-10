package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Products;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class ReceiveProductsController {

    private static final Logger log = LoggerFactory.getLogger(ReceiveProductsController.class);
    @FXML
    public Button addToTableButton;
    @FXML
    public Button saveStockButton;
    @FXML
    private ComboBox<String> supplierComboBox;
    @FXML
    private ComboBox<String> productNameComboBox;
    @FXML
    private Spinner<Double> buyingQuantitySpinner;
    @FXML
    private Spinner<Double> buyingPriceSpinner;
    @FXML
    private Spinner<Double> minQuantitySpinner;
    @FXML
    private Spinner<Double> sellingPriceSpinner;
    @FXML
    private Spinner<Double> sellingQuantitySpinner;
    @FXML
    private DatePicker expiryDatePicker;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TableView<Products> productsTable;
    @FXML
    private Label totalBuyingPriceLabel;


    //database instance
    DatabaseConn databaseConn = new DatabaseConn();

    private ObservableList<Products> products = FXCollections.observableArrayList();
    private ObservableList<String> productNames = FXCollections.observableArrayList();
    private double totalBuyingPrice = 0.0;
    private StockController stockController;


    @FXML
    public void initialize() {
        initializeTable();
        loadFields();
        instantiateControllers();
    }


    private void instantiateControllers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/stock.fxml"));
            loader.load();
            stockController = loader.getController();
        } catch (Exception e) {
            log.error("Error instantiating sales controllers: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFields() {
        buyingPriceSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        buyingPriceSpinner.setValueFactory(valueFactory);
        sellingPriceSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        sellingPriceSpinner.setValueFactory(valueFactory1);
        buyingQuantitySpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        buyingQuantitySpinner.setValueFactory(valueFactory2);
        sellingQuantitySpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory4 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        sellingQuantitySpinner.setValueFactory(valueFactory4);

        minQuantitySpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 100000.0, 1.0);
        minQuantitySpinner.setValueFactory(valueFactory3);

        try (
                Connection conn = databaseConn.getConnection();
                Statement statement = conn.createStatement();
        ) {
            // clear existing items in the comboBoxes
            supplierComboBox.getItems().clear();
            productNameComboBox.getItems().clear();

            // Load the suppliers
            ResultSet resultSet = statement.executeQuery("SELECT * FROM suppliers");
            while (resultSet.next()) {
                supplierComboBox.getItems().add(resultSet.getString("name"));
            }

            // Load the products
            String query1 = "SELECT * FROM products";
            var rs1 = statement.executeQuery(query1);
            while (rs1.next()) {
                productNames.add(rs1.getString("name"));
            }

        } catch (Exception e) {
            log.error("Error loading combo boxes: ", e);
            e.printStackTrace();
        }
        setupNameComboBoxes();
    }

    private void setupNameComboBoxes() {
        productNameComboBox.setEditable(true); // Ensure the ComboBox is editable
        productNameComboBox.setItems(productNames);

        // Create a FilteredList wrapping the productNames list
        FilteredList<String> filteredItems = new FilteredList<>(productNames, p -> true);

        // Add a listener to the ComboBox editor to filter the items
        productNameComboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = productNameComboBox.getEditor();
            final String selected = productNameComboBox.getSelectionModel().getSelectedItem();

            // If no item in the ComboBox is selected or the editor's text is not equal to the selected item
            if (selected == null || !selected.equals(editor.getText())) {
                filteredItems.setPredicate(item -> {
                    // If the filter text is empty, display all items
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    // Compare item text with filter text
                    String lowerCaseFilter = newValue.toLowerCase();
                    return item.toLowerCase().contains(lowerCaseFilter);
                });
                productNameComboBox.setItems(filteredItems);
            }
        });

        // Add a listener to update the ComboBox items when the editor loses focus
        productNameComboBox.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                productNameComboBox.setItems(productNames);
                productNameComboBox.getSelectionModel().select(productNameComboBox.getEditor().getText());
            }
        });

        // Ensure the ComboBox is always focusable and editable
        productNameComboBox.setOnMouseClicked(event -> {
            if (productNames.isEmpty()) {
                productNameComboBox.setItems(FXCollections.observableArrayList());
            }
            productNameComboBox.show();
        });
    }

    private void initializeTable() {
        //id column
        TableColumn<Products, Number> numberColumn = new TableColumn<>("NO");
        numberColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(productsTable.getItems().indexOf(cellData.getValue()) + 1
                ));
        numberColumn.setPrefWidth(40);
        //ProductName
        TableColumn<Products, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setPrefWidth(200);
        //Quantity
        TableColumn<Products, Double> quantityColumn = new TableColumn<>(" B. Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("minBuyingQuantity"));
        quantityColumn.setPrefWidth(80);
        //Quantity
        TableColumn<Products, Double> sellingQuantityColumn = new TableColumn<>(" S. Quantity");
        sellingQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        sellingQuantityColumn.setPrefWidth(80);
        //Minimum Quantity
        TableColumn<Products, Double> minQuantityProductColumn = new TableColumn<>("Min Qty.");
        minQuantityProductColumn.setCellValueFactory(new PropertyValueFactory<>("minProductQuantity"));
        minQuantityProductColumn.setPrefWidth(100);
        //Buying Price
        TableColumn<Products, Double> buyingPriceColumn = new TableColumn<>("B.Price");
        buyingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        buyingPriceColumn.setPrefWidth(80);
        //Selling Price
        TableColumn<Products, Double> sellingPriceColumn = new TableColumn<>("S.Price");
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingPriceColumn.setPrefWidth(80);
        //supplier name
        TableColumn<Products, String> supplierNameColumn = new TableColumn<>("Supplier");
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierNameColumn.setPrefWidth(100);

        //expiry date
        TableColumn<Products, String> expiryDateColumn = new TableColumn<>("Expiry Date");
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        expiryDateColumn.setPrefWidth(100);
        //Description
        TableColumn<Products, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("productDescription"));
        descriptionColumn.setPrefWidth(200);
        descriptionColumn.setMinWidth(150);

        //editButton
        TableColumn<Products, String> editColumn = new TableColumn<>("Edit");
        editColumn.setPrefWidth(50);  // Set a fixed width
        editColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    totalBuyingPrice -= product.getBuyingPrice() * product.getMinBuyingQuantity();
                    totalBuyingPriceLabel.setText(" " + totalBuyingPrice);
                    products.remove(product);
                    productsTable.setItems(products);
                    editRow(product);
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
        TableColumn<Products, String> removeColumn = new TableColumn<>("Remove");
        removeColumn.setPrefWidth(70);  // Set a fixed width
        removeColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button deleteButton = new Button("Remove");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    deleteConfirmation.setTitle("Remove Product");
                    deleteConfirmation.setHeaderText("Are you sure you want to remove this Product?");
                    deleteConfirmation.setContentText("This action cannot be undone");
                    deleteConfirmation.showAndWait();

                    if (deleteConfirmation.getResult() != ButtonType.OK) {
                        deleteConfirmation.close();
                        return;
                    }
                    totalBuyingPrice -= product.getBuyingPrice() * product.getMinBuyingQuantity();
                    totalBuyingPriceLabel.setText(" " + totalBuyingPrice);
                    products.remove(product);
                    productsTable.setItems(products);
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

        productsTable.getColumns().addAll(numberColumn, productNameColumn, quantityColumn, minQuantityProductColumn, buyingPriceColumn, sellingPriceColumn, supplierNameColumn, expiryDateColumn, descriptionColumn, editColumn, removeColumn);
    }

    private void editRow(Products product) {
        if (product != null) {
            try {
                //getting old values
                String oldName = product.getProductName();
                Double oldQuantity = product.getProductQuantity();
                Double oldBuyingQuantity = product.getMinBuyingQuantity();
                Double oldMinQuantity = product.getMinProductQuantity();
                Double oldBuyingPrice = product.getBuyingPrice();
                Double oldSellingPrice = product.getSellingPrice();
                String oldSupplierName = product.getSupplierName();
                String oldExpiryDate = product.getExpiryDate();
                String oldDescription = product.getProductDescription();

                //setting old values to the fields
                productNameComboBox.setValue(oldName);
                sellingQuantitySpinner.getValueFactory().setValue(oldQuantity);
                buyingQuantitySpinner.getValueFactory().setValue(oldBuyingQuantity);
                minQuantitySpinner.getValueFactory().setValue(oldMinQuantity);
                buyingPriceSpinner.getValueFactory().setValue(oldBuyingPrice);
                sellingPriceSpinner.getValueFactory().setValue(oldSellingPrice);
                supplierComboBox.setValue(oldSupplierName);
                expiryDatePicker.setValue(LocalDate.parse(oldExpiryDate));
                descriptionTextArea.setText(oldDescription);


                addToTableButton.setOnAction(event -> {
                    String nameValue = productNameComboBox.getValue();
                    Double quantityValue = sellingQuantitySpinner.getValue();
                    Double buyingQuantityValue = buyingQuantitySpinner.getValue();
                    Double minQuantityValue = minQuantitySpinner.getValue();
                    Double buyingPriceValue = buyingPriceSpinner.getValue();
                    Double sellingPriceValue = sellingPriceSpinner.getValue();
                    String supplierNameValue = supplierComboBox.getValue();
                    LocalDate expiryDateValue = expiryDatePicker.getValue();
                    String descriptionValue = descriptionTextArea.getText().isBlank() ?"description NULL": descriptionTextArea.getText();

                    if (nameValue != null && !nameValue.isBlank() &&
                            quantityValue != null && !quantityValue.isNaN() &&
                            buyingQuantityValue != null && !buyingQuantityValue.isNaN() &&
                            minQuantityValue != null && !minQuantityValue.isNaN() &&
                            buyingPriceValue != null && !buyingPriceValue.isNaN() &&
                            sellingPriceValue != null && !sellingPriceValue.isNaN() &&
                            supplierNameValue != null && !supplierNameValue.isBlank() &&
                            expiryDateValue != null && !expiryDateValue.isBefore(LocalDate.now())) {

                        Products updatedProduct = new Products();
                        updatedProduct.setProductName(nameValue);
                        updatedProduct.setProductQuantity(quantityValue);
                        updatedProduct.setMinBuyingQuantity(buyingQuantityValue);
                        updatedProduct.setMinProductQuantity(minQuantityValue);
                        updatedProduct.setBuyingPrice(buyingPriceValue);
                        updatedProduct.setSellingPrice(sellingPriceValue);
                        updatedProduct.setSupplierName(supplierNameValue);
                        updatedProduct.setExpiryDate(expiryDateValue.toString());
                        updatedProduct.setProductDescription(descriptionValue);


                        products.add(updatedProduct);

                        productsTable.setItems(products);
                        totalBuyingPrice += buyingPriceValue * buyingQuantityValue;
                        totalBuyingPriceLabel.setText(" " + totalBuyingPrice);

                        //clear fields
                        productNameComboBox.setValue("");
                        sellingQuantitySpinner.getValueFactory().setValue(0.0);
                        buyingQuantitySpinner.getValueFactory().setValue(0.0);
                        minQuantitySpinner.getValueFactory().setValue(0.0);
                        buyingPriceSpinner.getValueFactory().setValue(0.0);
                        sellingPriceSpinner.getValueFactory().setValue(0.0);
                        expiryDatePicker.setValue(null);
                        descriptionTextArea.clear();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error Updating the Product.");
                        alert.setContentText("Please fill the fields correctly");
                        alert.showAndWait();
                    }
                });
            } catch (Exception ex) {
                System.out.println("Error getting old values: " + ex.getMessage());
                ex.printStackTrace();
            }

        }
    }

    @FXML
    private void addToTable() {
        String productName = productNameComboBox.getValue();
        Double buyingQuantity = buyingQuantitySpinner.getValue();
        Double sellingQuantity = sellingQuantitySpinner.getValue();
        Double minQuantity = minQuantitySpinner.getValue();
        Double buyingPrice = buyingPriceSpinner.getValue();
        Double sellingPrice = sellingPriceSpinner.getValue();
        String supplierName = supplierComboBox.getValue();
        LocalDate expiryDate = expiryDatePicker.getValue();
        String description = descriptionTextArea.getText().isBlank() ? "description NULL": descriptionTextArea.getText();

        if (productName != null && !productName.isBlank() &&
                buyingQuantity != null && !buyingQuantity.isNaN() &&
                sellingQuantity != null && !sellingQuantity.isNaN() &&
                minQuantity != null && !minQuantity.isNaN() &&
                buyingPrice != null && !buyingPrice.isNaN() &&
                sellingPrice != null && !sellingPrice.isNaN() &&
                supplierName != null && !supplierName.isBlank() &&
                expiryDate != null && !expiryDate.isBefore(LocalDate.now())) {

            Products product = new Products();
            product.setProductName(productName);
            product.setProductQuantity(sellingQuantity);
            product.setMinProductQuantity(minQuantity);
            product.setBuyingPrice(buyingPrice);
            product.setSellingPrice(sellingPrice);
            product.setSupplierName(supplierName);
            product.setExpiryDate(expiryDate.toString());
            product.setProductDescription(description);
            product.setMinBuyingQuantity(buyingQuantity);


            products.add(product);

            productsTable.setItems(products);
            totalBuyingPrice += buyingPrice * buyingQuantity;
            totalBuyingPriceLabel.setText(" " + totalBuyingPrice);

            //clear fields
            productNameComboBox.setValue("");
            buyingQuantitySpinner.getValueFactory().setValue(0.0);
            sellingQuantitySpinner.getValueFactory().setValue(0.0);
            minQuantitySpinner.getValueFactory().setValue(0.0);
            buyingPriceSpinner.getValueFactory().setValue(0.0);
            sellingPriceSpinner.getValueFactory().setValue(0.0);
            expiryDatePicker.setValue(null);
            descriptionTextArea.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error Adding the Product.");
            alert.setContentText("Please fill the fields correctly");
            alert.showAndWait();
        }

    }

    @FXML
    private void saveStock() {
        String sql = "INSERT INTO products (name, quantity, min_quantity, buying_price, selling_price, supplier_name, date_added, expiry_date, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), ?, ?) " +
                "ON CONFLICT(name) DO UPDATE SET " +
                "quantity = quantity + excluded.quantity, " +
                "min_quantity = excluded.min_quantity, " +
                "buying_price = excluded.buying_price, " +
                "selling_price = excluded.selling_price, " +
                "supplier_name = excluded.supplier_name, " +
                "date_added = datetime('now'), " +
                "expiry_date = excluded.expiry_date, " +
                "description = excluded.description";
        try (Connection connection = databaseConn.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Products product : products) {
                statement.setString(1, product.getProductName());
                statement.setDouble(2, product.getProductQuantity());
                statement.setDouble(3, product.getMinProductQuantity());
                statement.setDouble(4, product.getBuyingPrice());
                statement.setDouble(5, product.getSellingPrice());
                statement.setString(6, product.getSupplierName());
                statement.setString(7, product.getExpiryDate());
                statement.setString(8, product.getProductDescription());

                statement.addBatch(); // Add the current set of parameters to the batch
            }

            int[] rowsAffected = statement.executeBatch(); // Execute the batch statements
            if (rowsAffected.length == products.size()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Products saved successfully");
                alert.setContentText("All products have been saved to the database.");
                alert.showAndWait();

                stockController.populateTable();
                stockController.loadFields();

                products.clear();
                Stage stage = (Stage) saveStockButton.getScene().getWindow();
                stage.close();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error saving products");
                alert.setContentText("Some products were not saved to the database.");
                alert.showAndWait();
            }

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving products");
            alert.setContentText("Error saving products: " + ex.getMessage());
            alert.showAndWait();
            ex.printStackTrace();
        }
    }


}