package com.larrykin.jepschemistpos.CONTROLLERS;

import com.larrykin.jepschemistpos.MODELS.Todo;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TodoController {

    private static final Logger log = LoggerFactory.getLogger(TodoController.class);
    @FXML
    private Button todoCancelButton;

    @FXML
    private TextArea todoDescription;

    @FXML
    private Button todoSaveButton;

    @FXML
    private TextField todoTitle;

    @FXML
    private TableView<Todo> todosTable;

    //?instantiate database
    private DatabaseConn databaseConn = new DatabaseConn();

    @FXML
    public void initialize() {
        initializeTable();
        initializeButtons();
    }

    private void initializeButtons() {
    }

    private void initializeTable() {
        TableColumn<Todo, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setPrefWidth(220);

        TableColumn<Todo, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(450);

        //editButton
        TableColumn<Todo, String> editColumn = new TableColumn<>("   Edit ");
        editColumn.setPrefWidth(220);
        editColumn.setCellFactory(param -> new TableCell<Todo, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Todo todo = getTableView().getItems().get(getIndex());
//                    editRow(todo);
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
        TableColumn<Todo, String> deleteColumn = new TableColumn<>("  Mark Done & Delete ");
        deleteColumn.setPrefWidth(220);
        deleteColumn.setCellFactory(param -> new TableCell<Todo, String>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Todo todo = getTableView().getItems().get(getIndex());
//                    deleteRow(todo);
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

        todosTable.getColumns().addAll(titleColumn, descriptionColumn, editColumn, deleteColumn);
        populateTodosTable();

    }

    //?Populate todo's table
    private void populateTodosTable() {
        ObservableList<Todo> todos = FXCollections.observableArrayList();
        todos.addAll(getTodos());
        todosTable.setItems(todos);
    }

    //? get todos
    private List<Todo> getTodos() {
        List<Todo> todoList = new ArrayList<>();
        try (
                Connection connection = databaseConn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM todo");
        ) {
            while (resultSet.next()) {
                Todo todo = new Todo();
                todo.setTitle(resultSet.getString("title"));
                todo.setDescription(resultSet.getString("description"));
                todoList.add(todo);
            }
        } catch (Exception e) {
            log.error("Error fetching todos{}", e.getMessage());
            e.printStackTrace();
        }

        return todoList;
    }

}
