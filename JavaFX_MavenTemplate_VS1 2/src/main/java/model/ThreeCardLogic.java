package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ThreeCardLogic {
	
	  // Constant for the dealer's minimum qualifying hand (Queen High)
    public static final int HIGH_CARD_QUEEN = 12; // Queen has a value of 12

    // Constants for comparison results
    public static final int PLAYER_WIN = 2; // Indicates that the player wins
    public static final int DEALER_WIN = 1; // Indicates that the dealer wins
    public static final int TIE = 0;        // Indicates a tie


	  // Map of hand evaluators to their values
    private static final LinkedHashMap<Function<ArrayList<Card>, Boolean>, Integer> HAND_RANKS = new LinkedHashMap<>();

    static {
        HAND_RANKS.put(ThreeCardLogic::isStraightFlush, 1);
        HAND_RANKS.put(ThreeCardLogic::isThreeOfAKind, 2);
        HAND_RANKS.put(ThreeCardLogic::isStraight, 3);
        HAND_RANKS.put(ThreeCardLogic::isFlush, 4);
        HAND_RANKS.put(ThreeCardLogic::isPair, 5);
    }

    // Map of winnings multipliers for Pair Plus bets
    private static final Map<Integer, Integer> PAIR_PLUS_MULTIPLIERS = Map.of(
            1, 40, // Straight Flush
            2, 30, // Three of a Kind
            3, 6,  // Straight
            4, 3,  // Flush
            5, 1   // Pair
    );

    // Evaluates the value of a hand
    public static int evalHand(ArrayList<Card> hand) {
        if (hand == null || hand.size() < 3) {
            return 0; // Invalid or incomplete hand
        }

        return HAND_RANKS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(hand))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(0); // Default to 0 (High card)
    }


    // Evaluates Pair Plus winnings
    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        int handValue = evalHand(hand);
        return PAIR_PLUS_MULTIPLIERS.getOrDefault(handValue, 0) * bet;
    }

    // Compares dealer's hand and player's hand
    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int dealerValue = evalHand(dealer);
        int playerValue = evalHand(player);

        if (playerValue > dealerValue) return PLAYER_WIN;
        if (playerValue < dealerValue) return DEALER_WIN;

        // If ranks are equal, compare high cards
        ArrayList<Card> sortedDealer = new ArrayList<>(dealer);
        ArrayList<Card> sortedPlayer = new ArrayList<>(player);
        sortedDealer.sort(Comparator.comparingInt(Card::getValue).reversed());
        sortedPlayer.sort(Comparator.comparingInt(Card::getValue).reversed());

        for (int i = 0; i < sortedDealer.size(); i++) {
            if (sortedPlayer.get(i).getValue() > sortedDealer.get(i).getValue()) return PLAYER_WIN;
            if (sortedPlayer.get(i).getValue() < sortedDealer.get(i).getValue()) return DEALER_WIN;
        }

        return TIE; // Complete tie
    }


    // Helper: Checks for a straight flush
    public static boolean isStraightFlush(ArrayList<Card> hand) {
        return isFlush(hand) && isStraight(hand);
    }

    // Helper: Checks for three of a kind
    public static boolean isThreeOfAKind(ArrayList<Card> hand) {
        return hand.get(0).getValue() == hand.get(1).getValue() &&
               hand.get(1).getValue() == hand.get(2).getValue();
    }

    // Helper: Checks for a straight
    public static boolean isStraight(ArrayList<Card> hand) {
        ArrayList<Card> sortedHand = new ArrayList<>(hand);
        sortedHand.sort(Comparator.comparingInt(Card::getValue));
        return sortedHand.get(2).getValue() - sortedHand.get(1).getValue() == 1 &&
               sortedHand.get(1).getValue() - sortedHand.get(0).getValue() == 1;
    }

    // Helper: Checks for a flush
    public static boolean isFlush(ArrayList<Card> hand) {
        return hand.get(0).getSuit() == hand.get(1).getSuit() &&
               hand.get(1).getSuit() == hand.get(2).getSuit();
    }

    // Helper: Checks for a pair
    public static boolean isPair(ArrayList<Card> hand) {
        return hand.get(0).getValue() == hand.get(1).getValue() ||
               hand.get(1).getValue() == hand.get(2).getValue() ||
               hand.get(0).getValue() == hand.get(2).getValue();
    }
	
}
