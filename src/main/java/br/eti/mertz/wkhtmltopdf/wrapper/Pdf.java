package br.eti.mertz.wkhtmltopdf.wrapper;

import br.eti.mertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import br.eti.mertz.wkhtmltopdf.wrapper.configurations.WrapperConfigBuilder;
import br.eti.mertz.wkhtmltopdf.wrapper.page.Page;
import br.eti.mertz.wkhtmltopdf.wrapper.page.PageType;
import br.eti.mertz.wkhtmltopdf.wrapper.params.Param;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Pdf implements PdfService {

    private static final String STDINOUT = "-";

    private WrapperConfig wrapperConfig;

    private List<Param> params;

    private List<Page> pages;

    private boolean hasToc = false;

    public Pdf(WrapperConfig wrapperConfig) {
        this.wrapperConfig = wrapperConfig;
        this.params = new ArrayList<Param>();
        this.pages = new ArrayList<Page>();
    }

    public Pdf() {
        this(new WrapperConfigBuilder().build());
    }

    public void addPage(String source, PageType type) {
        this.pages.add(new Page(source, type));
    }

    public void addToc() {
        this.hasToc = true;
    }

    public void addParam(Param param) {
        params.add(param);
    }

    public void addParam(Param... params) {
        for (Param param : params) {
            addParam(param);
        }
    }

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

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(getCommandAsArray());

        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                OutputStream stdInStream = process.getOutputStream();
                stdInStream.write(page.getSource().getBytes("UTF-8"));
                stdInStream.close();
            }
        }

        InputStream stdOutStream = process.getInputStream();
        InputStream stdErrStream = process.getErrorStream();
        process.waitFor();

        ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
        ByteArrayOutputStream stdErr = new ByteArrayOutputStream();

        while (stdOutStream.available() > 0) {
            stdOut.write((char) stdOutStream.read());
        }
        stdOutStream.close();
        while (stdErrStream.available() > 0) {
            stdErr.write((char) stdErrStream.read());
        }
        stdErrStream.close();

        if (process.exitValue() != 0) {
            throw new RuntimeException("Process (" + getCommand() + ") exited with status code " + process.exitValue() + ":\n" + new String(stdErr.toByteArray()));
        }

        return stdOut.toByteArray();
    }

    private String[] getCommandAsArray() {
        List<String> commandLine = new ArrayList<String>();
        commandLine.add(wrapperConfig.getWkhtmltopdfCommand());

        if (hasToc)
            commandLine.add("toc");

        for (Param p : params) {
            commandLine.add(p.getKey());

            String value = p.getValue();

            if (value != null) {
                commandLine.add(p.getValue());
            }
        }

        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                commandLine.add(STDINOUT);
            } else {
                commandLine.add(page.getSource());
            }
        }
        commandLine.add(STDINOUT);
        return commandLine.toArray(new String[commandLine.size()]);
    }

    public String getCommand() {
        return StringUtils.join(getCommandAsArray(), " ");
    }

}
