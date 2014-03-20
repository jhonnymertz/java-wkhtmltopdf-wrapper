package br.eti.mertz.wkhtmltopdf.wrapper;

import lombok.Data;
import lombok.NonNull;

@Data
public class Param {
	
	@NonNull
	private Object key;
	
	private Object value;

}
