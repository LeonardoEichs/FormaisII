package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ContextFreeGrammar.ContextFreeGrammar;
import ContextFreeGrammar.Operator;

public class FactoringTest {

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
					"A -> & | c\n" + 
					"B -> d\n" + 
					"C -> & | b\n" + 
					"D -> d";
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
		assertFalse(op.isFactored());
		
		op = new Operator(cfg[1]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[2]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[3]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[4]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[5]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[6]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[7]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[8]);
		assertTrue(op.isFactored());
		
		op = new Operator(cfg[9]);
		assertFalse(op.isFactored());
	}
	
	
	@Test 
	public void testFactorG1() {
		ContextFreeGrammar g = ContextFreeGrammar.isValidCFG(
				"S -> b c D | B c d\n" + 
				"B -> b B | b\n" + 
				"D -> d D | d");
		g.setId("G1");
		Operator op = new Operator(g);
		ArrayList<ContextFreeGrammar> results = new ArrayList<>();
		assertFalse(op.isFactored()); // not factored
		results = op.factorGrammar(11);
		op = new Operator(results.get(results.size()-1));
		assertTrue(op.isFactored()); // must be factored
		
		assertEquals("S -> b S1\n" + 
				"S3 -> & | D1\n" + 
				"B -> b B1\n" + 
				"D -> d D1\n" + 
				"D1 -> & | d D1\n" +
				"S1 -> b B1 c d | c S2\n" + 
				"B1 -> & | b B1\n" + 
				"S2 -> d S3\n" + 
				"", results.get(results.size()-1).getDefinition());	}
	
	@Test 
	public void testFactorG2() {
		ContextFreeGrammar g = ContextFreeGrammar.isValidCFG(
				"A -> B b | C d \n" + 
				"B -> C a B | &\n" + 
				"C -> c C | &");
		g.setId("G1");
		Operator op = new Operator(g);
		ArrayList<ContextFreeGrammar> results = new ArrayList<>();
		assertFalse(op.isFactored()); // not factored
		results = op.factorGrammar(6);
		op = new Operator(results.get(results.size()-1));
		assertTrue(op.isFactored()); // must be factored
		
		assertEquals("A -> a B b | d | c A1 | b\n" + 
				"A1 -> C A2\n" + 
				"A2 -> a B b | d\n" + 
				"B -> C a B | &\n" + 
				"C -> & | c C\n" + 
				"", results.get(results.size()-1).getDefinition());
	}
	
	@Test 
	public void testFactorG3() {
		ContextFreeGrammar g = ContextFreeGrammar.isValidCFG(
				"S -> a S | a B | d S \n" + 
				"B -> b B | b");
		g.setId("G1");
		Operator op = new Operator(g);
		ArrayList<ContextFreeGrammar> results = new ArrayList<>();
		assertFalse(op.isFactored()); // not factored
		results = op.factorGrammar(6);
		op = new Operator(results.get(results.size()-1));
		assertTrue(op.isFactored()); // must be factored
		String received = results.get(results.size()-1).getDefinition();
		assertEquals("S -> a S1 | d S\n" + 
				"B -> b B1\n" + 
				"S1 -> S | B\n" + 
				"B1 -> & | B\n" +
				"", received);
	}
	
	@Test 
	public void testFactorG4() {
		ContextFreeGrammar g = ContextFreeGrammar.isValidCFG(
				"S -> a S | a B | d S \n" + 
				"B -> b B | b");
		g.setId("G1");
		Operator op = new Operator(g);
		ArrayList<ContextFreeGrammar> results = new ArrayList<>();
		assertFalse(op.isFactored()); // not factored
		results = op.factorGrammar(6);
		op = new Operator(results.get(results.size()-1));
		assertTrue(op.isFactored()); // must be factored
		String received = results.get(results.size()-1).getDefinition();
		assertEquals("S -> a S1 | d S\n" + 
				"B -> b B1\n" + 
				"S1 -> S | B\n" + 
				"B1 -> & | B\n" +
				"", received);
	}


}
