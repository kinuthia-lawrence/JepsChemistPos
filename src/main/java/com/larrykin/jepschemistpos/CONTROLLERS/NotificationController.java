package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Products;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    @FXML
    public TableView<Products> expiredGoodsTableView;

    @FXML
    public TableView<Products> outOfStockTableView;

    @FXML
    public void initialize() {
        initializeExpiredGoodsTable();
        initializeOutOfStockTable();
        instantiateStockController();
        alertNotification();

    }


    //? instantiate database connection
    DatabaseConn databaseConn = new DatabaseConn();
    private StockController stockController;
    private void instantiateStockController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/stock.fxml"));
            Parent root = loader.load();
            stockController = loader.getController();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Instantiating Stock Controller");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void alertNotification() {
        try {
            //check if any of the table has data
            if (!expiredGoodsTableView.getItems().isEmpty() || !outOfStockTableView.getItems().isEmpty()) {
               Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notification");
                alert.setHeaderText("You have some notifications");
                alert.setContentText("You have some expired goods or out of stock products");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Alerting Notification");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void initializeExpiredGoodsTable() {
        //? Expired goods table columns
        //Product ID column
        TableColumn<Products, Object> expiredProductIDColumn = new TableColumn<>("Product ID");
        expiredProductIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        expiredProductIDColumn.setPrefWidth(75);
        //Product Name column
        TableColumn<Products, String> expiredProductNameColumn = new TableColumn<>("Product Name");
        expiredProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        expiredProductNameColumn.setPrefWidth(150);
        //product category column
        TableColumn<Products, String> expiredProductCategoryColumn = new TableColumn<>("Product Category");
        expiredProductCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        expiredProductCategoryColumn.setPrefWidth(110);
        //product quantity column
        TableColumn<Products, Double> expiredProductQuantityColumn = new TableColumn<>("Product Quantity");
        expiredProductQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        expiredProductQuantityColumn.setPrefWidth(110);
        //product price column
        TableColumn<Products, Double> expiredProductPriceColumn = new TableColumn<>("Buying Price");
        expiredProductPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        expiredProductPriceColumn.setPrefWidth(100);
        //supplier column
        TableColumn<Products, String> expiredSupplierColumn = new TableColumn<>("Supplier Name");
        expiredSupplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        expiredSupplierColumn.setPrefWidth(100);
        //date added
        TableColumn<Products, String> dateAddedColumn = new TableColumn<>("Date Added");
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        dateAddedColumn.setPrefWidth(100);
        //expiry date
        TableColumn<Products, String> expiryDateColumn = new TableColumn<>("Expiry Date");
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        expiryDateColumn.setPrefWidth(100);
        // delete as refunded column
        TableColumn<Products, String> deleteColumn = new TableColumn<>("Delete as Refunded");
        deleteColumn.setPrefWidth(150);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button deleteButton = new Button("Delete as Refunded");

            {
                deleteButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    deleteAsRefunded(product);
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
        // delete as loss column
        TableColumn<Products, String> deleteAsLossColumn = new TableColumn<>("Delete as Loss");
        deleteAsLossColumn.setPrefWidth(150);  // Set a fixed width
        deleteAsLossColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button deleteButton = new Button("Delete as Loss");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    deleteAsLoss(product);
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

        expiredGoodsTableView.getColumns().addAll(expiredProductIDColumn, expiredProductNameColumn, expiredProductCategoryColumn, expiredProductQuantityColumn, expiredProductPriceColumn, expiredSupplierColumn, dateAddedColumn, expiryDateColumn, deleteColumn, deleteAsLossColumn);

        checkExpiredGoods();
    }

    private void deleteAsLoss(Products product) {
        //? Delete the product from the expired_goods table and increment the expired loss in utils
        try {
            //get the value = product quantity * buying price
            double loss = product.getProductQuantity() * product.getBuyingPrice();

            Connection connection = databaseConn.getConnection();
            String deleteSQL = "DELETE FROM expired_goods WHERE id = ?";
            String updateSQL = "UPDATE utils SET expired_loss = expired_loss + ?";

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
                 PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                deleteStatement.setInt(1, (int) product.getProductID());
                deleteStatement.executeUpdate();

                updateStatement.setDouble(1, loss);
                updateStatement.executeUpdate();

                connection.close();
                populateExpiredGoodsTable();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Deleting Product as Loss");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void deleteAsRefunded(Products product) {
        try {
            //get the value = product quantity * buying price
            double refunded = product.getProductQuantity() * product.getBuyingPrice();

            Connection connection = databaseConn.getConnection();
            String deleteSQL = "DELETE FROM expired_goods WHERE id = ?";
            String updateSQL = "UPDATE utils SET refunded_expired = refunded_expired + ?";

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
                 PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                deleteStatement.setInt(1, (int) product.getProductID());
                deleteStatement.executeUpdate();

                updateStatement.setDouble(1, refunded);
                updateStatement.executeUpdate();

                connection.close();
                populateExpiredGoodsTable();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Deleting Product as Refunded");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void checkExpiredGoods() {
        String selectExpiredGoodsSQL = "SELECT * FROM products WHERE expiry_date < CURRENT_DATE";
        String insertExpiredGoodsSQL = "INSERT INTO expired_goods (id, name, category, quantity, buying_price, supplier_name, date_added, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String deleteExpiredGoodsSQL = "DELETE FROM products WHERE id = ?";

        try (Connection connection = databaseConn.getConnection();
             Statement selectStatement = connection.createStatement();
             ResultSet resultSet = selectStatement.executeQuery(selectExpiredGoodsSQL)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double buyingPrice = resultSet.getDouble("buying_price");
                String supplierName = resultSet.getString("supplier_name");
                String dateAdded = resultSet.getString("date_added");
                String expiryDate = resultSet.getString("expiry_date");

                try (PreparedStatement insertStatement = connection.prepareStatement(insertExpiredGoodsSQL);
                     PreparedStatement deleteStatement = connection.prepareStatement(deleteExpiredGoodsSQL)) {

                    // Insert into expired_goods table
                    insertStatement.setInt(1, id);
                    insertStatement.setString(2, name);
                    insertStatement.setString(3, category);
                    insertStatement.setInt(4, quantity);
                    insertStatement.setDouble(5, buyingPrice);
                    insertStatement.setString(6, supplierName);
                    insertStatement.setString(7, dateAdded);
                    insertStatement.setString(8, expiryDate);
                    insertStatement.executeUpdate();

                    // Delete from products table
                    deleteStatement.setInt(1, id);
                    deleteStatement.executeUpdate();
                }
            }
            connection.close();
            populateExpiredGoodsTable();
        } catch (Exception e) {
            System.out.println("Error :" + e.getMessage() );
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Processing Expired Goods");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void populateExpiredGoodsTable() {
        ObservableList<Products> expiredGoods = FXCollections.observableArrayList();
        List<Products> products = new ArrayList<>();

        try {
            Connection connection = databaseConn.getConnection();
            String query = "SELECT * FROM expired_goods";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Products product = new Products();
                product.setProductID(resultSet.getObject("id"));
                product.setProductName(resultSet.getString("name"));
                product.setProductCategory(resultSet.getString("category"));
                product.setProductQuantity(resultSet.getDouble("quantity"));
                product.setBuyingPrice(resultSet.getDouble("buying_price"));
                product.setSupplierName(resultSet.getString("supplier_name"));
                product.setDateAdded(resultSet.getString("date_added"));
                product.setExpiryDate(resultSet.getString("expiry_date"));
                products.add(product);
            }
            connection.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Populating Expired Goods Table");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        expiredGoods.addAll(products);
        expiredGoodsTableView.setItems(expiredGoods);
    }


    private void initializeOutOfStockTable() {
        //? Out of stock table columns
        //Product ID column
        TableColumn<Products, Object> outOfStockProductIDColumn = new TableColumn<>("Product ID");
        outOfStockProductIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        outOfStockProductIDColumn.setPrefWidth(75);
        //Product Name column
        TableColumn<Products, String> outOfStockProductNameColumn = new TableColumn<>("Product Name");
        outOfStockProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        outOfStockProductNameColumn.setPrefWidth(200);
        //product category column
        TableColumn<Products, String> outOfStockProductCategoryColumn = new TableColumn<>("Product Category");
        outOfStockProductCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        outOfStockProductCategoryColumn.setPrefWidth(200);
        //product quantity column
        TableColumn<Products, Double> outOfStockProductQuantityColumn = new TableColumn<>("Product Qty.");
        outOfStockProductQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        outOfStockProductQuantityColumn.setPrefWidth(125);
        //minimum product quantity column
        TableColumn<Products, Double> outOfStockMinProductQuantityColumn = new TableColumn<>("Minimum Qty.");
        outOfStockMinProductQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("minProductQuantity"));
        outOfStockMinProductQuantityColumn.setPrefWidth(125);
        //product price column
        TableColumn<Products, Double> outOfStockProductPriceColumn = new TableColumn<>("Buying Price");
        outOfStockProductPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        outOfStockProductPriceColumn.setPrefWidth(125);
        //supplier column
        TableColumn<Products, String> outOfStockSupplierColumn = new TableColumn<>("Supplier Name");
        outOfStockSupplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        outOfStockSupplierColumn.setPrefWidth(175);
        //delete column
        TableColumn<Products, String> deleteColumn = new TableColumn<>("Delete Product");
        deleteColumn.setPrefWidth(100);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Products, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    stockController.deleteRow(product);
                    checkOutOfStock();
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

        outOfStockTableView.getColumns().addAll(outOfStockProductIDColumn, outOfStockProductNameColumn, outOfStockProductCategoryColumn, outOfStockProductQuantityColumn, outOfStockMinProductQuantityColumn,  outOfStockProductPriceColumn, outOfStockSupplierColumn, deleteColumn);

        checkOutOfStock();
    }

    private void checkOutOfStock() {
        ObservableList<Products> outOfStockProducts = FXCollections.observableArrayList();
        List<Products> products = new ArrayList<>();

        try {
            Connection connection = databaseConn.getConnection();
            String query = "SELECT * FROM products WHERE quantity <= min_quantity";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Products product = new Products();
                product.setProductID(resultSet.getObject("id"));
                product.setProductName(resultSet.getString("name"));
                product.setProductCategory(resultSet.getString("category"));
                product.setProductQuantity(resultSet.getDouble("quantity"));
                product.setMinProductQuantity(resultSet.getDouble("min_quantity"));
                product.setBuyingPrice(resultSet.getDouble("buying_price"));
                product.setSupplierName(resultSet.getString("supplier_name"));
                products.add(product);

            }
            connection.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Checking Out of Stock Products");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();

            e.printStackTrace();
        }
        outOfStockProducts.addAll(products);
        outOfStockTableView.setItems(outOfStockProducts);


    }
}
