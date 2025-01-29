package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;

public class WinLoseController {
    private Stage primaryStage;
    private Socket socket;  // Maintain socket connection to server

    @FXML
    private Label winLoseMessageLabel, winningsLabel;

    @FXML
    private Button playAgainButton, exitButton;

    /**
     * Stores the primary stage for screen transitions.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Stores the socket to reuse the connection when playing again.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Displays the results of the game.
     */
    public void setResults(String message, int winnings) {
        winLoseMessageLabel.setText(message);
        winningsLabel.setText("Total Winnings: $" + winnings);
    }

    /**
     * Transitions back to the GamePlay screen to start a new hand.
     */
    @FXML
    private void handlePlayAgain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GamePlayScreen.fxml"));
            Scene gameScene = new Scene(loader.load());

            // Get the GamePlayController and reinitialize with the existing socket
            GamePlayController gameController = loader.getController();
            gameController.initializeGame(socket);

            primaryStage.setScene(gameScene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading GamePlayScreen.fxml");
            e.printStackTrace();
        }
    }

    /**
     * Closes the application when the player chooses to exit.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}
