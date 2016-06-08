package net.generalised.genedit.baseapp.controller;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import net.generalised.genedit.controller.ViewUtil;

/**
 * 
 * 
 * @author Dimitar Dimitrov
 */
public class ControllerRegistry {

//	private static class EventHandler {
//		private Object controller;
//		
//		private Method method;
//
//		public EventHandler(Object controller, Method method) {
//			super();
//			this.controller = controller;
//			this.method = method;
//		}
//
//		public Object getController() {
//			return controller;
//		}
//
//		public Method getMethod() {
//			return method;
//		}
//		
//	}
//	
//	private static final Map<Enum<?>, EventHandler> controllers = 
//		new HashMap<Enum<?>, EventHandler>();
	
	private static final Map<Class<? extends Event>, BaseController<?>> controllers = 
		new Hashtable<Class<? extends Event>, BaseController<?>>(); 
	
	@SuppressWarnings("unchecked")
	public static void registerControllers(Object controllersGroup) {
	//public static void registerControllers(Class<?> controllersGroupClass) {
		
//		if (! controllersGroupClass.isEnum() || controllersGroupClass.getEnumConstants() == null) {
//			//TODO: opravi logikata
//			for (Class<?> c : controllersGroupClass.getClasses()) {
//				if (BaseController.class.isAssignableFrom(c)) {
//					try {
//						BaseController bc = (BaseController) c.newInstance();//TODO: be careful
//						registerController(bc);
//					} catch (InstantiationException e1) {
//						throw new RuntimeException(e1);
//					} catch (IllegalAccessException e1) {
//						throw new RuntimeException(e1);
//					}
//				}
//			}
//
////			throw new IllegalArgumentException(
////					"Invalid controller class. Should be an enum with fields implementing "
////							+ BaseController.class.getSimpleName());
//		} else {
//			Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) controllersGroupClass; 
//			for (Enum<?> e : enumClass.getEnumConstants()) {
//				if (! (e instanceof BaseController)) {
//					throw new IllegalArgumentException(
//							"All enum items should implement "
//									+ BaseController.class.getSimpleName());
//				}
//				BaseController bc = (BaseController) e;
//				registerController(bc);
//			}
//		}
		for (Method method : controllersGroup.getClass().getMethods()) {
			if (method.isAnnotationPresent(Controller.class)) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length < 1 || parameterTypes.length > 2) {
					throw new IllegalArgumentException("controller methods should have 1 or 2 arguments");
				}
				//TODO: check the types of the arguments!
				Class<? extends Event> eventType = (Class<? extends Event>) parameterTypes[0];
				ReflectionController<?> controller = new ReflectionController(
						controllersGroup, method, eventType);
				//controllers.put(eventType, controller);
				registerController(controller);
			}
		}

	}
	
	public static <T extends Event> void registerController(BaseController<T> controller) {
		if (controller == null || controller.getHandledEventType() == null) {
			throw new NullPointerException("controller and id of handled events cannot be null");
		}
		Class<T> eventType = controller.getHandledEventType();
		if (controllers.get(eventType) != null) {
			System.out.println("Warning: Replacing existing controller for: "
					+ eventType.toString());
		}
		controllers.put(controller.getHandledEventType(), controller);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Event> boolean dispatchEvent(T event) {
		BaseController<T> controller = (BaseController<T>) controllers.get(event.getClass());
		if (controller != null) {
			try {
				return controller.handle(event);
				// TODO and what if false?
			} catch (Exception e) {
				e.printStackTrace();
				ViewUtil.showExceptionMessageBox(e); // TODO better message
			}
		}
		ViewUtil.showErrorMessageBox("Unhandled event " + event.getClass().getName());
		return false;
	}
}
