package ContextFreeGrammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextFreeGrammar {

	private String grammar;
	private HashSet<String> vn;
	private HashSet<String> vt;
	private HashMap<String, HashSet<String>> productions;
	private String s;

	public ContextFreeGrammar() {
		vn = new HashSet<String>();
		vt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
	}
	
	public ContextFreeGrammar(String inp) {
		this.grammar = inp;
		vn = new HashSet<String>();
		vt = new HashSet<String>();
		productions = new HashMap<String, HashSet<String>>();
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
	
	public HashSet<String> getFirst(String a){
		HashSet<String> ret = new HashSet<String>();
		if(vt.contains(a)) {
			ret.add(a);
			return ret;
		} else if (vn.contains(a)) {
			HashSet<String> symbolProductions = productions.get(a);
			for(String prod : symbolProductions) {
				if (prod.equals("&")){
					ret.add("&");
					continue;
				}
				ArrayList<String> tokens = tokenize(prod);
				for(int i = 0; i < tokens.size(); i++) {
					String c = tokens.get(i);
					if(vt.contains(c)) {
						ret.add(new String(c));
						break;
					} else {
						HashSet<String> cFirst = this.getFirst(c);
						
						if(cFirst.contains("&") && i != tokens.size()-1) {
							cFirst.remove("&");
							ret.addAll(cFirst);
						} else if (cFirst.contains("&")) {
							ret.addAll(cFirst);
						} else {
							ret.addAll(cFirst);
							break;
						}
					}
				}
			}
			return ret;
		}
		
		return null;
	}
	
	private ArrayList<String> tokenize(String prod){
		Pattern pattern = Pattern.compile("[A-Z][0-9]*");
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
			prodList.add(prod);
			cfg.productions.put(vn, prodList);
		}
		return true;
	}

	private static String[] getProductions(String input) {
		String[] prod = input.split("[\\r\\n]+");
		return prod;
	}

}
