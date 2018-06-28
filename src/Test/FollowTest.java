package Test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ContextFreeGrammar.ContextFreeGrammar;

public class FollowTest {

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
		op.calculateFollow();

		
		Set<String> followS = op.getFollow("S");
		Set<String> followB = op.getFollow("B");
		Set<String> followC = op.getFollow("C");
		Set<String> expectedFollow = new HashSet<String>();
		expectedFollow.add("$");
		assertTrue(followS.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("e");
		expectedFollow.add("d");
		assertTrue(followB.equals(expectedFollow));
		expectedFollow.clear();

		expectedFollow.add("$");
		assertTrue(followC.equals(expectedFollow));
		expectedFollow.clear();	
		}
	
	@Test
	public void firstNTG1Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[1]);
		op.calculateFollow();

		Set<String> followS = op.getFollow("S");
		Set<String> followA = op.getFollow("A");
		Set<String> followB = op.getFollow("B");
		Set<String> followC = op.getFollow("C");
		Set<String> followD = op.getFollow("D");
		Set<String> followE = op.getFollow("E");
		Set<String> expectedFollow = new HashSet<String>();
		
		expectedFollow.add("$");
		assertTrue(followS.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("b");
		expectedFollow.add("$");
		assertTrue(followA.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("c");
		assertTrue(followB.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("d");
		expectedFollow.add("e");
		expectedFollow.add("$");
		assertTrue(followC.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("e");
		expectedFollow.add("$");
		assertTrue(followD.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("$");
		assertTrue(followE.equals(expectedFollow));
		expectedFollow.clear();
	}
	
	@Test
	public void firstNTG2Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[2]);
		op.calculateFollow();

		Set<String> followS = op.getFollow("S");
		Set<String> followA = op.getFollow("A");
		Set<String> followB = op.getFollow("B");
		Set<String> expectedFollow = new HashSet<String>();
		
		expectedFollow.add("$");
		assertTrue(followS.equals(expectedFollow));
		assertTrue(followA.equals(expectedFollow));
		assertTrue(followB.equals(expectedFollow));
	}
	
	@Test
	public void firstNTG3Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[3]);
		op.calculateFollow();

		Set<String> followS = op.getFollow("S");
		Set<String> followA = op.getFollow("B");
		Set<String> followB = op.getFollow("C");
		Set<String> expectedFollow = new HashSet<String>();
		
		expectedFollow.add("$");
		assertTrue(followS.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("b");
		assertTrue(followA.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("d");
		assertTrue(followB.equals(expectedFollow));
		expectedFollow.clear();
	}
	
	@Test
	public void firstNTG4Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[4]);
		op.calculateFollow();

		Set<String> followS = op.getFollow("S");
		Set<String> expectedFollow = new HashSet<String>();

		expectedFollow.add("$");
		expectedFollow.add(")");
		assertTrue(followS.equals(expectedFollow));
		expectedFollow.clear();
	}
	
	@Test
	public void firstNTG5Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[5]);
		op.calculateFollow();
		
		Set<String> followS = op.getFollow("S");
		Set<String> followA = op.getFollow("A");
		Set<String> followB = op.getFollow("B");
		Set<String> followC = op.getFollow("C");
		Set<String> followD = op.getFollow("D");
		Set<String> expectedFollow = new HashSet<String>();
		
		expectedFollow.add("$");
		assertTrue(followS.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("b");
		assertTrue(followA.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("$");
		assertTrue(followB.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("$");
		assertTrue(followC.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("b");
		assertTrue(followD.equals(expectedFollow));
		expectedFollow.clear();
	}
	
	@Test
	public void firstNTG6Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[6]);
		op.calculateFollow();

		Set<String> followS = op.getFollow("S");
		Set<String> followA = op.getFollow("A");
		Set<String> followB = op.getFollow("B");
		Set<String> expectedFollow = new HashSet<String>();
		
		expectedFollow.add("$");
		expectedFollow.add("a");
		expectedFollow.add("b");
		assertTrue(followS.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("$");
		expectedFollow.add("a");
		expectedFollow.add("b");
		assertTrue(followS.equals(expectedFollow));
		
		expectedFollow.add("d");
		assertTrue(followA.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("$");
		expectedFollow.add("a");
		expectedFollow.add("b");
		assertTrue(followB.equals(expectedFollow));
		expectedFollow.clear();
	}
	
	@Test
	public void firstNTG7Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[7]);
		op.calculateFollow();

		Set<String> followE = op.getFollow("E");
		Set<String> followE1 = op.getFollow("E1");
		Set<String> followT = op.getFollow("T");
		Set<String> followT1 = op.getFollow("T1");
		Set<String> followF = op.getFollow("F");
		Set<String> expectedFollow = new HashSet<String>();
		
		expectedFollow.add("$");
		expectedFollow.add(")");
		assertTrue(followE.equals(expectedFollow));
		
		assertTrue(followE1.equals(expectedFollow));
		
		expectedFollow.add("+");
		assertTrue(followT.equals(expectedFollow));
		
		assertTrue(followT1.equals(expectedFollow));
		
		expectedFollow.add("*");
		assertTrue(followF.equals(expectedFollow));
		expectedFollow.clear();
	}
	
	@Test
	public void firstNTG9Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[8]);
		op.calculateFollow();

		Set<String> followX = op.getFollow("X");
		Set<String> followY = op.getFollow("Y");
		Set<String> followZ = op.getFollow("Z");
		Set<String> expectedFollow = new HashSet<String>();
		
		expectedFollow.add("$");
		assertTrue(followX.equals(expectedFollow));
		expectedFollow.clear();
		expectedFollow.add("m");
		assertTrue(followY.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("$");
		assertTrue(followZ.equals(expectedFollow));
		expectedFollow.clear();
	}
	
	@Test
	public void firstNTG10Test() {
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar[9]);
		op.calculateFollow();

		Set<String> followS = op.getFollow("S");
		Set<String> followA = op.getFollow("A");
		Set<String> followB = op.getFollow("B");
		Set<String> followC = op.getFollow("C");
		Set<String> followD = op.getFollow("D");
		Set<String> expectedFollow = new HashSet<String>();
		expectedFollow.add("$");
		assertTrue(followS.equals(expectedFollow));
		
		assertTrue(followA.equals(expectedFollow));

		assertTrue(followB.equals(expectedFollow));
		
		expectedFollow.add("a");
		assertTrue(followC.equals(expectedFollow));
		expectedFollow.clear();
		
		expectedFollow.add("$");
		assertTrue(followD.equals(expectedFollow));
		expectedFollow.clear();
	}

}
