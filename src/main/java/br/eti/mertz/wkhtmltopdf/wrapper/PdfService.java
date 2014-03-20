package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.File;

public interface PdfService {
	
	public void addPage(String page);
	
	public void addCover(String cover);
	
	public void addToc();
	
	public File saveAs(String path);

}
