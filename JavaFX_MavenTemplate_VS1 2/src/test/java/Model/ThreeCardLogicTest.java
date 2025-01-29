package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import model.Card;
import model.ThreeCardLogic;

class ThreeCardLogicTest {

    private ArrayList<Card> createHand(char suit1, int value1, char suit2, int value2, char suit3, int value3) {
        return new ArrayList<>(Arrays.asList(
            new Card(suit1, value1),
            new Card(suit2, value2),
            new Card(suit3, value3)
        ));
    }
    
    @Test
    void testStraightFlush() {
        ArrayList<Card> hand = createHand('C', 10, 'C', 11, 'C', 12);
        assertEquals(1, ThreeCardLogic.evalHand(hand), "Straight Flush should evaluate to 1.");
    }
    
    
    @Test
    void testThreeOfAKind() {
        ArrayList<Card> hand = createHand('H', 7, 'C', 7, 'D', 7);
        assertEquals(2, ThreeCardLogic.evalHand(hand), "Three of a Kind should evaluate to 2.");
    }

    @Test
    void testStraight() {
        ArrayList<Card> hand = createHand('C', 5, 'D', 6, 'H', 7);
        assertEquals(3, ThreeCardLogic.evalHand(hand), "Straight should evaluate to 3.");
    }
    
    @Test
    void testFlush() {
        ArrayList<Card> hand = createHand('H', 4, 'H', 9, 'H', 2);
        assertEquals(4, ThreeCardLogic.evalHand(hand), "Flush should evaluate to 4.");
    }


    @Test
    void testPair() {
        ArrayList<Card> hand = createHand('C', 8, 'H', 8, 'S', 3);
        assertEquals(5, ThreeCardLogic.evalHand(hand), "Pair should evaluate to 5.");
    }
    
    @Test
    void testHighCard() {
        ArrayList<Card> hand = createHand('D', 3, 'H', 5, 'C', 8);
        assertEquals(0, ThreeCardLogic.evalHand(hand), "High Card should evaluate to 0.");
    }
    
    @Test
    void testStraightFlushWinnings() {
        ArrayList<Card> hand = createHand('C', 10, 'C', 11, 'C', 12);
        assertEquals(400, ThreeCardLogic.evalPPWinnings(hand, 10), "Straight Flush with $10 bet should pay $400.");
    }
    
    @Test
    void testThreeOfAKindWinnings() {
        ArrayList<Card> hand = createHand('H', 7, 'C', 7, 'D', 7);
        assertEquals(300, ThreeCardLogic.evalPPWinnings(hand, 10), "Three of a Kind with $10 bet should pay $300.");
    }

    @Test
    void testStraightWinnings() {
        ArrayList<Card> hand = createHand('C', 5, 'D', 6, 'H', 7);
        assertEquals(60, ThreeCardLogic.evalPPWinnings(hand, 10), "Straight with $10 bet should pay $60.");
    }
    
    @Test
    void testFlushWinnings() {
        ArrayList<Card> hand = createHand('H', 4, 'H', 9, 'H', 2);
        assertEquals(30, ThreeCardLogic.evalPPWinnings(hand, 10), "Flush with $10 bet should pay $30.");
    }
    
    @Test
    void testPairWinnings() {
        ArrayList<Card> hand = createHand('C', 8, 'H', 8, 'S', 3);
        assertEquals(10, ThreeCardLogic.evalPPWinnings(hand, 10), "Pair with $10 bet should pay $10.");
    }
    
    @Test
    void testHighCardNoWinnings() {
        ArrayList<Card> hand = createHand('D', 3, 'H', 5, 'C', 8);
        assertEquals(0, ThreeCardLogic.evalPPWinnings(hand, 10), "High Card should pay $0.");
    }

    @Test
    void testPlayerWinsStraightFlush() {
        ArrayList<Card> player = createHand('C', 10, 'C', 11, 'C', 12);
        ArrayList<Card> dealer = createHand('H', 5, 'H', 6, 'H', 7);
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), "Player with Straight Flush should win.");
    }
    
    @Test
    void testDealerWinsHigherPair() {
        ArrayList<Card> player = createHand('C', 8, 'H', 8, 'S', 3);
        ArrayList<Card> dealer = createHand('D', 9, 'H', 9, 'C', 5);
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer with higher Pair should win.");
    }
    
//    @Test
//    void testTieSamePair() {
//        ArrayList<Card> player = createHand('C', 8, 'H', 8, 'S', 3);
//        ArrayList<Card> dealer = createHand('D', 8, 'H', 8, 'C', 5);
//        assertEquals(0, ThreeCardLogic.compareHands(dealer, player), "Both hands with same Pair should tie.");
//    }
    
    @Test
    void testPlayerWinsHighCard() {
        ArrayList<Card> player = createHand('D', 10, 'H', 11, 'C', 13);
        ArrayList<Card> dealer = createHand('H', 5, 'H', 7, 'D', 9);
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), "Player with higher High Card should win.");
    }

    
    @Test
    void testDealerWinsHighCard() {
        ArrayList<Card> player = createHand('C', 4, 'H', 6, 'D', 9);
        ArrayList<Card> dealer = createHand('S', 10, 'H', 11, 'C', 13);
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer with higher High Card should win.");
    }
    
    @Test
    void testEmptyHand() {
        ArrayList<Card> hand = new ArrayList<>();
        assertEquals(0, ThreeCardLogic.evalHand(hand), "Empty hand should evaluate to 0.");
    }


//    @Test
//    void testSingleCardHand() {
//        ArrayList<Card> hand = createHand('C', 7, 'C', 0, 'C', 0);
//        assertEquals(0, ThreeCardLogic.evalHand(hand), "Single card hand should evaluate to 0.");
//    }

    @Test
    void testInvalidSuitHandling() {
        assertThrows(IllegalArgumentException.class, () -> new Card('X', 7), "Invalid suit should throw exception.");
    }
    
    @Test
    void testInvalidValueHandling() {
        assertThrows(IllegalArgumentException.class, () -> new Card('C', 1), "Invalid value should throw exception.");
    }

//    @Test
//    void testMultipleDeckEvaluations() {
//        ArrayList<Card> hand1 = createHand('C', 8, 'H', 8, 'S', 3);
//        ArrayList<Card> hand2 = createHand('D', 9, 'H', 9, 'C', 5);
//        assertNotEquals(ThreeCardLogic.evalHand(hand1), ThreeCardLogic.evalHand(hand2), "Different hands should have different evaluations.");
//    }




}