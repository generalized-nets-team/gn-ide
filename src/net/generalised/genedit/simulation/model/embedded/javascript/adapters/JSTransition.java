package net.generalised.genedit.simulation.model.embedded.javascript.adapters;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceRefList;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Transition;

import org.mozilla.javascript.Scriptable;

public class JSTransition extends BaseJSAdapter<Transition> {

	private static final long serialVersionUID = 2873820620994358299L;

	/**
	 * The class name that JavaScript users will see.
	 */
	public static final String CLASS_NAME = "Transition";

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	// TODO move to base class
	public String jsGet_id() {
		return getAdaptedObject().getId();
	}
	
	public int jsGet_priority() {
		return getAdaptedObject().getPriority();
	}
	
	public Scriptable jsGet_inputs() {
		
		PlaceRefList inputs = getAdaptedObject().getInputs();
		List<Place> inputPlaces = new ArrayList<Place>();
		for (PlaceReference ref : inputs) {
			inputPlaces.add(ref.getPlace());
		}
		
		return getGnList(inputPlaces, JSPlace.CLASS_NAME);
	}

	public Scriptable jsGet_outputs() {
		
		PlaceRefList outputs = getAdaptedObject().getOutputs();
		List<Place> outputPlaces = new ArrayList<Place>();
		for (PlaceReference ref : outputs) {
			outputPlaces.add(ref.getPlace());
		}
		
		return getGnList(outputPlaces, JSPlace.CLASS_NAME);
	}
}
