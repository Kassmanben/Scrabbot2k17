import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TrieTest {

	Trie t = new Trie("dict.txt", 10);

	@Before
	public void setUp() throws Exception {
	}
	@Test
	public void getNodesInitializes(){
		assertFalse(t.getNodes().isEmpty());
	}
	
	@Test
	public void testContainsWordString() {
		for (String word : t.getDictionary()) {
			assertTrue(t.containsWord(word));
		}
	}
	
	@Test
	public void addWord(){
		t.addWord("arglebargle");
		assertFalse(t.getNodes().isEmpty());
	}
	
	

}
