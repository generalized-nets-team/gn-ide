package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.gn.transitiontype.TransitionTypeNode;
import net.generalised.genedit.model.gn.transitiontype.TransitionTypeParser;

/**
 * Represents a transition in a GN.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class Transition extends GnObjectCommon implements GnObject,
		GnObjectWithPosition, GnObjectWithDimensions {
	
	private final TransitionMatrix<IntegerInf> capacities;

	private final PlaceRefList inputs;

	private IntegerInf lifeTime;

	private final PlaceRefList outputs;

	private final TransitionMatrix<FunctionReference> predicates;

	private int startTime;

	private String type;
	// TODO both String and binary tree, in case if the string is not valid?
	// or just a node InvalidTypeNode with String property :)
	private TransitionTypeNode typeTree;

	private int visualHeight; //TODO: int or visualMetricType...

	private int visualPositionX;
	
	private int visualPositionY;

	public TransitionMatrix<IntegerInf> getCapacities() {
		return capacities;
	}

	public PlaceRefList getInputs() {
		//return Collections.unmodifiableList(inputs);
		return inputs;
	}

	public IntegerInf getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(IntegerInf lifeTime) {
		this.lifeTime = lifeTime;
	}

	public PlaceRefList getOutputs() {
		return outputs;
		//return Collections.unmodifiableList(outputs);
	}

	public TransitionMatrix<FunctionReference> getPredicates() {
		return predicates;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		if (startTime < 0)
			throw new IllegalArgumentException("start time cannot be negative");
		this.startTime = startTime;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		// TODO: validation!
		this.type = type;
	}
	
	public TransitionTypeNode getTypeTree() {
		// TODO cache
		this.typeTree = TransitionTypeParser.parse(this.type);
		return this.typeTree;
	}

	public int getVisualHeight() {
		return visualHeight;
	}

	public void setVisualHeight(int visualHeight) {
		this.visualHeight = visualHeight;
	}

	public int getVisualWidth() {
		return 0;
	}

	public void setVisualWidth(int width) {
	}

	public int getVisualPositionX() {
		return visualPositionX;
	}

	public void setVisualPositionX(int visualPositionX) {
		this.visualPositionX = visualPositionX;
	}

	public int getVisualPositionY() {
		return visualPositionY;
	}

	public void setVisualPositionY(int visualPositionY) {
		this.visualPositionY = visualPositionY;
	}

	public void setVisualPosition(int x, int y) {
		setVisualPositionX(x);
		setVisualPositionY(y);
	}

	public Transition(String id) { //TODO: is this id needed?
		this.capacities = new TransitionMatrix<IntegerInf>(this, IntegerInf.POSITIVE_INFINITY);
		this.setId(id);
		this.inputs = new PlaceRefList(this, true);
		this.lifeTime = IntegerInf.POSITIVE_INFINITY;
		this.outputs = new PlaceRefList(this, false);
		// TODO get "false" from the proper location
		this.predicates = TransitionMatrix.create(this, FunctionReference.class, new FunctionReference("false"));
		this.startTime = 0;
		this.type = "";
		this.typeTree = TransitionTypeParser.parse(this.type);
		this.visualHeight = 0;
		this.visualPositionX = 0;
		this.visualPositionY = 0;
	}
	
	public List<PlaceReference> getInputsAndOutputs() {
		List<PlaceReference> result = new ArrayList<PlaceReference>();
		result.addAll(this.inputs);
		result.addAll(this.outputs);
		return result;
	}
}
