package net.generalised.genedit.model.dataaccess.xmlread;

import net.generalised.genedit.baseapp.StringUtil;
import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.FunctionFactory;
import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GeneratorMode;
import net.generalised.genedit.model.gn.GnObjectCommon;
import net.generalised.genedit.model.gn.GnObjectWithDimensions;
import net.generalised.genedit.model.gn.GnObjectWithPosition;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;
import net.generalised.genedit.model.gn.RichTextName;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.TokenGenerator;
import net.generalised.genedit.model.gn.Transition;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * A class that handles all SAX events that can be fired when parsing a GN model
 * 
 * @author Dimitar Dimitrov
 *
 */
//TODO: remove "public"!
public class GnHandler extends DefaultHandler2
{
	//TODO: string constants for xml elements - in base class, protected

	private GeneralizedNet gn;
	
	public GeneralizedNet getGn() {
		return gn;
	}
	
	private Transition currentTransition;
	private Place currentPlace;
	private PlaceReference currentPlaceReference;
	private Token currentToken;
	private Characteristic currentCharacteristic;
	private Place currentInput;
	private Place currentOutput;
	
	private String currentCharacters;
	private StringBuffer functionsCharacters;
	private boolean currentElementIsFunctions;
	
	//private HashMap<String, Function> functionReferences; // <String, FunctionReference>
	//private FunctionFactory functionFactory;

	public GnHandler() {
		super();
		this.gn = new GeneralizedNet("");
		this.currentTransition = null;
		this.currentPlace = null;
		this.currentInput = null;
		this.currentOutput = null;
		this.currentPlaceReference = null;
		this.currentToken = null;
		this.currentToken = null;
		this.currentCharacteristic = null;
		this.functionsCharacters = new StringBuffer();
		this.currentElementIsFunctions = false;
		//this.functionReferences = new HashMap<String, Function>();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (!currentElementIsFunctions) {
			currentCharacters += new String(ch, start, length);
		} else {
			functionsCharacters.append(new String(ch, start, length));
		}
	}

	private void readCommonAttributes(Attributes attributes, GnObjectCommon gnObjectCommon) {
		String id = attributes.getValue("id");
		gnObjectCommon.setId(id);

		String name = attributes.getValue("name");
		if (name != null)
			gnObjectCommon.setName(new RichTextName(name));
		
		String priority = attributes.getValue("priority");
		if (priority != null)
			gnObjectCommon.setPriority(Integer.parseInt(priority));
	}
	
	private void readPositionAttributes(Attributes attributes, GnObjectWithPosition gnObject) {
		String positionX = attributes.getValue("positionX");
		if (positionX != null)
			gnObject.setVisualPositionX(Integer.parseInt(positionX));
		// TODO: positionX can be double? keep in mind that!

		String positionY = attributes.getValue("positionY");
		if (positionY != null)
			gnObject.setVisualPositionY(Integer.parseInt(positionY));
	}
	
	private void readDimensionAttributes(Attributes attributes, GnObjectWithDimensions gnObject) {
		String positionX = attributes.getValue("sizeX");
		if (positionX != null)
			gnObject.setVisualWidth(Integer.parseInt(positionX));
		// TODO: sizeX can be double? keep in mind that!

		String positionY = attributes.getValue("sizeY");
		if (positionY != null)
			gnObject.setVisualHeight(Integer.parseInt(positionY));
	}

	@Override
	public void startElement(String uri, String localName, String tagName, Attributes attributes) throws SAXException {
		// namespace gn; maybe proceed only if gn: or nothing?
		// String... = localName.eq...; switch(...)
		if (localName.equals("gn")) {
			gn.setName(attributes.getValue("name"));
			// TODO: xmlns:ns, xsd, ...

			String time = attributes.getValue("time"); 
			if (time != null)
				gn.setTime(Integer.parseInt(time));

			String timeStep = attributes.getValue("timeStep"); 
			if (timeStep != null)
				gn.setTimeStep(Integer.parseInt(timeStep));
			
			String timeStart = attributes.getValue("timeStart"); 
			if (timeStart != null)
				gn.setTimeStart(Integer.parseInt(timeStart));
			
			String language = attributes.getValue("language");
			if (language != null)
				gn.setFunctionLanguage(language);
			//functionFactory = gn.getFunctionFactory();
			
			//TODO: fundefs, root, positionX, positionY, sizeX, sizeY
		}
		else if (localName.equals("transition")) {
			currentTransition = new Transition(""); //...

			readCommonAttributes(attributes, currentTransition);

			readPositionAttributes(attributes, currentTransition);
			
			String startTime = attributes.getValue("startTime");
			if (startTime != null)
				currentTransition.setStartTime(Integer.parseInt(startTime));

			String lifeTime = attributes.getValue("lifeTime");
			if (lifeTime != null)
				currentTransition.setLifeTime(new IntegerInf(Integer.parseInt(lifeTime)));

			String type = attributes.getValue("type");
			if (type != null)
				currentTransition.setType(type);

			readDimensionAttributes(attributes, currentTransition);

			gn.getTransitions().add(currentTransition);
		}
		else if (localName.equals("input")
				|| localName.equals("output")) {
			String ref = attributes.getValue("ref");
			currentPlace = gn.getPlaces().get(ref);
			if (currentPlace == null) {
				currentPlace = new Place(ref);
				gn.getPlaces().add(currentPlace);
			}
			if (localName.equals("input")) {
				currentPlaceReference = currentTransition.getInputs().add(currentPlace);
			}
			else {
				currentPlaceReference = currentTransition.getOutputs().add(currentPlace);
			}
		}
		else if (localName.equals("point")) {
			//TODO: following attributes are not mandatory in xsd!
			Point point = new Point();
			readPositionAttributes(attributes, point);
			currentPlaceReference.getArc().add(point); //TODO: this doesn't call a setter
		}
		else if (localName.equals("predicate")) {
			String input = attributes.getValue("input");
			currentInput = gn.getPlaces().get(input);
			String output = attributes.getValue("output");
			currentOutput = gn.getPlaces().get(output);
			//TODO: if == null... or maybe xsd doesn't allow this?
		}
		else if (localName.equals("place")) {
			String id = attributes.getValue("id");
			currentPlace = gn.getPlaces().get(id);
			if (currentPlace == null) {
				currentPlace = new Place(id);
				gn.getPlaces().add(currentPlace);
			}

			readCommonAttributes(attributes, currentPlace);

			readPositionAttributes(attributes, currentPlace);

			String charFunctionName = attributes.getValue("char");
			if (charFunctionName != null) {
//				Function function = functionReferences.get(charFunctionName);
//				if (function == null) {
//					function = functionFactory.createEmptyFunction(charFunctionName);
//					functionReferences.put(charFunctionName, function);
//				}
				currentPlace.setCharFunction(new FunctionReference(charFunctionName));
			}

			String capacity = attributes.getValue("capacity");
			if (capacity != null)
				currentPlace.setCapacity(new IntegerInf(Integer.parseInt(capacity)));

			String merge = attributes.getValue("merge");
			if (merge != null)
				currentPlace.setMerge(Boolean.parseBoolean(merge));
			
			String mergeRule = attributes.getValue("mergeRule");
			if (mergeRule != null) {
//				Function function = functionReferences.get(mergeRule);
//				if (function == null) {
//					function = functionFactory.createEmptyFunction(mergeRule);
//					functionReferences.put(mergeRule, function);
//				}
				currentPlace.setMergeRule(new FunctionReference(mergeRule));
			}

			//TODO: this is nice, but last modification don't call setters (notifyObservers)
		}
		else if (localName.equals("token")
				|| localName.equals("generator")) {
			String id = attributes.getValue("id");
			String hostName = attributes.getValue("host");
			Place host = gn.getPlaces().get(hostName);
			//if host != null... no problem
			if (localName.equals("token"))
				currentToken = new Token(id, host);
			else {
				String type = attributes.getValue("type");
				GeneratorMode mode = null;
				if (type.equalsIgnoreCase("conditional"))
					mode = GeneratorMode.CONDITIONAL;
				else if (type.equalsIgnoreCase("periodic"))
					mode = GeneratorMode.PERIODIC;
				else if (type.equalsIgnoreCase("random"))
					mode = GeneratorMode.RANDOM;
				//else - default mode?
				currentToken = new TokenGenerator(id, host, mode);
			}

			readCommonAttributes(attributes, currentToken);
			
			String entering = attributes.getValue("entering");
			if (entering != null)
				currentToken.setEnteringTime(Integer.parseInt(entering));

			String leaving = attributes.getValue("leaving");
			if (leaving != null)
				currentToken.setLeavingTime(new IntegerInf(Integer.parseInt(leaving)));
			
			if (localName.equals("generator")) {
				String predicateName = attributes.getValue("predicate");
				if (predicateName != null) {
					//Function predicate = functionFactory.createEmptyFunction(predicateName);
					FunctionReference predicate = new FunctionReference(predicateName);
					((TokenGenerator)currentToken).setPredicate(predicate);
				}
				
				String period = attributes.getValue("period");
				if (period != null)
					((TokenGenerator)currentToken).setPeriod(Integer.parseInt(period));
			}
			
			gn.getTokens().add(currentToken);
		}
		else if (localName.equals("char")) {
			String type = attributes.getValue("type");
			int historyDepth = 1;
			String history = attributes.getValue("history");
			if (history != null)
				historyDepth = Integer.parseInt(history);
			currentCharacteristic = new Characteristic(type, historyDepth);
			
			String name = attributes.getValue("name");
			if (name != null)
				currentCharacteristic.setName(name);
			
			currentToken.getChars().add(currentCharacteristic);
		}
		else if (localName.equals("functions")) {
			currentElementIsFunctions = true;
		}
		else if (localName.equals("placeRadius")) {
			//
		} //metric, transitionTriangleSize...
		
		currentCharacters = ""; //without this, <char /> will have value "\n    " or something similar
	}

	// ne moje vsi4ko tuk, za6toto ima mnogo vlojeni elementi! inak 6te6e da e lesno - samo currentAttributes
	@Override
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
		if (localName.equals("functions")) {

			//FIXME: (P)CDATA!
			
			String functions = functionsCharacters.toString();
			
			if (StringUtil.isNullOrEmpty(gn.getFunctionLanguage())) {
				String actualLanguage = FunctionFactory.detectLanguage(functions);
				if (actualLanguage != null) {
					this.gn.setFunctionLanguage(actualLanguage);
				}
			}
			
//			List<? extends Function> functions = new GntcflParser().parseFunctions(functionsCharacters.toString());
//			for (Function f : functions) {
//				Function added = functionReferences.get(f.getName());
//				if (added != null) {
//					added.setDefinition(f.getDefinition());
//					gn.getFunctions().add(added);
//				} else {
//					gn.getFunctions().add(f);
//				}
//			}
			gn.setFunctions(functions);
			
			currentElementIsFunctions = false;
		}
		else if (localName.equals("predicate")) {
//			Function p = functionReferences.get(currentCharacters);
//			if (p == null) {
//				p = functionFactory.createEmptyFunction(currentCharacters);
//				functionReferences.put(currentCharacters, p);
//			}
			FunctionReference ref = new FunctionReference(currentCharacters);
			currentTransition.getPredicates().setAt(currentInput, currentOutput, ref);
		}
		else if (localName.equals("capacities")) {
			String[] numbers = currentCharacters.split(" ");//can use limit = inputs*outputs
			//or use Scanner?
			//java.util.Scanner sc = new java.util.Scanner(currentCharacters);
			//TODO: numbers.length must be equal to inputs*outputs; then create 2D matrix
			//default = -1!
		}
		else if (localName.equals("char")) {
//			//TODO: check the history for empty 1st value
			currentCharacteristic.setValue(currentCharacters);
		}
		
		currentCharacters = null;
	}
	
	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		String comment = new String(ch, start, length);
		// TODO
	}
}