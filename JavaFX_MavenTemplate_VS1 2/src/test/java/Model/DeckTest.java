package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import model.Card;
import model.Deck;

class DeckTest {

	@Test
	void testForFullDeck() {
		Deck deck = new Deck();
		assertEquals(52, deck.size());
	}
	
	@Test
	void testSuitAndValueDistribution() {
	    Deck deck = new Deck();
	    Map<Character, Integer> suitCount = new HashMap<>();
	    Map<Integer, Integer> valueCount = new HashMap<>();

	    for (Card card : deck) {
	        suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
	        valueCount.put(card.getValue(), valueCount.getOrDefault(card.getValue(), 0) + 1);
	    }

	    assertEquals(4, suitCount.size(), "Deck should have 4 suits.");
	    suitCount.values().forEach(count -> assertEquals(13, count, "Each suit should have 13 cards."));
	    valueCount.values().forEach(count -> assertEquals(4, count, "Each value should appear 4 times."));
	}
	
	
	@Test
	void testDeckShuffling() {
	    Deck deck1 = new Deck();
	    Deck deck2 = new Deck();
	    deck2.newDeck(); // Reinitialize the second deck

	    // Check if at least one card is in a different position
	    boolean isShuffled = false;
	    for (int i = 0; i < deck1.size(); i++) {
	        if (!deck1.get(i).equals(deck2.get(i))) {
	            isShuffled = true;
	            break;
	        }
	    }

	    assertTrue(isShuffled, "Deck should be shuffled; at least one card should differ in position.");
	}
	
	@Test
	void testNewDeckCreation() {
	    Deck deck = new Deck();
	    deck.remove(0); // Remove one card
	    deck.newDeck(); // Reset the deck

	    assertEquals(52, deck.size(), "Deck should have 52 cards after calling newDeck().");
	}
	
	@Test
	void testCardRemoval() {
	    Deck deck = new Deck();
	    Card removedCard = deck.remove(0);

	    assertNotNull(removedCard, "Removed card should not be null.");
	    assertEquals(51, deck.size(), "Deck size should decrease by 1 after removing a card.");
	}


	

}
