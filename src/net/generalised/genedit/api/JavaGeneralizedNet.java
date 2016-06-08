package net.generalised.genedit.api;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnList;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.Transition;

public class JavaGeneralizedNet {

	private final GeneralizedNet gn;
	
	public JavaGeneralizedNet(GeneralizedNet gn) {
		if (!gn.getFunctionLanguage().equals(JavaFunctionFactory.LANGUAGE))
			throw new IllegalArgumentException("Only Java GNs are allowed");
		this.gn = gn;
	}

	GeneralizedNet getWrappedGn() { // Default visibility
		return this.gn;
	}
	
	public String getName() {
		return this.gn.getName();
	}
	
	public int getTime() {
		return this.gn.getCurrentTime();
	}
	
	public List<JavaTransition> getTransitions() {
		// TODO implement some map function
		GnList<Transition> transitions = gn.getTransitions();
		List<JavaTransition> result = new ArrayList<JavaTransition>();
		for (Transition transition : transitions) {
			result.add(new JavaTransition(transition)); // TODO use the same instance
		}
		return result;
	}

	public JavaTransition getTransition(String id) {
		Transition transition = gn.getTransitions().get(id);
		if (transition != null) return new JavaTransition(transition);
		return null;
	}
	
	public List<JavaPlace> getPlaces() {
		GnList<Place> places = gn.getPlaces();
		List<JavaPlace> result = new ArrayList<JavaPlace>();
		for (Place place: places) {
			result.add(new JavaPlace(place));
		}
		return result;
	}

	public JavaPlace getPlace(String id) {
		Place place = gn.getPlaces().get(id);
		if (place != null) return new JavaPlace(place);
		return null;
	}

	public List<JavaToken> getTokens() {
		GnList<Token> tokens = gn.getTokens();
		List<JavaToken> result = new ArrayList<JavaToken>();
		for (Token token : tokens) {
			result.add(new JavaToken(token));
		}
		return result;
	}

	public JavaToken getToken(String id) {
		Token token = gn.getTokens().get(id);
		if (token != null) return new JavaToken(token);
		return null;
	}
}
