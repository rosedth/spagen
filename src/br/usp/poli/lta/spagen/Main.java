package br.usp.poli.lta.spagen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import br.usp.poli.lta.spagen.spa.Machine;
import br.usp.poli.lta.spagen.spa.SPA;
import br.usp.poli.lta.spagen.wirth.Grammar;
import br.usp.poli.lta.spagen.wirth.Parser;

public class Main {
	
	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Parser p = new Parser(in);
		Grammar g = p.parse();
		if (!g.undeclaredRules().isEmpty()) {
			for (String rulename : g.undeclaredRules()) {
				System.out.println("undeclared rule: " + rulename);
			}
		} else {
			SPA spa = SPA.fromGrammar(g);
			spa.deleteEmpty();
			spa.deleteUnreachables();
			
			for (Machine machine : spa.getMachines())
				machine.writeDot(new OutputStreamWriter(System.out));
		}
	}
	
}
