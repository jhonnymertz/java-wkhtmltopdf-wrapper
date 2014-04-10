package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.eti.mertz.wkhtmltopdf.wrapper.options.GlobalOption;
import lombok.Data;

@Data
public class Pdf implements PdfService {

	private String wkhtmlpdf;
	private List<Param> params;

	public Pdf(String wkhtmltopdf, List<Param> params) {
		this.wkhtmlpdf = wkhtmltopdf;
		this.params = params;
	}

	public Pdf(List<Param> params) {
		this("wkhtmltopdf", params);
	}

	public Pdf(Param... params) {
		this("wkhtmltopdf", Arrays.asList(params));
	}

	public Pdf() {
		this("wkhtmltopdf", new ArrayList<Param>());
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

	public void addParam(Param param) {
		params.add(param);
	}

	public void addParam(Param... params) {
		for (Param param : params) {
			addParam(param);
		}
	}

	/**
	 * TODO save file and returns
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public File saveAs(String path) throws IOException, InterruptedException {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(this.toString() + path);

		proc.waitFor();

		return new File(path);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Param param : params) {
			sb.append(param);
		}

		return new StringBuilder(wkhtmlpdf).append(sb.toString()).toString();
	}

	public void addParam(GlobalOption option) {
		addParam(new Param(option.toString()));
	}

}
