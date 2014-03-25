package br.eti.mertz.wkhtmltopdf.wrapper;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Params {

	List<Param> params;

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
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(Param param : params){
			sb.append(param);
		}
		return sb.toString();
	}

}
