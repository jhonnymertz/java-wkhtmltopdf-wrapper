package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.eti.mertz.wkhtmltopdf.wrapper.options.GlobalOption;
import lombok.Data;

@Data
public class Pdf implements PdfService {

    static final String STDOUT = "-";

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
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public File saveAs(String path) throws IOException, InterruptedException {
        File file = new File(path);
        getPDF(path);
        return file;
	}

    public File saveAs(String path, byte[] document) throws IOException {
        File file = new File(path);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(document);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        return file;
    }

    public byte[] getPDF(String path) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        if(htmlFromString && !this.params.contains(new Param("-"))) {
            this.addParam(new Param("-"));
        }
        String command = this.commandWithParameters() + Symbol.separator + path;
        Process process = runtime.exec(command);
        if(htmlFromString) {
            OutputStream stdInStream = process.getOutputStream();
            stdInStream.write(htmlInput.getBytes());
            stdInStream.close();
        }
        InputStream stdOutStream = process.getInputStream();
        InputStream stdErrStream = process.getErrorStream();
        process.waitFor();

        ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdErr = new ByteArrayOutputStream();

        while(stdOutStream.available()>0) {
            stdOut.write((char) stdOutStream.read());
        }
        stdOutStream.close();
        while(stdErrStream.available()>0) {
            stdErr.write((char) stdErrStream.read());
        }
        stdErrStream.close();

        if(process.exitValue() != 0) {
            throw new RuntimeException("Process (" + command + ") exited with status code " + process.exitValue() + ":\n"+new String(stdErr.toByteArray()));
        }

        return stdOut.toByteArray();
    }

    public byte[] getPDF() throws IOException, InterruptedException {
        return getPDF(STDOUT);
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
