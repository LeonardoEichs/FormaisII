package ContextFreeGrammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.naming.Context;

public class ContextFreeGrammar {

	private String grammar;
	private HashSet<String> vn;
	private HashSet<String> vt;
	private HashMap<String, HashSet<String>> productions;
	private HashMap<String, HashSet<String>> first;
	private HashMap<String, HashSet<String>> firstNT;
	private HashMap<String, HashSet<String>> follow;
	private String s;
	private String id;
	

	/*
	 * Default constructor
	 */
	public ContextFreeGrammar() {
		vn = new HashSet<String>();
		vt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
		first = new HashMap<String, HashSet<String>>();
		firstNT = new HashMap<String, HashSet<String>>();
		//calculateFirst();
		follow = new HashMap<String, HashSet<String>>();
	}
	
	public ContextFreeGrammar(HashSet<String> _vn, HashSet<String> _vt, HashMap<String, HashSet<String>> _productions, String _s) {
		this.vn = _vn;
		s = _s;
		this.vt = _vt;
		productions = _productions;
		first = new HashMap<String, HashSet<String>>();
		calculateFirst();
		calculateFirstNT();
		follow = new HashMap<String, HashSet<String>>();
		calculateFollow();
	}
	
	/*
	 * Constructor with input
	 */
	public ContextFreeGrammar(String inp) {
		this.grammar = inp;
		vn = new HashSet<String>();
		vt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
		first = new HashMap<String, HashSet<String>>();
		firstNT = new HashMap<String, HashSet<String>>();
		//calculateFirst();
		//calculateFirstNT();
		follow = new HashMap<String, HashSet<String>>();
	}
	
	public ContextFreeGrammar(ContextFreeGrammar g) {
		String init = g.getInitialSymbol();
		Set<String> newVn = new HashSet<String>();
		Set<String> newVt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
		
		String gr = g.grammar;
		for (String s : g.getVn()) {
			newVn.add(s);
		}
		for (String s : g.getVt()) {
			newVt.add(s);
		}
		
		this.s = init;
		this.vn = (HashSet<String>) newVn;
		this.vt = (HashSet<String>)newVt;
		this.grammar = gr;
		for (String pr : this.vn) {
			productions.put(pr, new HashSet<>());
			for (String prod : g.getGrammarProductions(pr)) {
				this.addProduction(pr, prod);
				
			}
		}
		this.first = g.first;
		this.firstNT = g.firstNT;
		this.follow = g.follow;
	}
	
	public Boolean isEmptyGrammar() {
		return !removeInfertile().getVn().contains(this.getInitialSymbol());
	}
	
	public Boolean isInfinite() {
		ContextFreeGrammar newG = this.removeInfertile();
		newG = newG.removeUnreachable();
				
		String symbol = "#";
		
		for(String nonTerminal : newG.getVn()) {
			HashSet<String> markedSymbol = new HashSet<String>();
			HashSet<String> markedSymbolOld = new HashSet<String>();
			markedSymbol.add(nonTerminal);
			
			do {
				markedSymbolOld.addAll(markedSymbol);
				for(String nt : newG.getVn()) {
					for(String prod : newG.getGrammarProductions(nt)) {
						ArrayList<String> tokenizedProd = tokenize(prod);
						for(String letter : tokenizedProd) {
							if(markedSymbol.contains(letter)) {
								if(nt.equals(nonTerminal)) {
									markedSymbol.add(symbol);
								}
								else {
									markedSymbol.add(nt);
								}
							}
						}
						
					}
				}
			}while(!markedSymbol.equals(markedSymbolOld));
			
			if(markedSymbol.contains(symbol)) {
				return true;
			}

		}
		
		return false;
	}

	
	public String getDefinition() {
		String grammar = "";
		String aux = "";
		HashSet<String> prodList;
		
		for (String vN : this.productions.keySet()) {
			prodList = this.productions.get(vN);
			
			for (String prod : prodList) {
				aux += prod + " | ";
			}
			if (aux.length() > 0) { 
				aux = aux.substring(0, aux.length()-2);
			}
			aux = aux.trim().replaceAll(" +", " ");
			if (vN.equals(this.s)) {
				grammar = vN + " -> " + aux + "\n" + grammar;
			} else {
				grammar += vN + " -> " + aux + "\n";
			}
			aux = "";
		}
		this.grammar = grammar;
		return grammar;
}
	
	public static ContextFreeGrammar isValidCFG(String input) {
		ContextFreeGrammar cfg = new ContextFreeGrammar(input);
		
		String[] productions = getProductions(input);
		
		validateProductions(productions, cfg);
		if(cfg.vn.isEmpty()) {
			return null;
		}
		cfg.calculateFirst();
		cfg.calculateFirstNT();
		return cfg;
	}
	
	public HashMap<String, HashSet<String>> getFirstNT() {
		return firstNT;
	}
	
	public Set<String> getFirstNT(String nt) {
		if (!isVnVt(nt)) { // if a symbol does not belong to vn vt
			return null;
		}
		return getFirstNT().get(nt);
	}
	
	private boolean isVnVt(String str) {
		boolean onlySpaces = true;
		for (String s : tokenize(str)) {
			if (s.matches("[\\s]+")) {
				continue;
			} else {
				onlySpaces = false;
				if (!vn.contains(s) && !vt.contains(s)) {
					return false;
				}
			}
		}
		if (onlySpaces) {
			return false;
		}
		return true;
	}
	
	public void calculateFirstNT(){
		firstNT = new HashMap<>();
		Set<String> set;
		boolean wasChanged = true; // avoid infinite loop from recursion
		
		// Initializes firstNT set for every vn from the grammar
		for(String nt : vn) {
			firstNT.put(nt, new HashSet<>());			
		}
		
		// Do it only while the set was changed
		while(wasChanged){
			wasChanged = false;
			// for every vn from the grammar
			for(String nt : vn) {
				set = new HashSet<>();
				// for every prod from the vn
				for(String prod : getGrammarProductions(nt)) {
					set.addAll(getProductionFirstNT(nt, tokenize(prod)));
				}
				// if the set was changed
				if (firstNT.get(nt).addAll(set)) {
					wasChanged = true;
				}
			}
		}
	}

	
	private Set<String> getProductionFirstNT(String vn, ArrayList<String> production){
		Set<String> firstNTSet = new HashSet<>();
		Set<String> nextFirstNT = null;
		String prodSymbol;
		boolean goToTheNext;
		int symbolCount = 0;
		
		prodSymbol = production.get(symbolCount);
		goToTheNext = true;
				
		while(goToTheNext) {
			goToTheNext = false;
			if(!vt.contains(prodSymbol)) { // symbol is not a terminal
				firstNTSet.add(prodSymbol);
				nextFirstNT = getFirstNT().get(prodSymbol); // get the firstNT set for the symbol
				firstNTSet.addAll(nextFirstNT); // add the set to the firstNTSet
				// for every production of the symbol
				for(String prod : getGrammarProductions(prodSymbol)){
					if(tokenize(prod).contains("&")) { // if it contains epsilon
						goToTheNext = true; // go to the next symbol to get the firstNT
						if(++symbolCount >= production.size()) { // verify the size of the production
							goToTheNext = false; // if it is the last vn from the production, return
						}
						else {
							prodSymbol = production.get(symbolCount); // get the next symbol
						}
					}
				}
				
			}
		}
		return firstNTSet;
}
	public void calculateFirst(){
		for (String a : vt) {
			a = a.replaceAll("\\s","");
			HashSet<String> hs = new HashSet<String>();
			hs.add(a);
			first.put(a, hs);
		}
		for (String a : vn) {
			first.put(a, new HashSet<String>());
		}
		boolean changed = true;
		while(changed) {
			changed = false;
			for (String a : vn) {
				HashSet<String> symbolProductions = productions.get(a);
				for(String prod : symbolProductions) {
					String prod2 = prod.replaceAll("\\s","");
					if (prod2.compareTo("&") == 0){
						HashSet<String> aux = first.get(a);
						if(aux.add(prod2)) {
							changed = true;
						}
						first.put(a, aux);
						continue;
					}
					ArrayList<String> tokens = tokenize(prod);
					for(int i = 0; i < tokens.size(); i++) {
						String c = tokens.get(i);
						if(vt.contains(c)) {
							HashSet<String> aux = first.get(a);
							if(aux.add(c)){
								changed = true;
							};
							first.put(a, aux);
							
							break;
						} else {
							HashSet<String> cFirst = new HashSet<String>();
							cFirst.addAll(first.get(c));
							HashSet<String> aux = first.get(a);
							if(cFirst.contains("&") && i != tokens.size()-1) {
								cFirst.remove("&");
								if(aux.addAll(cFirst)) {
									changed = true;
								}
								first.put(a, aux);
							} else if (cFirst.contains("&")) {
								if(aux.addAll(cFirst)) {
									changed = true;
								}
								first.put(a, aux);;
							} else {
								if(aux.addAll(cFirst)) {
									changed = true;
								}
								first.put(a, aux);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void calculateFollow(){
		if(first.size() == 0) {
			calculateFirst();
		}
		for (String a : vn) {
			follow.put(a, new HashSet<String>());
		}
		follow.get(s).add("$");
		for (String a : vn) {
			HashSet<String> symbolProductions = productions.get(a);
			for(String prod : symbolProductions) {
				//prod = prod.replaceAll("\\s","");
				ArrayList<String> tokens = tokenize(prod);
				for(int i = 0; i < tokens.size()-1; i++) {
					String c = tokens.get(i);
					if(vn.contains(c)) {
						String c2 = tokens.get(i+1);
						follow.get(c).addAll(first.get(c2));
						follow.get(c).remove("&");
					}
				}
			}
		}
		boolean changed = true;
		while(changed){
			changed = false;
			for (String a : vn) {
				HashSet<String> symbolProductions = productions.get(a);
				for(String prod : symbolProductions) {
					//prod = prod.replaceAll("\\s","");
					ArrayList<String> tokens = tokenize(prod);
					if(tokens.get(0).compareTo("&") == 0) {
						continue;
					}
					for(int i = 0; i < tokens.size()-1; i++) {
						String b = tokens.get(i);
						if(vn.contains(b)) {
							String c = tokens.get(i+1);
							if (a.compareTo(b) == 0) {
								continue;
							}
							if(first.get(c).contains("&")){
								HashSet<String> aux = follow.get(b);
								if(aux.addAll(follow.get(a))){
									changed = true;
								}	
							}
						}
					}
					String x = tokens.get(tokens.size()-1);
					HashSet<String> aux;
					if(vn.contains(x)) {
						aux = follow.get(tokens.get(tokens.size()-1));
						if(aux.addAll(follow.get(a))){
							changed = true;
						}
					}
				}
			}
		}
	}
	
	public HashSet<String> getFirst(String a){
		return first.get(a);
	}
	public HashSet<String> getFollow(String a){
		return follow.get(a);
	}
	
	public ContextFreeGrammar removeEpsilon() {
		boolean changed = true;
		HashSet<String> ne = new HashSet<String>();
		//HashSet<String> unchecked = vn;
		while(changed) {
			changed = false;
			for (String symbol : vn) {
				HashSet<String> symbolProductions = productions.get(symbol);
				searchProds: {
					for(String prod : symbolProductions) {
						ArrayList<String> tokens = tokenize(prod);
						if(tokens.get(0).compareTo("&") == 0) {
							if(ne.add(symbol)) {
								changed = true;
							}
							break;
						}
						for(int i = 0; i < tokens.size(); i++) {
							String c = tokens.get(i);
							if(!ne.contains(c)) {
								break;
							}
							if(i == tokens.size()-1) {
								if(ne.add(symbol)) {
									changed = true;
								}
								break searchProds;
							}
						}
						
					}
				}
			}
		}
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		for (String symbol : vn) {
			HashSet<String> newSymbolProductions = new HashSet<String>();
			HashSet<String> symbolProductions = productions.get(symbol);
			for(String prod : symbolProductions) {
				ArrayList<String> tokens = tokenize(prod);
				if(tokens.get(0).compareTo("&") != 0) {
					/*for(int i = 0; i < tokens.size(); i++) {
						String c = tokens.get(i);
						if(ne.contains(c)) {
							ArrayList<String> aux = new ArrayList<String>();
							aux.addAll(tokens);
							aux.remove(i);
							String auxString = aux.toString();
							auxString = auxString.replace("[", " ").replace("]", " ").replace(",", "");
							newSymbolProductions.add(auxString);
						}
						
					}
					String auxString = tokens.toString();
					auxString = auxString.replace("[", " ").replace("]", " ").replace(",", "");
					newSymbolProductions.add(auxString);
					*/
					newSymbolProductions.addAll(getEpsilonCombination(tokens, ne));
				}
			}
			newProductions.put(symbol, newSymbolProductions);
		}
		ContextFreeGrammar eFree = new ContextFreeGrammar(vn, vt, newProductions, s);
		return eFree;
	}
	
	private HashSet<String> getEpsilonCombination(ArrayList<String> tokens, HashSet<String> ne){
		HashSet<String> comb = new HashSet<String>();
		String auxString = tokens.toString();
		auxString = auxString.replace("[", " ").replace("]", " ").replace(",", "");
		comb.add(auxString);
		for(int i = 0; i < tokens.size(); i++) {
			String c = tokens.get(i);
			if(ne.contains(c)) {
				ArrayList<String> aux = new ArrayList<String>();
				aux.addAll(tokens);
				aux.remove(i);
				comb.addAll(getEpsilonCombination(aux, ne));
			}
			
		}
		return comb;
	}
	
	public ContextFreeGrammar removeSimpleProductions() {
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> nt = new HashMap<String, HashSet<String>>();
		boolean changed = true;
		for (String a : vn) {
			HashSet<String> temp = new HashSet<String>();
			temp.add(a);
			nt.put(a, temp);
			
			HashSet<String> newSymbolProductions = new HashSet<String>();
			newSymbolProductions.addAll(productions.get(a));
			newProductions.put(a, newSymbolProductions);
		}
		
		while(changed) {
			changed = false;
			for(String symbol : vn) {
				HashSet<String> symbolProductions = productions.get(symbol);
				for(String prod : symbolProductions) {
					String prodaux = prod.replaceAll("\\s","");
					if (vn.contains(prodaux)) {
						if(nt.get(symbol).addAll(nt.get(prodaux))) {
							changed = true;
						}
						newProductions.get(symbol).remove(prod);
					}
				}
			}
		}
		
		for(String symbol : vn) {
			HashSet<String> n = nt.get(symbol);
			for(String s : n) {
				newProductions.get(symbol).addAll(newProductions.get(s));
			}
		}
		
		ContextFreeGrammar simpleFree = new ContextFreeGrammar(vn, vt, newProductions, s);
		return simpleFree;
	}
	
	public ContextFreeGrammar removeInfertile() {
		HashSet<String> nf = new HashSet<String>();
		HashSet<String> unfertile = new HashSet<String>();
		HashSet<String> nfUvt = new HashSet<String>();
		
		//build n1
		for (String a : vn) {
			HashSet<String> symbolProductions = productions.get(a);
			searchProds : {
				for(String prod : symbolProductions) {
					ArrayList<String> tokens = tokenize(prod);	
					if(vt.containsAll(tokens) || tokens.get(0).compareTo("&") == 0) {
						nf.add(a);
						break searchProds;
					}
				}
				unfertile.add(a);
			}
		}
		nfUvt.addAll(nf);
		nfUvt.addAll(vt);
		boolean changed = true;
		while(changed) {
			changed = false;
			for (String symbol : unfertile) {
				HashSet<String> symbolProductions = productions.get(symbol);	
				searchProds: {
					for(String prod : symbolProductions) {
						ArrayList<String> tokens = tokenize(prod);
						if(nfUvt.containsAll(tokens)) {
							if(nf.add(symbol)) {
								nfUvt.add(symbol);
								changed = true;
								break searchProds;
							}
							nfUvt.add(symbol);
						}
					}
				}
			}
		}
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		for (String symbol : nf) {
			HashSet<String> symbolProductions = new HashSet<String>();
			symbolProductions.addAll(productions.get(symbol));
			for(String prod : symbolProductions) {
				ArrayList<String> tokens = tokenize(prod);
				for(int i = 0; i < tokens.size(); i++) {
					String c = tokens.get(i);
					if (!(nf.contains(c) || vt.contains(c))) {
						symbolProductions.remove(prod);
					}
				}
			}
			newProductions.put(symbol, symbolProductions);
		}
		
		
		ContextFreeGrammar noInfertile = new ContextFreeGrammar(nf, vt, newProductions, s);
		return noInfertile;
	}
	
	public ContextFreeGrammar removeUnreachable() {
		HashSet<String> newvt = new HashSet<String>();
		HashSet<String> newvn = new HashSet<String>();
		Stack<String> toCheck = new Stack<String>();
		toCheck.push(s);
		newvn.add(s);
		
		while(!toCheck.isEmpty()) {
			String symbol = toCheck.pop();
			HashSet<String> symbolProductions = productions.get(symbol);
			for(String prod : symbolProductions) {
				ArrayList<String> tokens = tokenize(prod);
				for(int i = 0; i < tokens.size(); i++) {
					String c = tokens.get(i);
					if(vn.contains(c)) {
						if(!newvn.contains(c)) {
							toCheck.add(c);
							newvn.add(c);
						}
					} else {
						newvt.add(c);
					}
				}
			}
		}
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		for (String a : newvn) {
			newProductions.put(a, productions.get(a));
		}
		
		return new ContextFreeGrammar(newvn, newvt, newProductions, s);
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
	

	private static ContextFreeGrammar validateProductions(String[] prods, ContextFreeGrammar cfg) {
		String[] prodSplit;
		String vn = "";
		HashSet<String> pr = new HashSet<String>();
		boolean isSDefined = false;
		for(String prod : prods) {
			prodSplit = prod.split("->");
			if(prodSplit.length != 2) {
				return null;
			}
			vn = prodSplit[0];
			if(vn.length() == 0) {
				return null;
			}
			pr = cfg.productions.get(vn);
			if(pr == null) {
				pr = new HashSet<String>();
			}
			vn = vn.replaceAll("\\s+", "");
			if(vn.length() > 1) {
				if (!vn.substring(1).matches("^[0-9\\s+]+")) {
					cfg.vn.clear();
					return null;
				}
			}
			if (!Character.isUpperCase(vn.charAt(0))) {
				cfg.vn.clear();
				return null;
			}
			cfg.vn.add(vn);
			if (!isSDefined) {
				cfg.s = vn;
				isSDefined = true;
				
			}
			if (!validateProduction(vn, prod, pr, cfg)) {
				cfg.vn.clear();
				return null;
			}
		}
		for(String c : cfg.vn) { // Vn com produções vazias
			if(cfg.getGrammarProductions(c).isEmpty()) {
				cfg.vn.clear();
				return null;
			}
		}

		return cfg;
	}

	private static boolean validateProduction(String vn, String productions,
			HashSet<String> prodList, ContextFreeGrammar cfg) {
		String prod = productions.substring(productions.indexOf("->")+2);
		if (prod.replaceAll("\\s+", "").length() < 1) {
			cfg.vn.clear();
			return false;
		}
		String[] prods = prod.split("\\|");
		for(String eachProd : prods) {
			if (eachProd.replaceAll("\\s+", "").length() < 1) { // |prod| = 0
				cfg.vn.clear();
				return false;
			}
			String[] symbols = eachProd.split("[\\s\\r]+"); // + E T
			for (String symb : symbols) {
				if(symb.isEmpty()) {
					continue;
				}
				if (Character.isUpperCase(symb.charAt(0))) {
					for (int i = 1; i < symb.length(); i++) {
						if (!Character.isDigit(symb.charAt(i))) { // E1E
							cfg.vn.clear();
							return false;
						}
					}
					cfg.vn.add(symb);
				} else {
					for (int i = 1; i < symb.length(); i++) {
						if (Character.isUpperCase(symb.charAt(i))) {
							cfg.vn.clear();
							return false;
						}
					}
					cfg.vt.add(symb);
				}
			}
			prodList.add(eachProd);
			cfg.productions.put(vn, prodList);
		}
		return true;
	}

	private static String[] getProductions(String input) {
		String[] prod = input.split("[\\r\\n]+");
		return prod;
	}
	
	public Set<String> getGrammarProductions(String vn) {
		Set<String> prod = productions.get(vn);
		if (prod == null) {
			prod = new HashSet<String>();
		}
		return prod;
	}

	public void addProduction(String nt, String prod) {
		if (!this.vn.contains(nt)) {
			this.vn.add(nt);
			this.productions.put(nt, new HashSet<String>());
		}
		HashSet<String> p = this.productions.get(nt);
		p.add(prod);
		this.productions.put(nt, p);
	}

	public HashSet<String> getVt() {
		return this.vt;
	}
	
	public HashSet<String> getVn() {
		return this.vn;
	}
	
	public HashMap<String, HashSet<String>> getFirst() {
		return first;
	}
	
	public String getInitialSymbol() {
		return this.s;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}

	public void removeProduction(String nonterminal, String prod) {
		HashSet<String> pSet = this.productions.get(nonterminal);
		pSet.remove(prod);
		this.productions.put(nonterminal, pSet);
	}
	
	public HashMap<String, HashSet<String>> getProductions(){
		return productions;
	}

    @Override
    public String toString(){
        return this.getId();
    }
}
