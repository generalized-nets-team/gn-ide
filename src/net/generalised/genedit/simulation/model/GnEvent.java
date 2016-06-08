package net.generalised.genedit.simulation.model;

import java.util.List;

import net.generalised.genedit.baseapp.model.Command;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Token;

public abstract class GnEvent extends Command {
	protected GeneralizedNet gn;
	protected Token token;
	protected List<Characteristic> characteristics;
	
	public GnEvent(GeneralizedNet gn, Token token, List<Characteristic> characteristics) {
		this.gn = gn;
		this.token = token;
		this.characteristics = characteristics;
	}
	
	//abstract void process();
	
	protected void setCharacteristics() {
		for (Characteristic ch : characteristics) {
			token.getChars().add(ch);
		}
	}
	
	public Token getToken() {
		return token;
	}
	
	public List<Characteristic> getChars() {
		return characteristics;
	}
}
