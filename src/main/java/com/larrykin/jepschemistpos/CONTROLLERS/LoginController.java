package com.larrykin.jepschemistpos.CONTROLLERS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private AnchorPane anchorPaneLeft;

    @FXML
    private AnchorPane anchorPaneRight;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private Hyperlink forgotPasswordHyperLink;

    @FXML
    private Hyperlink helpHyperlink;

    @FXML
    private AnchorPane imageAnchorPane;

    @FXML
    private Button loginButton;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private PasswordField passwordField;


    public void cancelOnAction() {
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.close();
    }

    public void loginOnAction(ActionEvent actionEvent) {
        if(!emailTextField.getText().isBlank() && !passwordField.getText().isBlank()){
            System.out.println("Login Successful");
        }else {
            errorLabel.setText("Please enter your email and password !!!");
        }
    }
}
