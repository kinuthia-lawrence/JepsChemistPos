package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Screen;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        launchImages();
        helpHyperlink.setOnAction(event -> openHelp());
        forgotPasswordHyperLink.setOnAction(event -> openForgotPassword());
    }

    private void openForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Confirm Reset Password");
        alert.setContentText("Are you sure you want to reset your password? This action is irreversible.");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        //?Add icon
        Image image = new Image(getClass().getResourceAsStream("/IMAGES/passwordLock.png"));
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);


        alert.showAndWait();


        if (alert.getResult() == ButtonType.YES) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/forgotPassword.fxml"));
            Stage forgotPasswordStage = new Stage();
            try {
                Scene scene = new Scene(fxmlLoader.load());
                forgotPasswordStage.setScene(scene);
                forgotPasswordStage.centerOnScreen();
                forgotPasswordStage.setTitle("Forgot Password");
                forgotPasswordStage.show();
            } catch (Exception e) {
                System.out.println("Error loading forgot Password" + e.getMessage());
                e.printStackTrace();
            }
        }
        else alert.close();


    }

    private void openHelp() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Help.fxml"));
        Stage helpStage = new Stage();
        try {
            Scene scene = new Scene(fxmlLoader.load());
            helpStage.setScene(scene);
            helpStage.centerOnScreen();
            helpStage.setTitle("Help");
            helpStage.setWidth(1200);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            helpStage.setHeight(screenBounds.getHeight());
            helpStage.show();
        } catch (Exception e) {
            System.out.println("Error loading help" + e.getMessage());
            e.printStackTrace();
        }

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
        //! Set accelerator for login button (Shift+E)
        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.E, KeyCombination.SHIFT_DOWN);
        loginButton.getScene().getAccelerators().put(keyCombination, this::loadDashboard);
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


}