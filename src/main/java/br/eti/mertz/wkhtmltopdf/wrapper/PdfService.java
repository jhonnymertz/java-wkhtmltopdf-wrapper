package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.File;
import java.io.IOException;

public interface PdfService {
	
	public void addPage(String page);
	
	public void addCover(String cover);
	
	public void addToc();
	
	public File saveAs(String path) throws IOException, InterruptedException;

}
