package ContextFreeGrammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public static ContextFreeGrammar isValidCFG(String input) {
		ContextFreeGrammar cfg = new ContextFreeGrammar(input);
		
		String[] productions = getProductions(input);
		
		validateProductions(productions, cfg);
		if(cfg.vn.isEmpty()) {
			return null;
		}
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
					prod = prod.replaceAll("\\s","");
					if (prod.compareTo("&") == 0){
						HashSet<String> aux = first.get(a);
						aux.add(prod);
						first.put(a, aux);
						continue;
					}
					ArrayList<String> tokens = tokenize(prod);
					for(int i = 0; i < tokens.size(); i++) {
						String c = tokens.get(i);
						if(vt.contains(c)) {
							HashSet<String> aux = first.get(a);
							aux.add(c);
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
				prod = prod.replaceAll("\\s","");
				ArrayList<String> tokens = tokenize(prod);
				for(int i = 1; i < tokens.size()-1; i++) {
					String c = tokens.get(i);
					String c2 = tokens.get(i-1);
					follow.get(c2).addAll(first.get(c));
					follow.get(c2).remove("&");
				}
			}
		}
		boolean changed = true;
		while(changed){
			changed = false;
			for (String a : vn) {
				HashSet<String> symbolProductions = productions.get(a);
				for(String prod : symbolProductions) {
					prod = prod.replaceAll("\\s","");
					if(prod.compareTo("&") == 0) {
						continue;
					}
					ArrayList<String> tokens = tokenize(prod);
					for(int i = 0; i < tokens.size()-1; i++) {
						String c = tokens.get(i);
						if(first.get(c).contains("&")){
							HashSet<String> aux = follow.get(c);
							if(aux.addAll(follow.get(a))){
								changed = true;
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
	
	private ArrayList<String> tokenize(String prod){
		Pattern pattern = Pattern.compile("[A-Za-z][0-9]*");
		Matcher m = pattern.matcher(prod);
		ArrayList<String> tokens = new ArrayList<String>();
		while(m.find()){
		    String token = m.group();   
		    tokens.add(token);
		}
		return tokens;
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
			if (prod.replaceAll("\\s+", "").length() < 1) { // |prod| = 0
				cfg.vn.clear();
				return false;
			}
			String[] symbols = prod.split("[\\s\\r]+"); // + E T
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

}
