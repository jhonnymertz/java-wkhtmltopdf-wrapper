package com.github.jhonnymertz.wkhtmltopdf.wrapper.unit;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.StringContains.containsString;

public class CommandTests {

    @Test
    public void testCommand() throws Exception {
        WrapperConfig wc = new WrapperConfig("wkhtmltopdf");
        Pdf pdf = new Pdf(wc);
        pdf.addToc();
        pdf.addParam(new Param("--enable-javascript"), new Param("--html-header", "file:///example.html"));
        pdf.addPageFromUrl("http://www.google.com");
        Assert.assertThat("command params should contain the --enable-javascript and --html-header", pdf.getCommand(), containsString("--enable-javascript --html-header file:///example.html"));
    }

    @Test
    public void testXvfbCommand() throws Exception {
        WrapperConfig wc = new WrapperConfig("wkhtmltopdf");
        wc.setXvfbConfig(new XvfbConfig());
        Pdf pdf = new Pdf(wc);
        Assert.assertThat("command should contain xvfb-run config", pdf.getCommand(), containsString("xvfb-run"));
    }
}
