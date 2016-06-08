package net.generalised.genedit.simulation.model.embedded;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.common.IndexMatrix;
import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnList;
import net.generalised.genedit.model.gn.GnUtil;
import net.generalised.genedit.model.gn.JavaScriptFunction;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.TokenGenerator;
import net.generalised.genedit.model.gn.Transition;
import net.generalised.genedit.model.gn.TransitionMatrix;
import net.generalised.genedit.simulation.model.EnterEvent;
import net.generalised.genedit.simulation.model.GnEvent;
import net.generalised.genedit.simulation.model.GnEvents;
import net.generalised.genedit.simulation.model.LeaveEvent;
import net.generalised.genedit.simulation.model.MoveEvent;
import net.generalised.genedit.simulation.model.Simulation;
import net.generalised.genedit.simulation.model.SimulationException;

public class EmbeddedSimulation extends Simulation {

	// abstract transition
	private final Set<Transition> abstractTransition;

	// map : token generator id -> next token id
	private Map<String, Integer> tokenGeneratorsNextIds;
	
	private Map<Place, IntegerInf> maxCapacities;

	private final FunctionRunner functionRunner;
	
	public EmbeddedSimulation(GnDocument document, GeneralizedNet gn, FunctionRunner functionRunner) 
			throws SimulationException {
		
		super(document, gn);
		this.functionRunner = functionRunner;
		
		if (! gn.getFunctionLanguage().equals(functionRunner.getFunctionLanguage())) {
			throw new SimulationException("GN model functions are written in " + gn.getFunctionLanguage() 
					+ " but only " + functionRunner.getFunctionLanguage() + " functions can be executed.");
		}
		
		// step (B02)
		this.abstractTransition = new HashSet<Transition>(); //new Transition("abstract");
		
		this.tokenGeneratorsNextIds = new HashMap<String, Integer>();
		
		// undocumented; TODO arc capacities too
		maxCapacities = new HashMap<Place, IntegerInf>();
		for (Place place : gn.getPlaces()) {
			maxCapacities.put(place, place.getCapacity());
		}
	}
	
	@Override
	protected void closeEventSource() throws SimulationException {
		// Nothing. gn.restoreState() should do the job.
		
		// TODO make method backup/restore in Document - write to XML, restore, update views!
	}

	private static final Random RANDOM = new Random();
	
	private GnEvents putTokens(int time) {
		GeneralizedNet gn = getGn();
		GnEvents events = new GnEvents(gn);
		GnList<Token> tokens = gn.getTokens();
		List<Token> pendingNewTokens = new ArrayList<Token>();
		for (Token token : tokens) {
			if (token.getEnteringTime() == time
					|| token instanceof TokenGenerator && token.getEnteringTime() < time) { // TODO be careful
				Token backupToken = this.backupGn.getTokens().get(token.getId());
				Place host = gn.getPlaces().get(backupToken.getHost().getId());
				
				if (token instanceof TokenGenerator) {
					TokenGenerator generator = (TokenGenerator) token;
					boolean shouldGenerate = false;
					switch (generator.getType()) {
					case PERIODIC:
						int period = generator.getPeriod();
						if (period == 0 || time % period == 0)
							shouldGenerate = true;
						break;
					case RANDOM:
						shouldGenerate = RANDOM.nextBoolean();
						break;
					case CONDITIONAL:
						FunctionReference predicate = generator.getPredicate();
						shouldGenerate = (Boolean) this.functionRunner.run(predicate, gn, null);
						break;
					}
					if (shouldGenerate) {
						Integer boxedNextId = this.tokenGeneratorsNextIds.get(generator.getId());
						int nextId;
						if (boxedNextId == null)
							nextId = 1;
						else
							nextId = boxedNextId.intValue();
						this.tokenGeneratorsNextIds.put(generator.getId(), new Integer(nextId + 1));
						
						String id = generator.getId() + "_" + nextId;
						Token newToken = new Token(id, host);
						copyChars(newToken, backupToken);
						events.add(new EnterEvent(gn, newToken, host, token.getChars()));
						// TODO set other properties?
						pendingNewTokens.add(newToken);
					}
				} else {
					// TODO undoable set?
					gn.getTokens().get(token.getId()).setHost(host); // XXX
					
					// undocumented
					if (! host.getCapacity().isPositiveInfinity()) {
						host.setCapacity(new IntegerInf(host.getCapacity().getValue() - 1));
					}
					
					copyChars(token, backupToken);
					events.add(new EnterEvent(gn, token, host, token.getChars()));
				}
			}
		}
		gn.getTokens().addAll(pendingNewTokens);
		
		return events;
	}
	
	private static void copyChars(Token newToken, Token oldToken) {
		for (Characteristic characteristic : oldToken.getChars()) {
			try {
				newToken.getChars().add((Characteristic) characteristic.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}

	private static final Comparator<Place> PLACE_COMPARATOR = new Comparator<Place>() {

		public int compare(Place arg0, Place arg1) {
			// descending order
			// TODO if equal priorities? transitions' priorities?
			int priorityDiff = arg1.getPriority() - arg0.getPriority();
			if (priorityDiff != 0) {
				return priorityDiff;
			} else {
				return arg1.hashCode() - arg0.hashCode();
			}
		}
		
	};
	
	private static final Comparator<Token> TOKEN_COMPARATOR = new Comparator<Token>() {

		public int compare(Token arg0, Token arg1) {
			// descending order
			// TODO if equal priorities? places and transitions' priorities?
			int priorityDiff = arg1.getPriority() - arg0.getPriority();
			if (priorityDiff != 0) {
				return priorityDiff;
			} else {
				return arg1.hashCode() - arg0.hashCode();
			}
		}
		
	};
	
	private GnEvents performAbstractTransitionStep() {
		
		GeneralizedNet gn = getGn();
		
		GnEvents events = new GnEvents(gn);
		
		// step (A01)
		SortedSet<Place> inputPlaces = new TreeSet<Place>(PLACE_COMPARATOR);
		SortedSet<Place> outputPlaces = new TreeSet<Place>(PLACE_COMPARATOR);
		for (Transition tr : this.abstractTransition) {
			for (PlaceReference ref : tr.getInputs()) {
				inputPlaces.add(ref.getPlace());
			}
			for (PlaceReference ref : tr.getOutputs()) {
				outputPlaces.add(ref.getPlace());
			}
		}
		
		// XXX why there's no such step in the official documentation?
		// Leave tokens from output places
		for (Place place : outputPlaces) {
			if (place.getRightTransition() == null)
				for (Token token : GnUtil.getTokensAt(gn, place.getId())) {
					token.setHost(null);
					events.add(new LeaveEvent(gn, token, place, token.getChars()));
					
					// yet another undocumented action
					if (! place.getCapacity().isPositiveInfinity() && place.getCapacity().getValue() < maxCapacities.get(place).getValue())
						// TODO not more than original capacity!
						place.setCapacity(new IntegerInf(place.getCapacity().getValue() + 1));
				}
		}
		
		// TODO why not just set a flag?
		Map<Place, SortedSet<Token>> movableTokens = new HashMap<Place, SortedSet<Token>>();
		Map<Place, Set<Token>> unmovableTokens = new HashMap<Place, Set<Token>>();
		for (Place place : inputPlaces) {
			SortedSet<Token> tokens = new TreeSet<Token>(TOKEN_COMPARATOR);
			tokens.addAll(GnUtil.getTokensAt(gn, place.getId()));
			movableTokens.put(place, tokens);
			
			unmovableTokens.put(place, new HashSet<Token>());
		}
		for (Place place : outputPlaces) {
			unmovableTokens.put(place, new HashSet<Token>());
		}
		
		// step (A02)
		IndexMatrix<Place, Boolean> predicateValues = new IndexMatrix<Place, Boolean>(); // true, false, null
//		for (Place input : inputPlaces) {
//			Transition transition = input.getRightTransition();
//			for (Place output : transition.getOutputs().toPlaceList()) {
//				if (output.getCapacity().getValue() == 0
//						|| transition.getCapacities().getAt(input, output).getValue() == 0) {
//					predicateValues.setAt(input, output, Boolean.FALSE);
//				}
//			}
//		}
		// TODO fill it
		
		// step (A03)
		for (Place input : inputPlaces) {
			// TODO The sorted places are passed sequentially by their priority, starting with
			// the place having the highest priority, which has at least one token and through
			// which no transfer has occurred on the current time-step.
			//for (Token token : movableTokens.get(input)) { // TODO ordinary iterator with remove?
			SortedSet<Token> movableTokensForThisInput = movableTokens.get(input);
			if (! movableTokensForThisInput.isEmpty()) {
				Token token = movableTokensForThisInput.first();
				// TODO if token splitting...
				
				List<Place> currentOutputs = input.getRightTransition().getOutputs().toPlaceList();
				for (Place output : currentOutputs) {
					Boolean boxedValue = predicateValues.getAt(input, output);
					boolean value;
					if (boxedValue == null) {
						
						if (output.getCapacity().getValue() != 0
								&& input.getRightTransition().getCapacities().getAt(input, output).getValue() != 0) {
							TransitionMatrix<FunctionReference> predicates = input.getRightTransition().getPredicates();
							FunctionReference predicateReference = predicates.getAt(input, output);
							String predicateName = predicateReference.getFunctionName();
							if (predicateName.equals(JavaScriptFunction.TRUE.getName())) { // XXX JS-related stuff must not be directly referenced in this class!
								value = true;
							} else if (predicateName.equals(JavaScriptFunction.FALSE.getName())) {
								value = false;
							} else {
								
								// TODO get function runner for current lang
								
								// ? init JS context every time, because the GN object may be modified
								value = (Boolean) this.functionRunner.run(predicateReference, gn, token);
								// TODO all set methods should register their changes so a GNTP response can be constructed
	
								// System.out.println(">>>>> Token " + token.getId() + " -> " + value);// delete
							}
							// the following makes predicates to be calculated for every token
							// predicateValues.setAt(input, output, new Boolean(value));
						} else {
							value = false;
						}
					} else {
						value = boxedValue.booleanValue();
					}
					
					// step (A04)
					if (value) {

						token.setHost(output);
						
						// TODO remove token from first list
						//movableTokens.get(input).remove(token);
						unmovableTokens.get(output).add(token);

						// step (A05)
						if (output.getCharFunction() != null) {
							this.functionRunner.run(output.getCharFunction(), gn, token);
						}
						if (output.isMerge()) {
							// TODO somewhere token merging - after/before char functions
							if (output.getMergeRule() != null) {
								// JavaScriptRunner...
							} else {
								// ...
							}
							// TODO how does merging affect capacities?
						}

						events.add(new MoveEvent(gn, token, output, token.getChars()));

						// step (A06)
						// step (A07)

						// step (A08)
						if (! output.getCapacity().isPositiveInfinity()) {
							int newCapacity = output.getCapacity().getValue() - 1;
							output.setCapacity(new IntegerInf(newCapacity));
							if (newCapacity == 0) {
								// TODO put zeroes in matrix?
							}
						}
						
						// undocumented:
						if (! input.getCapacity().isPositiveInfinity() && input.getCapacity().getValue() < maxCapacities.get(input).getValue()) {
							input.setCapacity(new IntegerInf(input.getCapacity().getValue() + 1));
						}
						
						// step (A09)
						// step (A10) - maybe check if the algorithm can continue and if not - break
						
						// if gn.isTokenSpliitingEnabled(); otherwise - continue and generate different named tokens!
						break;
					}
				}
				
				// TODO if cannot pass - move to secondList
			}
		}
		
		return events;
	}
	
	@Override
	protected GnEvents getNextEvents(int count) throws SimulationException {
		
		GeneralizedNet gn = getGn();
		
		// TODO see V. Gochev's implementation
		
		this.abstractTransition.addAll(gn.getTransitions());
		// step (B05)
		for (Iterator<Transition> iter = this.abstractTransition.iterator(); iter.hasNext(); ) {
			Transition transition = iter.next();
			// TODO is this correct?
			if (gn.getCurrentTime() < transition.getStartTime() ||
					(! transition.getLifeTime().isPositiveInfinity() && gn.getCurrentTime() > transition.getStartTime() + transition.getLifeTime().getValue())) {
				iter.remove();
			}
		}		
		
		// step (B06)
		for (Iterator<Transition> iter = this.abstractTransition.iterator(); iter.hasNext(); ) {
			Transition transition = iter.next();
			if (! transition.getTypeTree().evaluate(gn)) {
				iter.remove();
			}
		}
		// ? <!-- The transition type can be an identifier of a predicate, which should be evaluated every time a transition is fired -->
		
		// step (B07)
		// >>> TODO !!! union transitions!!! hmm - union requires equal times?
		
		// step (B08): algorithm A from D. Dimitrov
		GnEvents events = performAbstractTransitionStep();
		// XXX Algorithm A encompasses several GN steps! Perform only one step...
		// FIXME perform count steps - this method has an argument!
		
		// step (B09)
		// remove transitions from AT - skip for now
		
		// step (B01)
		GnEvents addEvents = putTokens(gn.getCurrentTime());
		for (GnEvent event: addEvents.getEvents()) {
			events.add(event);
		}
		
		//getDocument().notifyObservers(events); // XXX why do we need this?!
		
		return events;
	}

}
