package net.generalised.genedit.baseapp.model;

import net.generalised.genedit.baseapp.StringUtil;

/**
 * ...
 * 
 * @author Dimitar Dimitrov
 *
 */
public class PropertyChangeCommand extends Command {

	// TODO: undo of this command may not be accurate if setters have side effects, like in Function
	
	private Object oldValue;
	private final String property;
	
	public PropertyChangeCommand(String description, BaseModel target, String property, Object newValue) {
		super(description);

		if (target == null) {
			throw new IllegalArgumentException("target cannot be null");
		}
		StringUtil.assertNotEmpty(property, "property name");
		
		super.setAffectedObject(target);
		this.property = property;
		this.oldValue = newValue;
	}
	
	public PropertyChangeCommand(BaseModel target, String property, Object newValue) {
		this("Change " + property + " in " + target.getClass().getSimpleName(), target, property, newValue);
	}
	
	@Override
	public void execute() {
		swap();
	}

	@Override
	public void unExecute() {
		swap();
	}
	
	private void swap() {
		BaseModel target = getAffectedObject();
		Object currValue = target.get(property, Object.class);
		target.set(property, oldValue);
		oldValue = currValue;
	}
	
	@Override
	public BaseModel getAffectedObject() {
		if (property != "parent") { //FIXME: hardcoded string
			return super.getAffectedObject();
		}
		return null; //changing parent affects at least two objects
	}
	
	@Override
	public void setAffectedObject(BaseModel affectedObject) {
//		if (affectedObject == null) {
//			throw new IllegalArgumentException("affected object cannot be null");
//		}
		if (/*getAffectedObject() != null && */affectedObject != getAffectedObject()) {
			throw new UnsupportedOperationException("affected object cannot be changed");
		}
	}

}
