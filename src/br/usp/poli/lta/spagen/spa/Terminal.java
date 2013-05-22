package br.usp.poli.lta.spagen.spa;

public class Terminal extends Symbol {

	private String terminal;
	
	public Terminal(String terminal) {
		this.terminal = terminal;
	}
	
	public String getTerminal() {
		return terminal;
	}
	
	@Override
	public String getLabel() {
		return this.terminal;
	}
	
}
