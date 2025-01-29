package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import model.Card;
import model.Dealer;
import model.Deck;
import model.Player;
import model.PokerInfo;
import model.ThreeCardLogic;
import server.PokerServer;

/**
 * Handles client connections and game interactions on the server side.
 * Each instance of this class manages a single player.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket; // Socket connection with the client
    private final PokerServer server; // Reference to the main PokerServer instance
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private Dealer dealer;
    private Player player;
    private Deck deck;
    private boolean playingAnotherHand = false;
    private int playerNumber; // Player ID assigned by the server
    private static int connectedPlayers = 0; // Track players connected
    private static final Object playerLock = new Object(); // Lock for synchronization

    /**
     * Constructor initializes a new client handler instance.
     *
     * @param clientSocket The socket connection to the client.
     * @param server       The PokerServer instance managing the game.
     * @param playerNumber The unique player number assigned by the server.
     */
    public ClientHandler(Socket clientSocket, PokerServer server, int playerNumber) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.dealer = new Dealer();
        this.player = new Player();
        this.deck = new Deck();
        this.playerNumber = playerNumber;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Returns the player's current hand.
     * @return The player's hand as an ArrayList of Cards.
     */
    public ArrayList<Card> getPlayerHand() {
        return player.getHand();
    }

    /**
     * Returns the player's number (1 or 2).
     * @return The player's assigned number.
     */
    public int getPlayerNumber() {
        return playerNumber;
    }



    /**
     * Runs the client handler, setting up communication and listening for game actions.
     */
    @Override
    public void run() {
        try {
            setupStreams();

            synchronized (playerLock) {
                connectedPlayers++;
                playerNumber = connectedPlayers;
                server.logGameEvent("Player " + playerNumber + " connected: " + clientSocket.getInetAddress());

                // Wait until both players join before starting
                while (connectedPlayers < 2) {
                    server.logGameEvent("Waiting for second player...");
                    playerLock.wait(); // Pause thread until another player joins
                }
                playerLock.notifyAll(); // Wake both players when ready
            }

            // Send Player Number to Client
            output.writeInt(playerNumber);
            output.flush();

            while (!clientSocket.isClosed()) {
                Object receivedData = input.readObject();
                if (receivedData instanceof PokerInfo) {
                    PokerInfo info = (PokerInfo) receivedData;
                    processGame(info);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            server.logGameEvent("Player " + playerNumber + " disconnected.");
        } finally {
            closeConnection();
        }
    }



    /**
     * Initializes input and output streams for communication with the client.
     */
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        input = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Processes a game round based on the player's bet and actions.
     *
     * @param info The PokerInfo object containing the player's bet and game state.
     */
    
    private void processGame(PokerInfo info) {
        player.setAnteBet(info.getAnteBet());
        player.setPairPlusBet(info.getPairPlusBet());

        synchronized (server) {
            // Ensure both players have connected before proceeding
            while (server.getClientCount() < 2) {
                try {
                    server.logGameEvent("Waiting for Player 2 to join...");
                    server.wait(); // Corrected: `wait()` on server object
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (playerNumber == 1) {
                server.logGameEvent("Player 1 clicked 'Deal' - Starting the game.");
                deck = new Deck(); // Reset deck for a new game
                deck.newDeck();

                // Deal hands for both players
                player.setHand(dealer.dealHand());
                server.getClients().get(1).getPlayer().setHand(dealer.dealHand());

                info.setPlayerHand(player.getHand());
                info.setOpponentHand(server.getClients().get(1).getPlayer().getHand());

                dealer.setDealersHand(dealer.dealHand());
                info.setDealerHand(dealer.getDealersHand());
                info.setDealerCardsHidden(true);

                server.notifyAll(); // Notify all players the game has started
            } else {
                // Player 2 waits for Player 1 to start the game
                while (player.getHand().isEmpty()) {
                    try {
                        server.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        server.logGameEvent("Cards Dealt -> Player " + playerNumber + ": " + player.getHand() +
                " | Opponent: " + server.getClients().get(1).getPlayer().getHand() +
                " | Dealer: [Hidden]");

        if (info.isPlayerFolded()) {
            synchronized (server) {
                server.incrementReadyPlayers();
                info.setGameMessage("Player " + playerNumber + " folded.");
                if (server.getReadyPlayers() == 2) {
                    info.setDealerCardsHidden(false);
                    server.resetReadyPlayers();
                }
                server.broadcastToPlayers(info);
                return;
            }
        }
        
        if (info.isPlayerFolded() || info.getPlayBet() > 0) {
            System.out.println("[SERVER] Player " + playerNumber + " has played or folded.");

            // Reveal dealer's cards immediately
            info.setDealerCardsHidden(false);

            // Send updated state to both players
            server.broadcastToPlayers(info);
        }

        synchronized (server) {
            server.incrementReadyPlayers();
            while (server.getReadyPlayers() < 2) {
                try {
                    server.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int gameResult = ThreeCardLogic.compareHands(dealer.getDealersHand(), player.getHand());
            int winnings = 0;

            if (gameResult == ThreeCardLogic.PLAYER_WIN) {
                winnings += player.getAnteBet() * 2;
                winnings += player.getPlayBet() * 2;
                info.setGameMessage("Player " + playerNumber + " wins against dealer!");
            } else if (gameResult == ThreeCardLogic.DEALER_WIN) {
                winnings -= player.getAnteBet();
                winnings -= player.getPlayBet();
                info.setGameMessage("Player " + playerNumber + " loses to dealer.");
            } else {
                winnings += player.getAnteBet();
                winnings += player.getPlayBet();
                info.setGameMessage("Player " + playerNumber + " ties with dealer.");
            }

            int pairPlusWinnings = ThreeCardLogic.evalPPWinnings(player.getHand(), player.getPairPlusBet());
            winnings += pairPlusWinnings;

            if (pairPlusWinnings > 0) {
                info.setGameMessage(info.getGameMessage() + " Won Pair Plus: $" + pairPlusWinnings);
            } else if (player.getPairPlusBet() > 0) {
                winnings -= player.getPairPlusBet();
                info.setGameMessage(info.getGameMessage() + " Lost Pair Plus.");
            }

            player.updateTotalWinnings(winnings);
            info.setTotalWinnings(player.getTotalWinnings());

            info.setDealerCardsHidden(false);
            server.logGameEvent("Game Result: Player " + playerNumber + " | Bet: $" + info.getAnteBet() +
                    " | Pair Plus: $" + info.getPairPlusBet() + " | " + info.getGameMessage() +
                    " | Winnings: $" + info.getTotalWinnings());

            server.resetReadyPlayers();
            server.broadcastToPlayers(info);
        }
    }







    /**
     * Sends updated game data back to the client.
     *
     * @param info The PokerInfo object containing the updated game state.
     */
    public void sendToClient(PokerInfo info) {
        try {
            output.writeObject(info);
            output.flush();
        } catch (IOException e) {
            server.logGameEvent("Error sending game data to Player " + playerNumber + ": " + e.getMessage());
        }
    }

    /**
     * Closes the connection and cleans up resources when a client disconnects.
     */
    public void closeConnection() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            server.logGameEvent("Error closing connection for Player " + playerNumber + ": " + e.getMessage());
        } finally {
            server.removeClient(this);
        }
    }

    /**
     * Checks if the player is currently in another hand.
     *
     * @return true if the player is playing another hand, false otherwise.
     */
    public boolean isPlayingAnotherHand() {
        return playingAnotherHand;
    }
}
