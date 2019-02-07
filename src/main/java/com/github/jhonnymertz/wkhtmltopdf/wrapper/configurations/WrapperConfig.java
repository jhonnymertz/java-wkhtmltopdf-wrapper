package com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations;

import java.io.IOException;
import java.nio.charset.Charset;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.exceptions.WkhtmltopdfConfigurationException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides wrapper configuration such as the xvfb configuration and wkhtmltopdf command to be used
 */
public class WrapperConfig {

    private static final Logger logger = LoggerFactory.getLogger(WrapperConfig.class);

    /**
     * the Xvfb configuration
     */
    private XvfbConfig xvfbConfig;

    /**
     * Default wkhtmltopdf command to be used
     */
    private String wkhtmltopdfCommand = "wkhtmltopdf";

    /**
     * Initialize the configuration based on searching for wkhtmltopdf command to be used into the SO's path
     */
    public WrapperConfig() {
        logger.debug("Initialized with default configurations.");
        setWkhtmltopdfCommand(findExecutable());
    }

    /**
     * Initialize the configuration based on a provided wkhtmltopdf command to be used
     * @param wkhtmltopdfCommand the wkhtmltopdf command
     */
    public WrapperConfig(String wkhtmltopdfCommand) {
        setWkhtmltopdfCommand(wkhtmltopdfCommand);
    }

    /**
     * Gets the wkhtmltopdf command to be used while calling wkhtmltopdf
     * It's default is 'wkhtmltopdf'
     * @return the wkhtmltopdf command
     */
    public String getWkhtmltopdfCommand() {
        return wkhtmltopdfCommand;
    }

    /**
     * Sets the configuration based on a provided wkhtmltopdf command to be used
     * @param wkhtmltopdfCommand the wkhtmltopdf command
     */
    public void setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

    /**
     * Attempts to find the `wkhtmltopdf` executable in the system path.
     *
     * @return the wkhtmltopdf command according to the OS
     */
    public String findExecutable() {
        try {
            String osname = System.getProperty("os.name").toLowerCase();

            String cmd = osname.contains("windows") ? "where.exe wkhtmltopdf" : "which wkhtmltopdf";

            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            String text = IOUtils.toString(p.getInputStream(), Charset.defaultCharset()).trim();

            if (text.isEmpty())
                throw new WkhtmltopdfConfigurationException("wkhtmltopdf command was not found in your classpath. " +
                        "Verify its installation or initialize wrapper configurations with correct path/to/wkhtmltopdf");

            logger.debug("Wkhtmltopdf command found in classpath: {}", text);
            setWkhtmltopdfCommand(text);
        } catch (InterruptedException e) {
            logger.error("Fatal:",e);
        } catch (IOException e) {
            logger.error("Fatal:",e);
        }

        return getWkhtmltopdfCommand();
    }

    /**
     * Verify whether the Xvfb support is enabled and configured
     * @return status of Xvfb configuration
     */
    public boolean isXvfbEnabled() {
        return xvfbConfig != null;
    }

    /**
     * Gets the Xvfb configuration
     * @return the Xvfb configuration
     */
    public XvfbConfig getXvfbConfig() {
        return xvfbConfig;
    }

    /**
     * Sets the Xvfb configuration
     * @param xvfbConfig the Xvfb configuration
     */
    public void setXvfbConfig(XvfbConfig xvfbConfig) {
        this.xvfbConfig = xvfbConfig;
    }

    @Override
    public String toString() {
        return "{" +
                "xvfbConfig=" + xvfbConfig +
                ", wkhtmltopdfCommand='" + wkhtmltopdfCommand + '\'' +
                '}';
    }
}
