package net.generalised.genedit.baseapp.model;

import java.beans.Expression;
import java.beans.Statement;

import net.generalised.genedit.baseapp.BaseObject;
import net.generalised.genedit.baseapp.StringUtil;


/**
 * ............
 * All fields should be either immutable or of subclass of {@link BaseModel}.
 * or getters return unmodifiable collections...?  
 * (obsolete) //All setter methods must call registerChange();
 * Setter methods should not have side effects that are not undoable, if you want to use {@link PropertyChangeCommand}.
 * It's not allowed to have methods of type setXxx(int) and setXxx(Integer)...
 * 
 * @author Dimitar Dimitrov
 *
 */
public abstract class BaseModel extends BaseObject { //FIXME: most BaseModels are not Observables!!!
	//rename to ModelObject?
	
	public <T> T get(String property, Class<T> returnType) {
		StringUtil.assertNotEmpty(property, "property name");
		String capitalized = capitalizeFirstLetter(property);
		Expression expression = new Expression(this, "get" + capitalized, null);
		Object returnValue = null;
		try {
			expression.execute();
			returnValue = expression.getValue();
		} catch (Exception e) {
			//if boolean
			expression = new Expression(this, "is" + capitalized, null);
			try {
				expression.execute();
				returnValue = expression.getValue();
			} catch (Exception e1) {
				throw new RuntimeException(e);
			}
		}
		
		T result = returnType.cast(returnValue);
		return result;
	}
	
	public void set(String property, Object value) {
		StringUtil.assertNotEmpty(property, "property name");
		String capitalized = capitalizeFirstLetter(property);
		Statement statement = new Statement(this, "set" + capitalized, new Object[]{value});
		try {
			statement.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		//aspect-oriented?
		//if @Own (@AutoChild) - set the parent = this, a na predi6nata stoinost setParent(null)
	}

	private static String capitalizeFirstLetter(String string) {
		if (StringUtil.isNullOrEmpty(string)) {
			return string;
		}
		String result = Character.toUpperCase(string.charAt(0)) + string.substring(1);; 
		return result;
	}

	public void undoableSet(String property, Object value) {
		execute(new PropertyChangeCommand(this, property, value));
	}
	
	public void execute(Command command) {
		ModelWithHistory model = getParent(ModelWithHistory.class);
		if (model == null) {
			throw new UnsupportedOperationException(
					"Cannot execute commands on objects that have no parent of type ModelWithHistory");
		}
		model.execute(command);
	}

	@Override
	public void setParent(BaseObject parent) {
		if (parent != null && ! (parent instanceof BaseModel)) {
			throw new IllegalArgumentException("parent of BaseModel can be only BaseModel or null");
		}
		super.setParent(parent);
	}
	
	@Override
	public BaseModel getParent() {
		return (BaseModel) super.getParent();
	}
	
	//TODO: clone, equals...
}
