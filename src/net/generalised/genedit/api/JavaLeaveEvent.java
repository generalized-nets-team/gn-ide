package net.generalised.genedit.api;

import net.generalised.genedit.simulation.model.LeaveEvent;

public class JavaLeaveEvent extends JavaGnEvent {

	public JavaLeaveEvent(LeaveEvent event) {
		super(event);
	}

	public JavaPlace getPlace() {
		return new JavaPlace(((LeaveEvent) event).getPlace());
	}
	
}
