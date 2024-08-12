package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Sales;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SalesController {

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
    private Spinner<Double> mpesaSpinner;

    @FXML
    private Button salesButton;

    @FXML
    private Button sellButton;

    @FXML
    private Button stockButton;

    @FXML
    private ScrollPane tableScrollPane;
    @FXML
    public void initialize(){
        loadSpinners();
        loadSalesTable();
        loadStockTable();

    }

    private void loadStockTable() {
    }

    private void loadSalesTable() {
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
