package net.generalised.genedit.model.gn;

public class JavaScriptFunction extends Function {

	public JavaScriptFunction(String definition) {
		this(definition, false);
	}

	protected JavaScriptFunction(String definition, boolean isConstant) {
		super(definition, isConstant);
	}

	@Override
	public String getLanguage() {
		return JavaScriptFunctionFactory.LANGUAGE;
	}
	
	public static final JavaScriptFunction TRUE = new JavaScriptFunction("true", true);

	public static final JavaScriptFunction FALSE = new JavaScriptFunction("false", true);
}
