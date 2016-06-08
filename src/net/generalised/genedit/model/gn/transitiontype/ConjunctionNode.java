package net.generalised.genedit.model.gn.transitiontype;

import net.generalised.genedit.model.gn.GeneralizedNet;

public class ConjunctionNode extends TransitionTypeMultiNode {

	public static final String SYMBOL = "and";
	
	@Override
	protected String getOperationSymbol() {
		return SYMBOL;
	}

	public boolean evaluate(GeneralizedNet gn) {
		for (TransitionTypeNode child : this.getChildren()) {
			if (! child.evaluate(gn)) {
				return false;
			}
		}
		return true;
	}

}
