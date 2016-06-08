package net.generalised.genedit.model.dataaccess.xmlread;

import java.util.List;

import net.generalised.genedit.model.gn.Function;

public interface FunctionParser<T extends Function> {
//	protected char openingBracket;//TODO: can be a String - like Pascal ("begin")
//	protected char closingBracket;//why the hell protected char, not protected getter?
	
//	protected String getNextFunction(int index) {
//		return null;
//	}
	
	public abstract List<T> parseFunctions(String functions);/* {
		List<? extends Function> result = new ArrayList<Function>();
		
		//while ()
		
		int startIndex = 0;
		int length = functions.length();
		int balance = 0;
		
		for (int index = 0; index < length; ++index) {
			char currentChar = functions.charAt(index);
			if (currentChar == openingBracket) {
				++balance;
			} else if (currentChar == closingBracket) {
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

					//TODO: function definition is HTML escaped... <= is &lt;=
					//TODO: if CDATA - maybe not?

					++index;
					String definition = functions.substring(startIndex, index);
//					String name = Function.extractNameFromDefinition(definition);
//
//					Function f = new Function(name);
//					f.setDefinition(definition);
//					result.add(f);
					
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
	}*/
}
