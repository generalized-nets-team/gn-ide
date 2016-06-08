package net.generalised.genedit.baseapp.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Dimitar Dimitrov
 */
public class ReflectionController<T extends Event> implements BaseController<T> {

	private final Class<T> eventType;
	private final Object controller;
	private final Method controllerMethod;
	
	public ReflectionController(Object controller, Method controllerMethod, Class<T> eventType) {
		this.controller = controller;
		this.controllerMethod = controllerMethod;
		this.eventType = eventType;
	}
	
	public Class<T> getHandledEventType() {
		return eventType;
	}

	public boolean handle(T event) {
		try {
			Class<?>[] parameterTypes = controllerMethod.getParameterTypes();
			Object returnValue = null;
			if (parameterTypes.length == 1) {
				returnValue = controllerMethod.invoke(controller, event);
			} else if (parameterTypes.length == 2) { //TODO: check if param[1] is the same as getSource's class? or at least BaseView
				returnValue = controllerMethod.invoke(controller, event, event.getSource());
			} else {
				throw new RuntimeException("Invalid controller method");
			}
			
			if (returnValue instanceof Boolean) {
				return (Boolean) returnValue;
			} else {
				return true;
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
