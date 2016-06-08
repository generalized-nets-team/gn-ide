package net.generalised.genedit.api;

import net.generalised.genedit.model.gn.Function;
import net.generalised.genedit.model.gn.GeneralizedNet;

public abstract class JavaFunction extends Function {

	public JavaFunction(String name) {
		super(name, false);
	}

	@Override
	public String getLanguage() {
		return JavaFunctionFactory.LANGUAGE;
	}
	
	@Override
	public String getDefinition() {
		return super.getName();
	}
	
	public abstract Object run(GeneralizedNet gn, JavaToken token);
}
