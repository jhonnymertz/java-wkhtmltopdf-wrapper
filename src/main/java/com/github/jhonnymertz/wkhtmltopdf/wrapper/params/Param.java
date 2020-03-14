package com.github.jhonnymertz.wkhtmltopdf.wrapper.params;

import java.util.ArrayList;
import java.util.List;

public class Param {

    private String key;

    // Some commands accept more than one value such as cookies and headers
    private List<String> values = new ArrayList<String>();

    public Param(String key, String... valueArray) {
        this.key = key;
        for (String value : valueArray) {
            values.add(value);
        }
    }

    public Param(String key) {
        this(key, new String[0]);
    }

    public String getKey() {
        return key;
    }

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
