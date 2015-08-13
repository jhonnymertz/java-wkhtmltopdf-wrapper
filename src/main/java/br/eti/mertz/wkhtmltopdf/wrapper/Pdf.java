package br.eti.mertz.wkhtmltopdf.wrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.eti.mertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import br.eti.mertz.wkhtmltopdf.wrapper.configurations.WrapperConfigBuilder;
import br.eti.mertz.wkhtmltopdf.wrapper.params.Param;

public class Pdf implements PdfService {

    private static final String STDOUT = "-";

    private WrapperConfig wrapperConfig;

    private String path;
    private List<Param> params;

	private String htmlInput = null;
    private boolean htmlFromString = false;

	public Pdf(WrapperConfig wrapperConfig, String path, List<Param> params) {
        this.wrapperConfig = wrapperConfig;
        this.params = params;
        this.path = path;
	}

	public Pdf(String path, List<Param> params) {
		this(new WrapperConfigBuilder().build(), path, params);
	}

	public Pdf(String path, Param... params) {
		this(new WrapperConfigBuilder().build(), path, Arrays.asList(params));
	}

	public Pdf(String path) {
		this(new WrapperConfigBuilder().build(), path, new ArrayList<Param>());
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

        if(htmlFromString && !this.params.contains(new Param("-"))) {
            this.addParam(new Param("-"));
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(getCommandAsArray());

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
            throw new RuntimeException("Process (" + getCommand() + ") exited with status code " + process.exitValue() + ":\n"+new String(stdErr.toByteArray()));
        }

        return stdOut.toByteArray();
    }

    private String[] getCommandAsArray(){
        List<String> commandLine = new ArrayList<String>();
        commandLine.add(wrapperConfig.getWkhtmltopdfCommand());
        for(Param p : params) {
            commandLine.add(p.getKey());

            String value = p.getValue();

            if(value != null) {
                commandLine.add(p.getValue());
            }
        }
        commandLine.add(path);
        return commandLine.toArray(new String[commandLine.size()]);
    }

    public String getCommand(){
        return Arrays.toString(getCommandAsArray());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
