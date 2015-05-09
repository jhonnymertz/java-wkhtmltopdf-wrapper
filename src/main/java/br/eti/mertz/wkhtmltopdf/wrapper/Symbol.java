package br.eti.mertz.wkhtmltopdf.wrapper;

public enum Symbol {

	separator(" "), param("");

	private String symbol;

	Symbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

}
