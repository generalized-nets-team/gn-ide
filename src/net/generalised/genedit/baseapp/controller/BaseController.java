package net.generalised.genedit.baseapp.controller;

/**
 * Interface for all controllers. 
 * 
 * @author Dimitar Dimitrov.
 *
 */
public interface BaseController<T extends Event> {

	public Class<T> getHandledEventType(); // XXX new subclass for every new event
	
	public boolean handle(T event);
}
