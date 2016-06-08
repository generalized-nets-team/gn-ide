package net.generalised.genedit.model.gn;

import net.generalised.genedit.baseapp.model.BaseModelList;

/**
 * @author Dimitar Dimitrov
 *
 */
public class GnList<T extends GnObjectWithId> extends BaseModelList<T> {

//	abstract GnObjects - done
//	GnObjects(GN) - done
//	GnObject getById(String) - done
//	dali da ne e generic? - done
//	add: trqbva da nqma s takova Id
//	remove
//
//
//	Places:GnObjects
//	remove: trqbva da maha i vsi4ki arcs!
//
//	Transitions:GnObjects
//	addInput
//	remove: trqbva da maha i arcs, i refs ot places... ami i matricite...

	public GnList() {
		super(true);
	}
	
	@Override
	public boolean add(T object) {
		if (! this.contains(object)) { //TODO: ensure uniqueness of IDs?
			return super.add(object);
		}
		//return false;
		throw new IllegalArgumentException("object already in the list");
	}
	
	// TODO: there are more add methods!
	
	// TODO: in GeneralizedNet ima6e T addT(T), ama e kofti kato ideq... vsy6tnost samo za Function ima6e
	
	// TODO (ne e naleja6to): kogato nqkoi smeni id-to na element ot tezi, trqbva da proverqvame za uniqueness pak!
	
	/**
	 * Gets the first element with the given identifier. //TODO: ne e hubavo da ima drugi...
	 * 
	 * @param id ... If it is null or an empty string, the method returns null, even if an element with id="" exist.
	 * @return
	 */
	public T get(String id) {
		T result = null;
		if (id != null && id.length() > 0)
			for (T element : this) { //TODO: optimize this search?
				if (element.getId().equals(id)) {
					result = element;
					break;
				}
			}
		return result;
	}
}
