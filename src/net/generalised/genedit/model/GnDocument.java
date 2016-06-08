package net.generalised.genedit.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import net.generalised.genedit.baseapp.model.Document;
import net.generalised.genedit.baseapp.model.ParseException;
import net.generalised.genedit.model.dataaccess.GnParseException;
import net.generalised.genedit.model.dataaccess.xmlread.GnXmlReader;
import net.generalised.genedit.model.dataaccess.xmlwrite.GnXmlWriter;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;
import net.generalised.genedit.model.gn.Transition;
import net.generalised.genedit.model.gn.VisualParameters;
import net.generalised.genedit.simulation.model.Simulation;
import net.generalised.genedit.simulation.model.SimulationException;

/**
 * Represents a GN model document, which is saved in a file.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class GnDocument extends Document<GeneralizedNet> implements Observer { //TODO: remove this
	
	private Simulation simulation = null;
	
	public GnDocument(GeneralizedNet model, boolean readOnly) {
		super(model, readOnly);
	}
	
	public GnDocument(String fileName, boolean readOnly)
			throws IllegalArgumentException, IOException, ParseException {
		super(fileName, readOnly);
	}
	
	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	//TODO: Dependency injection? or simply remove this method???
	@Override
	public GeneralizedNet load(String fileName) throws IOException, ParseException {
		GeneralizedNet gn;
		try {
			gn = GnXmlReader.getInstance().xmlToGn(fileName);
		} catch (GnParseException e) {

			//TODO: temporary code
			//if the error is that "name" attribute is missing, modify the content and parse again
			if (e.getMessage().contains("Attribute 'name' must appear on element 'gn'.")) {
				gn = tempOpenGenneteXml(fileName);
				if (gn != null) {
					return gn;
				}
			}
			
			throw new ParseException(e);
		}
		return gn;
	}
	
	// delete this method when Gennete is fixed
	private GeneralizedNet tempOpenGenneteXml(String fileName) {
		GeneralizedNet gn = null;
		try {
			File inputFile = new File(fileName);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			File outputFile = File.createTempFile("GeneditImport", ".xml");
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

			Random random = new Random();
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.replace("xmlns=\"http://www.clbme.bas.bg/GN\"",
						"xmlns=\"http://www.clbme.bas.bg/GN\" name=\"_imported\"");
				line = line.replace("</transition>", "<predicates/></transition>");
				line = line.replace("capacity=\"inf\"", "capacity=\"-1\"");
				line = line.replace("</places>", "</places><tokens/>");
				line = line.replace("output ref=\"Z", "output ref=\"tEmp___" + random.nextInt(Integer.MAX_VALUE));
				writer.write(line);
				writer.write('\n');
			}
			
			writer.close();
			reader.close();
			
			gn = GnXmlReader.getInstance().xmlToGn(outputFile.getAbsolutePath());

			outputFile.delete();
			
			for (Transition t : gn.getTransitions()) {
				t.setVisualHeight(t.getVisualHeight() - 16);
				
				for (PlaceReference ref : t.getInputs()) {
					for (Point point : ref.getArc()) {
						point.setVisualPositionX(point.getVisualPositionX() - 16);
						point.setVisualPositionY(point.getVisualPositionY() - 16);
					}
					Collections.reverse(ref.getArc());
				}
				for (PlaceReference ref : t.getOutputs()) {
					for (Point point : ref.getArc()) {
						point.setVisualPositionX(point.getVisualPositionX() - 16);
						point.setVisualPositionY(point.getVisualPositionY() - 16);
					}
					Point last = ref.getArc().get(ref.getArc().size() - 1); 
					for (Place place : gn.getPlaces()) {
						if (Math.abs(place.getVisualPositionX() - 16 - last.getVisualPositionX()) <= 2
								&& Math.abs(place.getVisualPositionY() - last.getVisualPositionY()) <= 2) {
							ref.setPlace(place);
							place.setLeftTransition(t);
							last.setVisualPositionX(last.getVisualPositionX() + 16);
							break;
						}
					}
				}
			}
			List<Place> placesToRemove = new ArrayList<Place>();
			for (Place place : gn.getPlaces()) {
				if (place.getId().startsWith("tEmp___")) {
					placesToRemove.add(place);
				}
			}
			for (Place place : placesToRemove) {
				gn.getPlaces().remove(place);
			}
			
			VisualParameters vp = gn.getVisualParameters();
			vp.setPlaceRadius(16);
			vp.setTransitionTriangleSize(new Point(32, 16));
			vp.setGridStep(8);
			
			return gn;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GnParseException e) {
			e.printStackTrace();
		}
		return gn;
	}
	
	/**
	 * Saves current document to the file, specified in this object.
	 * 
	 * @throws IOException if the file cannot be created
	 */
	@Override
	public synchronized void save() throws IOException {
		try {
			GnXmlWriter.getInstance().gnToXml(getModel(), getFileName(), false, true);
		} catch (GnParseException e) {
			throw new RuntimeException(e);
		}
		
		setModified(false);

		notifyObservers();
	}

	public boolean isInSimulationMode() {
		return simulation != null && simulation.isRunning();
	}

	// during simulation history is needed too
//	public void execute(Command command) {
//		// TODO something about RefreshCommand?
//		if (isInSimulationMode()) {
//			if (! (command instanceof GnEvent || command instanceof GnEvents)) //ee, be6e po-krasivo
//				return;
//			commands.execute(command);
//		} else {
//			commands.execute(command);
//			this.modified = true;
//		}
//		setChanged();
//		notifyObservers(command);
//	}
	
	@Override
	public void close() {
		//check this 
		if (isInSimulationMode()) {
			try {
				simulation.stop();
			} catch (SimulationException e) {
				e.printStackTrace();
			}
		}
		//TODO: remove observers if Selection is observable
	}

	public void update(Observable o, Object arg) {
		notifyObservers();
	}
}
