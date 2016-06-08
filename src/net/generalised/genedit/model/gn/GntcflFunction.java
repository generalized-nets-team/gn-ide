package net.generalised.genedit.model.gn;

/**
 * @author Dimitar Dimitrov
 *
 */
public class GntcflFunction extends Function {

	public GntcflFunction(String definition) {
		this(definition, false);
	}

	private GntcflFunction(String definition, boolean constant) {
		super(definition, constant);
		
	}
	
	@Override
	public String getLanguage() {
		return GntcflFunctionFactory.LANGUAGE;
	}

	public static final GntcflFunction TRUE = new GntcflFunction("true", true);
	
	public static final GntcflFunction FALSE = new GntcflFunction("false", true);
}
