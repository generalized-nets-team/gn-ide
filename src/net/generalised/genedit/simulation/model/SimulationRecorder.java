package net.generalised.genedit.simulation.model;

public abstract class SimulationRecorder {

	//TODO: private file, public getter for writing
	
	public SimulationRecorder(Simulation simulation, String fileName) {
		// TODO Auto-generated constructor stub
	}
	
	public abstract void start();
	//TODO: automatically stop on simulation stop
	
	public void stop() {
		//TODO: close the file
	}
	
	// even recorded simulation can be recorded again - to gif, for example
}
