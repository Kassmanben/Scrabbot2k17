import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scrabbot {

	public ArrayList<String> dictionary;
	public Map<String, Integer> wordValues;
	public Map<Character, Integer> letterValues;
	public ArrayList<Character> letterBag;
	public ArrayList<Character> letterRack;
	public HashMap<String, Integer> alreadySeen;
	public char blank = '_';
	public String big = "a";
	public int bigS = 0;
	public Trie t;
	public ArrayList<String> allWords;
	public String originalRack;

	public char[] alphabet = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };
	public char[] scrabbleAlphabet = { 'q', 'z', 'j', 'x', 'k', 'f', 'h', 'v',
			'w', 'y', 'b', 'c', 'm', 'p', 'd', 'g', 'a', 'e', 'i', 'l', 'n',
			'o', 'r', 's', 't', 'u', '_' };

	public static void print(Object s) {
		StdOut.println(s);
	}

	/**
	 * Initializes Scrabbot with a dictionary from the file provided
	 * 
	 * @param filename
	 *            String representing the filename of the lexicon Note: The Trie
	 *            and dictionary are initialized with the max word length set to
	 *            100, but this can be lowered to limit the length of words
	 *            allowed
	 */
	public Scrabbot(String filename) {
		t = new Trie(filename, 100);
		initializeGameDictionary(filename, 100);
		initializeBag();
		initializeletterValues();
		fillWordValues();
		allWords = new ArrayList<String>();
	}

	/**
	 * Runs the Scrabbot with a randomly generated rack of tiles, using the
	 * efficient search method to find the highest scoring word possible
	 */
	public void run() {
		String rack = generateRandomRack();
		rack = sortRack(rack);
		originalRack = rack;
		StdOut.println("EFFICIENT RUN\n\tRandom rack: " + rack.toUpperCase());
		nodeSearch(rack);
		if (big.length() > 1) {
			print("The word worth the most points is: " + big + "\nPoints: "
					+ getWordValue(big));
		} else {
			print("This rack contains no valid words");
		}
		/* Can be uncommented to see the full list of words and point values */
		// ArrayList<String> sorted = sortAllWordsByPoints();
		// for (String s : sorted) {
		// print(s + "\t" + getWordValue(s));
		// }
	}

	/**
	 * Runs the Scrabbot with a user generated rack of tiles, using the
	 * efficient search method to find the highest scoring word possible
	 * 
	 * @param rack
	 *            String of tiles provided by the user, used to return the list
	 *            of words that can be made from these letters
	 */
	public String runWithRack(String rack) {
		originalRack = rack;
		rack = sortRack(rack);
		StdOut.println("EFFICIENT RUN\n\tTesting rack: " + rack.toUpperCase());
		nodeSearch(rack);
		if (big.length() > 1) {
			print("The word worth the most points is: " + big + "\nPoints: "
					+ getWordValue(big));
		} else {
			print("This rack contains no valid words");
		}
		/* Can be uncommented to see the full list of words and point values */
		// ArrayList<String> sorted = sortAllWordsByPoints();
		// for (String s : sorted) {
		// print(s + "\t" + getWordValue(s));
		// }
		return big;
	}

	/**
	 * Runs the Scrabbot with a randomly generated rack of tiles, using the
	 * expensive permutation method
	 */
	public void runWithAllPermutations(String rack) {
		originalRack = rack;
		StdOut.println("PERMUTATIONS \n\tTesting rack: " + rack.toUpperCase());
		alreadySeen = new HashMap<String, Integer>();
		for (int i = rack.length(); i > 0; i--) {
			permutation(rack);
		}
		if (big.length() > 1) {
			print("The word worth the most points is: " + big + "\nPoints: "
					+ getWordValue(big));
		} else {
			print("This rack contains no valid words");
		}
	}

	/**
	 * Sorts the rack of tiles by point value of the tiles
	 * 
	 * @param rack
	 *            String of tiles to be sorted
	 * @return String of sorted letters
	 */
	public String sortRack(String rack) {
		String str = "";
		for (int i = 0; i < scrabbleAlphabet.length; i++) {
			for (char l : rack.toLowerCase().toCharArray()) {
				if (l == scrabbleAlphabet[i]) {
					str += l;
				}
			}
		}
		return str;
	}

	/**
	 * Sorts a list of TrieEdges by point value of the letters of the edge names
	 * 
	 * @param edges
	 *            ArrayList of TrieEdges to be sorted
	 * @return ArrayList of sorted TrieEdges
	 */
	public ArrayList<TrieEdge> sortEdgeList(ArrayList<TrieEdge> edges) {
		ArrayList<TrieEdge> temp = new ArrayList<TrieEdge>();
		for (int i = 0; i < scrabbleAlphabet.length; i++) {
			for (TrieEdge e : edges) {
				if (e.getEdgeName() == scrabbleAlphabet[i]) {
					temp.add(e);
				}
			}
		}
		return temp;
	}

	/**
	 * Sorts the final list of all possible words by length
	 * 
	 * @return ArrayList of sorted words, by length
	 */
	public ArrayList<String> sortAllWordsByLength() {
		ArrayList<String> t = new ArrayList<String>();
		for (int i = 0; i < allWords.size(); i++) {
			String max = "";
			for (String s : allWords) {
				if (s.length() > max.length() && !t.contains(s)) {
					max = s;
				}
			}
			if (!max.equals("")) {
				t.add(max);
			}
		}
		return t;
	}

	/**
	 * Sorts the final list of all possible words by point value of the word
	 * 
	 * @return ArrayList of sorted words, by point value
	 */
	public ArrayList<String> sortAllWordsByPoints() {
		ArrayList<String> t = new ArrayList<String>();
		for (int i = 0; i < allWords.size(); i++) {
			String max = "";
			int points = 0;
			for (String s : allWords) {
				if (getWordValue(s) > points && !t.contains(s)) {
					max = s;
					points = getWordValue(s);
				}
			}
			if (!max.equals("")) {
				t.add(max);
			} else {
				return t;
			}
		}
		return t;
	}

	/**
	 * Returns all of the letters that are different between the original rack
	 * and the proposed word. Accounts for blanks, including situation where the
	 * blank has replaced a letter which is contained in the rack to create a
	 * new word with two of those letters
	 * 
	 * @param a
	 *            String, representing the original set of letters
	 * @param b
	 *            String, representing the new set of letters
	 * 
	 * @return the difference between the two strings, letterwise
	 */
	public String whichLettersAreDifferent(String a, String b) {
		String str = "";
		ArrayList<Character> lista = new ArrayList<Character>();
		for (char letter : a.toCharArray()) {
			lista.add(letter);
		}
		ArrayList<Character> listb = new ArrayList<Character>();
		for (char letter : b.toCharArray()) {
			listb.add(letter);
		}
		for (char n : listb) {
			if (!lista.contains(n)) {
				str += n;
			} else {
				lista.remove(lista.indexOf(n));
			}
		}
		return str;
	}

	/**
	 * Allows the nodeSearch function to be called with one parameter
	 * 
	 * @param str
	 *            String representing the rack of tiles
	 */
	public void nodeSearch(String str) {
		nodeSearch(t.getNodes().get(0), str, "", '_', '_');
	}

	/**
	 * Recursively finds all possible words that exist in the Trie
	 * 
	 * @param n
	 *            TrieNode, the current TrieNode being searched for edges
	 * @param str
	 *            String representing the tiles "still on the rack" that haven't
	 *            been placed onto the finished word
	 * @param word
	 *            String representing the tiles in the finished word, or the
	 *            path that has been taken down the branch thus far
	 * @param blank
	 *            char representing the blank tile, if it exists on this rack
	 */
	public void nodeSearch(TrieNode n, String str, String word, char blank1,
			char blank2) {
		if(getWordValue(big)>50){
			return;
		}
		// If the current node is a valid ending of a word, add the word to the
		// list of all words, checking whether it is currently the highest
		// scoring and whether there is a blank tile being used
		if (n.isTerminal() && dictionary.contains(word)) {
			String temp = whichLettersAreDifferent(originalRack, word);
			for (int i = 0; i < temp.length(); i++) {
				word = word.replaceFirst(String.valueOf(temp.charAt(i)), "_");
			}
			if (getWordValue(word) >= getWordValue(big)) {
				big = word;
			}
			allWords.add(word);
		}

		// Fills an ArrayList with all possible branches to explore, based on
		// the remaining letters on the rack
		ArrayList<TrieEdge> temp = new ArrayList<TrieEdge>();
		for (TrieEdge e : n.edgesOutOf) {
			if (str.contains(String.valueOf(e.getEdgeName()))
					|| str.contains(String.valueOf('_'))) {
				temp.add(e);
			}
		}
		if (temp.isEmpty()) {
			return;
		}
		// Prioritizes higher-scoring letters to check
		temp = sortEdgeList(temp);
		// Switches in the valid tiles to check if they are words, accounting
		// for blank tiles
		for (TrieEdge e : temp) {
			try {
				nodeSearch(
						t.getNodes().get(e.getTo()),
						str.substring(0, str.indexOf(e.getEdgeName()))
								+ str.substring(str.indexOf(e.getEdgeName()) + 1),
						word + e.getEdgeName(), blank1, blank2);

			} catch (Exception k) {
				if (str.indexOf('_') != str.lastIndexOf('_')) {
					for (TrieEdge f : t.getNodes().get(e.getTo()).edgesOutOf) {
						nodeSearch(
								t.getNodes().get(e.getTo()),
								str.substring(0, str.indexOf("_"))
										+ str.substring(str.indexOf("_") + 1,
												str.lastIndexOf('_'))
										+ str.substring(str.lastIndexOf('_') + 1),
								word + e.getEdgeName() + f.getEdgeName(), e
										.getEdgeName(), f.getEdgeName());
						nodeSearch(
								t.getNodes().get(e.getTo()),
								str.substring(0, str.indexOf("_"))
										+ str.substring(str.indexOf("_") + 1,
												str.lastIndexOf('_'))
										+ str.substring(str.lastIndexOf('_') + 1),
								f.getEdgeName() + word + e.getEdgeName(), e
										.getEdgeName(), f.getEdgeName());
					}
				} else if (str.contains(String.valueOf('_'))) {
					nodeSearch(
							t.getNodes().get(e.getTo()),
							str.substring(0, str.indexOf("_"))
									+ str.substring(str.indexOf("_") + 1), word
									+ e.getEdgeName(), e.getEdgeName(), blank2);

				}
			}
		}
		return;
	}

	/**
	 * Fills the lookup table of word values, based on the lexicon provided Note
	 * that a 50-point bonus is provided for 7 letter words
	 */
	public void fillWordValues() {
		wordValues = new HashMap<String, Integer>();
		for (String word : dictionary) {
			int value = 0;
			for (char letter : word.toCharArray()) {
				value += getLetterValue(letter);
			}
			if (word.length() == 7) {
				value += 50;
			}
			wordValues.put(word, value);
		}
	}

	/**
	 * Allows the recursive function to be called with only one parameter
	 * 
	 * @param s
	 *            String representing the set of tiles to be permuted
	 */
	public void permutation(String s) {
		permutation("", s);
	}

	/**
	 * Recursively searches all possible permutations of all lengths greater
	 * than 2 to see if they are valid words, then marks the highest scoring
	 * word
	 * 
	 * @param prefix
	 *            String representing the current substring of the permutation
	 *            being considered
	 * @param s
	 *            String representing the substring of the original permutation
	 *            not being considered
	 */
	public void permutation(String prefix, String s) {
		int n = s.length();

		// Ignoring substrings of permutations that have already been
		// considered, checks whether the current permutation is a word, and
		// then whether is is the highest scoring word thus far
		if (!alreadySeen.containsKey(prefix)) {
			if (dictionary.contains(prefix)) {
				alreadySeen.put(prefix, prefix.hashCode());
				if (getWordValue(prefix) > bigS) {
					big = prefix;
					bigS = getWordValue(prefix);

				}
			}
		}

		// Repeats the above case, in the situation that the word has a blank
		// tile
		if (prefix.contains(String.valueOf('_')) && prefix.length() > 1
				&& !alreadySeen.containsKey(prefix)) {
			for (int i = 0; i < alphabet.length; i++) {
				String temp = prefix.replace('_', alphabet[i]);
				alreadySeen.put(prefix, prefix.hashCode());
				if (dictionary.contains(temp)) {
					if (getWordValue(temp) - getLetterValue(alphabet[i]) > bigS) {
						big = temp;
						bigS = getWordValue(temp) - getLetterValue(alphabet[i]);
					}
					break;
				}
			}
		}

		// Creates a new permutation, so long as the new word is longer than 2
		// letters
		if (n > 0) {
			for (int i = 0; i < n; i++)
				permutation(prefix + s.charAt(i),
						s.substring(0, i) + s.substring(i + 1, n));
		}
	}

	/**
	 * Prints the current bag state, how many total tiles there are and how many
	 * of each tile is left
	 */
	public void printBagState() {
		StdOut.println("Number of Tiles in the bag: " + letterBag.size());
		StdOut.println("Letter count: ");
		char temp = '_';
		int charCount = 0;
		Collections.sort(letterBag);
		for (char c : letterBag) {
			if (c == temp) {
				charCount++;
			} else {
				String ptst = "\t" + temp + ": " + charCount;
				StdOut.println(ptst.toUpperCase());
				temp = c;
				charCount = 1;
			}
		}
	}

	/**
	 * Generates a random rack of letters, pulling the tiles out of the bag so
	 * they cannot be reused
	 * 
	 * @return String of 7 random tiles pulled from the letter bag
	 */
	public String generateRandomRack() {
		letterRack = new ArrayList<Character>();
		String rack = "";

		// Pulls random chars from the letter bag, removing them each time they
		// are pulled
		for (int i = 0; i < 7; i++) {
			int randomIndex = (int) (Math.random() * (letterBag.size() - i));
			letterRack.add(letterBag.get(randomIndex));
			rack += letterBag.get(randomIndex);
			letterBag.remove(randomIndex);
		}
		return rack;
	}

	/** Creates a hashmap to determine the value of each letter */
	public void initializeletterValues() {
		letterValues = new HashMap<Character, Integer>();
		letterValues.clear();
		letterValues.put('a', 1);
		letterValues.put('b', 3);
		letterValues.put('c', 3);
		letterValues.put('d', 2);
		letterValues.put('e', 1);
		letterValues.put('f', 4);
		letterValues.put('g', 2);
		letterValues.put('h', 4);
		letterValues.put('i', 1);
		letterValues.put('j', 8);
		letterValues.put('k', 5);
		letterValues.put('l', 1);
		letterValues.put('m', 3);
		letterValues.put('n', 1);
		letterValues.put('o', 1);
		letterValues.put('p', 3);
		letterValues.put('q', 10);
		letterValues.put('r', 1);
		letterValues.put('s', 1);
		letterValues.put('t', 1);
		letterValues.put('u', 1);
		letterValues.put('v', 4);
		letterValues.put('w', 4);
		letterValues.put('x', 8);
		letterValues.put('y', 4);
		letterValues.put('z', 10);
		letterValues.put('_', 0);
	}

	/**
	 * Creates a "bag" of letters with the correct distribution of letters for
	 * Scrabble
	 */
	public void initializeBag() {
		letterBag = new ArrayList<Character>();
		letterBag.clear();
		letterBag.add('k');
		letterBag.add('j');
		letterBag.add('x');
		letterBag.add('q');
		letterBag.add('z');

		// After adding all the letters that only exist once in the bag, add the
		// correct distribution of remaining letters
		for (int i = 0; i < 12; i++) {
			if (i < 2) {
				letterBag.add('b');
				letterBag.add('c');
				letterBag.add('m');
				letterBag.add('p');
				letterBag.add('f');
				letterBag.add('h');
				letterBag.add('w');
				letterBag.add('v');
				letterBag.add('y');
				letterBag.add('_');
			}
			if (i < 3) {
				letterBag.add('g');
			}
			if (i < 4) {
				letterBag.add('l');
				letterBag.add('s');
				letterBag.add('u');
				letterBag.add('d');
			}
			if (i < 6) {
				letterBag.add('n');
				letterBag.add('r');
				letterBag.add('t');
			}
			if (i < 8) {
				letterBag.add('o');
			}
			if (i < 9) {
				letterBag.add('a');
				letterBag.add('i');
			}
			letterBag.add('e');

		}
	}

	/**
	 * Initializes the game dictionary based on the lexicon provided in the .txt
	 * file
	 * 
	 * @param filename
	 *            String representing the filename of the lexicon. Note: The
	 *            dictionary is initialized with the max word length set to 100,
	 *            but this can be lowered to limit the length of words allowed
	 */
	public void initializeGameDictionary(String filename, int maxLength) {
		dictionary = new ArrayList<String>();
		dictionary.clear();
		File f = new File("src/" + filename);
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null && line.length() < maxLength) {
				dictionary.add(line.toLowerCase());
			}
		} catch (Exception e) {
			System.err.println("File not found");
		}
	}

	/**
	 * Returns the letter value based on the pre-defined hashmap
	 * 
	 * @return int letter value of a given char
	 */
	public int getLetterValue(char letter) {
		try {
			return letterValues.get(letter);
		} catch (Exception e) {
			System.err.println(letter + " is not a tile in Scrabble");
		}
		return 0;
	}

	/**
	 * Returns the word value based on the pre-defined hashmap If there is a
	 * blank tile in the word, a new word value is determined
	 * 
	 * @return int word value of a given string
	 */
	public int getWordValue(String word) {
		try {
			return wordValues.get(word);
		} catch (Exception e) {
			int v = 0;
			for (char l : word.toCharArray()) {
				v += getLetterValue(l);
			}
			if (word.length() == 7) {
				v += 50;
			}
			return v;
		}
	}

	public void compareSpeeds(String rack) {
		long initialTime = System.currentTimeMillis();
		runWithRack(rack);
		print((System.currentTimeMillis() - initialTime) / 1000.0 + " seconds to find word");
		initialTime = System.currentTimeMillis();
		runWithAllPermutations(rack);
		print((System.currentTimeMillis() - initialTime) / 1000.0 + " seconds to find word");
	}

	public void runStateManager() {
		print("Please enter the mode: run, type in rack, permutations, compare speeds");
		String mode = StdIn.readLine();
		while (true) {
			big = "a";
			if (mode.toLowerCase().equals("run")) {
				run();
				mode = "";
			} else if (mode.toLowerCase().equals("permutations")) {
				print("Enter seven letters, using _ to represent blanks");
				String rack = StdIn.readLine();
				runWithAllPermutations(rack);
				mode = "";
			} else if (mode.toLowerCase().equals("type in rack")) {
				print("Enter seven letters, using _ to represent blanks");
				String rack = StdIn.readLine();
				runWithRack(rack);
				mode = "";
			} else if (mode.toLowerCase().equals("compare speeds")) {
				print("Enter seven letters, using _ to represent blanks");
				String rack = StdIn.readLine();
				compareSpeeds(rack);
				mode = "";
			} else {
				print("\nPlease enter the mode: run, type in rack, permutations, compare speeds, quit");
				mode = StdIn.readLine();
				if (mode.equals("quit")) {
					break;
				}
			}
			
		}
		
	}

	public static void main(String[] args) {
		Scrabbot s = new Scrabbot("dict.txt");
		s.runStateManager();
	}

}
