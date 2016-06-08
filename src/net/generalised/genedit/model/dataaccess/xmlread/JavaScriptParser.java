package net.generalised.genedit.model.dataaccess.xmlread;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.generalised.genedit.model.gn.FunctionFactory;
import net.generalised.genedit.model.gn.JavaScriptFunction;
import net.generalised.genedit.model.gn.JavaScriptFunctionFactory;

public class JavaScriptParser implements FunctionParser<JavaScriptFunction> {

	public List<JavaScriptFunction> parseFunctions(String functions) {

		List<JavaScriptFunction> result = new ArrayList<JavaScriptFunction>();
		
		// TODO temporary:
		// instead of spaces can be tab
		Pattern pattern = Pattern.compile("function +[a-zA-Z0-9_]+ *\\([a-zA-Z0-9_, ]*\\)");
		Matcher matcher = pattern.matcher(functions);
		while (matcher.find()) {
			String declaration = matcher.group();
			result.add(new JavaScriptFunction(declaration + " {}"));
		}
		
//		int startIndex = 0;
//		int length = functions.length();
//		int balance = 0;
//		final int functionWordLength = "function".length();
//		
//		// TODO utilites for skipping a comment (skipUntil("*/") or skipUntil("\n")->new index)
//		// skipUntil("\"", escapeStrings) - for strings that may contain quotes
//		
//		for (int index = 0; index < length; ++index) {
//			char currentChar = functions.charAt(index);
//			// if /* - loop until */ ; if // - loop until \n
//			if (currentChar == 'f'
//					&& index < length - 1 - functionWordLength
//					&& functions.substring(index + 1,
//							index + functionWordLength).equals("unction")) {
//				//beginIndex = index; // TODO get previous comments too!
//				index += functionWordLength;
//				// skip whitespace b4 da name? not here if ()
//				// FIXME temporary:
//				System.out.println(functions.substring(index));
//				/*int indexOfOpeningBracket = functions.indexOf('(', index);
//				String functionName = functions.substring(index, indexOfOpeningBracket).trim();
//				result.add(new JavaScriptFunction("function " + functionName + "(){}"));*/
//			}
//		}
//		// TODO try this:
//		/*
//    private ScriptOrFnNode parseJavascript(File file) throws IOException {
//        // Try to open a reader to the file supplied...
//        Reader reader = new FileReader(file);
//
//        // Setup the compiler environment, error reporter...
//        CompilerEnvirons compilerEnv = new CompilerEnvirons();
//        ErrorReporter errorReporter = compilerEnv.getErrorReporter();
//
//        // Create an instance of the parser...
//        Parser parser = new Parser(compilerEnv, errorReporter);
//
//        String sourceURI;
//
//        try {
//            sourceURI = file.getCanonicalPath();
//        } catch (IOException e) {
//            sourceURI = file.toString();
//        }
//
//        // Try to parse the reader...
//        ScriptOrFnNode scriptOrFnNode = parser.parse(reader, sourceURI, 1);
//
//        return scriptOrFnNode;
//    }
//		 */
		
		return result;
	}

	//Temp, move to test:
	public static void main(String[] args) {
		FunctionFactory.addFactory(new JavaScriptFunctionFactory());
		
		JavaScriptParser parser = new JavaScriptParser();
		List<JavaScriptFunction> functions = parser.parseFunctions("jsklfjsdlf function x() ggbg functiony()\nfunction test(m,n) {function intest (){}   x=new function (){}}   function f( ) {function h0 ( k0, l1 )}");
		for (JavaScriptFunction function : functions) {
			System.out.println(function.getName());
		}
	}
}

