package ContextFreeGrammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Operator {
	
	private ContextFreeGrammar cfg;

	public Operator(ContextFreeGrammar cfg) {
		this.cfg = cfg;
	}
	
	public boolean hasLeftRecursion() {
		// For every vn
		for (String nt: this.cfg.getVn()) {
			// If the firstNT set from the current non terminal contains itself
			System.out.println(nt);
			System.out.println(cfg.getFirst(nt));
			if (cfg.getFirst(nt).contains(nt)) {
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
			return attempts;
		}

		ContextFreeGrammar previous = new ContextFreeGrammar(cfg);
		Operator newOperator = new Operator(previous);
		
		int i = 1;
		while(i <= steps) {
			ContextFreeGrammar cfgFc = newOperator.factorGrammar(previous);
			ContextFreeGrammar g = ContextFreeGrammar.isValidCFG(mapToInput(cfgFc.getProductions(), cfgFc.getInitialSymbol()));
			g.setId(cfg.getId() + " [F" + i + "]");
			newOperator = new Operator(g);
			attempts.add(g);
			if(newOperator.isFactored()) {
				break;
			}
			previous = new ContextFreeGrammar(g);
			i++;
		}
		
		return attempts;
	}
	
	public ContextFreeGrammar factorGrammar(ContextFreeGrammar g) {
		ArrayList<String> nProd1, nProd2; // Production to be analyzed and look-ahead
		Set<String> prod;
		ContextFreeGrammar newG = new ContextFreeGrammar(g);
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		boolean repeat = true;
		boolean stepDone = false;
		// Direct Factoring
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
		if(s.length() == 1) {
			return s + "" + 1;
		}
		String newVN = s.substring(1, s.length());
		int number = Integer.parseInt(newVN) + 1;
		return s.charAt(0) + "" + number;
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

}
