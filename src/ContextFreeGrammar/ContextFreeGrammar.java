package ContextFreeGrammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ContextFreeGrammar {

	private String grammar;
	private HashSet<String> vn;
	private HashSet<String> vt;
	private HashMap<String, HashSet<String>> productions;
	private HashMap<String, HashSet<String>> first;
	private HashMap<String, HashSet<String>> follow;
	private String s;
	

	/*
	 * Default constructor
	 */
	public ContextFreeGrammar() {
		vn = new HashSet<String>();
		vt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
		first = new HashMap<String, HashSet<String>>();
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
		//calculateFirst();
		follow = new HashMap<String, HashSet<String>>();
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
		return cfg;
	}
	
	public void calculateFirst(){
		for (String s : vt) {
			s = s.replaceAll("\\s","");
			HashSet<String> hs = new HashSet<String>();
			hs.add(s);
			first.put(s, hs);
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
					for(int i = 0; i < tokens.size(); i++) {
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
				}
			}
			newProductions.put(symbol, newSymbolProductions);
		}
		ContextFreeGrammar eFree = new ContextFreeGrammar(vn, vt, newProductions, s);
		return eFree;
	}
	
	private ArrayList<String> tokenize(String prod){
		/*
		Pattern pattern = Pattern.compile("([A-Z][0-9]*)|([a-z]+)|(\\+)|(\\()|(\\))");
		Matcher m = pattern.matcher(prod);
		ArrayList<String> tokens = new ArrayList<String>();
		while(m.find()){
		    String token = m.group();   
		    tokens.add(token);
		}
		return tokens;
		*/
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
	/*
	public boolean isFactored() {
		Set<String> aux, firstSet, productions;
		for (String nt : this.vn) {
			firstSet = new HashSet<String>();
			productions = this.getGrammarProductions(nt);
			for (String prod: productions) {
				aux = getProductionFirstSet(tokenize(prod));
				for (String prodSymbol : aux) {
					if (firstSet.contains(prodSymbol)) {
							return false;
					}
				}
				firstSet.addAll(aux);
			}
		}
		return true;
	}
	*/

	public HashSet<String> getVt() {
		return this.vt;
	}

}
