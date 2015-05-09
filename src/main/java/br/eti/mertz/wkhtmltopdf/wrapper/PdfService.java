package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.File;
import java.io.IOException;

public interface PdfService {
	
	void addHtmlInput(String page);
	
	void addCover(String cover);
	
	void addToc();
	
	File saveAs(String path) throws IOException, InterruptedException;

}
