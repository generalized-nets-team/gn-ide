package net.generalised.genedit.simulation.model.embedded.javascript.adapters;

import java.util.List;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnUtil;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.Transition;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JSPlace extends BaseJSAdapter<Place> {

	private static final long serialVersionUID = -5692366742447518999L;

	/**
	 * The class name that JavaScript users will see.
	 */
	public static final String CLASS_NAME = "Place";

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	public String jsGet_id() {
		return getAdaptedObject().getId();
	}

	public int jsGet_priority() {
		return getAdaptedObject().getPriority();
	}

	public Number jsGet_capacity() {
		return getAdaptedObject().getCapacity().toNumber();
	}

	@SuppressWarnings("unchecked")
	private Scriptable getTransition(Transition transition) {
		if (transition != null) {
			BaseJSAdapter<Transition> result = (BaseJSAdapter<Transition>) Context
					.getCurrentContext().newObject(getParentScope(), JSTransition.CLASS_NAME);
			result.setAdaptedObject(transition);
	
			return result;
		} else {
			return null;
		}
	}
	
	public Scriptable jsGet_input() {
		return getTransition(getAdaptedObject().getLeftTransition());
	}
	
	public Scriptable jsGet_output() {
		return getTransition(getAdaptedObject().getRightTransition());
	}

	public JSToken[] jsGet_tokens() {
		
		Place place = getAdaptedObject();
		GeneralizedNet gn = place.getParent(GeneralizedNet.class);
		List<Token> tokens = GnUtil.getTokensAt(gn, place.getId());
		
		int tokensCount = tokens.size();
		JSToken[] result = new JSToken[tokensCount];
		for (int i = 0; i < tokensCount; i++) {
			result[i] = (JSToken) Context.getCurrentContext().newObject(
					getParentScope(), JSToken.CLASS_NAME);
			result[i].setAdaptedObject(tokens.get(i));
			result[i].setReadOnly(isReadOnly());
		}
		return result;
	}
	
	/*
	TODO?
	exists 	<place reference frame index> 	Evaluates to true if in the previous step there had been a token in the referred place or if a token is moved to this place during the current step (by some previous operation prior to the current function execution).
	isthere 	<place reference frame index> 	Evaluates to true if in the previous step there had been at least one token in the referred place and it hasn't been moved during this step by an operation prior to this function execution.
	wasthere 	<place reference index> 	Evaluates to true if there had been a token in this place in the previous step. 
	*/
}
