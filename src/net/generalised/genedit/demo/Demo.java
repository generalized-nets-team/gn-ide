package net.generalised.genedit.demo;

import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.simulation.model.Simulation;

public interface Demo {

	public String getGnXml();
	
	public Simulation createSimulation(GnDocument document);
	
}
