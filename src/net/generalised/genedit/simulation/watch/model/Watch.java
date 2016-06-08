package net.generalised.genedit.simulation.watch.model;

import java.util.List;

import org.eclipse.swt.graphics.Color;

import net.generalised.genedit.model.gn.Characteristic;

/**
 * Value object ...
 */
public class Watch {

	private Characteristic characteristic;

	private Color lineColor;
	
	// TODO Window reference/id
	// TODO X axis parameters

	public Watch(Characteristic characteristic, Color lineColor) {
		this.characteristic = characteristic;
		this.lineColor = lineColor; 
	}
	
	public Characteristic getCharacteristic() {
		return this.characteristic;
	}
	
	public boolean watchHistory() {
		return !watchSingleList(); 
	}
	
	public boolean watchSingleList() {
		return this.characteristic.getType().equals("vector"); // XXX hardcoded
		// TODO strings are not allowed; how to handle them?
	}
	
	public double[] getActualData() {
		// be careful with caching - this method should be called on update
		double[] result = null;
		if (watchSingleList()) {
			String[] stringArray = this.characteristic.getValue().split(" ");
			result = new double[stringArray.length];
			for (int i = 0; i < stringArray.length; i++) {
				result[i] = new Double(stringArray[i]).doubleValue();
			}
		} else {
			List<String> stringList = this.characteristic.getHistoryValues().getValues(); 
			result = new double[stringList.size()];
			for (int i = 0; i < result.length; i++) {
				result[i] = new Double(stringList.get(i)).doubleValue();
			}
		}
		return result;
	}
	
	public Color getLineColor() {
		return this.lineColor;
	}
}
