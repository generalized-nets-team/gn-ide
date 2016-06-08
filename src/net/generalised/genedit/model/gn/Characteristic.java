package net.generalised.genedit.model.gn;

import java.util.List;

import net.generalised.genedit.baseapp.StringUtil;
import net.generalised.genedit.model.common.IntegerInf;

public class Characteristic extends GnObjectWithId implements Cloneable {
	
	private String type;//TODO: String or enum?
	
	private CharacteristicHistory historyValues;

	public static final String DEFAULT = "Default";

	public String getName() {
		return getId();
	}

	public void setName(String name) {
		setId(name);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (! this.type.equals(type)) {
			historyValues = new CharacteristicHistory(historyValues.getCapacity());
				//TODO: or throw exception, if during simulation? 
			this.type = type;
		}
	}
	
	public CharacteristicHistory getHistoryValues() {
		return historyValues;// TODO: return immutable!
	}

//	public void setHistoryValues(CharacteristicHistory values) {
//		if (values == null) {
//			throw new NullPointerException();
//		}
//		this.historyValues = values;
//	}

	public Characteristic(String type, IntegerInf history) {
		//this.name = DEFAULT;
		setId(DEFAULT);
		StringUtil.assertNotEmpty(type, "characteristic type");
		this.type = type;
		this.historyValues = new CharacteristicHistory(history);
	}

	public Characteristic(String type, int history) {
		this(type, new IntegerInf(history));
	}
	
	public String getValue() {
		List<String> values = historyValues.getValues();
		if (values.size() > 0)
			return values.get(values.size() - 1);
		return "";
	}
	
	public void setValue(String value) {//TODO: if we call set, do we need keeping history at all? why not delete all old values?
		historyValues.setTopValue(value);
	}
	
	public void pushValue(String value) {
		historyValues.add(value);
	}
	
	public IntegerInf getHistory() {
		return historyValues.getCapacity();
	}
	
	public void setHistory(IntegerInf value) {
		this.historyValues = new CharacteristicHistory(value);
		//TODO: da pazim li starite stoinosti?
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Characteristic result = (Characteristic) super.clone();
		result.historyValues = (CharacteristicHistory) this.historyValues.clone();
		return result;
	}
	
//	public Characteristic clone() {
//		Characteristic characteristic = new Characteristic(this.type, this.historyValues.getCapacity());
//		characteristic.name = this.name;
//		characteristic.historyValues = this.historyValues.clone();
//		return characteristic;
//	}
}
