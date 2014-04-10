package br.eti.mertz.wkhtmltopdf.wrapper.builder;

import br.eti.mertz.wkhtmltopdf.wrapper.Pdf;
import br.eti.mertz.wkhtmltopdf.wrapper.options.GlobalOption;

public class CommandBuilder {

	private Pdf pdf;

	public CommandBuilder() {
		pdf = new Pdf();
	}

	public CommandBuilder collate() {
		pdf.addParam(GlobalOption.COLLATE);
		return this;
	}

	public Pdf toPdf() {
		return pdf;
	}

}
