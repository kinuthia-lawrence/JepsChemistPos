package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.ENUMS.ROLE;
import com.larrykin.jepschemistpos.MODELS.Products;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class StockController {

    private static final Logger log = LoggerFactory.getLogger(StockController.class);
    @FXML
    private Button addStockButton;

    @FXML
    private Spinner<Double> buyingPriceSpinner;

    @FXML
    private DatePicker expiryDatePicker;

    @FXML
    private ImageView iconSearch;

    @FXML
    private Spinner<Double> minQuantitySpinner;

    @FXML
    private ComboBox<String> nameComboBox;

    @FXML
    private TextArea optionalDescription;

    @FXML
    private Spinner<Double> quantitySpinner;

    @FXML
    private Button refreshButton;

    @FXML
    private ImageView refreshIcon;

    @FXML
    private Button saveButton;

    @FXML
    private Button searchButton;

    @FXML
    private HBox searchParent;

    @FXML
    private TextField searchTextField;

    @FXML
    private Spinner<Double> sellingPriceSpinner;

    @FXML
    private TableView<Products> stockTable;

    @FXML
    private ComboBox<String> supplierComboBox;

    @FXML
    public void initialize() {
        initializeUiElements();
        initializeTable();
        loadFields();
        instantiateControllers();
        addStockButton.setOnAction(e -> receiveProducts());
        saveButton.setOnAction(e -> saveProduct());
    }

    private void initializeUiElements() {
        //? Search Button
        Image searchImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/search.png")));
        iconSearch.setImage(searchImage);

        searchButton.setOnAction(event -> {
            if (searchTextField.getText().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please enter a product name to search!");
                alert.showAndWait();
                return;
            }
            populateFilteredStockTable();
        });

        //? Refresh Button
        Image refreshImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/refresh.png")));
        refreshIcon.setImage(refreshImage);

        refreshButton.setOnAction(e -> {
            populateTable();
            loadFields();
            searchTextField.setText("");
        });
    }


    //database instance
    DatabaseConn conn = new DatabaseConn();

    private ObservableList<String> productNames = FXCollections.observableArrayList();
    private SalesController salesController;

    private void instantiateControllers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/sale.fxml"));
            loader.load();
            salesController = loader.getController();
        } catch (Exception e) {
            System.out.println("Error instantiating sales controllers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadFields() {
        buyingPriceSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        buyingPriceSpinner.setValueFactory(valueFactory);
        sellingPriceSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        sellingPriceSpinner.setValueFactory(valueFactory1);
        quantitySpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        quantitySpinner.setValueFactory(valueFactory2);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 100000.0, 1.0);
        minQuantitySpinner.setValueFactory(valueFactory3);

        try (
                Connection connection = conn.getConnection();
                Statement statement = connection.createStatement();
        ) {
            //Clear existing items in the ComboBoxes
            nameComboBox.getItems().clear();
            supplierComboBox.getItems().clear();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM suppliers");
            while (resultSet.next()) {
                supplierComboBox.getItems().add(resultSet.getString("name"));
            }

            // Load product names into the productNames list
            resultSet = statement.executeQuery("SELECT * FROM products");
            while (resultSet.next()) {
                productNames.add(resultSet.getString("name"));
            }
        } catch (Exception e) {
            log.error("Error loading combo boxes: ", e);
            e.printStackTrace();
        }

        // Call setupNameComboBox to set up the nameComboBox
        setupNameComboBox();
    }

    private void setupNameComboBox() {
        nameComboBox.setEditable(true); // Ensure the ComboBox is editable
        nameComboBox.setItems(productNames);

        // Create a FilteredList wrapping the productNames list
        FilteredList<String> filteredItems = new FilteredList<>(productNames, p -> true);

        // Add a listener to the ComboBox editor to filter the items
        nameComboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = nameComboBox.getEditor();
            final String selected = nameComboBox.getSelectionModel().getSelectedItem();

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
                nameComboBox.setItems(filteredItems);
            }
        });

        // Add a listener to update the ComboBox items when the editor loses focus
        nameComboBox.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                nameComboBox.setItems(productNames);
                nameComboBox.getSelectionModel().select(nameComboBox.getEditor().getText());
            }
        });

        // Ensure the ComboBox is always focusable and editable
        nameComboBox.setOnMouseClicked(event -> {
            if (productNames.isEmpty()) {
                nameComboBox.setItems(FXCollections.observableArrayList());
            }
            nameComboBox.show();
        });
    }

    private void initializeTable() {
//        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //id column
        TableColumn<Products, Object> serviceIDColumn = new TableColumn<>("ID");
        serviceIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        serviceIDColumn.setPrefWidth(30);
        //ProductName
        TableColumn<Products, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setPrefWidth(100);
        //Quantity
        TableColumn<Products, Double> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        quantityColumn.setPrefWidth(75);
        //Minimum Quantity
        TableColumn<Products, Double> minQuantityProductColumn = new TableColumn<>("Min Qty.");
        minQuantityProductColumn.setCellValueFactory(new PropertyValueFactory<>("minProductQuantity"));
        minQuantityProductColumn.setPrefWidth(75);
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
                    if (DashboardController.loggedInUser.getRole() == ROLE.ADMIN) {
                        editRow(product);
                    } else {
                        Alert userAlert = new Alert(Alert.AlertType.ERROR);
                        userAlert.setTitle("Error");
                        userAlert.setHeaderText("You are not authorized to edit records");
                        userAlert.setContentText("Only Admins can edit records");
                        userAlert.showAndWait();
                    }
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
                    if (DashboardController.loggedInUser.getRole() == ROLE.ADMIN) {
                        deleteRow(product);
                    } else {
                        Alert userAlert = new Alert(Alert.AlertType.ERROR);
                        userAlert.setTitle("Error");
                        userAlert.setHeaderText("You are not authorized to delete records");
                        userAlert.setContentText("Only Admins can delete records");
                        userAlert.showAndWait();
                    }
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

        stockTable.getColumns().addAll(serviceIDColumn, productNameColumn, quantityColumn, minQuantityProductColumn, buyingPriceColumn, sellingPriceColumn, supplierNameColumn, dateAddedColumn, expiryDateColumn, descriptionColumn, editColumn, deleteColumn);

        populateTable();
    }

    private void receiveProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/receive_products.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Receive Products");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateTable() {
        ObservableList<Products> products = FXCollections.observableArrayList();
        products.addAll(getProductsFromDatabase());
        stockTable.setItems(products);
    }

    private List<Products> getProductsFromDatabase() {
        List<Products> products = new ArrayList<>();

        try (
                Connection connection = conn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM products");
        ) {
            while (resultSet.next()) {
                Products product = new Products();
                product.setProductID(resultSet.getObject("id"));
                product.setProductName(resultSet.getString("name"));
                product.setProductQuantity(resultSet.getDouble("quantity"));
                product.setMinProductQuantity(resultSet.getDouble("min_quantity"));
                product.setBuyingPrice(resultSet.getDouble("buying_price"));
                product.setSellingPrice(resultSet.getDouble("selling_price"));
                product.setSupplierName(resultSet.getString("supplier_name"));
                product.setDateAdded(resultSet.getString("date_added"));
                product.setExpiryDate(resultSet.getString("expiry_date"));
                product.setProductDescription(resultSet.getString("description"));
                products.add(product);
            }
        } catch (Exception e) {
            System.out.println("Error fetching product: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public void deleteRow(Products product) {
        Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        deleteConfirmation.setTitle("Delete Product");
        deleteConfirmation.setHeaderText("Are you sure you want to delete this Product?");
        deleteConfirmation.setContentText("This action cannot be undone");
        deleteConfirmation.showAndWait();

        if (deleteConfirmation.getResult() != ButtonType.OK) {
            deleteConfirmation.close();
            return;
        }

        String sql = "DELETE FROM products WHERE id = ?";
        try (
                Connection connection = conn.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql);
        ) {
            pstmt.setObject(1, product.getProductID());
            int rowAffected = pstmt.executeUpdate();
            if (rowAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Product deleted successfully");
                alert.setContentText("Product deleted successfully");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                timeline.setCycleCount(1);
                timeline.play();
                alert.showAndWait();
                //? On success Methods
                connection.close();
                salesController.updateCurrentStockWorth();
                populateTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error deleting Product");
                alert.setContentText("Error deleting Product");
                alert.showAndWait();
            }


        } catch (SQLException e) {
            System.out.println("Error(sql) deleting Product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveProduct() {
        String nameValue = nameComboBox.getValue();
        Double quantityValue = quantitySpinner.getValue();
        Double minQuantityValue = minQuantitySpinner.getValue();
        Double buyingPriceValue = buyingPriceSpinner.getValue();
        Double sellingPriceValue = sellingPriceSpinner.getValue();
        String supplierNameValue = supplierComboBox.getValue();
        LocalDate expiryDateValue = expiryDatePicker.getValue();
        String descriptionValue = optionalDescription.getText();

        if (nameValue != null && !nameValue.isBlank() &&
                quantityValue != null && !quantityValue.isNaN() &&
                minQuantityValue != null && !minQuantityValue.isNaN() &&
                buyingPriceValue != null && !buyingPriceValue.isNaN() &&
                sellingPriceValue != null && !sellingPriceValue.isNaN() &&
                supplierNameValue != null && !supplierNameValue.isBlank() &&
                expiryDateValue != null && !expiryDateValue.isBefore(LocalDate.now())) {

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
            try (
                    Connection connection = conn.getConnection();
                    PreparedStatement statement = connection.prepareStatement(sql);
            ) {
                statement.setString(1, nameValue);
                statement.setDouble(2, quantityValue);
                statement.setDouble(3, minQuantityValue);
                statement.setDouble(4, buyingPriceValue);
                statement.setDouble(5, sellingPriceValue);
                statement.setString(6, supplierNameValue);
                statement.setString(7, expiryDateValue.toString());
                statement.setString(8, descriptionValue != null && !descriptionValue.isBlank() ? descriptionValue : "description NULL");

                int rowAffected = statement.executeUpdate();
                if (rowAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Product saved successfully");
                    alert.setContentText("Product saved successfully");
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                    timeline.setCycleCount(1);
                    timeline.play();
                    alert.showAndWait();

                    connection.close();
                    populateTable();
                    loadFields();
                    salesController.updateCurrentStockWorth();
                    updateTotalStock(quantityValue, buyingPriceValue);


                    //clear fields
                    nameComboBox.setValue("");

                    quantitySpinner.getValueFactory().setValue(0.0);
                    minQuantitySpinner.getValueFactory().setValue(0.0);
                    buyingPriceSpinner.getValueFactory().setValue(0.0);
                    sellingPriceSpinner.getValueFactory().setValue(0.0);
                    supplierComboBox.setValue(null);
                    expiryDatePicker.setValue(null);
                    optionalDescription.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error saving product");
                    alert.setContentText("Error saving product");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                System.out.println("Error(sql) saving product: " + ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error saving product");
                alert.setContentText("Error saving product: " + ex.getMessage());
                alert.showAndWait();
                ex.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error Saving the Product.");
            alert.setContentText("Please fill the fields correctly");
            alert.showAndWait();
        }
    }


    private void updateTotalStock(Double quantityValue, Double buyingPriceValue) {
        double totalStock = quantityValue * buyingPriceValue;
        String sql = "UPDATE utils SET total_value_of_added_stock = total_value_of_added_stock + ?";
        try (
                Connection connection = conn.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setDouble(1, totalStock);
            int resultSet = statement.executeUpdate();
            if (resultSet > 0) {
                ;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error updating total stock");
                alert.setContentText("Error updating total stock");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error updating total stock");
            alert.setContentText("Error updating total stock: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void editRow(Products product) {
        if (product != null) {
            try {
                //getting old values
                String oldName = product.getProductName();
                Double oldQuantity = product.getProductQuantity();
                Double oldMinQuantity = product.getMinProductQuantity();
                Double oldBuyingPrice = product.getBuyingPrice();
                Double oldSellingPrice = product.getSellingPrice();
                String oldSupplierName = product.getSupplierName();
                String oldExpiryDate = product.getExpiryDate();
                String oldDescription = product.getProductDescription();

                //setting old values to the fields
                nameComboBox.setValue(oldName);
                quantitySpinner.getValueFactory().setValue(oldQuantity);
                minQuantitySpinner.getValueFactory().setValue(oldMinQuantity);
                buyingPriceSpinner.getValueFactory().setValue(oldBuyingPrice);
                sellingPriceSpinner.getValueFactory().setValue(oldSellingPrice);
                supplierComboBox.setValue(oldSupplierName);
                expiryDatePicker.setValue(LocalDate.parse(oldExpiryDate));
                optionalDescription.setText(oldDescription);


                saveButton.setText("UPDATE");
                saveButton.setOnAction(event -> {
                    String nameValue = nameComboBox.getValue();
                    Double quantityValue = quantitySpinner.getValue();
                    Double minQuantityValue = minQuantitySpinner.getValue();
                    Double buyingPriceValue = buyingPriceSpinner.getValue();
                    Double sellingPriceValue = sellingPriceSpinner.getValue();
                    String supplierNameValue = supplierComboBox.getValue();
                    LocalDate expiryDateValue = expiryDatePicker.getValue();
                    String descriptionValue = optionalDescription.getText();

                    if (nameValue != null && !nameValue.isBlank() &&
                            quantityValue != null && !quantityValue.isNaN() &&
                            minQuantityValue != null && !minQuantityValue.isNaN() &&
                            buyingPriceValue != null && !buyingPriceValue.isNaN() &&
                            sellingPriceValue != null && !sellingPriceValue.isNaN() &&
                            supplierNameValue != null && !supplierNameValue.isBlank() &&
                            expiryDateValue != null && !expiryDateValue.isBefore(LocalDate.now())) {

                        String sql = "UPDATE products SET name = ?, quantity = ?, min_quantity = ?, buying_price = ?, selling_price = ?, supplier_name = ?, expiry_date = ?, description = ? WHERE id = ?";
                        try (
                                Connection connection = conn.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql);
                        ) {
                            statement.setString(1, nameValue);
                            statement.setDouble(2, quantityValue);
                            statement.setDouble(3, minQuantityValue);
                            statement.setDouble(4, buyingPriceValue);
                            statement.setDouble(5, sellingPriceValue);
                            statement.setString(6, supplierNameValue);
                            statement.setString(7, expiryDateValue.toString());
                            statement.setString(8, descriptionValue != null && !descriptionValue.isBlank() ? descriptionValue : "description NULL");
                            statement.setObject(9, product.getProductID());

                            int rowAffected = statement.executeUpdate();
                            if (rowAffected > 0) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText("Product updated successfully");
                                alert.setContentText("Product updated successfully");
                                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                                timeline.setCycleCount(1);
                                timeline.play();
                                alert.showAndWait();

                                connection.close();
                                populateTable();
                                loadFields();

                                //clear fields
                                nameComboBox.setValue("");
                                quantitySpinner.getValueFactory().setValue(0.0);
                                minQuantitySpinner.getValueFactory().setValue(0.0);
                                buyingPriceSpinner.getValueFactory().setValue(0.0);
                                sellingPriceSpinner.getValueFactory().setValue(0.0);
                                supplierComboBox.setValue(null);
                                expiryDatePicker.setValue(null);
                                optionalDescription.clear();
                                saveButton.setText("SAVE");
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Error Updating product");
                                alert.setContentText("Error Updating product");
                                alert.showAndWait();
                            }
                        } catch (SQLException ex) {
                            System.out.println("Error(sql) updating product: " + ex.getMessage());
                            ex.printStackTrace();
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

    private void populateFilteredStockTable() {
        stockTable.getItems().clear();
        stockTable.getItems().addAll(getFilteredProductsFromDatabase());
    }

    private List<Products> getFilteredProductsFromDatabase() {
        List<Products> products = new ArrayList<>();
        try (
                Connection connection = conn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM products WHERE name LIKE '%" + searchTextField.getText() + "%'");
        ) {
            while (resultSet.next()) {
                Products product = new Products();
                product.setProductID(resultSet.getObject("id"));
                product.setProductName(resultSet.getString("name"));
                product.setProductQuantity(resultSet.getDouble("quantity"));
                product.setMinProductQuantity(resultSet.getDouble("min_quantity"));
                product.setBuyingPrice(resultSet.getDouble("buying_price"));
                product.setSellingPrice(resultSet.getDouble("selling_price"));
                product.setSupplierName(resultSet.getString("supplier_name"));
                product.setDateAdded(resultSet.getString("date_added"));
                product.setExpiryDate(resultSet.getString("expiry_date"));
                product.setProductDescription(resultSet.getString("description"));
                products.add(product);
            }
        } catch (Exception e) {
            System.out.println("Error fetching product: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }
}

