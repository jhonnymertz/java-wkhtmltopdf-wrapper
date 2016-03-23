package br.eti.mertz.wkhtmltopdf.wrapper.configurations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WrapperConfig {

    public WrapperConfig(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

    public WrapperConfig(){
        this.wkhtmltopdfCommand = findExecutable();
    }

    public String getWkhtmltopdfCommand() {
        return wkhtmltopdfCommand;
    }

    public void setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

    private boolean xvfbEnabled = false;
    private String xvfbrunCommand = "xvfb-run";

    public boolean isXvfbEnabled() {
        return xvfbEnabled;
    }

    public void setXvfbEnabled(boolean xvfbEnabled) {
        this.xvfbEnabled = xvfbEnabled;
    }

    public String getXvfbrunCommand() {
        return xvfbrunCommand;
    }

    public void setXvfbrunCommand(String xvfbrunCommand) {
        this.xvfbrunCommand = xvfbrunCommand;
    }

    /**
     * Attempts to find the `wkhtmltopdf` executable in the system path.
     *
     * @return
     */
    private String wkhtmltopdfCommand = "wkhtmltopdf";

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
            String line = "";
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
}
