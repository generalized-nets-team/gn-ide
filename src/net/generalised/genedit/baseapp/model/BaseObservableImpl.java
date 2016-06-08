package net.generalised.genedit.baseapp.model;

import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * 
 * .... WeakReference...
 * 
 * @author Dimitar Dimitrov
 *
 */
public class BaseObservableImpl implements BaseObservable {

	private Vector<WeakReference<BaseObserver>> obs;
	
	public BaseObservableImpl() {
		obs = new Vector<WeakReference<BaseObserver>>();
	}
	
    public synchronized void addObserver(BaseObserver observer) {
        if (observer == null) {
            throw new NullPointerException();
        }
        boolean contains = false;
        for (WeakReference<BaseObserver> ob : obs) {
        	if (ob.get().equals(observer)) {
        		contains = true;
        		break;
        	}
        }
		if (! contains) {
		    obs.addElement(new WeakReference<BaseObserver>(observer));
		}
    }

	public synchronized int countObservers() {
		return obs.size();
	}

	public synchronized void deleteObserver(BaseObserver observer) {
        for (WeakReference<BaseObserver> ob : obs) {
        	if (ob.get().equals(observer)) {
        		obs.remove(ob);
        		break;
        	}
        }
	}

	public synchronized void deleteObservers() {
		obs.removeAllElements();
	}

	public void notifyObservers() {
		notifyObservers(null);
	}

	public void notifyObservers(Object arg) {
		Object[] arrLocal;

		synchronized (this) {
			arrLocal = obs.toArray();
		}

		for (int i = arrLocal.length - 1; i >= 0; i--) {
			BaseObserver observer = (BaseObserver) ((WeakReference<?>) arrLocal[i]).get();
			if (observer != null) {
				observer.update(this, arg);
			} //TODO: else - add index to list of weakrefs to be removed
		}
	}

}
