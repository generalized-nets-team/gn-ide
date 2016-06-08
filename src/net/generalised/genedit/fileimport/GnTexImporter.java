package net.generalised.genedit.fileimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;
import net.generalised.genedit.model.gn.Transition;
import net.generalised.genedit.model.gn.VisualParameters;

public class GnTexImporter implements GnImporter {

	private List<Transition> transitions;
	private List<Place> places;
	private int height;
	private boolean visualParametersSet = false;
	
	public void importFileToGn(String fileName, GeneralizedNet gn) throws IOException, IllegalArgumentException {
		File file = new File(fileName);
		if (file.exists() && file.length() > 0) {
			BufferedReader input =  new BufferedReader(new FileReader(file));
			try {
				//TODO: if graphic... or formal?...
				
				//if (gn == null)
					//gn = new GeneralizedNet("Untitled");

				String line;
				
				do {
					line = input.readLine();
				} while (line != null && !line.startsWith("\\begin{picture}"));
				
				int beginIndex = line.lastIndexOf(',') + 1;
				int endIndex = line.length() - 1;
				height = Integer.parseInt(line.substring(beginIndex, endIndex));
				//TODO: set GN width & height?
				
				line = input.readLine();
				
				if (line != null && line.startsWith("%Transitions")) {
					while (readTransition(input, gn) != null) ;
				}
				transitions = gn.getTransitions();
				
				//line ne e promeneno, ostava si %tr...
				//if (line != null && line.startsWith("%Places")) {
				while (readPlace(input, gn) != null);
				//}
				places = gn.getPlaces();
				
				while (readArc(input, gn) != null);
			//TODO: catch NullPointerException...	
			} finally {
				input.close();
			}
		} else throw new FileNotFoundException(fileName);
	}

	//TODO: these methods are for graphic structure TeX only!
	private String readId(BufferedReader input) throws IOException {
		int beginIndex, endIndex;
		
		String line = input.readLine();
		if (line.startsWith("%"))
			return null;
		beginIndex = line.indexOf('$') + 1;
		endIndex = line.lastIndexOf('$');
		String id = line.substring(beginIndex, endIndex);
		//TODO: error prone:
		if (id.indexOf("_{") > 0) {
			id = id.replace("_{", "");
			id = id.replace("}", "");
		}
		
		return id;
	}
	
	private Transition readTransition(BufferedReader input, GeneralizedNet gn) throws IOException {
		Transition transition = null;
		
		int beginIndex, endIndex;
		
		String id = readId(input);
		if (id == null)
			return null;
		transition = gn.getTransitions().get(id);
		if (transition == null) {
			transition = new Transition(id);
			gn.getTransitions().add(transition);
		}
		
		String line = input.readLine();
		
		beginIndex = 5;
		endIndex = line.indexOf(',', beginIndex + 1);
		int x = Integer.parseInt(line.substring(beginIndex, endIndex));
		beginIndex = endIndex + 1;
		endIndex = line.indexOf(')', beginIndex + 1);
		int y = this.height - Integer.parseInt(line.substring(beginIndex, endIndex));
		beginIndex = line.lastIndexOf('{') + 1;
		endIndex = line.indexOf('}', beginIndex + 1);
		int height = Integer.parseInt(line.substring(beginIndex, endIndex));
		transition.setVisualPosition(x, y);
		transition.setVisualHeight(height);
		
		line = input.readLine();
		
		return transition;
	}
	
	private Place readPlace(BufferedReader input, GeneralizedNet gn) throws IOException {
		Place place = null;
		
		int beginIndex, endIndex;
		
		String id = readId(input);
		if (id == null)
			return null;
		place = gn.getPlaces().get(id);
		if (place == null) {
			place = new Place(id);
			gn.getPlaces().add(place);
		}

		String line = input.readLine();
		
		beginIndex = 5;
		endIndex = line.indexOf(',', beginIndex + 1);
		int x = Integer.parseInt(line.substring(beginIndex, endIndex));
		beginIndex = endIndex + 1;
		endIndex = line.indexOf(')', beginIndex + 1);
		int y = this.height - Integer.parseInt(line.substring(beginIndex, endIndex));
		place.setVisualPosition(x, y);

		if (!visualParametersSet) {
			beginIndex = line.lastIndexOf('{') + 1;
			endIndex = line.indexOf('}', beginIndex + 1);
			int diameter = Integer.parseInt(line.substring(beginIndex, endIndex));
			int radius = diameter / 2;
			VisualParameters visualParameters = gn.getVisualParameters();
			visualParameters.setPlaceRadius(radius);
			//TODO: tuk li mu e mqstoto: i otkade sme sigurni, 4e e tolkova:
			visualParameters.setGridStep(radius / 2);
			visualParameters.setTransitionTriangleSize(new Point(diameter, radius));
			visualParametersSet = true;
		}
		
		return place;
	}
	
	private Transition findTransition(int x, int y) {
		for (Transition t : transitions) {
			if (t.getVisualPositionX() == x && y >= t.getVisualPositionY() && 
					y <= t.getVisualPositionY() + t.getVisualHeight()) {
				return t;
			}
		}
		return null;
	}
	
	private Place findPlace(int x, int y, boolean isStartPoint, int radius) {
		if (!isStartPoint)
			radius = -radius;
		for (Place p : places) {
			if (p.getVisualPositionX() + radius == x &&
					p.getVisualPositionY() == y)
				return p;
		}
		return null;
	}
	
	private List<Point> readArc(BufferedReader input, GeneralizedNet gn) throws IOException {
		List<Point> arc = new ArrayList<Point>();
		//TODO: hmm, tuk e slojnoto - moje da sa na4upeni...
		//a i trqbva da se razbira koq kam kakvo e
		//kato za na4alo samo nena4upenite strelki:
		
		int placeRadius = gn.getVisualParameters().getPlaceRadius();
		
		int beginIndex, endIndex;
		
		String line = input.readLine();
		if (line.startsWith("\\end"))
			return null;
		
		Transition transition = null;
		Place place = null;
		boolean isInput = false;
		
		beginIndex = 5;
		endIndex = line.indexOf(',', beginIndex + 1);
		int x = Integer.parseInt(line.substring(beginIndex, endIndex));
		beginIndex = endIndex + 1;
		endIndex = line.indexOf(')', beginIndex + 1);
		int y = this.height - Integer.parseInt(line.substring(beginIndex, endIndex));

		Point point;
		
		if (line.indexOf("\\line") < 0) {
			transition = findTransition(x, y);
			if (transition != null) {
				point = new Point(x, y);
				isInput = false;
			} else {
				place = findPlace(x, y, true, placeRadius);
				isInput = true;
				point = new Point(x - placeRadius, y);
			}
			arc.add(point);
		} else {
			point = new Point(x,y);
			arc.add(point);
			//TODO: kogato e polyline, parvo e predposlednata line, posle kakto si trqbva (vektorat nakraq)
			
			while (line.indexOf("\\line") >= 0) {
				line = input.readLine();
	
				beginIndex = 5;
				endIndex = line.indexOf(',', beginIndex + 1);
				x = Integer.parseInt(line.substring(beginIndex, endIndex));
				beginIndex = endIndex + 1;
				endIndex = line.indexOf(')', beginIndex + 1);
				y = this.height - Integer.parseInt(line.substring(beginIndex, endIndex));
	
				point = new Point(x, y);
				arc.add(point);
			}
			
			int beforeLastIndex = arc.size() - 2; 
			Point p = arc.get(beforeLastIndex);
			arc.remove(beforeLastIndex);
			arc.add(0, p);
			x = p.getVisualPositionX();
			y = p.getVisualPositionY();
			transition = findTransition(x, y);
			if (transition != null) {
				isInput = false;
			} else {
				place = findPlace(x, y, true, placeRadius);
				isInput = true;
				p.setVisualPositionX(x - placeRadius);
			}
			
			Point lastPoint = arc.get(arc.size() - 1);
			x = lastPoint.getVisualPositionX();
			y = lastPoint.getVisualPositionY();
		}
		
		//TODO: priemame, 4e vektorat e (1,0)
		beginIndex = line.lastIndexOf('{') + 1;
		endIndex = line.indexOf('}', beginIndex + 1);
		int length = Integer.parseInt(line.substring(beginIndex, endIndex));
		x += length;
		
		if (isInput) {
			transition = findTransition(x, y);
			point = new Point(x, y);
		} else {
			place = findPlace(x, y, false, placeRadius);
			point = new Point(x + placeRadius, y);
		}
		arc.add(point);
		
		if (transition != null && place != null) { //TODO: else...
			PlaceReference ref;//TODO: moje ve4e da q ima, prosto arc da nqma!
			if (isInput) {
				ref = transition.getInputs().undoableAdd(place);
				ref.setArc(arc);
			} else {
				ref = transition.getOutputs().undoableAdd(place);
				ref.setArc(arc);
			}
		}
		
		return arc;
	}
}
