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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        todoCancelButton.setOnAction(event -> {
            todoTitle.clear();
            todoDescription.clear();
        });
        todoSaveButton.setOnAction(event -> {
                    saveTodo();
                }
        );
    }

    private void saveTodo() {
        String title = todoTitle.getText();
        String description = todoDescription.getText();

        if (title.isBlank() || description.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Fields");
            alert.setHeaderText("Please fill in all fields");
            alert.showAndWait();
            return;
        }

        String sql = "INSERT INTO todo (title, description) VALUES (?, ?)";
        try (
                Connection connection = databaseConn.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            int output = preparedStatement.executeUpdate();

            if (output > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Todo Saved");
                alert.setHeaderText("Todo Saved Successfully");
                TimeUnit.SECONDS.sleep(2);
                alert.showAndWait();
                connection.close();
                populateTodosTable();
                todoTitle.clear();
                todoDescription.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Todo Save Error");
                alert.setHeaderText("Todo Save Failed");
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.error("Error saving todo{}", e.getMessage());
            e.printStackTrace();
        }
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
                    editRow(todo);
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
                    deleteRow(todo);
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

    //? delete row
    private void deleteRow(Todo todo) {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete Todo");
        deleteAlert.setHeaderText("Are you sure you want to delete " + todo.getTitle());
        deleteAlert.setContentText("This Action is irreversible!!");
        deleteAlert.showAndWait();

        if (deleteAlert.getResult() != ButtonType.OK) {
            deleteAlert.close();
            return;
        }
        String deleteQuery = "DELETE FROM todo WHERE title=?";
        try (
                Connection connection = databaseConn.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
        ) {
            preparedStatement.setString(1, todo.getTitle());
            int output = preparedStatement.executeUpdate();
            if (output > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Todo Deleted");
                alert.setHeaderText("Todo Deleted Successfully");
                TimeUnit.SECONDS.sleep(2);
                alert.showAndWait();
                connection.close();
                populateTodosTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Todo Delete Error");
                alert.setHeaderText("Todo Delete Failed");
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.error("Error deleting todo{}", e.getMessage());
            e.printStackTrace();
        }
    }

    //?Edit row
    private void editRow(Todo todo) {
        if (todo != null) {
            try {
                String oldTitle = todo.getTitle();
                //setting field to old values
                todoTitle.setText(todo.getTitle());
                todoDescription.setText(todo.getDescription());

                todoSaveButton.setText("Update");
                todoSaveButton.setOnAction(event -> {
                    //getting new todo details
                    String updatedTitle = todoTitle.getText();
                    String updatedDescription = todoDescription.getText();

                    //validate that the fields are not empty
                    if (!updatedDescription.isBlank() || !updatedTitle.isBlank()) {
                        String sql = "UPDATE todo SET title = ?, description = ? WHERE title = ?";
                        try (
                                Connection connection = databaseConn.getConnection();
                                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        ) {
                            preparedStatement.setString(1, updatedTitle);
                            preparedStatement.setString(2, updatedDescription);
                            preparedStatement.setString(3, oldTitle);
                            int output = preparedStatement.executeUpdate();

                            if (output > 0) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Todo Updated");
                                alert.setHeaderText("Todo Updated Successfully");
                                TimeUnit.SECONDS.sleep(2);
                                alert.showAndWait();

                                connection.close();
                                populateTodosTable();
                                todoTitle.clear();
                                todoDescription.clear();
                                todoSaveButton.setText("Save");
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Todo Update Error");
                                alert.setHeaderText("Todo Update Failed");
                                alert.showAndWait();
                            }
                        } catch (Exception e) {
                            log.error("Error updating todo{}", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                log.error("Error editing todo{}", e.getMessage());
                e.printStackTrace();
            }
        }
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
