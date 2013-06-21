package br.usp.poli.lta.spagen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.usp.poli.lta.spagen.spa.Machine;
import br.usp.poli.lta.spagen.spa.SPA;
import br.usp.poli.lta.spagen.wirth.Grammar;
import br.usp.poli.lta.spagen.wirth.Parser;

public class Main {
	
	public static void main(String[] args) throws Exception {
		FileInputStream file=new FileInputStream("..\\Wirth.txt");
		BufferedReader in = new BufferedReader(new InputStreamReader(file));
		
		BufferedWriter out;
		
		FileWriter fileNonTerms = new FileWriter("..\\NonTerms.txt");
		FileWriter fileTerms = new FileWriter("..\\Terms.txt");
		FileWriter fileKeywords = new FileWriter("..\\Keywords.txt");
		
		Parser p = new Parser(in);
		Grammar g = p.parse();
		file.close();
		in.close();
		if (!g.undeclaredRules().isEmpty()) {
			for (String rulename : g.undeclaredRules()) {
				System.out.println("undeclared rule: " + rulename);
			}
		} else {
			SPA spa = SPA.fromGrammar(g);
			spa.deleteEmpty();
			spa.deleteUnreachables();
			
			for (Machine machine : spa.getMachines()){
				out= new BufferedWriter(new FileWriter("..\\"+machine.getId()+".dot"));
				machine.writeDot(out);
				out.close();
			}

			out= new BufferedWriter(fileTerms);
			for(String term: p.getTerminalList()){
				out.write(term);
				out.newLine();
			}
			out.close();
			
			out= new BufferedWriter(fileNonTerms);
			for(String term: p.getNonTerminalList()){
				out.write(term);
				out.newLine();
			}
			out.close();			
		
			out= new BufferedWriter(fileKeywords);
			ArrayList<String> keys=p.getKeywordList();
			for(int i=0;i<keys.size();i++){
				out.write(keys.get(i));
				out.newLine();
			}
			out.close();			
		}
	}
	
}
