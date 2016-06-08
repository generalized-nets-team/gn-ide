package net.generalised.genedit.simulation.model;

import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;

public class MoveEvent extends GnEvent {
	protected Place from;
	protected Place to;
	//TODO: characteristics!
	
	public MoveEvent(GeneralizedNet gn, Token token, Place to, List<Characteristic> characteristics) {
		super(gn, token, characteristics);
		this.from = token.getHost();
		this.to = to;
	}

	public Place getStartPlace() {
		return from;
	}

	public Place getEndPlace() {
		return to;
	}

	public void execute() {
		gn.moveTokenTo(token, to);
		setCharacteristics();
	}
	
	public void unExecute() {
		gn.moveTokenTo(token, from);
		//TODO: chars...
	}

}
