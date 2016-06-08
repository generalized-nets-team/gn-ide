package net.generalised.genedit.baseapp;


/**
 * 
 * @author Dimitar Dimitrov
 */
public abstract class BaseObject {

	private BaseObject parent;

	/**
	 * @param parent ... can be null
	 */
	public void setParent(BaseObject parent) {
		if (this.isParentOf(parent)) {
			throw new IllegalArgumentException("loops not allowed in parent relation");
		}
		this.parent = parent;
	}

	public BaseObject getParent() {
		return parent;
	}

	public boolean isParentOf(BaseObject child) {
		for (BaseObject model = child; model != null; model = model.getParent()) {
			if (this.equals(model)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the nearest parent of a given type. If this object is of the
	 * requested type, returns this object.
	 * 
	 * @param baseClass
	 *            The type of the parent.
	 * @return A parent of the given type or null, if no parent of this type is
	 *         available.
	 */
	public <T extends BaseObject> T getParent(Class<T> baseClass) {
		for (BaseObject model = this; model != null; model = model.getParent()) {
			if (baseClass.isInstance(model)) {
				return baseClass.cast(model);
			}
		}
		return null;
	}
}
