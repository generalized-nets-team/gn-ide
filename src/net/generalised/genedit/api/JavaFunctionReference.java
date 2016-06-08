package net.generalised.genedit.api;

import net.generalised.genedit.model.gn.FunctionReference;

public class JavaFunctionReference extends FunctionReference {

	private final JavaFunction function;
	
	public JavaFunctionReference(JavaFunction function) {
		super(function.getName());
		this.function = function;
	}

	public JavaFunction getFunction() {
		return function;
	}
}
