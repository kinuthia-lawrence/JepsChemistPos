package com.larrykin.jepschemistpos.MODELS;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Service {
    private SimpleObjectProperty<Object> serviceID;
    private SimpleStringProperty date;
    private SimpleStringProperty serviceName;
    private SimpleStringProperty description;
    private SimpleDoubleProperty cashPayment;
    private SimpleDoubleProperty mpesaPayment;

    public Service(SimpleDoubleProperty mpesaPayment, SimpleDoubleProperty cashPayment, SimpleStringProperty description, SimpleStringProperty serviceName, SimpleStringProperty date, SimpleObjectProperty<Object> serviceID) {
        this.mpesaPayment = mpesaPayment;
        this.cashPayment = cashPayment;
        this.description = description;
        this.serviceName = serviceName;
        this.date = date;
        this.serviceID = serviceID;
    }

    public Service() {
        this.serviceID = new SimpleObjectProperty<>();
        this.date = new SimpleStringProperty("");
        this.serviceName = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.cashPayment = new SimpleDoubleProperty(0.0);
        this.mpesaPayment = new SimpleDoubleProperty(0.0);
    }

    public Object getServiceID() {
        return serviceID.get();
    }

    public SimpleObjectProperty<Object> serviceIDProperty() {
        return serviceID;
    }

    public void setServiceID(Object serviceID) {
        this.serviceID.set(serviceID);
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

    public String getServiceName() {
        return serviceName.get();
    }

    public SimpleStringProperty serviceNameProperty() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName.set(serviceName);
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

    public double getCashPayment() {
        return cashPayment.get();
    }

    public SimpleDoubleProperty cashPaymentProperty() {
        return cashPayment;
    }

    public void setCashPayment(double cashPayment) {
        this.cashPayment.set(cashPayment);
    }

    public double getMpesaPayment() {
        return mpesaPayment.get();
    }

    public SimpleDoubleProperty mpesaPaymentProperty() {
        return mpesaPayment;
    }

    public void setMpesaPayment(double mpesaPayment) {
        this.mpesaPayment.set(mpesaPayment);
    }
}
