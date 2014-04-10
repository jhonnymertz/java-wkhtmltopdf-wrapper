package br.eti.mertz.wkhtmltopdf.wrapper.options;

public enum GlobalOption {

	COLLATE("collate");
	
	//outros

	private String name;

	private GlobalOption(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
