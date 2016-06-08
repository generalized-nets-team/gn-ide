package net.generalised.genedit.baseapp.model;


/**
 * @author Dimitar Dimitrov
 *
 */
public class ListCommand extends Command {

	public enum ListChange {
		ADD,
		REMOVE,
		SET
	}
	
	private BaseModel oldValue;
	private int index;
	private final ListChange type;
	
	private ListCommand(String description, BaseModelList<?> target, ListChange type, BaseModel value,
			int index) {
		super(description);
		this.setAffectedObject(target);
		this.oldValue = value;
		if (type == ListChange.REMOVE && value != null) {
			this.index = -1;
			// this.index = target.indexOf(value); // not now!
		} else if (type == ListChange.ADD && index < 0) {
			this.index = -1; //target.size();
		} else {
			this.index = index;
		}
		this.type = type;
	}

	/**
	 * @param description
	 * @param target
	 * @param type
	 * @param value
	 *     ... in case of REMOVE: either value or index can be set, but not both (if both - the element has priority over the index)!
	 * @param index ... if type == ADD, this can be negative - that means the last element. 
	 */
	public static <T extends BaseModel> ListCommand create(String description, BaseModelList<T> target, ListChange type, T value, int index) {
		return new ListCommand(description, target, type, value, index);
	}
	
	public static <T extends BaseModel> ListCommand create(BaseModelList<T> target, ListChange type, T value, int index) {
		return new ListCommand("List change", target, type, value, index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BaseModelList<BaseModel> getAffectedObject() {
		return (BaseModelList<BaseModel>) super.getAffectedObject();
	}
	
	@Override
	public void execute() {
		switch (type) {
		case ADD:
			if (index < 0) {
				index = getAffectedObject().size();
			}
			getAffectedObject().add(index, oldValue);
			break;
		case REMOVE:
			if (index < 0) {
				index = getAffectedObject().indexOf(oldValue);
			}
			oldValue = getAffectedObject().remove(index);
			break;
		case SET:
			swap();
			break;
		}
	}

	@Override
	public void unExecute() {
		switch (type) {
		case ADD:
			getAffectedObject().remove(index);
			break;
		case REMOVE:
			getAffectedObject().add(index, oldValue);
			break;
		case SET:
			swap();
			break;
		}
	}
	
	private void swap() {
		BaseModelList<BaseModel> target = getAffectedObject();
		BaseModel currValue = target.get(index);
		target.set(index, oldValue);
		oldValue = currValue;
	}

}
