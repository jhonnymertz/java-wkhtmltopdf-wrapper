package com.github.jhonnymertz.wkhtmltopdf.wrapper.integration;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class PdfIntegrationTests {

    @Test
    void findExecutable() {
        //see if executable is installed
        try {
            WrapperConfig.findExecutable();
        } catch (RuntimeException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    void getDefaultWkhtmltopdfCommandAsArray() {
        String installedCommand = WrapperConfig.findExecutable();

        WrapperConfig wc = new WrapperConfig();
        String[] result = wc.getWkhtmltopdfCommandAsArray();

        assertArrayEquals(installedCommand.split(" "), result);
    }

    @Test
    void getCustomWkhtmltopdfCommandAsArray() {
        WrapperConfig wc = new WrapperConfig("custom wkhtmltopdf command");

        String[] result = wc.getWkhtmltopdfCommandAsArray();

        assertEquals("custom", result[0]);
        assertEquals("wkhtmltopdf", result[1]);
        assertEquals("command", result[2]);
    }

    @Test
    void testPdfFromStringTo() throws Exception {

        // GIVEN a html template containing special characters that java stores in utf-16 internally
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();

        String pdfText = getPdfTextFromBytes(pdfBytes);

        assertThat("document should contain the creditorName", pdfText, containsString("Müller"));
    }

    @Test
    void testMultiplePages() throws Exception {
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 1</h1></html>");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 2</h1></html>");
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 4</h1></html>");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();

        String pdfText = getPdfTextFromBytes(pdfBytes);

        assertThat("document should contain the fourth page name", pdfText, containsString("Page 4"));
    }

    @Test
    void callingGetCommandFollowedByGetPdfShouldNotInfluenceTheOutput() throws Exception {
        Pdf pdf = new Pdf();

        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Twice</h1></html>");

        // WHEN
        pdf.getCommand();
        //Followed by
        byte[] pdfBytes = pdf.getPDF();//Causes the page fromString's content to have become the file path

        String pdfText = getPdfTextFromBytes(pdfBytes);

        assertThat("document should contain the string that was originally inserted", pdfText, containsString("Twice"));
    }

    @Test
    void testRemovingGeneratedFile() throws Exception {
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 1</h1></html>");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 2</h1></html>");
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 4</h1></html>");

        File savedPdf = pdf.saveAs("output.pdf");
        assertTrue(savedPdf.delete());
    }

    private String getPdfTextFromBytes(byte[] pdfBytes) throws IOException {
        PDDocument pdDocument = Loader.loadPDF(pdfBytes);
        String text = new PDFTextStripper().getText(pdDocument);

        pdDocument.close();
        return text;
    }

    @Test
    void CleanUpTempFilesTest() {
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<!DOCTYPE html><head><title>title</title></head><body><p>TEST</p></body>");
        try {
            pdf.getPDF();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    void testPdfWithXvfb() throws Exception {
        WrapperConfig wc = null;
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            XvfbConfig xc = new XvfbConfig();
            xc.addParams(new Param("--auto-servernum"), new Param("--server-num=1"));

            wc = new WrapperConfig();
            wc.setXvfbConfig(xc);
        }
        Pdf pdf = wc != null ? new Pdf(wc) : new Pdf();
        pdf.addPageFromUrl("http://www.google.com");

        pdf.saveAs("output.pdf");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();
        PDDocument pdDocument = Loader.loadPDF(pdfBytes);
        String pdfText = new PDFTextStripper().getText(pdDocument);

        assertThat("document should be generated", pdfText, containsString("Google"));
    }

    @Test
    void testPdfWithLongParameters() throws Exception {
        final String executable = WrapperConfig.findExecutable();
        Pdf pdf = new Pdf(new WrapperConfig(executable));
        pdf.addPageFromUrl("http://www.google.com");

        pdf.addParam(new Param("--javascript-delay", "2000"));

        pdf.saveAs("output.pdf");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();
        PDDocument pdDocument = Loader.loadPDF(pdfBytes);
        String pdfText = new PDFTextStripper().getText(pdDocument);

        assertThat("document should be generated", pdfText, containsString("Google"));
    }
}
