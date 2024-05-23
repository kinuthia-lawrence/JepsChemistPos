package com.larrykin.jepschemistpos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load());
        stage.setScene(loginScene);
        stage.show();




    }
}
