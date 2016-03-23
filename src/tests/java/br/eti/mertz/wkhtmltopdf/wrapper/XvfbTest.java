package br.eti.mertz.wkhtmltopdf.wrapper;

import br.eti.mertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import br.eti.mertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import br.eti.mertz.wkhtmltopdf.wrapper.page.PageType;
import br.eti.mertz.wkhtmltopdf.wrapper.params.Param;
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

        XvfbConfig xc = new XvfbConfig();
        xc.addParams(new Param("--auto-servernum"), new Param("--server-num=1"));

        WrapperConfig wc = new WrapperConfig();
        wc.setXvfbConfig(xc);

        Pdf pdf = new Pdf(wc);
        pdf.addPage("http://www.google.com", PageType.url);

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
