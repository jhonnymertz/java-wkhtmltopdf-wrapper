package com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions;

/**
 * Exception to describe and track pdf exporting errors due to timeout
 */
public class PDFTimeoutException extends RuntimeException {

    /**
     * The PDF command which originated the exception
     */
    private String command;

    /**
     * The default exit status code for timeout of the PDF command
     */
    private int timeout;

    /**
     * The output of the PDF command
     */
    private byte[] out;

    /**
     * Instantiates a new Pdf timeout exception when wkhtmltopdf timeout happens
     *
     * @param command the command
     * @param timeout the timeout
     * @param out     the out
     */
    public PDFTimeoutException(final String command, final int timeout, final byte[] out) {
        super(String.format("Process '%s' timeout after %d seconds. " +
                "Try to increase the timeout via the 'PDF.setTimeout()' method.", command, timeout));
        this.command = command;
        this.timeout = timeout;
        this.out = out;
    }

    /**
     * Instantiates a new Pdf timeout exception when future process timeout happens
     *
     * @param timeout the timeout
     * @param e       the exception
     */
    public PDFTimeoutException(final int timeout, final Exception e) {
        super(String.format("Process timeout after %s seconds. " +
                "Try to increase the timeout via the 'PDF.setTimeout()' method.", timeout), e);
    }

    /**
     * Gets command.
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets exit status.
     *
     * @return the exit status
     */
    public int getExitStatus() {
        return 124;
    }

    /**
     * Get out byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getOut() {
        return out;
    }

    /**
     * Get timeout.
     *
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }
}
