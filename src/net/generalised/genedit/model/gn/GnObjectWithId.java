package net.generalised.genedit.model.gn;

import net.generalised.genedit.baseapp.StringUtil;
import net.generalised.genedit.baseapp.model.BaseModel;

/**
 * @author Dimitar Dimitrov
 */
public abstract class GnObjectWithId extends BaseModel {

	private String id = "";
	
	public String getId() {
		return id;
	}
	
	/**
	 * @param id ... if it is "" or null, no uniqueness check is performed.
	 */
	public void setId(String id) {
		if (! StringUtil.isNullOrEmpty(id)) {
			// TODO: while editing of GN, no uniqueness is required ?
			//TODO: validate id so it doesn't contain illegal chars?
			if (! getId().equals(id) && getParent() instanceof GnList && ((GnList<?>) getParent()).get(id) != null) {
				// TODO: is this check ok?
				throw new IllegalArgumentException("Item with ID '" + id + "' already exist in the parent list");
			}
			this.id = id;
		} else {
			this.id = ""; //TODO: maybe if null or "" - generate new?
		}
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "_" + id + "@" + Integer.toHexString(hashCode());
	}
	
}
