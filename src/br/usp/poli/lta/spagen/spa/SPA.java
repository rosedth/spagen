package br.usp.poli.lta.spagen.spa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.usp.poli.lta.spagen.wirth.CatExpr;
import br.usp.poli.lta.spagen.wirth.Expr;
import br.usp.poli.lta.spagen.wirth.Grammar;
import br.usp.poli.lta.spagen.wirth.NonTermExpr;
import br.usp.poli.lta.spagen.wirth.OptExpr;
import br.usp.poli.lta.spagen.wirth.OrExpr;
import br.usp.poli.lta.spagen.wirth.RepeatExpr;
import br.usp.poli.lta.spagen.wirth.TermExpr;

public class SPA {

	private Set<Machine> machines;
	
	public static SPA fromGrammar(Grammar grammar) {
		SPA spa = new SPA();
		
		Map<String, Machine> machines = new HashMap<String, Machine>();
		for (String rulename : grammar.getRules())
			machines.put(rulename, new Machine(rulename));
		
		for (String rulename : machines.keySet()) {
			Machine machine = machines.get(rulename);
			State init = machine.newState();
			machine.setInit(init);
			State stop = machine.newState(true);
			Expr expr = grammar.get(rulename);
			buildMachine(machines, machine, init, expr, stop);
			spa.addMachine(machine);
		}
			
		return spa;
	}
	
	private static void buildMachine(Map<String, Machine> machines, 
			Machine machine, State from, Expr expr, State to) {
		if (expr instanceof CatExpr) {
			CatExpr catExpr = (CatExpr) expr;
			List<Expr> expressions = catExpr.getExpressions();
			State before = from;
			for (int i=0, n=expressions.size(); i<n; i++) {
				Expr e = expressions.get(i);
				State after = (i+1 < n) ? machine.newState() : to;
				buildMachine(machines, machine, before, e, after);
				before = after;
			}
		} else if (expr instanceof OrExpr) {
			OrExpr orExpr = (OrExpr) expr;
			for (Expr e : orExpr.getExpressions())
				buildMachine(machines, machine, from, e, to);
		} else if (expr instanceof RepeatExpr) {
			RepeatExpr repeatExpr = (RepeatExpr) expr;
			State before = machine.newState();
			State after = machine.newState();
			from.addTransition(Empty.EMPTY, before);
			before.addTransition(Empty.EMPTY, after);
			after.addTransition(Empty.EMPTY, to);
			buildMachine(machines, machine, after, repeatExpr.getExpression(), before);
		} else if (expr instanceof OptExpr) {
			OptExpr optExpr = (OptExpr) expr;
			from.addTransition(Empty.EMPTY, to);
			buildMachine(machines, machine, from, optExpr.getExpression(), to);
		} else if (expr instanceof NonTermExpr) {
			NonTermExpr nonTermExpr = (NonTermExpr) expr;
			Machine submachine = machines.get(nonTermExpr.getName());
			Call call = new Call(submachine);
			from.addTransition(call, to);
		} else if (expr instanceof TermExpr) {
			TermExpr termExpr = (TermExpr) expr;
			Terminal terminal = new Terminal(termExpr.getName());
			from.addTransition(terminal, to);
		} else {
			Expr.unknownError(expr);
		}
	}
	
	public SPA() {
		this.machines = new HashSet<Machine>();
	}
	
	public void addMachine(Machine machine) {
		this.machines.add(machine);
	}
	
	public void deleteEmpty() {
		for (Machine machine : this.machines)
			machine.deleteEmpty();
	}
	
	public void deleteUnreachables() {
		for (Machine machine : this.machines)
			machine.deleteUnreachables();
	}
	
	public Set<Machine> getMachines() {
		return machines;
	}
	
}
