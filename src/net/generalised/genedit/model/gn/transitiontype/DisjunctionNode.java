package net.generalised.genedit.model.gn.transitiontype;

import net.generalised.genedit.model.gn.GeneralizedNet;

public class DisjunctionNode extends TransitionTypeMultiNode {

	public static final String SYMBOL = "or";
	
	@Override
	protected String getOperationSymbol() {
		return SYMBOL;
	}

	public boolean evaluate(GeneralizedNet gn) {
		for (TransitionTypeNode child : this.getChildren()) {
			if (child.evaluate(gn)) {
				return true;
			}
		}
		return false;
	}

}
