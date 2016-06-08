package net.generalised.genedit.api;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnUtil;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.Transition;

public class JavaPlace {

	private final Place place;
	
	public JavaPlace(Place place) {
		this.place = place;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof JavaPlace)) return false;
		return this.place.getId().equals(((Place)obj).getId());
	}

	public String getId() {
		return this.place.getId();
	}
	
	public int getPriority() {
		return this.place.getPriority();
	}

	public IntegerInf getCapacity() {
		return this.place.getCapacity();
	}

	public JavaTransition getInput() {
		Transition transition = this.place.getLeftTransition();
		return new JavaTransition(transition);
	}

	public JavaTransition getOutput() {
		Transition transition = this.place.getRightTransition();
		return new JavaTransition(transition);
	}

	public List<JavaToken> getTokens() {
		GeneralizedNet gn = place.getParent(GeneralizedNet.class);
		List<Token> tokens = GnUtil.getTokensAt(gn, place.getId());
		List<JavaToken> result = new ArrayList<JavaToken>();
		for (Token token : tokens) {
			result.add(new JavaToken(token));
		}
		return result;
	}
	
	public JavaToken getToken(String id) {
		GeneralizedNet gn = place.getParent(GeneralizedNet.class);
		Token token = gn.getTokens().get(id);
		if (token != null && token.getHost() != null && token.getHost().getId().equals(place.getId())) return new JavaToken(token);
		return null;
	}
	
	public JavaToken addToken(String id) {
		Token token = new Token(id, this.place);
		GeneralizedNet gn = place.getParent(GeneralizedNet.class);
		gn.getTokens().add(token);
		return new JavaToken(token);
	}
	
	public void removeToken(String id) {
		GeneralizedNet gn = place.getParent(GeneralizedNet.class);
		Token token = gn.getTokens().get(id);
		if (token != null && token.getHost() != null && token.getHost().getId().equals(place.getId())) {
			gn.getTokens().remove(token);
		}
	}
}
