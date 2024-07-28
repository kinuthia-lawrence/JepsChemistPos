package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private AnchorPane anchorPaneLeft;

    @FXML
    private AnchorPane anchorPaneRight;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField usernameTextField;

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
        if (!usernameTextField.getText().isBlank() && !passwordField.getText().isBlank()) {
            String usernameInput = usernameTextField.getText();
            String passwordInput = passwordField.getText();
            checkLoginCredentials(usernameInput, passwordInput);
        } else {
            errorLabel.setText("Please enter your email and password !!!");
        }
    }

    private void checkLoginCredentials(String usernameInput, String passwordInput) {
        DatabaseConn connectNow = new DatabaseConn();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM users WHERE username = '" + usernameInput + "' AND password = '" + passwordInput + "'";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);
            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    loadDashboard();
                    Stage loginWindow = (Stage) loginButton.getScene().getWindow();
                    loginWindow.close();
                } else {
                    passwordField.clear();
                    usernameTextField.clear();
                    errorLabel.setText("Invalid Login. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Dashboard.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load());
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(dashboardScene);
            dashboardStage.show();
        } catch (IOException e) {
            System.out.println("Error loading dashboard");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        launchImages();
    }

    private void launchImages() {
        //? background image for the login page
        Image backgroundImage = new Image(getClass().getResourceAsStream("/IMAGES/pharmacy3.jpg"));
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, null, null, null);
        mainPane.setBackground(new Background(bgImage));
        //? logo of the login page
        Image logoImage = new Image(getClass().getResourceAsStream("/IMAGES/microscope.jpg"));
        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, true);
        BackgroundImage logo = new BackgroundImage(logoImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
        imageAnchorPane.setBackground(new Background(logo));
    }
}