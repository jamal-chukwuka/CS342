package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import model.Card;
import model.Player;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void testDefaultConstructor() {
        Player player = new Player();
        assertNotNull(player.getHand());
        assertTrue(player.getHand().isEmpty());
        assertEquals(0, player.getTotalWinnings());
    }

    @Test
    void testSetAndGetHand() {
        Player player = new Player();
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card('H', 11)); // Jack of Hearts
        hand.add(new Card('S', 12)); // Queen of Spades
        hand.add(new Card('D', 13)); // King of Diamonds

        player.setHand(hand);

        assertEquals(3, player.getHand().size());
        assertEquals("Jack of Hearts", player.getHand().get(0).toString());
        assertEquals("Queen of Spades", player.getHand().get(1).toString());
        assertEquals("King of Diamonds", player.getHand().get(2).toString());
    }

    @Test
    void testSetAndGetAnteBet() {
        Player player = new Player();
        player.setAnteBet(50);
        assertEquals(50, player.getAnteBet());
    }

    @Test
    void testSetAndGetPairPlusBet() {
        Player player = new Player();
        player.setPairPlusBet(100);
        assertEquals(100, player.getPairPlusBet());
    }

    @Test
    void testSetAndGetPlayBet() {
        Player player = new Player();
        player.setPlayBet(75);
        assertEquals(75, player.getPlayBet());
    }

    @Test
    void testUpdateAndGetTotalWinnings() {
        Player player = new Player();
        player.updateTotalWinnings(200);
        assertEquals(200, player.getTotalWinnings());

        player.updateTotalWinnings(-50);
        assertEquals(150, player.getTotalWinnings());
    }

    @Test
    void testResetTotalWinnings() {
        Player player = new Player();
        player.updateTotalWinnings(300);
        assertEquals(300, player.getTotalWinnings());

        player.resetTotalWinnings();
        assertEquals(0, player.getTotalWinnings());
    }

    @Test
    void testResetForNewGame() {
        Player player = new Player();
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card('H', 11)); // Jack of Hearts
        player.setHand(hand);
        player.setAnteBet(50);
        player.setPairPlusBet(100);
        player.setPlayBet(75);

        player.resetForNewGame();

        assertTrue(player.getHand().isEmpty());
        assertEquals(0, player.getAnteBet());
        assertEquals(0, player.getPairPlusBet());
        assertEquals(0, player.getPlayBet());
    }

    @Test
    void testNegativeAnteBet() {
        Player player = new Player();
        player.setAnteBet(-10); // Invalid value
        assertEquals(-10, player.getAnteBet()); // Ensure the value is set correctly
    }

    @Test
    void testLargeBets() {
        Player player = new Player();
        player.setAnteBet(Integer.MAX_VALUE);
        player.setPairPlusBet(Integer.MAX_VALUE);
        player.setPlayBet(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, player.getAnteBet());
        assertEquals(Integer.MAX_VALUE, player.getPairPlusBet());
        assertEquals(Integer.MAX_VALUE, player.getPlayBet());
    }

    @Test
    void testUpdateTotalWinningsWithZero() {
        Player player = new Player();
        player.updateTotalWinnings(0);
        assertEquals(0, player.getTotalWinnings());
    }

    @Test
    void testResetForNewGameDoesNotAffectTotalWinnings() {
        Player player = new Player();
        player.updateTotalWinnings(500);
        player.resetForNewGame();
        assertEquals(500, player.getTotalWinnings());
    }

    @Test
    void testMultipleUpdatesToTotalWinnings() {
        Player player = new Player();
        player.updateTotalWinnings(100);
        player.updateTotalWinnings(200);
        player.updateTotalWinnings(-50);

        assertEquals(250, player.getTotalWinnings());
    }

    @Test
    void testHandAfterNewGameReset() {
        Player player = new Player();
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card('S', 5));
        player.setHand(hand);

        player.resetForNewGame();
        assertTrue(player.getHand().isEmpty());
    }

    @Test
    void testResetAllBets() {
        Player player = new Player();
        player.setAnteBet(50);
        player.setPairPlusBet(75);
        player.setPlayBet(100);

        player.resetForNewGame();

        assertEquals(0, player.getAnteBet());
        assertEquals(0, player.getPairPlusBet());
        assertEquals(0, player.getPlayBet());
    }

    @Test
    void testTotalWinningsAfterNegativeUpdates() {
        Player player = new Player();
        player.updateTotalWinnings(-100);
        assertEquals(-100, player.getTotalWinnings());
    }

    @Test
    void testEdgeCaseEmptyHand() {
        Player player = new Player();
        assertTrue(player.getHand().isEmpty());
    }

    @Test
    void testEdgeCaseZeroBets() {
        Player player = new Player();
        assertEquals(0, player.getAnteBet());
        assertEquals(0, player.getPairPlusBet());
        assertEquals(0, player.getPlayBet());
    }

    @Test
    void testLargeNegativeBets() {
        Player player = new Player();
        player.setAnteBet(-Integer.MAX_VALUE);
        player.setPairPlusBet(-Integer.MAX_VALUE);
        player.setPlayBet(-Integer.MAX_VALUE);

        assertEquals(-Integer.MAX_VALUE, player.getAnteBet());
        assertEquals(-Integer.MAX_VALUE, player.getPairPlusBet());
        assertEquals(-Integer.MAX_VALUE, player.getPlayBet());
    }
}

