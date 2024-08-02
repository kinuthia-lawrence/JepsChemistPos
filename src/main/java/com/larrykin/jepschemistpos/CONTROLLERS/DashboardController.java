package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
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
    private Button dashboardButton;

    @FXML
    private ImageView dashboardIcon;

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
//            Image settingsImage = new Image(getClass().getResourceAsStream("/IMAGES/settings.gif"));
//            settingsIcon.setImage(settingsImage);
            Image settingsImage = new Image(getClass().getResourceAsStream("/IMAGES/setting.gif"));
            settingsIcon.setImage(settingsImage);

            Image dashboardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/IMAGES/dashboard.png")));
            dashboardIcon.setImage(dashboardImage);
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

}
