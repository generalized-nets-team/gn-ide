package net.generalised.genedit.model.gn.transitiontype;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnUtil;
import net.generalised.genedit.model.gn.Token;

public class PlaceNode implements TransitionTypeNode {

	private String placeId;
	
	public PlaceNode() {
	}
	
	public PlaceNode(String placeId) {
		setPlaceId(placeId);
	}
	
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	
	public String getPlaceId() {
		return this.placeId;
	}
	
	public boolean evaluate(GeneralizedNet gn) {
		Token someToken = GnUtil.getSingleTokenAt(gn, placeId);
		return someToken != null;
	}
	
	@Override
	public String toString() {
		return this.placeId;
	}

}
