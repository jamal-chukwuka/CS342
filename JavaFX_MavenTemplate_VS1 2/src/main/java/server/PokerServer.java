package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import controller.ClientHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.PokerInfo;

public class PokerServer {
    private ServerSocket serverSocket;
    private final ObservableList<ClientHandler> clients = FXCollections.observableArrayList(); // Use ObservableList
    private boolean isRunning = false;
    private int playerCounter = 1;  // Track player numbers

    // Observable list for server log updates
    private final ObservableList<String> serverLog = FXCollections.observableArrayList();
    
    private int readyPlayers = 0;

    public synchronized void incrementReadyPlayers() {
        readyPlayers++;
    }

    public synchronized void resetReadyPlayers() {
        readyPlayers = 0;
    }

    public synchronized int getReadyPlayers() {
        return readyPlayers;
    }

    public int getClientCount() {
        return clients.size();
    }

    // Notify all players when both are ready
    public synchronized boolean isGameReady() {
        return clients.size() == 2;
    }

    public void broadcastToPlayers(PokerInfo info) {
        if (!isGameReady()) {
            logGameEvent("Waiting for another player to join...");
            return;  // Don't send game updates if only one player is connected
        }

        for (ClientHandler client : clients) {
            client.sendToClient(info);
        }
    }
    
    
//    /**
//     * Returns the opponent's hand for the given player number.
//     *
//     * @param playerNumber The player requesting the opponent's hand (1 or 2).
//     * @return The opponent's hand as an ArrayList of Cards, or an empty list if no opponent is found.
//     */
//    public synchronized ArrayList<Card> getOpponentHand(int playerNumber) {
//        for (ClientHandler client : clients) {
//            if (client.getPlayerNumber() != playerNumber) { // Find the opponent
//                return client.getPlayerHand();  // Return the opponent's hand
//            }
//        }
//        return new ArrayList<>(); // Return an empty list if opponent is not found
//    }

  


    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            logGameEvent("Server started on port: " + port);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                logGameEvent("New client connected: " + clientSocket.getInetAddress());
                
             // Assign Player 1 or Player 2
                int assignedPlayerNumber = playerCounter;
                playerCounter++;
                if (playerCounter > 2) {
                    playerCounter = 1; // Reset after two players
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket, this, assignedPlayerNumber);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            logGameEvent("Error starting server: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.closeConnection();
            }
            clients.clear();
            logGameEvent("Server stopped.");
        } catch (IOException e) {
            logGameEvent("Error stopping server: " + e.getMessage());
        }
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        logGameEvent("Client disconnected. Active clients: " + clients.size());

        if (clients.size() < 2) {
            logGameEvent("Game paused. Waiting for another player to join...");
        }
    }
    
    


    public synchronized void logGameEvent(String message) {
        Platform.runLater(() -> serverLog.add(message));
        System.out.println("[SERVER] " + message);
    }

    public ObservableList<String> getServerLog() {
        return serverLog;
    }

    public ObservableList<ClientHandler> getClients() {
        return clients;  // Returns an ObservableList
    }
    
    public synchronized void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
        System.out.println("New client connected. Active clients: " + clients.size());
    }
    
    
}
