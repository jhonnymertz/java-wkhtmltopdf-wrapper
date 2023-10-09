package com.github.jhonnymertz.wkhtmltopdf.wrapper.unit;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

class XvfbConfigTests {

    private WrapperConfig wc;

    @BeforeEach
    public void setUp() {
        wc = new WrapperConfig("wkhtmltopdf");
    }

    @Test
    void testXvfbEnable() throws Exception {
        wc.setXvfbConfig(new XvfbConfig());
        Pdf pdf = new Pdf(wc);
        assertThat("command should contain xvfb-run config", pdf.getCommand(), containsString("xvfb-run"));
    }

    @Test
    void testXvfbParams() throws Exception {
        XvfbConfig xvfbConfig = new XvfbConfig();
        xvfbConfig.addParams(new Param("--test-param"), new Param("--test-param2", "test-value"));
        wc.setXvfbConfig(xvfbConfig);
        Pdf pdf = new Pdf(wc);
        pdf.addPageFromUrl("http://www.google.com");
        assertThat("command params should contain xvfb params", pdf.getCommand(), containsString("xvfb-run --test-param --test-param2 test-value wkhtmltopdf http://www.google.com -"));
    }
}
