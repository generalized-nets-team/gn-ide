package net.generalised.genedit.simulation.model;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.baseapp.model.Command;
import net.generalised.genedit.model.gn.GeneralizedNet;

public class GnEvents extends Command { //hmmm... CompositeCommand?
	private List<GnEvent> events;
	private GeneralizedNet gn;
	
	public GnEvents(GeneralizedNet gn) {
		events = new ArrayList<GnEvent>();
		this.gn = gn;
	}
	
	public void add(GnEvent event) {
		events.add(event);
	}
	
	public List<GnEvent> getEvents() {
		return this.events;
	}
	
	//public void processAll() {
	public void execute() {
		for (GnEvent event : events) { //TODO: check the order!
			event.execute();
			//TODO: delay! (gn parameter) or only for different steps?
		}
		gn.increaseTime();
	}
	
	public void unExecute() {
		//TODO:...
		gn.decreaseTime();
	}
}
