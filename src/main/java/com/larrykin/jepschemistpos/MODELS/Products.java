package com.larrykin.jepschemistpos.MODELS;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Products {
    private SimpleObjectProperty<Object> productID;
    private SimpleStringProperty productName;
    private SimpleDoubleProperty productQuantity;
    private SimpleDoubleProperty minProductQuantity;
    private SimpleDoubleProperty buyingPrice;
    private SimpleDoubleProperty sellingPrice;
    private SimpleStringProperty supplierName;
    private SimpleStringProperty dateAdded;
    private SimpleStringProperty expiryDate;
    private SimpleStringProperty productDescription;
    private SimpleDoubleProperty minBuyingQuantity;

    //others
    private SimpleDoubleProperty sellingQuantity;
    private SimpleDoubleProperty total;

    public Products(SimpleObjectProperty<Object> productID, SimpleStringProperty productName, SimpleDoubleProperty productQuantity, SimpleDoubleProperty minProductQuantity, SimpleDoubleProperty buyingPrice, SimpleDoubleProperty sellingPrice,
                    SimpleStringProperty supplierName, SimpleStringProperty dateAdded, SimpleStringProperty expiryDate, SimpleStringProperty productDescription, SimpleDoubleProperty SellingQuantity, SimpleDoubleProperty total, SimpleDoubleProperty minBuyingQuantity) {
        this.productID = productID;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.minProductQuantity = minProductQuantity;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.supplierName = supplierName;
        this.dateAdded = dateAdded;
        this.expiryDate = expiryDate;
        this.productDescription = productDescription;
        this.sellingQuantity = SellingQuantity;
        this.total = total;
        this.minBuyingQuantity = minBuyingQuantity;
    }

    public Products() {
        this.productID = new SimpleObjectProperty<>();
        this.productName = new SimpleStringProperty("");
        this.productQuantity = new SimpleDoubleProperty(0.0);
        this.minProductQuantity = new SimpleDoubleProperty(0.0);
        this.buyingPrice = new SimpleDoubleProperty(0.0);
        this.sellingPrice = new SimpleDoubleProperty(0.0);
        this.supplierName = new SimpleStringProperty("");
        this.dateAdded = new SimpleStringProperty("");
        this.expiryDate = new SimpleStringProperty("");
        this.productDescription = new SimpleStringProperty("");
        this.sellingQuantity = new SimpleDoubleProperty(0.0);
        this.total = new SimpleDoubleProperty(0.0);
        this.minBuyingQuantity = new SimpleDoubleProperty();
    }

    public double getMinBuyingQuantity() {
        return minBuyingQuantity.get();
    }
    public SimpleDoubleProperty minBuyingQuantityProperty() {
        return minBuyingQuantity;
    }
    public void setMinBuyingQuantity(double minBuyingQuantity) {
        this.minBuyingQuantity.set(minBuyingQuantity);
    }

    public Object getProductID() {
        return productID.get();
    }

    public SimpleObjectProperty<Object> productIDProperty() {
        return productID;
    }

    public void setProductID(Object productID) {
        this.productID.set(productID);
    }

    public String getProductName() {
        return productName.get();
    }

    public SimpleStringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public double getProductQuantity() {
        return productQuantity.get();
    }

    public SimpleDoubleProperty productQuantityProperty() {
        return productQuantity;
    }

    public void setProductQuantity(double productQuantity) {
        this.productQuantity.set(productQuantity);
    }

    public double getMinProductQuantity() {
        return minProductQuantity.get();
    }

    public SimpleDoubleProperty minProductQuantityProperty() {
        return minProductQuantity;
    }

    public void setMinProductQuantity(double minProductQuantity) {
        this.minProductQuantity.set(minProductQuantity);
    }

    public double getBuyingPrice() {
        return buyingPrice.get();
    }

    public SimpleDoubleProperty buyingPriceProperty() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice.set(buyingPrice);
    }

    public double getSellingPrice() {
        return sellingPrice.get();
    }

    public SimpleDoubleProperty sellingPriceProperty() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice.set(sellingPrice);
    }

    public String getSupplierName() {
        return supplierName.get();
    }

    public SimpleStringProperty supplierNameProperty() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName.set(supplierName);
    }

    public String getDateAdded() {
        return dateAdded.get();
    }

    public SimpleStringProperty dateAddedProperty() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded.set(dateAdded);
    }

    public String getExpiryDate() {
        return expiryDate.get();
    }

    public SimpleStringProperty expiryDateProperty() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate.set(expiryDate);
    }

    public String getProductDescription() {
        return productDescription.get();
    }

    public SimpleStringProperty productDescriptionProperty() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription.set(productDescription);
    }

    public double getSellingQuantity() {
        return sellingQuantity.get();
    }

    public SimpleDoubleProperty sellingQuantityProperty() {
        return sellingQuantity;
    }

    public void setSellingQuantity(double sellingQuantity) {
        this.sellingQuantity.set(sellingQuantity);
    }

    public double getTotal() {
        return total.get();
    }

    public SimpleDoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }
}
