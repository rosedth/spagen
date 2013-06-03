package br.usp.poli.lta.spagen.wirth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Grammar {

	private Map<String, Expr> rules;
	
	public Grammar() {
		this.rules = new HashMap<String, Expr>();
	}
	
	public void add(String name, Expr expr) {
		if (this.rules.containsKey(name)) {
			List<Expr> expressions = new ArrayList<Expr>();
			expressions.add(this.rules.get(name));
			expressions.add(expr);
			expr = new OrExpr(expressions);
		}
		this.rules.put(name, expr);
	}
	
	public Set<String> getRules() {
		return this.rules.keySet();
	}
	
	public Expr get(String name) {
		return this.rules.get(name);
	}
	
	public Set<String> undeclaredRules() {
		Set<String> declareds = this.rules.keySet();
		Set<String> undeclareds = new HashSet<String>();
		
		for (Entry<String, Expr> entry : this.rules.entrySet())
			this.computeUndeclared(entry.getValue(), declareds, undeclareds);

		return undeclareds;
	}
	
	public Set<String> selfRecursives() {
		Set<String> recs = new HashSet<String>();
		for (Entry<String, Expr> entry : this.rules.entrySet()) {
			String rulename = entry.getKey();
			Expr expr = entry.getValue();
			Set<String> visiteds = new HashSet<String>();
			visiteds.add(rulename);
			this.computeSelfRecursives(rulename, false, expr, false, visiteds, recs);
		}
		return recs;
	}
	
	private void computeSelfRecursives(String rulename, boolean haveTermsBefore,
			Expr expr, boolean haveTermsAfter, Set<String> visiteds, Set<String> recs) {
		if (expr instanceof CatExpr) {
			CatExpr catExpr = (CatExpr) expr;
			List<Expr> expressions = catExpr.getExpressions();
			for (int i=0, n=expressions.size(); i<n; i++) {
				Expr e = expressions.get(i);
				this.computeSelfRecursives(rulename, i != 0, e, i+1<n, visiteds, recs);
			}
		} else if (expr instanceof OrExpr) {
			OrExpr orExpr = (OrExpr) expr;
			List<Expr> expressions = orExpr.getExpressions();
			for (Expr e : expressions)
				this.computeSelfRecursives(rulename, haveTermsBefore, e, haveTermsAfter, visiteds, recs);
		} else if (expr instanceof RepeatExpr) {
			RepeatExpr repeatExpr = (RepeatExpr) expr;
			this.computeSelfRecursives(rulename, haveTermsBefore, repeatExpr.getExpression(), haveTermsAfter, visiteds, recs);
		} else if (expr instanceof OptExpr) {
			OptExpr orExpr = (OptExpr) expr;
			this.computeSelfRecursives(rulename, haveTermsBefore, orExpr.getExpression(), haveTermsAfter, visiteds, recs);
		} else if (expr instanceof NonTermExpr) {
			NonTermExpr nonTermExpr = (NonTermExpr) expr;
			String name = nonTermExpr.getName();
			if (name.equals(rulename)) {
				if (haveTermsBefore && haveTermsAfter)
					recs.add(rulename);
			} else if (!visiteds.contains(name)) {
				visiteds = new HashSet<String>(visiteds);
				visiteds.add(name);
				Expr e = this.rules.get(name);
				this.computeSelfRecursives(rulename, haveTermsBefore, e, haveTermsAfter, visiteds, recs);
			}
		} else if (!(expr instanceof TermExpr)) {
			Expr.unknownError(expr);
		}
	}

	private void computeUndeclared(Expr expr, Set<String> declared, Set<String> undeclared) {
		if (expr instanceof CatExpr) {
			CatExpr catExpr = (CatExpr) expr;
			for (Expr e : catExpr.getExpressions())
				computeUndeclared(e, declared, undeclared);
		} else if (expr instanceof OrExpr) {
			OrExpr orExpr = (OrExpr) expr;
			for (Expr e : orExpr.getExpressions())
				computeUndeclared(e, declared, undeclared);
		} else if (expr instanceof RepeatExpr) {
			RepeatExpr repeatExpr = (RepeatExpr) expr;
			computeUndeclared(repeatExpr.getExpression(), declared, undeclared);
		} else if (expr instanceof OptExpr) {
			OptExpr optExpr = (OptExpr) expr;
			computeUndeclared(optExpr.getExpression(), declared, undeclared);
		} else if (expr instanceof NonTermExpr) {
			NonTermExpr nonTermExpr = (NonTermExpr) expr;
			String name = nonTermExpr.getName(); 
			if (!declared.contains(name))
				undeclared.add(name);
		} else if (!(expr instanceof TermExpr)) {
			Expr.unknownError(expr);
		}
	}
	
}
