package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.generalised.genedit.model.common.IntegerInf;

//TODO: do we need this? use the undo-redo history... but be careful with the history parameter!
public class CharacteristicHistory implements Cloneable {
	private int capacity;
	
	private ArrayList<String> values;
	// TODO: times!

	public IntegerInf getCapacity() {
		return new IntegerInf(capacity);
	}
	
	public List<String> getValues() {
		return Collections.unmodifiableList(values);
	}

	public void setTopValue(String value) {
		if (values.size() == 0)
			values.add(value);
		else values.set(0, value); // XXX is 0 the top index?
	}
	
	public void add(String value) {
		if(values.size() == capacity)
			values.remove(0); 
		values.add(value);
	}

	public CharacteristicHistory(IntegerInf capacity) {
		this.capacity = capacity.isPositiveInfinity() ? Integer.MAX_VALUE : capacity.getValue();
		this.values = new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object clone() throws CloneNotSupportedException {
		CharacteristicHistory charHistory = (CharacteristicHistory)super.clone();
		charHistory.values = (ArrayList<String>)this.values.clone(); 
		return charHistory;
	}
}
