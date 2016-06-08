package net.generalised.genedit.model.common;

/**
 * Represents a natural number. Allows the value "positive infinity".
 * Also allows the value of zero. Objects of this class are immutable.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class IntegerInf { // TODO extend Number?
	
	private int value;
	
	/**
	 * Constructs a new {@code IntegerInf} object with value 0.
	 */
	public IntegerInf() {
		this(0);
	}
	
	/**
	 * Constructs a new {@code IntegerInf} object with a given value.
	 * 
	 * @param value a nonnegative integer or -1 for positive infinity.
	 */
	public IntegerInf(int value) {
		if (value >= -1)
			this.value = value;
		else
			this.value = -1;
	}
	
	/**
	 * Gets the serializable integer value of this object.
	 * 
	 * @return nonnegative integer or -1 for positive infinity.
	 */
	public int getValue() {
		return value;
	}
	
	public Number toNumber() {
		if (isPositiveInfinity()) {
			return Double.POSITIVE_INFINITY;
		} else {
			return new Integer(value);
		}

	}
	
	/**
	 * Checks if this object's value represents positive infinity.
	 * 
	 * @return true iff this object's value is positive infinity.
	 */
	public boolean isPositiveInfinity() {
		return value == -1;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof IntegerInf))
			return false;
		return value == ((IntegerInf)object).value;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public String toString() {
		if (value >= 0)
			return Integer.toString(value);
		else
			return POSITIVE_INFINITY_STRING;
	}
	
	public static final IntegerInf POSITIVE_INFINITY = new IntegerInf(-1);
	
	public static final String POSITIVE_INFINITY_STRING = "infinity";
}
