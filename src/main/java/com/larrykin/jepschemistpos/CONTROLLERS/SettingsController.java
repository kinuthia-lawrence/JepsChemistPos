package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.ENUMS.ROLE;
import com.larrykin.jepschemistpos.MODELS.Supplier;
import com.larrykin.jepschemistpos.MODELS.User;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import com.larrykin.jepschemistpos.UTILITIES.PrintingManager;
import com.larrykin.jepschemistpos.UTILITIES.SceneManager;
import com.larrykin.jepschemistpos.UTILITIES.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SettingsController {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    @FXML
    private Button activateAddUser;

    @FXML
    private Button activateChangePassword;

    @FXML
    private Button addSupplierButton;

    @FXML
    private AnchorPane addSupplierPane;

    @FXML
    private Button addUserButton;

    @FXML
    private PasswordField auConfirmPasswordField;

    @FXML
    private TextField auEmailAccountToCreate;

    @FXML
    private PasswordField auPasswordField;

    @FXML
    private TextField auSuperAdminEmail;

    @FXML
    private PasswordField auSuperAdminPassword;

    @FXML
    private TextField auUsernameForTheAccount;

    @FXML
    private Button changePasswordButton;

    @FXML
    private PasswordField cpConfirmPasswordField;

    @FXML
    private TextField cpEmailAccountToChangePassword;

    @FXML
    private PasswordField cpNewPasswordField;

    @FXML
    private PasswordField cpOldPassword;

    @FXML
    private TextField cpSuperAdminEmail;

    @FXML
    private PasswordField cpSuperAdminPassword;

    @FXML
    private ToggleButton enableAutoConfirm;

    @FXML
    private ToggleButton enablePrintingToggle;

    @FXML
    private ImageView iconTheme;

    @FXML
    private AnchorPane modifyUserPane;

    @FXML
    private TextArea supplierContactInformation;

    @FXML
    private TextField supplierName;

    @FXML
    private TableView<Supplier> suppliersTable;

    @FXML
    private ToggleButton themeToggleButton;

    @FXML
    private Button viewUsersButton;

    //?instantiate database
    private DatabaseConn databaseConn = new DatabaseConn();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @FXML
    public void initialize() {
        initializeTable();
        initializeButtons();
        setIcon();
        updatePrintingToggleText();
        updateAutoConfirmText();
    }

    //?update printing enabled toggle
    private void updatePrintingToggleText() {
        boolean isPrintEnabled = PrintingManager.loadPrinterState();
        enablePrintingToggle.setSelected(isPrintEnabled);
        if (isPrintEnabled) {
            enablePrintingToggle.setText("Printing Enabled");
        } else {
            enablePrintingToggle.setText("Printing Disabled");
        }
    }

    //?update auto confirm text
    private void updateAutoConfirmText() {
        boolean isAutoConfirmEnabled = PrintingManager.loadAutoConfirmState();
        enableAutoConfirm.setSelected(isAutoConfirmEnabled);
        if (isAutoConfirmEnabled) {
            enableAutoConfirm.setText("Auto Confirm Enabled");
        } else {
            enableAutoConfirm.setText("Auto Confirm Disabled");
        }
    }

    //?initialize buttons
    private void initializeButtons() {
        // Toggle the theme when the button is clicked
        themeToggleButton.setOnAction(event -> {
            boolean theme = ThemeManager.loadThemeState();
            boolean isDarkMode = !theme;
            applyThemeToAllScenes(isDarkMode);
            ThemeManager.saveThemeState(isDarkMode);
            setIcon();
        });
        // Toggle the printing state when the button is clicked
        enablePrintingToggle.setOnAction(event -> {
            boolean isPrintEnabled = PrintingManager.loadPrinterState();
            boolean printState = !isPrintEnabled;
            PrintingManager.savePrinterState(printState);
            enablePrintingToggle.setSelected(printState);
            updatePrintingToggleText();
        });
        enableAutoConfirm.setOnAction(event -> {
            boolean isAutoConfirmEnabled = PrintingManager.loadAutoConfirmState();
            boolean autoConfirmState = !isAutoConfirmEnabled;
            PrintingManager.saveAutoConfirmState(autoConfirmState);
            enableAutoConfirm.setSelected(autoConfirmState);
            updateAutoConfirmText();
        });

        //activate change password
        activateChangePassword.setOnAction(event -> {
            cpSuperAdminEmail.setDisable(false);
            cpSuperAdminPassword.setDisable(false);
            cpEmailAccountToChangePassword.setDisable(false);
            cpOldPassword.setDisable(false);
            cpNewPasswordField.setDisable(false);
            cpConfirmPasswordField.setDisable(false);
            changePasswordButton.setDisable(false);
        });

        //activate add user
        activateAddUser.setOnAction(event -> {
            auSuperAdminEmail.setDisable(false);
            auSuperAdminPassword.setDisable(false);
            auEmailAccountToCreate.setDisable(false);
            auUsernameForTheAccount.setDisable(false);
            auPasswordField.setDisable(false);
            auConfirmPasswordField.setDisable(false);
            addUserButton.setDisable(false);
        });
        //add supplier
        addSupplierButton.setOnAction(event -> {
            addSupplier();
        });
        //change password
        changePasswordButton.setOnAction(event -> {
            changePassword();
        });
        //add user
        addUserButton.setOnAction(event -> {
            addUser();
        });
        //view users
        viewUsersButton.setOnAction(event -> showUserList());
    }

    //?Add a user
    private void addUser() {
        //check if the fields are not empty
        String superAdminEmail = auSuperAdminEmail.getText();
        String superAdminPassword = auSuperAdminPassword.getText();
        String emailAccountToCreate = auEmailAccountToCreate.getText();
        String usernameForTheAccount = auUsernameForTheAccount.getText();
        String password = auPasswordField.getText();
        String confirmPassword = auConfirmPasswordField.getText();

        //check if the fields are not empty
        if (!superAdminEmail.isBlank() && !superAdminPassword.isBlank() && !emailAccountToCreate.isBlank() && !usernameForTheAccount.isBlank() && !password.isBlank() && !confirmPassword.isBlank()) {


            //check if the new password and confirm password are the same
            if (password.equals(confirmPassword)) {
                //check if the super admin email and password are correct
                String sql = "SELECT password FROM users WHERE email = ? AND role = '" + ROLE.ADMIN + "'";
                try (
                        //check if the super admin email, username = admin and password are correct
                        Connection conn = databaseConn.getConnection();
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                ) {
                    preparedStatement.setString(1, superAdminEmail);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String hashedPassword = resultSet.getString("password");
                        if (passwordEncoder.matches(superAdminPassword, hashedPassword)) {
                            conn.close();
                            //check if the email account to create does not exist
                            Connection conn2 = databaseConn.getConnection();
                            String sql2 = "SELECT * FROM users WHERE email = ?";
                            PreparedStatement preparedStatement2 = conn2.prepareStatement(sql2);
                            preparedStatement2.setString(1, emailAccountToCreate);
                            ResultSet resultSet2 = preparedStatement2.executeQuery();
                            if (!resultSet2.next()) {
                                conn2.close();
                                //add the user
                                addUserToDatabase(emailAccountToCreate, usernameForTheAccount, passwordEncoder.encode(password));
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error Adding User");
                                alert.setHeaderText("Email Account to create already exists");
                                alert.showAndWait();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error Adding User");
                            alert.setHeaderText("Super Admin Password is incorrect");
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Adding User");
                        alert.setHeaderText("Super Admin Email is incorrect");
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    System.out.println("Error(sql): " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Adding User");
                alert.setHeaderText("Password and Confirm Password do not match");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Adding User");
            alert.setHeaderText("Please check the fields and try again");
            alert.showAndWait();
        }
    }

    private void addUserToDatabase(String emailAccountToCreate, String usernameForTheAccount, String password) {
        String sql = "INSERT INTO users(email, username, role, password) VALUES(?, ?, ?, ?)";
        try (Connection conn = databaseConn.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, emailAccountToCreate);
            preparedStatement.setString(2, usernameForTheAccount);
            preparedStatement.setString(3, ROLE.USER.toString());
            preparedStatement.setString(4, password);
            int output = preparedStatement.executeUpdate();
            if (output > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Added");
                alert.setHeaderText("User Added Successfully");
                TimeUnit.SECONDS.sleep(2);
                alert.showAndWait();
                auSuperAdminEmail.clear();
                auSuperAdminPassword.clear();
                auEmailAccountToCreate.clear();
                auUsernameForTheAccount.clear();
                auPasswordField.clear();
                auConfirmPasswordField.clear();
                //
                auSuperAdminEmail.setDisable(true);
                auConfirmPasswordField.setDisable(true);
                auEmailAccountToCreate.setDisable(true);
                auUsernameForTheAccount.setDisable(true);
                auPasswordField.setDisable(true);
                auConfirmPasswordField.setDisable(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error(sql) Adding User");
                alert.setHeaderText("Error Adding User");
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println("Error(sql): " + e.getMessage());
            e.printStackTrace();
        }
    }

    //?Change password
    private void changePassword() {
        //check if the fields are not empty
        String superAdminEmail = cpSuperAdminEmail.getText();
        String superAdminPassword = cpSuperAdminPassword.getText();
        String emailAccountToChangePassword = cpEmailAccountToChangePassword.getText();
        String oldPassword = cpOldPassword.getText();
        String newPassword = cpNewPasswordField.getText();
        String confirmPassword = cpConfirmPasswordField.getText();
        if (!superAdminEmail.isBlank() && !superAdminPassword.isBlank() && !emailAccountToChangePassword.isBlank() && !oldPassword.isBlank() && !newPassword.isBlank() && !confirmPassword.isBlank()) {
            //check if the new password and confirm password are the same
            if (newPassword.equals(confirmPassword)) {
                //check if the super admin email and password are correct
                String sql = "SELECT password FROM users WHERE  role = ? AND email = ? ";
                try (
                        Connection conn = databaseConn.getConnection();
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                ) {
                    preparedStatement.setString(1, ROLE.ADMIN.toString());
                    preparedStatement.setString(2, superAdminEmail);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String hashedPassword = resultSet.getString("password");
                        if (passwordEncoder.matches(superAdminPassword, hashedPassword)) {
                            conn.close();
                            //check if the email account to change password exists
                            Connection conn2 = databaseConn.getConnection();
                            String sql2 = "SELECT * FROM users WHERE email = ?";
                            PreparedStatement preparedStatement2 = conn2.prepareStatement(sql2);
                            preparedStatement2.setString(1, emailAccountToChangePassword);
                            ResultSet resultSet2 = preparedStatement2.executeQuery();
                            if (resultSet2.next()) {
                                conn2.close();
                                //check if the old password is correct
                                Connection conn3 = databaseConn.getConnection();
                                String sql3 = "SELECT password FROM users WHERE email = ?";
                                PreparedStatement preparedStatement3 = conn3.prepareStatement(sql3);
                                preparedStatement3.setString(1, emailAccountToChangePassword);
                                ResultSet resultSet3 = preparedStatement3.executeQuery();
                                if (resultSet3.next()) {
                                    String hashedPassword2 = resultSet3.getString("password");
                                    if (passwordEncoder.matches(oldPassword, hashedPassword2)) {
                                        conn3.close();
                                        //change the password
                                        changePasswordInDatabase(passwordEncoder.encode(newPassword), emailAccountToChangePassword);
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error Changing Password");
                                        alert.setHeaderText("Old Password is incorrect");
                                        alert.showAndWait();
                                    }
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error Changing Password");
                                    alert.setHeaderText("No user with such account.");
                                    alert.showAndWait();
                                }
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error Changing Password");
                                alert.setHeaderText("Email Account to change password does not exist");
                                alert.showAndWait();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error Changing Password");
                            alert.setHeaderText("Super Admin Password is incorrect");
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Changing Password");
                        alert.setHeaderText("Super Admin Email is incorrect");
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    System.out.println("Error(sql): " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Changing Password");
                alert.setHeaderText("New Password and Confirm Password do not match");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Changing Password");
            alert.setHeaderText("Please check the fields and try again");
            alert.showAndWait();
        }
    }

    private void changePasswordInDatabase(String newPassword, String emailAccountToChangePassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = databaseConn.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, emailAccountToChangePassword);
            int output = preparedStatement.executeUpdate();
            if (output > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Changed");
                alert.setHeaderText("Password Changed Successfully");
                TimeUnit.SECONDS.sleep(2);
                alert.showAndWait();
                cpSuperAdminEmail.clear();
                cpSuperAdminPassword.clear();
                cpEmailAccountToChangePassword.clear();
                cpOldPassword.clear();
                cpNewPasswordField.clear();
                cpConfirmPasswordField.clear();
                //disable fields
                cpSuperAdminPassword.setDisable(true);
                cpSuperAdminEmail.setDisable(true);
                cpEmailAccountToChangePassword.setDisable(true);
                cpOldPassword.setDisable(true);
                cpNewPasswordField.setDisable(true);
                cpConfirmPasswordField.setDisable(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error(sql) Changing Password");
                alert.setHeaderText("Error Changing Password");
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println("Error(sql): " + e.getMessage());
            e.printStackTrace();
        }
    }

    //?Add a supplier
    private void addSupplier() {
        String supplierNameText = supplierName.getText();
        String supplierContactInformationText = supplierContactInformation.getText();
        if (!supplierNameText.isBlank() && !supplierContactInformationText.isBlank()) {
            String sql = "INSERT INTO suppliers(name, contact_information) VALUES(?, ?)";
            try (Connection conn = databaseConn.getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ) {
                preparedStatement.setString(1, supplierNameText);
                preparedStatement.setString(2, supplierContactInformationText);
                int output = preparedStatement.executeUpdate();
                if (output > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Supplier Added");
                    alert.setHeaderText("Supplier Added Successfully");
                    TimeUnit.SECONDS.sleep(2);
                    alert.showAndWait();

                    conn.close();
                    populateSuppliersTable();
                    supplierName.clear();
                    supplierContactInformation.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error(sql) Adding Supplier");
                    alert.setHeaderText("Error Adding Supplier");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                log.error("Error Saving Supplier{}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Adding Supplier");
            alert.setHeaderText("Please check the fields and try again");
            alert.showAndWait();
        }
    }

    //?columns for supplierName, supplierContactInformation, editButton and DeleteButton
    private void initializeTable() {
        //supplierName
        TableColumn<Supplier, String> supplierNameColumn = new TableColumn<>("Supplier Name");
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierNameColumn.setPrefWidth(120);
        //contact information
        TableColumn<Supplier, String> supplierContactInformationColumn = new TableColumn<>("Contact Information");
        supplierContactInformationColumn.setCellValueFactory(new PropertyValueFactory<>("supplierContactInformation"));
        supplierContactInformationColumn.setPrefWidth(200);
        //editButton
        TableColumn<Supplier, String> editColumn = new TableColumn<>("   Edit ");
        editColumn.setPrefWidth(100);  // Set a fixed width
        editColumn.setCellFactory(param -> new TableCell<Supplier, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    if (DashboardController.loggedInUser.getRole() == ROLE.ADMIN) {
                        editRow(supplier);
                    } else {
                        Alert userAlert = new Alert(Alert.AlertType.ERROR);
                        userAlert.setTitle("Error");
                        userAlert.setHeaderText("You are not authorized to Edit records");
                        userAlert.setContentText("Only Admins can Edit records");
                        userAlert.showAndWait();
                    }
                });
            }


            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
        //delete column
        TableColumn<Supplier, String> deleteColumn = new TableColumn<>("  Delete ");
        deleteColumn.setPrefWidth(100);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Supplier, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    if (DashboardController.loggedInUser.getRole() == ROLE.ADMIN) {
                        deleteRow(supplier);
                    } else {
                        Alert userAlert = new Alert(Alert.AlertType.ERROR);
                        userAlert.setTitle("Error");
                        userAlert.setHeaderText("You are not authorized to delete records");
                        userAlert.setContentText("Only Admins can delete records");
                        userAlert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        suppliersTable.getColumns().addAll(supplierNameColumn, supplierContactInformationColumn, editColumn, deleteColumn);
        populateSuppliersTable();
    }

    //? edit a supplier
    private void editRow(Supplier supplier) {
        //getting the old supplier name and contact information
        if (supplier != null) {
            try {
                String oldSupplierName = supplier.getSupplierName();
                String oldSupplierContactInformation = supplier.getSupplierContactInformation();
                //setting the old supplier name and contact information to the text fields
                supplierName.setText(oldSupplierName);
                supplierContactInformation.setText(oldSupplierContactInformation);

                //updating the supplier
                addSupplierButton.setText("Update Supplier");
                addSupplierButton.setOnAction(event -> {
                    //getting the new supplier name and contact information
                    String newSupplierName = supplierName.getText();
                    String newSupplierContactInformation = supplierContactInformation.getText();

                    //check if name and contact info. is black if not, update the supplier
                    if (!newSupplierName.isBlank() || !newSupplierContactInformation.isBlank()) {
                        String sql = "UPDATE suppliers SET name = ?, contact_information = ? WHERE name = ?";
                        try (Connection conn = databaseConn.getConnection();
                             PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        ) {
                            preparedStatement.setString(1, newSupplierName);
                            preparedStatement.setString(2, newSupplierContactInformation);
                            preparedStatement.setString(3, oldSupplierName);
                            int output = preparedStatement.executeUpdate();
                            if (output > 0) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Supplier Updated");
                                alert.setHeaderText("Supplier Updated Successfully");
                                TimeUnit.SECONDS.sleep(2);
                                alert.showAndWait();

                                conn.close();

                                populateSuppliersTable();
                                supplierName.clear();
                                supplierContactInformation.clear();
                                addSupplierButton.setText("Add Supplier");
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error(sql) Updating Supplier");
                                alert.setHeaderText("Error Updating Supplier");
                                alert.showAndWait();
                            }
                        } catch (Exception e) {
                            log.error("Error updateing supplier{}", e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Updating Supplier");
                        alert.setHeaderText("Please check the fields and try again");
                        alert.showAndWait();
                    }
                });

            } catch (Exception e) {
                log.error("Error editing supplier::{}", e.getMessage());
                e.printStackTrace();
            }
        }

    }

    //? delete a supplier
    private void deleteRow(Supplier supplier) {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete Supplier");
        deleteAlert.setHeaderText("Are you sure you want to delete " + supplier.getSupplierName());
        deleteAlert.setContentText("This Action is irreversible!!");
        deleteAlert.showAndWait();

        if (deleteAlert.getResult() != ButtonType.OK) {
            deleteAlert.close();
            return;
        }
        String deleteQuery = "DELETE FROM suppliers WHERE name=?";
        try (
                Connection connection = databaseConn.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
        ) {
            preparedStatement.setObject(1, supplier.getSupplierName());
            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Supplier deleted successfully");
                alert.setContentText("Supplier deleted successfully");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                timeline.setCycleCount(1);
                timeline.play();
                alert.showAndWait();
                //? On success Methods
                connection.close();
                populateSuppliersTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error deleting Supplier");
                alert.setContentText("Error deleting Supplier");
                alert.showAndWait();
            }

        } catch (Exception e) {
            System.out.println("Error (sql):" + e.getMessage());
            e.printStackTrace();
        }
    }

    //? Populate the suppliers table
    private void populateSuppliersTable() {
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        suppliers.addAll(getSuppliers());
        suppliersTable.setItems(suppliers);
    }

    //?get supplier from the database
    private List<Supplier> getSuppliers() {
        List<Supplier> supplierList = new ArrayList<>();

        try (
                Connection conn = databaseConn.getConnection();
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM suppliers");
        ) {
            while (resultSet.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierName(resultSet.getString("name"));
                supplier.setSupplierContactInformation(resultSet.getString("contact_information"));
                supplierList.add(supplier);
            }

        } catch (Exception e) {
            log.error("Error fetching Suppliers{}", e.getMessage());
            e.printStackTrace();
        }
        return supplierList;
    }

    //?set the icon
    private void setIcon() {
        boolean darkMode = ThemeManager.loadThemeState();
        if (!darkMode) {
            themeToggleButton.setText("Dark Mode");
            Image dark_theme = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/dark_theme.png")));
            iconTheme.setImage(dark_theme);
        } else {
            themeToggleButton.setText("Light Mode");
            Image light_theme = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/light_theme.png")));
            iconTheme.setImage(light_theme);
        }
    }

    //? Apply the theme to all scenes
    private void applyThemeToAllScenes(boolean isDarkMode) {
        Platform.runLater(() -> {
            if (themeToggleButton.getScene() != null) {
                Stage stage = (Stage) themeToggleButton.getScene().getWindow();
                Scene scene = stage.getScene();
                ThemeManager.applyTheme(scene, isDarkMode);

                // Apply theme to other scenes if they are already created
                for (Scene otherScene : SceneManager.getAllScenes()) {
                    ThemeManager.applyTheme(otherScene, isDarkMode);
                }
            }
        });
    }

    //?show user list

    private void showUserList() {
        Stage userStage = new Stage();
        userStage.setTitle("User List");

        TableView<User> userTableView = new TableView<>();
        ObservableList<User> users = FXCollections.observableArrayList(getUsersFromDatabase());

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(200);

        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(100);

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.setPrefWidth(50);

        //delete column
        TableColumn<User, String> deleteColumn = new TableColumn<>("  Delete ");
        deleteColumn.setPrefWidth(75);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<User, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (DashboardController.loggedInUser.getRole() == ROLE.ADMIN) {
                        deleteUser(user);
                        //?refresh the table
                        users.clear();
                        users.addAll(getUsersFromDatabase());
                    } else {
                        Alert userAlert = new Alert(Alert.AlertType.ERROR);
                        userAlert.setTitle("Error");
                        userAlert.setHeaderText("You are not authorized to delete records");
                        userAlert.setContentText("Only Admins can delete records");
                        userAlert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });


        userTableView.getColumns().addAll(emailColumn, usernameColumn, roleColumn, deleteColumn);
        userTableView.setItems(users);

        VBox vbox = new VBox(userTableView);
        Scene scene = new Scene(vbox, 425, 300);
        userStage.setScene(scene);
        userStage.show();
    }

    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        String query = "SELECT email, username, role FROM users";
        try (Connection connection = databaseConn.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                User user = new User(resultSet.getString("username"), resultSet.getString("email"), ROLE.valueOf(resultSet.getString("role")));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private void deleteUser(User user) {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete User");
        deleteAlert.setHeaderText("Are you sure you want to delete " + user.getUsername());
        deleteAlert.setContentText("This Action is irreversible!!");
        deleteAlert.showAndWait();

        if (deleteAlert.getResult() != ButtonType.OK) {
            deleteAlert.close();
            return;
        }
        String deleteQuery = "DELETE FROM users WHERE email=?";
        try (
                Connection connection = databaseConn.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
        ) {
            preparedStatement.setObject(1, user.getEmail()); // set the value of the first parameter to email
            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("User deleted successfully");
                alert.setContentText("User deleted successfully");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                timeline.setCycleCount(1);
                timeline.play();
                alert.showAndWait();
                //? On success Methods
                connection.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error deleting User");
                alert.setContentText("Error deleting User");
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.error("Error Deleting User{}", e.getMessage());
            e.printStackTrace();
        }
    }
}