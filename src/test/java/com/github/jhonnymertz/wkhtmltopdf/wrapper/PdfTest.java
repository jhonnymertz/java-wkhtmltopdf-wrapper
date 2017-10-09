package com.github.jhonnymertz.wkhtmltopdf.wrapper;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

import static org.hamcrest.core.StringContains.containsString;

public class PdfTest {

    @Test
    public void testCommand() throws Exception {
        Pdf pdf = new Pdf();
        pdf.addToc();
        pdf.addParam(new Param("--enable-javascript"), new Param("--html-header", "file:///example.html"));
        pdf.addPageFromUrl("http://www.google.com");
        Assert.assertThat("command params should contain the --enable-javascript and --html-header", pdf.getCommand(), containsString("--enable-javascript --html-header file:///example.html"));
    }

    @Test
    public void findExecutable() throws Exception {
        WrapperConfig wc = new WrapperConfig();
        Assert.assertThat("executable should be /usr/bin/wkhtmltopdf", wc.findExecutable(), containsString("/usr/bin/wkhtmltopdf"));
    }

    @Test
    public void testPdfFromStringTo() throws Exception {

        // GIVEN a html template containing special characters that java stores in utf-16 internally
        Pdf pdf = new Pdf();
        pdf.addPageFromString("<html><head><meta charset=\"utf-8\"></head><h1>Müller</h1></html>");

        // WHEN
        byte[] pdfBytes = pdf.getPDF();

        PDFParser parser = new PDFParser(new ByteArrayInputStream(pdfBytes));

        // that is a valid PDF (otherwise an IOException occurs)
        parser.parse();
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String pdfText = pdfTextStripper.getText(new PDDocument(parser.getDocument()));

        Assert.assertThat("document should contain the creditorName", pdfText, containsString("Müller"));
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

        PDFParser parser = new PDFParser(new ByteArrayInputStream(pdfBytes));

        // that is a valid PDF (otherwise an IOException occurs)
        parser.parse();
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String pdfText = pdfTextStripper.getText(new PDDocument(parser.getDocument()));

        Assert.assertThat("document should contain the fourth page name", pdfText, containsString("Page 4"));
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
}
