package model;

import java.util.ArrayList;


/**
 * Represents a player in the game, managing their hand, bets, and total winnings.
 * This class provides the core functionalities to track a player's state throughout the game.
 */
public class Player {
    private ArrayList<Card> hand; // Stores the player's current hand
    private int anteBet; // The player's Ante bet
    private int pairPlusBet; // The player's Pair Plus bet
    private int playBet; // The player's Play bet
    private int totalWinnings; // Tracks the player's total winnings across games

    /**
     * Default constructor for the Player class.
     * Initializes the player's hand as an empty list and sets total winnings to zero.
     */
    public Player() {
        hand = new ArrayList<>(); // Start with an empty hand
        totalWinnings = 0; // No winnings at the start
    }

    /**
     * Sets the player's hand to the provided list of cards.
     * @param hand the new hand to assign to the player
     */
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    /**
     * Retrieves the player's current hand.
     * @return the list of cards in the player's hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Sets the player's Ante bet.
     * @param anteBet the amount to set for the Ante bet
     */
    public void setAnteBet(int anteBet) {
        this.anteBet = anteBet;
    }

    /**
     * Retrieves the player's Ante bet.
     * @return the current Ante bet amount
     */
    public int getAnteBet() {
        return anteBet;
    }

    /**
     * Sets the player's Pair Plus bet.
     * @param pairPlusBet the amount to set for the Pair Plus bet
     */
    public void setPairPlusBet(int pairPlusBet) {
        this.pairPlusBet = pairPlusBet;
    }

    /**
     * Retrieves the player's Pair Plus bet.
     * @return the current Pair Plus bet amount
     */
    public int getPairPlusBet() {
        return pairPlusBet;
    }

    /**
     * Sets the player's Play bet.
     * @param playBet the amount to set for the Play bet
     */
    public void setPlayBet(int playBet) {
        this.playBet = playBet;
    }

    /**
     * Retrieves the player's Play bet.
     * @return the current Play bet amount
     */
    public int getPlayBet() {
        return playBet;
    }

    /**
     * Updates the player's total winnings by adding the given amount.
     * Positive amounts represent gains, and negative amounts represent losses.
     * @param amount the amount to add to the total winnings
     */
    public void updateTotalWinnings(int amount) {
        totalWinnings += amount;
    }

    /**
     * Retrieves the player's total winnings.
     * @return the current total winnings amount
     */
    public int getTotalWinnings() {
        return totalWinnings;
    }

    /**
     * Resets the player's total winnings to zero.
     */
    public void resetTotalWinnings() {
        totalWinnings = 0;
    }

    /**
     * Resets the player's state for a new game.
     * Clears the hand and resets all bets to zero.
     */
    public void resetForNewGame() {
        hand.clear(); // Remove all cards from the player's hand
        anteBet = 0; // Reset Ante bet
        pairPlusBet = 0; // Reset Pair Plus bet
        playBet = 0; // Reset Play bet
    }
}
