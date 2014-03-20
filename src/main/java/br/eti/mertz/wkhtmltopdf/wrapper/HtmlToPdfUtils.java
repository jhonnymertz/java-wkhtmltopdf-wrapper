package br.eti.mertz.wkhtmltopdf.wrapper;

public class HtmlToPdfUtils {
	
	private static String command = "wkhtmltopdf";
	
	public static void execute(Pdf pdf){
		
		pdf.getAttrsAsCommandParams();
		
		Runtime rt = Runtime.getRuntime();
		
		Process proc = rt.exec();
		
	}

}
