package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.File;

import lombok.experimental.Builder;

@Builder
public class Pdf implements PdfService{
	
	private Boolean enableJavascript = true;
	private Boolean debugJavascript = false;
	private Long javascriptDelay = 0L;
	private String headerHtml;
	private String footerHtml;
	private String url;
	private String path;
	private Param cookie;
	
	public String getAttrsAsCommandParams(){
		return "";
	}

	/**
	 * TODO
	 * Add a HTML file, a HTML string or a page from a URL
	 */
	public void addPage(String page) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * TODO
	 * Add a HTML file, a HTML string or a page from a URL
	 */
	public void addCover(String cover) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * TODO
	 * just the TOC option from wkhtmltopdf
	 */
	public void addToc() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * TODO
	 * save file and returns
	 */
	public File saveAs(String path) {
		// TODO Auto-generated method stub
		return null;
	}

}
