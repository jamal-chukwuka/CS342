package controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import server.PokerServer;

/**
 * Controls the game state UI on the server side.
 * This class is responsible for updating the game log and tracking active clients.
 */
public class GameStateController {

    @FXML
    private ListView<String> gameLogListView; // Displays log messages for server events

    @FXML
    private Label activeClientsLabel; // Shows the number of connected clients

    private PokerServer pokerServer; // Reference to the PokerServer instance

    /**
     * Sets the server instance, binds the game log ListView, and tracks client count.
     *
     * @param pokerServer The running PokerServer instance.
     */
    public void setServerInstance(PokerServer pokerServer) {
        this.pokerServer = pokerServer;

        // Bind the server's log messages to the UI list view
        if (pokerServer.getServerLog() != null) {
            gameLogListView.setItems(pokerServer.getServerLog());

            // Automatically scroll the game log to show the latest entries
            pokerServer.getServerLog().addListener((ListChangeListener<String>) change ->
                Platform.runLater(() -> gameLogListView.scrollTo(gameLogListView.getItems().size() - 1))
            );
        }

        updateClientCount(); // Update active clients count when initializing

    }

    /**
     * Updates the active client count label dynamically.
     * This method ensures that the label always reflects the correct number of connected clients.
     */
    /**
     * Updates the active client count label dynamically.
     * Ensures the label always reflects the correct number of connected clients.
     */
    private void updateClientCount() {
        if (pokerServer != null) {
            Platform.runLater(() -> {
                int activeClients = pokerServer.getConnectedClients().size();
                activeClientsLabel.setText("Active Clients: " + activeClients);
            });
        }
    }

}
