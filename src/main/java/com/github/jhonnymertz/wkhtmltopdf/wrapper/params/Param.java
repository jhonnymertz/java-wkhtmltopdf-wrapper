package com.github.jhonnymertz.wkhtmltopdf.wrapper.params;

public class Param {

    private String key;

    private String value;

    public Param(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Param(String key) {
        this(key, null);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append(Symbol.separator)
                .append(Symbol.param).append(key);
        if (value != null)
            sb.append(Symbol.separator).append(value);
        return sb.toString();
    }

}
