package br.usp.poli.lta.spagen.wirth;

import java.io.BufferedReader;
import java.io.IOException;

public class Lexer {

	private BufferedReader bufferedReader;
	private Token current;
	private int line;
	private int column;
	
	public Lexer(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
		this.current = null;
		this.line = 0;
		this.column = 0;
	}
	
	public Token peek() {
		return this.current;
	}
	
	public Token next() throws IOException, ParserException {
		Token old = this.current;
		int c = this.readAndUpdatePos();
		
		while (Character.isSpaceChar(c) || (c == '\n') || (c == '\t') || (c == '\r'))
			c = this.readAndUpdatePos();
		
		switch (c) {
		case -1:
			this.current = Token.EOF;
			break;
		case '(':
			this.current = Token.LPAREN;
			break;
		case ')':
			this.current = Token.RPAREN;
			break;
		case '[':
			this.current = Token.LBRACKET;
			break;
		case ']':
			this.current = Token.RBRACKET;
			break;
		case '{':
			this.current = Token.LCURLY;
			break;
		case '}':
			this.current = Token.RCURLY;
			break;
		case '.':
			this.current = Token.DOT;
			break;
		case '=':
			this.current = Token.EQ;
			break;
		case '|':
			this.current = Token.PIPE;
			break;
		case '"':
			this.current = this.string();
			break;
		default:
			if (!Character.isLetter(c))
				this.lexerError("unexpected character: '" + (char)c + "'");
			this.current = this.identifier(c);
		}
		
		return old;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}

	private Token string() throws IOException, ParserException {
		StringBuilder sb = new StringBuilder();
		int c = this.readAndUpdatePos();
		while ((c != -1) && (c != '"')) {
			sb.append((char)c);
			c = this.readAndUpdatePos();
		}
		if (c == -1)
			this.lexerError("unclosed terminal");
		return new Token(TokenType.STRING, sb.toString());
	}
	
	private Token identifier(int c) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append((char)c);
		
		this.bufferedReader.mark(1);
		c = this.read();
		while (Character.isLetterOrDigit(c)) {
			this.updatePos(c);
			sb.append((char)c);
			this.bufferedReader.mark(1);
			c = this.read();
		}
		
		this.bufferedReader.reset();
		return new Token(TokenType.ID, sb.toString());
	}
	
	private void updatePos(int c) {
		if (c == '\n') {
			this.line++;
			this.column = 1;
		} else
			this.column++;
				
	}
	
	private int read() throws IOException {
		return this.bufferedReader.read();
	}
	
	private int readAndUpdatePos() throws IOException {
		int c = this.read(); 
		this.updatePos(c);
		return c;
	}
	
	private void lexerError(String message) throws ParserException {
		throw new ParserException(message, this.line, this.column);
	}
	
}
