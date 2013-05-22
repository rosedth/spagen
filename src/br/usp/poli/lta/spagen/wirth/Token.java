package br.usp.poli.lta.spagen.wirth;

public class Token {

	public static final Token EOF      = new Token(TokenType.EOF);
	public static final Token LPAREN   = new Token(TokenType.LPAREN);
	public static final Token RPAREN   = new Token(TokenType.RPAREN);
	public static final Token LBRACKET = new Token(TokenType.LBRACKET);
	public static final Token RBRACKET = new Token(TokenType.RBRACKET);
	public static final Token LCURLY   = new Token(TokenType.LCURLY);
	public static final Token RCURLY   = new Token(TokenType.RCURLY);
	public static final Token DOT      = new Token(TokenType.DOT);
	public static final Token EQ       = new Token(TokenType.EQ);
	public static final Token PIPE     = new Token(TokenType.PIPE);
	
	private TokenType type;
	private String lexeme;
	
	public Token(TokenType type, String lexeme) {
		super();
		this.type = type;
		this.lexeme = lexeme;
	}
	
	public Token(TokenType type) {
		this(type, null);
	}

	public TokenType getType() {
		return type;
	}
	
	public void setType(TokenType type) {
		this.type = type;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}
	
}
