package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Supplier;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SettingsController {
    @FXML
    private Button addSupplierButton;

    @FXML
    private AnchorPane addSupplierPane;

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

    //?instantiate database
    private DatabaseConn databaseConn = new DatabaseConn();

    @FXML
    public void initialize() {
        initializeTable();

        setIcon();
        // Toggle the theme when the button is clicked
        themeToggleButton.setOnAction(event -> {
            boolean theme = ThemeManager.loadThemeState();
            boolean isDarkMode = !theme;
            applyThemeToAllScenes(isDarkMode);
            ThemeManager.saveThemeState(isDarkMode);
            setIcon();
        });
    }

    //columns for supplierName, supplierContactInformation, editButton and DeleteButton
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
        TableColumn<Supplier, String> editColumn = new TableColumn<>("Edit");
        editColumn.setPrefWidth(100);  // Set a fixed width
        editColumn.setCellFactory(param -> new TableCell<Supplier, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    editRow(supplier);
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
        TableColumn<Supplier, String> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setPrefWidth(100);  // Set a fixed width
        deleteColumn.setCellFactory(param -> new TableCell<Supplier, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    deleteRow(supplier);
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
                        try (Connection conn = databaseConn.getConnection()) {
                            String sql = "UPDATE suppliers SET name = ?, contact_information = ? WHERE name = ?";
                            PreparedStatement preparedStatement = conn.prepareStatement(sql);
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
                            System.out.println("Error(sql): " + e.getMessage());
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
                System.out.println("Error (sql): " + e.getMessage());
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
        try {
            Connection connection = databaseConn.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);

            preparedStatement.setObject(1, supplier.getSupplierName());
            int rowAffected = preparedStatement.executeUpdate();
            if(rowAffected > 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Supplier deleted successfully");
                alert.setContentText("Supplier deleted successfully");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> alert.close()));
                timeline.setCycleCount(1);
                timeline.play();
                alert.showAndWait();
                //? On success Methods
                populateSuppliersTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error deleting Supplier");
                alert.setContentText("Error deleting Supplier");
                alert.showAndWait();
                connection.close();
            }
            connection.close();
        } catch (Exception e) {
            System.out.println("Error (sql):" + e.getMessage());
            e.printStackTrace();
        }
    }

    // Populate the suppliers table
    private void populateSuppliersTable() {
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        suppliers.addAll(getSuppliers());
        suppliersTable.setItems(suppliers);
    }

    //get supplier from the database
    private List<Supplier> getSuppliers() {
        List<Supplier> supplierList = new ArrayList<>();

        try (Connection conn = databaseConn.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM suppliers");

            while (resultSet.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierName(resultSet.getString("name"));
                supplier.setSupplierContactInformation(resultSet.getString("contact_information"));
                supplierList.add(supplier);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return supplierList;
    }

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

    // Apply the theme to all scenes
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
}