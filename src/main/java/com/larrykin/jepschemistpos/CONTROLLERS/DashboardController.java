package com.larrykin.jepschemistpos.CONTROLLERS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

public class DashboardController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button dashboardButton;

    @FXML
    private ImageView dashboardIcon;

    @FXML
    private Button inventoryButton;

    @FXML
    private ImageView inventoryIcon;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button notificationButton;

    @FXML
    private ImageView notificationIcon;

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
    private VBox sidebar;

    @FXML
    private Button stockButton;

    @FXML
    private ImageView stockIcon;


    @FXML
    public void initialize() {
        loadImages();

    }

    private void loadImages() {
        try {        //? logo of the dashboard page
            Image logoImage = new Image(getClass().getResourceAsStream("/IMAGES/microscope.jpg"));
            logoImageView.setImage(logoImage);
            Circle circle = new Circle(logoImageView.getFitWidth() / 2, logoImageView.getFitHeight() / 2, Math.min(logoImageView.getFitWidth(), logoImageView.getFitHeight()) / 2);
            logoImageView.setClip(circle);
            //? buttonIcons
            Image dashboardImage = new Image(getClass().getResourceAsStream("/IMAGES/dashboard.png"));
            dashboardIcon.setImage(dashboardImage);
            Image salesImage = new Image(getClass().getResourceAsStream("/IMAGES/sales.png"));
            salesIcon.setImage(salesImage);
            Image inventoryImage = new Image(getClass().getResourceAsStream("/IMAGES/inventory.png"));
            inventoryIcon.setImage(inventoryImage);
            Image reportImage = new Image(getClass().getResourceAsStream("/IMAGES/reports.png"));
            reportIcon.setImage(reportImage);

//            Image notificationImage = new Image(getClass().getResourceAsStream("/IMAGES/notify.png"));
//            notificationIcon.setImage(notificationImage);
            Image notificationImage = new Image(getClass().getResourceAsStream("/IMAGES/notification.gif"));
            notificationIcon.setImage(notificationImage);

            Image serviceImage = new Image(getClass().getResourceAsStream("/IMAGES/services.gif"));
            serviceIcon.setImage(serviceImage);
            Image stockImage = new Image(getClass().getResourceAsStream("/IMAGES/stock.png"));
            stockIcon.setImage(stockImage);
        } catch (Exception e) {
            System.out.println("Error loading images : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
