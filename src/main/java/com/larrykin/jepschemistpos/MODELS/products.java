package com.larrykin.jepschemistpos.MODELS;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class products {
    private SimpleObjectProperty<Object> productID;
    private SimpleStringProperty productName;
    private SimpleStringProperty productCategory;
    private SimpleDoubleProperty productQuantity;
    private SimpleDoubleProperty buyingPrice;
    private SimpleDoubleProperty sellingPrice;
    private SimpleStringProperty supplierName;
    private SimpleStringProperty dateAdded;
    private SimpleStringProperty expiryDate;
    private SimpleStringProperty productDescription;

    public products(SimpleObjectProperty<Object> productID, SimpleStringProperty productName, SimpleStringProperty productCategory, SimpleDoubleProperty productQuantity, SimpleDoubleProperty buyingPrice, SimpleDoubleProperty sellingPrice, SimpleStringProperty supplierName, SimpleStringProperty dateAdded, SimpleStringProperty expiryDate, SimpleStringProperty productDescription) {
        this.productID = productID;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.supplierName = supplierName;
        this.dateAdded = dateAdded;
        this.expiryDate = expiryDate;
        this.productDescription = productDescription;
    }

    public products() {
        this.productID = new SimpleObjectProperty<>();
        this.productName = new SimpleStringProperty("");
        this.productCategory = new SimpleStringProperty("");
        this.productQuantity = new SimpleDoubleProperty(0.0);
        this.buyingPrice = new SimpleDoubleProperty(0.0);
        this.sellingPrice = new SimpleDoubleProperty(0.0);
        this.supplierName = new SimpleStringProperty("");
        this.dateAdded = new SimpleStringProperty("");
        this.expiryDate = new SimpleStringProperty("");
        this.productDescription = new SimpleStringProperty("");
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

    public String getProductCategory() {
        return productCategory.get();
    }

    public SimpleStringProperty productCategoryProperty() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory.set(productCategory);
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
}
