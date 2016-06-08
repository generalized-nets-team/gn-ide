package net.generalised.genedit.api;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.simulation.model.GnEvent;

public abstract class JavaGnEvent {

	protected final GnEvent event;
	
	public JavaGnEvent(GnEvent event) {
		this.event = event;
	}
	
	public JavaToken getToken() {
		return new JavaToken(event.getToken());
	}
	
	public List<JavaCharacteristic> getChars() {
		List<JavaCharacteristic> result = new ArrayList<JavaCharacteristic>();
		for (Characteristic characteristic : event.getChars()) {
			result.add(new JavaCharacteristic(characteristic));
		}
		return result;
	}
}
