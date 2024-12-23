package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.ENUMS.ROLE;
import com.larrykin.jepschemistpos.UTILITIES.CREDENTIALS;
import com.larrykin.jepschemistpos.UTILITIES.CustomAlert;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

public class SignUpController {

    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);
    @FXML
    private Label alertLabel;

    @FXML
    private TextField adminEmail;

    @FXML
    private TextField adminUsername;

    @FXML
    private Label codeAlert;

    @FXML
    private TextField codeTextField;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private ToggleButton developerModeToggle;

    @FXML
    private PasswordField enterPassword;

    @FXML
    private Button getCodeButton;

    @FXML
    private AnchorPane passwordReset;

    @FXML
    private Button saveUser;

    private final CustomAlert customAlert = new CustomAlert();
    DatabaseConn databaseConn = new DatabaseConn();
    private String randomCode;

    @FXML
    public void initialize() {
        codeTextField.requestFocus();
        getCodeButton.setOnAction(event -> {
            log.info("Get code button clicked");
            getCodeButton.setDisable(true);
            signUp();
        });
        saveUser.setOnAction(event -> {
            log.info("Save user button clicked");
            saveUser.setDisable(true);
            createUser(randomCode);
        });
        developerModeToggle.setOnAction(event -> {
            if(developerModeToggle.isSelected()){
                getCodeButton.setDisable(true);
                //? check if there exist registered users
                String listUsers = "SELECT * FROM users";

                try (
                        Connection connection = databaseConn.getConnection();
                        ResultSet queryResults = connection.createStatement().executeQuery(listUsers);
                ) {
                    connection.close();
                    if (queryResults.next()) {
                        alertLabel.setText("Admin already exist, please contact the administrator");
                        alertLabel.setVisible(true);
                        alertLabel.setStyle("-fx-text-fill: red");
                        return;
                    } else {
                        // Generate a random code
                        randomCode = "L@rrykin2547";
                        //set fields to enabled
                        codeTextField.setDisable(false);
                        adminEmail.setDisable(false);
                        adminUsername.setDisable(false);
                        enterPassword.setDisable(false);
                        confirmPassword.setDisable(false);
                        saveUser.setDisable(false);
                    }
                } catch (Exception e) {
                    getCodeButton.setDisable(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error occurred");
                    alert.setContentText("An error occurred while trying to check for registered users");
                    alert.showAndWait();

                    log.error("Error occurred while trying to check for registered users", e);
                }
            }else{
                getCodeButton.setDisable(false);
                alertLabel.setVisible(false);
                codeTextField.setDisable(true);
                adminEmail.setDisable(true);
                adminUsername.setDisable(true);
                enterPassword.setDisable(true);
                confirmPassword.setDisable(true);
                saveUser.setDisable(true);
            }
        });
    }

    private void signUp() {
        getCodeButton.setDisable(true);
        //? check if there exist registered users
        String listUsers = "SELECT * FROM users";

        try (
                Connection connection = databaseConn.getConnection();
                ResultSet queryResults = connection.createStatement().executeQuery(listUsers);
        ) {
            connection.close();
            if (queryResults.next()) {
                alertLabel.setText("Admin already exist, please contact the administrator");
                alertLabel.setVisible(true);
                alertLabel.setStyle("-fx-text-fill: red");
                return;
            } else {
                // Generate a random code
                randomCode = UUID.randomUUID().toString().substring(0, 6);
                try {
                    // Compose the email content
                    String emailContent = "Admin Registration code is: " + randomCode;

                    // Set up the email server properties
                    Properties properties = new Properties();
                    properties.put("mail.smtp.host", CREDENTIALS.getSmtpHost());
                    properties.put("mail.smtp.port", CREDENTIALS.getSmtpPort());
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.socketFactory.port", CREDENTIALS.getSmtpPort());
                    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

                    // Create a new session with an authenticator
                    Authenticator auth = new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(CREDENTIALS.getEmailUsername(), CREDENTIALS.getEmailPassword());
                        }
                    };

                    Session session = Session.getInstance(properties, auth);

                    // Create a new email message
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(CREDENTIALS.getEmailUsername()));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(CREDENTIALS.getEmailUsername()));
                    message.setSubject("Admin Registration Code");
                    message.setText(emailContent);

                    // Send the email
                    Transport.send(message);

                    // inform that code is sent to developer.
                    alertLabel.setText("Code has been sent to Developer, Please contact the developer for the code");
                    alertLabel.setVisible(true);
                    alertLabel.setStyle("-fx-text-fill: green");


                    //set fields to enabled
                    codeTextField.setDisable(false);
                    adminEmail.setDisable(false);
                    adminUsername.setDisable(false);
                    enterPassword.setDisable(false);
                    confirmPassword.setDisable(false);
                    saveUser.setDisable(false);
                } catch (MessagingException e) {
                    getCodeButton.setDisable(false);
                    if (e.getCause() instanceof ConnectException) {
                        customAlert.showAlert("Network Error", "Unable to connect to the email server. " +
                                "Please check your " +
                                "network connection and try again.", Alert.AlertType.ERROR);
                        log.error("Unable to connect to the email server. Please check your network connection and try again.", e);
                    } else if (e.getCause() instanceof UnknownHostException) {
                        customAlert.showAlert("Network Error", "Unable to connect to the email server. Please contact the developer to fix the server address.", Alert.AlertType.ERROR);
                        log.error("Unable to connect to the email server. Please contact the developer to fix the server address.", e);
                    } else if (e instanceof AuthenticationFailedException) {
                        customAlert.showAlert("Authentication Error", "Invalid email username or password. Please contact Developer.", Alert.AlertType.ERROR);
                        log.error("Invalid email username or password. Please contact Developer.", e);
                    } else {
                        customAlert.showAlert("Email Error", "An error occurred while sending the email. Please try again later.", Alert.AlertType.ERROR);
                        log.error("An error occurred while sending the email. Please try again later.", e);
                    }
                } catch (Exception e) {
                    getCodeButton.setDisable(false);
                    customAlert.showAlert("Unexpected Error", "An unexpected error occurred while processing your request. Please try again.", Alert.AlertType.ERROR);
                    log.error("An unexpected error occurred while processing your request. Please try again.", e);
                }
            }
        } catch (Exception e) {
            getCodeButton.setDisable(false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error occurred");
            alert.setContentText("An error occurred while trying to check for registered users");
            alert.showAndWait();

            log.error("Error occurred while trying to check for registered users", e);
        }

    }

    private void createUser(String randomCode) {

        String code = codeTextField.getText();
        String password = enterPassword.getText();
        String passwordConfirm = confirmPassword.getText();
        String username = adminUsername.getText();
        String email = adminEmail.getText();

        // field validation
        if (code.isBlank() || password.isBlank() || passwordConfirm.isBlank() || username.isBlank() || email.isBlank()) {
            codeAlert.setText("Please fill in all fields");
            codeAlert.setVisible(true);
            codeAlert.setStyle("-fx-text-fill: red");
            return;
        }

        log.info("Code{}", code);
        log.info("randomCode{}", randomCode);
        //verify the code
        if (!code.equals(randomCode)) {
                codeAlert.setText("Invalid code");
                codeAlert.setVisible(true);
                saveUser.setDisable(false);
                return;

        }

        //confirm passwords
        if (!password.equals(passwordConfirm)) {
            codeAlert.setText("Passwords do not match");
            codeAlert.setVisible(true);
            saveUser.setDisable(false);
            return;
        }

        //create user query
        String insertUser =
                "INSERT INTO users (username, email, role, password) VALUES ('" + username + "', '" + email + "', '" + ROLE.ADMIN + "', " +
                        "'" + password + "')";

        try (Connection connection = databaseConn.getConnection();
             Statement statement = connection.createStatement();
        ) {
            int result = statement.executeUpdate(insertUser);
            if (result > 0) {
                customAlert.showAlert("Success", "Admin successfully created", Alert.AlertType.INFORMATION);

                // Disable fields
                codeTextField.setDisable(true);
                adminEmail.setDisable(true);
                adminUsername.setDisable(true);
                enterPassword.setDisable(true);
                confirmPassword.setDisable(true);
                saveUser.setDisable(true);
            } else {
                saveUser.setDisable(false);
                customAlert.showAlert("Error", "Error in creating Admin", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            saveUser.setDisable(false);
            customAlert.showAlert("ERROR", "Error in creating Admin :: " + e.getMessage(), Alert.AlertType.ERROR);
            log.error("Error in creating Admin", e);
        }
    }


}
