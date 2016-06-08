package tempdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import net.generalised.genedit.demo.Demo;
import net.generalised.genedit.demo.NumberInputDialog;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnUtil;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.simulation.model.EnterEvent;
import net.generalised.genedit.simulation.model.GnEvent;
import net.generalised.genedit.simulation.model.GnEvents;
import net.generalised.genedit.simulation.model.LeaveEvent;
import net.generalised.genedit.simulation.model.MoveEvent;
import net.generalised.genedit.simulation.model.Simulation;
import net.generalised.genedit.simulation.model.SimulationException;

public class NeftochimDemo implements Demo {

	public String getGnXml() {
		return "<?xml version=\"1.0\" ?><gn xmlns=\"http://www.clbme.bas.bg/GN\" name=\"Neftochim\" time=\"256\" timeStart=\"0\" timeStep=\"1\" root=\"true\">  <transitions>    <transition id=\"Z1\" name=\"Z1\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"80\" positionY=\"40\" sizeY=\"420\">      <inputs>        <input ref=\"L1\">          <arc>            <point positionX=\"30\" positionY=\"270\"/>            <point positionX=\"80\" positionY=\"270\"/>          </arc>        </input>        <input ref=\"M1\">          <arc>            <point positionX=\"30\" positionY=\"440\"/>            <point positionX=\"80\" positionY=\"440\"/>          </arc>        </input>      </inputs>      <outputs>        <output ref=\"L2\">          <arc>            <point positionX=\"80\" positionY=\"80\"/>            <point positionX=\"140\" positionY=\"80\"/>          </arc>        </output>        <output ref=\"L3\">          <arc>            <point positionX=\"80\" positionY=\"230\"/>            <point positionX=\"140\" positionY=\"230\"/>          </arc>        </output>        <output ref=\"L4\">          <arc>            <point positionX=\"80\" positionY=\"380\"/>            <point positionX=\"140\" positionY=\"380\"/>          </arc>        </output>        <output ref=\"M2\">          <arc>            <point positionX=\"80\" positionY=\"440\"/>            <point positionX=\"140\" positionY=\"440\"/>          </arc>        </output>      </outputs>      <predicates>        <predicate input=\"L1\" output=\"L2\">true</predicate>        <predicate input=\"L1\" output=\"L3\">true</predicate>        <predicate input=\"L1\" output=\"L4\">true</predicate>        <predicate input=\"L1\" output=\"M2\">false</predicate>        <predicate input=\"M1\" output=\"L2\">false</predicate>        <predicate input=\"M1\" output=\"L3\">false</predicate>        <predicate input=\"M1\" output=\"L4\">false</predicate>        <predicate input=\"M1\" output=\"M2\">true</predicate>      </predicates>    </transition>    <transition id=\"Z2\" name=\"Z2\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"220\" positionY=\"40\" sizeY=\"100\">      <inputs>        <input ref=\"L2\">          <arc>            <point positionX=\"140\" positionY=\"80\"/>            <point positionX=\"220\" positionY=\"80\"/>          </arc>        </input>        <input ref=\"L5\">          <arc>            <point positionX=\"270\" positionY=\"60\"/>            <point positionX=\"310\" positionY=\"60\"/>            <point positionX=\"310\" positionY=\"8\"/>            <point positionX=\"180\" positionY=\"8\"/>            <point positionX=\"180\" positionY=\"60\"/>            <point positionX=\"220\" positionY=\"60\"/>          </arc>        </input>      </inputs>      <outputs>        <output ref=\"L5\">          <arc>            <point positionX=\"220\" positionY=\"60\"/>            <point positionX=\"270\" positionY=\"60\"/>          </arc>        </output>        <output ref=\"L6\">          <arc>            <point positionX=\"220\" positionY=\"120\"/>            <point positionX=\"270\" positionY=\"120\"/>          </arc>        </output>      </outputs>      <predicates>        <predicate input=\"L2\" output=\"L5\">true</predicate>        <predicate input=\"L2\" output=\"L6\">true</predicate>        <predicate input=\"L5\" output=\"L5\">true</predicate>        <predicate input=\"L5\" output=\"L6\">true</predicate>      </predicates>    </transition>    <transition id=\"Z3\" name=\"Z3\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"220\" positionY=\"190\" sizeY=\"100\">      <inputs>        <input ref=\"L3\">          <arc>            <point positionX=\"140\" positionY=\"230\"/>            <point positionX=\"220\" positionY=\"230\"/>          </arc>        </input>        <input ref=\"L7\">          <arc>            <point positionX=\"270\" positionY=\"210\"/>            <point positionX=\"310\" positionY=\"210\"/>            <point positionX=\"310\" positionY=\"150\"/>            <point positionX=\"180\" positionY=\"150\"/>            <point positionX=\"180\" positionY=\"210\"/>            <point positionX=\"220\" positionY=\"210\"/>          </arc>        </input>      </inputs>      <outputs>        <output ref=\"L7\">          <arc>            <point positionX=\"220\" positionY=\"210\"/>            <point positionX=\"270\" positionY=\"210\"/>          </arc>        </output>        <output ref=\"L8\">          <arc>            <point positionX=\"220\" positionY=\"270\"/>            <point positionX=\"270\" positionY=\"270\"/>          </arc>        </output>      </outputs>      <predicates>        <predicate input=\"L3\" output=\"L7\">true</predicate>        <predicate input=\"L3\" output=\"L8\">true</predicate>        <predicate input=\"L7\" output=\"L7\">true</predicate>        <predicate input=\"L7\" output=\"L8\">true</predicate>      </predicates>    </transition>    <transition id=\"Z4\" name=\"Z4\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"220\" positionY=\"340\" sizeY=\"100\">      <inputs>        <input ref=\"L4\">          <arc>            <point positionX=\"140\" positionY=\"380\"/>            <point positionX=\"220\" positionY=\"380\"/>          </arc>        </input>        <input ref=\"L9\">          <arc>            <point positionX=\"270\" positionY=\"360\"/>            <point positionX=\"310\" positionY=\"360\"/>            <point positionX=\"310\" positionY=\"300\"/>            <point positionX=\"180\" positionY=\"300\"/>            <point positionX=\"180\" positionY=\"360\"/>            <point positionX=\"220\" positionY=\"360\"/>          </arc>        </input>      </inputs>      <outputs>        <output ref=\"L9\">          <arc>            <point positionX=\"220\" positionY=\"360\"/>            <point positionX=\"270\" positionY=\"360\"/>          </arc>        </output>        <output ref=\"L10\">          <arc>            <point positionX=\"220\" positionY=\"420\"/>            <point positionX=\"270\" positionY=\"420\"/>          </arc>        </output>      </outputs>      <predicates>        <predicate input=\"L4\" output=\"L9\">true</predicate>        <predicate input=\"L4\" output=\"L10\">true</predicate>        <predicate input=\"L9\" output=\"L9\">true</predicate>        <predicate input=\"L9\" output=\"L10\">true</predicate>      </predicates>    </transition>    <transition id=\"Z5\" name=\"Z5\" priority=\"0\" startTime=\"0\" lifeTime=\"-1\" positionX=\"370\" positionY=\"40\" sizeY=\"400\">      <inputs>        <input ref=\"L6\">          <arc>            <point positionX=\"270\" positionY=\"120\"/>            <point positionX=\"370\" positionY=\"120\"/>          </arc>        </input>        <input ref=\"L8\">          <arc>            <point positionX=\"270\" positionY=\"270\"/>            <point positionX=\"370\" positionY=\"270\"/>          </arc>        </input>        <input ref=\"L10\">          <arc>            <point positionX=\"270\" positionY=\"420\"/>            <point positionX=\"370\" positionY=\"420\"/>          </arc>        </input>      </inputs>      <outputs>        <output ref=\"L11\">          <arc>            <point positionX=\"370\" positionY=\"270\"/>            <point positionX=\"420\" positionY=\"270\"/>          </arc>        </output>      </outputs>      <predicates>        <predicate input=\"L6\" output=\"L11\">true</predicate>        <predicate input=\"L8\" output=\"L11\">true</predicate>        <predicate input=\"L10\" output=\"L11\">true</predicate>      </predicates>    </transition>  </transitions>  <places>    <place id=\"L1\" name=\"L1\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"30\" positionY=\"270\"/>    <place id=\"M1\" name=\"M1\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"30\" positionY=\"440\"/>    <place id=\"L2\" name=\"L2\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"140\" positionY=\"80\"/>    <place id=\"L3\" name=\"L3\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"140\" positionY=\"230\"/>    <place id=\"L4\" name=\"L4\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"140\" positionY=\"380\"/>    <place id=\"M2\" name=\"M2\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"140\" positionY=\"440\"/>    <place id=\"L5\" name=\"L5\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"270\" positionY=\"60\"/>    <place id=\"L6\" name=\"L6\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"270\" positionY=\"120\"/>    <place id=\"L7\" name=\"L7\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"270\" positionY=\"210\"/>    <place id=\"L8\" name=\"L8\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"270\" positionY=\"270\"/>    <place id=\"L9\" name=\"L9\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"270\" positionY=\"360\"/>    <place id=\"L10\" name=\"L10\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"270\" positionY=\"420\"/>    <place id=\"L11\" name=\"L11\" priority=\"0\" capacity=\"-1\" merge=\"false\" positionX=\"420\" positionY=\"270\"/>  </places>  <tokens>    <generator type=\"periodic\" period=\"1\" id=\"Petrol\" name=\"Petrol\" priority=\"0\" host=\"L1\" entering=\"0\" leaving=\"-1\">      <char name=\"Default\" type=\"double\" history=\"1\">0</char>    </generator>    <generator type=\"periodic\" period=\"1\" id=\"Order\" name=\"Order\" priority=\"0\" host=\"M1\" entering=\"0\" leaving=\"-1\">      <char name=\"Paint_kg\" type=\"double\" history=\"1\">0</char>      <char name=\"Plastics_kg\" type=\"double\" history=\"1\">0</char>      <char name=\"Fuel_kg\" type=\"double\" history=\"1\">0</char>      <char name=\"Color\" type=\"string\" history=\"1\">green</char>    </generator>    <token id=\"PetrolForPaint\" name=\"PetrolForPaint\" priority=\"0\" host=\"L5\" entering=\"0\" leaving=\"-1\">      <char name=\"Default\" type=\"double\" history=\"1\">0</char>    </token>    <token id=\"PetrolForPlastics\" name=\"PetrolForPlastics\" priority=\"0\" host=\"L7\" entering=\"0\" leaving=\"-1\">      <char name=\"Default\" type=\"double\" history=\"1\">0</char>    </token>    <token id=\"PetrolForFuel\" name=\"PetrolForFuel\" priority=\"0\" host=\"L9\" entering=\"0\" leaving=\"-1\">      <char name=\"Default\" type=\"double\" history=\"1\">0</char>    </token>  </tokens>  <functions/></gn>";
	}

	public Simulation createSimulation(GnDocument document) {
		try {
			return new NeftochimSimulation(document, document.getModel());
		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static class NeftochimSimulation extends Simulation {

		private double paint;
		private double plastics;
		private double fuel;
		
		private int tonsPerTanker;
		private int tankerStep;
		
		private int percentsForPaint;
		private int percentsForPlastics;
		
		private boolean shouldStop = false;
		
		private static final Random random = new Random();
		
		public NeftochimSimulation(GnDocument document, GeneralizedNet gn) throws SimulationException {
			super(document, gn);
		}

		private void showErrorAndStop() {
			MessageBox box = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.OK | SWT.ICON_ERROR);
			box.setText("Грешка");
			box.setMessage("Въведена е невалидна стойност. Моля започнете отначало.");
			box.open();
			
			try {
				shouldStop = true;
				stop();
			} catch (SimulationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void start() {
			super.start();
			
			//TODO: 0 is acceptable in some cases, but be careful later!
			
			NumberInputDialog dialog = new NumberInputDialog(Display.getCurrent().getActiveShell());
			Double[] values = dialog.open(
					"Колко кг боя се получават средно от един тон нефт:",
					"Колко кг пластмаси се получават средно от един тон нефт:",
					"Колко кг горива се получават средно от един тон нефт:",
					"Колко тона нефт има средно в един танкер:",
					"На колко дни средно пристига танкер с нефт:"
			);

			for (int i = 0; i < values.length; ++i)
				if (values[i] == null || values[i].doubleValue() < 0) {
					showErrorAndStop();
					return;
				}
			
			paint = values[0].doubleValue();
			plastics = values[1].doubleValue();
			fuel = values[2].doubleValue();

			tonsPerTanker = values[3].intValue();
			tankerStep = values[4].intValue();
		}
		
		@Override
		protected void closeEventSource() throws SimulationException {
		}

		@Override
		protected GnEvents getNextEvents(int count) throws SimulationException {
			GnEvents result = new GnEvents(getGn());
			
			if (shouldStop) {
				shouldStop = false;
				return result;
			}
			
			GeneralizedNet gn = getGn();
			int time = gn.getCurrentTime();
			
			Token t;
			List<Characteristic> chars;
			GnEvent e;
			NumberInputDialog dialog = null;
			Double[] values;
			Characteristic ch;

			if (time == 0) {
				dialog = new NumberInputDialog(Display.getCurrent().getActiveShell());
				values = dialog.open(
						"Колко тона е началното количество нефт, предвиден за бои:",
						"Колко тона е началното количество нефт, предвиден за пластмаси:",
						"Колко тона е началното количество нефт, предвиден за горива:"
						);
				for (int i = 0; i < values.length; ++i) {
					if (values[i] == null || values[i].doubleValue() < 0) {
						values[i] = new Double(0);
					}
				}
				
				t = gn.getTokens().get("PetrolForPaint");
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				ch.setValue("" + values[0].intValue());
				t.getChars().add(ch);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L5"), chars);
				result.add(e);
				
				t = gn.getTokens().get("PetrolForPlastics");
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				ch.setValue("" + values[1].intValue());
				t.getChars().add(ch);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L7"), chars);
				result.add(e);
				
				t = gn.getTokens().get("PetrolForFuel");
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				ch.setValue("" + values[2].intValue());
				t.getChars().add(ch);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L9"), chars);
				result.add(e);
			}
			
			if (time % tankerStep == 0) {
				Token tanker = new Token("Tanker" + (time / tankerStep + 1), null);
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				ch.setValue("" + tonsPerTanker);
				chars.add(ch);
				e = new EnterEvent(gn, tanker, gn.getPlaces().get("L1"), chars);
				result.add(e);
			}
			
			if (time > 0 && (time - 1) % tankerStep == 0) {
				t = GnUtil.getSingleTokenAt(gn, "L1");
				e = new LeaveEvent(gn, t, null, new ArrayList<Characteristic>());
				result.add(e);
				
				// non-automatic demo:
//				dialog = new NumberInputDialog(Display.getCurrent().getActiveShell());
//				values = dialog.open(
//						"Колко % нефт да бъде заделен за бои (0-100):",
//						"Колко % нефт да бъде заделен за пластмаси (0-100):"
//						);
//				
//				if (values[0] == null || values[0].doubleValue() < 0 || values[0].intValue() > 100) {
//					values[0] = new Double(0);
//				}
//				percentsForPaint = values[0].intValue();
	//
//				if (values[1] == null || values[1].doubleValue() < 0 || values[1].intValue() > (100 - percentsForPaint)) {
//					values[1] = new Double(0);
//				}
//				percentsForPlastics = values[1].intValue();
				// automatic demo:
				percentsForPaint = random.nextInt(100);
				percentsForPlastics = random.nextInt(100 - percentsForPaint);
				
				t = new Token(GnUtil.getSingleTokenAt(gn, "L1").getId() + "_1" , null);
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				ch.setValue("" + (tonsPerTanker * percentsForPaint / 100));
				chars.add(ch);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L2"), chars);
				result.add(e);

				t = new Token(GnUtil.getSingleTokenAt(gn, "L1").getId() + "_2" , null);
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				ch.setValue("" + (tonsPerTanker * percentsForPlastics / 100));
				chars.add(ch);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L3"), chars);
				result.add(e);
				
				t = new Token(GnUtil.getSingleTokenAt(gn, "L1").getId() + "_3" , null);
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				ch.setValue("" + (tonsPerTanker - tonsPerTanker * percentsForPaint / 100 - tonsPerTanker * percentsForPlastics / 100));
				chars.add(ch);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L4"), chars);
				result.add(e);
			}
			
			if (time > 0 && (time - 2) % tankerStep == 0) {
				Token warehoused;
				int quantity;
				
				warehoused = GnUtil.getSingleTokenAt(gn, "L5");
				t = GnUtil.getSingleTokenAt(gn, "L2");
				e = new LeaveEvent(gn, t, null, new ArrayList<Characteristic>());
				quantity = Integer.parseInt(warehoused.getDefaultCharacteristic().getValue());
				quantity += Integer.parseInt(t.getDefaultCharacteristic().getValue());
				warehoused.getDefaultCharacteristic().setValue("" + quantity);
				result.add(e);

				warehoused = GnUtil.getSingleTokenAt(gn, "L7");
				t = GnUtil.getSingleTokenAt(gn, "L3");
				e = new LeaveEvent(gn, t, null, new ArrayList<Characteristic>());
				quantity = Integer.parseInt(warehoused.getDefaultCharacteristic().getValue());
				quantity += Integer.parseInt(t.getDefaultCharacteristic().getValue());
				warehoused.getDefaultCharacteristic().setValue("" + quantity);
				result.add(e);

				warehoused = GnUtil.getSingleTokenAt(gn, "L9");
				t = GnUtil.getSingleTokenAt(gn, "L4");
				e = new LeaveEvent(gn, t, null, new ArrayList<Characteristic>());
				quantity = Integer.parseInt(warehoused.getDefaultCharacteristic().getValue());
				quantity += Integer.parseInt(t.getDefaultCharacteristic().getValue());
				warehoused.getDefaultCharacteristic().setValue("" + quantity);
				result.add(e);
			}

			chars = new ArrayList<Characteristic>();
			

			// non-automatic demo:
//			dialog = new NumberInputDialog(Display.getCurrent().getActiveShell());
//			values = dialog.open(
//					"За колко кг е поръчката за бои:",
//					"За колко кг е поръчката за пластмаси:",
//					"За колко кг е поръчката за гориво:"
//					);
//			for (int i = 0; i < values.length; ++i) {
//				if (values[i] == null || values[i].doubleValue() < 0) {
//					values[i] = new Double(0);
//				}
//			}
			// automatic demo
			values = new Double[3];
			values[0] = new Double(random.nextInt(tonsPerTanker)); //?
			values[1] = new Double(random.nextInt(tonsPerTanker)); //?
			values[2] = new Double(random.nextInt(tonsPerTanker)); //?
			
			ch = new Characteristic("double", 1);
			ch.setId("Paint_kg");
			ch.setValue("" + values[0].intValue());
			chars.add(ch);
			
			ch = new Characteristic("double", 1);
			ch.setId("Plastics_kg");
			ch.setValue("" + values[1].intValue());
			chars.add(ch);

			ch = new Characteristic("double", 1);
			ch.setId("Fuel_kg");
			ch.setValue("" + values[2].intValue());
			chars.add(ch);

			ch = new Characteristic("string", 1);
			ch.setId("Color");
			ch.setValue("green");
			chars.add(ch);
			
			if (time >= 2) { //was w/o condition
				t = new Token("Order" + (time + 1), null);
				e = new EnterEvent(gn, t, gn.getPlaces().get("M1"), chars);
				result.add(e);
			}
			
			if (time > 2) { //was 0
				int orderQuantity;
				
				int l5char = (int)(Integer.parseInt(
						GnUtil.getSingleTokenAt(gn, "L5").getDefaultCharacteristic().getValue()) - 
						(double)Integer.parseInt(GnUtil.getSingleTokenAt(gn, "M1").getChars().get("Paint_kg").getValue())/paint);
				int l7char = (int)(Integer.parseInt(
						GnUtil.getSingleTokenAt(gn, "L7").getDefaultCharacteristic().getValue()) - 
						(double)Integer.parseInt(GnUtil.getSingleTokenAt(gn, "M1").getChars().get("Plastics_kg").getValue())/plastics);
				int l9char = (int)(Integer.parseInt(
						GnUtil.getSingleTokenAt(gn, "L9").getDefaultCharacteristic().getValue()) - 
						(double)Integer.parseInt(GnUtil.getSingleTokenAt(gn, "M1").getChars().get("Fuel_kg").getValue())/fuel);
				chars = new ArrayList<Characteristic>();
				e = new MoveEvent(gn, GnUtil.getSingleTokenAt(gn, "M1"), gn.getPlaces().get("M2"), chars);
				result.add(e);
				
				boolean orderCanBeSatisfied = (l5char >= 0 && l7char >= 0 && l9char >= 0);
				
				t = new Token("Paint" + time, null);
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				orderQuantity = Integer.parseInt(GnUtil.getSingleTokenAt(gn, "M1").getChars().get("Paint_kg").getValue());
				if (orderCanBeSatisfied)
					ch.setValue("" + orderQuantity);
				else
					ch.setValue("0");
				chars.add(ch);
				ch = new Characteristic("string", 1);
				ch.setId("Color"); ch.setValue("red");
				chars.add(ch);
//				int l5char = (int)(Integer.parseInt(
//						GnUtil.getSingleTokenAt(gn, "L5").getDefaultCharacteristic().getValue()) - (double)orderQuantity/paint);
				//if (l5char < 0) {
//					MessageBox box = new MessageBox(Display.getCurrent()
//							.getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
//					box.setText("Край");
//					box.setMessage("Наличното количество нефт не е достатъчно, за да се изпълни поръчката на бои.");
//					box.open();
//					
//					result = new GnEvents(gn);
//					//l5char = 0;// $$$
//					//stop();
//					return result;
					//chars.get(0).setValue("0");
					//orderCanBeSatisfied = false;
				//} else
				if (orderCanBeSatisfied)
					GnUtil.getSingleTokenAt(gn, "L5").getDefaultCharacteristic().setValue("" + l5char);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L6"), chars);
				result.add(e);
				
				t = new Token("Plastics" + time, null);
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				orderQuantity = Integer.parseInt(GnUtil.getSingleTokenAt(gn, "M1").getChars().get("Plastics_kg").getValue());
				if (orderCanBeSatisfied)
					ch.setValue("" + orderQuantity);
				else
					ch.setValue("0");
				chars.add(ch);
				ch = new Characteristic("string", 1);
				ch.setId("Color"); ch.setValue("blue");
				chars.add(ch);
//				int l7char = (int)(Integer.parseInt(
//						GnUtil.getSingleTokenAt(gn, "L7").getDefaultCharacteristic().getValue()) - (double)orderQuantity/plastics);
				//if (l7char < 0 || !orderCanBeSatisfied) {
//					MessageBox box = new MessageBox(Display.getCurrent()
//							.getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
//					box.setText("Край");
//					box.setMessage("Наличното количество нефт не е достатъчно, за да се изпълни поръчката на пластмаси.");
//					box.open();
	//
//					result = new GnEvents(gn);
//					return result;
					//chars.get(0).setValue("0");
					//orderCanBeSatisfied = false;
				//} else
				if (orderCanBeSatisfied)
					GnUtil.getSingleTokenAt(gn, "L7").getDefaultCharacteristic().setValue("" + l7char);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L8"), chars);
				result.add(e);

				t = new Token("Fuel" + time, null);
				chars = new ArrayList<Characteristic>();
				ch = new Characteristic("double", 1);
				orderQuantity = Integer.parseInt(GnUtil.getSingleTokenAt(gn, "M1").getChars().get("Fuel_kg").getValue());
				if (orderCanBeSatisfied)
					ch.setValue("" + orderQuantity);
				else
					ch.setValue("0");
				chars.add(ch);
				ch = new Characteristic("string", 1);
				ch.setId("Color"); ch.setValue("gray");
				chars.add(ch);
//				int l9char = (int)(Integer.parseInt(
//						GnUtil.getSingleTokenAt(gn, "L9").getDefaultCharacteristic().getValue()) - (double)orderQuantity/fuel);
				//if (l9char < 0 || !orderCanBeSatisfied) {
//					MessageBox box = new MessageBox(Display.getCurrent()
//							.getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
//					box.setText("Край");
//					box.setMessage("Наличното количество нефт не е достатъчно, за да се изпълни поръчката на горива.");
//					box.open();
	//
//					result = new GnEvents(gn);
//					return result;
					//chars.get(0).setValue("0");
					//orderCanBeSatisfied = false;
				//} else
				if (orderCanBeSatisfied)
					GnUtil.getSingleTokenAt(gn, "L9").getDefaultCharacteristic().setValue("" + l9char);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L10"), chars);
				result.add(e);
			}
			
			if (time > 3) { //was 1
				chars = new ArrayList<Characteristic>();
				
				e = new LeaveEvent(gn, GnUtil.getSingleTokenAt(gn, "M2"), null, chars);
				result.add(e);
				
				e = new LeaveEvent(gn, GnUtil.getSingleTokenAt(gn, "L6"), null, chars);
				result.add(e);
				e = new LeaveEvent(gn, GnUtil.getSingleTokenAt(gn, "L8"), null, chars);
				result.add(e);
				e = new LeaveEvent(gn, GnUtil.getSingleTokenAt(gn, "L10"), null, chars);
				result.add(e);
				
				t = new Token("Products" + (time - 1), null);
				ch = new Characteristic("double", 1);
				ch.setId("Paint_kg");
				ch.setValue(GnUtil.getSingleTokenAt(gn, "L6").getDefaultCharacteristic().getValue());
				chars.add(ch);
				ch = new Characteristic("double", 1);
				ch.setId("Plastics_kg");
				ch.setValue(GnUtil.getSingleTokenAt(gn, "L8").getDefaultCharacteristic().getValue());
				chars.add(ch);
				ch = new Characteristic("double", 1);
				ch.setId("Fuel_kg");
				ch.setValue(GnUtil.getSingleTokenAt(gn, "L10").getDefaultCharacteristic().getValue());
				chars.add(ch);
				e = new EnterEvent(gn, t, gn.getPlaces().get("L11"), chars);
				result.add(e);
			}
			
			if (time > 4) { //was 2
				chars = new ArrayList<Characteristic>();
				e = new LeaveEvent(gn, GnUtil.getSingleTokenAt(gn, "L11"), null, chars);
				result.add(e);
			}
			
			return result;
		}
	}

}