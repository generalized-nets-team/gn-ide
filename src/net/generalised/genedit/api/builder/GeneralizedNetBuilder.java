package net.generalised.genedit.api.builder;

import net.generalised.genedit.api.JavaFunction;
import net.generalised.genedit.api.JavaFunctionFactory;
import net.generalised.genedit.api.JavaFunctionReference;
import net.generalised.genedit.api.JavaGeneralizedNet;
import net.generalised.genedit.baseapp.SettingsManager;
import net.generalised.genedit.main.MainConfigProperties;
import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.FunctionFactory;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GeneratorMode;
import net.generalised.genedit.model.gn.JavaScriptFunctionFactory;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.TokenGenerator;
import net.generalised.genedit.model.gn.Transition;
import net.generalised.genedit.simulation.SimulationConfigProperties;

public class GeneralizedNetBuilder {

	static {
		SettingsManager.getInstance().setDefaultProperty(MainConfigProperties.NEW_GN_NAME, "Untitled");
		SettingsManager.getInstance().setDefaultProperty(MainConfigProperties.DEFAULT_FUNCTION_LANGUAGE, JavaScriptFunctionFactory.LANGUAGE);
		SettingsManager.getInstance().setDefaultProperty(MainConfigProperties.DEFAULT_TRANSITION_PREFIX, "Z");
		SettingsManager.getInstance().setDefaultProperty(MainConfigProperties.DEFAULT_PLACE_PREFIX, "l");
		SettingsManager.getInstance().setDefaultProperty(MainConfigProperties.DEFAULT_TOKEN_PREFIX, "alpha");

		SettingsManager.getInstance().setDefaultProperty(SimulationConfigProperties.DEFAULT_DELAY_TIME, "0");
		
		FunctionFactory.addFactory(new JavaFunctionFactory());
		FunctionFactory.addFactory(new JavaScriptFunctionFactory()); // XXX remove this - used only for EmbeddedSimulation's references to JavaScriptFunction.TRUE
	}
	
	protected final GeneralizedNet gn;
	
	public GeneralizedNetBuilder(String name) {
		this.gn = new GeneralizedNet(name, JavaFunctionFactory.LANGUAGE);
	}
	
	protected GeneralizedNetBuilder(GeneralizedNet gn) {
		this.gn = gn;
	}
	
	public JavaGeneralizedNet build() {
		// TODO check for errors
		return new JavaGeneralizedNet(this.gn);
	}

	public GeneralizedNetBuilder setGnTime(int time) {
		gn.setTime(time);
		return this;
	}

	public GeneralizedNetBuilder setGnTimeStart(int time) {
		gn.setTimeStart(time);
		return this;
	}
	
	public GeneralizedNetBuilder setGnTimeStep(int timeStep) {
		gn.setTimeStep(timeStep);
		return this;
	}

	public GeneralizedNetBuilder setTokenSplittingEnabled(boolean enabled) {
		gn.setTokenSplittingEnabled(enabled);
		return this;
	}

	public TransitionBuilder addTransition(String id) {
		Transition transition = new Transition(id);
		gn.getTransitions().add(transition);
		return new TransitionBuilder(transition);
	}
	
	protected abstract static class PartBuilder extends GeneralizedNetBuilder {
		
		public PartBuilder(GeneralizedNet gn) {
			super(gn);
		}
		
//		public GeneralizedNet build() { // the inner class was not static
//			return GeneralizedNetBuilder.this.build();
//		}
		
	}
	
	public static class TransitionBuilder extends PartBuilder {
		
		protected final Transition transition;
		
		public TransitionBuilder(Transition transition) {
			super(transition.getParent(GeneralizedNet.class));
			this.transition = transition;
		}
		
		public TransitionBuilder setTransitionPriority(int priority) {
			transition.setPriority(priority);
			return this;
		}
		
		public TransitionBuilder setLifeTime(IntegerInf lifeTime) {
			transition.setLifeTime(lifeTime);
			return this;
		}
		
		public TransitionBuilder setTransitionStartTime(int startTime) {
			transition.setStartTime(startTime);
			return this;
		}
		
		public TransitionBuilder setType(String type) {
			transition.setType(type);
			return this;
		}

		public TransitionBuilder setCapacity(String fromId, String toId, IntegerInf value) {
			transition.getCapacities().setAt(gn.getPlaces().get(fromId), gn.getPlaces().get(toId), value);
			return this;
		}

		public TransitionBuilder setCapacity(String fromId, String toId, int value) {
			transition.getCapacities().setAt(gn.getPlaces().get(fromId), gn.getPlaces().get(toId), new IntegerInf(value));
			return this;
		}

		public TransitionBuilder setPredicate(String fromId, String toId, JavaFunction predicate) {
			transition.getPredicates().setAt(gn.getPlaces().get(fromId), gn.getPlaces().get(toId), new JavaFunctionReference(predicate));
			return this;
		}

		public PlaceBuilder addInput(String id) {
			Place place = gn.getPlaces().get(id); 
			if (place == null) {
				place = new Place(id);
				gn.getPlaces().add(place);
			}
			transition.getInputs().add(place);
			return new PlaceBuilder(place, transition);
		}
		
		public PlaceBuilder addOutput(String id) {
			Place place = gn.getPlaces().get(id); 
			if (place == null) {
				place = new Place(id);
				gn.getPlaces().add(place);
			}
			transition.getOutputs().add(place);
			return new PlaceBuilder(place, transition);
		}
	}
	
	public static class PlaceBuilder extends TransitionBuilder {
		private final Place place;
		
		public PlaceBuilder(Place place, Transition lastTransition) {
			super(lastTransition);
			this.place = place;
		}
		
		public PlaceBuilder setPlacePriority(int priority) {
			place.setPriority(priority);
			return this;
		}

		public PlaceBuilder setPlaceCapacity(IntegerInf capacity) {
			place.setCapacity(capacity);
			return this;
		}

		public PlaceBuilder setPlaceCapacity(int capacity) {
			place.setCapacity(new IntegerInf(capacity));
			return this;
		}

		public PlaceBuilder setCharFunction(JavaFunction charFunction) {
			place.setCharFunction(new JavaFunctionReference(charFunction));
			return this;
		}
		
		public PlaceBuilder setMergeTokens(boolean merge) {
			place.setMerge(merge);
			return this;
		}
		
		public PlaceBuilder setMergeRule(JavaFunction function) {
			place.setMergeRule(new JavaFunctionReference(function));
			return this;
		}
		
		public TokenBuilder addToken(String id) {
			Token token = new Token(id, place);
			gn.getTokens().add(token);
			return new TokenBuilder(token, place, transition);
		}
		
		public TokenBuilder addPeriodicTokenGenerator(String id, int period) {
			TokenGenerator generator = new TokenGenerator(id, place, GeneratorMode.PERIODIC);
			generator.setPeriod(period);
			gn.getTokens().add(generator);
			return new TokenBuilder(generator, place, transition);
		}
		
		public TokenBuilder addRandomTokenGenerator(String id) {
			TokenGenerator generator = new TokenGenerator(id, place, GeneratorMode.RANDOM);
			return new TokenBuilder(generator, place, transition);
		}
		
		public TokenBuilder addConditionalTokenGenerator(String id, JavaFunction condition) {
			TokenGenerator generator = new TokenGenerator(id, place, GeneratorMode.PERIODIC);
			generator.setPredicate(new JavaFunctionReference(condition));
			gn.getTokens().add(generator);
			return new TokenBuilder(generator, place, transition);
		}
	}
	
	public static class TokenBuilder extends PlaceBuilder {
		private final Token token;
		
		public TokenBuilder(Token token, Place lastPlace, Transition lastTransition) {
			super(lastPlace, lastTransition);
			
			this.token = token;
		}
		
		public TokenBuilder setTokenPriority(int priority) {
			token.setPriority(priority);
			return this;
		}
		
		public TokenBuilder setTokenEnteringTime(int enteringTime) {
			token.setEnteringTime(enteringTime);
			return this;
		}
		
		public TokenBuilder setTokenLeavingTime(IntegerInf leavingTime) {
			token.setLeavingTime(leavingTime);
			return this;
		}

		public TokenBuilder addCharacteristic(String name, String type, int history) {
			return addCharacteristic(name, type, history, ""); // TODO "" or null?
		}

		public TokenBuilder addCharacteristic(String name, String type, int history, String value) {
			Characteristic characteristic = new Characteristic(type, history);
			characteristic.setId(name);
			token.getChars().add(characteristic);
			return this;
		}
	}
}
