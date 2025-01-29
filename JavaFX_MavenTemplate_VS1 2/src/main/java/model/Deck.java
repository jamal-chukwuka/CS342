package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of cards for use in the game.
 * This class extends ArrayList<Card>, making it a dynamic collection of cards with additional behavior.
 */
public class Deck extends ArrayList<Card> {

    /**
     * Constructor for the Deck class.
     * Automatically initializes the deck by creating a new set of shuffled cards.
     */
    public Deck() {
        newDeck(); // Populate and shuffle the deck immediately upon creation
    }

    /**
     * Resets the deck to a full standard 52-card set and shuffles it.
     * Clears any existing cards, generates cards for all suits and values, and shuffles the deck.
     */
    public void newDeck() {
        this.clear(); // Remove all existing cards from the deck
        
        // Define the four suits: Clubs, Diamonds, Hearts, Spades
        List<Character> suits = List.of('C', 'D', 'H', 'S');
        
        // Loop through each suit and create cards for values 2 through 14 (inclusive)
        suits.forEach(suit -> {
            for (int value = 2; value <= 14; value++) {
                this.add(new Card(suit, value)); // Add a new card to the deck
            }
        });
        
        // Shuffle the deck to randomize card order
        Collections.shuffle(this);
    }
}
