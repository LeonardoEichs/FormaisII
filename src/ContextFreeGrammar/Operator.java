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

	public Operator(ContextFreeGrammar cfg) {
		this.cfg = cfg;
	}
	
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
					if(nProd1.get(0).equals(nProd2.get(0)) && 
							Character.isLowerCase(nProd1.get(0).charAt(0)) && 
							Character.isLowerCase(nProd2.get(0).charAt(0))) { // Direct Factoring
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
				for(int j = i+1; j < prods.size(); j++) {
					nProd2 = tokenize(prods.get(j));
					if(nProd1.get(0).equals(nProd2.get(0)) && 
							Character.isUpperCase(nProd1.get(0).charAt(0)) && 
							Character.isUpperCase(nProd2.get(0).charAt(0))) {
						repeat = false;
						newProductions.remove(nonterminal);
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
							production.add(prodi + prods.get(i).replaceFirst("^\\s+", "").substring(nProd1.get(0).length()+1));
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

	public ContextFreeGrammar eliminateLeftRecursion() {
		ContextFreeGrammar newG = this.cfg;
		Operator newOp = new Operator(newG);
		
		HashSet<String> productionSet = new HashSet<String>();
		HashMap<String, HashSet<String>> newProd = new HashMap<String, HashSet<String>>();
		
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
			ArrayList<String> productionsAi = newOp.getProdList(newG.getGrammarProductions(ai));
			for (int j = 0; j <= i-1; j++) { // For every Aj
				aj = numberedVn.get(j);
				for (String aiProd : productionsAi) { // Ai -> aiProd | aiProd | aiProd
					productionSet = new HashSet<String>();
					firstSymbolAiProd = newOp.tokenize(aiProd).get(0);
					if(newOp.cfg.getVt().contains(firstSymbolAiProd)) { // terminal skip
						if(newProd.containsKey(ai)) {
							productionSet.addAll(newProd.get(ai));
						}
						productionSet.add(aiProd);
						newProd.put(ai, productionSet);
						continue;
					}
					if (firstSymbolAiProd.equals(aj)) { // firstSymbolAiProd == Aj
						System.out.println("* Indirect Recursion ------ Ai: " + ai.trim() + " | Aj: " + aj.trim() + " | Prod: " + aiProd.trim());
						for(String prodJ : newG.getGrammarProductions(aj)) {
							if(!prodJ.trim().equals("&")) {
								productionSet.add(prodJ.trim() + aiProd.substring(aj.length()+1));
							}
							else {
								if(aiProd.trim().substring(aj.length()).length() >= 1) {
									productionSet.add(aiProd.trim().substring(aj.length()));
								}
							}
						}
						if(newProd.containsKey(ai)) {
							productionSet.addAll(newProd.get(ai));
						}
						newProd.put(ai, productionSet);
					}
				}
			}
			if(!newProd.containsKey(ai)) {
				productionSet = new HashSet<String>();
				productionSet.addAll(newG.getGrammarProductions(ai));
				newProd.put(numberedVn.get(i), productionSet);
			}
			else {
				for(int k = i+1; k < numberedVn.size(); k++) {
					String symbol = numberedVn.get(k);
					productionSet = new HashSet<String>();
					productionSet.addAll(newProd.get(ai));
					for(String aiProd : productionsAi) {
						firstSymbolAiProd = tokenize(aiProd).get(0);
						if(firstSymbolAiProd.equals(symbol)) {
							productionSet.add(aiProd);
						}
						newProd.put(numberedVn.get(i), productionSet);
					}
				}	
			}			
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

}
