package com.github.jhonnymertz.wkhtmltopdf.wrapper;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.page.Page;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.page.PageType;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Params;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Represents a Pdf file
 */
public class Pdf {
    
    private static final Logger logger = LoggerFactory.getLogger(Pdf.class);
    
    private static final String STDINOUT = "-";
    
    private final WrapperConfig wrapperConfig;
    
    private final Params params;
    
    private final Params tocParams;
    
    private final List<Page> pages;
    
    private boolean hasToc = false;
    
    private int timeout = 10;
    
    public Pdf() {
        this(new WrapperConfig());
    }
    
    public Pdf(WrapperConfig wrapperConfig) {
        this.wrapperConfig = wrapperConfig;
        this.params = new Params();
        this.tocParams = new Params();
        this.pages = new ArrayList<Page>();
        logger.info("Initialized with {}", wrapperConfig);
    }

    /**
     * Add a page to the pdf
     *
     * @deprecated Use the specific type method to a better semantic
     */
    @Deprecated
    public void addPage(String source, PageType type) {
        this.pages.add(new Page(source, type));
    }

    /**
     * Add a page from an URL to the pdf
     */
    public void addPageFromUrl(String source) {
        this.pages.add(new Page(source, PageType.url));
    }

    /**
     * Add a page from a HTML-based string to the pdf
     */
    public void addPageFromString(String source) {
        this.pages.add(new Page(source, PageType.htmlAsString));
    }

    /**
     * Add a page from a file to the pdf
     */
    public void addPageFromFile(String source) {
        this.pages.add(new Page(source, PageType.file));
    }
    
    public void addToc() {
        this.hasToc = true;
    }
    
    public void addParam(Param param, Param... params) {
        this.params.add(param, params);
    }
    
    public void addTocParam(Param param, Param... params) {
        this.tocParams.add(param, params);
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public File saveAs(String path) throws IOException, InterruptedException {
        File file = new File(path);
        FileUtils.writeByteArrayToFile(file, getPDF());
        logger.info("PDF successfully saved in {}", file.getAbsolutePath());
        return file;
    }
    
    public byte[] getPDF() throws IOException, InterruptedException, PDFExportException {
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        try {
            logger.debug("Generating pdf with: {}", getCommand());
            Process process = Runtime.getRuntime().exec(getCommandAsArray());
            
            Future<byte[]> inputStreamToByteArray = executor.submit(streamToByteArrayTask(process.getInputStream()));
            Future<byte[]> outputStreamToByteArray = executor.submit(streamToByteArrayTask(process.getErrorStream()));
            
            process.waitFor();
            
            if (process.exitValue() != 0) {
                byte[] errorStream = getFuture(outputStreamToByteArray);
                logger.error("Error while generating pdf: {}", new String(errorStream));
                throw new PDFExportException(getCommand(), process.exitValue(), errorStream, getFuture(inputStreamToByteArray));
            } else {
                logger.debug("Wkhtmltopdf output:\n{}", new String(getFuture(outputStreamToByteArray)));
            }
            
            logger.info("PDF successfully generated with: {}", getCommand());
            return getFuture(inputStreamToByteArray);
        } finally {
            logger.debug("Shutting down executor for wkhtmltopdf.");
            executor.shutdownNow();
            cleanTempFiles();
        }
    }
    
    private String[] getCommandAsArray() throws IOException {
        List<String> commandLine = new ArrayList<String>();
        
        if (wrapperConfig.isXvfbEnabled()) {
            commandLine.addAll(wrapperConfig.getXvfbConfig().getCommandLine());
        }
        
        commandLine.add(wrapperConfig.getWkhtmltopdfCommand());
        
        commandLine.addAll(params.getParamsAsStringList());
        
        if (hasToc) {
            commandLine.add("toc");
            commandLine.addAll(tocParams.getParamsAsStringList());
        }
        
        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                
                File temp = File.createTempFile("java-wkhtmltopdf-wrapper" + UUID.randomUUID().toString(), ".html");
                FileUtils.writeStringToFile(temp, page.getSource(), "UTF-8");
                page.setFilePath(temp.getAbsolutePath());
                commandLine.add(temp.getAbsolutePath());
            } else {
                //Add source
                commandLine.add(page.getSource());
            }
        }
        commandLine.add(STDINOUT);
        logger.debug(commandLine.toString());
        return commandLine.toArray(new String[commandLine.size()]);
    }
    
    private Callable<byte[]> streamToByteArrayTask(final InputStream input) {
        return new Callable<byte[]>() {
            public byte[] call() throws Exception {
                return IOUtils.toByteArray(input);
            }
        };
    }
    
    private byte[] getFuture(Future<byte[]> future) {
        try {
            return future.get(this.timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void cleanTempFiles() {
        logger.debug("Cleaning up temporary files...");
        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                logger.debug("Delete temp file at: " + page.getFilePath());
                new File(page.getFilePath()).delete();
            }
        }
    }
    
    public String getCommand() throws IOException {
        return StringUtils.join(getCommandAsArray(), " ");
    }
    
}
