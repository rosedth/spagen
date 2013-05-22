package br.usp.poli.lta.spagen.wirth;

public class ParserException extends Exception {
	private static final long serialVersionUID = -7722729681279071506L;

	private int line;
	private int column;
	
	public ParserException(String message, int line, int column) {
		super(message);
		this.line = line;
		this.column = column;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getMessage());
		sb.append(" at line ");
		sb.append(this.line);
		sb.append(" column ");
		sb.append(this.column);
		return sb.toString();
	}

}
