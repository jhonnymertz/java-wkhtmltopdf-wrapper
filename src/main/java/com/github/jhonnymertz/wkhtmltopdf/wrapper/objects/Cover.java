package com.github.jhonnymertz.wkhtmltopdf.wrapper.objects;

public class Cover extends Page {
    @Override
    public String SetObjectIdentifier()
    {
        return "cover";
    }

    public Cover( String source, SourceType type )
    {
        super( source, type );
    }
    
}
