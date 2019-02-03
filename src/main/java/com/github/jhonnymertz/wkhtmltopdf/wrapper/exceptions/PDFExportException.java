package com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions;

/**
 * Exception to describe and track pdf exporting errors
 */
public class PDFExportException extends RuntimeException {

    private String command;

    private int exitStatus;

    private byte[] out;

    private byte[] err;

    public PDFExportException(String command, int exitStatus, byte[] err, byte[] out) {
        this.command = command;
        this.exitStatus = exitStatus;
        this.err = err;
        this.out = out;
    }

    public String getCommand() {
        return command;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public byte[] getOut() {
        return out;
    }

    public byte[] getErr() {
        return err;
    }

    @Override
    public String getMessage() {
        return "Process (" + this.command + ") exited with status code " + this.exitStatus + ":\n" + new String(err);
    }
}
