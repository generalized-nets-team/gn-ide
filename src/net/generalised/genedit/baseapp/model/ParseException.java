package net.generalised.genedit.baseapp.model;

/**
 * Exception that should be thrown if a document cannot be read due to invalid file format.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = -3570841530225842416L;

	public ParseException() {
	}

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
