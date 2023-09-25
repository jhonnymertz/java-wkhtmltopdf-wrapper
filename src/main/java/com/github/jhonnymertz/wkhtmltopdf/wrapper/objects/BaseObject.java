package com.github.jhonnymertz.wkhtmltopdf.wrapper.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Params;

public abstract class BaseObject {
	String objectIdentifier;

	Params params;

	BaseObject()
	{
		objectIdentifier = SetObjectIdentifier();
		params = new Params();
	}

	public abstract String SetObjectIdentifier();

	public void addParam( Param param, Param... params )
	{
		this.params.add( param, params );
	}

	public List<String> getCommandAsList(Pdf pdf) throws IOException
	{
		List<String> commands = new ArrayList<>();
		commands.add( objectIdentifier );
		commands.addAll( this.params.getParamsAsStringList() );
		return commands;
	}
}
