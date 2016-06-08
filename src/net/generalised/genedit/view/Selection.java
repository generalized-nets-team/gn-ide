package net.generalised.genedit.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.model.BaseObservableImpl;
import net.generalised.genedit.baseapp.model.BaseObserver;
import net.generalised.genedit.model.GnDocument;

/**
 * Represents a multiple selection of GN model objects.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class Selection extends BaseObservableImpl implements BaseObserver {
	
	private final GnDocument document;
	private final List<Object> selectedItems; //TODO: WeakReferences? and why Object?
	
	// FIXME: if a given object is removed from GN, it should be removed from here too!!! (see update method)
	
	public Selection(GnDocument document) {
		this.document = document;
		this.selectedItems = new ArrayList<Object>();
		this.document.addObserver(this);
	}
	
	/**
	 * Gets a list of all selected objects.
	 * 
	 * @return an unmodifiable list of all selected objects
	 */
	public List<Object> getObjects() {
		return Collections.unmodifiableList(selectedItems);
	}
	
	//TODO: da pazi selekciqta za razli4nite mreji, zasega ne
	// TODO: why boolean?
	public boolean add(/*GeneralizedNet gn,*/ Object object) {
		if (!selectedItems.contains(object)) {
			selectedItems.add(object);
			notifyObservers();
			return true;
		}
		return false;
	}
	
	/**
	 * Selects only the specified object. All previously selected objects are deselected.
	 * 
//	 * @param gn the GN to which this object belongs, in case the model contains other GNs
	 * @param object the object to be selected
	 */
	public void select(/*GeneralizedNet gn, */Object object) {
		selectedItems.clear();
		selectedItems.add(object);
		
		notifyObservers();
	}
	
	public boolean clear(/*GeneralizedNet gn*/) {
		if (selectedItems.size() > 0) {
			selectedItems.clear();
			
			notifyObservers();
			
			return true;
		}
		return false;
	}

	public void update(BaseObservable o, Object arg) {
		// TODO check for removed objects, remove from selectedItems and then notifyObservers
	}
	
	public boolean containsOnlyInstancesOf(Class<?> clazz) {
		
		boolean result = true;
		
		for (Object object : selectedItems) {
			if (! clazz.isAssignableFrom(object.getClass())) {
				return false;
			}
		}
		
		return result;
	}
}
