package net.generalised.genedit.simulation.model.embedded.javascript.adapters;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GnList;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JSToken extends BaseJSAdapter<Token> {

	private static final long serialVersionUID = -7481339824219614932L;

	/**
	 * The class name that JavaScript users will see.
	 */
	public static final String CLASS_NAME = "Token";

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
	
	@SuppressWarnings("unchecked")
	public Scriptable jsGet_host() {
		Place host = getAdaptedObject().getHost();
		if (host != null) {
			BaseJSAdapter<Place> result = (BaseJSAdapter<Place>) Context
					.getCurrentContext().newObject(getParentScope(), JSPlace.CLASS_NAME);
			result.setAdaptedObject(host);
	
			return result;
		} else {
			return null;
		}
	}
	
	// workaround
	public void jsSet_host(JSPlace place) {
		Place p = place.getAdaptedObject();
		getAdaptedObject().setHost(p);
	}
	
	public Scriptable jsGet_chars() {
		Scriptable result = getGnList(getAdaptedObject().getChars(), JSCharacteristic.CLASS_NAME);
		// auto detect type from value (JavaScript has typeof that returns number/string/boolean/object (arrays are objects)
		// TODO allow deleting chars?
		return result;
	}
	
	public JSCharacteristic jsGet_Default() {
		// .default maybe won't work, so we use .Default
		Characteristic defaultChar = getAdaptedObject().getDefaultCharacteristic();
		if (defaultChar == null) {
			return null;
		}
		JSCharacteristic result = new JSCharacteristic();
		result.setAdaptedObject(defaultChar);
		result.setReadOnly(isReadOnly());
		return result;
	}

	public JSCharacteristic jsFunction_addChar(String id, String type, int history) {
		if (isReadOnly()) {
			// TODO throw ECMA error
			return null;
		}
		
		// TODO auto detect type from value? use "double" for booleans
		// TODO if history is undefined?
		Characteristic characteristic = new Characteristic(type, history);
		characteristic.setId(id);
		getAdaptedObject().getChars().add(characteristic);
		
		JSCharacteristic result = new JSCharacteristic();
		result.setAdaptedObject(characteristic);
		result.setReadOnly(isReadOnly());
		return result;
	}
	
	public void jsFunction_delChar(String id) {
		if (isReadOnly()) {
			// TODO throw ECMA error
			return;
		}
		
		GnList<Characteristic> chars = getAdaptedObject().getChars();
		Characteristic characteristic = chars.get(id);
		if (characteristic != null) {
			chars.remove(characteristic);
		}
	}
	
	public void jsFunction_split(String id) {
		if (isReadOnly()) {
			// TODO throw ECMA error
			return;
		}
		
		// TODO clone token and chars, put in the same place (if capacity allows)
	}
	
}
