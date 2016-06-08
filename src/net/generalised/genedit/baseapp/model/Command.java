package net.generalised.genedit.baseapp.model;

/**
 * Represents an encapsulated command, as described in the Command design pattern. 
 * 
 * @author Dimitar Dimitrov
 *
 */
public abstract class Command {
	
	private String description;
	
	private BaseModel affectedObject;
	
	public abstract void execute();

	public abstract void unExecute();
	
	public String getDescription() {
		return description;
	}
	
	private static final String DEFAULT_DESCRIPTION = "";
	
	public Command(String description) {
		if (description != null) {
			this.description = description;
		} else {
			this.description = DEFAULT_DESCRIPTION;
		}
		this.affectedObject = null; // null means that the root object is modified, e.g. Document
	}
	
	public Command() {
		this(DEFAULT_DESCRIPTION);
	}

	public void setAffectedObject(BaseModel affectedObject) {
		this.affectedObject = affectedObject;
	}

	public BaseModel getAffectedObject() {
		return affectedObject;
	}
}
