package net.generalised.genedit.api;

import net.generalised.genedit.simulation.model.EnterEvent;

public class JavaEnterEvent extends JavaGnEvent {

	public JavaEnterEvent(EnterEvent event) {
		super(event);
	}

	public JavaPlace getPlace() {
		return new JavaPlace(((EnterEvent) event).getPlace());
	}
	
}
