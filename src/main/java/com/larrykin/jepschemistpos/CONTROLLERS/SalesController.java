package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Products;
import com.larrykin.jepschemistpos.MODELS.Sales;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import com.larrykin.jepschemistpos.UTILITIES.PrintingManager;
import com.larrykin.jepschemistpos.UTILITIES.ReceiptPrinter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteErrorCode;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SalesController {


    private static final Logger log = LoggerFactory.getLogger(SalesController.class);
    @FXML
    private TableView<Products> cartTableView;

    @FXML
    private Spinner<Double> cashSpinner;

    @FXML
    private Spinner<Double> creditSpinner;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Spinner<Double> discoutSpinner;

    @FXML
    public Label expectedAmountLabel;

    @FXML
    private ImageView iconRefresh;

    @FXML
    private ImageView iconSearch;

    @FXML
    private Spinner<Double> mpesaSpinner;

    @FXML
    private Button refreshButton;

    @FXML
    private Button salesButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button sellButton;

    @FXML
    private Button stockButton;

    @FXML
    private ScrollPane tableScrollPane;

    @FXML
    public void initialize() {
        loadSpinners();
        initializeStockTable();
        initializeSalesTable();
        initializeButtons();
        initializeUIElements();
        initializeCartTable();

        // Pre-initialize the TableView by adding and removing a dummy item
        Products dummyProduct = new Products();
        cartTableView.getItems().add(dummyProduct);
        cartTableView.getItems().remove(dummyProduct);
    }

    private void initializeCartTable() {

        // Create columns
        TableColumn<Products, Object> productIDColumn = new TableColumn<>("F.ID");
        productIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        productIDColumn.setPrefWidth(30);

        TableColumn<Products, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setPrefWidth(100);

        TableColumn<Products, Double> sellingPriceColumn = new TableColumn<>("S.Price");
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingPriceColumn.setPrefWidth(55);

        TableColumn<Products, Void> decrementColumn = new TableColumn<>("--");
        decrementColumn.setPrefWidth(35);
        decrementColumn.setCellFactory(param -> new TableCell<>() {
            private final Button decrementButton = new Button("-");

            {
                decrementButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                decrementButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    Double currentQuantity = product.getSellingQuantity();
                    if (currentQuantity > 1) {
                        product.setSellingQuantity(currentQuantity - 1);
                        updateTotal(product);
                        cartTableView.refresh();
                    } else {
                        product.setSellingQuantity(1);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(decrementButton);
                }
            }
        });

        TableColumn<Products, Integer> quantityColumn = new TableColumn<>("No.");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("sellingQuantity"));
        quantityColumn.setPrefWidth(35);


        TableColumn<Products, Void> incrementColumn = new TableColumn<>("++");
        incrementColumn.setPrefWidth(35);
        incrementColumn.setCellFactory(param -> new TableCell<>() {
            private final Button incrementButton = new Button("+");

            {
                incrementButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                incrementButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    double currentSellingQuantity = product.getSellingQuantity();
                    double availableQuantity = product.getProductQuantity();

                    if (currentSellingQuantity < availableQuantity) {
                        product.setSellingQuantity(currentSellingQuantity + 1);
                        updateTotal(product);
                        cartTableView.refresh();
                    } else {
                        product.setSellingQuantity(availableQuantity);
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Quantity Limit Reached");
                        alert.setHeaderText("Cannot increment quantity");
                        alert.setContentText("The available quantity in stock is " + availableQuantity + ".");
                        alert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(incrementButton);
                }
            }
        });

        TableColumn<Products, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setPrefWidth(50);
        totalColumn.setCellValueFactory(cellData -> {
            Products sellingProduct = cellData.getValue();
            return new SimpleDoubleProperty(sellingProduct.getSellingPrice() * sellingProduct.getSellingQuantity()).asObject();
        });
        //delete buttons column
        TableColumn<Products, String> deleteColumn = new TableColumn<>("Del.");
        deleteColumn.setPrefWidth(35);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button deleteButton = new Button("X");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    cartTableView.getItems().remove(product);
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

        //Add columns to the table
        cartTableView.getColumns().addAll(productIDColumn, productNameColumn, sellingPriceColumn, decrementColumn, quantityColumn, incrementColumn, totalColumn, deleteColumn);
    }


    //instantiate database
    DatabaseConn databaseConn = new DatabaseConn();


    TableView<Sales> salesTableView = new TableView<>();
    TableView<Products> stockTableView = new TableView<>();

    //? Initialize UI elements
    private void initializeUIElements() {
        Image searchImage = new Image(getClass().getResourceAsStream("/IMAGES/search.png"));
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

        Image refreshImage = new Image(getClass().getResourceAsStream("/IMAGES/refresh.png"));
        iconRefresh.setImage(refreshImage);

        refreshButton.setOnAction(event -> {
            refreshStockTable();
        });
    }

    //? Initialize buttons
    private void initializeButtons() {
        stockButton.setOnAction(event -> {
            // Clear the content of the tableScrollPane
            tableScrollPane.setContent(null);
            tableScrollPane.setContent(stockTableView);
        });

        salesButton.setOnAction(event -> {
            // Clear the content of the tableScrollPane
            tableScrollPane.setContent(null);
            tableScrollPane.setContent(salesTableView);
        });

        sellButton.setOnAction(event -> {
            // Sell the products in the cart
            sellProducts();
        });
    }

    //? Sell products
    private void sellProducts() {
        // Check if cart is empty
        if (cartTableView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Cart");
            alert.setHeaderText("Cart is empty");
            alert.setContentText("Please add products to the cart before selling.");
            alert.showAndWait();
            return;
        }

        StringBuilder goods = new StringBuilder();
        for (Products product : cartTableView.getItems()) {
            goods.append(product.getProductName())
                    .append(" (")
                    .append(product.getSellingQuantity())
                    .append("), ");
        }

        // Get values of the spinners
        double discount = discoutSpinner.getValue();
        double mpesa = mpesaSpinner.getValue();
        double cash = cashSpinner.getValue();
        double credit = creditSpinner.getValue();

        // Get the total expected amount
        double expectedAmount = Double.parseDouble(expectedAmountLabel.getText());

        // Get the paid amount by summing up the cash, mpesa, and credit
        double paidAmount = cash + mpesa + credit;

        // Get description, if empty set to "No description"
        String description = descriptionTextArea.getText();
        if (description.isEmpty()) {
            description = "No description";
        }

        // Print all the values in terminal (for text)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Sale");
        alert.setHeaderText("Confirm the sale");
        alert.setContentText("Goods: " + goods + "\n" +
                "Expected Amount: " + expectedAmount + "\n" +
                "Discount: " + discount + "\n" +
                "Cash: " + cash + "\n" +
                "Mpesa: " + mpesa + "\n" +
                "Credit: " + credit + "\n" +
                "Description: " + description + "\n" +
                "Paid Amount: " + paidAmount + "\n");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            // Save the sale to the database
            try (Connection connection = databaseConn.getConnection()) {
                String insertQuery = "INSERT INTO sales (date, goods, expected_total, total_amount, discount, cash, mpesa, credit, description) VALUES (datetime('now'), ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, goods.toString());
                    preparedStatement.setDouble(2, expectedAmount);
                    preparedStatement.setDouble(3, paidAmount);
                    preparedStatement.setDouble(4, discount);
                    preparedStatement.setDouble(5, cash);
                    preparedStatement.setDouble(6, mpesa);
                    preparedStatement.setDouble(7, credit);
                    preparedStatement.setString(8, description);
                    int rowAffected = preparedStatement.executeUpdate();
                    if (rowAffected > 0) {
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Success");
                        alert1.setHeaderText("Sale success");
                        alert1.setContentText("Sale saved successfully");
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> alert1.close()));
                        timeline.setCycleCount(1);
                        timeline.play();
                        alert1.showAndWait();

                        connection.close();
                        updateDatabase(cartTableView);
                        updateCashAndMpesa(cash, mpesa);
                        populateSalesTable();

                        boolean isPrintEnabled = PrintingManager.loadPrinterState();
                        if (isPrintEnabled) {
                            // Print the receipt

                            String chemistName = "Jelps Chemist";
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                            StringBuilder receiptText = new StringBuilder();
                            receiptText.append("--------------------------------------------\n");
                            receiptText.append("          ").append(chemistName).append("\n");
                            receiptText.append("          ").append(currentDate).append("\n");
                            receiptText.append("--------------------------------------------\n");
                            receiptText.append("Goods:\n");
                            for (Products product : cartTableView.getItems()) {
                                receiptText.append(product.getProductName())
                                        .append(" (")
                                        .append(product.getSellingQuantity())
                                        .append(")\n");
                            }
                            receiptText.append("--------------------------------------------\n");
                            receiptText.append("Expected Amount: ").append(expectedAmount).append("\n");
                            receiptText.append("Discount: ").append(discount).append("\n");
                            receiptText.append("Cash: ").append(cash).append("\n");
                            receiptText.append("Mpesa: ").append(mpesa).append("\n");
                            receiptText.append("Credit: ").append(credit).append("\n");
                            receiptText.append("Description: ").append(description).append("\n");
                            receiptText.append("Paid Amount: ").append(paidAmount).append("\n");
                            receiptText.append("--------------------------------------------\n");
                            receiptText.append("          Thank you for shopping!\n");
                            receiptText.append("--------------------------------------------\n");

                            ReceiptPrinter receiptPrinter = new ReceiptPrinter(receiptText.toString());
                            receiptPrinter.printReceipt();

                            //?log the receipt in termimal
//                            log.info("Receipt: {}", receiptText.toString());
                        }

                        // Clear carts, label, spinners, and textArea
                        cartTableView.getItems().clear();
                        expectedAmountLabel.setText("0.00");
                        discoutSpinner.getValueFactory().setValue(0.0);
                        mpesaSpinner.getValueFactory().setValue(0.0);
                        cashSpinner.getValueFactory().setValue(0.0);
                        creditSpinner.getValueFactory().setValue(0.0);
                        descriptionTextArea.clear();

                    } else {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setTitle("Error");
                        alert1.setHeaderText("Error in saving the sale");
                        alert1.setContentText("Error in saving the sale");
                        alert1.showAndWait();
                    }
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == SQLiteErrorCode.SQLITE_BUSY.code) {
                    System.out.println("Database is busy, retrying...");
                } else {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Error");
                    alert1.setHeaderText("Error in saving the sale");
                    alert1.setContentText("Error: " + e.getMessage());
                    alert1.showAndWait();
                    System.out.println("Error in saving the sale: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } else {
            alert.close();
        }
    }


    //? Update database after selling
    private void updateDatabase(TableView<Products> cartTableView) {
        new Thread(() -> {
            try {
                // Use CopyOnWriteArrayList to avoid ConcurrentModificationException
                CopyOnWriteArrayList<Products> productsList = new CopyOnWriteArrayList<>(cartTableView.getItems());

                for (Products product : productsList) {
                    try {
                        Object productID = product.getProductID();
                        double sellingQuantity = product.getSellingQuantity();
                        double newQuantity = product.getProductQuantity() - sellingQuantity;

                        // Update the quantity of the product in the database
                        String updateQuery = "UPDATE products SET quantity = ? WHERE id = ?";
                        try (
                                Connection connection = databaseConn.getConnection();
                                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)
                        ) {
                            preparedStatement.setDouble(1, newQuantity);
                            preparedStatement.setObject(2, productID);
                            int rowAffected = preparedStatement.executeUpdate();

                            if (rowAffected > 0) {
                                ;
                            } else {
                                System.out.println("Error, Product ID: " + productID + " not updated.");
                            }
                        } catch (Exception e) {
                            System.out.println("Error updating product quantities: " + e.getMessage());
                        }
                    } catch (Exception e) {
                        System.out.println("Error processing product: " + e.getMessage());
                    }
                }
                populateStockTable();
                updateCurrentStockWorth();
            } catch (Exception e) {
                System.out.println("Error updating product quantities: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    //? Update cash and mpesa
    private void updateCashAndMpesa(Double cash, Double mpesa) {
        new Thread(() -> {
            try (
                    Connection connection = databaseConn.getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT current_cash, current_mpesa FROM utils");
            ) {
                if (resultSet.next()) {
                    double currentCash = resultSet.getDouble("current_cash");
                    double currentMpesa = resultSet.getDouble("current_mpesa");

                    double newCash = currentCash + cash;
                    double newMpesa = currentMpesa + mpesa;

                    //? Update the cash and mpesa in the database
                    String updateQuery = "UPDATE utils SET current_cash = ?, current_mpesa = ?,total_cash_from_sales = total_cash_from_sales + ?, total_mpesa_from_sales = total_mpesa_from_sales + ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setDouble(1, newCash);
                    preparedStatement.setDouble(2, newMpesa);
                    preparedStatement.setDouble(3, cash);
                    preparedStatement.setDouble(4, mpesa);
                    int rowAffected = preparedStatement.executeUpdate();
                    if (rowAffected > 0) {
                        ;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error updating cash and mpesa");
                        alert.setContentText("Error updating cash and mpesa");
                        alert.showAndWait();

                    }
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error updating cash and mpesa");
                alert.showAndWait();
                System.out.println("Error updating cash and mpesa: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    //? Update current stock worth
    public void updateCurrentStockWorth() {
        double stockWorth = 0.0;

        String sql = "SELECT SUM(buying_price * quantity) AS stock_worth FROM products";
        try (
                Connection connection = databaseConn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
        ) {

            if (resultSet.next()) {
                stockWorth = resultSet.getDouble("stock_worth");
                //? update current stock worth n utils table
                try {
                    String updateSQL = "UPDATE utils SET current_stock_value = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
                    preparedStatement.setDouble(1, stockWorth);
                    int rowAffected = preparedStatement.executeUpdate();

                    if (rowAffected > 0) {
                        ;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error updating stock worth");
                        alert.setContentText("Error updating stock worth");
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    System.out.println("Error updating stock worth: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error calculating stock worth: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //? Initialize stock table
    private void initializeStockTable() {
        //id column
        TableColumn<Products, Object> productsObjectTableColumn = new TableColumn<>("ID");
        productsObjectTableColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        productsObjectTableColumn.setPrefWidth(30);
        //ProductName
        TableColumn<Products, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setPrefWidth(100);
        //Category
        TableColumn<Products, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        categoryColumn.setPrefWidth(65);
        //Quantity
        TableColumn<Products, Double> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        quantityColumn.setPrefWidth(65);
        //Selling Price
        TableColumn<Products, Double> sellingPriceColumn = new TableColumn<>("S.Price");
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingPriceColumn.setPrefWidth(50);
        //expiry date
        TableColumn<Products, String> expiryDateColumn = new TableColumn<>("Expiry Date");
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        expiryDateColumn.setPrefWidth(75);
        //Description
        TableColumn<Products, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("productDescription"));
        descriptionColumn.setPrefWidth(200);
        descriptionColumn.setMinWidth(100);

        //editButton
        TableColumn<Products, String> addToCartColumn = new TableColumn<>("ADD");
        addToCartColumn.setPrefWidth(100);  // Set a fixed width
        addToCartColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button editButton = new Button("Add to Cart");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    addToCart(product);
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

        stockTableView.getColumns().addAll(productsObjectTableColumn, productNameColumn, categoryColumn, quantityColumn, sellingPriceColumn, expiryDateColumn, descriptionColumn, addToCartColumn);
        stockTableView.setPrefWidth(Region.USE_COMPUTED_SIZE);
        salesTableView.setMinWidth(Region.USE_COMPUTED_SIZE);
//        Double height = tableScrollPane.heightProperty().subtract(10).doubleValue();
        stockTableView.setPrefHeight(580);
        stockTableView.setMinHeight(Region.USE_COMPUTED_SIZE);
        stockTableView.setMaxHeight(Region.USE_COMPUTED_SIZE);
        stockTableView.setMaxWidth(Region.USE_COMPUTED_SIZE);
        tableScrollPane.setContent(stockTableView);
        populateStockTable();
    }

    //? Populate stock table
    private void populateStockTable() {
        ObservableList<Products> products = FXCollections.observableArrayList();
        products.addAll(getProductsFromDatabase());
        stockTableView.setItems(products);
    }

    //? Get products from database
    private List<Products> getProductsFromDatabase() {
        List<Products> products = new ArrayList<>();

        try (
                Connection connection = databaseConn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM products");
        ) {
            while (resultSet.next()) {
                Products product = new Products();
                product.setProductID(resultSet.getObject("id"));
                product.setProductName(resultSet.getString("name"));
                product.setProductCategory(resultSet.getString("category"));
                product.setProductQuantity(resultSet.getDouble("quantity"));
                product.setSellingPrice(resultSet.getDouble("selling_price"));
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

    //? populate filtered products
    public void populateFilteredStockTable() {
        stockTableView.getItems().clear();
        stockTableView.getItems().addAll(filteredProducts());
    }

    //? Filtered products
    private List<Products> filteredProducts() {
        List<Products> products = new ArrayList<>();

        String searchName = searchTextField.getText();

        try (
                Connection connection = databaseConn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM  products  WHERE name LIKE  '%" + searchName + "%'");
        ) {
            while (resultSet.next()) {
                Products product = new Products();
                product.setProductID(resultSet.getObject("id"));
                product.setProductName(resultSet.getString("name"));
                product.setProductCategory(resultSet.getString("category"));
                product.setProductQuantity(resultSet.getDouble("quantity"));
                product.setSellingPrice(resultSet.getDouble("selling_price"));
                product.setExpiryDate(resultSet.getString("expiry_date"));
                product.setProductDescription(resultSet.getString("description"));
                products.add(product);
            }

        } catch (Exception e) {
            System.out.println("Error fetching filtered products" + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error filtering products");
            alert.setContentText("Error :" + e.getMessage());
            alert.showAndWait();
        }

        return products;
    }

    //? refreshTableView
    public void refreshStockTable() {
        searchTextField.setText("");
        populateStockTable();
    }

    //? Add to cart
    private void addToCart(Products product) {
        // Add product to the cart
        if (product.getProductQuantity() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Out of Stock");
            alert.setHeaderText("Product out of stock");
            alert.setContentText("The product is out of stock.");
            alert.showAndWait();
            return;
        }
        product.setSellingQuantity(1); // Set initial quantity to 1
        cartTableView.getItems().add(product);
        cartTableView.refresh();
        updateExpectedAmount();
    }

    //? Update total
    private void updateTotal(Products product) {
        double total = product.getSellingPrice() * product.getSellingQuantity();
        product.setTotal(total);
        updateExpectedAmount();
    }

    //? Update expected amount
    private void updateExpectedAmount() {
        double totalAmount = 0.0;
        for (Products product : cartTableView.getItems()) {
            totalAmount += product.getSellingPrice() * product.getSellingQuantity();
        }
        expectedAmountLabel.setText(String.format("%.2f", totalAmount));
    }

    //? Initialize sales table
    private void initializeSalesTable() {
        //id
        TableColumn<Sales, Object> salesIDColumn = new TableColumn<>("ID");
        salesIDColumn.setCellValueFactory(new PropertyValueFactory<>("salesID"));
        salesIDColumn.setPrefWidth(30);
        //date
        TableColumn<Sales, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(100);
        //Goods
        TableColumn<Sales, String> goodsColumn = new TableColumn<>("Goods");
        goodsColumn.setCellValueFactory(new PropertyValueFactory<>("goods"));
        goodsColumn.setPrefWidth(150);
        goodsColumn.setCellFactory(tableCell -> {
            TableCell<Sales, String> cell = new TableCell<>() {
                private final Text text = new Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(goodsColumn.widthProperty().subtract(5));
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });
        //Total expected
        TableColumn<Sales, Double> expectedAmountColumn = new TableColumn<>("Expected Amount");
        expectedAmountColumn.setCellValueFactory(new PropertyValueFactory<>("expectedAmount"));
        expectedAmountColumn.setPrefWidth(110);
        //Total
        TableColumn<Sales, Double> totalAmountColumn = new TableColumn<>("Paid Amount");
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalAmountColumn.setPrefWidth(100);
        //Discount
        TableColumn<Sales, Double> discountAmountColumn = new TableColumn<>("Discount");
        discountAmountColumn.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));
        discountAmountColumn.setPrefWidth(75);
        //Cash
        TableColumn<Sales, Double> cashColumn = new TableColumn<>("Cash");
        cashColumn.setCellValueFactory(new PropertyValueFactory<>("cash"));
        cashColumn.setPrefWidth(50);
        //Mpesa
        TableColumn<Sales, Double> mpesaColumn = new TableColumn<>("Mpesa");
        mpesaColumn.setCellValueFactory(new PropertyValueFactory<>("mpesa"));
        mpesaColumn.setPrefWidth(50);
        //Credit
        TableColumn<Sales, Double> creditColumn = new TableColumn<>("Credit");
        creditColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        creditColumn.setPrefWidth(50);
        //Description
        TableColumn<Sales, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(150);
        //delete column
        TableColumn<Sales, String> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setPrefWidth(65);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Sales, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Sales record = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Record");
                    alert.setHeaderText("Are you sure you want to delete this record?");
                    alert.setContentText("This action cannot be undone.");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        salesTableView.getItems().remove(record);
                    } else {
                        alert.close();
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

        salesTableView.getColumns().addAll(salesIDColumn, dateColumn, goodsColumn, expectedAmountColumn, totalAmountColumn, discountAmountColumn, cashColumn, mpesaColumn, creditColumn, descriptionColumn, deleteColumn);
        salesTableView.setPrefWidth(Region.USE_COMPUTED_SIZE);
        salesTableView.setPrefHeight(580);

        populateSalesTable();
    }

    //? Populate sales table
    private void populateSalesTable() {
        ObservableList<Sales> sales = FXCollections.observableArrayList();
        sales.addAll(getSalesFromDatabase());
        salesTableView.setItems(sales);
    }

    //? Get sales from database
    private List<Sales> getSalesFromDatabase() {
        List<Sales> sales = new ArrayList<>();
        try (
                Connection connection = databaseConn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM sales ORDER BY date DESC");
        ) {
            while (resultSet.next()) {
                Sales sale = new Sales();
                sale.setSalesID(resultSet.getObject("id"));
                sale.setDate(resultSet.getString("date"));
                sale.setGoods(resultSet.getString("goods"));
                sale.setExpectedAmount(resultSet.getDouble("expected_total"));
                sale.setTotalAmount(resultSet.getDouble("total_amount"));
                sale.setDiscountAmount(resultSet.getDouble("discount"));
                sale.setCash(resultSet.getDouble("cash"));
                sale.setMpesa(resultSet.getDouble("mpesa"));
                sale.setCredit(resultSet.getDouble("credit"));
                sale.setDescription(resultSet.getString("description"));
                sales.add(sale);
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error fetching sales");
            alert.setContentText("Error fetching sales: " + e.getMessage());
            alert.showAndWait();
            System.out.println("Error fetching sales: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

    //? Load spinners
    private void loadSpinners() {
        discoutSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        discoutSpinner.setValueFactory(valueFactory);
        mpesaSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory1 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        mpesaSpinner.setValueFactory(valueFactory1);
        cashSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        cashSpinner.setValueFactory(valueFactory2);
        creditSpinner.setEditable(true);
        SpinnerValueFactory<Double> valueFactory3 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100000.0, 0.0);
        creditSpinner.setValueFactory(valueFactory3);
    }
}
