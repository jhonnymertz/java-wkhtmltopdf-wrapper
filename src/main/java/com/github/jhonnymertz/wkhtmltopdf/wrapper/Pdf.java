package com.github.jhonnymertz.wkhtmltopdf.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.page.Page;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.page.PageType;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Params;

public class Pdf {

    private static final String STDINOUT = "-";

    private final WrapperConfig wrapperConfig;

    private final Params params;

    private final List<Page> pages;

    private boolean hasToc = false;

    public Pdf() {
      this(new WrapperConfig());
    }

    public Pdf(WrapperConfig wrapperConfig) {
        this.wrapperConfig = wrapperConfig;
        this.params = new Params();
        this.pages = new ArrayList<Page>();
    }

    public void addPage(String source, PageType type) {
        this.pages.add(new Page(source, type));
    }

    public void addToc() {
        this.hasToc = true;
    }

    public void addParam(Param param, Param... params) {
        this.params.add( param, params );
    }

    public void saveAs(String path) throws IOException, InterruptedException {
        saveAs(path, getPDF());
    }

    private static File saveAs(String path, byte[] document) throws IOException {
        File file = new File(path);
        FileUtils.writeByteArrayToFile( file, document );

        return file;
    }

    public byte[] getPDF() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(getCommandAsArray());

        byte[] inputBytes = IOUtils.toByteArray( process.getInputStream() );
        byte[] errorBytes = IOUtils.toByteArray( process.getErrorStream() );
        
        process.waitFor();

        if (process.exitValue() != 0) {
            throw new RuntimeException("Process (" + getCommand() + ") exited with status code " + process.exitValue() + ":\n" + new String(errorBytes));
        }

        return inputBytes;
    }

    private String[] getCommandAsArray() throws IOException {
        List<String> commandLine = new ArrayList<String>();

        if (wrapperConfig.isXvfbEnabled())
            commandLine.addAll(wrapperConfig.getXvfbConfig().getCommandLine());

        commandLine.add(wrapperConfig.getWkhtmltopdfCommand());

        commandLine.addAll(params.getParamsAsStringList());

        if (hasToc)
            commandLine.add("toc");

        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {

                File temp = File.createTempFile("java-wkhtmltopdf-wrapper" + UUID.randomUUID().toString(), ".html");
                FileUtils.writeStringToFile(temp, page.getSource(), "UTF-8");

                page.setSource(temp.getAbsolutePath());
            }

            commandLine.add(page.getSource());
        }
        commandLine.add(STDINOUT);
        return commandLine.toArray(new String[commandLine.size()]);
    }

    public String getCommand() throws IOException {
        return StringUtils.join(getCommandAsArray(), " ");
    }

}
