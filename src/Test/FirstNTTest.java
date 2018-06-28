package Test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ContextFreeGrammar.ContextFreeGrammar;

public class FirstNTTest {
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
					"A -> &\n" + 
					"B -> &\n" + 
					"C -> &\n" + 
					"D -> a | &";
	}

	@Test
	public void firstNTG0Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[0]);
		
		Set<String> expected = new HashSet<String>();
		
		assertEquals(expected, op.getFirstNT().get("S"));
		assertEquals(expected, op.getFirstNT().get("B"));
		assertEquals(expected, op.getFirstNT().get("C"));
	}
	
	@Test
	public void firstNTG1Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[1]);
		Set<String> expected = new HashSet<String>();
		
		assertEquals(expected, op.getFirstNT().get("A"));
		assertEquals(expected, op.getFirstNT().get("B"));
		assertEquals(expected, op.getFirstNT().get("C"));
		assertEquals(expected, op.getFirstNT().get("D"));
		assertEquals(expected, op.getFirstNT().get("E"));
		expected.add("A");
		expected.add("B");
		expected.add("C");
		assertEquals(expected, op.getFirstNT().get("S"));
	}
	
	@Test
	public void firstNTG2Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[2]);
		Set<String> expected = new HashSet<String>();
		
		assertEquals(expected, op.getFirstNT().get("A"));
		assertEquals(expected, op.getFirstNT().get("B"));
		
		expected.add("A");
		expected.add("B");
		
		assertEquals(expected, op.getFirstNT().get("S"));
	}
	
	@Test
	public void firstNTG3Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[3]);
		Set<String> expected = new HashSet<String>();
		
		assertEquals(expected, op.getFirstNT().get("B"));
		assertEquals(expected, op.getFirstNT().get("C"));
		
		expected.add("B");
		expected.add("C");
		
		assertEquals(expected, op.getFirstNT().get("S"));
	}
	
	@Test
	public void firstNTG4Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[4]);
		Set<String> expected = new HashSet<String>();
		
		assertEquals(expected, op.getFirstNT().get("S"));
	}
	
	@Test
	public void firstNTG5Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[5]);
		Set<String> expected = new HashSet<String>();
		
		assertEquals(expected, op.getFirstNT().get("B"));
		assertEquals(expected, op.getFirstNT().get("C"));
		assertEquals(expected, op.getFirstNT().get("D"));
		
		expected.add("D");
		assertEquals(expected, op.getFirstNT().get("A"));
		
		expected.add("A");
		expected.add("B");
		expected.add("C");
		assertEquals(expected, op.getFirstNT().get("S"));
	}
	
	@Test
	public void firstNTG6Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[6]);
		Set<String> expected = new HashSet<String>();
		assertEquals(expected, op.getFirstNT().get("A"));
		
		expected.add("A");
		assertEquals(expected, op.getFirstNT().get("S"));
		assertEquals(expected, op.getFirstNT().get("B"));
	}
	
	@Test
	public void firstNTG7Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[7]);
		Set<String> expected = new HashSet<String>();
		assertEquals(expected, op.getFirstNT().get("E1"));
		assertEquals(expected, op.getFirstNT().get("T1"));
		assertEquals(expected, op.getFirstNT().get("F"));
		
		expected.add("T");
		expected.add("F");
		assertEquals(expected, op.getFirstNT().get("E"));
		expected.remove("T");
		assertEquals(expected, op.getFirstNT().get("T"));
	}
	
	@Test
	public void firstNTG8Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[8]);
		Set<String> expected = new HashSet<String>();
		assertEquals(expected, op.getFirstNT().get("Y"));
		assertEquals(expected, op.getFirstNT().get("Z"));
		expected.add("Z");
		expected.add("Y");
		assertEquals(expected, op.getFirstNT().get("X"));
	}
	
	@Test
	public void firstNTG9Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[9]);
		Set<String> expected = new HashSet<String>();
		assertEquals(expected, op.getFirstNT().get("A"));
		assertEquals(expected, op.getFirstNT().get("B"));
		assertEquals(expected, op.getFirstNT().get("C"));
		assertEquals(expected, op.getFirstNT().get("D"));
		
		expected.add("A");
		expected.add("B");
		expected.add("C");
		expected.add("D");

		assertEquals(expected, op.getFirstNT().get("S"));
	}

}
