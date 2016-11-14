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

    public void setKey(String key) {
        this.key = key;
    }

    // This is kept for backwards compatibility it will
    // only return the first arg if it exists
    @Deprecated
    public String getValue() {
        if (values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    public List<String> getValues() {
        return values;
    }

    @Deprecated
    public void setValue(String value) {
        if (values.isEmpty()) {
            values.add(value);
        } else {
            values.set(0, value);
        }
    }

    public void setValues(List<String> values) {
        this.values = values;
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
