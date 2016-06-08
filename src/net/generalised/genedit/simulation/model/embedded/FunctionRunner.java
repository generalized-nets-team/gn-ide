package net.generalised.genedit.simulation.model.embedded;

import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Token;

public interface FunctionRunner {

	public String getFunctionLanguage();
	
	public Object run(FunctionReference function, GeneralizedNet gn, Token token);
	
}
