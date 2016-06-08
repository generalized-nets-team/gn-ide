package net.generalised.genedit.api;

import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.simulation.model.SimulationException;
import net.generalised.genedit.simulation.model.embedded.EmbeddedSimulation;

public class JavaSimulation extends EmbeddedSimulation {

	public JavaSimulation(GeneralizedNet gn) throws SimulationException {
		super(new GnDocument(gn, false), gn, JavaFunctionRunner.getInstance());
	}
}
