package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ContextFreeGrammar.ContextFreeGrammar;
import ContextFreeGrammar.Operator;

public class Main {
	public static void main(String[] args) {
		
		/*
		String grammar;
		grammar = "S -> x y z | a B C\n" + 
					"B -> c | c d\n" + 
					"C -> e g | d f\n";
		ContextFreeGrammar cfg  = ContextFreeGrammar.isValidCFG(grammar);
		//System.out.println(cfg.getGrammarProductions("S"));
		System.out.println(cfg.getDefinition());
		*/
		
		/*
		String grammar = "S -> x y z | a B C\n" + 
				"B -> c | c d\n" + 
				"C -> e g | d f\n";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		Set<String> firstS = op.getFirst("S");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		System.out.println(firstS);
		System.out.println(firstB);
		System.out.println(firstC);
		*/
		
		/*
		String grammar = "S -> A B C D E\n" + 
				"A -> a | &\n" + 
				"B -> b | &\n" + 
				"C -> c\n"	+ 
				"D -> d | &\n" + 
				"E -> e | &";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		Set<String> firstS = op.getFirst("S");
		Set<String> firstA = op.getFirst("A");
		Set<String> firstB = op.getFirst("B");
		Set<String> firstC = op.getFirst("C");
		Set<String> firstD = op.getFirst("D");
		Set<String> firstE = op.getFirst("E");
		System.out.println(firstS);
		System.out.println(firstA);
		System.out.println(firstB);
		System.out.println(firstC);
		System.out.println(firstD);
		System.out.println(firstE);
		*/
		
		/*
		String grammar = "E -> T E1\n" + 
				"E1 -> + T E1 | &\n" + 
				"T -> F T1\n" + 
				"T1 -> * F T1 | &\n" + 
				"F -> id | ( E )";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		Set<String> firstE = op.getFirst("E");
		Set<String> firstE1 = op.getFirst("E1");
		Set<String> firstT = op.getFirst("T");
		Set<String> firstT1 = op.getFirst("T1");
		Set<String> firstF = op.getFirst("F");
		System.out.println(firstE);
		System.out.println(firstE1);
		System.out.println(firstT);
		System.out.println(firstT1);
		System.out.println(firstF);
		*/
		
		/*
		String grammar = "S -> B b | C d\n" + 
				"B -> a B | &\n" + 
				"C -> c C | &\n";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		//Set<String> firstX = op.getFirst("X");
		//Set<String> firstY = op.getFirst("Y");
		//Set<String> firstZ = op.getFirst("Z");
		//System.out.println(firstX);
		//System.out.println(firstY);
		//System.out.println(firstZ);
		Operator operator = new Operator(op);
		System.out.println(operator.isFactored());
		*/
		
		/*
		String grammar = "A -> a B | b C | c B | c D | d | b D | b B | &\n" + 
				"B -> a B | &\n" + 
				"C -> c C | &\n" +
				"D -> d D | &";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		//Set<String> firstX = op.getFirst("X");
		//Set<String> firstY = op.getFirst("Y");
		//Set<String> firstZ = op.getFirst("Z");
		//System.out.println(firstX);
		//System.out.println(firstY);
		//System.out.println(firstZ);
		Operator operator = new Operator(op);
		System.out.println(operator.isFactored());
		ContextFreeGrammar newOp = operator.factorGrammar(op);
		newOp.getDefinition();
		*/
		
		/*
		String grammar = "A -> a B | B C | c B | C D | d | b D | B d | b C | & | b \n" + 
				"B -> b B | &\n" + 
				"C -> b C | &\n" +
				"D -> d D | &";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		//Set<String> firstX = op.getFirst("X");
		//Set<String> firstY = op.getFirst("Y");
		//Set<String> firstZ = op.getFirst("Z");
		//System.out.println(firstX);
		//System.out.println(firstY);
		//System.out.println(firstZ);
		Operator operator = new Operator(op);
		System.out.println(operator.isFactored());
		operator.factorGrammar(op);
		*/
		
		/*
		String grammar = "A -> a B | B C | c B | C D | d | b D | B d | & \n" + 
				"B -> b B | &\n" + 
				"C -> b C | & | c\n" +
				"D -> d D | &";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		Operator operator = new Operator(op);
		//System.out.println(operator.isFactored());
		//operator.factorGrammar(op);
		List<String> teste = new ArrayList<String>();
		teste.add("B");
		teste.add("C");
		teste.add("f");
		System.out.println(operator.getProductionFirstSet(teste));
		*/
		/*
		String grammar = "A -> B b | C d \n" + 
				"B -> C a B | &\n" + 
				"C -> c C | &\n";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		Operator operator = new Operator(op);
		ArrayList<ContextFreeGrammar> results = new ArrayList<>();
		results = operator.factorGrammar(6);
		System.out.println(results.get(results.size()-1).getDefinition());
		//System.out.println(operator.isFactored());
		ContextFreeGrammar newGrammar = operator.factorGrammar(op);
		System.out.println(newGrammar.getDefinition());
		newGrammar = operator.factorGrammar(newGrammar);
		System.out.println(newGrammar.getDefinition());
		newGrammar = operator.factorGrammar(newGrammar);
		System.out.println(newGrammar.getDefinition());
		newGrammar = operator.factorGrammar(newGrammar);
		System.out.println(newGrammar.getDefinition());
		newGrammar = operator.factorGrammar(newGrammar);
		System.out.println(newGrammar.getDefinition());
		*/
		
		/*
		String grammar = "S -> B b | C d \n" + 
				"B -> C a B | &\n" + 
				"C -> c C | &\n";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		Operator operator = new Operator(op);
		ArrayList<ContextFreeGrammar> results = new ArrayList<>();
		results = operator.factorGrammar(6);
		System.out.println(results.get(results.size()-1).getDefinition());
		*/
		
		/*
		String grammar = "S -> b c D | B c d\n" + 
				"B -> b B | b\n" + 
				"D -> d D | d";
		ContextFreeGrammar op = ContextFreeGrammar.isValidCFG(grammar);
		System.out.println(op.getDefinition());
		Operator operator = new Operator(op);
		ArrayList<ContextFreeGrammar> results = new ArrayList<>();
		results = operator.factorGrammar(11);
		System.out.println(results.get(0).getDefinition());
		System.out.println(results.get(1).getDefinition());
		System.out.println(results.get(2).getDefinition());
		System.out.println(results.get(3).getDefinition());
		System.out.println(results.get(4).getDefinition());
		System.out.println(results.get(5).getDefinition());
		System.out.println(results.get(6).getDefinition());
		System.out.println(results.get(7).getDefinition());
		System.out.println(results.get(8).getDefinition());
		System.out.println(results.get(9).getDefinition());
		System.out.println(results.get(10).getDefinition());
		//System.out.println(results.get(results.size()-1).getDefinition());
		 */
		
		ContextFreeGrammar cfg[];
		String[] grammar;
		int lengthValid = 10;

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
		for (String gra: grammar) {
			System.out.println(i);
			cfg[i++] = ContextFreeGrammar.isValidCFG(gra);
		}
		
		Operator op = new Operator(cfg[0]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[1]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[2]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[3]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[4]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[5]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[6]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[7]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[8]);
		System.out.println(op.hasLeftRecursion());
		op = new Operator(cfg[9]);
		System.out.println(op.hasLeftRecursion());
		

	}
}
