package com.larrykin.jepschemistpos.CONTROLLERS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class StockController {

    @FXML
    private Button addStockButton;

    @FXML
    private Spinner<?> buyingPriceSpinner;

    @FXML
    private ComboBox<?> categoryCombobox;

    @FXML
    private DatePicker expiryDatePicker;

    @FXML
    private ComboBox<?> nameComboBox;

    @FXML
    private TextArea optionalDescription;

    @FXML
    private Spinner<?> quantitySpinner;

    @FXML
    private Button saveButton;

    @FXML
    private Spinner<?> sellingPriceSpinner;

    @FXML
    private TableView<?> stockTable;

    @FXML
    private ComboBox<?> supplierComboBox;

}
