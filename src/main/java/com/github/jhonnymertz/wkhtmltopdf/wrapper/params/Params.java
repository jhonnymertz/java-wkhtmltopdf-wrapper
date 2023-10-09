package com.github.jhonnymertz.wkhtmltopdf.wrapper.params;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The type Params.
 */
public class Params {

    private Collection<Param> params;

    /**
     * Instantiates a new Params.
     */
    public Params() {
        this.params = new ArrayList<>();
    }

    /**
     * Add.
     *
     * @param param  the param
     * @param params the params
     */
    public void add(final Param param, final Param... params) {
        this.params.add(param);
        this.params.addAll(Arrays.asList(params));
    }

    /**
     * Gets params as string list.
     *
     * @return the params as string list
     */
    public List<String> getParamsAsStringList() {
        List<String> commandLine = new ArrayList<>();

        for (Param p : params) {
            commandLine.add(p.getKey());

            for (String value : p.getValues()) {
                if (value != null) {
                    commandLine.add(value);
                }
            }
        }

        return commandLine;
    }

    @Override
    public String toString() {
        return StringUtils.join(params, "");
    }

}
