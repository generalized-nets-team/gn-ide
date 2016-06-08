package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.baseapp.model.BaseModel;

/**
 * Represents a reference from a transition to a place in a GN.
 * Can be input or output for a given transition.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class PlaceReference extends BaseModel implements GnObject {
	private List<Point> arc;

	private Place place;
	
	//TODO: use enum or something else
	private boolean isInputFromThisPlace;

	public List<Point> getArc() {
		return arc;
	}

	public void setArc(List<Point> arc) {
		this.arc = arc;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}
	
	public boolean isInputFromThisPlace() {
		return isInputFromThisPlace;
	}

	public void setInputFromThisPlace(boolean isInputFromThisPlace) {
		this.isInputFromThisPlace = isInputFromThisPlace;
	}

	public PlaceReference() {
		this(null, false);
	}
	
	public PlaceReference(Place place, boolean isInputFromThisPlace) {
		this.arc = new ArrayList<Point>();
		this.isInputFromThisPlace = isInputFromThisPlace;
		this.place = place;
	}

}
