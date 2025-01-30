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
    private boolean hasPlayed = false; // Track if the player has played

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
     * Handles the player's Play or Fold action.
     *
     * @param info The PokerInfo object containing the player's action.
     */
    private void handlePlayerAction(PokerInfo info) {
        player.setAnteBet(info.getAnteBet());
        player.setPairPlusBet(info.getPairPlusBet());

        if (info.isPlayerFolded()) {
            server.incrementReadyPlayers();
            info.setGameMessage("Player " + playerNumber + " folded.");
            server.logGameEvent("Player " + playerNumber + " folded.");
        } else {
            server.logGameEvent("Player " + playerNumber + " played.");
        }

        server.broadcastToPlayers(info);
    }


    /**
     * Resolves the game round, determining winners and updating winnings.
     *
     * @param info The PokerInfo object used to communicate results.
     */
    private void resolveRound(PokerInfo info) {
        info.setDealerCardsHidden(false); // Reveal dealer's cards
        server.resetReadyPlayers();       // Reset for next round

        int gameResult = ThreeCardLogic.compareHands(dealer.getDealersHand(), player.getHand());
        int winnings = calculateWinnings(gameResult, info);

        player.updateTotalWinnings(winnings);
        info.setTotalWinnings(player.getTotalWinnings());

        server.logGameEvent("Game Result: Player " + playerNumber + " | " + info.getGameMessage() +
                            " | Winnings: $" + info.getTotalWinnings());

        server.broadcastToPlayers(info);
    }

    
    /**
     * Calculates the player's winnings based on the game result.
     *
     * @param gameResult The outcome of the hand (win, lose, tie).
     * @param info       The PokerInfo object containing bet details.
     * @return The total winnings for the player.
     */
    private int calculateWinnings(int gameResult, PokerInfo info) {
        int winnings = 0;

        if (gameResult == ThreeCardLogic.PLAYER_WIN) {
            winnings += player.getAnteBet() * 2;
            winnings += player.getPlayBet() * 2;
            info.setGameMessage("Player " + playerNumber + " wins!");
        } else if (gameResult == ThreeCardLogic.DEALER_WIN) {
            winnings -= player.getAnteBet();
            winnings -= player.getPlayBet();
            info.setGameMessage("Player " + playerNumber + " loses.");
        } else {
            winnings += player.getAnteBet();
            winnings += player.getPlayBet();
            info.setGameMessage("Player " + playerNumber + " ties.");
        }

        // Pair Plus Winnings
        int pairPlusWinnings = ThreeCardLogic.evalPPWinnings(player.getHand(), player.getPairPlusBet());
        winnings += pairPlusWinnings;

        if (pairPlusWinnings > 0) {
            info.setGameMessage(info.getGameMessage() + " Won Pair Plus: $" + pairPlusWinnings);
        } else if (player.getPairPlusBet() > 0) {
            winnings -= player.getPairPlusBet();
            info.setGameMessage(info.getGameMessage() + " Lost Pair Plus.");
        }

        return winnings;
    }




    /**
     * Checks if it is the player's turn based on the server's turn tracking.
     *
     * @return true if it is the player's turn, false otherwise.
     */
    private boolean isPlayerTurn() {
        return (playerNumber == 1 && server.isPlayer1Turn()) || 
               (playerNumber == 2 && !server.isPlayer1Turn());
    }



/**
 * Deals cards to the player, opponent, and dealer.
 */
    private void dealCards(PokerInfo info) {
        server.logGameEvent("Player " + playerNumber + " clicked 'Deal' - Starting the game.");
        
        deck = new Deck();
        deck.newDeck();

        player.setHand(dealer.dealHand());

        // Get opponent and assign cards
        ClientHandler opponent = server.getOpponentHandler(this);
        if (opponent != null) {
            opponent.getPlayer().setHand(dealer.dealHand());
        }

        dealer.setDealersHand(dealer.dealHand());

        info.setPlayerHand(player.getHand());
        info.setOpponentHand(opponent != null ? opponent.getPlayer().getHand() : new ArrayList<>());
        info.setDealerHand(dealer.getDealersHand());
        info.setDealerCardsHidden(true);

        server.broadcastToPlayers(info);
    }


/**
 * Handles player's play or fold action.
 */
private void processPlayerAction(PokerInfo info) {
    if (info.isPlayerFolded()) {
        server.logGameEvent("Player " + playerNumber + " folded.");
        server.incrementReadyPlayers();
        if (server.getReadyPlayers() == 2) {
            info.setDealerCardsHidden(false);
        }
        server.broadcastToPlayers(info);
        return;
    }

    if (info.getPlayBet() > 0) {
        server.logGameEvent("Player " + playerNumber + " placed Play Bet.");
        server.incrementReadyPlayers();
    }
}

/**
 * Checks if both players have played/folded and reveals dealer's cards if needed.
 */
private void checkRoundCompletion(PokerInfo info) {
    if (server.getReadyPlayers() == 2) {
        info.setDealerCardsHidden(false);
        server.broadcastToPlayers(info);
    }
}


/**
 * Determines the game results and winnings.
 */
private void resolveGameResults(PokerInfo info) {
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

    server.logGameEvent("Game Result: Player " + playerNumber + " | " + info.getGameMessage());

    server.resetReadyPlayers();
    server.broadcastToPlayers(info);
    server.switchTurn();
}

/**
 * Processes a player's action and ensures the game progresses correctly.
 * Handles dealing, turn-taking, and resolves the round when both players have acted.
 *
 * @param info The PokerInfo object containing the player's action (Play/Fold) and bet amounts.
 */
private void processGame(PokerInfo info) {
    synchronized (server) {
        if (!server.isGameReady()) {
            server.logGameEvent("Waiting for another player...");
            return;
        }

        if (playerNumber != server.getCurrentTurnPlayer()) {
            server.logGameEvent("Not Player " + playerNumber + "'s turn. Waiting...");
            return;
        }

        handlePlayerAction(info);

        // If both players have acted, resolve the round
        if (server.getReadyPlayers() == 2) {
            resolveRound(info);
        } else {
            // Switch turns
            server.switchTurn();
            server.broadcastToPlayers(info);
        }
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
