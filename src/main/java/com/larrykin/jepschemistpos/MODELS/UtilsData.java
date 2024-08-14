package com.larrykin.jepschemistpos.MODELS;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UtilsData {
    private SimpleObjectProperty<Object> id;
    private SimpleBooleanProperty lightTheme;
    private SimpleDoubleProperty currentCash;
    private SimpleDoubleProperty currentMpesa;
    private SimpleDoubleProperty currentStock;
    private SimpleIntegerProperty servicesNumber;
    private SimpleDoubleProperty totalCashFromSales;
    private SimpleDoubleProperty servicesRevenue;
    private SimpleDoubleProperty totalValueOfAddedStock;
    private SimpleDoubleProperty totalMpesaFromSales;

    public UtilsData() {
        this.id = new SimpleObjectProperty<>();
        this.lightTheme = new SimpleBooleanProperty(true);
        this.currentCash = new SimpleDoubleProperty(0.0);
        this.currentMpesa = new SimpleDoubleProperty(0.0);
        this.currentStock = new SimpleDoubleProperty(0.0);
        this.servicesNumber = new SimpleIntegerProperty(0);
        this.totalCashFromSales = new SimpleDoubleProperty(0.0);
        this.servicesRevenue = new SimpleDoubleProperty(0.0);
        this.totalValueOfAddedStock = new SimpleDoubleProperty(0.0);
        this.totalMpesaFromSales = new SimpleDoubleProperty(0.0);
    }

    public UtilsData(SimpleObjectProperty<Object> id, SimpleBooleanProperty lightTheme, SimpleDoubleProperty currentCash, SimpleDoubleProperty currentMpesa, SimpleDoubleProperty currentStock, SimpleIntegerProperty servicesNumber, SimpleDoubleProperty totalCashFromSales, SimpleDoubleProperty servicesRevenue, SimpleDoubleProperty totalValueOfAddedStock, SimpleDoubleProperty totalMpesaFromSales) {
        this.id = id;
        this.lightTheme = lightTheme;
        this.currentCash = currentCash;
        this.currentMpesa = currentMpesa;
        this.currentStock = currentStock;
        this.servicesNumber = servicesNumber;
        this.totalCashFromSales = totalCashFromSales;
        this.servicesRevenue = servicesRevenue;
        this.totalValueOfAddedStock = totalValueOfAddedStock;
        this.totalMpesaFromSales = totalMpesaFromSales;
    }

    public Object getId() {
        return id.get();
    }

    public SimpleObjectProperty<Object> idProperty() {
        return id;
    }

    public void setId(Object id) {
        this.id.set(id);
    }

    public boolean isLightTheme() {
        return lightTheme.get();
    }

    public SimpleBooleanProperty lightThemeProperty() {
        return lightTheme;
    }

    public void setLightTheme(boolean lightTheme) {
        this.lightTheme.set(lightTheme);
    }

    public double getCurrentCash() {
        return currentCash.get();
    }

    public SimpleDoubleProperty currentCashProperty() {
        return currentCash;
    }

    public void setCurrentCash(double currentCash) {
        this.currentCash.set(currentCash);
    }

    public double getCurrentMpesa() {
        return currentMpesa.get();
    }

    public SimpleDoubleProperty currentMpesaProperty() {
        return currentMpesa;
    }

    public void setCurrentMpesa(double currentMpesa) {
        this.currentMpesa.set(currentMpesa);
    }

    public double getCurrentStock() {
        return currentStock.get();
    }

    public SimpleDoubleProperty currentStockProperty() {
        return currentStock;
    }

    public void setCurrentStock(double currentStock) {
        this.currentStock.set(currentStock);
    }

    public int getServicesNumber() {
        return servicesNumber.get();
    }

    public SimpleIntegerProperty servicesNumberProperty() {
        return servicesNumber;
    }

    public void setServicesNumber(int servicesNumber) {
        this.servicesNumber.set(servicesNumber);
    }

    public double getTotalCashFromSales() {
        return totalCashFromSales.get();
    }

    public SimpleDoubleProperty totalCashFromSalesProperty() {
        return totalCashFromSales;
    }

    public void setTotalCashFromSales(double totalCashFromSales) {
        this.totalCashFromSales.set(totalCashFromSales);
    }

    public double getServicesRevenue() {
        return servicesRevenue.get();
    }

    public SimpleDoubleProperty servicesRevenueProperty() {
        return servicesRevenue;
    }

    public void setServicesRevenue(double servicesRevenue) {
        this.servicesRevenue.set(servicesRevenue);
    }

    public double getTotalValueOfAddedStock() {
        return totalValueOfAddedStock.get();
    }

    public SimpleDoubleProperty totalValueOfAddedStockProperty() {
        return totalValueOfAddedStock;
    }

    public void setTotalValueOfAddedStock(double totalValueOfAddedStock) {
        this.totalValueOfAddedStock.set(totalValueOfAddedStock);
    }

    public double getTotalMpesaFromSales() {
        return totalMpesaFromSales.get();
    }

    public SimpleDoubleProperty totalMpesaFromSalesProperty() {
        return totalMpesaFromSales;
    }

    public void setTotalMpesaFromSales(double totalMpesaFromSales) {
        this.totalMpesaFromSales.set(totalMpesaFromSales);
    }
}
