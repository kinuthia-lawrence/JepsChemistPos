package com.larrykin.jepschemistpos.MODELS;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Service {
    private SimpleObjectProperty<Object> serviceID;
    private SimpleStringProperty date;
    private SimpleStringProperty patientName;
    private SimpleDoubleProperty age;
    private SimpleStringProperty gender;
    private SimpleStringProperty residence;
    private SimpleStringProperty contactInfo;
    private SimpleStringProperty description;
    private SimpleDoubleProperty cashPayment;
    private SimpleDoubleProperty mpesaPayment;

    public Service(SimpleDoubleProperty mpesaPayment, SimpleDoubleProperty cashPayment, SimpleStringProperty description, SimpleStringProperty patientName, SimpleStringProperty date, SimpleObjectProperty<Object> serviceID,
                   SimpleStringProperty residence, SimpleStringProperty contactInfo, SimpleStringProperty gender, SimpleDoubleProperty age) {
        this.mpesaPayment = mpesaPayment;
        this.cashPayment = cashPayment;
        this.description = description;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.contactInfo = contactInfo;
        this.residence = residence;
        this.date = date;
        this.serviceID = serviceID;
    }

    public Service() {
        this.serviceID = new SimpleObjectProperty<>();
        this.date = new SimpleStringProperty("");
        this.patientName = new SimpleStringProperty("");
        this.age = new SimpleDoubleProperty(0.0);
        this.gender = new SimpleStringProperty("");
        this.residence = new SimpleStringProperty("");
        this.contactInfo = new SimpleStringProperty("");
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

    public String getPatientName() {
        return patientName.get();
    }

    public SimpleStringProperty patientNameProperty() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName.set(patientName);
    }

    public double getAge() {
        return age.get();
    }
    public SimpleDoubleProperty ageProperty() {
        return age;
    }
    public void setAge(double age) {
        this.age.set(age);
    }
    public String getGender() {
        return gender.get();
    }
    public SimpleStringProperty genderProperty(){
        return gender;
    }
    public void setGender(String gender){
        this.gender.set(gender);
    }
    public String getResidence() {
        return residence.get();
    }
    public SimpleStringProperty residenceProperty() {
        return residence;
    }
    public void setResidence(String residence) {
        this.residence.set(residence);
    }
    public String getContactInfo() {
        return contactInfo.get();
    }
    public SimpleStringProperty contactInfoProperty() {
        return contactInfo;
    }
    public void setContactInfo(String contactInfo) {
        this.contactInfo.set(contactInfo);
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
