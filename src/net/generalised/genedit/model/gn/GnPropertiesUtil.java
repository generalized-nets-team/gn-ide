package net.generalised.genedit.model.gn;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for dealing with {@link GnProperty}s.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class GnPropertiesUtil {

	private GnPropertiesUtil() {
	}

	public static Method getGetter(Class c, Field field) {
		String fieldName = field.getName();
		//if boolean
		String methodName = "get" + Character.toUpperCase(fieldName.charAt(0))
			+ fieldName.substring(1);
		try {
			Method result = c.getMethod(methodName);
			return result;
		} catch (SecurityException e) {
			e.printStackTrace(); //?
			return null;
		} catch (NoSuchMethodException e) {
			
			methodName = "is" + methodName.substring(3);
			try {
				Method result = c.getMethod(methodName);
				return result;
			} catch (SecurityException ex) {
				e.printStackTrace(); //?
				return null;
			} catch (NoSuchMethodException ex) {
				return null;
			}
			
		}
	}
	
	public static List<Object> getGnPropertiesValues(GnObject gnObject) {
		List<Object> result = new ArrayList<Object>();
		Class<? extends GnObject> c = gnObject.getClass();
		for (Field field : c.getDeclaredFields()) {
			if (field.isAnnotationPresent(GnProperty.class)) {
				Method method = getGetter(c, field);
				try {
					Object value = method.invoke(gnObject);
					result.add(value);
				} catch (IllegalArgumentException e) {
					// TODO handle these...
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
}
