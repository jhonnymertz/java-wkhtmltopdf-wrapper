package com.github.jhonnymertz.wkhtmltopdf.wrapper.objects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;

public class Page extends BaseObject {
    private String source;
    private String filePath;
    private SourceType type;

    public Page(String source, SourceType type) {
        this.source = source;
        this.type = type;
    }

    @Override
    public String SetObjectIdentifier()
    {
        return "page";
    }

    @Override
    public List<String> getCommandAsList(Pdf pdf) throws IOException
    {
        List<String> commands = new ArrayList<>();
        commands.add( objectIdentifier );

        // specify input
        if ( this.getType().equals( SourceType.htmlAsString ) )
        {
            // htmlAsString pages are first store into a temp file, then the location is passed as parameter to
            // wkhtmltopdf, this is a workaround to avoid huge commands
            if ( this.getFilePath() != null )
                Files.deleteIfExists( Paths.get( this.getFilePath() ) );
            File temp = File.createTempFile( Pdf.TEMPORARY_FILE_PREFIX + UUID.randomUUID().toString(), ".html", pdf.tempDirectory ); // TODO: figure out a way to seperate this, so we don't need to pass the Pdf object in
            FileUtils.writeStringToFile( temp, this.getSource(), "UTF-8" );
            this.setFilePath( temp.getAbsolutePath() );
            commands.add( temp.getAbsolutePath() );
        }
        else
        {
            commands.add( this.getSource() );
        }

        // specify options
        commands.addAll( this.params.getParamsAsStringList() );
        return commands;

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public SourceType getType() {
        return type;
    }

    public void setType(SourceType type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
