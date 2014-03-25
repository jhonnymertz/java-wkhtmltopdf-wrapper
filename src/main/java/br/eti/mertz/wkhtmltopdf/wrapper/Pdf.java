package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.File;
import java.io.IOException;

import lombok.Data;

@Data
public class Pdf implements PdfService {

	private String wkhtmlpdf;
	private Params params;

	public Pdf(String wkhtmltopdf, Params params) {
		this.wkhtmlpdf = wkhtmltopdf;
		this.params = params;
	}

	public Pdf(Params params) {
		this("wkhtmltopdf", params);
	}

	public Pdf() {
		this("wkhtmltopdf", new Params());
	}

	/**
	 * TODO Add a HTML file, a HTML string or a page from a URL
	 */
	public void addPage(String page) {
		// TODO Auto-generated method stub

	}

	/**
	 * TODO Add a HTML file, a HTML string or a page from a URL
	 */
	public void addCover(String cover) {
		// TODO Auto-generated method stub

	}

	/**
	 * TODO just the TOC option from wkhtmltopdf
	 */
	public void addToc() {
		// TODO Auto-generated method stub

	}

	/**
	 * TODO save file and returns
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public File saveAs(String path) throws IOException, InterruptedException {

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(this.toString());
		
		proc.waitFor();

		return new File(path);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(wkhtmlpdf).append(params);
		return sb.toString();
	}

}
