package org.example.votingsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class HelloController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private RadioButton option1, option2, option3;
    @FXML
    private ToggleGroup optionsGroup;
    @FXML
    private TableView<Vote> tableView;
    @FXML
    private TableColumn<Vote, String> userColumn;
    @FXML
    private TableColumn<Vote, String> optionColumn;
    @FXML
    private TableColumn<Vote, Integer> countColumn;

    private ObservableList<Vote> voteList = FXCollections.observableArrayList();

    private Connection conn;

    @FXML
    public void initialize() {
        // Set up the TableView columns
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));
        optionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOptionName()));
        countColumn.setCellValueFactory(cellData -> cellData.getValue().voteCountProperty().asObject());

        // Initialize the database connection
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/FinalProjectDB", "postgres", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load votes from the database
        loadVotes();
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();  // Hash this in a real system
        String email = emailField.getText();

        try {
            String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);  // Hash this in a real system
                stmt.setString(3, email);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleVote(ActionEvent event) {
        int userId = getUserId(usernameField.getText());
        int voteOptionId = getSelectedOptionId();

        if (userId == -1 || voteOptionId == -1) {
            showAlert("Error", "Invalid user or vote option.");
            return;
        }

        try {
            String query = "INSERT INTO votes (user_id, vote_option) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, voteOptionId);
                stmt.executeUpdate();
                loadVotes();  // Reload the votes after inserting
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSelectedOptionId() {
        if (option1.isSelected()) {
            return 1;
        }
        if (option2.isSelected()) {
            return 2;
        }
        if (option3.isSelected()) {
            return 3;
        }
        return -1;
    }

    private int getUserId(String username) {
        try {
            String query = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadVotes() {
        if (conn == null) {
            System.out.println("Database connection is not initialized!");
            return;
        }

        voteList.clear();  // Clear existing data
        try {
            // Modify the query to get user details as well
            String query = "SELECT u.username, vo.id, vo.option_name, COUNT(v.id) as vote_count " +
                    "FROM votes v " +
                    "JOIN users u ON v.user_id = u.id " +
                    "JOIN vote_options vo ON v.vote_option = vo.id " +
                    "GROUP BY u.username, vo.id, vo.option_name";

            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String username = rs.getString("username");
                    String optionName = rs.getString("option_name");
                    int voteCount = rs.getInt("vote_count");
                    int optionId = rs.getInt("id");

                    // Create a Vote object with the user and vote option
                    User user = new User(username);  // Assume User constructor only needs username
                    VoteOption voteOption = new VoteOption(optionId, optionName);
                    Vote vote = new Vote(user, voteOption, voteCount);
                    voteList.add(vote);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(voteList);  // Set the table items to the vote list
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
