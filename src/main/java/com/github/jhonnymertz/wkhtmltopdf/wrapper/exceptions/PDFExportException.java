package com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions;

/**
 * Exception to describe and track pdf exporting errors
 */
public class PDFExportException extends RuntimeException {

    private String command;

    private int exitStatus;

    private byte[] out;

    private byte[] err;

    /**
     * Instantiates a new Pdf export exception.
     *
     * @param command    the command
     * @param exitStatus the exit status
     * @param err        the err
     * @param out        the out
     */
    public PDFExportException(final String command, final int exitStatus, final byte[] err, final byte[] out) {
        this.command = command;
        this.exitStatus = exitStatus;
        this.err = err;
        this.out = out;
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
        return exitStatus;
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
     * Get err byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getErr() {
        return err;
    }

    @Override
    public String getMessage() {
        return "Process (" + this.command + ") exited with status code " + this.exitStatus + ":\n" + new String(err);
    }
}
