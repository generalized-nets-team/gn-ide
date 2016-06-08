package net.generalised.genedit.baseapp.view;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.generalised.genedit.baseapp.BaseObject;
import net.generalised.genedit.baseapp.controller.ControllerRegistry;
import net.generalised.genedit.baseapp.controller.Event;
import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.model.BaseObservableImpl;
import net.generalised.genedit.baseapp.model.BaseObserver;
import net.generalised.genedit.controller.ViewUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * ...since it is an {@link Observable} too, all modifications should be followed by {{@link #notifyObservers(Object)}...
 * ...when a view notifies its observers, its parent notifies its observers too, and so on. This is because when we observe a given view, we might also want to observe its children too.
 * 
 * @author Dimitar Dimitrov
 *
 */
public abstract class BaseView extends BaseObject implements BaseObservable, BaseObserver {

	private final List<BaseView> children = new ArrayList<BaseView>();
	// BaseObservable - ModelWithHistory and BaseObject
	private final List<WeakReference<BaseObservable>> observables = new ArrayList<WeakReference<BaseObservable>>();
	private Widget uiComponent;//TODO: Widget or Control or?
	private final BaseObservableImpl observableHelper;
	
	/**
	 * ...
	 * @param parent ...can be null for the root view only.
	 */
	// FIXME: creation of SWT component requires the parent component! it cannot be attached later!
	public BaseView(BaseView parent, BaseObservable... observables) {
		setParent(parent);
		uiComponent = null;
		for (BaseObservable observable : observables) {//all BaseObjects or only BaseModels? ako slojim BaseView-ta - kato iz4eznat, kak 6te gi gepim novite?
			addAsObserverTo(observable);
		}
		this.observableHelper = new BaseObservableImpl();
		//TODO: TOVA ne e dobre! vsi4ki notifications po modela idvat ot Document! to nqma i logika da e inak (primerno FunctionView moje da iska da znae i GN kakvi ne6ta ima)
		//e dobre, za6to togava BaseObject e Observable???
		//oba4e view-tata, koito nabliudavame...
		//! ne vsqko view se interesuva ot Document, 4e da go gleda; to pyk ne mojem ottuk direktno da vzemem current doc, 6toto e app zavisim;
		//i sega kakvo - trqbva da podadem i Doc, i GN?
	}
	
	/**
	 * @return the children
	 */
	public List<BaseView> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public void setParent(BaseObject parent) {
		if (parent != null && !(parent instanceof BaseView)) {
			throw new IllegalArgumentException("parent of BaseView can only be a BaseView");
		}
		if (getParent() != null) {
			assert getParent() instanceof BaseView;
			getParent().children.remove(this);
		}
		super.setParent(parent);
		if (parent != null) {
			getParent().children.add(this);
		}
	}
	
	@Override
	public BaseView getParent() {
		return (BaseView) super.getParent();
	}
	
	public void addChild(BaseView view) {
		if (children.contains(view)) {
			throw new IllegalArgumentException("view already is a child");
		}
		view.setParent(this);
		//children.add(view);
	}
	
	public void removeChild(BaseView view) {
		view.setParent(null);
	}
	
	public Widget getUIComponent() {
		return uiComponent;
	}
	
	public void initUIComponent(Widget parent) {
		if (uiComponent == null) {
			uiComponent = createUIComponent(parent);
		} else {
			throw new IllegalStateException("initUIComponent cannot be called more than once");
			// FIXME actually you can, if previous call to createUIComponent returned null
		}
	}
	
	protected abstract Widget createUIComponent(Widget parent);
	
//	public Widget getParentUIComponent() {
//		return getParent().getUIComponent();
//	}
	
	public final void update(final BaseObservable o, final Object arg) {
		if (o != null) { //FIXME: tova e pak zaradi skapanata ni ierarhiq ot BaseView-ta i UI componenti
//			if (o instanceof BaseObject) {
				//TODO: getCurrent() ? NullPointerException
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						//TODO: with reflection select some of the update methods???
						//@Updater methodName(X extends BaseObject, Object arg)
						syncUpdate(o, arg);
					}
				});
			// FIXME: Selection is not a BaseObject!!!
//			} else {
//				throw new IllegalArgumentException("unsupported observable type: " + o.getClass().getName());
//			}
		}
	}
	
	protected void syncUpdate(BaseObservable observable, Object argument) { //XXX no asyncUpdate?
		// default implementation - do nothing
	}
	
	public void addAsObserverTo(BaseObservable observable) {
		observable.addObserver(this);
		observables.add(new WeakReference<BaseObservable>(observable)); //TODO: check for duplicates? linear search
	}
	
	public void dispose() {
		for (BaseView child : children) {
			child.dispose();
		}
		children.clear();
		if (getUIComponent() != null) {
			getUIComponent().dispose();
		}
		for (WeakReference<BaseObservable> observable : observables) {
			if (observable.get() != null) {
				observable.get().deleteObserver(this);
			}
		}
		observables.clear();
	}

	public void addObserver(BaseObserver observer) {
		observableHelper.addObserver(observer);
	}

	public int countObservers() {
		return observableHelper.countObservers();
	}

	public void deleteObserver(BaseObserver observer) {
		observableHelper.deleteObserver(observer);
	}

	public void deleteObservers() {
		observableHelper.deleteObservers();
	}

	public void notifyObservers() {
		observableHelper.notifyObservers();
	}

	public void notifyObservers(Object arg) {
		observableHelper.notifyObservers(arg);
		if (getParent() != null) {
			getParent().notifyObservers(arg);
		}
	}

	//TODO: set zoom, add problem - from root or directly?
	
	/**
	 * Fires an {@link Event) with this object as source.
	 */
	public boolean dispatchEvent(Class<? extends Event> eventType) {
		Event event;
		try {
			event = eventType.newInstance();
			event.setSource(this);
			return ControllerRegistry.dispatchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			ViewUtil.showExceptionMessageBox(e);
		}
		return false;
	}
	
	public boolean dispatchEvent(Event event) {
		event.setSource(this);
		return ControllerRegistry.dispatchEvent(event);
	}
}
