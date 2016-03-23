package br.eti.mertz.wkhtmltopdf.wrapper.params;

import java.util.ArrayList;
import java.util.List;

public class Params {

    private List<Param> params;

    public Params() {
        this.params = new ArrayList<Param>();
    }

    public void add(Param param) {
        params.add(param);
    }

    public void add(Param... params) {
        for (Param param : params) {
            add(param);
        }
    }

    public List<String> getParamsAsStringList() {
        List<String> commandLine = new ArrayList<String>();

        for (Param p : params) {
            commandLine.add(p.getKey());

            String value = p.getValue();

            if (value != null) {
                commandLine.add(p.getValue());
            }
        }

        return commandLine;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Param param : params) {
            sb.append(param);
        }
        return sb.toString();
    }

}
