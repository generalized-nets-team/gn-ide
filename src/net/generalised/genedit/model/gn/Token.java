package net.generalised.genedit.model.gn;

import net.generalised.genedit.model.common.IntegerInf;

/**
 * Represents a token in a GN.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class Token extends GnObjectCommon implements Cloneable, GnObject {

	private CharList chars;

	public class CharList extends GnList<Characteristic> {
		
		public CharList(Token token) {
			super();
			setParent(token);
		}

		@Override
		public boolean add(Characteristic object) {
			Characteristic ch = get(object.getName()); 
			if (ch != null) {
				//LEKO S TOVA PUSH!
				ch.pushValue(object.getValue());
				//Type check? History? ...
				return true;
			} else {
				return super.add(object);
			}
		}
	}
	
	private Place host;
	
	private int enteringTime;

	private IntegerInf leavingTime;

	public GnList<Characteristic> getChars() {
		return chars;
	}

	public int getEnteringTime() {
		return enteringTime;
	}

	public void setEnteringTime(int enteringTime) {
		if (enteringTime < 0)
			throw new IllegalArgumentException("entering time cannot be negative");
		this.enteringTime = enteringTime;
	}

	public Place getHost() {
		return host;
	}

	public void setHost(Place host) {
		this.host = host;
	}

	public IntegerInf getLeavingTime() {
		return leavingTime;
	}

	public void setLeavingTime(IntegerInf leavingTime) {
		this.leavingTime = leavingTime;
	}

	public Token(String id, Place host) {
		setId(id);
		this.chars = new CharList(this);
		this.enteringTime = 0;
		this.setHost(host);
		this.leavingTime = IntegerInf.POSITIVE_INFINITY;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Token token = (Token)super.clone();
		//token.chars = new ArrayList<Characteristic>(this.chars.size());
		this.chars = new CharList(this);//TODO: be careful with setParent!
		for (Characteristic ch : this.chars)
		{
			token.chars.add((Characteristic)ch.clone());
		}
		return token;
	}

//	public Token clone() {
//		Token token = new Token(id, host);
//		token.enteringTime = this.enteringTime;
//		token.leavingTime = this.leavingTime;
//		token.name = this.name;
//		token.priority = this.priority;
//		token.chars = new ArrayList<Characteristic>(this.chars.size());
//		for (Characteristic ch : this.chars)
//		{
//			token.chars.add(ch.clone());
//		}
//		return token;
//	}
	
//	/**
//	 * Tokens can be compared with TokenGenerators
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (! (obj instanceof Token))
//			return false;
//		return ((Token)obj).id.equals(id);
//	}
//
//	@Override
//	public int hashCode() {
//		// TODO Auto-generated method stub
//		return super.hashCode();
//	}

	public Characteristic getDefaultCharacteristic() {
		return getChars().get(Characteristic.DEFAULT);
	}
}
