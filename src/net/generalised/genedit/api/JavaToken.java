package net.generalised.genedit.api;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GnList;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;

public class JavaToken {

	private final Token token;
	
	public JavaToken(Token token) {
		this.token = token;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof JavaToken)) return false;
		return this.token.getId().equals(((Token)obj).getId());
	}
	
	public String getId() {
		return this.token.getId();
	}
	
	public int getPriority() {
		return this.token.getPriority();
	}
	
	public JavaPlace getHost() {
		Place place = this.token.getHost();
		if (place != null) return new JavaPlace(place);
		return null;
	}

	public List<JavaCharacteristic> getChars() {
		GnList<Characteristic> chars = token.getChars();
		List<JavaCharacteristic> result = new ArrayList<JavaCharacteristic>();
		for (Characteristic characteristic : chars) {
			result.add(new JavaCharacteristic(characteristic));
		}
		return result;
	}

	public JavaCharacteristic getDefault() {
		Characteristic defaultChar = token.getDefaultCharacteristic();
		if (defaultChar != null) {
			return new JavaCharacteristic(defaultChar);
		}
		return null;
	}
	
	public JavaCharacteristic getChar(String id) {
		Characteristic characteristic = this.token.getChars().get(id);
		return characteristic != null ? new JavaCharacteristic(characteristic) : null;
	}
	
	public JavaCharacteristic addChar(String id, String type, int history) {
		Characteristic characteristic = new Characteristic(type, history);
		characteristic.setId(id);
		this.token.getChars().add(characteristic);
		return new JavaCharacteristic(characteristic);
	}
	
	public void delChar(String id) {
		GnList<Characteristic> chars = this.token.getChars();
		Characteristic characteristic = chars.get(id);
		if (characteristic != null) {
			chars.remove(characteristic);
		}
	}
	
}
