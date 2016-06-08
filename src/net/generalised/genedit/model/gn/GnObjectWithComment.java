package net.generalised.genedit.model.gn;

public interface GnObjectWithComment {

	public String getComment();
	
	public void setComment(String comment);
	
	// Objects with comments: GN, Transitions list, Transition, Inputs for a transition, Outputs for a transition,
	// Place reference, Arc, Point, Predicates matrix, Predicate item in matrix, Capacities matrix, Capacity item in matrix,
	// Places list, Place, Tokens list, Token, Characteristic, History?, Functions list, Function(in code),
	// Visual parameters (with its components), comments after last tag
	
	// Corresponding classes:
	// GnObject (GN, Transition, Place, Token, PlaceRef, Function), GnList,
	// GnObjectWithId (Characteristic, Function, Place, Token, Transition)
	// VisualParameters
	
	// TODO GnXmlReader/Writer, modify XSD!
}
