package net.generalised.genedit.api;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.model.BaseObserver;
import net.generalised.genedit.simulation.model.EnterEvent;
import net.generalised.genedit.simulation.model.GnEvent;
import net.generalised.genedit.simulation.model.GnEvents;
import net.generalised.genedit.simulation.model.LeaveEvent;
import net.generalised.genedit.simulation.model.MoveEvent;

public abstract class SimulationEventsListener {

	private final BaseObserver observer;
	
	public SimulationEventsListener() {
		this.observer = new BaseObserver() {
			
			public void update(BaseObservable o, Object arg) {
				if (arg instanceof GnEvents) {
					List<JavaGnEvent> safeEvents = new ArrayList<JavaGnEvent>();
					for (GnEvent event : ((GnEvents) arg).getEvents()) {
						if (event instanceof EnterEvent)
							safeEvents.add(new JavaEnterEvent((EnterEvent) event));
						else if (event instanceof MoveEvent)
							safeEvents.add(new JavaMoveEvent((MoveEvent) event));
						else if (event instanceof LeaveEvent)
							safeEvents.add(new JavaLeaveEvent((LeaveEvent) event));
					}
					handleEvent(safeEvents);
				}
			}
		};
	}
	
	// default visibility
	BaseObserver getObserver() {
		return this.observer;
	}

	public abstract void handleEvent(List<JavaGnEvent> events);
}
