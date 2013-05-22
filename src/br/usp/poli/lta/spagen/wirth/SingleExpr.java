package br.usp.poli.lta.spagen.wirth;

public abstract class SingleExpr extends Expr {

	private Expr expression;
	
	public SingleExpr(Expr expression) {
		super();
		this.expression = expression;
	}

	public Expr getExpression() {
		return expression;
	}

	public void setExpression(Expr expression) {
		this.expression = expression;
	}
	
}
