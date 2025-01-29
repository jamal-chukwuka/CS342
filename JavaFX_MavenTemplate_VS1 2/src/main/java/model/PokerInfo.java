package model;

import java.io.*;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures compatibility during serialization

    private int anteBet; // The player's ante bet
    private int pairPlusBet; // The player's Pair Plus bet
    private int playBet; // The player's play bet
    private int totalWinnings; // The player's total winnings across games
    private boolean playerFolded; // Tracks if the player has folded
    private String gameMessage; // Stores game-related messages to display to the player
    private int currentTurn = 1; // Start with Player 1

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int turn) {
        this.currentTurn = turn;
    }

    // These lists store the player's and dealer's hands
    // Marked as transient since ArrayLists containing objects may not serialize well by default
    private transient ArrayList<Card> playerHand;
    private transient ArrayList<Card> dealerHand;
    
    private boolean dealerCardsHidden = true; // True when dealer's cards are hidden
    private ArrayList<Card> opponentHand = new ArrayList<>();

    public ArrayList<Card> getOpponentHand() {
        return new ArrayList<>(opponentHand); // Return a copy to prevent modification
    }

    public void setOpponentHand(ArrayList<Card> opponentHand) {
        this.opponentHand = new ArrayList<>(opponentHand);
    }


    public boolean isDealerCardsHidden() {
        return dealerCardsHidden;
    }

    public void setDealerCardsHidden(boolean dealerCardsHidden) {
        this.dealerCardsHidden = dealerCardsHidden;
    }



    /**
     * Default constructor.
     * Initializes empty hands and ensures the game message is never null.
     */
    public PokerInfo() {
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
        this.gameMessage = "";
    }

    // ===== Getters and Setters =====

    public int getAnteBet() {
        return anteBet;
    }

    public void setAnteBet(int anteBet) {
        this.anteBet = anteBet;
    }

    public int getPairPlusBet() {
        return pairPlusBet;
    }

    public void setPairPlusBet(int pairPlusBet) {
        this.pairPlusBet = pairPlusBet;
    }

    public int getPlayBet() {
        return playBet;
    }

    public void setPlayBet(int playBet) {
        this.playBet = playBet;
    }

    public int getTotalWinnings() {
        return totalWinnings;
    }

    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    /**
     * Returns a copy of the player's hand to prevent unintended modifications.
     */
    public ArrayList<Card> getPlayerHand() {
        return new ArrayList<>(playerHand);
    }

    /**
     * Stores a copy of the player's hand to prevent external modification.
     */
    public void setPlayerHand(ArrayList<Card> playerHand) {
        this.playerHand = new ArrayList<>(playerHand);
    }

    /**
     * Returns a copy of the dealer's hand to ensure data integrity.
     */
    public ArrayList<Card> getDealerHand() {
        return new ArrayList<>(dealerHand);
    }

    /**
     * Stores a copy of the dealer's hand to protect the original data.
     */
    public void setDealerHand(ArrayList<Card> dealerHand) {
        this.dealerHand = new ArrayList<>(dealerHand);
    }

    public boolean isPlayerFolded() {
        return playerFolded;
    }

    public void setPlayerFolded(boolean playerFolded) {
        this.playerFolded = playerFolded;
    }

    public String getGameMessage() {
        return gameMessage;
    }

    /**
     * Ensures the game message is never null to avoid potential issues in UI updates.
     */
    public void setGameMessage(String gameMessage) {
        this.gameMessage = (gameMessage == null) ? "" : gameMessage;
    }

    // ===== Custom Serialization and Deserialization Methods =====

    /**
     * Handles custom serialization to properly store the player's and dealer's hands.
     * The hands are written separately since transient fields are not serialized by default.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Serialize all non-transient fields

        // Write player hand size and cards
        out.writeInt(playerHand.size());
        for (Card card : playerHand) {
            out.writeObject(card);
        }

        // Write dealer hand size and cards
        out.writeInt(dealerHand.size());
        for (Card card : dealerHand) {
            out.writeObject(card);
        }
    }

    /**
     * Handles custom deserialization to reconstruct the player's and dealer's hands.
     * This ensures that the transient fields are restored correctly when the object is read back.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize all non-transient fields

        // Read player hand
        int playerHandSize = in.readInt();
        playerHand = new ArrayList<>();
        for (int i = 0; i < playerHandSize; i++) {
            playerHand.add((Card) in.readObject());
        }

        // Read dealer hand
        int dealerHandSize = in.readInt();
        dealerHand = new ArrayList<>();
        for (int i = 0; i < dealerHandSize; i++) {
            dealerHand.add((Card) in.readObject());
        }
    }
}
