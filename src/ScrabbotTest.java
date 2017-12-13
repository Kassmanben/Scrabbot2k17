import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.*;


public class ScrabbotTest {
	
	private Scrabbot bot;
	@Before
	public void setUp() throws Exception {
		bot = new Scrabbot("dict.txt");
	}
	
	@Test
	public void bagInitializes() {
		assertFalse(bot.letterBag.isEmpty());
		assertEquals(2,Collections.frequency(bot.letterBag, 'c'));
		assertEquals(12,Collections.frequency(bot.letterBag, 'e'));
		assertEquals(4,Collections.frequency(bot.letterBag, 's'));
		assertEquals(1,Collections.frequency(bot.letterBag, 'x'));
	}
	
	@Test
	public void dictionaryInitializes(){
		assertFalse(bot.dictionary.isEmpty());
	}
	
	@Test
	public void lettersInitialize(){
		assertFalse(bot.letterValues.isEmpty());
		assertEquals(bot.getLetterValue('x'),8);
		assertEquals(bot.getLetterValue('_'),0);
		assertEquals(bot.getLetterValue('a'),1);
	}
	
	@Test
	public void wordValuesFilled() {
		assertFalse(bot.wordValues.isEmpty());
		assertEquals(bot.getWordValue("hello"), 8);
		assertEquals(bot.getWordValue("cars"), 6);	
	}
	
	@Test
	public void runWithRack(){
		bot.runWithRack("aireumn");
		assertEquals("uremia", bot.big);
	}
	
	@Test
	public void whichLettersAreDifferent(){
		assertEquals(bot.whichLettersAreDifferent("facetime", "effacees"),"fes");
	}
	
	@Test
	public void rackGenerates() {
		String testR = null;
		assertEquals(null,testR);
		testR = bot.generateRandomRack();
		assertFalse(testR.isEmpty());
	}
	
	@Test
	public void permutationsCorrect(){
		bot.runWithAllPermutations("uswindp");
		//Requires this assertion, because words with the same number of points are treated the same
		assertEquals("", bot.whichLettersAreDifferent("upwinds",bot.big));
		assertEquals(bot.bigS, bot.getWordValue(bot.big));
		
	}
}
