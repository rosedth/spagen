package br.usp.poli.lta.spagen.wirth;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private Lexer lexer;
	
	public Parser(BufferedReader bufferedReader) {
		super();
		this.lexer = new Lexer(bufferedReader);
	}
	
	public Grammar parse() throws IOException, ParserException {
		this.lexer.next();
		Grammar grammar = this.grammar();
		this.consume(TokenType.EOF);
		return grammar;
	}
	
	private Grammar grammar() throws IOException, ParserException {
		Grammar grammar = new Grammar();
		while (this.lexer.peek().getType() == TokenType.ID)
			this.rule(grammar);
		return grammar;
	}

	private void rule(Grammar grammar) throws IOException, ParserException {
		String ruleName = this.lexer.next().getLexeme();
		this.consume(TokenType.EQ);
		Expr expr = this.orexpr();
		this.consume(TokenType.DOT);
		grammar.add(ruleName, expr);
	}
	
	private Expr orexpr() throws IOException, ParserException {
		Expr expr = this.catexpr();
		if (this.lexer.peek().getType() == TokenType.PIPE) {
			List<Expr> expressions = new ArrayList<Expr>();
			expressions.add(expr);
			while (this.lexer.peek().getType() == TokenType.PIPE) {
				this.lexer.next();
				expressions.add(this.catexpr());
			}
			expr = new OrExpr(expressions);
		}
		return expr;
	}
	
	private boolean isAtomExpr() {
		switch (this.lexer.peek().getType()) {
		case LPAREN:
		case LBRACKET:
		case LCURLY:
		case STRING:
		case ID:
			return true;
		default:
			return false;
		}
	}
	
	private Expr catexpr() throws IOException, ParserException {
		Expr expr = this.atomexpr();
		if (this.isAtomExpr()) {
			List<Expr> expressions = new ArrayList<Expr>();
			expressions.add(expr);
			while (this.isAtomExpr())
				expressions.add(atomexpr());
			expr = new CatExpr(expressions);
		}
		return expr;
	}
	
	private Expr atomexpr() throws IOException, ParserException {
		Expr expr = null;
		switch (this.lexer.peek().getType()) {
		case LPAREN:
			this.lexer.next();
			expr = this.orexpr();
			consume(TokenType.RPAREN);
			break;
		case LBRACKET:
			this.lexer.next();
			expr = new OptExpr(this.orexpr());
			consume(TokenType.RBRACKET);
			break;
		case LCURLY:
			this.lexer.next();
			expr = new RepeatExpr(this.orexpr());
			consume(TokenType.RCURLY);
			break;
		case ID: 
			expr = new NonTermExpr(this.lexer.next().getLexeme());
			break;
		case STRING:
			expr = new TermExpr(this.lexer.next().getLexeme());
			break;
		default:
			this.parserError(TokenType.LPAREN, TokenType.LBRACKET, 
				TokenType.LCURLY, TokenType.ID, TokenType.STRING);
		}
		return expr;
	}
	
	private Token consume(TokenType expected) throws IOException, ParserException {
		if (this.lexer.peek().getType() != expected)
			this.parserError(expected);
		return this.lexer.next();
	}
	
	private void parserError(TokenType expected, TokenType ... expecteds) throws ParserException {
		TokenType found = this.lexer.peek().getType();
		StringBuilder sb = new StringBuilder();
		sb.append("expected: ");
		sb.append(expected);
		for (int i=0; i<expecteds.length; i++) {
			sb.append(", ");
			sb.append(expecteds[i]);
		}
		sb.append(" but found: ");
		sb.append(found);
		throw new ParserException(sb.toString(), this.lexer.getLine(), this.lexer.getColumn());
	}
	
}
