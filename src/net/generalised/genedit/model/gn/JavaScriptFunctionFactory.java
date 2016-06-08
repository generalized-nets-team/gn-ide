package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.generalised.genedit.baseapp.StringUtil;
import net.generalised.genedit.model.dataaccess.xmlread.FunctionParser;
import net.generalised.genedit.model.dataaccess.xmlread.JavaScriptParser;

public class JavaScriptFunctionFactory extends FunctionFactory {

	public static String LANGUAGE = "JavaScript";
	
	public JavaScriptFunctionFactory() {
		super(LANGUAGE);
	}

	@Override
	protected String createEmptyDefinition(String name) {
		//TODO: what if name is "true" or "false"?
		String definition = "function " + name + "() {\n}";
		return definition;
	}

	@Override
	protected Function createNewFunction(String definition) {
		return new JavaScriptFunction(definition);
	}

	@Override
	public String extractNameFromDefinition(String definition) {
		if (! StringUtil.isNullOrEmpty(definition)) {
			Pattern pattern = Pattern.compile("function +[a-zA-Z0-9_]+");
			Matcher matcher = pattern.matcher(definition);
			if (matcher.find()) {
				String declaration = matcher.group();
				String name = declaration.substring("function".length() + 1).trim();
				return name;
			}
		}
		
		return "";
		
		// TODO skip whitespace, comments, ...
		// TODO use 3rd party JS parser, validator, etc.; http://stackoverflow.com/questions/6511556/javascript-parser-for-java
		// Rhino?
		// http://fifesoft.com/rsyntaxtextarea/
		// http://code.google.com/p/jsyntaxpane/
	}

	private static List<JavaScriptFunction> predefinedFunctions;
	
	@Override
	public List<? extends Function> getPredefinedConstants() {
		if (predefinedFunctions == null) {
			predefinedFunctions = new ArrayList<JavaScriptFunction>() {
				private static final long serialVersionUID = 1L;
				{
					add(JavaScriptFunction.TRUE);
					add(JavaScriptFunction.FALSE);
				}
			};
		}
		return predefinedFunctions;
	}

	@Override
	public boolean isLanguageOf(String definitions) {
		if (definitions == null || definitions.trim().length() == 0) {
			return false; // cannot tell anything
		}
		// TODO better
		return definitions.contains("function ") && definitions.contains("{");
	}

	private static final JavaScriptParser parser = new JavaScriptParser();
	
	@Override
	public FunctionParser<JavaScriptFunction> getFunctionParser() {
		return parser;
	}
}

