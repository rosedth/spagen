package br.usp.poli.lta.spagen.spa;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Machine {

	private String id;
	private Set<State> states;
	private State init;
	
	public Machine(String id) {
		this.id = id;
		this.states = new HashSet<State>();
	}
	
	public void setInit(State init) {
		this.init = init;
	}
	
	public State getInit() {
		return init;
	}
	
	public State newState(boolean isFinal) {
		State state = new State(isFinal);
		this.states.add(state);
		return state;
	}
	
	public State newState() {
		return this.newState(false);
	}
	
	public void deleteEmpty() {
		for (State state : this.states)
			state.deleteEmpty();
	}
	
	public Set<State> getStates() {
		return states;
	}
	
	public void deleteUnreachables() {
		Set<State> reachable = new HashSet<State>();
		Set<State> frontier = new HashSet<State>();
		frontier.add(this.init);
		
		while (!frontier.isEmpty()) {
			State state = frontier.iterator().next();
			frontier.remove(state);
			reachable.add(state);
			for (State s : state.getReachables())
				if (!reachable.contains(s))
					frontier.add(s);
		}
		
		this.states = reachable;
	}
	
	public Set<State> getFinalStates() {
		Set<State> fs = new HashSet<State>();
		for (State s : this.states)
			if (s.isFinal())
				fs.add(s);
		return fs;
	}
	
	public void writeDot(Writer writer) {
		PrintWriter pw = new PrintWriter(writer);
		Map<State, Integer> m = this.numberedStates();
		pw.print("digraph ");
		pw.print(this.id);
		pw.println(" {");
		pw.print("\tnode [shape = doublecircle];");
		for (State finalState : this.getFinalStates()) {
			pw.print(" ");
			pw.print(m.get(finalState));
		}
		pw.println(";");
		pw.println("\tghost [shape = none, label = \"\"];");
		pw.println("\tnode [shape = circle];");
		pw.print("\tghost -> ");
		pw.print(m.get(this.init));
		pw.print(" [ label = \"");
		pw.print(this.id);
		pw.println("\" ];");
		for (State from : this.states) {
			Map<Symbol, Set<State>> transitions = from.getTransitions();
			for (Entry<Symbol, Set<State>> entry : transitions.entrySet()) {
				Symbol symbol = entry.getKey();
				for (State to : entry.getValue()) {
					pw.print("\t");
					pw.print(m.get(from));
					pw.print(" -> ");
					pw.print(m.get(to));
					pw.print(" [ label = \"");
					pw.print(symbol.getLabel());
					pw.println("\" ];");
				}
			}
		}
		pw.println("}");
		pw.flush();
	}
	
	private Map<State, Integer> numberedStates() {
		Map<State, Integer> m = new HashMap<State, Integer>();
		for (State s : this.states)
			m.put(s, m.size());
		return m;
	}

	public String getId() {
		return this.id;
	}
	
}
