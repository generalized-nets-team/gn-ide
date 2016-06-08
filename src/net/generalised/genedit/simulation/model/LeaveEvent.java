package net.generalised.genedit.simulation.model;

import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;

public class LeaveEvent extends GnEvent {
	protected Place place; //never used

	public LeaveEvent(GeneralizedNet gn, Token token, Place place, List<Characteristic> characteristics) {
		super(gn, token, characteristics);
		this.place = place;
	}
	
	public Place getPlace() {
		return place;
	}

	public void execute() {
		gn.leaveToken(token);
		setCharacteristics();
	}

	public void unExecute() {
		gn.enterToken(token, place);
		//TODO: chars...
	}

}
