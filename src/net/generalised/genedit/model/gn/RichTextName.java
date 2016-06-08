package net.generalised.genedit.model.gn;

/**
 * GN object name that supports subscripts and other simple formatting. Immutable.
 */
public class RichTextName {

	private final String mainPart;
	private final String subscriptPart;
	
	private final String fullText;
	
	private static final RichTextName EMPTY = new RichTextName("", "");
	public static final RichTextName constructEmptyName() {
		return EMPTY;
	}
	
	private static String[] parseString(String fullText) {

		String mainPart = "";
		String subscriptPart = "";

		int fullTextLength = (fullText != null ? fullText.length() : 0);
		if (fullTextLength == 0) {
			mainPart = "";
			subscriptPart = "";
		} else {
			int indexOfUnderscore = fullText.indexOf("_"); 
			if (indexOfUnderscore >= 1 && indexOfUnderscore < fullTextLength - 1) {
				// text is possibly in TeX format (something_{something})
				mainPart = fullText.substring(0, indexOfUnderscore);
				subscriptPart = fullText.substring(indexOfUnderscore + 1);
				if (subscriptPart.charAt(0) == '{' &&
						subscriptPart.charAt(subscriptPart.length() - 1) == '}') {
					subscriptPart = subscriptPart.substring(1, subscriptPart.length() - 1);
				}
			} else {
				// text is possibly in format <letters><digits> or just plain text
				int splitIndex = fullTextLength;
				for (splitIndex-- ; splitIndex >= 0; splitIndex--) {
					if (! Character.isDigit(fullText.charAt(splitIndex))) {
						splitIndex++;
						break;
					}
				}
				mainPart = fullText.substring(0, splitIndex);
				subscriptPart = fullText.substring(splitIndex);
			}
			// User will be able to manage which part is a subscript and which - not

			// TODO
			// if greek letter - prefix with "\\"
			// allow escaping
		}
		
		return new String[] {mainPart, subscriptPart};
	}
	
	private static String constructFullText(String mainPart, String subscriptPart) {
		
		if (subscriptPart.length() > 0) {
			return mainPart + "_{" + subscriptPart + "}";
		} else {
			return mainPart;
		}
	}
	
	public RichTextName(String fullText) {

		String[] parts = parseString(fullText.trim());
		
		this.mainPart = parts[0];
		this.subscriptPart = parts[1];
		
		this.fullText = constructFullText(this.mainPart, this.subscriptPart);
	}
	
	public RichTextName(String mainPart, String subscriptPart) {
		if (mainPart == null) {
			throw new NullPointerException("Main part cannot be null");
		} else {
			this.mainPart = mainPart.trim();
		}
		
		if (subscriptPart == null) {
			this.subscriptPart = "";
		} else {
			this.subscriptPart = subscriptPart.trim();
		}
		
		this.fullText = constructFullText(this.mainPart, this.subscriptPart);
	}
	
	public String getMainPart() {
		return this.mainPart;
	}
	
	public String getSubscriptPart() {
		return this.subscriptPart;
	}
	
	/**
	 * Returns the representation suitable for serializing.
	 * @return
	 */
	public String getFullText() {
		return this.fullText;
	}
	
	@Override
	public String toString() {
		return this.fullText;
	}
	
	public String toTexString() {
		return this.fullText;
	}
	
	public boolean isEmpty() {
		return this.mainPart.length() == 0 && this.subscriptPart.length() == 0;
	}
}
