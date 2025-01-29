package model;

import java.io.Serializable;
import java.util.Map;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    // Represents the suit of the card (Clubs, Diamonds, Hearts, Spades)
    private char suit; /* Valid values: ‘C’, ‘D’, ’S’, ‘H’ */

    // Represents the value of the card (2 to 14, where 11-14 correspond to Jack, Queen, King, Ace)
    private int value;

    // Mapping of face card values (Jack, Queen, King, Ace) to their string equivalents
    private static final Map<Integer, String> FACE_VALUES = Map.of(
            11, "Jack",
            12, "Queen",
            13, "King",
            14, "Ace"
    );

    // Mapping of suits to their respective codes for card image paths
    private static final Map<Character, String> SUIT_CODES = Map.of(
            'C', "c",  // Clubs
            'D', "d",  // Diamonds
            'H', "h",  // Hearts
            'S', "s"   // Spades
    );

    // Mapping of card values to their respective codes for card image paths
    private static final Map<Integer, String> VALUE_CODES = Map.of(
            14, "a",   // Ace
            13, "k",   // King
            12, "q",   // Queen
            11, "j"    // Jack
    );

    /**
     * Constructor to initialize a card with a specific suit and value.
     * 
     * @param suit the suit of the card ('C', 'D', 'H', 'S')
     * @param value the value of the card (2 to 14, where 11-14 are face cards)
     * @throws IllegalArgumentException if the suit or value is invalid
     */
    public Card(char suit, int value) {
        if (!isValidSuit(suit)) {
            throw new IllegalArgumentException("Invalid suit. Must be 'C', 'D', 'H', or 'S'.");
        }

        if (value < 2 || value > 14) {
            throw new IllegalArgumentException("Invalid value. Must be between 2 and 14.");
        }

        this.suit = suit;
        this.value = value;
    }

    /**
     * Gets the suit of the card.
     * 
     * @return the suit character ('C', 'D', 'H', 'S')
     */
    public char getSuit() {
        return suit;
    }

    /**
     * Gets the value of the card.
     * 
     * @return the card's value (2 to 14)
     */
    public int getValue() {
        return value;
    }

    /**
     * Validates if the provided suit is valid.
     * 
     * @param suit the suit to validate
     * @return true if the suit is valid, false otherwise
     */
    private boolean isValidSuit(char suit) {
        return SUIT_CODES.containsKey(suit);
    }

    /**
     * Converts a suit character to its full name.
     * 
     * @param suit the suit character
     * @return the full name of the suit
     * @throws IllegalStateException if the suit is unexpected
     */
    private String suitToFullName(char suit) {
        switch (suit) {
            case 'C':
                return "Clubs";
            case 'D':
                return "Diamonds";
            case 'H':
                return "Hearts";
            case 'S':
                return "Spades";
            default:
                throw new IllegalStateException("Unexpected value: " + suit);
        }
    }

    /**
     * Converts the card to a string representation (e.g., "Ace of Spades").
     * 
     * @return the string representation of the card
     */
    public String toString() {
        String faceValue = FACE_VALUES.getOrDefault(value, String.valueOf(value));
        return faceValue + " of " + suitToFullName(suit);
    }

    /**
     * Gets the image path for the card.
     * 
     * @return the relative path to the card's image file
     */
    /**
     * Gets the image path for the card.
     *
     * @return the relative path to the card's image file
     */
    /**
     * Gets the image path for the card.
     *
     * @return the relative path to the card's image file.
     */
    public String getImagePath() {
        // Ensure suit is mapped and converted to lowercase
        String suitCode = SUIT_CODES.get(suit);
        if (suitCode == null) {
            System.err.println("Error: No suit code found for suit " + suit);
            suitCode = String.valueOf(suit).toLowerCase(); // Fallback to lowercase suit
        }

        // Explicitly map face card values to correct lowercase letter codes
        String valueCode;
        if (VALUE_CODES.containsKey(value)) {
            valueCode = VALUE_CODES.get(value); // Use mapped value (a, k, q, j)
        } else {
            valueCode = String.valueOf(value); // Use number for non-face cards
        }

        // Construct the expected file path for the image
        String path = "/views/cards/simple_" + suitCode + "_" + valueCode + ".png";

        // Debugging statement to verify correct values
        System.out.println("Generated Image Path: " + path + " | Suit: " + suitCode + " | Value: " + valueCode);

        return path;
    }






    /**
     * Gets the image path for the card back.
     * 
     * @return the relative path to the card back image
     */
    public static String getCardBackPath() {
        String path = "/cards/Sparky.jpeg";

        // Debugging statement for logging the card back path
        System.out.println("Loading card back image: " + path);

        return path;
    }
}
