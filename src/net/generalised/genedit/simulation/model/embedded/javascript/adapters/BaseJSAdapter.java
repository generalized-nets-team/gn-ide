package net.generalised.genedit.simulation.model.embedded.javascript.adapters;

import java.util.List;

import net.generalised.genedit.model.gn.GnObjectWithId;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Base class for JavaScript adapters of GN objects.
 * 
 * @param <T> The type of the object being adapted.
 * 
 * @author Dimitar Dimitrov
 */
public abstract class BaseJSAdapter<T> extends ScriptableObject {

	private static final long serialVersionUID = -215068499689007325L;

	/**
	 * Reference to the object being adapted by this adapter.
	 */
	protected T adaptedObject;
	
	private int setObjectCount = 0;
	
	private boolean readOnly = false;

	/**
	 * Sets the object adapted. This method cannot be called more than once.
	 * 
	 * @param adaptedObject The object to be adapted. 
	 */
	public void setAdaptedObject(T adaptedObject) {
		if (this.setObjectCount != 0)
			throw new IllegalStateException("adapted object cannot be set twice");
		this.setObjectCount++;
		
		this.adaptedObject = adaptedObject;
	}

	/**
	 * Gets the object adapted by this adapter.
	 * @return The object adapted by this adapter.
	 */
	public T getAdaptedObject() {
		return this.adaptedObject;
	}

	@SuppressWarnings("unchecked")
	protected <R extends GnObjectWithId> Scriptable getGnList(
			List<R> gnList, /*Class<S> jsClass,*/ String jsClassName) {
		
		Scriptable jsObjects = Context.getCurrentContext().newObject(getParentScope());
		
		for (R gnObject : gnList) {
			BaseJSAdapter<R> jsAdapter = (BaseJSAdapter<R>) Context.getCurrentContext().newObject(getParentScope(), jsClassName);
			jsAdapter.setReadOnly(isReadOnly());
			jsAdapter.setAdaptedObject(gnObject);
			jsObjects.put(gnObject.getId(), jsObjects, jsAdapter);
		}
		
		// TODO another property - count/length? What if some object has the same ID?
		
		return jsObjects;
	}
	
	public void setReadOnly(boolean value) {
		this.readOnly = value;
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}

}
