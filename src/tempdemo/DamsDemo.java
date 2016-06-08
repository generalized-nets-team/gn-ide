package tempdemo;

import java.util.ArrayList;
import java.util.Random;

import net.generalised.genedit.demo.Demo;
import net.generalised.genedit.demo.NumberInputDialog;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnUtil;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.simulation.model.EnterEvent;
import net.generalised.genedit.simulation.model.GnEvents;
import net.generalised.genedit.simulation.model.LeaveEvent;
import net.generalised.genedit.simulation.model.Simulation;
import net.generalised.genedit.simulation.model.SimulationException;

import org.eclipse.swt.widgets.Display;

public class DamsDemo implements Demo {

	public String getGnXml() {
		return "<?xml version=\"1.0\" ?><gn xmlns=\"http://www.clbme.bas.bg/GN\" name=\"Demo\" time=\"256\" timeStart=\"0\" timeStep=\"1\" root=\"true\"><transitions><transition id=\"Z1\" name=\"Z1\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"100\" positionY=\"40\" sizeY=\"240\"><inputs><input ref=\"L1\"><arc><point positionX=\"50\" positionY=\"80\"/><point positionX=\"100\" positionY=\"80\"/></arc></input><input ref=\"L4\"><arc><point positionX=\"150\" positionY=\"260\"/><point positionX=\"190\" positionY=\"260\"/><point positionX=\"190\" positionY=\"300\"/><point positionX=\"80\" positionY=\"300\"/><point positionX=\"80\" positionY=\"260\"/><point positionX=\"100\" positionY=\"260\"/></arc></input></inputs><outputs><output ref=\"L2\"><arc><point positionX=\"100\" positionY=\"140\"/><point positionX=\"150\" positionY=\"140\"/></arc></output><output ref=\"L3\"><arc><point positionX=\"100\" positionY=\"200\"/><point positionX=\"150\" positionY=\"200\"/></arc></output><output ref=\"L4\"><arc><point positionX=\"100\" positionY=\"260\"/><point positionX=\"150\" positionY=\"260\"/></arc></output></outputs><predicates><predicate input=\"L1\" output=\"L2\">false</predicate><predicate input=\"L1\" output=\"L3\">false</predicate><predicate input=\"L1\" output=\"L4\">false</predicate><predicate input=\"L4\" output=\"L2\">false</predicate><predicate input=\"L4\" output=\"L3\">false</predicate><predicate input=\"L4\" output=\"L4\">false</predicate></predicates></transition><transition id=\"Z2\" name=\"Z2\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"200\" positionY=\"40\" sizeY=\"120\"><inputs><input ref=\"L2\"><arc><point positionX=\"150\" positionY=\"140\"/><point positionX=\"200\" positionY=\"140\"/></arc></input></inputs><outputs><output ref=\"L5\"><arc><point positionX=\"200\" positionY=\"80\"/><point positionX=\"250\" positionY=\"80\"/></arc></output><output ref=\"L6\"><arc><point positionX=\"200\" positionY=\"140\"/><point positionX=\"250\" positionY=\"140\"/></arc></output></outputs><predicates><predicate input=\"L2\" output=\"L5\">false</predicate><predicate input=\"L2\" output=\"L6\">false</predicate></predicates></transition><transition id=\"Z3\" name=\"Z3\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"360\" positionY=\"40\" sizeY=\"240\"><inputs><input ref=\"L7\"><arc><point positionX=\"310\" positionY=\"80\"/><point positionX=\"360\" positionY=\"80\"/></arc></input><input ref=\"L3\"><arc><point positionX=\"150\" positionY=\"200\"/><point positionX=\"360\" positionY=\"200\"/></arc></input><input ref=\"L6\"><arc><point positionX=\"250\" positionY=\"140\"/><point positionX=\"360\" positionY=\"140\"/></arc></input><input ref=\"L10\"><arc><point positionX=\"410\" positionY=\"260\"/><point positionX=\"450\" positionY=\"260\"/><point positionX=\"450\" positionY=\"300\"/><point positionX=\"340\" positionY=\"300\"/><point positionX=\"340\" positionY=\"260\"/><point positionX=\"360\" positionY=\"260\"/></arc></input></inputs><outputs><output ref=\"L8\"><arc><point positionX=\"360\" positionY=\"140\"/><point positionX=\"410\" positionY=\"140\"/></arc></output><output ref=\"L9\"><arc><point positionX=\"360\" positionY=\"200\"/><point positionX=\"410\" positionY=\"200\"/></arc></output><output ref=\"L10\"><arc><point positionX=\"360\" positionY=\"260\"/><point positionX=\"410\" positionY=\"260\"/></arc></output></outputs><predicates><predicate input=\"L7\" output=\"L8\">false</predicate><predicate input=\"L7\" output=\"L9\">false</predicate><predicate input=\"L7\" output=\"L10\">false</predicate><predicate input=\"L3\" output=\"L8\">false</predicate><predicate input=\"L3\" output=\"L9\">false</predicate><predicate input=\"L3\" output=\"L10\">false</predicate><predicate input=\"L6\" output=\"L8\">false</predicate><predicate input=\"L6\" output=\"L9\">false</predicate><predicate input=\"L6\" output=\"L10\">false</predicate><predicate input=\"L10\" output=\"L8\">false</predicate><predicate input=\"L10\" output=\"L9\">false</predicate><predicate input=\"L10\" output=\"L10\">false</predicate></predicates></transition><transition id=\"Z4\" name=\"Z4\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"460\" positionY=\"40\" sizeY=\"120\"><inputs><input ref=\"L8\"><arc><point positionX=\"410\" positionY=\"140\"/><point positionX=\"460\" positionY=\"140\"/></arc></input></inputs><outputs><output ref=\"L11\"><arc><point positionX=\"460\" positionY=\"80\"/><point positionX=\"510\" positionY=\"80\"/></arc></output><output ref=\"L12\"><arc><point positionX=\"460\" positionY=\"140\"/><point positionX=\"510\" positionY=\"140\"/></arc></output></outputs><predicates><predicate input=\"L8\" output=\"L11\">false</predicate><predicate input=\"L8\" output=\"L12\">false</predicate></predicates></transition><transition id=\"Z5\" name=\"Z5\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"620\" positionY=\"40\" sizeY=\"240\"><inputs><input ref=\"L13\"><arc><point positionX=\"570\" positionY=\"80\"/><point positionX=\"620\" positionY=\"80\"/></arc></input><input ref=\"L15\"><arc><point positionX=\"670\" positionY=\"260\"/><point positionX=\"710\" positionY=\"260\"/><point positionX=\"710\" positionY=\"300\"/><point positionX=\"600\" positionY=\"300\"/><point positionX=\"600\" positionY=\"260\"/><point positionX=\"620\" positionY=\"260\"/></arc></input><input ref=\"L12\"><arc><point positionX=\"510\" positionY=\"140\"/><point positionX=\"620\" positionY=\"140\"/></arc></input><input ref=\"L9\"><arc><point positionX=\"410\" positionY=\"200\"/><point positionX=\"620\" positionY=\"200\"/></arc></input></inputs><outputs><output ref=\"L14\"><arc><point positionX=\"620\" positionY=\"140\"/><point positionX=\"670\" positionY=\"140\"/></arc></output><output ref=\"L15\"><arc><point positionX=\"620\" positionY=\"260\"/><point positionX=\"670\" positionY=\"260\"/></arc></output></outputs><predicates><predicate input=\"L13\" output=\"L14\">false</predicate><predicate input=\"L13\" output=\"L15\">false</predicate><predicate input=\"L15\" output=\"L14\">false</predicate><predicate input=\"L15\" output=\"L15\">false</predicate><predicate input=\"L12\" output=\"L14\">false</predicate><predicate input=\"L12\" output=\"L15\">false</predicate><predicate input=\"L9\" output=\"L14\">false</predicate><predicate input=\"L9\" output=\"L15\">false</predicate></predicates></transition></transitions><places><place id=\"L1\" name=\"L1\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"50\" positionY=\"80\"/><place id=\"L4\" name=\"L4\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"150\" positionY=\"260\"/><place id=\"L2\" name=\"L2\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"150\" positionY=\"140\"/><place id=\"L3\" name=\"L3\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"150\" positionY=\"200\"/><place id=\"L5\" name=\"L5\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"250\" positionY=\"80\"/><place id=\"L6\" name=\"L6\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"250\" positionY=\"140\"/><place id=\"L7\" name=\"L7\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"310\" positionY=\"80\"/><place id=\"L10\" name=\"L10\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"410\" positionY=\"260\"/><place id=\"L8\" name=\"L8\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"410\" positionY=\"140\"/><place id=\"L9\" name=\"L9\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"410\" positionY=\"200\"/><place id=\"L11\" name=\"L11\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"510\" positionY=\"80\"/><place id=\"L12\" name=\"L12\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"510\" positionY=\"140\"/><place id=\"L13\" name=\"L13\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"570\" positionY=\"80\"/><place id=\"L15\" name=\"L15\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"670\" positionY=\"260\"/><place id=\"L14\" name=\"L14\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"670\" positionY=\"140\"/></places><tokens><generator type=\"periodic\" period=\"1\" id=\"Alpha1\" name=\"Alpha1\" priority=\"0\" host=\"L1\" entering=\"0\" leaving=\"-1\"><char name=\"Color\" type=\"string\" history=\"1\">blue</char></generator><generator type=\"periodic\" period=\"1\" id=\"Alpha2\" name=\"Alpha2\" priority=\"0\" host=\"L7\" entering=\"0\" leaving=\"-1\"><char name=\"Color\" type=\"string\" history=\"1\">blue</char></generator><generator type=\"periodic\" period=\"1\" id=\"Alpha3\" name=\"Alpha3\" priority=\"0\" host=\"L13\" entering=\"0\" leaving=\"-1\"><char name=\"Color\" type=\"string\" history=\"1\">blue</char></generator><token id=\"Alpha4\" name=\"Alpha4\" priority=\"0\" host=\"L4\" entering=\"0\" leaving=\"-1\"><char name=\"Color\" type=\"string\" history=\"1\">blue</char>	</token><token id=\"Alpha5\" name=\"Alpha5\" priority=\"0\" host=\"L10\" entering=\"0\" leaving=\"-1\"><char name=\"Color\" type=\"string\" history=\"1\">blue</char>	</token><token id=\"Alpha6\" name=\"Alpha6\" priority=\"0\" host=\"L15\" entering=\"0\" leaving=\"-1\"><char name=\"Color\" type=\"string\" history=\"1\">blue</char>	</token></tokens><functions/></gn>";
	}

	public Simulation createSimulation(GnDocument document) {
		try {
			return new YazoviriSimulation(document, document.getModel());
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static class YazoviriSimulation extends Simulation {

		// 1 - dam Kircali
		// 2 - dam Studen Kladenec
		// 3 - dam Ivaylovgrad;
		
		private double capacity1;
		private double capacity2;
		private double capacity3;
		
		private double pritok1;
		private double pritok2;
		private double pritok3;
		
		private double kWh_per_SingleWaterUnit;
		
		private static final Random random = new Random();

		public YazoviriSimulation(GnDocument document, GeneralizedNet gn) throws SimulationException {
			super(document, gn);
		}

		@Override
		public void start() {
			super.start();
			
			NumberInputDialog dialog = new NumberInputDialog(Display.getCurrent().getActiveShell());
			Double[] values = dialog.open(
					"Капацитет на яз. Кърджали (куб.м.):",
					"Капацитет на яз. Студен кладенец (куб.м.):",
					"Капацитет на яз. Ивайловград (куб.м.):",
					"Притоци към яз. Кърджали на ден (куб.м.):",
					"Притоци към яз. Студен кладенец на ден (куб.м.):",
					"Притоци към яз. Ивайловград на ден (куб.м.):",
					"Произведен ток от 1000 куб.м. вода (кВч):"
			);

			for (int i = 0; i < values.length; ++i)
				if (values[i] == null || values[i].doubleValue() < 0) {
					//showErrorAndStop();
					return;
				}
			
			capacity1 = values[0].doubleValue();
			capacity2 = values[1].doubleValue();
			capacity3 = values[2].doubleValue();

			pritok1 = values[3].intValue();
			pritok2 = values[4].intValue();
			pritok3 = values[5].intValue();
			
			kWh_per_SingleWaterUnit = values[6].intValue();
		}
		
		@Override
		protected void closeEventSource() throws SimulationException {
		}

		private ArrayList<Characteristic> createCharList(String defaultCharType, String defaultCharValue) {
			ArrayList<Characteristic> chars = new ArrayList<Characteristic>();
			Characteristic ch = new Characteristic(defaultCharType, 1);
			ch.setValue(defaultCharValue);
			chars.add(ch);
			return chars;
		}
		
		private EnterEvent createEnterEvent(GeneralizedNet gn, String tokenId,
					String defaultCharType, String defaultCharValue, String color, String placeId) {
			
			Token token = new Token(tokenId, null);
			ArrayList<Characteristic> chars = createCharList(defaultCharType, defaultCharValue);
			Characteristic ch = new Characteristic("string", 1);
			ch.setName("Color");
			ch.setValue(color);
			chars.add(ch);
			EnterEvent ee = new EnterEvent(gn, token, gn.getPlaces().get(placeId), chars);
			return ee;
		}
		
		@Override
		protected GnEvents getNextEvents(int count) throws SimulationException {
			GeneralizedNet gn = getGn();
			GnEvents result = new GnEvents(gn);
			
			if (gn.getCurrentTime() == 0) {
				result.add(createEnterEvent(gn, "WaterKircali",
						"double", "" + 0, "blue", "L4")); //XXX 0? Empty dam?
				result.add(createEnterEvent(gn, "WaterStudenKladenets",
						"double", "" + 0, "blue", "L10")); //XXX 0? Empty dam?
				result.add(createEnterEvent(gn, "WaterIvaylovgrad",
						"double", "" + 0, "blue", "L15")); //XXX 0? Empty dam?
			}
			
			result.add(createEnterEvent(gn, "Water1_" + (gn.getCurrentTime() + 1),
					"double", "" + pritok1, "blue", "L1"));

			result.add(createEnterEvent(gn, "Water2_" + (gn.getCurrentTime() + 1),
					"double", "" + pritok2, "blue", "L7"));

			result.add(createEnterEvent(gn, "Water3_" + (gn.getCurrentTime() + 1),
					"double", "" + pritok3, "blue", "L13"));

			if (gn.getCurrentTime() > 0) {
				
				for (int damNumber = 0; damNumber < 2; damNumber++) {
					int percentsForVets = random.nextInt(100);
					int percentsForDrain = random.nextInt(100 - percentsForVets);
					int percentsForDam = 100 - percentsForVets - percentsForDrain;
					//pyrvo smqtai poslednoto, naloji mu ograni4enie da ne e pove4e ot kapaciteta na qzovira
					
					Token pritokToken = GnUtil.getSingleTokenAt(gn, "L" + (1 + damNumber * 6));
					String pritokTokenId = pritokToken.getId();
					Token damToken = GnUtil.getSingleTokenAt(gn, "L" + (4 + damNumber * 6));
					Token previousDrainToken = gn.getCurrentTime() > 0 ? GnUtil.getSingleTokenAt(gn, "L" + (damNumber * 6 - 3)) : null; 
					
					double totalWater = Double.parseDouble(pritokToken.getDefaultCharacteristic().getValue())
							+ Double.parseDouble(damToken.getDefaultCharacteristic().getValue());

					if (previousDrainToken != null) {
						totalWater += Double.parseDouble(previousDrainToken.getDefaultCharacteristic().getValue());
						result.add(new LeaveEvent(gn, pritokToken, gn.getPlaces().get("L" + (damNumber * 6 - 3)), new ArrayList<Characteristic>()));
					}
					
					result.add(new LeaveEvent(gn, pritokToken, gn.getPlaces().get("L" + (1 + damNumber * 6)), new ArrayList<Characteristic>()));

					if (percentsForVets > 0) {
						result.add(createEnterEvent(gn, pritokTokenId + "_1",
								"double", "" + totalWater * percentsForVets / 100, "blue", "L" + (2 + damNumber * 6)));

					}

					if (percentsForDrain > 0) {
						result.add(createEnterEvent(gn, pritokTokenId + "_2",
								"double", "" + totalWater * percentsForDrain / 100, "blue", "L" + (3 + damNumber * 6)));

					}
					
					if (percentsForDam > 0) {
						damToken.getDefaultCharacteristic().setValue("" + (totalWater * percentsForDam / 100));
					}
				}
			}
			//...
			
			return result;
		}

	}
}
