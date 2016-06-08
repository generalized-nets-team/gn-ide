package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.baseapp.SettingsManager;
import net.generalised.genedit.baseapp.StringUtil;
import net.generalised.genedit.baseapp.model.BaseModel;
import net.generalised.genedit.main.MainConfigProperties;
import net.generalised.genedit.model.dataaccess.xmlread.FunctionParser;

/* TODO:
associations:
tr<->place with arc
tr->func
place->func
token->place - lesno
 */

public class GeneralizedNet extends BaseModel implements GnObject { //TODO: GnObjectWithPosition, GnObjectWithDimensions
	// TODO: in all setters: range check ( >=0, ...)
	
	private final GnList<Transition> transitions;
	
	private final GnList<Place> places;
	
	private final GnList<Token> tokens;
	
	// all functions, not only global ones
	private String functions;

	// TODO: import list?

	//TODO: visualBounds? or in visualParameters?
	
	private VisualParameters visualParameters;
	
	@Deprecated
	private OriginalState originalState;
	
	private String name;
	
	// TODO: which of these ints are IntegerInf?
	
	private int time;
	
	private int timeStep;
	
	private int timeStart;
	
	private int currentTime;
	
	private String functionLanguage;
	
	//TODO: delay? (milliseconds)

	private boolean tokenSplittingEnabled;
	
	//private String fundefs? for compatibility only; use "import"
	
	private boolean root;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		StringUtil.assertNotEmpty(name, "GN name");
		this.name = name;
	}

	public GnList<Transition> getTransitions() {
		return transitions;
	}
	
	public GnList<Place> getPlaces() {
		return places;
	}

	public GnList<Token> getTokens() {
		return tokens;
	}
	
	public String getFunctions() {
		return functions;
	}
	
	public void setFunctions(String functions) {
		this.functions = functions;
	}
	
	// temporary?
	public List<String> getFunctionNames() {
		
		FunctionFactory factory = getFunctionFactory();
		FunctionParser<?> parser = factory.getFunctionParser();

		List<? extends Function> functionsList = parser.parseFunctions(this.functions);
		
		List<String> result = new ArrayList<String>(functionsList.size());
		for (Function function : functionsList) {
			result.add(function.getName());
		}
		return result;
	}
	
	public VisualParameters getVisualParameters() {
		return visualParameters;
	}

	public void setVisualParameters(VisualParameters visualParameters) {
		this.visualParameters = visualParameters;
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		if (time <= 0)
			throw new IllegalArgumentException("time must be positive");
		this.time = time;
	}

	public int getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(int timeStart) {
		if (time < 0)
			throw new IllegalArgumentException("start time cannot be negative");
		this.timeStart = timeStart;
	}

	public int getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(int timeStep) {
		if (time <= 0)
			throw new IllegalArgumentException("time step must be positive");
		this.timeStep = timeStep;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(int currentTime) {
		if (currentTime < 0 || currentTime > time) //TODO: is that correct? kratno na timeStep???
			throw new IllegalArgumentException();
		this.currentTime = currentTime;
	}
	
	public void increaseTime() {
		currentTime += timeStep; //TODO: if > time?
	}
	
	public void decreaseTime() {
		currentTime -= timeStep; //TODO: if < 0?
	}

	public boolean isTokenSplittingEnabled() {
		return this.tokenSplittingEnabled;
	}
	
	public void setTokenSplittingEnabled(boolean value) {
		this.tokenSplittingEnabled = value;
	}
	
	public boolean isRoot() {
		return root;
	}
	
	public void setRoot(boolean root) {
		this.root = root;
	}
	
	public GeneralizedNet(String name) {
		this(name, SettingsManager.getInstance().getProperty(MainConfigProperties.DEFAULT_FUNCTION_LANGUAGE));
	}
	
	public GeneralizedNet(String name, String functionLanguage) {
		this.transitions = new GnList<Transition>();
		this.transitions.setParent(this);
		this.places = new GnList<Place>();
		this.places.setParent(this);
		this.tokens = new GnList<Token>();
		this.tokens.setParent(this);
		this.functions = "";
		//Default Visual Parameters:
		this.visualParameters = new VisualParameters(15, new Point(30, 15));
		this.originalState = null;
		this.name = name;
		this.time = 256; //256?
		this.timeStep = 1;
		this.timeStart = 0;
		this.currentTime = 0;
		this.tokenSplittingEnabled = true;
		this.root = true;
		this.functionLanguage = functionLanguage;
	}
	
	public static GeneralizedNet generateMinimalValidGn() {
		String name = SettingsManager.getInstance().getProperty(MainConfigProperties.NEW_GN_NAME);
		GeneralizedNet gn = new GeneralizedNet(name);
		int gridStep = gn.getVisualParameters().getGridStep();
		Transition transition = new Transition("Z1");
		transition.setVisualPosition(10 * gridStep, 10 * gridStep);
		transition.setVisualHeight(10 * gridStep);
		gn.getTransitions().add(transition);
		Place place = new Place("l1");
		place.setVisualPosition(5 * gridStep, 14 * gridStep);
		gn.getPlaces().add(place);
		transition.getInputs().add(place);
		transition.getOutputs().add(place);
		return gn;
	}

	//TODO: argument checking, exceptions...
	
	public void prepareForSimulation() {
		
		originalState = new OriginalState(this);
		for (Token t: tokens) {
			t.setHost(null);
		}
		//setChanged();
		//notifyObservers();
	}
	
	public void restoreState() {
		if (originalState != null) {
			originalState.restore(this);
			originalState = null;
			//setChanged();
			//notifyObservers(); //TODO: object
		}
	}
	
//	public boolean isSimulationMode() {
//		return originalState != null;
//	}
	
	//TODO: do we need the following 3 functions?
	public void enterToken(Token token, Place place) {
		//if the token doesn't exist, add it:
		if (getTokens().get(token.getId()) == null)
			tokens.add(token);
		token.setHost(place);
	}
	
	public void moveTokenTo(Token token, Place place) {
		token.setHost(place);
	}
	
	public void leaveToken(Token token) {
		token.setHost(null);
	}
	
	public void remove(Object object) {
		if (object instanceof Token) {
			tokens.remove(object);
		} else if (object instanceof Place) { 
			//...trqbva da se triqt i referenciite kam nego... i u4astiqta v predikati...
			places.remove(object);
		} else if (object instanceof Transition) {
			transitions.remove(object);
//		} else if (object instanceof Function) {
//			functions.remove(object);
		} //TODO: else - Exception
	}

	/**
	 * @param functionLanguage the function language to set. Can be empty.
	 */
	public void setFunctionLanguage(String functionLanguage) {
		if (functionLanguage == null) {
			this.functionLanguage = "";
		} else {
			this.functionLanguage = functionLanguage;
		}
	}

	/**
	 * @return the function language
	 */
	public String getFunctionLanguage() {
		return functionLanguage;
	}
	
	public FunctionFactory getFunctionFactory() {
		String language = getFunctionLanguage();
		if (language.length() == 0) {
			language = SettingsManager.getInstance().getProperty(MainConfigProperties.DEFAULT_FUNCTION_LANGUAGE);
		}
		return FunctionFactory.getFactory(language);
	}
}
