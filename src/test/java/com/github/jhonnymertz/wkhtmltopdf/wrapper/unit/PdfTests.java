package com.github.jhonnymertz.wkhtmltopdf.wrapper.unit;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.FilenameFilterConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
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
    void testXvfbCommand() throws Exception {
        wc.setXvfbConfig(new XvfbConfig());
        pdf = new Pdf(wc);
        assertThat("command should contain xvfb-run config", pdf.getCommand(), containsString("xvfb-run"));
    }
}
