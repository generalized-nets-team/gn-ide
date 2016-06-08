package net.generalised.genedit.model.gn.transitiontype;

public class TransitionTypeParser {

	/**
	 * @param type
	 * @return null, if the given expression is invalid and cannot be parsed.
	 */
	public static TransitionTypeNode parse(String type) {
		String expression = type.replaceAll(" ", ""); // XXX 
		if (expression.length() == 0) {
			return new TrueNode();
		}
		try {
			ParseResult parseResult = parse(expression, 0);
			return parseResult.tree;
		} catch (Exception e) {
			return null;
		}
	}
	
	// TODO should return last index!
	private static ParseResult parse(String expression, int startIndex) throws IllegalArgumentException {
		
		ParseResult result = new ParseResult();
		
		String token = getNextToken(expression, startIndex);
		if (ConjunctionNode.SYMBOL.equals(token) || DisjunctionNode.SYMBOL.equals(token)) {
			TransitionTypeMultiNode tree;
			if (ConjunctionNode.SYMBOL.equals(token)) {
				tree = new ConjunctionNode();
			} else if (DisjunctionNode.SYMBOL.equals(token)) {
				tree = new DisjunctionNode();
			} else {
				throw new IllegalArgumentException();
			}
			
			int index = startIndex + token.length();
			String openingBracket = getNextToken(expression, index);
			if (! openingBracket.equals("(")) {
				throw new IllegalArgumentException();
			}
			index++; // skip '('
		
			while (! ")".equals(token = getNextToken(expression, index))) {
				if (token == null) {
					break;
				}
				if (token.equals(",")) {
					continue;
				}
				ParseResult parseResult = parse(expression, index);
				tree.addChild(parseResult.tree);
				index = parseResult.nextStartIndex;
				if (expression.charAt(index) == ',') {
					index++; // skip ','
				}
			}
			
			result.tree = tree;
			result.nextStartIndex = index + 1;
			return result;
		} else if (token != null && token.length() > 0) {
			// checking for (a and b)
			if (token.equals("false") || token.contains("(") || token.contains(")") || token.contains(",")) {
				throw new IllegalArgumentException();
			}
			result.tree = new PlaceNode(token);
			result.nextStartIndex = startIndex + token.length();
			return result;
		}
		throw new IllegalArgumentException();
	}

	private static int indexOf(String text, char symbol, int startIndex) {
		int result = text.indexOf(symbol, startIndex);
		if (result < 0) {
			result = Integer.MAX_VALUE;
		}
		return result;
	}
	
	/**
	 * Default visibility for tests only.
	 * 
	 * @param expression Must not contain whitespace.
	 * @param startIndex
	 * @return
	 */
	static String getNextToken(String expression, int startIndex) {
		if (startIndex >= expression.length()) {
			return null;
		}
		int indexOfOpeningBracket = indexOf(expression,'(', startIndex);
		int indexOfClosingBracket = indexOf(expression,')', startIndex);
		int indexOfComma = indexOf(expression, ',', startIndex);
		
		if (indexOfOpeningBracket == startIndex || indexOfClosingBracket == startIndex || indexOfComma == startIndex) {
			return expression.substring(startIndex, startIndex + 1);
		}
		
		int minIndex = Math.min(Math.min(indexOfOpeningBracket, indexOfClosingBracket),
				indexOfComma);
		if (minIndex == Integer.MAX_VALUE) {
			return expression.substring(startIndex);
		}
		return expression.substring(startIndex, minIndex);
	}
	
	static class ParseResult {
		public TransitionTypeNode tree;
		public int nextStartIndex;
	}
}
