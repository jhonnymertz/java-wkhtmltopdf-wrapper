package br.eti.mertz.wkhtmltopdf.wrapper;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class Command {
	
	@NonNull
	private String name;
	
	private List<Param> params;
	
	public void addParams(Param... params){
		for(Param param : params){
			addParam(param);
		}
	}
	
	public void addParam(Param param){
		params.add(param);
	}

}
