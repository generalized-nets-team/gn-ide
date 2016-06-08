package net.generalised.genedit.model.errors;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.Function;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Transition;

/**
 * Provides methods for checking the GN model for errors.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class ErrorChecker {

	private GnDocument document;
	
	/**
	 * Constructs a new error checker
	 * @param document the document containing the GN model to be checked
	 */
	public ErrorChecker(GnDocument document) {
		this.document = document;
	}

	//TODO: class ProblemList, errors first, then warnings, boolean hasErrors

	// TODO: check for unique friendly names - these are warnings only?
	
	/**
	 * Checks the topology of the GN model, for example missing inputs.
	 * 
	 * @return a list of {@link Problem}
	 */
	public List<Problem> checkTopology() {
		List<Problem> result = new ArrayList<Problem>();
		//TODO: this works only in the root GN; if GNs as chars - recursion
		GeneralizedNet gn = document.getModel();
		for (Transition t : gn.getTransitions()) {
			if (t.getInputs().size() == 0)
				result.add(new Problem("Transition '" + t.getId() + "' has no inputs.", true));
			if (t.getOutputs().size() == 0)
				result.add(new Problem("Transition '" + t.getId() + "' has no outputs.", true));
		}
		//TODO: each place can participate in exactly one input (resp. output) ("double place binding"?)
		return result;
	}
	
	/**
	 * Checks for problems in the visual representation of a model.
	 * Usually these are only warnings.
	 * 
	 * @return a list of {@link Problem} 
	 */
	public List<Problem> checkGraphicalTopology() {
		List<Problem> result = new ArrayList<Problem>();
		GeneralizedNet gn = document.getModel();
		for (Transition t : gn.getTransitions()) {
			for (PlaceReference ref : t.getInputs()) {
				String transitionName = t.getName().toString();
				String placeName = ref.getPlace().getName().toString();
				if (ref.getArc().size() == 0)
					result.add(new Problem("Missing arc from place '" + placeName
							+ "' to transition '" + transitionName + "'.", false));
				//else if arcata ne po4va ot kraga i ne svar6va v transitiona - warning
			}
			for (PlaceReference ref : t.getOutputs()) {
				String transitionName = t.getName().toString();
				String placeName = ref.getPlace().getName().toString();
				if (ref.getArc().size() == 0)
					result.add(new Problem("Missing arc from transition '" + transitionName
							+ "' to place '" + placeName + "'.", false));
				//else...
			}
		}
		//no overlapping places and transitions
		//no overlapping arcs
		//warnings only
		return result;
	}
	
	public List<Problem> checkFunction(Function function) {
		// TODO: one of the things to check - if all functions are of the specified language!
		return new ArrayList<Problem>();
	}
	
	/**
	 * Calls all check methods in this class. 
	 * 
	 * @return a list of {@link Problem}
	 */
	public List<Problem> checkAll() {
		List<Problem> result = new ArrayList<Problem>();
		result.addAll(this.checkTopology());
//		for (Function function : document.getModel().getFunctions()) {
//			result.addAll(this.checkFunction(function));			
//		}
		result.addAll(this.checkGraphicalTopology());
		//TODO: ami koi 6te proverqva za gre6ni tipove na harakteristiki, lipsva6ti stoinosti, ...?
		return result;
	}
}
