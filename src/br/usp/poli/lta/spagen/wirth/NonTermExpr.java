package br.usp.poli.lta.spagen.wirth;

public class NonTermExpr extends Expr {

	private String name;

	public NonTermExpr(String name) {
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
