package com.github.jhonnymertz.wkhtmltopdf.wrapper.integration;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.Cover;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.Page;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.TableOfContents;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.StringContains.containsString;

public class PdfIntegrationTests {

    @Test
    public void findExecutable() {
        //see if executable is installed
        try {
            WrapperConfig.findExecutable();
        } catch (RuntimeException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void getDefaultWkhtmltopdfCommandAsArray() {
        String installedCommand = WrapperConfig.findExecutable();

        WrapperConfig wc = new WrapperConfig();
        String[] result = wc.getWkhtmltopdfCommandAsArray();

        Assert.assertArrayEquals(installedCommand.split(" "), result);
    }

    @Test
    public void getCustomWkhtmltopdfCommandAsArray() {
        WrapperConfig wc = new WrapperConfig("custom wkhtmltopdf command");

        String[] result = wc.getWkhtmltopdfCommandAsArray();

        Assert.assertEquals("custom", result[0]);
        Assert.assertEquals("wkhtmltopdf", result[1]);
        Assert.assertEquals("command", result[2]);
    }

    @Test
    public void testPdfFromStringTo() throws Exception {

        // GIVEN a html template containing special characters that java stores in utf-16 internally
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();

        String pdfText = getPdfTextFromBytes(pdfBytes);

        Assert.assertThat("document should contain the creditorName", pdfText, containsString("Müller"));
    }

    @Test
    public void testMultipleObjectsWithOptions() throws Exception {
        final String executable = WrapperConfig.findExecutable();
        WrapperConfig config = new WrapperConfig(executable);
        Pdf pdf = new Pdf(config);
        pdf.addParam( new Param( "--footer-font-size", "10" ) );
        pdf.addParam( new Param( "--margin-bottom", "20" ) );
        Cover coverPage = pdf.addCoverFromString("<html><head><meta charset=\"utf-8\"></head><h1>Cover Page</h1></html>");
        TableOfContents toc = pdf.addToc();
        toc.addParam(new Param("--disable-dotted-lines"));
        Page mainPage = pdf.addPageFromString("<html><head><meta charset=\"utf-8\"><style>h2 { page-break-before: always; }</style></head><h2>Heading 1</h2>Blah blah blah<h2>Heading 2</h2>This is some test text</html>");
        mainPage.addParam(new Param("--footer-center", "Page [page] of [topage]" ));
        try {
            pdf.getPDF();
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testMultiplePages() throws Exception {
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 1</h1></html>");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 2</h1></html>");
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 4</h1></html>");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();

        String pdfText = getPdfTextFromBytes(pdfBytes);

        Assert.assertThat("document should contain the fourth page name", pdfText, containsString("Page 4"));
    }

    @Test
    public void callingGetCommandFollowedByGetPdfShouldNotInfluenceTheOutput() throws Exception {
        Pdf pdf = new Pdf();

        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Twice</h1></html>");

        // WHEN
        pdf.getCommand();
        //Followed by
        byte[] pdfBytes = pdf.getPDF();//Causes the page fromString's content to have become the file path

        String pdfText = getPdfTextFromBytes(pdfBytes);

        Assert.assertThat("document should contain the string that was originally inserted", pdfText, containsString("Twice"));
    }

    @Test
    public void testRemovingGeneratedFile() throws Exception {
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 1</h1></html>");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 2</h1></html>");
        pdf.addPageFromUrl("http://www.google.com");
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Page 4</h1></html>");

        File savedPdf = pdf.saveAs("output.pdf");
        Assert.assertTrue(savedPdf.delete());
    }

    private String getPdfTextFromBytes(byte[] pdfBytes) throws IOException {
        PDDocument pdDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        String text = new PDFTextStripper().getText(pdDocument);

        pdDocument.close();
        return text;
    }

    @Test
    public void CleanUpTempFilesTest() {
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<!DOCTYPE html><head><title>title</title></head><body><p>TEST</p></body>");
        try {
            pdf.getPDF();
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testPdfWithXvfb() throws Exception {
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
        PDDocument pdDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        String pdfText = new PDFTextStripper().getText(pdDocument);

        Assert.assertThat("document should be generated", pdfText, containsString("Google"));
    }

    @Test
    public void testPdfWithLongParameters() throws Exception {
        final String executable = WrapperConfig.findExecutable();
        Pdf pdf = new Pdf(new WrapperConfig(executable));
        pdf.addPageFromUrl("http://www.google.com");

        pdf.addParam(new Param("--javascript-delay", "2000"));

        pdf.saveAs("output.pdf");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();
        PDDocument pdDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        String pdfText = new PDFTextStripper().getText(pdDocument);

        Assert.assertThat("document should be generated", pdfText, containsString("Google"));
    }
}
