package com.github.jhonnymertz.wkhtmltopdf.wrapper.objects;

/**
 * The type Cover.
 */
public class Cover extends Page {

    @Override
    public String SetObjectIdentifier() {
        return "cover";
    }

    /**
     * Instantiates a new Cover.
     *
     * @param source the source
     * @param type   the type
     */
    public Cover(final String source, final SourceType type) {
        super(source, type);
    }

}
