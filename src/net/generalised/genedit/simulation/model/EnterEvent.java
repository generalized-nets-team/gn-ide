package net.generalised.genedit.simulation.model;

import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;

public class EnterEvent extends GnEvent {
	protected final Place place;
	
	public EnterEvent(GeneralizedNet gn, Token token, Place place, List<Characteristic> characteristics) {
		super(gn, token, characteristics);
		this.place = place;
	}
	
	public Place getPlace() {
		return place;
	}

	public void execute() {
		gn.enterToken(token, place);
		setCharacteristics();
	}
	
	public void unExecute() {
		
	}

}
