package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.PokerServer;
import java.io.IOException;

public class ServerController {
    @FXML
    private TextField portField; // Input field for the server port number
    @FXML
    private Label serverStatusLabel; // Label to display server status
    @FXML
    private Button startServerButton; // Button to start the server

    private PokerServer pokerServer; // The PokerServer instance
    private Stage primaryStage; // Reference to the primary UI window

    // Allows the main application to set the primary stage for scene transitions
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Handles the "Start Server" button click.
     * Reads the port number from the input field, starts the server in a new thread, 
     * and then transitions to the game state screen.
     */
    @FXML
    private void handleStartServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim()); // Parse port number
            System.out.println("[DEBUG] Start Server button clicked. Port: " + port);

            // Start the server on a separate thread to prevent UI blocking
            Thread serverThread = new Thread(() -> {
                pokerServer = new PokerServer();
                pokerServer.startServer(port);
            });
            serverThread.setDaemon(true); // Ensures the server thread stops when the app exits
            serverThread.start();

            // Transition to the game state screen after starting the server
            Platform.runLater(() -> {
                System.out.println("[DEBUG] Switching to Game State screen...");
                showGameStateScreen();
            });

        } catch (NumberFormatException e) {
            System.err.println("[ERROR] Invalid port number. Please enter a valid integer.");
        }
    }

    /**
     * Loads and switches to the game state screen.
     * Ensures the UI updates properly after the server has started.
     */
    private void showGameStateScreen() {
        try {
            if (primaryStage == null) {
                System.err.println("[ERROR] primaryStage is null. Cannot switch scenes.");
                return;
            }

            // Load the ServerGameState.fxml file for the next screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServerGameState.fxml"));
            VBox root = loader.load();

            // Set the controller for the game state screen and pass the server instance
            GameStateController controller = loader.getController();
            controller.setServerInstance(pokerServer);

            // Switch to the new scene
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Poker Server - Game State");
            primaryStage.show();

            System.out.println("[DEBUG] Successfully switched to Game State Screen.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load the game state screen.");
        }
    }

    /**
     * Handles stopping the server when the "Stop Server" button is clicked.
     * Stops the PokerServer instance and notifies the user.
     */
    @FXML
    private void handleStopServer() {
        if (pokerServer != null) {
            pokerServer.stopServer();
            showInfo("Server Stopped", "The server has been stopped.");
        }
    }

    /**
     * Displays an error message in an alert dialog.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an informational message in an alert dialog.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
