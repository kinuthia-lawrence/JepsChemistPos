package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Products;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    @FXML
    private TableView<Products> expiredGoodsTableView;

    @FXML
    private TableView<Products> outOfStockTableView;

    @FXML
    public void initialize() {
        initializeExpiredGoodsTable();
        initializeOutOfStockTable();

    }
    //? instantiate database connection
    DatabaseConn databaseConn = new DatabaseConn();
    private StockController stockController;

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
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    //todo : delete from database as refunded
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
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Products product = getTableView().getItems().get(getIndex());
                    //todo : delete from database as loss
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

    private void checkExpiredGoods() {
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
        TableColumn<Products, Double> outOfStockProductQuantityColumn = new TableColumn<>("Product Quantity");
        outOfStockProductQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        outOfStockProductQuantityColumn.setPrefWidth(200);
        //product price column
        TableColumn<Products, Double> outOfStockProductPriceColumn = new TableColumn<>("Buying Price");
        outOfStockProductPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        outOfStockProductPriceColumn.setPrefWidth(150);
        //supplier column
        TableColumn<Products, String> outOfStockSupplierColumn = new TableColumn<>("Supplier Name");
        outOfStockSupplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        outOfStockSupplierColumn.setPrefWidth(200);
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

        outOfStockTableView.getColumns().addAll(outOfStockProductIDColumn, outOfStockProductNameColumn, outOfStockProductCategoryColumn, outOfStockProductQuantityColumn, outOfStockProductPriceColumn, outOfStockSupplierColumn, deleteColumn);

        checkOutOfStock();
    }

    private void checkOutOfStock() {
        ObservableList<Products> outOfStockProducts = FXCollections.observableArrayList();
        List<Products> products = new ArrayList<>();

        try{
            Connection connection = databaseConn.getConnection();
            String query = "SELECT * FROM products WHERE quantity <= 2";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Products product = new Products();
                product.setProductID(resultSet.getObject("id"));
                product.setProductName(resultSet.getString("name"));
                product.setProductCategory(resultSet.getString("category"));
                product.setProductQuantity(resultSet.getDouble("quantity"));
                product.setBuyingPrice(resultSet.getDouble("buying_price"));
                product.setSupplierName(resultSet.getString("supplier_name"));
                products.add(product);

            }
            connection.close();
        }catch (Exception e){
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
