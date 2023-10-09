package com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Params;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for Xvfb parameters and configuration
 */
public class XvfbConfig {

    private final Params params = new Params();
    private String command;

    /**
     * Instantiates a new Xvfb config.
     */
    public XvfbConfig() {
        this("xvfb-run");
    }

    /**
     * Instantiates a new Xvfb config.
     *
     * @param command the command
     */
    public XvfbConfig(final String command) {
        setCommand(command);
    }

    /**
     * Add params.
     *
     * @param param  the param
     * @param params the params
     */
    public void addParams(final Param param, final Param... params) {
        this.params.add(param, params);
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
     * Sets command.
     *
     * @param command the command
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Gets command line.
     *
     * @return the command line
     */
    public List<String> getCommandLine() {
        List<String> commandLine = new ArrayList<>();

        commandLine.add(getCommand());
        commandLine.addAll(params.getParamsAsStringList());

        return commandLine;
    }

    @Override
    public String toString() {
        return "{" +
                "command='" + command + '\'' +
                ", params=" + params +
                '}';
    }
}
