package br.eti.mertz.wkhtmltopdf.wrapper;

import br.eti.mertz.wkhtmltopdf.wrapper.page.PageType;
import br.eti.mertz.wkhtmltopdf.wrapper.params.Param;

import java.io.IOException;

public interface PdfService {

    void addPage(String page, PageType type);

    void addToc();

    void addParam(Param param);

    void addParam(Param... params);

    void saveAs(String path) throws IOException, InterruptedException;

    byte[] getPDF() throws IOException, InterruptedException;
}
