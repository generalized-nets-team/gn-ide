package net.generalised.genedit.model.gn;

import net.generalised.genedit.baseapp.model.BaseModel;

public class VisualParameters extends BaseModel {
	//TODO: add more settings here; also make hierarcy - VisualParametersPlaces, ...
	//TODO: each object has reference to VisualParametersXXX

	//TODO: default settings... ako ne6to go nqma v XML-a, 6te izpolzva default value;
	//TODO: dobre, ama pri save kak 6te razbira dali stoinostta e bila v XML-a ili e default? 

	private int placeRadius;
	
	//TODO: find proper type; Size/Dimensions/Point... ?
	private Point transitionTriangleSize;
	
	private int gridStep;

	public VisualParameters(int placeRadius, Point transitionTriangleSize) {
		super();
		this.placeRadius = placeRadius;
		this.transitionTriangleSize = transitionTriangleSize;
		this.gridStep = 10;
	}

	public int getPlaceRadius() {
		return placeRadius;
	}

	public void setPlaceRadius(int placeRadius) {
		if (placeRadius <= 1)
			throw new IllegalArgumentException("place radius must be >= 2");
		this.placeRadius = placeRadius;
	}

	public Point getTransitionTriangleSize() {
		return transitionTriangleSize;
	}

	public void setTransitionTriangleSize(Point transitionTriangleSize) {
		this.transitionTriangleSize = transitionTriangleSize;
	}

	public int getGridStep() {
		return gridStep;
	}

	public void setGridStep(int gridStep) {
		this.gridStep = gridStep;
	}
}
