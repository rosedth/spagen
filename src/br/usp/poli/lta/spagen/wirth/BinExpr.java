package br.usp.poli.lta.spagen.wirth;

import java.util.List;

public abstract class BinExpr extends Expr {

	private List<Expr> expressions;

	public BinExpr(List<Expr> expressions) {
		super();
		this.expressions = expressions;
	}

	public List<Expr> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<Expr> expressions) {
		this.expressions = expressions;
	}
	
}
