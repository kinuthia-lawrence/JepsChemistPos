package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.ENUMS.DashboardOptions;
import com.larrykin.jepschemistpos.MODELS.Model;
import com.larrykin.jepschemistpos.MODELS.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DashboardController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Hyperlink companyHyperlink;

    @FXML
    private Button homeButton;

    @FXML
    private ImageView homeIcon;

    @FXML
    private Label datetimeLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Button helpButton;

    @FXML
    private ImageView helpIcon;

    @FXML
    private Button inventoryButton;

    @FXML
    private ImageView inventoryIcon;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView logoutIcon;

    @FXML
    private Button notificationButton;

    @FXML
    private ImageView notificationIcon;

    @FXML
    private HBox profileHBox;

    @FXML
    private ImageView profilePic;

    @FXML
    private Button reportButton;

    @FXML
    private ImageView reportIcon;

    @FXML
    private Button salesButton;

    @FXML
    private ImageView salesIcon;

    @FXML
    private ImageView serviceIcon;

    @FXML
    private Button servicesButton;

    @FXML
    private ImageView settingsIcon;

    @FXML
    private VBox sidebar;

    @FXML
    private Button stockButton;

    @FXML
    private ImageView stockIcon;

    @FXML
    private Label usernameLabel;

    private User user;

    @FXML
    public void initialize() {
        loadImages();
        addDateTimeDisplay();
        setUserDetails();
        openCompany();
        loadDashboard();
        addListeners();
        otherActions();

    }


    private void openCompany() {
        if (companyHyperlink == null) {
            System.out.println("companyHyperlink is null");
            return;
        }
        companyHyperlink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://kinuthia-lawrence.github.io/portfolio/"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    public void setUser(User user) {
        this.user = user;
        setUserDetails();
    }

    private void setUserDetails() {
        if (user != null) {
            usernameLabel.setText(user.getUsername());
            emailLabel.setText(user.getEmail());
        }
        // create the spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        // Add the spacer to the VBox before the HBox
        sidebar.getChildren().add(sidebar.getChildren().size() - 1, spacer);

    }

    private void addDateTimeDisplay() {
        // Update the date and time periodically
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM yyyy HH:mm:ss");
            datetimeLabel.setText(now.format(formatter));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadImages() {
        try {


            //? logo of the dashboard page
            Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/microscope.jpg")));
            logoImageView.setImage(logoImage);
            Circle circle = new Circle(logoImageView.getFitWidth() / 2, logoImageView.getFitHeight() / 2, Math.min(logoImageView.getFitWidth(), logoImageView.getFitHeight()) / 2);
            logoImageView.setClip(circle);
            //? profile picture
            Image profilePicture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/user.png")));
            profilePic.setImage(profilePicture);
            Circle avator = new Circle(profilePic.getFitWidth() / 2, profilePic.getFitHeight() / 2, Math.min(profilePic.getFitWidth(), profilePic.getFitHeight()) / 2);
            profilePic.setClip(avator);

            //? buttonIcons
//? settings icon
            Image settingsImage = new Image(getClass().getResourceAsStream("/IMAGES/settings.gif"));
            Image settingsClicked = new Image(getClass().getResourceAsStream("/IMAGES/setting.gif"));

// Set the initial image
            settingsIcon.setImage(settingsImage);

// Add click listener
            settingsIcon.setOnMouseClicked(event -> {
                settingsIcon.setImage(settingsClicked);
                Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.SETTINGS);
            });

// Revert image when another view is opened
            Model.getInstance().getViewFactory().getDashboardSelectedItem().addListener((observable, oldVal, newVal) -> {
                if (newVal != DashboardOptions.SETTINGS) {
                    settingsIcon.setImage(settingsImage);
                }
            });

            Image homeImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/home.png")));
            homeIcon.setImage(homeImage);
            Image salesImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/sales.png")));
            salesIcon.setImage(salesImage);
            Image inventoryImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/inventory.png")));
            inventoryIcon.setImage(inventoryImage);
            Image reportImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/reports.png")));
            reportIcon.setImage(reportImage);

            Image notificationImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/notify.png")));
            notificationIcon.setImage(notificationImage);
//            Image notificationImage = new Image(getClass().getResourceAsStream("/IMAGES/notification.gif"));
//            notificationIcon.setImage(notificationImage);

            Image serviceImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/services.gif")));
            serviceIcon.setImage(serviceImage);
            Image stockImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/stock.png")));
            stockIcon.setImage(stockImage);
            Image helpImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/help.gif")));
            helpIcon.setImage(helpImage);
            Image logoutImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/power.gif")));
            logoutIcon.setImage(logoutImage);
        } catch (Exception ex) {
            System.out.println("Error loading images : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadDashboard() {
        borderPane.setCenter(Model.getInstance().getViewFactory().getHomeAnchorPane());
        Model.getInstance().getViewFactory().getDashboardSelectedItem().addListener((observable, oldVal, newVal) -> {
            switch (newVal) {
                case HOME -> borderPane.setCenter(Model.getInstance().getViewFactory().getHomeAnchorPane());
                case SALES -> borderPane.setCenter(Model.getInstance().getViewFactory().getSaleAnchorPane());
                case STOCK -> borderPane.setCenter(Model.getInstance().getViewFactory().getStockAnchorPane());
                case SERVICES -> borderPane.setCenter(Model.getInstance().getViewFactory().getServicesAnchorPane());
                case NOTIFICATIONS ->
                        borderPane.setCenter(Model.getInstance().getViewFactory().getNotificationAnchorPane());
                case INVENTORY -> borderPane.setCenter(Model.getInstance().getViewFactory().getInventoryAnchorPane());
                case REPORTS -> borderPane.setCenter(Model.getInstance().getViewFactory().getReportsAnchorPane());
                case HELP -> borderPane.setCenter(Model.getInstance().getViewFactory().getHelpAnchorPane());
            }
        });
    }

    private void addListeners() {
        homeButton.setOnAction(e -> setHome());
        salesButton.setOnAction(e -> setSales());
        stockButton.setOnAction(e -> setStock());
        servicesButton.setOnAction(e -> setServices());
        notificationButton.setOnAction(e -> setNotification());
        inventoryButton.setOnAction(e -> setInventory());
        reportButton.setOnAction(e -> setReport());
        helpButton.setOnAction(e -> setHelp());
        logoutButton.setOnAction(e -> logout());
    }

    private void setHome() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.HOME);
    }

    private void setSales() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.SALES);
    }

    private void setStock() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.STOCK);
    }

    private void setServices() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.SERVICES);
    }

    private void setNotification() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.NOTIFICATIONS);
    }

    private void setInventory() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.INVENTORY);
    }

    private void setReport() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.REPORTS);
    }

    private void setHelp() {
        Model.getInstance().getViewFactory().getDashboardSelectedItem().set(DashboardOptions.HELP);
    }

    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.showAndWait();


        if (alert.getResult() == ButtonType.OK) {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
        } else {
            alert.close();
        }

    }

    private void otherActions() {
        Tooltip settingsTooltip = new Tooltip("Settings");
        Tooltip homeTooltip = new Tooltip("View Home");
        Tooltip salesTooltip = new Tooltip("Manage Sales");
        Tooltip stockTooltip = new Tooltip("Manage Stock");
        Tooltip servicesTooltip = new Tooltip("Manage Services");
        Tooltip notificationTooltip = new Tooltip("View Notifications");
        Tooltip inventoryTooltip = new Tooltip("Manage Inventory");
        Tooltip reportTooltip = new Tooltip("View Reports");
        Tooltip helpTooltip = new Tooltip("Get Help");
        Tooltip logoutTooltip = new Tooltip("Logout");
        Tooltip openCompany = new Tooltip("Open Company Website");

        // Set the delay to zero
        settingsTooltip.setShowDelay(Duration.ZERO);
        homeTooltip.setShowDelay(Duration.ZERO);
        salesTooltip.setShowDelay(Duration.ZERO);
        stockTooltip.setShowDelay(Duration.ZERO);
        servicesTooltip.setShowDelay(Duration.ZERO);
        notificationTooltip.setShowDelay(Duration.ZERO);
        inventoryTooltip.setShowDelay(Duration.ZERO);
        reportTooltip.setShowDelay(Duration.ZERO);
        helpTooltip.setShowDelay(Duration.ZERO);
        logoutTooltip.setShowDelay(Duration.ZERO);
        openCompany.setShowDelay(Duration.ZERO);

        Tooltip.install(settingsIcon, settingsTooltip);
        Tooltip.install(homeButton, homeTooltip);
        Tooltip.install(salesButton, salesTooltip);
        Tooltip.install(stockButton, stockTooltip);
        Tooltip.install(servicesButton, servicesTooltip);
        Tooltip.install(notificationButton, notificationTooltip);
        Tooltip.install(inventoryButton, inventoryTooltip);
        Tooltip.install(reportButton, reportTooltip);
        Tooltip.install(helpButton, helpTooltip);
        Tooltip.install(logoutButton, logoutTooltip);
        Tooltip.install(companyHyperlink, openCompany);

    }

}
