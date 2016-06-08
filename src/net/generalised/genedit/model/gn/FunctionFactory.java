package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.generalised.genedit.baseapp.StringUtil;
import net.generalised.genedit.model.dataaccess.xmlread.FunctionParser;

/**
 * Base class for all factories that create functions.
 * Implementation of the Abstract Factory Design Pattern.
 * 
 * @author Dimitar Dimitrov
 */
public abstract class FunctionFactory {
	
	private final String language;
	
	public FunctionFactory(String language) {
		StringUtil.assertNotEmpty(language, "language");
		this.language = language;
	}
	
	public Function createEmptyFunction(String name) {
		Function function = getPredefinedConstant(name);
		if (function != null) {
			return function;
		}
		String definition = createEmptyDefinition(name);
		return createNewFunction(definition);
	}
	
	public Function createFunction(String definition) {
		String name = extractNameFromDefinition(definition);
		Function function = getPredefinedConstant(name);
		if (function != null) {
			return function;
		}
		return createNewFunction(definition);
	}
	
	protected abstract Function createNewFunction(String definition);
	
	public abstract String extractNameFromDefinition(String definition);

	protected abstract String createEmptyDefinition(String name);

	public abstract boolean isLanguageOf(String definitions); //requires full specification

	public String getLanguage() {
		return language;
	}

	public abstract List<? extends Function> getPredefinedConstants(); 

	public Function getPredefinedConstant(String name) {
		// TODO: some languages are case-sensitive, others - not
		for (Function f : getPredefinedConstants()) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}
	
	//TODO: something for highlighting here? get map<interval, syntax element>
	
	private static Map<String, FunctionFactory> factories = new HashMap<String, FunctionFactory>();
	
	public static void addFactory(FunctionFactory factory) {
		String language = factory.getLanguage();
		StringUtil.assertNotEmpty(language, "language");
		factories.put(language, factory);
	}
	
	/**
	 * @param language ... case insensitive
	 * @return
	 */
	public static FunctionFactory getFactory(String language) {
		return factories.get(language);
	}
	
	public static boolean isLanguageSupported(String language) {
		return factories.containsKey(language);
	}
	
	public static String detectLanguage(String definitions) {
		for (String language : factories.keySet()) {
			if (factories.get(language).isLanguageOf(definitions)) {
				return language;
			}
		}
		return null;
	}
	
	public static List<String> getSupportedLanguages() {
		List<String> result = new ArrayList<String>(factories.keySet());
		return result;
	}
	
	public abstract FunctionParser<?> getFunctionParser();
}
