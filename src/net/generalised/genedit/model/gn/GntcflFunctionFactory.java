package net.generalised.genedit.model.gn;

import java.util.Arrays;
import java.util.List;

import net.generalised.genedit.model.dataaccess.xmlread.FunctionParser;
import net.generalised.genedit.model.dataaccess.xmlread.GntcflParser;

/**
 * 
 * @author Dimitar Dimitrov
 *
 */
public class GntcflFunctionFactory extends FunctionFactory {

	// TODO: Singleton (but not standard one - keep a hashtable language->factory
	
	public static String LANGUAGE = "GNTCFL";
	
	/**
	 * @param language
	 */
	public GntcflFunctionFactory() {
		super(LANGUAGE);
	}

	@Override
	public String createEmptyDefinition(String name) {
		//TODO: what if name is "true" or "false"?
		String definition = "(defun " + name + " \"\" () ()\n)";
		return definition;
	}

	@Override
	public Function createNewFunction(String definition) {
		//TODO: what if name is "true" or "false"?
		return new GntcflFunction(definition);
	}

	@Override
	public String extractNameFromDefinition(String definition) {
		int beginOfNameIndex = definition.indexOf("(defun") + 7;
		int endOfNameIndex = definition.indexOf(' ', beginOfNameIndex);
		int i = definition.indexOf('\n', beginOfNameIndex);
		if ((i > 0 && i < endOfNameIndex) || endOfNameIndex < 0)
			endOfNameIndex = i;
		i = definition.indexOf('\t', beginOfNameIndex);
		if ((i > 0 && i < endOfNameIndex) || endOfNameIndex < 0)
			endOfNameIndex = i;
		//TODO: \r is also a whitespace...
		//TODO: these can cause problems if longer whitespace
		String result = null;
		try {
			result = definition.substring(beginOfNameIndex, endOfNameIndex);
		} catch (StringIndexOutOfBoundsException e) {
			result = ""; //TODO: zasega
		}
		return result;
	}
	
	@Override
	public boolean isLanguageOf(String definitions) {
		if (definitions == null || definitions.trim().length() == 0) {
			return false; // cannot tell anything
		}
		return definitions.contains("(defun");
	}

	private static List<GntcflFunction> predefinedFunctions;
	
	@Override
	public List<? extends Function> getPredefinedConstants() {
		if (predefinedFunctions == null) {
			 predefinedFunctions = Arrays.asList(
						GntcflFunction.TRUE,
						GntcflFunction.FALSE);
		}
		return predefinedFunctions;
	}

//	@Override
//	public boolean isReservedName(String name) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	private static final GntcflParser parser = new GntcflParser();
	
	@Override
	public FunctionParser<GntcflFunction> getFunctionParser() {
		return parser;
	}
}
