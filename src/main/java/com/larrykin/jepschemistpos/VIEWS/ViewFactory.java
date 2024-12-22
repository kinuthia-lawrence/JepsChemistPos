package com.larrykin.jepschemistpos.VIEWS;

import com.larrykin.jepschemistpos.ENUMS.DashboardOptions;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



public class ViewFactory {
    //? Initializing the ObjectProperty and the panes
    private final ObjectProperty dashboardSelectedItem;
    private AnchorPane homeAnchorPane, saleAnchorPane, inventoryAnchorPane, reportsAnchorPane, settingsAnchorPane, stockAnchorPane, servicesAnchorPane, helpAnchorPane, notificationAnchorPane;

    //?Constructor
    public ViewFactory() {
        this.dashboardSelectedItem = new SimpleObjectProperty();
    }

    //!  DASHBOARD SECTION
    public ObjectProperty<DashboardOptions> getDashboardSelectedItem() {
        return dashboardSelectedItem;
    }

    //? getting the Dashboard resources
    public AnchorPane getHomeAnchorPane() {
        if (homeAnchorPane == null) {
            try {
                homeAnchorPane = new FXMLLoader(getClass().getResource("/FXML/home.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the homeAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return homeAnchorPane;
    }

    public AnchorPane getSaleAnchorPane() {
        if (saleAnchorPane == null) {
            try {
                saleAnchorPane = new FXMLLoader(getClass().getResource("/FXML/sale.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the saleAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return saleAnchorPane;
    }

    public AnchorPane getInventoryAnchorPane() {
        if (inventoryAnchorPane == null) {
            try {
                inventoryAnchorPane = new FXMLLoader(getClass().getResource("/FXML/inventory.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the inventoryAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return inventoryAnchorPane;
    }

    public AnchorPane getReportsAnchorPane() {
        if (reportsAnchorPane == null) {
            try {
                reportsAnchorPane = new FXMLLoader(getClass().getResource("/FXML/reports.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the reportsAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return reportsAnchorPane;
    }

    public AnchorPane getSettingsAnchorPane() {
        if (settingsAnchorPane == null) {
            try {
                settingsAnchorPane = new FXMLLoader(getClass().getResource("/FXML/settings.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the settingsAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return settingsAnchorPane;
    }

    public AnchorPane getStockAnchorPane() {
        if (stockAnchorPane == null) {
            try {
                stockAnchorPane = new FXMLLoader(getClass().getResource("/FXML/stock.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the stockAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return stockAnchorPane;
    }

    public AnchorPane getServicesAnchorPane() {
        if (servicesAnchorPane == null) {
            try {
                servicesAnchorPane = new FXMLLoader(getClass().getResource("/FXML/services.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the servicesAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return servicesAnchorPane;
    }

    public AnchorPane getHelpAnchorPane() {
        if (helpAnchorPane == null) {
            try {
                helpAnchorPane = new FXMLLoader(getClass().getResource("/FXML/help.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the helpAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return helpAnchorPane;
    }

    public AnchorPane getNotificationAnchorPane() {
        if (notificationAnchorPane == null) {
            try {
                notificationAnchorPane = new FXMLLoader(getClass().getResource("/FXML/notification.fxml")).load();
            } catch (Exception e) {
                System.out.println("Error loading the notificationAnchorPane" + e.getMessage());
                e.printStackTrace();
            }
        }
        return notificationAnchorPane;
    }

    //?Loading the login page
    public void loadLogin() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);
            stage.setTitle("Jelps Chemist POS");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            Image icon = new Image(getClass().getResourceAsStream("/IMAGES/microscope.jpg"));
            stage.getIcons().add(icon);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error loading the loginAnchorPane" + e.getMessage());
            e.printStackTrace();
        }

    }
}
