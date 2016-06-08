package net.generalised.genedit.view.properties;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.demo.DemoMain;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.dataaccess.xmlread.FunctionParser;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.Function;
import net.generalised.genedit.model.gn.FunctionFactory;
import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GeneratorMode;
import net.generalised.genedit.model.gn.GnObjectCommon;
import net.generalised.genedit.model.gn.GntcflFunctionFactory;
import net.generalised.genedit.model.gn.TransitionMatrix;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.RichTextName;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.TokenGenerator;
import net.generalised.genedit.model.gn.Transition;
import net.generalised.genedit.view.Constants;
import net.generalised.genedit.view.GnView;
import net.generalised.genedit.view.Selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.cloudgarden.resource.SWTResourceManager;

public class PropertiesView extends BaseView {

	//FIXME: catch exceptions on invalid input, warn the user
	
	private final GnDocument document;
	private final GeneralizedNet gn;
	
	private Table table;

	public PropertiesView(BaseView parent, GeneralizedNet gn) {
		super(parent, gn.getParent(GnDocument.class));
		//super(parent, SWT.H_SCROLL | SWT.V_SCROLL); if ScrolledComposite
		addAsObserverTo(getParent(GnView.class).getSelection());
		this.gn = gn;
		this.document = gn.getParent(GnDocument.class);
	}

	@Override
	protected Widget createUIComponent(Widget parent) {
		Composite composite = new Composite((Composite) parent, SWT.NONE);
		StackLayout layout = new StackLayout();
		composite.setLayout(layout);
		{
			table = new Table(composite, SWT.FULL_SELECTION);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			
			TableColumn tableColumnName = new TableColumn(table, SWT.NONE);
			tableColumnName.setText("Property");
			tableColumnName.setWidth(128);

			TableColumn tableColumnValue= new TableColumn(table, SWT.NONE);
			tableColumnValue.setText("Value");
			tableColumnValue.setWidth(384);

			//http://download.eclipse.org/eclipse/downloads/documentation/2.0/html/plugins/org.eclipse.platform.doc.isv/reference/api/org/eclipse/swt/custom/TableEditor.html
			if(! document.isReadOnly()) {
				final TableEditor editor = new TableEditor (table);
				table.addSelectionListener(new SelectionAdapter(){
					@Override
					public void widgetSelected(SelectionEvent e) {
						// Clean up any previous editor control
						Control oldEditor = editor.getEditor();
						if (oldEditor != null)
							oldEditor.dispose();
						
						if (document.isInSimulationMode())
							return; //TODO for now
						
						// Identify the selected row
						int index = table.getSelectionIndex ();
						if (index == -1)
							return;
						TableItem item = table.getItem(index);
	
						// The control that will be the editor must be a child of the Table
						Object itemData = item.getData();
						if (itemData instanceof PropertyTool) {
							final Control editControl = ((PropertyTool)itemData).getEditorControl();
							
							//The text editor must have the same size as the cell and must
							//not be any smaller than 50 pixels.
							editor.horizontalAlignment = SWT.LEFT;
							editor.grabHorizontal = true;
							editor.minimumWidth = 50;
	
							// Open the text editor in the second column of the selected row.
							editor.setEditor(editControl, item, 1);
	
							// Assign focus to the text control
							editControl.setFocus ();
						}
					}
				});
			}
		}
		layout.topControl = table;
		composite.layout();
		return composite;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void syncUpdate(BaseObservable o, Object arg) {
		//TODO: if modified object is current viewable, redraw the table
		//if other object is modified - do nothing!
		//TODO: ne raboti pri F5!
		//if (o instanceof Document) {
			Selection sel = getParent(GnView.class).getSelection();// sometimes the selection is the BaseObservable
			if (sel.getObjects().size() > 0) {//...?
				table.removeAll(); //TODO: napravi taka, 4e tova da se izvikva pri Refresh!
				Control topControl = ((StackLayout) ((Composite) getUIComponent()).getLayout()).topControl;
				if (topControl != table) {
					topControl.dispose();
					((StackLayout) ((Composite) getUIComponent()).getLayout()).topControl = table;
				}
	
				Object obj = sel.getObjects().get(0);
				
				if (obj instanceof GeneralizedNet) {
					buildGNTable((GeneralizedNet)obj);
				} else if (obj instanceof Transition) {
					buildTransitionTable((Transition)obj);
				} else if (obj instanceof Place) {
					buildPlaceTable((Place)obj);
				} else if (obj instanceof Token) {
					Token token = (Token) obj;
					buildTokenTable(token);
				} else if (obj instanceof Characteristic) {
					Characteristic characteristic = (Characteristic) obj;
					buildCharTable(characteristic);
				} else if (obj instanceof FunctionReference) { //XXX
					buildFunctionTable((FunctionReference)obj);
				} else if (obj instanceof TransitionMatrix && ((TransitionMatrix<?>)obj).getValueClass() == FunctionReference.class) {
					buildPredicatesMatrix((TransitionMatrix<FunctionReference>) obj);
				} else if (obj instanceof TransitionMatrix && ((TransitionMatrix<?>)obj).getValueClass() == IntegerInf.class) {
					buildCapacitiesMatrix((TransitionMatrix<IntegerInf>) obj);
				} // TODO: VisualParameters... but they are not in selection (why not)???
				((Composite) getUIComponent()).layout();
			}
	}
	
//	private TableItem createSimpleTableItem(Table parent, String name, BaseModel object, String property) {
//		TableItem result = new TableItem(parent, SWT.NONE);
//
//		
//		
//		return result;
//	}
	
	private void buildCommonPropertiesTable(final GnObjectCommon gnObject) {
		TableItem id = new TableItem(table, SWT.NONE);
		id.setText(new String[]{"Unique Identifier", gnObject.getId()});
		id.setData(new StringPropertyTool(id, new Listener(){
			public void handleEvent(Event event) {
				gnObject.undoableSet("id", (String)event.data);
			}}));
		
		TableItem name = new TableItem(table, SWT.NONE);
		name.setText(new String[]{"Friendly Name", gnObject.getName().toString()});
		name.setData(new StringPropertyTool(name, new Listener(){
			public void handleEvent(Event event) {
				gnObject.undoableSet("name", new RichTextName((String)event.data));
			}}));

		if (! document.isReadOnly()) {
			TableItem nameMain = new TableItem(table, SWT.NONE);
			nameMain.setText(new String[]{"Friendly Name: Main Part", gnObject.getName().getMainPart()});
			nameMain.setData(new StringPropertyTool(nameMain, new Listener(){
				public void handleEvent(Event event) {
					RichTextName richText = new RichTextName((String)event.data, gnObject.getName().getSubscriptPart());
					gnObject.undoableSet("name", richText);
				}}));
	
			TableItem nameSubscript = new TableItem(table, SWT.NONE);
			nameSubscript.setText(new String[]{"Friendly Name: Subscript", gnObject.getName().getSubscriptPart()});
			nameSubscript.setData(new StringPropertyTool(nameSubscript, new Listener(){
				public void handleEvent(Event event) {
					RichTextName richText = new RichTextName(gnObject.getName().getMainPart(), (String)event.data);
					gnObject.undoableSet("name", richText);
				}}));
		}
		
		TableItem priority = new TableItem(table, SWT.NONE);
		priority.setText(new String[]{"Priority", "" + gnObject.getPriority()});
		priority.setData(new IntegerPropertyTool(priority, new Listener(){
			public void handleEvent(Event event) {
				gnObject.undoableSet("priority", (Integer)event.data);
			}}, false));
	}
	
	private void buildGNTable(final GeneralizedNet generalizedNet) {
		TableItem name = new TableItem(table, SWT.NONE);
		name.setText(new String[]{"Name", generalizedNet.getName()});
		name.setData(new StringPropertyTool(name, new Listener(){
			public void handleEvent(Event event) {
				generalizedNet.undoableSet("name", (String)event.data);
			}}));

		TableItem time = new TableItem(table, SWT.NONE);
		time.setText(new String[]{"Time", Integer.toString(generalizedNet.getTime())});
		time.setData(new IntegerPropertyTool(time, new Listener(){
			public void handleEvent(Event event) {
				generalizedNet.undoableSet("time", (Integer)event.data);
			}
		}, false));

		TableItem timeStart = new TableItem(table, SWT.NONE);
		timeStart.setText(new String[]{"Start Time", Integer.toString(generalizedNet.getTimeStart())});
		timeStart.setData(new IntegerPropertyTool(timeStart, new Listener(){
			public void handleEvent(Event event) {
				generalizedNet.undoableSet("timeStart", (Integer)event.data);
			}
		}, false));

		TableItem timeStep = new TableItem(table, SWT.NONE);
		timeStep.setText(new String[]{"Time Step", Integer.toString(generalizedNet.getTimeStep())});
		timeStep.setData(new IntegerPropertyTool(timeStep, new Listener(){
			public void handleEvent(Event event) {
				generalizedNet.undoableSet("timeStep", (Integer)event.data);
			}
		}, false));
		
		TableItem currentTime = new TableItem(table, SWT.NONE);
		currentTime.setText(new String[]{"Current Time", Integer.toString(generalizedNet.getCurrentTime())});
	
		TableItem language = new TableItem(table, SWT.NONE);
		language.setText(new String[]{"Function Language", generalizedNet.getFunctionLanguage()});
		List<String> availableLanguages = FunctionFactory.getSupportedLanguages();
		language.setData(new ComboPropertyTool(language, new Listener(){
			public void handleEvent(Event event) {
				generalizedNet.undoableSet("functionLanguage", (String)event.data);
				// TODO: it's not so simple - what about existing functions written in another language?
				// TODO handle "New..." option
			}}, availableLanguages, false));

		TableItem root = new TableItem(table, SWT.NONE);
		root.setText(new String[]{"Is Root GN", Boolean.toString(generalizedNet.isRoot())});
		
		//TODO: x,y? 
	}
	
	private void buildTransitionTable(final Transition transition) {
		buildCommonPropertiesTable(transition);
		
		TableItem startTime = new TableItem(table, SWT.NONE);
		startTime.setText(new String[]{"Start Time", Integer.toString(transition.getStartTime())});
		//TODO: Command...

		TableItem lifeTime = new TableItem(table, SWT.NONE);
		lifeTime.setText(new String[]{"Life Time", transition.getLifeTime().toString()});
		//TODO: Command...
		
		TableItem type = new TableItem(table, SWT.NONE);
		type.setText(new String[]{"Type", transition.getType()});
		//TODO: Command...
		
		TableItem x = new TableItem(table, SWT.NONE);
		x.setText(new String[]{"Position X", Integer.toString(transition.getVisualPositionX())});
		x.setData(new IntegerPropertyTool(x, new Listener() {
			public void handleEvent(Event event) {
				transition.undoableSet("visualPositionX", event.data);
				// TODO: arcs...
			}
		}, false));

		TableItem y = new TableItem(table, SWT.NONE);
		y.setText(new String[]{"Position Y", Integer.toString(transition.getVisualPositionY())});
		y.setData(new IntegerPropertyTool(y, new Listener() {
			public void handleEvent(Event event) {
				transition.undoableSet("visualPositionY", event.data);
			}
		}, false));

		TableItem height = new TableItem(table, SWT.NONE);
		height.setText(new String[]{"Height", Integer.toString(transition.getVisualHeight())});
		height.setData(new IntegerPropertyTool(height, new Listener() {
			public void handleEvent(Event event) {
				transition.undoableSet("visualHeight", event.data);
			}
		}, false));
	}
	
	private void buildPlaceTable(final Place place) {
		buildCommonPropertiesTable(place);
		
		TableItem capacity = new TableItem(table, SWT.NONE);
		capacity.setText(new String[]{"Capacity", place.getCapacity().toString()});
		capacity.setData(new IntegerPropertyTool(capacity, new Listener() {
			public void handleEvent(Event event) {
				place.undoableSet("capacity", event.data);
			}
		}, true));

		TableItem charFunction = new TableItem(table, SWT.NONE);
		List<String> functions = gn.getFunctionNames();
		String charFunctionName = ""; //"(None)";// TODO: duplicate code - "(None)" is defined in the tool
		if (place.getCharFunction() != null)
			charFunctionName = place.getCharFunction().getFunctionName();
		charFunction.setText(new String[] { "Characteristic Function",
				charFunctionName });
		charFunction.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "function.png"));
		charFunction.setData(new ComboPropertyTool(charFunction,
				new Listener() {
					public void handleEvent(Event event) {
						String name = (String) event.data;
						FunctionReference function = getFunctionFromEventData(name);
						place.undoableSet("charFunction", function);
					}
				}, functions, true));
		
		TableItem mergeRule = new TableItem(table, SWT.NONE);
		String mergeRuleName = ""; //"(None)";// TODO: duplicate code
		if (place.getMergeRule() != null)
			mergeRuleName = place.getMergeRule().getFunctionName();
		mergeRule.setText(new String[] { "Merge Rule", mergeRuleName });
		mergeRule.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "function.png"));
		mergeRule.setData(new ComboPropertyTool(mergeRule, new Listener() {
			public void handleEvent(Event event) {
				String name = (String) event.data;
				FunctionReference function = getFunctionFromEventData(name);
				//TODO: boolean merge - mai e nujno (6toto moje da e taka: ako rule != null - true)
				place.undoableSet("mergeRule", function);
			}
		}, functions, true));

		TableItem outputFor = new TableItem(table, SWT.NONE);
		String leftTransition = "";
		if (place.getLeftTransition() != null) {
			leftTransition = place.getLeftTransition().getId();
		}
		outputFor.setText(new String[] {"Output for Transition", leftTransition});
		outputFor.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "outputs.gif"));

		TableItem inputFor = new TableItem(table, SWT.NONE);
		String rightTransition = "";
		if (place.getRightTransition() != null) {
			rightTransition = place.getRightTransition().getId();
		}
		inputFor.setText(new String[] {"Input for Transition", rightTransition});
		inputFor.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "inputs.gif"));
		
		TableItem x = new TableItem(table, SWT.NONE);
		x.setText(new String[]{"Position X", Integer.toString(place.getVisualPositionX())});
		x.setData(new IntegerPropertyTool(x, new Listener() {
			public void handleEvent(Event event) {
				place.undoableSet("visualPositionX", event.data);
			}
		}, false));

		TableItem y = new TableItem(table, SWT.NONE);
		y.setText(new String[]{"Position Y", Integer.toString(place.getVisualPositionY())});
		y.setData(new IntegerPropertyTool(y, new Listener() {
			public void handleEvent(Event event) {
				place.undoableSet("visualPositionY", event.data);
			}
		}, false));
	}
	
	private void buildTokenTable(final Token token) {
		buildCommonPropertiesTable(token);
		
		TableItem host = new TableItem(table, SWT.NONE);
		String hostName = "";
		if (token.getHost() != null)
			hostName = token.getHost().getName().toString();
		host.setText(new String[]{"Host", hostName});
		host.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "place.gif"));
		//TODO: Command...
		
		TableItem enterTime = new TableItem(table, SWT.NONE);
		enterTime.setText(new String[]{"Entering Time", Integer.toString(token.getEnteringTime())});
		enterTime.setData(new IntegerPropertyTool(enterTime, new Listener() {
			public void handleEvent(Event event) {
				token.undoableSet("enteringTime", (Integer)event.data);
			}
		}, false));

		//ignored by GNTicker, extension of GN
//		TableItem leaveTime = new TableItem(table, SWT.NONE);
//		leaveTime.setText(new String[]{"Leaving Time", token.getLeavingTime().toString()});
//		leaveTime.setData(new IntegerPropertyTool(leaveTime, new Listener() {
//			public void handleEvent(Event event) {
//				token.undoableSet("leavingTime", new IntegerInf((Integer)event.data));
//			}
//		}, false));

		if (token instanceof TokenGenerator) {
			final TokenGenerator generator = (TokenGenerator)token;

			TableItem type = new TableItem(table, SWT.NONE);
			type.setText(new String[]{"Type", generator.getType().toString()});
			//TODO: capital first letter
			//TODO: Drop-down

			if (generator.getType().equals(GeneratorMode.PERIODIC)) {
				TableItem period = new TableItem(table, SWT.NONE);
				period.setText(new String[]{"Period", Integer.toString(generator.getPeriod())});
				period.setData(new IntegerPropertyTool(period, new Listener() {
					public void handleEvent(Event event) {
						token.undoableSet("period", (Integer)event.data);
					}
				}, false));
			} else if (generator.getType().equals(GeneratorMode.CONDITIONAL)) {
				TableItem predicate = new TableItem(table, SWT.NONE);
				String predicateName = "";
				if (generator.getPredicate() != null) {
					predicateName = generator.getPredicate().getFunctionName();
				}
				predicate.setText(new String[]{"Predicate", predicateName});
				predicate.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "function.png"));
				//TODO: Combo, do not forget "true" and maybe "false"
			}
			
			// TODO UnitTest: if the user changes the type, for example, to Periodic, then "Predicate" should appear 
		}

		Characteristic defaultChar = token.getDefaultCharacteristic();
		String defaultValue = "";
		if (defaultChar != null)
			defaultValue = defaultChar.getValue();
		//TODO: ami tipa?
		TableItem defaultCharTableItem = new TableItem(table, SWT.NONE);
		defaultCharTableItem.setText(new String[]{"Default Characteristic", defaultValue});
		//TODO: Command

		Characteristic colorChar = token.getChars().get("Color");
		String colorValue = "Default";
		if (colorChar != null) //TODO: && colorChar.equals("string")
			colorValue = colorChar.getValue();
		TableItem color = new TableItem(table, SWT.NONE);
		color.setText(new String[]{"Color", colorValue});
		//TODO: Command, ColorPickerPropertyTool
	}
	
	private void buildCharTable(final Characteristic characteristic) {
		TableItem name = new TableItem(table, SWT.NONE);
		name.setText(new String[]{"Name", characteristic.getName()});
		name.setData(new StringPropertyTool(name, new Listener(){
			public void handleEvent(Event event) {
				characteristic.undoableSet("name", event.data); // Rich Text...
			}}));

		TableItem type = new TableItem(table, SWT.NONE);
		type.setText(new String[]{"Type", characteristic.getType()});
		//TODO: Combo?
		type.setData(new StringPropertyTool(type, new Listener(){
			public void handleEvent(Event event) {
				characteristic.undoableSet("type", event.data);
			}}));
		
		TableItem value = new TableItem(table, SWT.NONE);
		value.setText(new String[]{"Value", characteristic.getValue()});
		value.setData(new StringPropertyTool(value, new Listener(){
			public void handleEvent(Event event) {
				//TODO: validation depending on type? if JS - use eval or JSON validator!
				characteristic.undoableSet("value", event.data);
			}}));
	}
	
	private void buildFunctionTable(final FunctionReference functionReference) {
		
		if (functionReference.getFunctionName().length() == 0) {
			return;
		}
		
		TableItem text = new TableItem(table, SWT.NONE);
		text.setText(new String[]{"", "To edit the function, double click its icon in the tree."});

		final String functionName = functionReference.getFunctionName();
		TableItem name = new TableItem(table, SWT.NONE);
		name.setText(new String[]{"Name", functionName});

		FunctionFactory factory = this.gn.getFunctionFactory();
		FunctionParser<?> parser = factory.getFunctionParser();
		List<? extends Function> functionsList = parser.parseFunctions(this.gn.getFunctions());
		Function function = null;
		for (Function f : functionsList) {
			if (functionName.equals(f.getName())) {
				function = f;
				break;
			}
		}
		if (function == null) {
			return;
		}
		
		String def = function.getDefinition();
		TableItem source = new TableItem(table, SWT.NONE);
		source.setText(new String[]{"Function Definition", def});

		TableItem language = new TableItem(table, SWT.NONE);
		language.setText(new String[]{"Language", this.gn.getFunctionLanguage()});

		if (this.gn.getFunctionLanguage().equals(GntcflFunctionFactory.LANGUAGE)) {
			TableItem frame = new TableItem(table, SWT.NONE);
			int indexOfFirstQuote = def.indexOf('\"') + 1;
			int indexOfSecondQuote = def.indexOf('\"', indexOfFirstQuote);
			String frameDef = indexOfFirstQuote > 1 && indexOfSecondQuote >= indexOfFirstQuote ?
					def.substring(indexOfFirstQuote, indexOfSecondQuote) : "(wrong syntax)";
			frame.setText(new String[]{"Frame Definition", frameDef});

			TableItem args = new TableItem(table, SWT.NONE); 
			int indexOfSecondBracket = def.indexOf('(', def.indexOf('(') + 1) + 1;
			int indexOfFirstClosingBracket = def.indexOf(')', indexOfSecondBracket);
			String formalArgs = indexOfSecondQuote > 0 && indexOfSecondBracket > indexOfSecondQuote && indexOfFirstClosingBracket >= indexOfSecondBracket ? 
					def.substring(indexOfSecondBracket, indexOfFirstClosingBracket) : "(wrong syntax)";
			args.setText(new String[]{"Formal Arguments", formalArgs});

			TableItem vars = new TableItem(table, SWT.NONE); 
			int indexOfThirdBracket = def.indexOf('(', indexOfSecondBracket) + 1;
			int indexOfSecondClosingBracket = def.indexOf(')', indexOfThirdBracket);
			String localVars = indexOfFirstClosingBracket > 0 && indexOfThirdBracket > indexOfFirstClosingBracket && indexOfSecondClosingBracket >= indexOfThirdBracket ? 
					def.substring(indexOfThirdBracket, indexOfSecondClosingBracket) : "(wrong syntax)";
			vars.setText(new String[]{"Local Variables", localVars});
			
			TableItem body = new TableItem(table, SWT.NONE);
			int indexBodyBegin = indexOfSecondClosingBracket + 1;
			int indexBodyEnd = indexOfSecondClosingBracket > 0 ? def.lastIndexOf(')') : -1;
			String singleLineBody = indexBodyBegin > 0 && indexBodyEnd >= indexBodyBegin ?  
					def.substring(indexOfSecondClosingBracket + 1, def.lastIndexOf(')'))
					.replace('\n', ' ').replace('\r', ' ').trim()
					: "(wrong syntax)";
			body.setText(new String[]{"Body", singleLineBody});
		}
	}
	
	private Table buildMatrix(List<PlaceReference> inputs,
			List<PlaceReference> outputs) {
		final Table tableMatrix = new Table((Composite) getUIComponent(), SWT.FULL_SELECTION);
		tableMatrix.setHeaderVisible(true);//because of resize
		tableMatrix.setLinesVisible(true);
		GridData tableLData = new GridData();
		tableLData.grabExcessVerticalSpace = true;
		tableLData.grabExcessHorizontalSpace = true;
		tableLData.horizontalAlignment = SWT.FILL;
		tableLData.verticalAlignment = SWT.FILL;
		tableMatrix.setLayoutData(tableLData);
		
		TableColumn columnFrom = new TableColumn(tableMatrix, SWT.NONE);
		columnFrom.setWidth(64);
		
		for (int col = 0; col < outputs.size(); ++col) {
			TableColumn tc = new TableColumn(tableMatrix, SWT.NONE);
			tc.setWidth(48);
		}
		
		String[] names = new String[1 + outputs.size()];
		
		TableItem tableItemTo = new TableItem(tableMatrix, SWT.NONE);
		names[0] = "From\\To";
		for (int col = 0; col < outputs.size(); ++col)
			names[col + 1] = outputs.get(col).getPlace().getName().toString();   
		tableItemTo.setText(names);
		
		for (int row = 0; row < inputs.size(); ++row) {
			TableItem tableItemRow = new TableItem(tableMatrix, SWT.NONE);
			tableItemRow.setText(inputs.get(row).getPlace().getName().toString());
		}
		
		columnFrom.pack();//? ami ostanalite?
		
		((StackLayout)((Composite) getUIComponent()).getLayout()).topControl = tableMatrix;

		return tableMatrix;
	}
	
	private void buildPredicatesMatrix(final TransitionMatrix<FunctionReference> predicates) {
		if(DemoMain.inDemoMode) return;//remove this line
		
		final List<PlaceReference> outputs = predicates.getTransition().getOutputs();
		final List<PlaceReference> inputs = predicates.getTransition().getInputs();
		
		final Table tableMatrix = buildMatrix(inputs, outputs); 

		tableMatrix.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				if (event.height < 20) //TODO: 20?...
					event.height = 20;
			}
		});

		String[] names = new String[1 + outputs.size()];
		for (int row = 0; row < inputs.size(); ++row) {
			TableItem tableItemRow = tableMatrix.getItem(row + 1);
			for (int col = 0; col < outputs.size(); ++col) {
				Place from = inputs.get(row).getPlace();
				Place to = outputs.get(col).getPlace();
				FunctionReference predicate = predicates.getAt(from, to); 
				names[col + 1] = predicate.getFunctionName();
			}
			tableItemRow.setText(names);
		}
		
		final List<String> functions = new ArrayList<String>();
		functions.add("false");
		functions.add("true");
		functions.addAll(gn.getFunctionNames());

		//http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet96.java?view=co
		final TableCursor cursor = new TableCursor(tableMatrix, SWT.NONE);
		final ControlEditor editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.horizontalAlignment = SWT.LEFT;
		editor.verticalAlignment = SWT.TOP;
		if (! document.isReadOnly()) {
			cursor.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Control oldEditor = editor.getEditor();
					if (oldEditor != null)
						oldEditor.dispose();
	
					if (document.isInSimulationMode())
						return; //zasega
					
					final TableItem row = cursor.getRow();
					final int column = cursor.getColumn();
					
					if (column == 0 || tableMatrix.indexOf(row) == 0)
						return;
					//TODO: header poletata i te da sa redaktiruemi - s tqh da moje da se zadavat predikati za cql red/kolona/tablica
					//no tam trqbva da ima opciq "Do not change" - lekooo
	
					ComboPropertyTool combo = new ComboPropertyTool(cursor,
							row.getText(column),
							new Listener() {
								public void handleEvent(Event event) {
									String name = (String) event.data;
									FunctionReference predicate = getFunctionFromEventData(name);
									Place from = inputs.get(tableMatrix.indexOf(row) - 1).getPlace();
									Place to = outputs.get(column - 1).getPlace();
									predicates.undoableSetAt(from, to, predicate);
								}
							},
							functions,
							false);
	
					editor.setEditor(combo.getEditorControl());
					combo.getEditorControl().setFocus();
				}
			});
		}
		cursor.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//TODO: da otvarq selected function (osven ako ne sme kliknali headera) v prozorec za redaktirane!
//				final TableItem row = cursor.getRow();
//				final int column = cursor.getColumn();
//				Place from = inputs.get(tableMatrix.indexOf(row) - 1).getPlace();
//				Place to = outputs.get(column - 1).getPlace();
//				Function predicate = predicates.getAt(from, to); 
//				MainController.onPredicatesMatrixDoubleClick(PropertiesView.this, predicate);
			}
		});
	}
	
	private void buildCapacitiesMatrix(final TransitionMatrix<IntegerInf> capacities) {
		final List<PlaceReference> outputs = capacities.getTransition().getOutputs();
		final List<PlaceReference> inputs = capacities.getTransition().getInputs();
		
		final Table tableMatrix = buildMatrix(inputs, outputs); 
		
		String[] names = new String[1 + outputs.size()];
		for (int row = 0; row < inputs.size(); ++row) {
			TableItem tableItemRow = tableMatrix.getItem(row + 1);
			for (int col = 0; col < outputs.size(); ++col) {
				Place from = inputs.get(row).getPlace();
				Place to = outputs.get(col).getPlace();
				IntegerInf capacity = capacities.getAt(from, to); 
				names[col + 1] = capacity.toString();
			}
			tableItemRow.setText(names);
		}
		
		//http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet96.java?view=co
		final TableCursor cursor = new TableCursor(tableMatrix, SWT.NONE);
		final ControlEditor editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		editor.horizontalAlignment = SWT.LEFT;
		editor.verticalAlignment = SWT.TOP;
		editor.minimumWidth = 32;
		if(! document.isReadOnly()) {
			cursor.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
	//				tableMatrix.setSelection(new TableItem[] { cursor.getRow()});
	//			}
	//			public void widgetDefaultSelected(SelectionEvent e) {
					Control oldEditor = editor.getEditor();
					if (oldEditor != null)
						oldEditor.dispose();
	
					if (document.isInSimulationMode())
						return; //zasega
					
					final TableItem row = cursor.getRow();
					final int column = cursor.getColumn();
					
					if (column == 0 || tableMatrix.indexOf(row) == 0)
						return;
					//TODO: header poletata i te da sa redaktiruemi - s tqh da moje da se zadavat predikati za cql red/kolona/tablica
					//no tam trqbva da ima opciq "Do not change" - lekooo
	
					IntegerPropertyTool text = new IntegerPropertyTool(cursor,
							row.getText(column),
							new Listener() {
								public void handleEvent(Event event) {
									Place from = inputs.get(tableMatrix.indexOf(row) - 1).getPlace();
									Place to = outputs.get(column - 1).getPlace();
									capacities.undoableSetAt(from, to, (IntegerInf)event.data);
								}
							},
							true);
	
					editor.setEditor(text.getEditorControl());
					text.getEditorControl().setFocus();
				}
			});
		}
	}
	
	private FunctionReference getFunctionFromEventData(String eventData) {
		FunctionReference function;
		if (eventData == null) {
			function = null;
		} else if (eventData.length() == 0) {
			//TODO: create new function, assign to the arc, open for editing...
			//EditGnController.insertFunction(getParent(MainForm.class));
			//predicate = gn.getFunctions().get(gn.getFunctions().size() - 1);
			function = null;//:(
			//FIXME too dirty hack; also requires new thread...
//		} else if (eventData.equals("true") || eventData.equals("false")) {
//			function = gn.getFunctionFactory().getPredefinedConstant(eventData);
//			//TODO: too dirty
		} else {
			function = new FunctionReference(eventData); //gn.getFunctions().get(eventData);
		}
		return function;
	}
}
