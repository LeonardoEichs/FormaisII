package ContextFreeGrammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Test.CFGTest;

public class Operator {
	
	private ContextFreeGrammar cfg;

	/**
	 * Constructor
	 */
	public Operator(ContextFreeGrammar cfg) {
		this.cfg = cfg;
	}
	
	/**
	 * verifies if there are left recursions
	 * @return true if there is some recursion
	 */
	public boolean hasLeftRecursion() {
		// For every vn
		for (String nt: this.cfg.getVn()) {
			// If the firstNT set from the current non terminal contains itself
			if (cfg.getFirstNT(nt).contains(nt)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if is factored
	 * @return true if it is factored
	 */
	public boolean isFactored() {
		Set<String> productions;
		Set<String> firstSet;
		HashSet<String> aux;
		for(String nonterminal : cfg.getVn()) {
			firstSet = new HashSet<String>();
			productions = cfg.getGrammarProductions(nonterminal);
			for(String prod : productions) {
				ArrayList<String> prodToken = tokenize(prod);
				aux = new HashSet<String>();
				int i = 0;
				do {
					aux.remove("&");
					if(i >= prodToken.size()) {
						aux.remove("&");
						break;
					}
					if(cfg.getVn().contains(prodToken.get(i))){
						aux.addAll(cfg.getFirst().get(prodToken.get(i)));
					}
					else {
						aux.add(prodToken.get(i));
					}
					i++;
				}while(aux.contains("&"));
				for(String symbol : aux) {
					if(firstSet.contains(symbol)) {
						return false;
					}
				}
				firstSet.addAll(aux);
			}
		}
		return true;
	}
	
	/**
	 * all first of the production
	 * @param production
	 * @return
	 */
	public Set<String> getProductionFirstSet(List<String> production){
		HashSet<String> aux;
		List<String> prodToken = production;
		aux = new HashSet<String>();
		int i = 0;
		do {
			aux.remove("&");
			if(i >= prodToken.size()) {
				break;
			}
			if(cfg.getVn().contains(prodToken.get(i))){
				aux.addAll(cfg.getFirst().get(prodToken.get(i)));
			}
			else {
				aux.add(prodToken.get(i));
			}
			i++;
		}while(aux.contains("&"));
		return aux;
	}
	
	/**
	 * factor grammar in a number of steps
	 * @param steps
	 * @return all steps
	 */
	public ArrayList<ContextFreeGrammar> factorGrammar(int steps) {
		ArrayList<ContextFreeGrammar> attempts = new ArrayList<>();
		if (isFactored()) {
			System.out.println(cfg.getId() + " is factored already.");
			return attempts;
		}

		ContextFreeGrammar previous = new ContextFreeGrammar(cfg);
		Operator newOperator = new Operator(previous);

		
		int i = 1;
		while(i <= steps) {
			ContextFreeGrammar g = ContextFreeGrammar.isValidCFG(mapToInput(newOperator.factorGrammar(previous).getProductions(), this.cfg.getInitialSymbol()));
			g.setId(cfg.getId() + " [F" + i + "]");
			newOperator = new Operator(g);
			attempts.add(g);
			if(newOperator.isFactored()) {
				break;
			}
			previous = new ContextFreeGrammar(g);
			i++;
		}
		

		if(newOperator.isFactored()) {
			System.out.println(cfg.getId() + " is factored in " + i + " steps.");
		}
		else {
			System.out.println(cfg.getId() + " is not factored in " + i + " steps.");

		}
		
		return attempts;
	}
	
	/**
	 * factorization of a grammar
	 * @param g
	 * @return factored g
	 */
	public ContextFreeGrammar factorGrammar(ContextFreeGrammar g) {
		ArrayList<String> nProd1, nProd2; // Production to be analyzed and look-ahead
		Set<String> prod;
		ContextFreeGrammar newG = new ContextFreeGrammar(g);
		Operator op = new Operator(newG);
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		boolean repeat = true;
		boolean stepDone = false;
		// Direct Factoring
		for(String nonterminal : newG.getVn()) {
			//repeat = true;
			HashSet<String> production = new HashSet<String>();
			prod = newG.getGrammarProductions(nonterminal);
			ArrayList<String> prods = getProdList(prod);
			String newNT = op.createNewVN(nonterminal);
			for(int i = 0; i < prods.size(); i++) {
				nProd1 = tokenize(prods.get(i));
				for(int j = i+1; j < prods.size(); j++) {
					nProd2 = tokenize(prods.get(j));
					if(nProd1.get(0).equals(nProd2.get(0)))	{ // Direct Factoring
						repeat = false;
						production.add(nProd1.get(0) + " " + newNT);
						if(newProductions.containsKey(nonterminal)) {
							production.addAll(newProductions.get(nonterminal));
						}
						newProductions.put(nonterminal, production);
						production = new HashSet<String>();
						if(newProductions.containsKey(newNT)) {
							production.addAll(newProductions.get(newNT));
						}
						if(nProd1.size() > 1) {
							production.add(prods.get(i).substring(2));
						}
						else {
							production.add("&");
						}
						if(nProd2.size() > 1) {
							production.add(prods.get(j).substring(2));
						}
						else {
							production.add("&");
						}
						newProductions.put(newNT, production);
						production = new HashSet<String>();
					}
				}
				if (!repeat) {
					// Did a step in the factorization
					// Add to the set the rest of the productions
					for(int k = i+1; k<prods.size(); k++) {
						ArrayList<String> nProd = tokenize(prods.get(k));
						if(!nProd1.get(0).equals(nProd.get(0))) {
							production = new HashSet<String>();
							if(newProductions.containsKey(nonterminal)) {
								production.addAll(newProductions.get(nonterminal));
							}
							production.add(prods.get(k));
							newProductions.put(nonterminal, production);
							production = new HashSet<String>();
						}
					}
					stepDone = true;
					break;
				}
				// Same symbol not found
				// Add to the set
				production = new HashSet<String>();
				if(newProductions.containsKey(nonterminal)) {
					production.addAll(newProductions.get(nonterminal));
				}
				production.add(prods.get(i));
				newProductions.put(nonterminal, production);
				production = new HashSet<String>();
			}
			if(stepDone) {
				for(String nt : newG.getVn()) {
					if(!newProductions.containsKey(nt)) {
						newProductions.put(nt, (HashSet<String>) g.getGrammarProductions(nt));
					}
				}
				return ContextFreeGrammar.isValidCFG(mapToInput(newProductions, g.getInitialSymbol()));
			}

		}
		// Indirect Factoring
		for(String nonterminal : newG.getVn()) {
			//repeat = true;
			HashSet<String> production = new HashSet<String>();
			prod = newG.getGrammarProductions(nonterminal);
			ArrayList<String> prods = getProdList(prod);
			String newNT = createNewVN(nonterminal);
			for(int i = 0; i < prods.size(); i++) {
				nProd1 = tokenize(prods.get(i));
				
				// Same symbol not found
				// Add to the set
				production = new HashSet<String>();
				if(Character.isUpperCase(nProd1.get(0).charAt(0))) {
					newProductions.remove(nonterminal);
					if(newProductions.containsKey(nonterminal)) {
						production.addAll(newProductions.get(nonterminal));
					}
					for(String prodi : g.getGrammarProductions(nProd1.get(0))) {
						ArrayList<String> nProdi = tokenize(prodi);
						if(nProdi.get(0).equals("&")) {
							production.add(prods.get(i).substring(nProd1.get(0).length()+1));
						}
						else {
							production.add(prodi.replaceAll("\\s+$", "") + " " + prods.get(i).replaceFirst("^\\s+", "").substring(nProd1.get(0).length()+1));
						}
					}
					newProductions.put(nonterminal, production);
					production = new HashSet<String>();
					for(int k = 0; k<prods.size(); k++) {
						ArrayList<String> nProd = tokenize(prods.get(k));
						if(!nProd1.get(0).equals(nProd.get(0))) {
							production = new HashSet<String>();
							if(newProductions.containsKey(nonterminal)) {
								production.addAll(newProductions.get(nonterminal));
							}
							production.add(prods.get(k));
							newProductions.put(nonterminal, production);
							production = new HashSet<String>();
						}
					}
					// Return of the new language
					return ContextFreeGrammar.isValidCFG(mapToInput(newProductions, g.getInitialSymbol()));
				}
			}
			if(stepDone) {
				return ContextFreeGrammar.isValidCFG(mapToInput(newProductions, g.getInitialSymbol()));
			}
		}
		return ContextFreeGrammar.isValidCFG(mapToInput(newProductions, g.getInitialSymbol()));
		
	}

	/**
	 * creates an string representation of the grammar
	 * @param new_prod
	 * @param string
	 * @return grammar
	 */
	private static String mapToInput(HashMap<String, HashSet<String>> new_prod, String string) {
		
		String aux = "";
		
		aux = string + " ->";
		boolean first = true;
		for(String s : new_prod.get(string)) {
			if(first) {
				aux = aux + " " + s.trim();
				first = false;
			}
			else {
				aux = aux + " | " + s.trim();
			}
		}
		aux = aux + " \n";
		
		new_prod.remove(string);

		for (Map.Entry<String, HashSet<String>> entry : new_prod.entrySet()) {
			String key = entry.getKey();
		    if(key == string)
		    	continue;
		    HashSet<String> value = entry.getValue();
		    
		    aux = aux + key.toString() + " ->";
		    
			first = true;
		    for(String s : value) {
				if(first) {
					aux = aux + " " + s.trim();
					first = false;
				}
				else {
					aux = aux + " | " + s.trim();
				}
			}
		    if(!aux.endsWith("\\n")) {
		    	aux = aux + " \n";
		    }
		}
		
		aux = aux.trim();		
		return aux;
		
	}

	/**
	 * transforms set into array list
	 * @param prod
	 * @return
	 */
	private ArrayList<String> getProdList(Set<String> prod) {
		ArrayList<String> l = new ArrayList<>();
		for (String s : prod) {
			if (s.isEmpty()) {
				continue;
			}
			l.add(s);
		}
		return l;
	}
	
	/**
	 * creates a new symbol based on the input
	 * for example: input = S3, output = S4
	 * @param s
	 * @return new symbol
	 */
	private String createNewVN(String s) {
		int number;
		if(s.length() == 1) {
			number = 1;
		}
		else {
			number = Integer.parseInt(s.substring(1, s.length())) + 1;
		}
		String newSymbol = s.charAt(0) + "" + number;
		while(cfg.getVn().contains(newSymbol)) {
			number = number + 1;
			newSymbol = s.charAt(0) + "" + number;
		}
		return newSymbol;
		
	}

	/**
	 * Separate string into an array list
	 * @param prod
	 * @return array list
	 */
	private ArrayList<String> tokenize(String prod){
		String[] br = prod.split(" ");
		ArrayList<String> list = new ArrayList<String>();
		for (String str : br) {
			if (!str.isEmpty()) {
				list.add(str);
			}
		}
		return list;
	}

	/**
	 * returns an equivalent CFG without left recursion
	 * @return
	 */
	public ContextFreeGrammar eliminateLeftRecursion() {
		ContextFreeGrammar newG = this.cfg;
		Operator newOp = new Operator(newG);
		
		HashSet<String> productionSet = new HashSet<String>();
		HashMap<String, HashSet<String>> newProd = newG.getProductions();
		
		ArrayList<String> numberedVn = new ArrayList<>();
		
		for (String nt : this.cfg.getVn()) { // ordered vn
			numberedVn.add(nt);
		}

		if (!hasLeftRecursion()) {
			return this.cfg;
		}
		
		// Indirect Left Recursion
		String ai, aj;
		String firstSymbolAiProd;
		for (int i = 0; i < numberedVn.size(); i++) { // Ai
			ai = numberedVn.get(i);
			ArrayList<String> productionsAi = newOp.getProdList(newProd.get(ai));
			for (int j = 0; j <= i-1; j++) { // For every Aj
				productionsAi = newOp.getProdList(newProd.get(ai));
				aj = numberedVn.get(j);
				for (String aiProd : productionsAi) { // Ai -> aiProd | aiProd | aiProd
					productionSet = new HashSet<String>();
					firstSymbolAiProd = tokenize(aiProd).get(0);
					if (firstSymbolAiProd.equals(aj)) { // firstSymbolAiProd == Aj
						newProd.put(ai, removeFromSet(newProd.get(ai),aiProd));
						productionSet.addAll(newProd.get(ai));
						System.out.println("* Indirect Recursion ------ Ai: " + ai.trim() + " | Aj: " + aj.trim() + " | Prod: " + aiProd.trim());
						for(String prodJ : newProd.get(aj)) {
							if(!prodJ.trim().equals("&")) {
								productionSet.add(prodJ.trim() + " " + aiProd.substring(aj.length()+1).trim() + " ");
							}
							else {
								if(aiProd.trim().substring(aj.length()).length() >= 1) {
									productionSet.add(aiProd.trim().substring(aj.length()));
								}
							}
						}
						newProd.put(ai, productionSet);
					}
				}
			}
			newProd = directRecursion(newProd, ai);
		}

		ContextFreeGrammar cfgIR = ContextFreeGrammar.isValidCFG(mapToInput(newProd, newG.getInitialSymbol()));
		
		// Direct Recursion
		newProd = new HashMap<String, HashSet<String>>();
		for(String nonterminal : cfgIR.getVn()) {
			String newNT = createNewVN(nonterminal);
			Set<String> prod = cfgIR.getGrammarProductions(nonterminal);
			ArrayList<String> prods = getProdList(prod);
			for(int i = 0; i < prods.size(); i++) {
				ArrayList<String> nProd1 = tokenize(prods.get(i));
				if(nProd1.get(0).equals(nonterminal)){
					System.out.println("* Direct Recursion ------ Symbol: " + nonterminal.trim() + " | Prod: " + prods.get(i).trim());
					productionSet = new HashSet<String>();
					for(String p : prods) {
						if(!tokenize(p).get(0).equals(nonterminal)) {
							if(tokenize(p).get(0).equals("&")) {
								productionSet.add(newNT);
							}
							else {
								productionSet.add(p.replaceAll("\\s*$", "") + " " + newNT);
							}
						}
						newProd.put(nonterminal, productionSet);
					}

					productionSet = new HashSet<String>();
					for(String p : prods) {
						if(tokenize(p).get(0).equals(nonterminal)) {
							productionSet.add(p.substring(tokenize(p).get(0).length()+1) + " " + newNT);
						}
						productionSet.add("&");
						newProd.put(newNT, productionSet);
					}
				}
			}
		}
		for(String nt : cfgIR.getVn()) {
			if(!newProd.containsKey(nt))
				newProd.put(nt, (HashSet<String>) cfgIR.getGrammarProductions(nt));
		}

		ContextFreeGrammar cfgDR = ContextFreeGrammar.isValidCFG(mapToInput(newProd, cfgIR.getInitialSymbol()));
		
		return cfgDR;
	}

	private HashSet<String> removeFromSet(HashSet<String> newProdAi, String aiProd) {
		HashSet<String> productionSet = new HashSet<String>();
		for(String prod : newProdAi) {
			if(!prod.equals(aiProd)) {
				productionSet.add(prod);
			}
		}
		return productionSet;
		
	}

	private HashMap<String, HashSet<String>> directRecursion(HashMap<String, HashSet<String>> newProd, String ai) {
			String newNT = createNewVN(ai);
			Set<String> prod = newProd.get(ai);
			ArrayList<String> prods = getProdList(prod);
			for(int i = 0; i < prods.size(); i++) {
				ArrayList<String> nProd1 = tokenize(prods.get(i));
				if(nProd1.get(0).equals(ai)){
					System.out.println("* Direct Recursion ------ Symbol: " + ai.trim() + " | Prod: " + prods.get(i).trim());
					HashSet<String> productionSet = new HashSet<String>();
					for(String p : prods) {
						if(!tokenize(p).get(0).equals(ai)) {
							if(tokenize(p).get(0).equals("&")) {
								productionSet.add(newNT);
							}
							else {
								productionSet.add(p.replaceAll("\\s*$", "") + " " + newNT);
							}
						}
						newProd.put(ai, productionSet);
					}
					productionSet = new HashSet<String>();
					for(String p : prods) {
						if(tokenize(p).get(0).equals(ai)) {
							productionSet.add(p.substring(tokenize(p).get(0).length()+1).replaceAll("\\s*$", "") + " " + newNT);
						}
						productionSet.add("&");
						newProd.put(newNT, productionSet);
					}

				}
			}
		return newProd;
	}

}
