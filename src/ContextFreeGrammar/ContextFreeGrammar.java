package ContextFreeGrammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.naming.Context;

public class ContextFreeGrammar {

	private String grammar; // The input grammar entered by the user
	private HashSet<String> vn; // non terminal symbols
	private HashSet<String> vt; // terminal symbols
	private HashMap<String, HashSet<String>> productions; // production rules
	private HashMap<String, HashSet<String>> first; // firsts
	private HashMap<String, HashSet<String>> firstNT; // firstsNT
	private HashMap<String, HashSet<String>> follow; // follow
	private String s; // initial symbol
	private String id; // an unique ID for the CGF
	

	/**
	 * Default constructor
	 */
	public ContextFreeGrammar() {
		vn = new HashSet<String>(); 
		vt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
		first = new HashMap<String, HashSet<String>>();
		firstNT = new HashMap<String, HashSet<String>>();
		follow = new HashMap<String, HashSet<String>>();
	}
	
	/**
	 * Hard-Coded Constructor
	 * @param _vn non-terminal entered by user
	 * @param _vt terminal entered by user
	 * @param _productions productions entered by user
	 * @param _s initial symbol entered by user
	 */
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
	
	/**
	 * Constructor with input
	 * @param inp the regular grammar entered by the user
	 */
	public ContextFreeGrammar(String inp) {
		this.grammar = inp;
		vn = new HashSet<String>();
		vt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
		first = new HashMap<String, HashSet<String>>();
		firstNT = new HashMap<String, HashSet<String>>();
		follow = new HashMap<String, HashSet<String>>();
	}
	
	/**
	 * Constructor
	 * Creates a new grammar based on the parameter
	 * @param g the grammar to have data copied from
	 */
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
	
	/**
	 * Checks if L(G) is Empty
	 * @return true if is empty
	 */
	public Boolean isEmptyGrammar() {
		return !removeInfertile().getVn().contains(this.getInitialSymbol());
	}
	
	/**
	 * Checks if L(G) is finite or infinite
	 * @return true if is infinite, false if finite
	 */
	public Boolean isInfinite() {
		//
		ContextFreeGrammar newG = this.removeInfertile();
		newG = newG.removeUnreachable();
		//	
		String symbol = "#";
		
		for(String nonTerminal : newG.getVn()) {
			// nt
			HashSet<String> markedSymbol = new HashSet<String>();
			HashSet<String> markedSymbolOld = new HashSet<String>();
			// mark nt
			markedSymbol.add(nonTerminal);
			
			do {
				markedSymbolOld.addAll(markedSymbol);
				for(String nt : newG.getVn()) {
					for(String prod : newG.getGrammarProductions(nt)) {
						// for each production ...
						ArrayList<String> tokenizedProd = tokenize(prod);
						for(String letter : tokenizedProd) {
							// if symbol in production is marked
							if(markedSymbol.contains(letter)) {
								// mark which nt producing it
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
			
			// if symbol # was marked is self embedded, therefore infinite
			if(markedSymbol.contains(symbol)) {
				return true;
			}

		}
		
		return false;
	}

	/**
	 * Printable form of the grammar
	 * @return string that contains printable grammar
	 */
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
	
	/**
	 * Checks if input is a valid grammar
	 * @param input
	 * @return ContextFreeGrammar object if is valid
	 */
	public static ContextFreeGrammar isValidCFG(String input) {
		ContextFreeGrammar cfg = new ContextFreeGrammar(input);
		
		String[] productions = getProductions(input);
		
		validateProductions(productions, cfg);
		if(cfg.vn.isEmpty()) {
			return null;
		}
		cfg.calculateFirst();
		cfg.calculateFirstNT();
		cfg.calculateFollow();
		return cfg;
	}
	
	/**
	 * Validates production
	 * @param prods
	 * @param cfg
	 * @return CFG Object
	 */
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

	/**
	 * method that validates if productions are valid
	 * @param vn
	 * @param productions
	 * @param prodList
	 * @param cfg
	 * @return true if valid
	 */
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

	
	/**
	 * Return First NTs
	 * @return firstNTs
	 */
	public HashMap<String, HashSet<String>> getFirstNT() {
		return firstNT;
	}
	
	/**
	 * Return FirstNT of specified nt
	 * @param nt
	 * @return firstNT of nt
	 */
	public Set<String> getFirstNT(String nt) {
		if (!isVnVt(nt)) { // if a symbol does not belong to vn vt
			return null;
		}
		return getFirstNT().get(nt);
	}
	
	/**
	 * If a symbol belongs to vn vt
	 * @param str
	 * @return true if belongs
	 */
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
	
	/**
	 * Calculate FirstNT
	 */
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
	
	/**
	 * Calculate First
	 */
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
	
	/**
	 * Calculate Follow
	 */
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
						follow.get(c).addAll(this.getGroupFirst(tokens, i+1));
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
							if (a.compareTo(b) == 0) {
								continue;
							}
							if(checkEpsilonFirst(tokens, i+1)){
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
	
	/**
	 * Checks if there is an & in the first set
	 * @param tokens
	 * @param i
	 * @return true if there 
	 */
	public boolean checkEpsilonFirst(ArrayList<String> tokens, int i) {
		for (int j = i; j < tokens.size(); j++) {
			String c = tokens.get(j);
			if(!first.get(c).contains("&")) {
				return false;
			}
		}
		return true;
	}
	
	public HashSet<String> getGroupFirst(ArrayList<String> tokens, int i){
		HashSet<String> groupFirst = new HashSet<String>();
		for(int j = i; j < tokens.size(); j++) {
			String c = tokens.get(j);
			groupFirst.addAll(first.get(c));
			if (!first.get(c).contains("&")){
				break;
			}
		}
		return groupFirst;
	}
	
	/**
	 * return first for nt "a"
	 * @param a
	 * @return first of a
	 */
	public HashSet<String> getFirst(String a){
		return first.get(a);
	}
	
	/**
	 * return follow for nt "a"
	 * @param a
	 * @return follow of a
	 */
	public HashSet<String> getFollow(String a){
		return follow.get(a);
	}
	
	/**
	 * returns a new CFG Object without epsilon
	 * @return CFG Object without epsilon
	 */
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
		System.out.println("Ne: " + ne.toString());
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		for (String symbol : vn) {
			HashSet<String> newSymbolProductions = new HashSet<String>();
			HashSet<String> symbolProductions = productions.get(symbol);
			for(String prod : symbolProductions) {
				ArrayList<String> tokens = tokenize(prod);
				if(tokens.get(0).compareTo("&") != 0) {
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
	
	/**
	 * retuns CFG Object without simple productions
	 * @return CFG Object without simple productions
	 */
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
		
		System.out.println("Na ---------------------");
		for (HashMap.Entry<String, HashSet<String>> entry : nt.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
			System.out.println("    * " + key + ": " + value);
		}
		System.out.println("------------------------");

		
		ContextFreeGrammar simpleFree = new ContextFreeGrammar(vn, vt, newProductions, s);
		return simpleFree;
	}
	
	/** 
	 * Returns CFG Object without simple productions
	 * @return CFG Object without simple productions
	 */
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
		System.out.println("Nf: " + nf.toString());
		HashMap<String, HashSet<String>> newProductions = new HashMap<String, HashSet<String>>();
		for (String symbol : nf) {
			HashSet<String> symbolProductions = new HashSet<String>();
			HashSet<String> symbolProductionsAux = new HashSet<String>();
			symbolProductions.addAll(productions.get(symbol));
			symbolProductionsAux.addAll(symbolProductions);
			for(String prod : symbolProductionsAux) {
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
	
	/**
	 * Return CFG Object without unreachable
	 * @return CFG Object without unreachable
	 */
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
	

	private static String[] getProductions(String input) {
		String[] prod = input.split("[\\r\\n]+");
		return prod;
	}
	
	/**
	 * return productions of a nt
	 * @param vn
	 * @return productions
	 */
	public Set<String> getGrammarProductions(String vn) {
		Set<String> prod = productions.get(vn);
		if (prod == null) {
			prod = new HashSet<String>();
		}
		return prod;
	}

	/**
	 * Add production
	 * @param nt
	 * @param prod
	 */
	public void addProduction(String nt, String prod) {
		if (!this.vn.contains(nt)) {
			this.vn.add(nt);
			this.productions.put(nt, new HashSet<String>());
		}
		HashSet<String> p = this.productions.get(nt);
		p.add(prod);
		this.productions.put(nt, p);
	}

	/**
	 * returns vt
	 * @return vt
	 */
	public HashSet<String> getVt() {
		return this.vt;
	}
	
	/**
	 * returns vn
	 * @return vn
	 */
	public HashSet<String> getVn() {
		return this.vn;
	}
	
	/**
	 * returns first
	 * @return first
	 */
	public HashMap<String, HashSet<String>> getFirst() {
		return first;
	}
	
	/**
	 * returns initial symbol
	 * @return initial symbol
	 */
	public String getInitialSymbol() {
		return this.s;
	}
	
	/**
	 * set Id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * returns id
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * remove production
	 * @param nonterminal to be removed
	 * @param prod with remotion
	 */
	public void removeProduction(String nonterminal, String prod) {
		HashSet<String> pSet = this.productions.get(nonterminal);
		pSet.remove(prod);
		this.productions.put(nonterminal, pSet);
	}
	
	/**
	 * return productions
	 * @return productions
	 */
	public HashMap<String, HashSet<String>> getProductions(){
		return productions;
	}

    @Override
    public String toString(){
        return this.getId();
    }
}
