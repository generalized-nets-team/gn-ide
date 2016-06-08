package net.generalised.genedit.simulation.model.embedded;

import net.generalised.genedit.model.gn.GeneralizedNet;

import org.junit.Test;

public class EmbeddedSimulationTest {

	@Test
	public void testSimple() {
		GeneralizedNet gn = new GeneralizedNet("test");
		// TODO construct
		
		gn.prepareForSimulation();
		
		gn.restoreState();
	}
}
