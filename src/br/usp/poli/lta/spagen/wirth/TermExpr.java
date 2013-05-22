package br.usp.poli.lta.spagen.wirth;

public class TermExpr extends Expr {

	private String name;

	public TermExpr(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
