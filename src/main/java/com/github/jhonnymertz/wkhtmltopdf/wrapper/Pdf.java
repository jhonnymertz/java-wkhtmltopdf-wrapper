package com.github.jhonnymertz.wkhtmltopdf.wrapper;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions.PDFExportException;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * Timeout to wait while generating a PDF, in seconds
     */
    private int timeout = 10;

    private File tempDirectory;

    private String outputFilename = null;

    private List<Integer> successValues = new ArrayList<Integer>(Arrays.asList(0));

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

    /**
     * Sets the timeout to wait while generating a PDF, in seconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * wkhtmltopdf often returns 1 to indicate some assets can't be found,
     *  this can occur for protocol less links or in other cases. Sometimes you
     *  may want to reject these with an exception which is the default, but in other
     *  cases the PDF is fine for your needs.  Call this method allow return values of 1.
     */
    public void setAllowMissingAssets() {
        if (!successValues.contains(1)) {
            successValues.add(1);
        }
    }

    public boolean getAllowMissingAssets() {
        return successValues.contains(1);
    }

    /**
     * In standard process returns 0 means "ok" and any other value is an error.  However, wkhtmltopdf
     * uses the return value to also return warning information which you may decide to ignore (@see setAllowMissingAssets)
     *
     * @param successValues  The full list of process return values you will accept as a 'success'.
     */
    public void setSuccessValues(List<Integer> successValues) {
        this.successValues = successValues;
    }

    /**
     * Sets the temporary folder to store files during the process
     * Default is provided by #File.createTempFile()
     * @param tempDirectory
     */
    public void setTempDirectory(File tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    /**
     * Executes the wkhtmltopdf into standard out and captures the results.
     * @param path The path to the file where the PDF will be saved.
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public File saveAs(String path) throws IOException, InterruptedException {
        File file = new File(path);
        FileUtils.writeByteArrayToFile(file, getPDF());
        logger.info("PDF successfully saved in {}", file.getAbsolutePath());
        return file;
    }

    /**
     * Executes the wkhtmltopdf saving the results directly to the specified file path.
     * @param path The path to the file where the PDF will be saved.
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public File saveAsDirect(String path)throws IOException, InterruptedException  {
        File file = new File(path);
        outputFilename = file.getAbsolutePath();
        getPDF();
        return file;
    }

    public byte[] getPDF() throws IOException, InterruptedException, PDFExportException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            String command = getCommand();
            logger.debug("Generating pdf with: {}", command);
            Process process = Runtime.getRuntime().exec(getCommandAsArray());

            Future<byte[]> inputStreamToByteArray = executor.submit(streamToByteArrayTask(process.getInputStream()));
            Future<byte[]> outputStreamToByteArray = executor.submit(streamToByteArrayTask(process.getErrorStream()));

            process.waitFor();

            if (!successValues.contains( process.exitValue() )) {
                byte[] errorStream = getFuture(outputStreamToByteArray);
                logger.error("Error while generating pdf: {}", new String(errorStream));
                throw new PDFExportException(command, process.exitValue(), errorStream, getFuture(inputStreamToByteArray));
            } else {
                logger.debug("Wkhtmltopdf output:\n{}", new String(getFuture(outputStreamToByteArray)));
            }

            logger.info("PDF successfully generated with: {}", command);
            return getFuture(inputStreamToByteArray);
        } finally {
            logger.debug("Shutting down executor for wkhtmltopdf.");
            executor.shutdownNow();
            cleanTempFiles();
        }
    }

    protected String[] getCommandAsArray() throws IOException {
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
                //htmlAsString pages are first store into a temp file, then the location is passed as parameter to
                // wkhtmltopdf, this is a workaround to avoid huge commands
                File temp = File.createTempFile("java-wkhtmltopdf-wrapper" + UUID.randomUUID().toString(), ".html", tempDirectory);
                FileUtils.writeStringToFile(temp, page.getSource(), "UTF-8");
                page.setFilePath(temp.getAbsolutePath());
                commandLine.add(temp.getAbsolutePath());
            } else {
                commandLine.add(page.getSource());
            }
        }
        commandLine.add( (null != outputFilename) ? outputFilename : STDINOUT);
        logger.debug("Command generated: {}", commandLine.toString());
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
                try {
                    Path p = Paths.get(page.getFilePath());
                    logger.debug("Delete temp file at: " + page.getFilePath() + " " + Files.deleteIfExists(p));
                } catch (IOException ex) {
                    logger.warn("Couldn't delete temp file " + page.getFilePath());
                }
            }
        }
    }

    /**
     * Gets the final wkhtmltopdf command as string
     * @return the generated command from params
     * @throws IOException
     */
    public String getCommand() throws IOException {
        return StringUtils.join(getCommandAsArray(), " ");
    }

}
