package br.usp.poli.lta.spagen.spa;

public class Empty extends Symbol {

	public static final Empty EMPTY = new Empty(); 
	
	private Empty() {
	}
	
	@Override
	public String getLabel() {
		return "";
	}
	
}
