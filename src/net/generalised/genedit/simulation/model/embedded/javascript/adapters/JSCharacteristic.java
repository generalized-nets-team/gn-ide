package net.generalised.genedit.simulation.model.embedded.javascript.adapters;

import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.CharacteristicHistory;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.simulation.model.embedded.javascript.JavaScriptRunner;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JSCharacteristic extends BaseJSAdapter<Characteristic> {

	private static final long serialVersionUID = 1145502412473335492L;

	/**
	 * The class name that JavaScript users will see.
	 */
	public static final String CLASS_NAME = "Characteristic";

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	public String jsGet_name() {
		return getAdaptedObject().getName();
	}

	public void jsSet_name(String value) {
		getAdaptedObject().setName(value);
	}
	
	public String jsGet_type() {
		return getAdaptedObject().getType();
	}
	
	private Object parseCharValue(String value) {
		GeneralizedNet gn = getAdaptedObject().getParent(GeneralizedNet.class);
		
		// workaround
		if ("string".equals(getAdaptedObject().getType())) {
			return value;
		}
		
		Object result = JavaScriptRunner.runJavaScript(gn, "", "eval(" + value + ")", true);
		return result;
	}
	
	public Object jsGet_value() {
		String value = getAdaptedObject().getValue();
		return parseCharValue(value);
	}
	
	/**
	 * @return Unmodifiable array of all history values.
	 */
	public Scriptable jsGet_history() {
		CharacteristicHistory history = getAdaptedObject().getHistoryValues();
		List<String> values = history.getValues();
		int valuesCount = values.size();
		Object[] resultValues = new Object[valuesCount];
		for (int i = 0; i < valuesCount; i++) {
			resultValues[i] = parseCharValue(values.get(i));
		}
		return Context.getCurrentContext().newArray(getParentScope(), resultValues);
	}
	
	public void jsSet_value(Object value) {
		if (isReadOnly()) {
			// TODO throw ECMA error
			return;
		}
		
		String type = getAdaptedObject().getType();
		String valueAsString;
		if ("string".equals(type)) {
			valueAsString = (String) value;
		} else if ("vector".equals(type)) {
			// TODO
			valueAsString = "";
		} else { // double or unrecognized
			valueAsString = value.toString();
		} // TODO object?
		getAdaptedObject().pushValue(valueAsString);
	}
	
	public void jsFunction_read(String message) {
		if (isReadOnly()) {
			// TODO throw ECMA error
			return;
		}
		// TODO fire GNTP INPUT event - read data from user 
	}
}

