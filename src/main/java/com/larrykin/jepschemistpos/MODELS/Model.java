package com.larrykin.jepschemistpos.MODELS;

import com.larrykin.jepschemistpos.VIEWS.ViewFactory;

public class Model {
    //?  Initializing the model and the view factory
    private static Model model;
    private final ViewFactory viewFactory;

    //? constructor
    public Model(){
        this.viewFactory= new ViewFactory();
    }

    //? this is a singleton pattern to ensure only one instance of the model is created
    public static synchronized Model getInstance(){
        if (model == null){
            model = new Model();
        }
        return model;
    }

    //? getter for the viewFactory to be used in the controller
    public ViewFactory getViewFactory(){
        return viewFactory;
    }
}
