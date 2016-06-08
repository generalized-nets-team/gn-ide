package net.generalised.genedit.baseapp;

/**
 * Contains utility methods for strings.
 * 
 * @author Dimitar Dimitrov
 */
public class StringUtil {

	private StringUtil() {
	}
	
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static void assertNotEmpty(String string,
			String exceptionMessageTitle) {
		if (isNullOrEmpty(string)) {
			throw new IllegalArgumentException(exceptionMessageTitle
					+ " cannot be null or empty string");
		}
	}
}
