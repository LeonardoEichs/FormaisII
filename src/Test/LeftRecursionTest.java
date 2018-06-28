package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ContextFreeGrammar.ContextFreeGrammar;
import ContextFreeGrammar.Operator;

public class LeftRecursionTest {

	private static ContextFreeGrammar cfg[];
	private static String[] grammar;
	private static int lengthValid = 10;
	
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
					"B -> b | S\n" + 
					"C -> &\n" + 
					"D -> d";
		grammar[6] = "S -> A S B | d\n" + 
					"A -> a | &\n" + 
					"B -> A B | b | &";
		grammar[7] = "E -> T E1\n" + 
					"E1 -> + T E1 | &\n" + 
					"T -> F T1\n" + 
					"T1 -> * F T1 | &\n" + 
					"F -> id | ( E )";
		grammar[8] = "X -> X Z | Y\n" + 
					"Y -> m | n | &\n" + 
					"Z -> m";
		grammar[9] = "S -> A B | C D\n" + 
					"A -> & \n" + 
					"B -> &\n" + 
					"C -> &\n" + 
					"D -> a | &";
		cfg = new ContextFreeGrammar[lengthValid];
		int i = 0;
		for (String grammar: grammar) {
			System.out.println(i);
			cfg[i++] = ContextFreeGrammar.isValidCFG(grammar);
		}

	}

	@Test
	public void isFactoredtest() {
		Operator op = new Operator(cfg[0]);
		assertFalse(op.hasLeftRecursion());
		
		op = new Operator(cfg[1]);
		assertFalse(op.hasLeftRecursion());
		
		op = new Operator(cfg[2]);
		assertFalse(op.hasLeftRecursion());
		
		op = new Operator(cfg[3]);
		assertFalse(op.hasLeftRecursion());
		
		op = new Operator(cfg[4]);
		assertFalse(op.hasLeftRecursion());
		
		op = new Operator(cfg[5]);
		assertTrue(op.hasLeftRecursion());
		
		op = new Operator(cfg[6]);
		assertTrue(op.hasLeftRecursion());
		
		op = new Operator(cfg[7]);
		assertFalse(op.hasLeftRecursion());
		
		op = new Operator(cfg[8]);
		assertTrue(op.hasLeftRecursion());
		
		op = new Operator(cfg[9]);
		assertFalse(op.hasLeftRecursion());
	}
	
	@Test
	public void eliminateRecursionTest() {
		ContextFreeGrammar g = ContextFreeGrammar.isValidCFG(
				"S -> B b | C d\n" + 
				"B -> C a B | &\n" + 
				"C -> c C | & | B\n");
		Operator op = new Operator(g);
		assertTrue(op.hasLeftRecursion());
		String def = g.getDefinition();
		
		System.out.println(op.eliminateLeftRecursion().getDefinition());
		assertEquals("S -> C a B b | C d | b\n" + 
				"B -> C a B | &\n" + 
				"C -> c C C1 | C1\n" + 
				"C1 -> & | a B C1\n" + 
				"", op.eliminateLeftRecursion().getDefinition());
		
		assertEquals(def, g.getDefinition());
	}

}
