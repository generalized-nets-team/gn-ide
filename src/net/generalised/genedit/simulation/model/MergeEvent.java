package net.generalised.genedit.simulation.model;

import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;

public class MergeEvent extends GnEvent {

	private Token oldToken;
	private Token newToken;
	private Place place;
	
	public MergeEvent(GeneralizedNet gn, Place place, Token newToken, Token oldToken,
			List<Characteristic> characteristics) {
		super(gn, null, characteristics);
		this.place = place;
		this.oldToken = oldToken;
		this.newToken = newToken;
	}

	@Override
	public void execute() {
		token = newToken;
		setCharacteristics();
		gn.leaveToken(oldToken);
	}

	@Override
	public void unExecute() {
		gn.enterToken(oldToken, place);
		// TODO chars
	}

}
