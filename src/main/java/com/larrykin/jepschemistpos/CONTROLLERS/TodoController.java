package com.larrykin.jepschemistpos.CONTROLLERS;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;

public class TodoController {

    @FXML
    private Button todoCancelButton;

    @FXML
    private TextArea todoDescription;

    @FXML
    private Button todoSaveButton;

    @FXML
    private TextField todoTitle;

    @FXML
    private TreeTableView<?> todosTable;

}
