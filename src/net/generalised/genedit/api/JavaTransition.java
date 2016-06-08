package net.generalised.genedit.api;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Transition;

public class JavaTransition {

	private final Transition transition;
	
	public JavaTransition(Transition transition) {
		this.transition = transition;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof JavaTransition)) return false;
		return this.transition.getId().equals(((Transition)obj).getId());
	}
	
	public String getId() {
		return this.transition.getId();
	}
	
	public int getPriority() {
		return this.transition.getPriority();
	}
	
	public List<JavaPlace> getInputs() {
		List<Place> places = this.transition.getInputs().toPlaceList();
		List<JavaPlace> result = new ArrayList<JavaPlace>();
		for (Place place: places) {
			result.add(new JavaPlace(place));
		}
		return result;
	}
	
	public List<JavaPlace> getOutputs() {
		List<Place> places = this.transition.getOutputs().toPlaceList();
		List<JavaPlace> result = new ArrayList<JavaPlace>();
		for (Place place: places) {
			result.add(new JavaPlace(place));
		}
		return result;
	}
}
