package net.generalised.genedit.api;

import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;

public class JavaCharacteristic {

	private final Characteristic characteristic;
	
	public JavaCharacteristic(Characteristic characteristic) {
		this.characteristic = characteristic;
	}
	
	public String getName() {
		return this.characteristic.getName();
	}
	
	public String getType() {
		return this.characteristic.getType();
	}
	
	public String getValue() {
		return this.characteristic.getValue();
	}
	
	public void setValue(String value) {
		this.characteristic.setValue(value);
	}
	
	public List<String> getHistory() {
		return this.characteristic.getHistoryValues().getValues();
	}

}
