package tempdemo;

import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnList;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.simulation.model.EnterEvent;
import net.generalised.genedit.simulation.model.GnEvent;
import net.generalised.genedit.simulation.model.GnEvents;
import net.generalised.genedit.simulation.model.LeaveEvent;
import net.generalised.genedit.simulation.model.MoveEvent;
import net.generalised.genedit.simulation.model.Simulation;
import net.generalised.genedit.simulation.model.SimulationException;

public class KahnSimulation extends Simulation {

	private String piChar;
	private String gammaChar;
	private String gammaColor;
	
	public KahnSimulation(GnDocument document, GeneralizedNet gn) throws SimulationException {
		super(document, gn);
		
		Token pi = gn.getTokens().get("pi");
		Characteristic processes = pi.getDefaultCharacteristic();
		if (processes != null) {
			this.piChar = processes.getValue();
		} else this.piChar = "";

		Token gamma = gn.getTokens().get("gamma");
		Characteristic internalChannels = gamma.getDefaultCharacteristic();
		if (internalChannels != null) {
			this.gammaChar = internalChannels.getValue();
		} else this.gammaChar = "";
		Characteristic color = gamma.getChars().get("Color");
		this.gammaColor = color.getValue();
	}

	@Override
	protected void closeEventSource() throws SimulationException {
	}

	@Override
	protected GnEvents getNextEvents(int count) throws SimulationException {
		GnEvents result = new GnEvents(getGn());
		GeneralizedNet gn = getGn();
		Characteristic ch;
		//List<Characteristic> chars;
		GnEvent e;

		int time = gn.getCurrentTime();
		
		if (time == 0) {
			
			Token pi = gn.getTokens().get("pi");
			
			//chars = new ArrayList<Characteristic>();
			ch = new Characteristic("vector", 100); // TODO history from model
			ch.setName("Default");
			ch.setValue(piChar);
			pi.getChars().add(ch);
			
			e = new EnterEvent(gn, pi, gn.getPlaces().get("L1"), pi.getChars());
			result.add(e);
			
			
			Token gamma = gn.getTokens().get("gamma");
			
			//chars = new ArrayList<Characteristic>();
			ch = new Characteristic("vector", 100); // TODO history from model
			ch.setName("Default");
			ch.setValue(gammaChar);
			gamma.getChars().add(ch);
			ch = new Characteristic("string", 1);
			ch.setName("Color");
			ch.setValue(gammaColor);
			gamma.getChars().add(ch);
			
			e = new EnterEvent(gn, gamma, gn.getPlaces().get("L13"), gamma.getChars());
			result.add(e);
			
		} else if (time == 1) {
			Token pi = gn.getTokens().get("pi");
			e = new LeaveEvent(gn, pi, gn.getPlaces().get("L1"), pi.getChars());
			GnList<Characteristic> chars = pi.getChars();
			result.add(e);

			pi = new Token("pi_1", null);
			e = new EnterEvent(gn, pi, gn.getPlaces().get("L2"), chars);
			result.add(e);
			
			Token iota = new Token("iota_1", null);
			//chars = new ArrayList<Characteristic>();
			ch = new Characteristic("vector", 100);
			ch.setName("Default");
			ch.setValue("[i a]");
			iota.getChars().add(ch);
			ch = new Characteristic("string", 1);
			ch.setName("Color");
			ch.setValue("blue");
			iota.getChars().add(ch);
			gn.getTokens().add(iota);
			e = new EnterEvent(gn, iota, gn.getPlaces().get("L6"), iota.getChars());
			result.add(e);
		} else if (time == 2) {
			Token pi = gn.getTokens().get("pi_1");
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L9"), pi.getChars());
			result.add(e);
			Token iota = gn.getTokens().get("iota_1");
			e = new MoveEvent(gn, iota, gn.getPlaces().get("L7"), iota.getChars());
			result.add(e);
		} else if (time == 3) {
			Token iota = gn.getTokens().get("iota_1");
			e = new LeaveEvent(gn, iota, gn.getPlaces().get("L7"), iota.getChars());
			result.add(e);
			
			Token pi_2 = new Token("pi_1_1", null);
			ch = new Characteristic("vector", 100);
			ch.setName("Default");
			ch.setValue("[[[p1 p2 p3] p1 [i] [c] [\"i?a\" \"i?b\" \"c!a\"] [[p1 \"i?a\" p2] [p2 \"c!a\" p3]]] p2]");
			pi_2.getChars().add(ch);
			gn.getTokens().add(pi_2);
			e = new EnterEvent(gn, pi_2, gn.getPlaces().get("L8"), pi_2.getChars());
			result.add(e);

			Token pi = gn.getTokens().get("pi_1");
			ch = new Characteristic("vector", 100);
			ch.setName("Default");
			ch.setValue("[[[q1 q2] q1 [c] [] ...");
			pi.getChars().add(ch);
			e = new EnterEvent(gn, pi, gn.getPlaces().get("L9"), pi.getChars());
			result.add(e);
		} else if (time == 4) {
			Token pi = gn.getTokens().get("pi_1_1");
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L14"), pi.getChars());
			result.add(e);
		} else if (time == 5) {
			Token pi = gn.getTokens().get("pi_1_1");
			pi.setId("pi_2");
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L3"), pi.getChars());
			result.add(e);
		} else if (time == 6) {
			Token pi = gn.getTokens().get("pi_2");
			// TODO modify char
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L11"), pi.getChars());
			result.add(e);
			// TODO update char in gamma
		} else if (time == 7) {
			Token pi = gn.getTokens().get("pi_2");
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L14"), pi.getChars());
			result.add(e);
			
			Token delta = new Token("delta", null);
			gn.getTokens().add(delta);
			ch = new Characteristic("vector", 100);
			ch.setName("Default");
			ch.setValue("[c b]");
			delta.getChars().add(ch);
			ch = new Characteristic("string", 100);
			ch.setName("Color");
			ch.setValue("red");
			delta.getChars().add(ch);
			e = new EnterEvent(gn, delta, gn.getPlaces().get("L10"), delta.getChars());
			result.add(e);
		} else if (time == 8) {
			Token pi = gn.getTokens().get("pi_1");
			// TODO update chars
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L8"), pi.getChars());
			result.add(e);
			
			Token delta = gn.getTokens().get("delta");
			e = new LeaveEvent(gn, delta, gn.getPlaces().get("L10"), delta.getChars());
			result.add(e);
			
			Token pi2 = gn.getTokens().get("pi_2");
			pi2.setId("pi_4");
			e = new MoveEvent(gn, pi2, gn.getPlaces().get("L5"), pi2.getChars());
			result.add(e);
		} else if (time == 9) {
			Token pi = gn.getTokens().get("pi_1");
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L14"), pi.getChars());
			result.add(e);
			
			Token pi4 = gn.getTokens().get("pi_4");
			e = new LeaveEvent(gn, pi4, gn.getPlaces().get("L5"), pi4.getChars());
			result.add(e);
		} else if (time == 10) {
			Token pi = gn.getTokens().get("pi_1");
			pi.setId("pi_4_1");
			e = new MoveEvent(gn, pi, gn.getPlaces().get("L5"), pi.getChars());
			result.add(e);
		} else if (time == 11) {
			Token pi4 = gn.getTokens().get("pi_4_1");
			e = new LeaveEvent(gn, pi4, gn.getPlaces().get("L5"), pi4.getChars());
			result.add(e);
		}
		
		return result;
	}

}
