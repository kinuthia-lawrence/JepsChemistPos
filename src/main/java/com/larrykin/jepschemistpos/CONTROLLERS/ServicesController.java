package com.larrykin.jepschemistpos.CONTROLLERS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ServicesController {

    @FXML
    private TableColumn<?, ?> cashPaymentColumn;

    @FXML
    private TextField cashPaymentTextField;

    @FXML
    private TableColumn<?, ?> dateColumn;

    @FXML
    private TableColumn<?, ?> descriptionColumn;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> mpesaPaymentColumn;

    @FXML
    private TextField mpesaPaymentTextField;

    @FXML
    private Button saveButton;

    @FXML
    private TableColumn<?, ?> serviceNameColumn;

    @FXML
    private TextField serviceNameTextField;

}
