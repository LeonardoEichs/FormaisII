package Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import ContextFreeGrammar.ContextFreeGrammar;

public class CFGTest {
	private static int lengthValid = 11;
	private static int lengthInvalid = 5;

	private static String[] grammarInvalid;
	private static String[] grammar;
	
	/**
	 * Initializes grammars
	 * @throws Exception
	 */
	@Before
	public void setUp() {
		grammar = new String[lengthValid];
		grammar[0] = "S -> x y z | a B C\n" + 
					"B -> c | c d\n" + 
					"C -> e g | d f\n";
		grammar[1] = "S -> A B C D E\n" + 
					"A -> a | &\n" + 
					"B -> b | &\n" + 
					"C -> c\n"	+ 
					"D -> d | &\n" + 
					"E -> e | &";
		grammar[2] = "S -> A B\n" + 
					"A -> &\n" + 
					"B -> &";
		grammar[3] = "S -> B b | C d\n" + 
					"B -> a B | &\n" + 
					"C -> c C | &\n";
		grammar[4] = "S -> ( S ) | &";
		grammar[5] = "S -> A B | C\n" + 
					"A -> D | a | &\n" + 
					"B -> b\n" + 
					"C -> &\n" + 
					"D -> d";
		grammar[6] = "S -> A S B | d\n" + 
					"A -> a\n" + 
					"B -> A B |b | &";
		grammar[7] = "E -> T E1\n" + 
					"E1 -> + T E1 | &\n" + 
					"T -> F T1\n" + 
					"T1 -> * F T1 | &\n" + 
					"F -> id | ( E )";
		grammar[8] = "X -> Y Z\n" + 
					"Y -> m | n | &\n" + 
					"Z -> m";
		grammar[9] = "S -> A B | C D\n" + 
					"A -> &\n" + 
					"B -> &\n" + 
					"C -> &\n" + 
					"D -> a | &";
		grammar[10] = "S -> A B | C D\n" +
					"A -> & | c\n" +
					"B -> d\n" +
					"C -> & | b\n" +
					"D -> d";
	}
	
	/**
	 * Initializes invalid grammars
	 * @throws Exception
	 */
	@Before
	public void setUpInvalid(){
		grammarInvalid = new String[lengthInvalid];
		grammarInvalid[0] = "S -> a B C | a S B C\n" + 
						"CB -> c Z\n" + 
						"CZ -> W Z\n" +
						"WZ -> W C\n" +
						"WC -> B C\n" +
						"aB -> a b\n" +
						"bB -> b b\n" +
						"bC -> b c\n" +
						"cC -> c c";
		grammarInvalid[1] = "S -> a b c | a A b c\n" + 
						"Ab -> b A\n" + 
						"Ac -> B b c c\n" + 
						"bB -> B b\n"	+ 
						"aB -> a a | a a A\n";
		grammarInvalid[2] = "S -> a B c\n" + 
						"aB -> c A\n" + 
						"Ac -> d";
		grammarInvalid[3] = "A -> B C | a b | H\n" + 
						"B -> D | &\n" + 
						"C -> f | G a | H E\n" + 
						"E -> e | J\n" + 
						"H -> K | & | a M\n" + 
						"M -> Z";
		grammarInvalid[4] = "A -> B C | a b | H\n" + 
						"B -> D | &\n" + 
						"C -> f | G a | H E\n" + 
						"E -> e | J\n" + 
						"H -> K | a M\n" + 
						"M -> Z";
	}

	
	@Test
	public void testValidGrammar() {
		ContextFreeGrammar cfg[] = new ContextFreeGrammar[lengthValid];
		int i = 0;
		for (String grammar: grammar) {
			System.out.println(i);
			cfg[i++] = ContextFreeGrammar.isValidCFG(grammar);
		}
		for (ContextFreeGrammar l : cfg) {
			assertNotNull(l);
		}
	}
	
	@Test
	public void testInvalidGrammar() {
		ContextFreeGrammar cfg[] = new ContextFreeGrammar[lengthInvalid];
		int i = 0;
		for (String grammar: grammarInvalid) {
			cfg[i++] = ContextFreeGrammar.isValidCFG(grammar);
		}
		for (ContextFreeGrammar l : cfg) {
			assertNull(l);
		}
	}

	
	
}
