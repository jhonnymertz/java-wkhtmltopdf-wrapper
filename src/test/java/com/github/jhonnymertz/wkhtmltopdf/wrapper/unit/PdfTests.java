package com.github.jhonnymertz.wkhtmltopdf.wrapper.unit;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.FilenameFilterConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.core.StringContains.containsString;

public class PdfTests {

    private WrapperConfig wc;
    private Pdf pdf;

    @Before
    public void setUp() {
        wc = new WrapperConfig("wkhtmltopdf");
        pdf = new Pdf(wc);
    }

    @After
    public void cleanUp() {
        pdf.cleanAllTempFiles();
    }

    @Test
    public void testParams() throws Exception {
        pdf.addParam(new Param("--enable-javascript"), new Param("--html-header", "file:///example.html"));
        pdf.addPageFromUrl("http://www.google.com");
        Assert.assertThat("command params should contain the --enable-javascript and --html-header", pdf.getCommand(), containsString("--enable-javascript --html-header file:///example.html"));
    }

    @Test
    public void testUniqueTempFileGenerationDirectory() throws IOException {
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        pdf.getCommand();
        pdf.getCommand();
        final File dir = new File(System.getProperty("java.io.tmpdir"));
        File[] files = dir.listFiles(new FilenameFilterConfig());
        Assert.assertEquals(1, files.length);
    }

    @Test
    public void testTempDirectoryCleanup() throws IOException {
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        pdf.getCommand();
        final File dir = new File(System.getProperty("java.io.tmpdir"));
        File[] files = dir.listFiles(new FilenameFilterConfig());
        Assert.assertEquals(1, files.length);
        pdf.cleanAllTempFiles();
        files = dir.listFiles(new FilenameFilterConfig());
        Assert.assertEquals(0, files.length);
    }

    @Test
    public void testCustomTempDirectory() throws IOException {
        File f = File.createTempFile("java-wrapper-wkhtmltopdf-test", ".html");
        pdf.setTempDirectory(new File(f.getParent()));
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        Assert.assertThat("command params should contain custom temp directory", pdf.getCommand(), containsString(f.getParent()));
    }

    @Test
    public void testMissingAssetsProperty() {
        pdf.addPageFromUrl("http://www.google.com");
        pdf.setAllowMissingAssets();
        Assert.assertTrue(pdf.getAllowMissingAssets());
        pdf.setSuccessValues(Arrays.asList(0, 1));
        Assert.assertTrue(pdf.getAllowMissingAssets());
    }

    @Test
    public void testAddPages() throws IOException {
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");
        pdf.addPageFromFile("test.html");
        Assert.assertThat("command params should contain url input", pdf.getCommand(), containsString("http://www.google.com"));
        Assert.assertThat("command params should contain file input", pdf.getCommand(), containsString("test.html"));
    }

    @Test
    public void testTocParams() throws IOException {
        pdf.addToc();
        pdf.addTocParam(new Param("--test-param"), new Param("--test-param2", "test-value"));
        pdf.addPageFromUrl("http://www.google.com");
        Assert.assertThat("command params should contain toc params", pdf.getCommand(), containsString("--test-param2 test-value"));
    }

    @Test
    public void testXvfbCommand() throws Exception {
        wc.setXvfbConfig(new XvfbConfig());
        pdf = new Pdf(wc);
        Assert.assertThat("command should contain xvfb-run config", pdf.getCommand(), containsString("xvfb-run"));
    }
}
