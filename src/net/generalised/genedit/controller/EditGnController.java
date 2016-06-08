package net.generalised.genedit.controller;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.baseapp.SettingsManager;
import net.generalised.genedit.baseapp.controller.Controller;
import net.generalised.genedit.baseapp.model.Command;
import net.generalised.genedit.baseapp.model.CompositeCommand;
import net.generalised.genedit.baseapp.model.ListCommand;
import net.generalised.genedit.baseapp.model.PropertyChangeCommand;
import net.generalised.genedit.baseapp.model.ListCommand.ListChange;
import net.generalised.genedit.main.MainConfigProperties;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.Function;
import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GeneratorMode;
import net.generalised.genedit.model.gn.GnList;
import net.generalised.genedit.model.gn.GnObjectWithId;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceRefList;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;
import net.generalised.genedit.model.gn.RichTextName;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.TokenGenerator;
import net.generalised.genedit.model.gn.Transition;
import net.generalised.genedit.view.MainForm;
import net.generalised.genedit.view.Selection;
import net.generalised.genedit.view.MainMenu.ApplyG2PrimOperatorEvent;
import net.generalised.genedit.view.MainMenu.ApplyG6OperatorEvent;
import net.generalised.genedit.view.MainMenu.CompareGnsEvent;
import net.generalised.genedit.view.MainMenu.InsertInputPlaceEvent;
import net.generalised.genedit.view.MainMenu.InsertOutputPlaceEvent;
import net.generalised.genedit.view.MainMenu.InsertTransitionEvent;
import net.generalised.genedit.view.MainMenu.RedoEvent;
import net.generalised.genedit.view.MainMenu.UndoEvent;
import net.generalised.genedit.view.MainMenu.UnionTransitionsEvent;
import net.generalised.genedit.view.TreeView.DropObjectEvent;
import net.generalised.genedit.view.graphical.GraphicalView;
import net.generalised.genedit.view.graphical.GraphicalView.DragEvent;
import net.generalised.genedit.view.graphical.tools.GraphicTool;
import net.generalised.genedit.view.graphical.tools.GraphicToolFactory;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

/**
 * @author Dimitar Dimitrov
 *
 */
public class EditGnController {

	@Controller
	public void undo(UndoEvent event, MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			document.undo(1);
		}
	}
	
	@Controller
	public void redo(RedoEvent event, MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			document.redo(1);
		}
	}

	private static <T extends GnObjectWithId> String getNextId(GnList<T> list, String prefix) {
		//another algorithm, O(n): iterate over elements, find maximum index, return prefix + (index + 1)
		String id = prefix;
		for (int i = 1; i <= Integer.MAX_VALUE; i++)
		{
			id = prefix + Integer.toString(i);
			if (list.get(id) == null) {
				break;
			}
		}
		return id;
	}
	
	@Controller
	public void insertTransition(InsertTransitionEvent event, MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			GeneralizedNet gn = ViewUtil.getCurrentlyViewedGn(view);
			final String prefix = SettingsManager.getInstance().getProperty(MainConfigProperties.DEFAULT_TRANSITION_PREFIX);
			String id = getNextId(gn.getTransitions(), prefix);
			int gridStep = gn.getVisualParameters().getGridStep();
			Transition newTransition = new Transition(id);
			newTransition.setVisualPosition(10 * gridStep, 10 * gridStep); //TODO:this almost always will overlap another transition!
			newTransition.setVisualHeight(10 * gridStep);
			gn.getTransitions().undoableAdd(newTransition);
			
			view.getCurrentGnView().getSelection().select(newTransition);
		}
	}
	
	private static void insertPlace(MainForm view, boolean isInput) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			
			// get selected transition
			List<Object> selected = view.getCurrentGnView().getSelection().getObjects();
			Transition currentTransition = null;
			if (selected.size() > 0) {
				Object firstSelected = selected.get(0);
				if (firstSelected instanceof Transition) {
					currentTransition = (Transition) firstSelected;
				}
			}
	
			if (currentTransition != null) {
				// generating the ID of the new place
				GeneralizedNet gn = ViewUtil.getCurrentlyViewedGn(view);
				final String prefix = SettingsManager.getInstance().getProperty(MainConfigProperties.DEFAULT_PLACE_PREFIX);
				String id = getNextId(gn.getPlaces(), prefix);
				Place place = new Place(id);

				//TODO: single undoable command!
				int gridStep = gn.getVisualParameters().getGridStep();

				// finding the coordinates of the lowest input/output place
				// TODO: find arcs too, not only places
				int placeX = currentTransition.getVisualPositionX();
				int placeY = currentTransition.getVisualPositionY()
						- gn.getVisualParameters().getPlaceRadius();
				PlaceReference newRef = null;
				List<PlaceReference> refs = null;
				if (isInput) {
					refs = currentTransition.getInputs();
				} else {
					refs = currentTransition.getOutputs();
				}
				for (PlaceReference ref : refs) {
					List<Point> arc = ref.getArc(); 
					if (arc.size() > 0) {
						int y;
						if (isInput) {
							y = arc.get(0).getVisualPositionY();
						} else {
							y = arc.get(arc.size() - 1).getVisualPositionY();
						}
						if (y > placeY) {
							placeY = y; 
						}
					}
				}
				
				gn.getPlaces().undoableAdd(place);
				
				// setting coordinates of the new place
				int offsetX = 0;
				if (isInput) {
					offsetX = -1;
				} else {
					offsetX = 1;
				}
				placeX += offsetX * 5 * gridStep;
				place.undoableSet("visualPositionX", placeX);
				placeY += 3 * gn.getVisualParameters().getPlaceRadius();
				place.undoableSet("visualPositionY", placeY);

				// adding arc between the place and the transition
				// TODO: make this undoable
				if (isInput) {
					newRef = currentTransition.getInputs().undoableAdd(place);
				} else {
					newRef = currentTransition.getOutputs().undoableAdd(place);
				}
				if (isInput) {
					newRef.getArc().add(new Point(placeX, placeY));
					newRef.getArc().add(new Point(currentTransition.getVisualPositionX(), placeY));
				} else {
					newRef.getArc().add(new Point(currentTransition.getVisualPositionX(), placeY));
					newRef.getArc().add(new Point(placeX, placeY));
				}
				
				// resize the transition if it is too short
				int maxY = currentTransition.getVisualPositionY() 
					+ currentTransition.getVisualHeight() 
					- gn.getVisualParameters().getPlaceRadius();
				if (placeY > maxY) {
					currentTransition.undoableSet("visualHeight",
							currentTransition.getVisualHeight() + placeY - maxY);
				}
			} else {
				ViewUtil.showErrorMessageBox("Please select a transition.");
			}
		}
	}
	
	@Controller
	public void insertInputPlace(InsertInputPlaceEvent event, MainForm view) {
		insertPlace(view, true);
	}
	
	@Controller
	public void insertOutputPlace(InsertOutputPlaceEvent event, MainForm view) {
		insertPlace(view, false);
	}
	
	private static void insertTokenOrGenerator(MainForm view, boolean generator) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			GeneralizedNet gn = ViewUtil.getCurrentlyViewedGn(view);
			final String prefix = SettingsManager.getInstance().getProperty(MainConfigProperties.DEFAULT_TOKEN_PREFIX);
			String id = getNextId(gn.getTokens(), prefix);
			Place host = null;
			List<Object> selected = view.getCurrentGnView().getSelection().getObjects();
			if (selected.size() > 0) {
				Object firstSelected = selected.get(0);
				if (firstSelected instanceof Place)
					host = (Place) firstSelected;
				else if (firstSelected instanceof Token)
					host = ((Token) firstSelected).getHost();
			}
			Token token = null;
			if (! generator) {
				token = new Token(id, host);
			} else {
				token = new TokenGenerator(id, host, GeneratorMode.PERIODIC);
			}
			gn.getTokens().undoableAdd(token);
			
			view.getCurrentGnView().getSelection().select(token);
		}
	}
	
	public static void insertToken(MainForm view) {
		insertTokenOrGenerator(view, false);
	}

	public static void insertTokenGenerator(MainForm view) {
		insertTokenOrGenerator(view, true);
	}

	public static void insertCharacteristic(final MainForm view) {
		final GnDocument document = view.getDocument();
		//final GeneralizedNet currentGN = ViewUtil.getCurrentlyViewedGn(view);
		//TODO: make Characteristic GnObjectWithId and make this simpler!
		if (document != null) {
			document.execute(new Command() {

				private Token token = null;
				private Characteristic insertedChar;

				@Override
				public void execute() {
					// TODO if not selected token - return
					// da ama taka vinagi 6te se vika setModified
					// a i moje sled uspe6no izpalnenie insertedChar da e == null, a taka opredelqme, 4e e za parvi pat
					List<Object> selected = view.getCurrentGnView().getSelection().getObjects(); 
					if (token != null || (selected.size() >= 1 && selected.get(0) instanceof Token)) {
						if (token == null)
							token = (Token)selected.get(0);
						String id = "ch";
						for (int i = 1; i <= Integer.MAX_VALUE; i++)
						{
							boolean found = false;
							java.util.List<Characteristic> chars = token.getChars();
							id = "ch" + Integer.toString(i);
							for (Characteristic c : chars) {
								if (c.getName().equals(id)) {
									found = true;
									break;
								}
							}
							if (!found)
								break;
						}
						if (insertedChar == null) {
							insertedChar = new Characteristic("string", 1); //TODO: are these values good?
							insertedChar.setName(id);
							insertedChar.setValue("..."); //mai inak garmi serveryt!
						}
						token.getChars().add(insertedChar);
						
						view.getCurrentGnView().getSelection().select(insertedChar);
					} else {
						MessageBox box = new MessageBox(Display.getCurrent().getActiveShell());
						box.setMessage("First select a token in the tree!");
						box.open();
					}
				}

				@Override
				public void unExecute() {
					if (insertedChar != null) {
						token.getChars().remove(insertedChar);
					}
				}

			});
		}
	}
	
	public static void insertFunction(MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			GeneralizedNet gn = ViewUtil.getCurrentlyViewedGn(view);
			String id = "";
			List<String> functions = gn.getFunctionNames();
			//XXX
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				id = "f" + i;
				if (! functions.contains(id)) {
					break;
				}
			}
			Function function = gn.getFunctionFactory().createEmptyFunction(id);
			String newFunctions = gn.getFunctions() + "\n\n" + function.getDefinition();
			gn.undoableSet("functions", newFunctions);
			
			view.getCurrentGnView().getSelection().select(function);
		}
	}

	public static void drawArcs(MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			final GeneralizedNet currentGN = ViewUtil.getCurrentlyViewedGn(view);
			// FIXME better command
			document.execute(new Command() {
				private GeneralizedNet gn = currentGN;

				private List<PlaceReference> addedArcs = new ArrayList<PlaceReference>();
				
				@Override
				public void execute() {
					//TODO: if (addedArcs.size() == 0), as in other Commands
					int radius = gn.getVisualParameters().getPlaceRadius();//TODO: this is changeable
					for (Transition t : gn.getTransitions()) {
						for (PlaceReference ref : t.getInputs()) {
							List<Point> arc = ref.getArc();
							Place p = ref.getPlace();
							if (arc.size() == 0) {
								arc.add(new Point(p.getVisualPositionX(), p.getVisualPositionY()));
								if (p.getVisualPositionX() > t.getVisualPositionX()) {
									arc.add(new Point(p.getVisualPositionX() + 2 * radius, p.getVisualPositionY()));
									arc.add(new Point(p.getVisualPositionX() + 2 * radius, p.getVisualPositionY() + 2 * radius));
									arc.add(new Point(t.getVisualPositionX() - radius, p.getVisualPositionY() + 2 * radius));
									arc.add(new Point(t.getVisualPositionX() - radius, p.getVisualPositionY()));						
								}
								arc.add(new Point(t.getVisualPositionX(), p.getVisualPositionY()));
							}
							addedArcs.add(ref);
						}
						for (PlaceReference ref : t.getOutputs()) {
							List<Point> arc = ref.getArc();
							Place p = ref.getPlace();
							if (arc.size() == 0) {
								arc.add(new Point(t.getVisualPositionX(), p.getVisualPositionY()));
								if (p.getVisualPositionX() < t.getVisualPositionX()) {//is this needed? za vseki slu4ai
									arc.add(new Point(t.getVisualPositionX() + radius, p.getVisualPositionY()));						
									arc.add(new Point(t.getVisualPositionX() + radius, p.getVisualPositionY() + 2 * radius));
									arc.add(new Point(p.getVisualPositionX() - 2 * radius, p.getVisualPositionY() + 2 * radius));
									arc.add(new Point(p.getVisualPositionX() - 2 * radius, p.getVisualPositionY()));
								}
								arc.add(new Point(p.getVisualPositionX(), p.getVisualPositionY()));
							}
							addedArcs.add(ref);
						}
					}
				}

				@Override
				public void unExecute() {
					for (PlaceReference ref : addedArcs) {
						ref.getArc().clear();
					}
					addedArcs.clear();
				}

			});
		}
	}

	public static void deleteGnObject(final MainForm view) {
		GnDocument document = view.getDocument();
		if (document != null) {
			final GeneralizedNet currentGN = ViewUtil.getCurrentlyViewedGn(view);
			// FIXME better command
			document.execute(new Command() {
				private GeneralizedNet gn = currentGN;
				private List<Object> deletedObjects = new ArrayList<Object>(); //or List<GnObject> ? but List<something> is not a GnObject...
				//TODO: if we delete a place, corresponding arc should be deleted also; the place is an input for some transition...

				@Override
				public void execute() {
					Selection selection = view.getCurrentGnView().getSelection();
					List<Object> selected = selection.getObjects();
					for (Object object : selected) {
						//if o ...
						deletedObjects.add(object);
						gn.remove(object); 
					}
					selection.clear();
				}

				@Override
				public void unExecute() {
					//TODO:...
				}
			});
			
		}
	}
	
	@Controller
	public void dropInTree(final DropObjectEvent event) {
		//TODO: move or copy?
		Thread t = new Thread(new Runnable() { //otherwise - SWTException Widget is disposed
			@SuppressWarnings("unchecked")
			public void run() {
				Object target = event.getTarget();
				List<Object> droppedObjects = event.getDroppedObjects();
				Object first = droppedObjects.get(0);
				if (target instanceof PlaceRefList && first instanceof Place) {
					for (Object o : droppedObjects) {
						if (o instanceof Place) {
							((PlaceRefList) target).undoableAdd((Place) o);
							//TODO: what about arc?
						}
					}
				} else if (target instanceof Place && first instanceof Token) {
					for (Object o : droppedObjects) {
						if (o instanceof Token) {
							((Token) o).undoableSet("host", target);
						}
					}
				} else if (target instanceof Token && first instanceof Characteristic) {
					for (Object o : droppedObjects) {
						if (o instanceof Characteristic) {
							Characteristic ch = (Characteristic) o;
							if (ch.getParent(Token.class) != target) {
								//TODO: command
								ch.getParent(GnList.class).undoableRemove(ch);
								((Token)target).getChars().undoableAdd(ch);
							}
						}
					}
				} else if (target instanceof Place && first instanceof FunctionReference) {
					Place place = (Place)target;
					for (Object o : droppedObjects) {
						if (o instanceof FunctionReference) {
							place.undoableSet("charFunction", o);
							break; //cannot assign more than one function
						}
					}
				}
			}
			
		});
		t.start();

	}

	@Controller
	public void onCompareTransitions(CompareGnsEvent event) {
		// TODO result should be: equal/subset/superset/nothing
	}
	
	@Controller
	public void onUnionTransitions(UnionTransitionsEvent event) {
		MainForm view = event.getSource().getParent(MainForm.class);
		
		Selection selection = view.getCurrentGnView().getSelection();
		List<Object> selectedObjects = selection.getObjects();
		if (selectedObjects.size() < 2 || !selection.containsOnlyInstancesOf(Transition.class)) {
			ViewUtil.showErrorMessageBox(view.getUIComponent().getShell(), "Please select at least two transitions first.");
			return;
		}
		
		// TODO: Command
		// TODO: reuse logic for all such events
	}
	
	private int snapToGrid(int value, int gridStep) {
		return Math.round(value / gridStep) * gridStep; // do we need round? int/int
	}
	
	@Controller
	public void onMouseDrag(DragEvent event, GraphicalView view) {
		
		// TODO make an undoable Command and document.execute it; this command should rearrange arcs

		List<org.eclipse.swt.graphics.Point> dragPath = event.getDragPath();
		
		int tolerance = 15;

		// TODO better: 1. collect various statistics - maxX, firstX, etc.; 2. boolean methods isDown, etc.
		
		boolean isDown = true;
		org.eclipse.swt.graphics.Point previousPoint = null;
		int sumX = 0;
		int minXd = Integer.MAX_VALUE;
		int maxXd = Integer.MIN_VALUE;
		for (org.eclipse.swt.graphics.Point point : dragPath) {
			if (previousPoint != null) {
				if (point.y < previousPoint.y) {
					isDown = false;
					break;
				}
			}
			sumX += point.x;
			if (point.x > maxXd) {
				maxXd = point.x;
			}
			if (point.x < minXd) {
				minXd = point.x;
			}
			previousPoint = point;
		}
		isDown = isDown && (maxXd - minXd < tolerance); //Math.max(tolerance, lastY-firstY/10));
		
		int maxPoints = 4;
		
		boolean isStartingRight = true;
		previousPoint = null;
		for (int i = 0; i < dragPath.size() && i < maxPoints; i++) {
			if (previousPoint != null) {
				if (Math.abs(dragPath.get(i).y - previousPoint.y) > tolerance
						|| dragPath.get(i).x < previousPoint.x) {
					isStartingRight = false;
					break;
				}
			}
			previousPoint = dragPath.get(i);
		}
		
		final GeneralizedNet gn = ViewUtil.getCurrentlyViewedGn(view);

		boolean snapToGrid = view.getParent(MainForm.class).getCurrentGnView().isSnapToGrid();
		int gridStep = gn.getVisualParameters().getGridStep();
		
		if (isDown) { // && !isStartingRight) {
			final String prefix = SettingsManager.getInstance().getProperty(MainConfigProperties.DEFAULT_TRANSITION_PREFIX);
			String id = getNextId(gn.getTransitions(), prefix);
			Transition transition = new Transition(id);
			int x = sumX / dragPath.size();
			int y = dragPath.get(0).y;
			int lastY = dragPath.get(dragPath.size() - 1).y;
			if (snapToGrid) {
				x = snapToGrid(x, gridStep);
				y = snapToGrid(y, gridStep);
				lastY = snapToGrid(lastY, gridStep);
			}
			int height = lastY - y;
			transition.setVisualPositionX(x);
			transition.setVisualPositionY(y);
			transition.setVisualHeight(height);
			gn.getTransitions().undoableAdd(transition);
			view.getParent(MainForm.class).getCurrentGnView().getSelection().add(transition);
			// gn.getParent(GnDocument.class).updateViews(); // XXX
		} else if (isStartingRight) {
			Transition transition = null;
			Place place = null;
			boolean fromTransitionToPlace = true;
			org.eclipse.swt.graphics.Point lastPoint = dragPath.get(dragPath.size() - 1);
			for (Transition t : gn.getTransitions()) {
				// XXX
				GraphicTool tool = GraphicToolFactory.getTool(gn, t, null, 1.0, null);
				if (tool.isUnder(dragPath.get(0).x, dragPath.get(0).y)) {
					transition = t;
					fromTransitionToPlace = true;
					break;
				} else if (tool.isUnder(lastPoint.x, lastPoint.y)) {
					transition = t;
					fromTransitionToPlace = false;
					break;
				}
			}
			for (Place p : gn.getPlaces()) {
				// XXX
				GraphicTool tool = GraphicToolFactory.getTool(gn, p, null, 1.0, null);
				if (tool.isUnder(dragPath.get(0).x, dragPath.get(0).y)) {
					place = p;
					fromTransitionToPlace = false;
					break;
				} else if (tool.isUnder(lastPoint.x, lastPoint.y)) {
					place = p;
					fromTransitionToPlace = true;
					break;
				}
			}
			
			if (transition != null) {
				// if place == null - creating a new one
				if (place == null) {
					final String prefix = SettingsManager.getInstance().getProperty(MainConfigProperties.DEFAULT_PLACE_PREFIX);
					String id = getNextId(gn.getPlaces(), prefix);
					place = new Place(id);
					int x, y;
					if (fromTransitionToPlace) {
						x = lastPoint.x;
						y = lastPoint.y;
					} else {
						x = dragPath.get(0).x;
						y = dragPath.get(0).y;
					}
					if (snapToGrid) {
						x = snapToGrid(x, gridStep);
						y = snapToGrid(y, gridStep);
					}
					place.setVisualPositionX(x);
					place.setVisualPositionY(y);
					// TODO single command!
					gn.getPlaces().undoableAdd(place);
				}
				PlaceRefList inputsOrOutputs;
				PlaceReference ref;
				List<Point> arc = new ArrayList<Point>();
				if (fromTransitionToPlace) {
					ref = new PlaceReference(place, false);
					int y0 = dragPath.get(0).y;
					if (snapToGrid) {
						y0 = snapToGrid(y0, gridStep);
					}
					arc.add(new Point(transition.getVisualPositionX(), y0));
					arc.add(new Point(place.getVisualPositionX(), place.getVisualPositionY()));
					inputsOrOutputs = transition.getOutputs();
				} else {
					ref = new PlaceReference(place, true);
					arc.add(new Point(place.getVisualPositionX(), place.getVisualPositionY()));
					int lastY = lastPoint.y;
					if (snapToGrid) {
						lastY = snapToGrid(lastY, gridStep);
					}
					arc.add(new Point(transition.getVisualPositionX(), lastY));
					inputsOrOutputs = transition.getInputs();
				}
				
				// TODO better arc algorithm
				int firstX = arc.get(0).getVisualPositionX();
				int firstY = arc.get(0).getVisualPositionY();
				int lastX = arc.get(arc.size() - 1).getVisualPositionX();
				int lastY = arc.get(arc.size() - 1).getVisualPositionY();
				if (firstX < lastX) {
					if (arc.get(0).getVisualPositionY() != arc.get(arc.size() - 1).getVisualPositionY()) {
						// TODO try to detect where it is
						int middleX = (firstX + lastX) / 2;
						if (snapToGrid) {
							middleX = snapToGrid(middleX, gridStep);
						}
						arc.add(1, new Point(middleX, firstY));
						arc.add(2, new Point(middleX, lastY));
					}
				} else {
					int maxX = firstX, minX = firstX, maxY = firstY, minY = firstY;
					for (org.eclipse.swt.graphics.Point point : dragPath) {
						if (maxX < point.x) {
							maxX = point.x;
						}
						if (minX > point.x) {
							minX = point.x;
						}
						if (maxY < point.y) {
							maxY = point.y;
						}
						if (minY > point.y) {
							minY = point.y;
						}
					}
					if (snapToGrid) {
						maxX = snapToGrid(maxX, gridStep);
						minX = snapToGrid(minX, gridStep);
						maxY = snapToGrid(maxY, gridStep);
						minY = snapToGrid(minY, gridStep);
					}
					int extremY = maxY;
					if (maxY - firstY + maxY - lastY < firstY - minY + lastY - minY) {
						extremY = minY;
					}
					arc.add(1, new Point(maxX, firstY));
					arc.add(2, new Point(maxX, extremY));
					arc.add(3, new Point(minX, extremY));
					arc.add(4, new Point(minX, lastY));
				}
				ref.setArc(arc);
				inputsOrOutputs.undoableAdd(ref);
			}
		}
	}
	
	@Controller
	public void onApplyG2PrimOperator(ApplyG2PrimOperatorEvent event) {
		MainForm view = event.getSource().getParent(MainForm.class);
		GnDocument document = view.getDocument();
		if (document != null) {
			final GeneralizedNet gn = ViewUtil.getCurrentlyViewedGn(view);
			
			final List<Place> inputPlaces = new ArrayList<Place>();
			final List<Place> outputPlaces = new ArrayList<Place>();
			final List<Place> loopPlaces = new ArrayList<Place>();
			final List<Place> internalPlaces = new ArrayList<Place>();
			for (Place place : gn.getPlaces()) {
				if (place.getLeftTransition() == null) {
					inputPlaces.add(place);
				} else if (place.getRightTransition() == null) {
					outputPlaces.add(place);
				} else if (place.getLeftTransition().getId().equals(
						place.getRightTransition().getId())) {
					loopPlaces.add(place);
				} else {
					internalPlaces.add(place);
				}
			}
			
			final int GRID_STEP = gn.getVisualParameters().getGridStep();
			final int STEP = 5 * GRID_STEP;;
			
			final Transition z1 = new Transition("Z_1");
			z1.setVisualPosition(2 * STEP, STEP);
			z1.setVisualHeight((Math.max(inputPlaces.size(), outputPlaces.size())
					+ loopPlaces.size() + 1) * STEP);
			z1.setPriority(0);
			// TODO predicates
			
			final Transition z2 = new Transition("Z_2");
			z2.setVisualPosition(6 * STEP, 2 * STEP);
			z2.setVisualHeight(2 * STEP);
			z2.setPriority(1);
			final Place lStar = new Place("l-star"); // TODO check if already exists; better name?
			lStar.setName(new RichTextName("l*"));
			lStar.setVisualPosition(7 * STEP, 3 * STEP);
			lStar.setPriority(Integer.MAX_VALUE);
			lStar.setCapacity(IntegerInf.POSITIVE_INFINITY);
			{
				PlaceReference inputRef = new PlaceReference();
				inputRef.setPlace(lStar);
				inputRef.setInputFromThisPlace(true);
				List<Point> arc = new ArrayList<Point>();
				arc.add(new Point(lStar.getVisualPositionX(), lStar.getVisualPositionY()));
				arc.add(new Point(lStar.getVisualPositionX() + 2 * GRID_STEP, lStar.getVisualPositionY())); 
				arc.add(new Point(lStar.getVisualPositionX() + 2 * GRID_STEP, z2.getVisualPositionY() + z2.getVisualHeight() + STEP / 2));
				arc.add(new Point(z2.getVisualPositionX() - GRID_STEP, z2.getVisualPositionY() + z2.getVisualHeight() + STEP / 2));
				arc.add(new Point(z2.getVisualPositionX() - GRID_STEP, lStar.getVisualPositionY()));
				arc.add(new Point(z2.getVisualPositionX(), lStar.getVisualPositionY()));
				inputRef.setArc(arc);
				z2.getInputs().add(inputRef);
	
				PlaceReference outputRef = new PlaceReference();
				outputRef.setPlace(lStar);
				outputRef.setInputFromThisPlace(false);
				arc = new ArrayList<Point>();
				arc.add(new Point(z2.getVisualPositionX(), lStar.getVisualPositionY()));
				arc.add(new Point(lStar.getVisualPositionX(), lStar.getVisualPositionY()));
				outputRef.setArc(arc);
				z2.getOutputs().add(outputRef);
			}
			z2.getPredicates().setAt(lStar, lStar, new FunctionReference("true")); // XXX
			
			Token gamma = new Token("gamma", lStar);
			gamma.setEnteringTime(gn.getTimeStart());
			// TODO complex characteristic
			// TODO char function of lStar
			// TODO set history of gamma - infinity?
			
			Command command = new CompositeCommand("Apply G2'") {
				
				@Override
				protected List<Command> fillSubCommands() {
					List<Command> result = new ArrayList<Command>();

					// remove internal places only
					for (Place place : internalPlaces) {
						result.add(ListCommand.create(gn.getPlaces(), ListChange.REMOVE, place, -1));
					}
					
					// remove all transitions
					int transitionsCount = gn.getTransitions().size();
					for (int i = 0; i < transitionsCount; i++) {
						result.add(ListCommand.create(gn.getTransitions(), ListChange.REMOVE, null, 0));
					}

					// add Z1 and Z2 transitions, as well as l* and the token
					result.add(ListCommand.create(gn.getTransitions(), ListChange.ADD, z1, 0));
					result.add(ListCommand.create(gn.getTransitions(), ListChange.ADD, z2, 1));
					result.add(ListCommand.create(gn.getPlaces(), ListChange.ADD, lStar, -1));
					
					// rearrange places - set new coordinates; add to input/output lists
					int yLeft = 0;
					for (Place place : inputPlaces) {
						int x = STEP;
						int y = yLeft * STEP + 2 * STEP;
						result.add(new PropertyChangeCommand(place, "visualPositionX", x));
						result.add(new PropertyChangeCommand(place, "visualPositionY", y));
						++yLeft;
						
						PlaceReference ref = new PlaceReference();
						ref.setPlace(place);
						ref.setInputFromThisPlace(true);
						List<Point> arc = new ArrayList<Point>();
						arc.add(new Point(x, y));
						arc.add(new Point(z1.getVisualPositionX(), y));
						ref.setArc(arc);
						
						result.add(ListCommand.create(z1.getInputs(), ListChange.ADD, ref, -1));
					}
					int yRight = 0;
					for (Place place : outputPlaces) {
						int x = 3 * STEP;
						int y = yRight * STEP + 2 * STEP;
						result.add(new PropertyChangeCommand(place, "visualPositionX", x));
						result.add(new PropertyChangeCommand(place, "visualPositionY", y));
						++yRight;

						PlaceReference ref = new PlaceReference();
						ref.setPlace(place);
						ref.setInputFromThisPlace(false);
						List<Point> arc = new ArrayList<Point>();
						arc.add(new Point(z1.getVisualPositionX(), y));
						arc.add(new Point(x, y));
						ref.setArc(arc);

						result.add(ListCommand.create(z1.getOutputs(), ListChange.ADD, ref, -1));
					}
					int yLoop = Math.max(yLeft, yRight);
					int yIndex = loopPlaces.size();
					for (Place place : loopPlaces) {
						int x = 3 * STEP;
						int y = yLoop * STEP + 2 * STEP;
						result.add(new PropertyChangeCommand(place, "visualPositionX", x));
						result.add(new PropertyChangeCommand(place, "visualPositionY", y));
						
						PlaceReference inputRef = new PlaceReference();
						inputRef.setPlace(place);
						inputRef.setInputFromThisPlace(true);
						List<Point> arc = new ArrayList<Point>();
						arc.add(new Point(x, y));
						arc.add(new Point(x + (yIndex + 1) * GRID_STEP, y));
						arc.add(new Point(x + (yIndex + 1) * GRID_STEP, z1.getVisualPositionY() + z1.getVisualHeight() + yIndex * GRID_STEP));
						arc.add(new Point(z1.getVisualPositionX() - yIndex * GRID_STEP, z1.getVisualPositionY() + z1.getVisualHeight() + yIndex * GRID_STEP));
						arc.add(new Point(z1.getVisualPositionX() - yIndex * GRID_STEP, y));
						arc.add(new Point(z1.getVisualPositionX(), y));
						inputRef.setArc(arc);
						result.add(ListCommand.create(z1.getInputs(), ListChange.ADD, inputRef, -1));

						PlaceReference outputRef = new PlaceReference();
						outputRef.setPlace(place);
						outputRef.setInputFromThisPlace(false);
						arc = new ArrayList<Point>();
						arc.add(new Point(z1.getVisualPositionX(), y));
						arc.add(new Point(x, y));
						outputRef.setArc(arc);
						result.add(ListCommand.create(z1.getOutputs(), ListChange.ADD, outputRef, -1));
						
						++yLoop;
						--yIndex;
					}
					
					// TODO set characteristic functions of outputs
					
					return result;
				}
			};
			
			gn.execute(command);
		}
	}

	@Controller
	public void onApplyG6Operator(ApplyG6OperatorEvent event) {
		MainForm view = event.getSource().getParent(MainForm.class);
		GnDocument document = view.getDocument();
		if (document != null) {
			final GeneralizedNet gn = ViewUtil.getCurrentlyViewedGn(view);
			// TODO undoable

		}
	}

}
