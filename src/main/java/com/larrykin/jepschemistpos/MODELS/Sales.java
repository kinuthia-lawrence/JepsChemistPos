package com.larrykin.jepschemistpos.MODELS;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sales {
    private SimpleObjectProperty<Object> salesID;
    private SimpleStringProperty date;
    private SimpleStringProperty goods;
    private SimpleDoubleProperty totalAmount;
    private SimpleDoubleProperty discountAmount;
    private SimpleDoubleProperty cash;
    private SimpleDoubleProperty mpesa;
    private SimpleDoubleProperty credit;
    private SimpleStringProperty description;

    public Sales(SimpleObjectProperty<Object> salesID, SimpleStringProperty date, SimpleStringProperty goods, SimpleDoubleProperty totalAmount, SimpleDoubleProperty discountAmount, SimpleDoubleProperty cash, SimpleDoubleProperty mpesa, SimpleDoubleProperty credit, SimpleStringProperty description) {
        this.salesID = salesID;
        this.date = date;
        this.goods = goods;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.cash = cash;
        this.mpesa = mpesa;
        this.credit = credit;
        this.description = description;
    }

    public Sales() {
        this.salesID = new SimpleObjectProperty<>();
        this.date = new SimpleStringProperty("");
        this.goods = new SimpleStringProperty("");
        this.cash = new SimpleDoubleProperty(0.0);
        this.discountAmount = new SimpleDoubleProperty(0.0);
        this.mpesa = new SimpleDoubleProperty(0.0);
        this.credit = new SimpleDoubleProperty(0.0);
        this.description = new SimpleStringProperty("");
    }

    public Object getSalesID() {
        return salesID.get();
    }

    public SimpleObjectProperty<Object> salesIDProperty() {
        return salesID;
    }

    public void setSalesID(Object salesID) {
        this.salesID.set(salesID);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getGoods() {
        return goods.get();
    }

    public SimpleStringProperty goodsProperty() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods.set(goods);
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public SimpleDoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount.set(totalAmount);
    }

    public double getDiscountAmount() {
        return discountAmount.get();
    }

    public SimpleDoubleProperty discountAmountProperty() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount.set(discountAmount);
    }

    public double getCash() {
        return cash.get();
    }

    public SimpleDoubleProperty cashProperty() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash.set(cash);
    }

    public double getMpesa() {
        return mpesa.get();
    }

    public SimpleDoubleProperty mpesaProperty() {
        return mpesa;
    }

    public void setMpesa(double mpesa) {
        this.mpesa.set(mpesa);
    }

    public double getCredit() {
        return credit.get();
    }

    public SimpleDoubleProperty creditProperty() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit.set(credit);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
