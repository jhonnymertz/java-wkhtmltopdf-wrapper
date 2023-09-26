package com.github.jhonnymertz.wkhtmltopdf.wrapper;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.FilenameFilterConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions.PDFExportException;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.BaseObject;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.Cover;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.Page;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.TableOfContents;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.SourceType;
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
import java.util.concurrent.*;

/**
 * Represents a Pdf file and wraps up the wkhtmltopdf processing
 */
public class Pdf {

    private static final Logger logger = LoggerFactory.getLogger(Pdf.class);

    private static final String STDINOUT = "-";

    private final WrapperConfig wrapperConfig;

    private final Params params;

    private final List<BaseObject> objects;

    private TableOfContents lastToc;

    /**
     * Timeout to wait while generating a PDF, in seconds
     */
    private int timeout = 10;

    public File tempDirectory;

    public static String TEMPORARY_FILE_PREFIX = "java-wkhtmltopdf-wrapper";

    private String outputFilename = null;

    private List<Integer> successValues = new ArrayList<Integer>(Arrays.asList(0));

    @Deprecated
    /**
     * Default constructor
     * @deprecated Use the constructor with the WrapperConfig definition
     */
    public Pdf() {
        this(new WrapperConfig());
    }

    public Pdf(WrapperConfig wrapperConfig) {
        this.wrapperConfig = wrapperConfig;
        this.params = new Params();
        this.objects = new ArrayList<BaseObject>();
        logger.info("Initialized with {}", wrapperConfig);
    }

    /**
     * Add a page to the pdf
     *
     * @deprecated Use the specific type method to a better semantic
     */
    @Deprecated
    public Page addPage(String source, SourceType type) {
        Page page = new Page(source, type);
        this.objects.add(page);
        return page;
    }

     /**
     * Add a cover from an URL to the pdf
     */
    public Cover addCoverFromUrl(String source) {
        Cover cover = new Cover(source, SourceType.url);
        this.objects.add(cover);
        return cover;
    }

    /**
     * Add a cover from a HTML-based string to the pdf
     */
    public Cover addCoverFromString(String source) {
        Cover cover = new Cover(source, SourceType.htmlAsString);
        this.objects.add(cover);
        return cover;
    }

    /**
     * Add a cover from a file to the pdf
     */
    public Cover addCoverFromFile(String source) {
        Cover cover = new Cover(source, SourceType.file);
        this.objects.add(cover);
        return cover;
    }

    /**
     * Add a page from an URL to the pdf
     */
    public Page addPageFromUrl(String source) {
        Page page = new Page(source, SourceType.url);
        this.objects.add(page);
        return page;
    }

    /**
     * Add a page from a HTML-based string to the pdf
     */
    public Page addPageFromString(String source) {
        Page page = new Page(source, SourceType.htmlAsString);
        this.objects.add(page);
        return page;
    }

    /**
     * Add a page from a file to the pdf
     */
    public Page addPageFromFile(String source) {
        Page page = new Page(source, SourceType.file);
        this.objects.add(page);
        return page;
    }

    /**
     * Add a toc
     */
    public TableOfContents addToc() {
        TableOfContents toc = new TableOfContents();
        this.objects.add(toc);
        this.lastToc = toc;
        return toc;
    }

    public void addParam(Param param, Param... params) {
        this.params.add(param, params);
    }

    /**
     * Adds a param to the most recently added toc
     */
    public void addTocParam(Param param, Param... params) {
        this.lastToc.addParam(param, params);
    }

    /**
     * Sets the timeout to wait while generating a PDF, in seconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * wkhtmltopdf often returns 1 to indicate some assets can't be found,
     * this can occur for protocol less links or in other cases. Sometimes you
     * may want to reject these with an exception which is the default, but in other
     * cases the PDF is fine for your needs.  Call this method allow return values of 1.
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
     * @param successValues The full list of process return values you will accept as a 'success'.
     */
    public void setSuccessValues(List<Integer> successValues) {
        this.successValues = successValues;
    }

    /**
     * Sets the temporary folder to store files during the process
     * Default is provided by #File.createTempFile()
     *
     * @param tempDirectory
     */
    public void setTempDirectory(File tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    /**
     * Executes the wkhtmltopdf into standard out and captures the results.
     *
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
     *
     * @param path The path to the file where the PDF will be saved.
     * @return the pdf file saved
     * @throws IOException when not able to save the file
     * @throws InterruptedException when the PDF generation process got interrupted
     */
    public File saveAsDirect(String path) throws IOException, InterruptedException {
        File file = new File(path);
        outputFilename = file.getAbsolutePath();
        getPDF();
        return file;
    }

    /**
     * Generates a PDF file as byte array from the wkhtmltopdf output
     *
     * @return the PDF file as a byte array
     * @throws IOException when not able to save the file
     * @throws InterruptedException when the PDF generation process got interrupted
     * @throws PDFExportException when the wkhtmltopdf process fails
     */
    public byte[] getPDF() throws IOException, InterruptedException, PDFExportException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            String command = getCommand();
            logger.debug("Generating pdf with: {}", command);
            Process process = Runtime.getRuntime().exec(getCommandAsArray());

            Future<byte[]> inputStreamToByteArray = executor.submit(streamToByteArrayTask(process.getInputStream()));
            Future<byte[]> outputStreamToByteArray = executor.submit(streamToByteArrayTask(process.getErrorStream()));

            process.waitFor();

            if (!successValues.contains(process.exitValue())) {
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

    /**
     * Get command as array string
     *
     * Note: htmlAsString pages are first store into a temp file, then the location is used in the wkhtmltopdf command
     * this is a workaround to avoid huge commands
     *
     * @return the wkhtmltopdf command as string array
     * @throws IOException when not able to save temporary files from htmlAsString
     */
    protected String[] getCommandAsArray() throws IOException {
        List<String> commandLine = new ArrayList<String>();

        if (wrapperConfig.isXvfbEnabled()) {
            commandLine.addAll(wrapperConfig.getXvfbConfig().getCommandLine());
        }

        commandLine.addAll(Arrays.asList(wrapperConfig.getWkhtmltopdfCommandAsArray()));

        // Global options
        commandLine.addAll(params.getParamsAsStringList());

        // Object and object options
        for (BaseObject object : objects) {
            commandLine.addAll(object.getCommandAsList(this));
        }

        commandLine.add((null != outputFilename) ? outputFilename : STDINOUT);
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

    /**
     * CLeans up temporary files used to generate the wkhtmltopdf command
     */
    private void cleanTempFiles() {
        logger.debug("Cleaning up temporary files...");
        for (BaseObject object : objects) {
            if(object instanceof Page) {
                Page page = (Page) object;
                if (page.getType().equals(SourceType.htmlAsString)) {
                    try {
                        Path p = Paths.get(page.getFilePath());
                        logger.debug("Delete temp file at: " + page.getFilePath() + " " + Files.deleteIfExists(p));
                    } catch (IOException ex) {
                        logger.warn("Couldn't delete temp file " + page.getFilePath());
                    }
                }
            }
        }
    }

    /**
     * Cleans up all the files generated by the library.
     */
    public void cleanAllTempFiles() {
        logger.debug("Cleaning up temporary files...");
        final File folder = new File(System.getProperty("java.io.tmpdir"));
        final File[] files = folder.listFiles(new FilenameFilterConfig());
        for (final File file : files) {
            if (!file.delete()) {
                logger.warn("Couldn't delete temp file " + file.getAbsolutePath());
            }
        }
        logger.debug("{} temporary files removed.", files.length);
    }

    /**
     * Gets the final wkhtmltopdf command as string
     *
     * @return the generated command from params
     * @throws IOException when not able to save temporary files from htmlAsString
     */
    public String getCommand() throws IOException {
        return StringUtils.join(getCommandAsArray(), " ");
    }

}
