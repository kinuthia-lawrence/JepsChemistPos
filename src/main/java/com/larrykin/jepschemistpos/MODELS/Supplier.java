package com.larrykin.jepschemistpos.MODELS;

public class Supplier {
    private String supplierName;
    private  String supplierContactInformation;

    //constructor
    public Supplier(String supplierName, String supplierContactInformation) {
        this.supplierName = supplierName;
        this.supplierContactInformation = supplierContactInformation;
    }
    public Supplier() {
        this.supplierName = "";
        this.supplierContactInformation = "";
    }
    //getters
    public String getSupplierName() {
        return supplierName;
    }
    public String getSupplierContactInformation() {
        return supplierContactInformation;
    }
    //setters
    public String setSupplierName(String supplierName) {
        return this.supplierName = supplierName;
    }
    public String setSupplierContactInformation(String supplierContactInformation) {
        return this.supplierContactInformation = supplierContactInformation;
    }
}
