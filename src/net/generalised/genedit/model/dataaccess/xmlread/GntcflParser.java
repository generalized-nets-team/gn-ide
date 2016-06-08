package net.generalised.genedit.model.dataaccess.xmlread;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.Function;
import net.generalised.genedit.model.gn.GntcflFunction;

/**
 * Parser for GNTCFL characteristic functions.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class GntcflParser implements FunctionParser<GntcflFunction> {

	/**
	 * Parses the specified text that contains all function definitions
	 * 
	 * @param functions text that contains function definitions 
	 * @return list of {@link Function}s
	 */
	public List<GntcflFunction> parseFunctions(String functions) {
		List<GntcflFunction> result = new ArrayList<GntcflFunction>();
				
		int startIndex = 0;
		int length = functions.length();
		int balance = 0;
		
		for (int index = 0; index < length; ++index) {
			char currentChar = functions.charAt(index);
			if (currentChar == '(') {
				++balance;
			} else if (currentChar == ')') {
				--balance;
				if (balance == 0) {
//					if (index < length - 1) {
//						++index;
//						currentChar = this.functionsCharacters.charAt(index);
//						while (currentChar != ' ' && currentChar != '\n' 
//							&& currentChar != '\t' && currentChar != '\r') {
//							++index;
//							currentChar = this.functionsCharacters.charAt(index);
//						}
//					}
//					TODO: s 1 cikal skip next whitespace - uveli4i index

					//TODO: function definition is HTML escaped! Use CDATA... <= is &lt;=
					//TODO: if CDATA - maybe not?

					++index;
					String definition = functions.substring(startIndex, index);
					GntcflFunction f = new GntcflFunction(definition);
					result.add(f);
					
					startIndex = index;
				}
			} else if (currentChar == ';') {
				//TODO: skip whole line
				//FIXME (CDATA): unfortunately, ; is present in &lt; too
			} else if (currentChar == '"') {
				//TODO: skip whole string, escaping \" ?
			}
		}
		
		return result;
	}
	
	//TODO: Functions -> String? Or this is the same for each language? 

}
