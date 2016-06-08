package net.generalised.genedit.baseapp.model;

/**
 * @author Dimitar Dimitrov
 *
 */
public interface BaseObservable {
	
	public void addObserver(BaseObserver observer);
	
	public void deleteObserver(BaseObserver observer);
	
	public void notifyObservers();
	
	public void notifyObservers(Object arg);
	
	public void deleteObservers();
	
	public int countObservers();
}
