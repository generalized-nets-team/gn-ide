package net.generalised.genedit.baseapp.controller;

import net.generalised.genedit.baseapp.view.BaseView;

/**
 * @author Dimitar Dimitrov
 */
public abstract class Event {

	private BaseView source;
	
	// for the reflection
	public Event() {
	}
	public void setSource(BaseView source) {
		this.source = source;
	}
	
	public Event(BaseView source) {
		super();
		this.source = source;
	}

	public BaseView getSource() {
		return source;
	}
	
}