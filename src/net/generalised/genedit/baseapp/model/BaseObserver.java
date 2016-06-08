package net.generalised.genedit.baseapp.model;

/**
 * @author Dimitar Dimitrov
 *
 */
public interface BaseObserver {

	void update(BaseObservable o, Object arg);

}
