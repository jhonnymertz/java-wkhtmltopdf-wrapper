package com.github.jhonnymertz.wkhtmltopdf.wrapper.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Params;

/**
 * The type Base object.
 */
public abstract class BaseObject {
	/**
	 * The Object identifier.
	 */
	String objectIdentifier;

	/**
	 * The Params.
	 */
	Params params;

	/**
	 * Instantiates a new Base object.
	 */
	BaseObject()
	{
		objectIdentifier = SetObjectIdentifier();
		params = new Params();
	}

	/**
	 * Set object identifier string.
	 *
	 * @return the string
	 */
	public abstract String SetObjectIdentifier();

	/**
	 * Gets object identifier.
	 *
	 * @return the object identifier
	 */
	public String getObjectIdentifier() {
		return this.objectIdentifier;
	}

	/**
	 * Add param.
	 *
	 * @param param  the param
	 * @param params the params
	 */
	public void addParam(final Param param, final Param... params )
	{
		this.params.add( param, params );
	}

	/**
	 * Gets command as list.
	 *
	 * @param pdf the pdf
	 * @return the command as list
	 * @throws IOException the io exception
	 */
	public List<String> getCommandAsList(final Pdf pdf) throws IOException
	{
		List<String> commands = new ArrayList<>();
		if(StringUtils.isNotBlank(objectIdentifier)){
			commands.add(objectIdentifier);
		}
		commands.addAll(this.params.getParamsAsStringList());
		return commands;
	}
}
