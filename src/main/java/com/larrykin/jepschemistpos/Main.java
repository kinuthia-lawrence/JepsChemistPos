package com.larrykin.jepschemistpos;

import com.larrykin.jepschemistpos.MODELS.Model;
import javafx.application.Application;
import javafx.stage.Stage;



public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Model.getInstance().getViewFactory().loadLogin();
    }
}
