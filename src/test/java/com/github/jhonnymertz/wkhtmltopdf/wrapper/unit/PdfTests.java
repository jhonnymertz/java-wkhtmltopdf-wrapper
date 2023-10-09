package com.github.jhonnymertz.wkhtmltopdf.wrapper.unit;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.FilenameFilterConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.Cover;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.Page;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.TableOfContents;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfTests {

    private WrapperConfig wc;
    private Pdf pdf;

    @BeforeEach
    public void setUp() {
        wc = new WrapperConfig("wkhtmltopdf");
        pdf = new Pdf(wc);
    }

    @AfterEach
    public void cleanUp() {
        pdf.cleanAllTempFiles();
    }

    @Test
    void testParams() throws Exception {
        pdf.addParam(new Param("--enable-javascript"), new Param("--html-header", "file:///example.html"));
        pdf.addPageFromUrl("http://www.google.com");
        assertThat("command params should contain the --enable-javascript and --html-header", pdf.getCommand(), containsString("--enable-javascript --html-header file:///example.html"));
    }

    @Test
    void testUniqueTempFileGenerationDirectory() throws IOException {
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        pdf.getCommand();
        pdf.getCommand();
        final File dir = new File(System.getProperty("java.io.tmpdir"));
        File[] files = dir.listFiles(new FilenameFilterConfig());
        assertEquals(1, files.length);
    }

    @Test
    void testTempDirectoryCleanup() throws IOException {
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        pdf.getCommand();
        final File dir = new File(System.getProperty("java.io.tmpdir"));
        File[] files = dir.listFiles(new FilenameFilterConfig());
        assertEquals(1, files.length);
        pdf.cleanAllTempFiles();
        files = dir.listFiles(new FilenameFilterConfig());
        assertEquals(0, files.length);
    }

    @Test
    void testCustomTempDirectory() throws IOException {
        File f = File.createTempFile("java-wrapper-wkhtmltopdf-test", ".html");
        pdf.setTempDirectory(new File(f.getParent()));
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        assertThat("command params should contain custom temp directory", pdf.getCommand(), containsString(f.getParent()));
    }

    @Test
    void testMissingAssetsProperty() {
        pdf.addPageFromUrl("http://www.google.com");
        pdf.setAllowMissingAssets();
        assertTrue(pdf.getAllowMissingAssets());
        pdf.setSuccessValues(Arrays.asList(0, 1));
        assertTrue(pdf.getAllowMissingAssets());
    }

    @Test
    void testAddPages() throws IOException {
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        pdf.addPageFromFile("test.html");
        assertThat("command params should contain url input", pdf.getCommand(), containsString("http://www.google.com"));
        assertThat("command params should contain file input", pdf.getCommand(), containsString("test.html"));
    }

    @Test
    void testTocParams() throws IOException {
        pdf.addToc();
        pdf.addTocParam(new Param("--test-param"), new Param("--test-param2", "test-value"));
        pdf.addPageFromUrl("http://www.google.com");
        assertThat("command params should contain toc params", pdf.getCommand(), containsString("--test-param2 test-value"));
    }

    @Test
    public void testTocParamsUsingTocObject() throws IOException {
        TableOfContents toc = pdf.addToc();
        toc.addParam(new Param("--test-param"), new Param("--test-param2", "test-value"));
        pdf.addPageFromUrl("http://www.google.com");
        Assert.assertThat("command params should contain toc params", pdf.getCommand(), containsString("--test-param2 test-value"));
    }

    @Test
    void testXvfbCommand() throws Exception {
        wc.setXvfbConfig(new XvfbConfig());
        pdf = new Pdf(wc);
        assertThat("command should contain xvfb-run config", pdf.getCommand(), containsString("xvfb-run"));
    }

    @Test
    public void testTocAlwaysFirstByDefault() throws Exception {
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addToc();
        pdf.addPageFromFile("test.html");
        Assert.assertThat("command params should contain toc before pages", pdf.getCommand(), containsString("wkhtmltopdf toc http://www.google.com test.html -"));
    }

    @Test
    public void testTocCustomLocation() throws Exception {
        wc.setAlwaysPutTocFirst(false);
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addToc();
        pdf.addPageFromFile("test.html");
        Assert.assertThat("command params should contain toc after url page and before file page", pdf.getCommand(), containsString("wkhtmltopdf http://www.google.com toc test.html -"));
    }

    @Test
    public void testPageParams() throws IOException {
        Page page1 = pdf.addPageFromUrl("http://www.google.com");
        page1.addParam(new Param("--exclude-from-outline"));
        Page page2 = pdf.addPageFromFile("test.html");
        page2.addParam( new Param("--zoom", "2"));
        Assert.assertThat("command url page should contain page specific params", pdf.getCommand(), containsString("http://www.google.com --exclude-from-outline"));
        Assert.assertThat("command file page should contain page specific params", pdf.getCommand(), containsString("test.html --zoom"));
    }

    @Test
    public void testCoverParams() throws IOException {
        Cover cover = pdf.addCoverFromFile("cover.html");
        cover.addParam(new Param("--test-param"), new Param("--test-param2", "test-value"));
        pdf.addPageFromUrl("http://www.google.com");
        Assert.assertThat("command params should contain cover params", pdf.getCommand(), containsString("cover cover.html --test-param --test-param2 test-value"));
    }

    @Test
    public void testMulipleObjects() throws IOException {
        wc.setAlwaysPutTocFirst(false);
        pdf.addCoverFromFile("cover.html");
        pdf.addPageFromFile("foreword.html");
        pdf.addToc();
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addPageFromFile("test.html");
        Assert.assertThat("command should match the order objects are added", pdf.getCommand(), containsString("wkhtmltopdf cover cover.html foreword.html toc http://www.google.com test.html -"));
    }
}