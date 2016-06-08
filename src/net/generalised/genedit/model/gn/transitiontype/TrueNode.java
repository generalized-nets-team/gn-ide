package net.generalised.genedit.model.gn.transitiontype;

import net.generalised.genedit.model.gn.GeneralizedNet;

public class TrueNode implements TransitionTypeNode {

	public boolean evaluate(GeneralizedNet gn) {
		return true;
	}
	
	@Override
	public String toString() {
		return "true";
	}

}
