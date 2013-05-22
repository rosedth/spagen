package br.usp.poli.lta.spagen.wirth;

public abstract class Expr {
	
	public static void unknownError(Expr badExpr) {
		throw new IllegalArgumentException("expecting " +
				CatExpr.class.getName() + ", " +
				OrExpr.class.getName() + ", " +
				RepeatExpr.class.getName() + ", " +
				OptExpr.class.getName() + ", " +
				NonTermExpr.class.getName() + " not " +
				badExpr.getClass().getName());
	}
	
}
