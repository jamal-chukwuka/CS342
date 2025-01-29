
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import model.Card;

class CardTest {

	@Test
	void testConstructorWithValidInputs() {
		Card card = new Card('C', 11);
		assertEquals('C', card.getSuit());
		assertEquals(11, card.getValue());
	}
	
	@Test 
	void testToStringForFaceCard() {
		Card card = new Card('D', 13);
		assertEquals("King of Diamonds", card.toString());
	}
	
	@Test
	void testConstructorWithInvalidSuit() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> new Card('X', 10));
        assertEquals("Invalid suit. Must be 'C', 'D', 'H', or 'S'.", exception.getMessage());
	}
	
	
	

}


