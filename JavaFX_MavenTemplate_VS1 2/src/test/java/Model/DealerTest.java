package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Card;
import model.Dealer;

class DealerTest {

	private Dealer dealer;

    @BeforeEach
    public void setUp() {
        dealer = new Dealer();
    }

    @Test
    public void testDealHandReturnsThreeCards() {
        ArrayList<Card> hand = dealer.dealHand();
        assertNotNull(hand, "The dealt hand should not be null.");
        assertEquals(3, hand.size(), "The hand should contain exactly 3 cards.");
    }

//    @Test
//    public void testDealHandReshufflesDeckWhenLow() {
//        // Simulate a low deck by dealing until fewer than 3 cards remain
//        for (int i = 0; i < 17; i++) {
//            dealer.dealHand();
//        }
//        // This next call should reshuffle the deck
//        ArrayList<Card> hand = dealer.dealHand();
//        assertNotNull(hand, "The dealt hand should not be null.");
//        assertEquals(3, hand.size(), "The hand should contain exactly 3 cards.");
//    }

    @Test
    public void testSetDealersHandStoresCorrectHand() {
        ArrayList<Card> sampleHand = new ArrayList<>();
        sampleHand.add(new Card('H', 10)); // 10 of Hearts
        sampleHand.add(new Card('S', 11)); // Jack of Spades
        sampleHand.add(new Card('D', 14)); // Ace of Diamonds

        dealer.setDealersHand(sampleHand);
        ArrayList<Card> retrievedHand = dealer.getDealersHand();

        assertEquals(sampleHand.size(), retrievedHand.size(), "The stored hand should have the same size as the input.");
        assertEquals(sampleHand, retrievedHand, "The retrieved hand should match the input hand.");
    }

    @Test
    public void testGetDealersHandReturnsCopy() {
        ArrayList<Card> sampleHand = new ArrayList<>();
        sampleHand.add(new Card('H', 5)); // 5 of Hearts
        sampleHand.add(new Card('C', 7)); // 7 of Clubs
        sampleHand.add(new Card('S', 13)); // King of Spades

        dealer.setDealersHand(sampleHand);
        ArrayList<Card> retrievedHand = dealer.getDealersHand();

        // Modify the retrieved hand
        retrievedHand.clear();

        // Original hand in the dealer should remain unchanged
        ArrayList<Card> originalHand = dealer.getDealersHand();
        assertFalse(originalHand.isEmpty(), "The original hand in the dealer should not be affected.");
    }

    @Test
    public void testReshufflingDeckDoesNotDuplicateCards() {
        ArrayList<Card> allCards = new ArrayList<>();
        for (int i = 0; i < 17; i++) { // Deal enough hands to force reshuffling
            allCards.addAll(dealer.dealHand());
        }

        // Check for duplicate cards in the combined dealt cards
        long uniqueCards = allCards.stream().distinct().count();
        assertEquals(allCards.size(), uniqueCards, "All dealt cards should be unique.");
    }
}
