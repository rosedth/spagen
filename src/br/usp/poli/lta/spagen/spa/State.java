package br.usp.poli.lta.spagen.spa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class State {

	private boolean isFinal;
	private Map<Symbol, Set<State>> transitions;
	
	public State(boolean isFinal) {
		this.isFinal = isFinal;
		this.transitions = new HashMap<Symbol, Set<State>>();
	}
	
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	
	public boolean isFinal() {
		return isFinal;
	}
	
	public void addTransition(Symbol symbol, State nextState) {
		Set<State> nextStates = this.transitions.get(symbol);
		if (nextStates == null) {
			nextStates = new HashSet<State>();
			this.transitions.put(symbol, nextStates);
		}
		nextStates.add(nextState);
	}

	public Map<Symbol, Set<State>> getTransitions() {
		return transitions;
	}
	
	public Set<State> getReachableByEmpty() {
		Set<State> reachable = new HashSet<State>();
		getReachableByEmpty(this, reachable);
		return reachable;
	}
	
	public Set<State> getReachables() {
		Set<State> reachables = new HashSet<State>();
		for (Set<State> states : this.transitions.values())
			reachables.addAll(states);
		return reachables;
	}
	
	public void deleteEmpty() {
		if (!this.transitions.containsKey(Empty.EMPTY))
			return;
		for (State state : this.getReachableByEmpty()) {
			if (state == this)
				continue;
			for (Entry<Symbol, Set<State>> entry : state.transitions.entrySet()) {
				Symbol symbol = entry.getKey();
				if (symbol != Empty.EMPTY)
					for (State nextState : entry.getValue())
						this.addTransition(symbol, nextState);
			}
			if (state.isFinal())
				this.isFinal = true;
		}
		this.transitions.remove(Empty.EMPTY);
	}
	
	private static void getReachableByEmpty(State current, Set<State> reachable) {
		reachable.add(current);
		Set<State> byEmpty = current.transitions.get(Empty.EMPTY);
		if (byEmpty != null)
			for (State state : byEmpty)
				if (!reachable.contains(state))
					getReachableByEmpty(state, reachable);
	}
	
}
