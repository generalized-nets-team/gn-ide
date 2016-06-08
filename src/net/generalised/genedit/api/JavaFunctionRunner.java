package net.generalised.genedit.api;

import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.simulation.model.embedded.FunctionRunner;

public class JavaFunctionRunner implements FunctionRunner {

	private static JavaFunctionRunner instance;
	
	private JavaFunctionRunner() {}
	
	public static JavaFunctionRunner getInstance() {
		if (instance == null) {
			instance = new JavaFunctionRunner();
		}
		return instance;
	}
	
	public Object run(FunctionReference function, GeneralizedNet gn, Token token) {
		if (function instanceof JavaFunctionReference)
			return ((JavaFunctionReference) function).getFunction().run(gn, new JavaToken(token));
		else
			throw new IllegalArgumentException("JavaFunctionRunner can run only JavaFunctions");
	}

	public String getFunctionLanguage() {
		return JavaFunctionFactory.LANGUAGE;
	}

}
