package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.eti.mertz.wkhtmltopdf.wrapper.options.GlobalOption;
import lombok.Data;

@Data
public class Pdf implements PdfService {

	private String command;
	private List<Param> params;
	private String htmlInput = null;
    private boolean htmlFromString = false;

	public Pdf(String wkhtmltopdf, List<Param> params) {
		this.command = wkhtmltopdf;
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

	public void addHtmlInput(String input) {
        this.htmlFromString = true;
        this.htmlInput = input;
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
        String command = this.commandWithParameters() + Symbol.separator + path;
        Process proc = rt.exec(command);
        if(htmlFromString) {
            OutputStream stdin = proc.getOutputStream();
            stdin.write(htmlInput.getBytes());
            stdin.close();
        }

		proc.waitFor();
        if(proc.exitValue() != 0) {
            throw new RuntimeException("Process (" + command + ") exited with status code " + proc.exitValue());
        }

		return new File(path);
	}

	public String commandWithParameters() {
		StringBuilder sb = new StringBuilder();
		for (Param param : params) {
			sb.append(param);
		}

		return command + sb.toString();
	}

	public void addParam(GlobalOption option) {
		addParam(new Param(option.toString()));
	}

}
