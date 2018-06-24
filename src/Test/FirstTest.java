package Test;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ContextFreeGrammar.ContextFreeGrammar;

public class FirstTest {

	private static String[] grammar;
	private static int lengthValid = 12;
	
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
		grammar[8] = "A -> B C | a b | H\n" + 
					"B -> D | &\n" + 
					"C -> f | G a | H E\n" + 
					"E -> e | J\n" + 
					"H -> K | & | a M\n" + 
					"M -> Z";
		grammar[9] = "A -> B C | a b | H\n" + 
					"B -> D | &\n" + 
					"C -> f | G a | H E\n" + 
					"E -> e | J\n" + 
					"H -> K | a M\n" + 
					"M -> Z";
		grammar[10] = "X -> Y Z\n" + 
					"Y -> m | n | &\n" + 
					"Z -> m";
		grammar[11] = "S -> A B | C D\n" + 
					"A -> &\n" + 
					"B -> &\n" + 
					"C -> &\n" + 
					"D -> a | &";
	}

	/**
	 * Test first set for G0
	 */
	@Test
	public void testFirstSetG0() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[0]);
		Set<String> firstS = op.getFirst("S");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> expectedFirst = new HashSet<String>();
		expectedFirst.add("a");
		expectedFirst.add("x");
		assertTrue(firstS.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("c");
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("e");
		expectedFirst.add("d");
		assertTrue(firstC.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G1
	 */
	@Test
	public void testFirstSetG1() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[1]);
		Set<String> firstS = op.getFirst("S");
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> firstD = op.getFirst("D");
		Set<String> firstE = op.getFirst("E");
		
		Set<String> expectedFirst = new HashSet<String>();
		expectedFirst.add("a");
		expectedFirst.add("b");
		expectedFirst.add("c");
		assertTrue(firstS.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("a");
		expectedFirst.add("&");
		assertTrue(firstA.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("b");
		expectedFirst.add("&");
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("c");
		assertTrue(firstC.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("d");
		expectedFirst.add("&");
		assertTrue(firstD.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("e");
		expectedFirst.add("&");
		assertTrue(firstE.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G2
	 */
	@Test
	public void testFirstSetG2() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[2]);
		Set<String> firstS = op.getFirst("S");
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> expectedFirst = new HashSet<String>();
		
		expectedFirst.add("&");
		assertTrue(firstS.equals(expectedFirst));
		assertTrue(firstA.equals(expectedFirst));
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();
	}

	/**
	 * Test first set for G3
	 */
	@Test
	public void testFirstSetG3() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[3]);
		Set<String> firstS = op.getFirst("S");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> expectedFirst = new HashSet<String>();

		expectedFirst.add("a");
		expectedFirst.add("b");
		expectedFirst.add("c");
		expectedFirst.add("d");
		assertTrue(firstS.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("a");
		expectedFirst.add("&");
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("c");
		expectedFirst.add("&");
		assertTrue(firstC.equals(expectedFirst));
		expectedFirst.clear();
	}

	/**
	 * Test first set for G4
	 */
	@Test
	public void testFirstSetG4() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[4]);
		Set<String> firstS = op.getFirst("S");
		Set<String> expectedFirst = new HashSet<String>();

		expectedFirst.add("(");
		expectedFirst.add("&");
		assertTrue(firstS.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G5
	 */
	@Test
	public void testFirstSetG5() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[5]);
		Set<String> firstS = op.getFirst("S");
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> firstD = op.getFirst("D");
		Set<String> expectedFirst = new HashSet<String>();
		
		expectedFirst.add("a");
		expectedFirst.add("b");
		expectedFirst.add("d");
		expectedFirst.add("&");
		assertTrue(firstS.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("d");
		expectedFirst.add("a");
		expectedFirst.add("&");
		assertTrue(firstA.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("b");
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("&");
		assertTrue(firstC.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("d");
		assertTrue(firstD.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G6
	 */
	@Test
	public void testFirstSetG6() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[6]);
		Set<String> firstS = op.getFirst("S");
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> expectedFirst = new HashSet<String>();
		expectedFirst.add("a");
		expectedFirst.add("d");
		assertTrue(firstS.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("a");
		assertTrue(firstA.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("a");
		expectedFirst.add("b");
		expectedFirst.add("&");
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G7
	 */
	@Test
	public void testFirstSetG7() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[7]);
		Set<String> firstE = op.getFirst("E");
		Set<String> firstE1 = op.getFirst("E1");
		Set<String> firstT = op.getFirst("T");
		Set<String> firstT1 = op.getFirst("T1");
		Set<String> firstF = op.getFirst("F");
		Set<String> expectedFirst = new HashSet<String>();

		expectedFirst.add("id");
		expectedFirst.add("(");
		assertTrue(firstE.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("+");
		expectedFirst.add("&");
		assertTrue(firstE1.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("id");
		expectedFirst.add("(");
		assertTrue(firstT.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("*");
		expectedFirst.add("&");
		assertTrue(firstT1.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("id");
		expectedFirst.add("(");
		assertTrue(firstF.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G8
	 */
	@Test
	public void testFirstSetG8() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[8]);
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> firstE = op.getFirst("E");
		Set<String> firstH = op.getFirst("H");
		Set<String> firstM = op.getFirst("M");
		Set<String> expectedFirst = new HashSet<String>();

		expectedFirst.add("a");
		expectedFirst.add("&");
		expectedFirst.add("f");
		expectedFirst.add("e");
		assertTrue(firstA.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("&");
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("f");
		expectedFirst.add("a");
		expectedFirst.add("e");
		assertTrue(firstC.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("e");
		assertTrue(firstE.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("a");
		expectedFirst.add("&");
		assertTrue(firstH.equals(expectedFirst));
		expectedFirst.clear();
		
		assertTrue(firstM.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G9
	 */
	@Test
	public void testFirstSetG9() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[9]);
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> firstE = op.getFirst("E");
		Set<String> firstH = op.getFirst("H");
		Set<String> firstM = op.getFirst("M");
		Set<String> expectedFirst = new HashSet<String>();

		expectedFirst.add("a");
		expectedFirst.add("f");
		assertTrue(firstA.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("&");
		assertTrue(firstB.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("f");
		expectedFirst.add("a");
		assertTrue(firstC.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("e");
		assertTrue(firstE.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("a");
		assertTrue(firstH.equals(expectedFirst));
		expectedFirst.clear();
		
		assertTrue(firstM.equals(expectedFirst));
		expectedFirst.clear();
	}
	
	/**
	 * Test first set for G10
	 */
	@Test
	public void testFirstSetG10() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[10]);
		Set<String> firstX = op.getFirst("X");
		Set<String> firstY = op.getFirst("Y");
		Set<String> firstZ = op.getFirst("Z");
		Set<String> expectedFirst = new HashSet<String>();

		expectedFirst.add("m");
		expectedFirst.add("n");
		assertTrue(firstX.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("m");
		expectedFirst.add("n");
		expectedFirst.add("&");
		assertTrue(firstY.equals(expectedFirst));
		expectedFirst.clear();
		
		expectedFirst.add("m");
		assertTrue(firstZ.equals(expectedFirst));
		expectedFirst.clear();
	}	
	
	/**
	 * Test first set for G11
	 */
	@Test
	public void testFirstSetG11() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[11]);
		Set<String> firstS = op.getFirst("S");
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> firstD = op.getFirst("D");
		Set<String> expectedFirst = new HashSet<String>();
		
		expectedFirst.add("a");
		expectedFirst.add("&");
		assertTrue(firstS.equals(expectedFirst));
		expectedFirst.clear();

		expectedFirst.add("&");
		assertTrue(firstA.equals(expectedFirst));

		assertTrue(firstB.equals(expectedFirst));

		assertTrue(firstC.equals(expectedFirst));

		expectedFirst.add("a");
		assertTrue(firstD.equals(expectedFirst));
		expectedFirst.clear();
	}
}
