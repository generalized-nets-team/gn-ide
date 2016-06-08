package net.generalised.genedit.simulation.model;

public class SimulationException extends Exception {

	private static final long serialVersionUID = 5866535515901576728L;

	public SimulationException() {
	}

	public SimulationException(String message) {
		super(message);
	}

	public SimulationException(Throwable cause) {
		super(cause);
	}

	public SimulationException(String message, Throwable cause) {
		super(message, cause);
	}

}
