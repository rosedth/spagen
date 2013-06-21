package br.usp.poli.lta.spagen.wirth;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

	private Lexer gramLexer;

	private ArrayList<String> nonTerminalList;
	private ArrayList<String> terminalList;
	private ArrayList<String> keywordList;
		
	public ArrayList<String> getNonTerminalList() {
		return nonTerminalList;
	}
	public ArrayList<String> getTerminalList() {
		return terminalList;
	}
	public ArrayList<String> getKeywordList() {
		return keywordList;
	}
	
	
	public Parser(BufferedReader bufferedReader) {
		super();
		this.gramLexer = new Lexer(bufferedReader);
		nonTerminalList=new ArrayList<String>();
		terminalList=new ArrayList<String>();
		keywordList=new ArrayList<String>();
	}
	
	public Grammar parse() throws IOException, ParserException {
		this.gramLexer.next();
		this.nonTerminalList.add(this.gramLexer.peek().getLexeme());
		Grammar grammar = this.grammar();
		this.consume(TokenType.EOF);
		return grammar;
	}
	
	
	private Grammar grammar() throws IOException, ParserException {
		Grammar grammar = new Grammar();
		while (this.gramLexer.peek().getType() == TokenType.ID){
			this.rule(grammar);
		}
		return grammar;
	}

	private void rule(Grammar grammar) throws IOException, ParserException {
		String ruleName = this.gramLexer.next().getLexeme();
		this.consume(TokenType.EQ);
		Expr expr = this.orexpr();
		this.consume(TokenType.DOT);
		grammar.add(ruleName, expr);
	}
	
	private Expr orexpr() throws IOException, ParserException {
		Expr expr = this.catexpr();
		if (this.gramLexer.peek().getType() == TokenType.PIPE) {
			List<Expr> expressions = new ArrayList<Expr>();
			expressions.add(expr);
			while (this.gramLexer.peek().getType() == TokenType.PIPE) {
				this.gramLexer.next();
				expressions.add(this.catexpr());
			}
			expr = new OrExpr(expressions);
		}
		return expr;
	}
	
	private boolean isAtomExpr() {
		switch (this.gramLexer.peek().getType()) {
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
		String name;
		switch (this.gramLexer.peek().getType()) {
		case LPAREN:
			this.gramLexer.next();
			expr = this.orexpr();
			consume(TokenType.RPAREN);
			break;
		case LBRACKET:
			this.gramLexer.next();
			expr = new OptExpr(this.orexpr());
			consume(TokenType.RBRACKET);
			break;
		case LCURLY:
			this.gramLexer.next();
			expr = new RepeatExpr(this.orexpr());
			consume(TokenType.RCURLY);
			break;
		case ID: 
			name=this.gramLexer.next().getLexeme();
			expr = new NonTermExpr(name);
			if (!nonTerminalList.contains(name))
				this.nonTerminalList.add(name);
			break;
		case STRING:
			name=this.gramLexer.next().getLexeme();
			expr = new TermExpr(name);

				if (isText(name)){
					if (!keywordList.contains(name)){
						this.keywordList.add(name);
					}
				}else{
					if (!terminalList.contains(name)){
						this.terminalList.add(name);
					}
				}
			break;
		default:
			this.parserError(TokenType.LPAREN, TokenType.LBRACKET, 
				TokenType.LCURLY, TokenType.ID, TokenType.STRING);
		}
		return expr;
	}
	private boolean isText(String name){
		boolean text=true;
		for(int i=0;i<name.length();i++ ){
			if (!Character.isLetter(name.charAt(i))){
				text=false;
				break;
			}
		}
		return (text && (name.length()>1));
	}
	
	
	private Token consume(TokenType expected) throws IOException, ParserException {
		if (this.gramLexer.peek().getType() != expected)
			this.parserError(expected);
		return this.gramLexer.next();
	}
	
	private void parserError(TokenType expected, TokenType ... expecteds) throws ParserException {
		TokenType found = this.gramLexer.peek().getType();
		StringBuilder sb = new StringBuilder();
		sb.append("expected: ");
		sb.append(expected);
		for (int i=0; i<expecteds.length; i++) {
			sb.append(", ");
			sb.append(expecteds[i]);
		}
		sb.append(" but found: ");
		sb.append(found);
		throw new ParserException(sb.toString(), this.gramLexer.getLine(), this.gramLexer.getColumn());
	}
	
}
