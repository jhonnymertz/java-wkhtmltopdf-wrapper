package com.github.jhonnymertz.wkhtmltopdf.wrapper.page;

public class Page {

    private String source;

    private PageType type;

    public Page(String source, PageType type) {
        this.source = source;
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public PageType getType() {
        return type;
    }

    public void setType(PageType type) {
        this.type = type;
    }
}
