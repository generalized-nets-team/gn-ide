package net.generalised.genedit.view;

import static net.generalised.genedit.view.Constants.RESOURCES_PATH;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.baseapp.controller.Event;
import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.model.Command;
import net.generalised.genedit.baseapp.model.Document;
import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnUtil;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceRefList;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.TokenGenerator;
import net.generalised.genedit.model.gn.Transition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.cloudgarden.resource.SWTResourceManager;

public class TreeView extends BaseView {

	private Tree tree;
	private TreeItem treeItemRoot;

	private final GeneralizedNet gn;
	
	public static class DropObjectEvent extends Event {
		
		private final List<Object> droppedObjects;
		private final Object target;
		
		public DropObjectEvent(final List<Object> droppedObjects, final Object target) {
			this.droppedObjects = droppedObjects;
			this.target = target;
		}

		public List<Object> getDroppedObjects() {
			return droppedObjects;
		}

		public Object getTarget() {
			return target;
		}
	}
	
	public static class TreeDoubleClickEvent extends Event {
	}

	public TreeView(BaseView parent, GeneralizedNet gn) {
		super(parent, gn.getParent(GnDocument.class));
		this.gn = gn;
		addAsObserverTo(getParent(GnView.class).getSelection());
	}

	@Override
	protected Composite createUIComponent(Widget parent) {
		Composite composite = new Composite((Composite) parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		{
			buildTree(composite);
		}
		composite.layout();
		return composite;
	}

	private void buildTree(Composite parent) {
		tree = new Tree(parent, SWT.MULTI);
		treeItemRoot = new TreeItem(tree, SWT.NONE);
		treeItemRoot.setText(gn.getName());
		treeItemRoot.setData(gn);
		// TODO: setImage

		List<Transition> transitions = gn.getTransitions();
		for (Transition t : transitions) {
			TreeItem treeItemTransition = new TreeItem(treeItemRoot, SWT.NONE);
			treeItemTransition.setText(t.getName().toString());
			treeItemTransition.setImage(SWTResourceManager
					.getImage(RESOURCES_PATH + "transition.gif"));
			treeItemTransition.setData(t);

			PlaceRefList inputs = t.getInputs();
			TreeItem treeItemInputs = new TreeItem(treeItemTransition, SWT.NONE);
			treeItemInputs.setText("Input Places");
			treeItemInputs.setImage(SWTResourceManager.getImage(RESOURCES_PATH
					+ "inputs.gif"));
			for (Place place : inputs.toPlaceList()) {
				buildPlace(treeItemInputs, place);
			}
			treeItemInputs.setData(inputs);
			treeItemInputs.setExpanded(true);

			// TODO: duplicating code!
			PlaceRefList outputs = t.getOutputs();
			TreeItem treeItemOutputs = new TreeItem(treeItemTransition,
					SWT.NONE);
			treeItemOutputs.setText("Output Places");
			treeItemOutputs.setImage(SWTResourceManager.getImage(RESOURCES_PATH
					+ "outputs.gif"));
			for (Place place : outputs.toPlaceList()) {
				buildPlace(treeItemOutputs, place);
			}
			treeItemOutputs.setData(outputs);
			treeItemOutputs.setExpanded(true);

//			Collection<Function> predicates = t.getPredicates().getAllValues();
//			if (predicates.size() > 0) {
				TreeItem treeItemPredicates = new TreeItem(treeItemTransition,
						SWT.NONE);
				treeItemPredicates.setText("Predicates");
				treeItemPredicates.setImage(SWTResourceManager
						.getImage(Constants.RESOURCES_PATH + "functions.gif"));
			treeItemPredicates.setData(t.getPredicates());
//				treeItemPredicates.setData(predicates);
//				List<Function> insertedPredicates = new ArrayList<Function>();
//				for (Function p : predicates) {
//					if (!insertedPredicates.contains(p)) {
//						TreeItem treeItemFunction = new TreeItem(
//								treeItemPredicates, SWT.NONE);
//						treeItemFunction.setText(p.getName());
//						treeItemFunction.setImage(SWTResourceManager
//								.getImage(Constants.RESOURCES_PATH
//										+ "function.png"));
//						treeItemFunction.setData(p);
//						insertedPredicates.add(p);
//					}
//				}
//			}

			TreeItem treeItemCapacities = new TreeItem(treeItemTransition,
						SWT.NONE);
			treeItemCapacities.setText("Capacities");
			treeItemCapacities.setData(t.getCapacities());

			treeItemTransition.setExpanded(true);
		}

		List<Token> tokens = new ArrayList<Token>();
		for (Token t : gn.getTokens()) {
			if (t.getHost() == null)
				tokens.add(t);
		}
		if (tokens.size() > 0) {
			TreeItem treeItemOtherTokens = new TreeItem(treeItemRoot, SWT.NONE);
			treeItemOtherTokens.setText("Tokens Without Hosts");
			treeItemOtherTokens.setImage(SWTResourceManager
					.getImage(Constants.RESOURCES_PATH + "tokens_alone.gif"));
			buildTokens(treeItemOtherTokens, tokens);
			treeItemOtherTokens.setExpanded(true);
		}

		List<Place> otherPlaces = new ArrayList<Place>();
		for (Place p : gn.getPlaces()) {
			if (p.getLeftTransition() == null && p.getRightTransition() == null)
				otherPlaces.add(p);
		}
		if (otherPlaces.size() > 0) {
			TreeItem treeItemOtherPlaces = new TreeItem(treeItemRoot, SWT.NONE);
			treeItemOtherPlaces.setText("Other Places");
			for (Place p : otherPlaces)
				buildPlace(treeItemOtherPlaces, p);
			treeItemOtherPlaces.setExpanded(true);
		}
		
		TreeItem treeItemFunctions = new TreeItem(treeItemRoot, SWT.NONE);
		treeItemFunctions.setText("Functions");// TODO: maybe all functions -
												// characteristic, predicates,
												// other
		treeItemFunctions.setImage(SWTResourceManager
				.getImage(Constants.RESOURCES_PATH + "functions.gif"));
		treeItemFunctions.setData(new FunctionReference(null)); // XXX
		List<String> functions = gn.getFunctionNames();
		for (String f : functions) {
			TreeItem treeItemFunction = new TreeItem(treeItemFunctions,
					SWT.NONE);
			treeItemFunction.setText(f);
			treeItemFunction.setImage(SWTResourceManager
					.getImage(Constants.RESOURCES_PATH + "function.png"));
			treeItemFunction.setData(new FunctionReference(f)); //XXX was Function!
		}
		treeItemFunctions.setExpanded(true);

		TreeItem treeItemVisualParameters = new TreeItem(treeItemRoot, SWT.NONE);
		treeItemVisualParameters.setText("Visual Parameters");
		treeItemVisualParameters.setImage(SWTResourceManager
				.getImage(RESOURCES_PATH + "visualparameters.gif"));
		treeItemVisualParameters.setExpanded(true);

		treeItemRoot.setExpanded(true);

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tree.getSelection().length > 0) {
					// tree.getSelection()[0].setExpanded(true);//? sometimes
					// Selection.length == 0

					Selection selection = getParent(GnView.class).getSelection();
					if ((e.stateMask & SWT.CTRL) == 0) {
						selection.clear();
					}
					for (TreeItem t : tree.getSelection()) {
						selection.add(t.getData());
					}
				}
			}
		});

		if (gn.getParent(Document.class).isReadOnly()) return;

		tree.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent arg0) {
				TreeView.this.dispatchEvent(TreeDoubleClickEvent.class);
			}

			public void mouseDown(MouseEvent arg0) {
			}

			public void mouseUp(MouseEvent arg0) {
			}
			
		});

		tree.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					// TODO why here and not in controller? and why not in keyReleased?
					gn.remove(tree.getSelection()[0].getData());
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		DragSource dragSource = new DragSource(tree, DND.DROP_MOVE | DND.DROP_COPY);
		//TODO: prou4i DND constants
		dragSource.setTransfer(new Transfer[]{TextTransfer.getInstance()});
		dragSource.addDragListener(new DragSourceListener() {

			public void dragFinished(DragSourceEvent event) {
			}

			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					//List<Object> selection = getParent(GnView.class).getSelection().getObjects();
					event.data = ".";//?; also does not permit empty string!
				}
			}

			public void dragStart(DragSourceEvent event) {
				List<Object> selection = getParent(GnView.class).getSelection().getObjects();
				Class<?> firstSelectedClass = null;
				boolean doit = true;
				if (selection.size() == 0) {
					doit = false;
				} else {
					// we want all objects to be of same type
					//TODO: this should be in the drop method?
					for (Object o : selection) {
						if (firstSelectedClass == null) {
							if (o instanceof Place) {
								firstSelectedClass = Place.class;
							} else if (o instanceof Token) {
								firstSelectedClass = Token.class; //this includes TokenGenerators
							} else if (o instanceof Characteristic) {
								firstSelectedClass = Characteristic.class;
							} else if (o instanceof FunctionReference) {
								firstSelectedClass = FunctionReference.class;
							} else {
								doit = false;
								break;
							}
						} else {
							if (! firstSelectedClass.isAssignableFrom(o.getClass())) {
								doit = false;
								break;
							}
						}
					}
				}
				if (!doit) {
					event.doit = false;
				}
			}
			
		});
		
		DropTarget dropTarget = new DropTarget(tree, DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
		dropTarget.addDropListener(new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
					//TODO: if a new object is inserted (dragged from coolbar to tree item) - using selection is too bad
					//String text = (String)event.data;//not used...
					final Object target = ((TreeItem)event.item).getData();
					final List<Object> selection = getParent(GnView.class).getSelection().getObjects();
					getParent(MainForm.class).dispatchEvent(new DropObjectEvent(selection, target));
				}
			}

			public void dropAccept(DropTargetEvent event) {
			}
			
		});
	}

	private void buildTokens(TreeItem root, List<Token> tokens) {
		for (Token t : tokens) {
			TreeItem treeItemToken = new TreeItem(root, SWT.NONE);
			treeItemToken.setText(t.getName().toString());
			if (t instanceof TokenGenerator)
				treeItemToken.setImage(SWTResourceManager
						.getImage(Constants.RESOURCES_PATH
								+ "token_generator.gif"));
			else
				treeItemToken.setImage(SWTResourceManager
						.getImage(Constants.RESOURCES_PATH + "token.png"));
			treeItemToken.setData(t);
			for (Characteristic c : t.getChars()) {
				TreeItem treeItemChar = new TreeItem(treeItemToken, SWT.NONE);
				String label = c.getName() + " = " + c.getValue();
				if (label.length() > 23) {
					label = label.substring(0, 20) + "...";
				}
				treeItemChar.setText(label);
				// TODO: Icon - based on type (double, string, vector...)
				// TODO: Font - based on name: Default - bold, Color - colored
				treeItemChar.setData(c);
				for (String value : c.getHistoryValues().getValues()) {
					TreeItem treeItemValue = new TreeItem(treeItemChar,
							SWT.NONE);
					treeItemValue.setText(value);
					treeItemValue.setData(c);
				}
			}
		}
	}

	private void buildPlace(TreeItem root, Place place) {
		TreeItem treeItemPlace = new TreeItem(root, SWT.NONE);
		treeItemPlace.setText(place.getName().toString());
		treeItemPlace.setImage(SWTResourceManager.getImage(RESOURCES_PATH
				+ "place.gif"));
		treeItemPlace.setData(place);
		List<Token> tokens = GnUtil.getTokensAt(gn, place.getId());
		buildTokens(treeItemPlace, tokens);
		if (place.getCharFunction() != null) {
			TreeItem treeItemChar = new TreeItem(treeItemPlace, SWT.NONE);
			treeItemChar.setText("Char. Function: " + place.getCharFunction().getFunctionName());
			treeItemChar.setImage(SWTResourceManager.getImage(RESOURCES_PATH
					+ "function.png"));
			treeItemChar.setData(place.getCharFunction());
		}
		if (place.getMergeRule() != null) {
			TreeItem treeItemMerge = new TreeItem(treeItemPlace, SWT.NONE);
			treeItemMerge.setText("Merge Rule: " + place.getMergeRule().getFunctionName());
			treeItemMerge.setImage(SWTResourceManager.getImage(RESOURCES_PATH
					+ "function.png"));
			treeItemMerge.setData(place.getMergeRule());
		}
		treeItemPlace.setExpanded(true);		
	}
	
	@Override
	public void syncUpdate(BaseObservable observable, final Object arg) {
		//TODO: getCurrent() ? NullPointerException
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				if (arg instanceof Command) { // TODO: napravi tova da se izvikva pri
					// F5
					// FIXME: on undo/redo there is no Command argument!!! also now the selection is not part of GNDocument!
					// TODO: make smarter solution - do not recreate the whole tree, keep the selection
					if (tree != null)
						tree.dispose();
					buildTree((Composite) getUIComponent());
					((Composite) getUIComponent()).layout();
				} else {
					// ...select the selected item in the tree!
					//Selection sel = getParent(GnViewComposite.class).getSelection();
					//if (sel.get().size() > 0) {// ...?
						//Object obj = sel.get().get(0);
					//}
				}
			}
			
		});
	}
}

