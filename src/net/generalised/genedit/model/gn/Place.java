package net.generalised.genedit.model.gn;

import net.generalised.genedit.model.common.IntegerInf;

/**
 * Represents a place in a GN.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class Place extends GnObjectCommon implements GnObject, GnObjectWithPosition {
	
	private IntegerInf capacity;

	private FunctionReference charFunction;
	
	private Transition leftTransition;

	private boolean merge;

	private FunctionReference mergeRule;

	private Transition rightTransition;

	private int visualPositionX;
	
	private int visualPositionY;

	//TODO: on all setters: check if the element exists in getParent(GN).xxx? (i.e. it is part of the same gn)

	public IntegerInf getCapacity() {
		return capacity;
	}

	public void setCapacity(IntegerInf capacity) {
		this.capacity = capacity;
	}

	public FunctionReference getCharFunction() {
		return charFunction;
	}

	public void setCharFunction(FunctionReference charFunction) {
		this.charFunction = charFunction;
	}
	
	public Transition getLeftTransition() {
		return leftTransition;
	}

	public void setLeftTransition(Transition leftTransition) {
		this.leftTransition = leftTransition;
	}

	//TODO: bad name?
	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public FunctionReference getMergeRule() {
		return mergeRule;
	}

	public void setMergeRule(FunctionReference mergeRule) {
		this.mergeRule = mergeRule;
	}

	public Transition getRightTransition() {
		return rightTransition;
	}

	public void setRightTransition(Transition rightTransition) {
		this.rightTransition = rightTransition;
	}

	public int getVisualPositionX() {
		return visualPositionX;
	}

	public int getVisualPositionY() {
		return visualPositionY;
	}

	public void setVisualPositionX(int visualPositionX) {
		this.visualPositionX = visualPositionX;
	}
	
	public void setVisualPositionY(int visualPositionY) {
		this.visualPositionY = visualPositionY;
	}
	
	public void setVisualPosition(int x, int y) {
		setVisualPositionX(x);
		setVisualPositionY(y);
	}
	
	public Place(String id) {
		this.capacity = IntegerInf.POSITIVE_INFINITY;
		this.charFunction = null;
		this.setId(id);
		this.leftTransition = null;
		this.merge = false;
		this.mergeRule = null;
		this.rightTransition = null;
		this.visualPositionX = 0;
		this.visualPositionY = 0;
	}
}
