package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.List;

public class GnUtil {

	private GnUtil() {
	}
	
	// ... if there is more than one token at the specified place, return only one of them 
	public static Token getSingleTokenAt(GeneralizedNet gn, String placeId) {
		List<Token> tokens = gn.getTokens();
		for (Token t : tokens) {
			if (t.getHost() != null && t.getHost().getId().equals(placeId)) {
				return t;
			}
		}
		return null;
	}
	
	public static List<Token> getTokensAt(GeneralizedNet gn, String placeId) {
		List<Token> tokens = gn.getTokens();
		List<Token> result = new ArrayList<Token>();
		for (Token t : tokens) {
			if (t.getHost() != null && t.getHost().getId().equals(placeId)) {
				result.add(t);
			}
		}
		return result;
	}
}
