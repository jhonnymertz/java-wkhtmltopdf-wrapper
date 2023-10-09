package com.github.jhonnymertz.wkhtmltopdf.wrapper.objects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;

/**
 * The type Page.
 */
public class Page extends BaseObject {
    private String source;
    private String filePath;
    private SourceType type;

    /**
     * Instantiates a new Page.
     *
     * @param source the source
     * @param type   the type
     */
    public Page(final String source, final SourceType type) {
        this.source = source;
        this.type = type;
    }

    @Override
    public String SetObjectIdentifier()
    {
        // leave blank as 'page' identifier is optional as per the wkhtmltopdf documentation
        return "";
    }

    @Override
    public List<String> getCommandAsList(final Pdf pdf) throws IOException
    {
        List<String> commands = new ArrayList<>();
        if(StringUtils.isNotBlank(objectIdentifier)){
            commands.add( objectIdentifier );
        }

        // specify input
        if ( this.getType().equals( SourceType.htmlAsString ) )
        {
            // htmlAsString pages are first store into a temp file, then the location is passed as parameter to
            // wkhtmltopdf, this is a workaround to avoid huge commands
            if ( this.getFilePath() != null )
                Files.deleteIfExists( Paths.get( this.getFilePath() ) );
            File temp = File.createTempFile( Pdf.TEMPORARY_FILE_PREFIX + UUID.randomUUID().toString(), ".html", pdf.getTempDirectory() ); // TODO: figure out a way to seperate this, so we don't need to pass the Pdf object in
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

    /**
     * Gets source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets source.
     *
     * @param source the source
     */
    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public SourceType getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(final SourceType type) {
        this.type = type;
    }

    /**
     * Gets file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets file path.
     *
     * @param filePath the file path
     */
    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }
}
