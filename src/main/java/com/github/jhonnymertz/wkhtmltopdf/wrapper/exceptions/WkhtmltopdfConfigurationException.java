package com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions;


/**
 * Exception to describe and track wrapper configuration errors
 */
public class WkhtmltopdfConfigurationException extends RuntimeException {

    /**
     * Instantiates a new Wkhtmltopdf configuration exception.
     *
     * @param s the s
     */
    public WkhtmltopdfConfigurationException(final String s) {
        super(s);
    }

}
