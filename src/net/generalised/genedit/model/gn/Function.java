package net.generalised.genedit.model.gn;

import net.generalised.genedit.baseapp.StringUtil;


/**
 * Represents a characteristic function in a GN.
 * The name of the function is its ID.
 * 
 * @author Dimitar Dimitrov
 *
 */
public abstract class Function extends GnObjectWithId implements GnObject {
	
	//TODO: be careful, setters should not have side effects in order to be undoable!
	
	private String definition;
	
	private final boolean constant;
	
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		if (constant) {
			throw new UnsupportedOperationException("Cannot modify constant function");
		}
		if (! StringUtil.isNullOrEmpty(definition)) {
			this.definition = definition;
			FunctionFactory factory = FunctionFactory.getFactory(getLanguage());
			setId(factory.extractNameFromDefinition(definition));
			// FIXME too long
			// TODO: do not allow functions with equal names; but in case of
			// opening an XML? and in case of empty defs?
			// TODO; do not allow functions with reserved names...
		} else {
			// TODO: be careful with that! if the user deletes the definition of
			// a function, the problems will arise later!
			this.definition = "";
			setId("");
		}
	}

	/**
	 * Gets the name of the function. Actually this is the value of {@link #getId()}.
	 * @return the name of the function
	 */
	public String getName() {
		return getId();
	}

	//TODO: validate name so it doesn't contain illegal chars?
	
//	public void setName(String name) {
//		//TODO: napravi go po-dobre: da zamestva imeto v definition (defun f ) -> (defun g ); ama ako e rekursivna...
//		if (definition != null && ! definition.equals("") && ! name.equals(this.name)) {
//			throw new IllegalStateException("Cannot change name if definition is not empty.");
//		}
//		this.name = name;
//	}

	protected Function(String definition, boolean isConstant) {
		setDefinition(definition);
		this.constant = isConstant;
		if (constant) {
			setId(definition); //FIXME - dirty hack; make ConstantFunction that extends Function and also wraps a Function
		}
		//this.name = name; this.definition = "";//well... how we'll restore that name later from XML?
	}
	
	public abstract String getLanguage();

	/**
	 * @return if the function is a predefined constant such as "true".
	 */
	public boolean isConstant() {
		return constant;
	}
}
