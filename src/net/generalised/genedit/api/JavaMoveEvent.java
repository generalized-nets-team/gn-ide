package net.generalised.genedit.api;

import net.generalised.genedit.simulation.model.MoveEvent;

public class JavaMoveEvent extends JavaGnEvent {

	public JavaMoveEvent(MoveEvent event) {
		super(event);
	}

	public JavaPlace getStartPlace() {
		return new JavaPlace(((MoveEvent) event).getStartPlace());
	}

	public JavaPlace getEndPlace() {
		return new JavaPlace(((MoveEvent) event).getEndPlace());
	}
}
