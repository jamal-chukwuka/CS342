package controllers;

import java.io.IOException;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the welcome screen. 
 * Handles user input for connecting to the server and switching to the game screen.
 */
public class WelcomeScreenController {
    @FXML
    private TextField ipAddressField, portField; // Input fields for server IP and port

    @FXML
    private Button connectButton, exitButton; // Buttons to connect or exit

    @FXML
    private Label errorMessage; // Displays connection errors

    private Stage primaryStage; // Reference to the main window

    private Socket socket; // Client socket connection to the server

    /**
     * Sets the primary stage for this controller. 
     * This allows scene transitions from the welcome screen to the game screen.
     *
     * @param primaryStage The main JavaFX stage.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Handles the Connect button click event. 
     * Attempts to connect to the server using the provided IP address and port.
     */
    @FXML
    private void handleConnect() {
        String ipAddress = ipAddressField.getText();
        int port;

        try {
            port = Integer.parseInt(portField.getText()); // Parse port number from input field
            socket = new Socket(ipAddress, port); // Establish connection to server
            System.out.println("Connected to server at " + ipAddress + ":" + port);

            // Transition to the game screen if the connection is successful
            showGamePlayScreen(socket);
        } catch (NumberFormatException e) {
            errorMessage.setText("Invalid port number."); // Display error if port is not a valid integer
        } catch (IOException e) {
            errorMessage.setText("Could not connect to server."); // Display error if connection fails
            e.printStackTrace();
        }
    }

    /**
     * Loads and transitions to the game play screen after a successful connection.
     *
     * @param socket The established client-server socket connection.
     */
    private void showGamePlayScreen(Socket socket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GamePlayScreen.fxml"));
            Scene gameScene = new Scene(loader.load());

            // Get the GamePlayController and pass the socket connection
            GamePlayController gameController = loader.getController();
            gameController.initializeGame(socket);

            // Switch the primary stage to the game screen
            primaryStage.setScene(gameScene);
            primaryStage.show();
        } catch (IOException e) {
            errorMessage.setText("Failed to load game screen."); // Display error if the screen fails to load
            e.printStackTrace();
        }
    }

    /**
     * Closes the application when the Exit button is clicked.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}
