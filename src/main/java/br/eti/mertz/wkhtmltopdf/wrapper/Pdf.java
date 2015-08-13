package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.eti.mertz.wkhtmltopdf.wrapper.params.Param;
import br.eti.mertz.wkhtmltopdf.wrapper.params.Symbol;

public class Pdf implements PdfService {

    private static final String STDOUT = "-";

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
	public void saveAs(String path) throws IOException, InterruptedException {
        saveAs(path, getPDF());
	}

    private File saveAs(String path, byte[] document) throws IOException {
        File file = new File(path);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(document);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        return file;
    }

    public byte[] getPDF() throws IOException, InterruptedException {
        return getPDF(STDOUT);
    }

    private byte[] getPDF(String path) throws IOException, InterruptedException {

        if(htmlFromString && !this.params.contains(new Param("-"))) {
            this.addParam(new Param("-"));
        }
        String command = this.commandParameters() + Symbol.separator + path;

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);

        if(htmlFromString) {
            OutputStream stdInStream = process.getOutputStream();
            stdInStream.write(htmlInput.getBytes("UTF-8"));
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

	public String commandParameters() {
		StringBuilder sb = new StringBuilder();
		for (Param param : params) {
			sb.append(param);
		}

		return command + sb.toString();
	}

}
