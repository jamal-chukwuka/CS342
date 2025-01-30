package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import controller.ClientHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.PokerInfo;

public class PokerServer {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private boolean isRunning = false;
    private int currentTurn = 1; // Start with Player 1
    private int readyPlayers = 0; // Track how many players have acted
    private final ObservableList<String> serverLog = FXCollections.observableArrayList();
    private int currentTurnPlayer = 1; // Always starts with Player 1

    

    public synchronized boolean isGameReady() {
        return clients.size() == 2;
    }

    public synchronized void incrementReadyPlayers() {
        readyPlayers++;
    }

    public synchronized void resetReadyPlayers() {
        readyPlayers = 0;
    }

    public synchronized int getReadyPlayers() {
        return readyPlayers;
    }
    
    public synchronized List<ClientHandler> getConnectedClients() {
        return new ArrayList<>(clients); // Return a copy of the client list
    }
    
    private boolean isPlayer1Turn = true; // Track turn using a boolean

    /**
     * Returns whether it is Player 1's turn.
     */
    public synchronized boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }
    
    

    public synchronized void broadcastToPlayers(PokerInfo info) {
        if (!isGameReady()) {
            logGameEvent("Waiting for another player to join...");
            return;
        }

        for (ClientHandler client : clients) {
            client.sendToClient(info);
        }
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            logGameEvent("Server started on port: " + port);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();

                // Prevent extra clients from joining
                if (clients.size() >= 2) {
                    logGameEvent("New connection attempt rejected: Max players reached.");
                    clientSocket.close();
                    continue;
                }

                logGameEvent("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, this, clients.size() + 1);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                if (isGameReady()) {
                    logGameEvent("Both players connected. Starting game...");
                    currentTurn = 1;
                }
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
            logGameEvent("Game paused. Waiting for another player...");
            currentTurn = 0;
        }
    }
    
    /**
     * Gets the opponent's ClientHandler.
     * @param currentPlayer The current player's ClientHandler.
     * @return The opponent's ClientHandler, or null if no opponent exists.
     */
    public synchronized ClientHandler getOpponentHandler(ClientHandler currentPlayer) {
        for (ClientHandler client : clients) {
            if (client != currentPlayer) {
                return client;
            }
        }
        return null;
    }


    /**
     * Switches turn between Player 1 and Player 2.
     */
    public synchronized void switchTurn() {
        if (clients.size() == 2) {
            currentTurnPlayer = (currentTurnPlayer == 1) ? 2 : 1;
            logGameEvent("Turn switched. Now Player " + currentTurnPlayer + "'s turn.");
        }
    }

    
    public synchronized int getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public synchronized int getCurrentTurn() {
        return currentTurn;
    }

    public synchronized void logGameEvent(String message) {
        Platform.runLater(() -> serverLog.add(message));
        System.out.println("[SERVER] " + message);
    }

    public ObservableList<String> getServerLog() {
        return serverLog;
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
        System.out.println("New client connected. Active clients: " + clients.size());
    }
    
    public synchronized boolean isGameThreadRunning(Thread gameThread) {
        return gameThread != null && gameThread.isAlive();
    }

    /**
     * Returns the opponent's hand for the given player number.
     *
     * @param requestingPlayer The player requesting the opponent's hand.
     * @return The opponent's PokerInfo containing their hand, or null if no opponent exists.
     */
    public synchronized PokerInfo getOpponentHand(int requestingPlayer) {
        if (!isGameReady()) return null;

        for (ClientHandler client : clients) {
            if (client.getPlayerNumber() != requestingPlayer) {
                PokerInfo opponentInfo = new PokerInfo();
                opponentInfo.setPlayerHand(client.getPlayer().getHand());
                return opponentInfo;
            }
        }
        return null;
    }
}
