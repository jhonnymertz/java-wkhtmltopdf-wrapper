package com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions;

/**
 * Exception to describe and track pdf generation process errors
 */
public class PDFGenerationException extends RuntimeException {

    /**
     * Instantiates a new Pdf timeout exception when future process errors happens
     *
     * @param e the original exception
     */
    public PDFGenerationException(final Exception e) {
        super(e);
    }
}
