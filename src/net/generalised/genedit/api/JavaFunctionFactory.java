package net.generalised.genedit.api;

import java.util.List;

import net.generalised.genedit.model.dataaccess.xmlread.FunctionParser;
import net.generalised.genedit.model.gn.Function;
import net.generalised.genedit.model.gn.FunctionFactory;

public class JavaFunctionFactory extends FunctionFactory {

	public static String LANGUAGE = "Java";
	
	public JavaFunctionFactory() {
		super(LANGUAGE);
	}

	@Override
	protected Function createNewFunction(String definition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractNameFromDefinition(String definition) {
		return definition;
	}

	@Override
	protected String createEmptyDefinition(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLanguageOf(String definitions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<? extends Function> getPredefinedConstants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FunctionParser<?> getFunctionParser() {
		// TODO Auto-generated method stub
		return null;
	}

}
