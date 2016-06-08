package net.generalised.genedit.simulation.model;

import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.GeneralizedNet;

public class RecordedSimulation extends Simulation {
	//file;
	
	public RecordedSimulation(GnDocument document, GeneralizedNet gn, String fileName) throws SimulationException {
		super(document, gn);
		//TODO: open file
	}
	
	@Override
	public void closeEventSource() {
		// TODO close the file
	}

	@Override
	protected GnEvents getNextEvents(int count) {
		//old: return raw xml from file, but xml parser...
		return null;
	}
}
