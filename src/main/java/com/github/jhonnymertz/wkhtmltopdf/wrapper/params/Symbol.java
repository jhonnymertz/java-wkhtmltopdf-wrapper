package com.github.jhonnymertz.wkhtmltopdf.wrapper.params;

/**
 * The enum Symbol.
 */
public enum Symbol {

    /**
     * Separator symbol.
     */
    separator(" "),
    /**
     * Param symbol.
     */
    param("");

    private final String symbol;

    Symbol(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

}
