package br.eti.mertz.wkhtmltopdf.wrapper.configurations;

public class WrapperConfig {
    private String wkhtmltopdfCommand = "wkhtmltopdf";

    public WrapperConfig(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

    public String getWkhtmltopdfCommand() {
        return wkhtmltopdfCommand;
    }

    public void setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }
}
