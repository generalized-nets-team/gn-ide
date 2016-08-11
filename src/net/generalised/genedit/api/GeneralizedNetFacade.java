package net.generalised.genedit.api;

import java.util.List;

import net.generalised.genedit.simulation.model.SimulationException;

public class GeneralizedNetFacade {

	public static JavaSimulation startSimulation(JavaGeneralizedNet gn, SimulationEventsListener listener) {
		try {
			JavaSimulation simulation = new JavaSimulation(gn.getWrappedGn());
			simulation.getDocument().addObserver(listener.getObserver());
			simulation.start();
			return simulation;
		} catch (SimulationException exception) {
			return null;
		}
	}
	
	public static JavaSimulation startSimulation(JavaGeneralizedNet gn) {
		return startSimulation(gn, new SimulationEventsListener() {
			@Override
			public void handleEvent(List<JavaGnEvent> event) {
				// do nothing
			}
		});
	}
}
