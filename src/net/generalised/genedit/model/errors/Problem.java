package net.generalised.genedit.model.errors;

/**
 * Represents a problem in a GN model. It can be an error or a warning.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class Problem {
	
	private final String message;
	
	//TODO: add problem location info - number of line in a function, GnObject pointer...
	
	/**
	 * true - Error
	 * false - Warning 
	 */
	private final boolean isError;

	public Problem(String message, boolean isError) {
		super();
		this.message = message;
		this.isError = isError;
	}

	public String getMessage() {
		return message;
	}

	public boolean isError() {
		return isError;
	}
}
