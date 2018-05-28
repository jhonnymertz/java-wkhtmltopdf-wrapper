package com.github.jhonnymertz.wkhtmltopdf.wrapper;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.page.PageType;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.hamcrest.core.StringContains.containsString;

public class XvfbTest {

    @Test
    public void testXvfbCommand() throws Exception {
        WrapperConfig wc = new WrapperConfig();
        wc.setXvfbConfig(new XvfbConfig());
        Pdf pdf = new Pdf(wc);
        Assert.assertThat("command should contain xvfb-run config", pdf.getCommand(), containsString("xvfb-run"));
    }

    @Test
    public void testPdfWithXvfb() throws Exception {
        //Dunno i don't need this test, so for
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

        PDFParser parser = new PDFParser(new ByteArrayInputStream(pdfBytes));

        // that is a valid PDF (otherwise an IOException occurs)
        parser.parse();
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String pdfText = pdfTextStripper.getText(new PDDocument(parser.getDocument()));

        Assert.assertThat("document should be generated", pdfText, containsString("Google"));
    }

}
