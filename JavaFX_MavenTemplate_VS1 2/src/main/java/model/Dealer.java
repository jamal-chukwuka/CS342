package model;

import java.util.ArrayList;

public class Dealer  {
    private Deck theDeck; // The deck of cards
    private ArrayList<Card> dealersHand; // Stores the dealer's current hand
    private static final int MINIMUM_CARDS = 3; // Minimum cards required to deal a hand

    // Constructor to initialize the deck and dealer's hand
    public Dealer() {
        theDeck = new Deck(); // Create a new deck
        theDeck.newDeck();    // Populate and shuffle the deck
        dealersHand = new ArrayList<>();
    }


    
//    public ArrayList<Card> dealHand() {
//        ArrayList<Card> hand = new ArrayList<>();
//        for (int i = 0; i < MINIMUM_CARDS; i++) {
//            if (!theDeck.isEmpty()) {
//                hand.add(theDeck.remove(0));
//            } else {
//                System.err.println("Deck is empty. Cannot deal more cards.");
//            }
//        }
//        System.out.println("Dealt hand: " + hand);
//        return hand;
//    }
    
    public ArrayList<Card> dealHand() {
        // If the deck is too small to deal, create a new shuffled deck
        if (theDeck.size() < 3) {
            System.out.println("Deck is empty or too small. Reshuffling...");
            theDeck.newDeck(); // Create and shuffle a new deck
        }

        // Deal 3 cards
        ArrayList<Card> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (theDeck.size() > 0) { // Ensure we don't remove from an empty deck
                hand.add(theDeck.remove(0));
            }
        }

        System.out.println("Dealt hand: " + hand);
        return hand;
    }


    public void setDealersHand(ArrayList<Card> hand) {
        this.dealersHand = new ArrayList<>(hand); // Store a copy of the dealer's hand
    }

    public ArrayList<Card> getDealersHand() {
        return new ArrayList<>(dealersHand); // Return a copy of the dealer's hand
    }

}
