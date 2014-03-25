package br.eti.mertz.wkhtmltopdf.wrapper;


public class Command {
	
	public static void main(String[] args) {
		Pdf pdf = new Pdf();
		
		pdf.getParams().add(new Param("enable-javascript"), new Param("html-header", "file:///lala.html"));
		
		System.out.println(pdf);
	}

}
