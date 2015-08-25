package br.eti.mertz.wkhtmltopdf.wrapper.configurations;

public class WrapperConfigBuilder {
    private String wkhtmltopdfCommand = "wkhtmltopdf";

    public WrapperConfigBuilder setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
        return this;
    }

    public WrapperConfig build() {
        return new WrapperConfig(wkhtmltopdfCommand);
    }
}