package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.generalised.genedit.baseapp.model.BaseModelList;

/**
 * @author Dimitar Dimitrov
 */
public class PlaceRefList extends BaseModelList<PlaceReference> {

	private final boolean isInputList;
	
	public PlaceRefList(Transition transition, boolean isInputList) {
		super(false);
		setParent(transition);
		this.isInputList = isInputList;
	}
	
	public PlaceReference add(Place place) {
		Transition transition = getParent(Transition.class);
		if (isInputList) {
			place.setRightTransition(transition);
		} else {
			place.setLeftTransition(transition);
		}
		PlaceReference reference = get(place);
		if (reference == null) {
			reference = new PlaceReference(place, isInputList);
			super.add(reference);
		}
		return reference;
	}
	
	public PlaceReference get(Place place) {
		for (PlaceReference ref : this) {
			if (ref.getPlace().equals(place)) { //TODO: equals?
				return ref;
			}
		}
		return null;
	}
	
	//TODO: override remove too
	
	public PlaceReference undoableAdd(Place place) {
		//TODO: CompositeCommand
		//TODO: Code repeat
		Transition transition = getParent(Transition.class);
		if (isInputList) {
			place.undoableSet("rightTransition", transition);
		} else {
			place.undoableSet("leftTransition", transition);
		}
		PlaceReference reference = get(place);
		if (reference == null) {
			reference = new PlaceReference(place, isInputList);
			super.undoableAdd(reference);
		}
		return reference;
	}
	
	@Override
	public void undoableAdd(PlaceReference reference) {
		//TODO: CompositeCommand
		//TODO: Code repeat
		Place place = reference.getPlace();
		Transition transition = getParent(Transition.class);
		if (isInputList) {
			place.undoableSet("rightTransition", transition);
		} else {
			place.undoableSet("leftTransition", transition);
		}
		super.undoableAdd(reference);
	}
	
	@Override
	public void add(int index, PlaceReference element) {
		if (get(element.getPlace()) == null) {
			super.add(index, element);
		}
	}
	
	@Override
	public boolean add(PlaceReference e) {
		
		Place place = e.getPlace();
		Transition transition = getParent(Transition.class);
		if (isInputList) {
			place.setRightTransition(transition);
		} else {
			place.setLeftTransition(transition);
		}
		
		if (get(e.getPlace()) == null) {
			return super.add(e);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends PlaceReference> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends PlaceReference> c) {
		throw new UnsupportedOperationException();
	}
	
	public List<Place> toPlaceList() {
		List<Place> result = new ArrayList<Place>();
		for (PlaceReference ref : this) {
			result.add(ref.getPlace());
		}
		return result;
	}
}
