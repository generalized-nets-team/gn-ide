package net.generalised.genedit.simulation;

import net.generalised.genedit.baseapp.controller.ControllerRegistry;
import net.generalised.genedit.baseapp.plugins.Plugin;
import net.generalised.genedit.simulation.controller.SimulationController;

public class SimulationPlugin extends Plugin {

	@Override
	public void initialize() {
		ControllerRegistry.registerControllers(new SimulationController());
		
		setDefaultProperty(SimulationConfigProperties.TICKER_SERVER_HOST, "127.0.0.1"); //62.44.101.87
		setDefaultProperty(SimulationConfigProperties.TICKER_SERVER_PORT, "54321");
		setDefaultProperty(SimulationConfigProperties.DEFAULT_DELAY_TIME, "1000");
	}

}
