package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Products;
import com.larrykin.jepschemistpos.MODELS.Sales;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalesController {

    @FXML
    public Label expectedAmountLabel;
    @FXML
    private TableView<Sales> cartTable;

    @FXML
    private Spinner<Double> cashSpinner;

    @FXML
    private Spinner<Double> creditSpinner;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Spinner<Double> discoutSpinner;


    @FXML
    private ImageView iconSearch;

    @FXML
    private Spinner<Double> mpesaSpinner;

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
        loadStockTable();
        loadSalesTable();
        initializeButtons();
        initializeUIElements();
    }




    //instantiate database
    DatabaseConn databaseConn = new DatabaseConn();

    TableView<Sales> salesTableView = new TableView<>();
    TableView<Products> stockTableView = new TableView<>();

    private void initializeUIElements() {
        Image searchImage = new Image(getClass().getResourceAsStream("/IMAGES/help.gif"));
        iconSearch.setImage(searchImage);
    }

    private void initializeButtons() {
        stockButton.setOnAction(event -> {
            tableScrollPane.setContent(null);
            loadStockTable();

        });

        salesButton.setOnAction(event -> {
            tableScrollPane.setContent(null);
            loadSalesTable();
            tableScrollPane.setContent(salesTableView);

        });

    }

    private void loadStockTable() {
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
        stockTableView.setPrefHeight(520);
        stockTableView.setMinHeight(Region.USE_COMPUTED_SIZE);
        stockTableView.setMaxHeight(Region.USE_COMPUTED_SIZE);
        stockTableView.setMaxWidth(Region.USE_COMPUTED_SIZE);
        tableScrollPane.setContent(stockTableView);
        populateStockTable();
    }

    private void populateStockTable() {
        ObservableList<Products> products = FXCollections.observableArrayList();
        products.addAll(getProductsFromDatabase());
        stockTableView.setItems(products);
    }

    private List<Products> getProductsFromDatabase() {
        List<Products> products = new ArrayList<>();

        try {
            Connection connection = databaseConn.getConnection();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM products");

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

    private void addToCart(Products product) {
    }

    private void loadSalesTable() {
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
        //Total
        TableColumn<Sales, Double> totalAmountColumn = new TableColumn<>("Total");
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalAmountColumn.setPrefWidth(50);
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
                    deleteRow(record);
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

        salesTableView.getColumns().addAll(salesIDColumn, dateColumn, goodsColumn, totalAmountColumn, discountAmountColumn, cashColumn, mpesaColumn, creditColumn, descriptionColumn, deleteColumn);
        salesTableView.setPrefWidth(Region.USE_COMPUTED_SIZE);
        salesTableView.setPrefHeight(520);

        populateSalesTable();
    }

    private void populateSalesTable() {
    }

    private void deleteRow(Sales record) {
    }

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
