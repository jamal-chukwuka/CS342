//package Model;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//
//import model.Card;
//
//class CardTest {
//
//    @Test
//    void testValidCardCreation() {
//        Card card = new Card('H', 11); // Jack of Hearts
//        assertEquals('H', card.getSuit());
//        assertEquals(11, card.getValue());
//    }
//
//    @Test
//    void testToStringFaceCard() {
//        Card card = new Card('S', 12); // Queen of Spades
//        assertEquals("Queen of Spades", card.toString());
//    }
//
//    @Test
//    void testToStringNumericCard() {
//        Card card = new Card('D', 7); // 7 of Diamonds
//        assertEquals("7 of Diamonds", card.toString());
//    }
//
//    @Test
//    void testToStringAce() {
//        Card card = new Card('C', 14); // Ace of Clubs
//        assertEquals("Ace of Clubs", card.toString());
//    }
//
//    @Test
//    void testInvalidSuitThrowsException() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            new Card('X', 10); // Invalid suit
//        });
//        assertEquals("Invalid suit. Must be 'C', 'D', 'H', or 'S'.", exception.getMessage());
//    }
//
//    @Test
//    void testInvalidLowValueThrowsException() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            new Card('H', 1); // Invalid value
//        });
//        assertEquals("Invalid value. Must be between 2 and 14.", exception.getMessage());
//    }
//
//    @Test
//    void testInvalidHighValueThrowsException() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            new Card('S', 15); // Invalid value
//        });
//        assertEquals("Invalid value. Must be between 2 and 14.", exception.getMessage());
//    }
//
//    @Test
//    void testGetImagePathFaceCard() {
//        Card card = new Card('H', 11); // Jack of Hearts
//        assertEquals("/cards/simple_h_j.png", card.getImagePath());
//    }
//
//    @Test
//    void testGetImagePathNumericCard() {
//        Card card = new Card('D', 9); // 9 of Diamonds
//        assertEquals("/cards/simple_d_9.png", card.getImagePath());
//    }
//
//    @Test
//    void testGetImagePathAce() {
//        Card card = new Card('C', 14); // Ace of Clubs
//        assertEquals("/cards/simple_c_a.png", card.getImagePath());
//    }
//
//    @Test
//    void testGetImagePathKing() {
//        Card card = new Card('S', 13); // King of Spades
//        assertEquals("/cards/simple_s_k.png", card.getImagePath());
//    }
//
//    @Test
//    void testGetCardBackPath() {
//        assertEquals("/cards/Sparky.jpeg", Card.getCardBackPath());
//    }
//
//    @Test
//    void testEdgeCaseLowestValidCard() {
//        Card card = new Card('C', 2); // 2 of Clubs
//        assertEquals("2 of Clubs", card.toString());
//        assertEquals("/cards/simple_c_2.png", card.getImagePath());
//    }
//
//    @Test
//    void testEdgeCaseHighestValidCard() {
//        Card card = new Card('S', 14); // Ace of Spades
//        assertEquals("Ace of Spades", card.toString());
//        assertEquals("/cards/simple_s_a.png", card.getImagePath());
//    }
//
//    @Test
//    void testSuitToFullNameClubs() {
//        Card card = new Card('C', 10); // 10 of Clubs
//        assertTrue(card.toString().contains("Clubs"));
//    }
//
//    @Test
//    void testSuitToFullNameDiamonds() {
//        Card card = new Card('D', 8); // 8 of Diamonds
//        assertTrue(card.toString().contains("Diamonds"));
//    }
//
//    @Test
//    void testSuitToFullNameHearts() {
//        Card card = new Card('H', 6); // 6 of Hearts
//        assertTrue(card.toString().contains("Hearts"));
//    }
//
//    @Test
//    void testSuitToFullNameSpades() {
//        Card card = new Card('S', 4); // 4 of Spades
//        assertTrue(card.toString().contains("Spades"));
//    }
//
//    @Test
//    void testEqualitySameSuitAndValue() {
//        Card card1 = new Card('H', 10);
//        Card card2 = new Card('H', 10);
//        assertEquals(card1.toString(), card2.toString());
//    }
//
//    @Test
//    void testInequalityDifferentSuit() {
//        Card card1 = new Card('H', 10);
//        Card card2 = new Card('D', 10);
//        assertNotEquals(card1.toString(), card2.toString());
//    }
//
//    @Test
//    void testInequalityDifferentValue() {
//        Card card1 = new Card('H', 10);
//        Card card2 = new Card('H', 11);
//        assertNotEquals(card1.toString(), card2.toString());
//    }
//    
//}
//    