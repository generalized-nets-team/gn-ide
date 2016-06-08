package net.generalised.genedit.simulation.model.embedded.javascript.adapters;


import net.generalised.genedit.model.gn.GeneralizedNet;

import org.mozilla.javascript.Scriptable;

public class JSGeneralizedNet extends BaseJSAdapter<GeneralizedNet> {

	private static final long serialVersionUID = -4148871258893370691L;

	/**
	 * The class name that JavaScript users will see.
	 */
	public static final String CLASS_NAME = "GeneralizedNet";

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	public Scriptable jsGet_transitions() {
		return getGnList(getAdaptedObject().getTransitions(), /*JSTransition.class,*/ JSTransition.CLASS_NAME);
	}

	public Scriptable jsGet_places() {
		return getGnList(getAdaptedObject().getPlaces(), JSPlace.CLASS_NAME);
	}

	public Scriptable jsGet_tokens() {
		// TODO do not return token generators?
		// The name "tokens" is hardcoded in EmbeddedSimulation
		return getGnList(getAdaptedObject().getTokens(), JSToken.CLASS_NAME);
	}

	public String jsGet_name() {
		return getAdaptedObject().getName();
	}

	public int jsGet_time() {
		return getAdaptedObject().getCurrentTime();
	}
	
	// TODO annotations for GN get/set methods and automatically create corresponding JS properties?
}
