package com.larrykin.jepschemistpos.MODELS;

import javafx.collections.ObservableList;

public class SaleData {
    private ObservableList<Products> cartItems;
    private String discount;
    private String extras;
    private Double cash;
    private Double mpesa;
    private Double credit;
    private String description;

    public SaleData(ObservableList<Products> cartItems, String discount, String extras, Double cash, Double mpesa, Double credit, String description) {
        this.cartItems = cartItems;
        this.discount = discount;
        this.extras = extras;
        this.cash = cash;
        this.mpesa = mpesa;
        this.credit = credit;
        this.description = description;
    }

    public ObservableList<Products> getCartItems() {
        return cartItems;
    }

    public String getDiscount() {
        return discount;
    }

    public String getExtras() {
        return extras;
    }

    public Double getCash() {
        return cash;
    }

    public Double getMpesa() {
        return mpesa;
    }

    public Double getCredit() {
        return credit;
    }

    public String getDescription() {
        return description;
    }
}
