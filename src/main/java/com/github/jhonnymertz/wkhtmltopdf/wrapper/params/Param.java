package com.github.jhonnymertz.wkhtmltopdf.wrapper.params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Param.
 */
public class Param {

    private String key;

    // Some commands accept more than one value such as cookies and headers
    private List<String> values = new ArrayList<>();

    /**
     * Instantiates a new Param.
     *
     * @param key        the key
     * @param valueArray the value array
     */
    public Param(final String key, final String... valueArray) {
        this.key = key;
        Collections.addAll(values, valueArray);
    }

    /**
     * Instantiates a new Param.
     *
     * @param key the key
     */
    public Param(final String key) {
        this(key, new String[0]);
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets values.
     *
     * @return the values
     */
    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(Symbol.separator)
                .append(Symbol.param).append(key);
        for (String value : values)
            sb.append(Symbol.separator).append(value);
        return sb.toString();
    }

}
