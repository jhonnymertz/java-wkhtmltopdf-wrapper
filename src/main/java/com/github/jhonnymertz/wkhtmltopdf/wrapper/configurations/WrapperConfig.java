package com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WrapperConfig {

    private XvfbConfig xvfbConfig;

    private String wkhtmltopdfCommand = "wkhtmltopdf";

    public WrapperConfig(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

    public WrapperConfig() {
        this.wkhtmltopdfCommand = findExecutable();
    }

    public String getWkhtmltopdfCommand() {
        return wkhtmltopdfCommand;
    }

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

            String cmd;
            if (osname.contains("windows"))
                cmd = "where wkhtmltopdf";
            else cmd = "which wkhtmltopdf";

            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.toString().isEmpty())
                throw new RuntimeException();

            setWkhtmltopdfCommand(sb.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return getWkhtmltopdfCommand();
    }

    public boolean isXvfbEnabled() {
        return xvfbConfig != null;
    }

    public XvfbConfig getXvfbConfig() {
        return xvfbConfig;
    }

    public void setXvfbConfig(XvfbConfig xvfbConfig) {
        this.xvfbConfig = xvfbConfig;
    }
}
