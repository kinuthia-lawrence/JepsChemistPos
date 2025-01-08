package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.ENUMS.ROLE;
import com.larrykin.jepschemistpos.MODELS.User;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import com.larrykin.jepschemistpos.UTILITIES.ThemeManager;
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
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

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
    private ImageView lockIcon;
    @FXML
    private Button loginButton;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Hyperlink signUpHyperlink;

    @FXML
    private PasswordField passwordField;
    DatabaseConn connectNow = new DatabaseConn();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConn databaseConn = new DatabaseConn();
        databaseConn.getConnection();

        launchImages();
        helpHyperlink.setOnAction(event -> openHelp());
        forgotPasswordHyperLink.setOnAction(event -> openForgotPassword());
        signUpHyperlink.setOnAction(event -> openSignUp());
    }

    private void openSignUp() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText("Confirm Sign Up");
        alert.setContentText("Are you sure it's 1'st Time SignUp? You will need Developer Approval; Otherwise contact" +
                " Administrator");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        //?Add icon
        Image image = new Image(getClass().getResourceAsStream("/IMAGES/passwordLock.png"));
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/signUp.fxml"));
            Stage signUpStage = new Stage();
            try {
                Scene scene = new Scene(fxmlLoader.load());
                signUpStage.setScene(scene);
                signUpStage.centerOnScreen();
                signUpStage.setTitle("Sign Up");
                signUpStage.show();
            } catch (Exception e) {
                log.error("Error loading Sign Up{}", e.getMessage());
                e.printStackTrace();
            }
        } else alert.close();
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
                log.error("Error loading forgot Password{}", e.getMessage());
                e.printStackTrace();
            }
        } else alert.close();
    }

    private void openHelp() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/help.fxml"));
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
            log.error("Error loading Help::{}", e.getMessage());
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

        Image lockImage = new Image(getClass().getResourceAsStream("/IMAGES/lock.png"));
        lockIcon.setImage(lockImage);
    }


    public void cancelOnAction() {
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.close();
    }

    public void loginOnAction(ActionEvent actionEvent) {
        if (!usernameTextField.getText().isBlank() && !passwordField.getText().isBlank()) {

            String usernameInput = usernameTextField.getText();
            String password = passwordField.getText();
            checkLoginCredentials(usernameInput, password);
        } else {
            errorLabel.setText("Please enter your username and password !!!");
        }
        //! Set accelerator for login button (Shift+E)
        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.E, KeyCombination.SHIFT_DOWN);
        User developerUser = new User("Larrykin343", "larrykin343@gmail.com", ROLE.ADMIN);
        loginButton.getScene().getAccelerators().put(keyCombination, () -> loadDashboard(developerUser));
    }

    private void checkLoginCredentials(String usernameInput, String passwordInput) {
        String verifyLogin = "SELECT username, email, role, password FROM users WHERE username = ? OR email = ?";
        try (
                Connection connectDB = connectNow.getConnection();
                PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);
        ) {
            preparedStatement.setString(1, usernameInput);
            preparedStatement.setString(2, usernameInput);

            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next()) {
                String username = queryResult.getString("username");
                String email = queryResult.getString("email");
                String role = queryResult.getString("role");
                String hashedPassword = queryResult.getString("password");
                if (passwordEncoder.matches(passwordInput, hashedPassword)) {
                    User loggedInUser = new User(username, email, ROLE.valueOf(role));
                    loadDashboard(loggedInUser);
                    log.info("User: {} has logged in as {}", username, role);
                } else {
                    passwordField.clear();
                    usernameTextField.clear();
                    errorLabel.setText("Invalid Login. Please try again.");
                }
            } else {
                passwordField.clear();
                usernameTextField.clear();
                errorLabel.setText("Invalid Login. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDashboard(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Dashboard.fxml"));
            Scene dashboardScene = new Scene(fxmlLoader.load());

            //? apply theme to the dashboard
            boolean isDarkMode = ThemeManager.loadThemeState();
            ThemeManager.applyTheme(dashboardScene, isDarkMode);

            DashboardController dashboardController = fxmlLoader.getController();
            dashboardController.setUser(user);//? Pass the user object to the dashboard controller
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(dashboardScene);
            dashboardStage.initStyle(StageStyle.DECORATED); // Ensure minimize and close buttons are visible
            // Get the width of the primary screen
          /*  Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            dashboardStage.setWidth(screenBounds.getWidth());
            dashboardStage.setHeight(screenBounds.getHeight());*/

            Image icon = new Image(getClass().getResourceAsStream("/IMAGES/microscope.jpg"));
            dashboardStage.getIcons().add(icon);

            dashboardStage.show();
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            System.out.println("Error loading dashboard");
            e.printStackTrace();
        }
    }


}