package net.generalised.genedit.model.dataaccess;

import net.generalised.genedit.baseapp.model.ParseException;

/**
 * Exception that is thrown when an error occurs during reading/writing GN
 * models in XML files.
 * 
 * @author Dimitar Dimitrov
 */
public class GnParseException extends ParseException {

	private static final long serialVersionUID = 4630623748002858581L;

	public GnParseException() {
	}

	public GnParseException(String message) {
		super(message);
	}

	public GnParseException(Throwable cause) {
		super(cause);
	}

	public GnParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
