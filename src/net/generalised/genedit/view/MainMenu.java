package net.generalised.genedit.view;

import java.io.File;

import net.generalised.genedit.baseapp.controller.Event;
import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.controller.EditGnController;
import net.generalised.genedit.controller.MainController;
import net.generalised.genedit.demo.DemoMain;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.simulation.controller.SimulationController;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;


import com.cloudgarden.resource.SWTResourceManager;

// TODO: view should not reference directly controllers 

/**
 * Creates the main menu of the application.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class MainMenu extends BaseView {
	
	// Events that can be dispatched by this view
	public static class NewFileEvent extends Event {
	}
	public static class OpenFileEvent extends Event {
		private File file;
		
		public OpenFileEvent() {
			
		}
		
		public OpenFileEvent(File file) {
			setFile(file);
		}
		
		public void setFile(File file) {
			this.file = file;
		}
		
		public File getFile() {
			return this.file;
		}
	}
	public static class SaveFileEvent extends Event {
	}
	public static class SaveAsFileEvent extends Event {
	}
	public static class CloseFileEvent extends Event {
	}
	public static class ReloadFileEvent extends Event {
	}
	public static class ImportGenneteFileEvent extends Event {
	}
	public static class ExportToBitmapEvent extends Event {
	}
	public static class ExportGraphicalStructureToTexEvent extends Event {
	}
	public static class ExportToSvgEvent extends Event {
	}
	public static class ExportFormalDefToTexEvent extends Event {
	}
	public static class ExitEvent extends Event {
	}
	public static class UndoEvent extends Event {
	}
	public static class RedoEvent extends Event {
	}
	public static class InsertTransitionEvent extends Event {
	}
	public static class InsertInputPlaceEvent extends Event {
	}
	public static class InsertOutputPlaceEvent extends Event {
	}
	public static class InsertTokenEvent extends Event {
	}
	public static class InsertTokenGeneratorEvent extends Event {
	}
	public static class InsertCharacteristicEvent extends Event {
	}
	public static class InsertFunctionEvent extends Event {
	}
	public static class UnionTransitionsEvent extends Event {
	}
	public static class IntersectTransitionsEvent extends Event {
	}
	public static class SubtractTransitionsEvent extends Event {
	}
	public static class CompareGnsEvent extends Event {
	}
	public static class UnionGnsEvent extends Event {
	}
	public static class IntersectGnsEvent extends Event {
	}
	public static class SubtractGnsEvent extends Event {
	}
	public static class ApplyG2PrimOperatorEvent extends Event {
	}
	public static class ApplyG6OperatorEvent extends Event {
	}
	public static class CheckGnForErrorsEvent extends Event {
	}
	public static class SettingsEvent extends Event {
	}
	
	public MainMenu(BaseView parent, BaseObservable... observables) {
		super(parent, observables);
	}

	//	@Deprecated
//	private final MainForm view;
//	
//	/**
//	 * Constructs the main menu of this application.
//	 * 
//	 * @param view the form component where the menu has to be attached. Usually the main form.
//	 */
//	@Deprecated
//	public MainMenu(final MainForm view) {
//		super(null);
//		this.view = view;
//	}
//
	private MenuItem pluginsMenuItem;
	
	private Menu constructMenu(Menu parent, String text) {
		MenuItem menuItem = new MenuItem(parent, SWT.CASCADE);
		menuItem.setText(text);
		Menu menu = new Menu(menuItem);
		menuItem.setMenu(menu);
		return menu;
	}
	
	private MenuItem constructMenuItem(Menu parent, String text,
			int accelerator, final Class<? extends Event> eventType) {
		MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(text);
		if (accelerator != 0) {
			result.setAccelerator(accelerator);
		}
		result.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				MainMenu.this.view.dispatchEvent(eventType);
			}
		});
		return result;
	}
	
	private MainForm view;
	
	@Override
	protected Widget createUIComponent(Widget parent) {
		this.view = getParent(MainForm.class);
		Menu mainMenu = new Menu(((Composite) parent).getShell(), SWT.BAR);
		{
			MenuItem fileMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
			fileMenuItem.setText("&File");
			fileMenuItem.setSelection(true);
			{
				Menu fileMenu = new Menu(fileMenuItem);
				if(!DemoMain.inDemoMode) { //remove this line
				
					constructMenuItem(fileMenu, "&New\tCtrl+N", SWT.CTRL + 'N', NewFileEvent.class);
					
					constructMenuItem(fileMenu, "&Open...\tCtrl+O", SWT.CTRL + 'O', OpenFileEvent.class);

					MenuItem saveFileMenuItem = constructMenuItem(fileMenu,
							"&Save\tCtrl+S", SWT.CTRL + 'S', SaveFileEvent.class);
					saveFileMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "save.gif"));

					MenuItem saveAsFileMenuItem = constructMenuItem(fileMenu,
							"Save &As...", 0, SaveAsFileEvent.class);
					saveAsFileMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "saveas.gif"));

					constructMenuItem(fileMenu, "&Close", 0, CloseFileEvent.class);

					constructMenuItem(fileMenu, "&Reload", 0, ReloadFileEvent.class);

					new MenuItem(fileMenu, SWT.SEPARATOR);

					MenuItem importMenuItem = constructMenuItem(fileMenu, "&Import Models Created With Gennete...",
							0, ImportGenneteFileEvent.class); 
					importMenuItem.setEnabled(false);

					constructMenuItem(fileMenu, "&Export To Bitmap...", 0, ExportToBitmapEvent.class);

					constructMenuItem(fileMenu, "Export To TeX...", 0, ExportGraphicalStructureToTexEvent.class);

					constructMenuItem(fileMenu, "Export Formal Definition To TeX...", 0, ExportFormalDefToTexEvent.class);

					MenuItem exportToSvgItem = constructMenuItem(fileMenu, "Export To SVG...", 0, ExportToSvgEvent.class);
					exportToSvgItem.setEnabled(false);

					new MenuItem(fileMenu, SWT.SEPARATOR);
				}
				{
					constructMenuItem(fileMenu, "E&xit\tAlt+F4", 0, ExitEvent.class);
				}
				fileMenuItem.setMenu(fileMenu);
			}
		}
		if(!DemoMain.inDemoMode)//remove this line
		{
			MenuItem editMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
			editMenuItem.setText("&Edit");
			{
				Menu editMenu = new Menu(editMenuItem);
				{
					constructMenuItem(editMenu, "&Undo\tCtrl+Z", SWT.CTRL + 'Z', UndoEvent.class);
				}
				{
					constructMenuItem(editMenu, "&Redo\tCtrl+Y", SWT.CTRL + 'Y', RedoEvent.class);
				}
				{
					new MenuItem(editMenu, SWT.SEPARATOR);
				}
				{
					MenuItem cutMenuItem = constructMenuItem(editMenu, "Cu&t\tCtrl+X", SWT.CTRL+'X', null);
					cutMenuItem.setEnabled(false);
					//TODO: be careful - calls gn.deleteObject ??? 
				}
				{
					MenuItem copyMenuItem = constructMenuItem(editMenu, "&Copy\tCtrl+C", SWT.CTRL+'C', null);
					copyMenuItem.setEnabled(false);
					//TODO:...
				}
				{
					MenuItem pasteMenuItem = constructMenuItem(editMenu, "&Paste\tCtrl+V", SWT.CTRL+'V', null);
					pasteMenuItem.setEnabled(false);
					//TODO:...
				}
				{
					MenuItem deleteMenuItem = new MenuItem(editMenu, SWT.PUSH);
					deleteMenuItem.setText("&Delete\tDel");
					//deleteMenuItem.setAccelerator(SWT.DEL);
					deleteMenuItem.setEnabled(false);
					//TODO: be careful - calls gn.deleteObject ???
				}
				{
					new MenuItem(editMenu, SWT.SEPARATOR);
				}
				{
					MenuItem findMenuItem = new MenuItem(editMenu, SWT.PUSH);
					findMenuItem.setText("&Find...\tCtrl+F");
					findMenuItem.setAccelerator(SWT.CTRL+'F');
					findMenuItem.setEnabled(false);
					//TODO: context sensitive: if default screen - find anywhere in GN model,
					//if Function editor - find in function only?
					//if XML Source - find in source only?
				}
//				{
//					MenuItem findNextMenuItem = new MenuItem(editMenu, SWT.PUSH);
//					findNextMenuItem.setText("Find &Next\tF3");
//					findNextMenuItem.setAccelerator(SWT.F3);
//					findNextMenuItem.setEnabled(false);
//					//TODO:...
//				}
				//TODO: Replace, ...
				{
					new MenuItem(editMenu, SWT.SEPARATOR);
				}
				{
					final MenuItem snapToGridMenuItem = new MenuItem(editMenu, SWT.CHECK);
					snapToGridMenuItem.setText("Snap to &Grid");
					snapToGridMenuItem.setSelection(true);
					snapToGridMenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								// TODO: this cannot be easily moved to controller, because it changes a menuitem...
								// why not? pass the menuitem or better - bind menuitem's visibility to the boolean property
								GnView composite = view.getCurrentGnView(); 
								boolean snapToGrid = snapToGridMenuItem.getSelection();
								composite.setSnapToGrid(snapToGrid);
								snapToGridMenuItem.setSelection(snapToGrid);
							}
						});
				}
				editMenuItem.setMenu(editMenu);
			}
		}
		if(!DemoMain.inDemoMode)//remove this line
		{
			MenuItem gnMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
			gnMenuItem.setText("&GN");
			{
				Menu gnMenu = new Menu(gnMenuItem);
				gnMenuItem.setMenu(gnMenu);
				{
					MenuItem insertTransitionMenuItem = constructMenuItem(gnMenu,
							"Insert a &Transition\tCtrl+T", SWT.CTRL + 'T', InsertTransitionEvent.class);
					insertTransitionMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH+"transition.gif"));
				}
				{
					MenuItem insertInputMenuItem = constructMenuItem(gnMenu,
							"Insert an &Input Place\tCtrl+Shift+I", SWT.CTRL + SWT.SHIFT + 'I', InsertInputPlaceEvent.class);
					insertInputMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH+"place.gif"));
				}
				{
					MenuItem insertInputMenuItem = constructMenuItem(gnMenu,
							"Insert an &Output Place\tCtrl+Shift+O", SWT.CTRL + SWT.SHIFT + 'O', InsertOutputPlaceEvent.class);
					insertInputMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH+"place.gif"));
				}
				{
					MenuItem insertTransitionAndPlacesMenuItem= new MenuItem(gnMenu, SWT.PUSH);
					insertTransitionAndPlacesMenuItem.setText("Insert a Transition &and Places");
					insertTransitionAndPlacesMenuItem.setEnabled(false);
					insertTransitionAndPlacesMenuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							//...
						}
					});
				}
				{
					MenuItem insertTokenMenuItem = new MenuItem(gnMenu, SWT.PUSH);
					insertTokenMenuItem.setText("Insert a To&ken\tCtrl+Shift+T");
					insertTokenMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "token.png"));
					insertTokenMenuItem.setAccelerator(SWT.CTRL + SWT.SHIFT + 'T');
					insertTokenMenuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							EditGnController.insertToken(view);
						}
					});
				}
				{
					MenuItem insertTokenGeneratorMenuItem = new MenuItem(gnMenu, SWT.PUSH);
					insertTokenGeneratorMenuItem.setText("Insert a Token &Generator");
					insertTokenGeneratorMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "token_generator.gif"));
					insertTokenGeneratorMenuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							EditGnController.insertTokenGenerator(view);
						}
					});
				}
				{
					MenuItem insertCharMenuItem = new MenuItem(gnMenu, SWT.PUSH);
					insertCharMenuItem.setText("Insert a &Characteristic\tCtrl+Shift+C");
					insertCharMenuItem.setAccelerator(SWT.CTRL + SWT.SHIFT + 'C');
					insertCharMenuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							EditGnController.insertCharacteristic(view);
						}
					});
				}
				{
					MenuItem insertFunctionMenuItem = new MenuItem(gnMenu, SWT.PUSH);
					insertFunctionMenuItem.setText("Insert a &Function\tCtrl+Shift+F");
					insertFunctionMenuItem.setAccelerator(SWT.CTRL + SWT.SHIFT + 'F');
					insertFunctionMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH+"function.png"));
					insertFunctionMenuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							EditGnController.insertFunction(view);
						}
					});
				}
				{
					MenuItem drawArcsMenuItem = new MenuItem(gnMenu, SWT.PUSH);
					drawArcsMenuItem.setText("Draw All &Missing Arcs");
					drawArcsMenuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							EditGnController.drawArcs(view);
						}
					});
				}
				{
					new MenuItem(gnMenu, SWT.SEPARATOR);
				}
				{
					MenuItem deleteMenuItem = new MenuItem(gnMenu, SWT.PUSH);
					deleteMenuItem.setText("&Delete Selected Object\tDel");
					deleteMenuItem.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							EditGnController.deleteGnObject(view);
						}
					});
				}
				{
					new MenuItem(gnMenu, SWT.SEPARATOR);
				}
				{
					constructMenuItem(gnMenu, "Check for &Errors\tF11", SWT.F11, CheckGnForErrorsEvent.class);
				}
				{
					new MenuItem(gnMenu, SWT.SEPARATOR);
				}
				{
					MenuItem operationsMenuItem = new MenuItem(gnMenu, SWT.CASCADE);
					operationsMenuItem.setText("Operations And Relations");
					operationsMenuItem.setEnabled(false);
					{
						Menu operationsMenu = new Menu(operationsMenuItem);
						operationsMenuItem.setMenu(operationsMenu);
						{
							constructMenuItem(operationsMenu, "&Union Selected Transitions", 0, 
									UnionTransitionsEvent.class);
							
							constructMenuItem(operationsMenu, "&Intersect Selected Transitions", 0, 
									IntersectTransitionsEvent.class);

							// is that name correct? how to allow the user to reverse the position of transitions?
							constructMenuItem(operationsMenu, "&Subtract Selected Transitions", 0, 
									SubtractTransitionsEvent.class);

							// TODO composition...
							
							new MenuItem(operationsMenu, SWT.SEPARATOR);
							
							constructMenuItem(operationsMenu, "Compare GN With...", 0, 
									CompareGnsEvent.class);
							
							constructMenuItem(operationsMenu, "Union GN With...", 0,
									UnionGnsEvent.class);
							
							constructMenuItem(operationsMenu, "Intersect GN With...", 0,
									IntersectGnsEvent.class);
							
							// is that name correct?
							constructMenuItem(operationsMenu, "Subtract GN With...", 0,
									SubtractGnsEvent.class);
							
						}
					}
					MenuItem operatorsMenuItem = new MenuItem(gnMenu, SWT.CASCADE);
					operatorsMenuItem.setText("Apply Ope&rator");
					{
						Menu operatorsMenu = new Menu(operatorsMenuItem);
						operatorsMenuItem.setMenu(operatorsMenu);
						{
							constructMenuItem(operatorsMenu, "G&2'", 0, 
									ApplyG2PrimOperatorEvent.class);
							
							constructMenuItem(operatorsMenu, "G&6", 0, 
									ApplyG6OperatorEvent.class);
							
							// TODO more operators
						}
					}
				}
			}
		}				
		{
			MenuItem viewMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
			viewMenuItem.setText("&View");
			{
				Menu viewMenu = new Menu(viewMenuItem);
				{
					MenuItem zoomInMenuItem = new MenuItem(viewMenu, SWT.PUSH);
					zoomInMenuItem.setText("Zoom &In\tNumeric +");
					zoomInMenuItem.setAccelerator(SWT.KEYPAD_ADD);
					zoomInMenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								GnDocument document = view.getDocument(); 
								if (document != null) {
									GnView composite = view.getCurrentGnView(); 
									int currentZoom = composite.getZoom(); 
									composite.setZoom(currentZoom + 10);
								}
							}
						});
				}
				{
					MenuItem zoomOutMenuItem = new MenuItem(viewMenu, SWT.PUSH);
					zoomOutMenuItem.setText("Zoom &Out\tNumeric -");
					zoomOutMenuItem.setAccelerator(SWT.KEYPAD_SUBTRACT);
					zoomOutMenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								GnDocument document = view.getDocument(); 
								if (document != null) {
									GnView composite = view.getCurrentGnView(); 
									int currentZoom = composite.getZoom(); 
									composite.setZoom(currentZoom - 10);
								}
							}
						});
				}
				{
					MenuItem zoom100MenuItem = new MenuItem(viewMenu, SWT.PUSH);
					zoom100MenuItem.setText("Original &Size (100%)"); 
					zoom100MenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								GnDocument document = view.getDocument(); 
								if (document != null) {
									GnView composite = view.getCurrentGnView(); 
									composite.setZoom(100);
								}
							}
						});
				}
				{
					MenuItem zoomFitMenuItem = new MenuItem(viewMenu, SWT.PUSH);
					zoomFitMenuItem.setText("&Fit in Window");
					zoomFitMenuItem.setEnabled(false);
					//TODO: ...
				}
				{
					new MenuItem(viewMenu, SWT.SEPARATOR);
				}
				{
					final MenuItem showGridMenuItem = new MenuItem(viewMenu, SWT.CHECK);
					showGridMenuItem.setText("Show Dot &Grid");
					showGridMenuItem.setSelection(true);
					showGridMenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								GnView composite = view.getCurrentGnView(); 
								boolean showGrid = showGridMenuItem.getSelection();
								composite.setShowGrid(showGrid);
								showGridMenuItem.setSelection(showGrid);
							}
						});
				}

				
				{
					new MenuItem(viewMenu, SWT.SEPARATOR);
				}
				if(!DemoMain.inDemoMode)//remove this line
				{
					MenuItem viewSourceMenuItem = new MenuItem(viewMenu, SWT.PUSH);
					viewSourceMenuItem.setText("&XML Source"); 
					viewSourceMenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								MainController.viewXmlSource(view);
							}
						});
				}
				if(!DemoMain.inDemoMode)//remove this line
				{
					MenuItem openFunctionEditorMenuItem = new MenuItem(viewMenu, SWT.PUSH);
					openFunctionEditorMenuItem.setText("Function &Editor"); 
					openFunctionEditorMenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								MainController.openFunctionEditor(view);
							}
						});
				}
				if(!DemoMain.inDemoMode)//remove this line
				{
					new MenuItem(viewMenu, SWT.SEPARATOR);
				}
				{
					MenuItem refreshMenuItem = new MenuItem(viewMenu, SWT.PUSH);
					refreshMenuItem.setText("&Refresh\tF5");
					refreshMenuItem.setAccelerator(SWT.F5);
					refreshMenuItem
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								GnDocument document = view.getDocument();
								if (document != null)
									document.updateViews();//?RefreshCommand?
							}
						});
				}
				viewMenuItem.setMenu(viewMenu);
			}
			{
				MenuItem simulateMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
				simulateMenuItem.setText("&Simulate");
				{
					Menu simulateMenu = new Menu(simulateMenuItem);
					{
						MenuItem startRealSimulationMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						startRealSimulationMenuItem.setText("Start &Real Simulation\tF9");
						startRealSimulationMenuItem.setAccelerator(SWT.F9);
						startRealSimulationMenuItem
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									if (DemoMain.inDemoMode)
										SimulationController.simulationStep(view);
									else
									SimulationController.startRealSimulation(view);
								}
							});
					}
					if(!DemoMain.inDemoMode)//remove this line
					{
						MenuItem goMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						goMenuItem.setText("&Go\tF8");
						goMenuItem.setAccelerator(SWT.F8);
						goMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "run.png"));
						goMenuItem
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									SimulationController.simulationGo(view);
								}
							});
					}
					{
						MenuItem stepMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						stepMenuItem.setText("&Step\tF7");
						stepMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "step.png"));
						stepMenuItem.setAccelerator(SWT.F7);
						stepMenuItem
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									SimulationController.simulationStep(view);
								}
							});
					}
					{
						MenuItem stepMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						stepMenuItem.setText("Step &Until Event..."); //Step &Until Specific Event?
						stepMenuItem.setEnabled(false);
						stepMenuItem
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									SimulationController.simulationStepUntil(view);
								}
							});
					}
					//TODO: STEP n, {TOKENS, SAVE??? - not implemented in server}
					{
						MenuItem pauseMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						pauseMenuItem.setText("Pause/Resume");
						pauseMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "pause.png"));
						pauseMenuItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								SimulationController.simulationPauseResume(view);
							}
						});
					}
					{
						MenuItem stopMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						stopMenuItem.setText("Sto&p");
						stopMenuItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "stop.png"));
						stopMenuItem
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									SimulationController.simulationStop(view);
								}
							});
					}
					{
						new MenuItem(simulateMenu, SWT.SEPARATOR);
					}
					{
						MenuItem startRecordedMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						startRecordedMenuItem.setText("&Open Recorded Simulation...");
						if(!DemoMain.inDemoMode) {
						startRecordedMenuItem
							.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									SimulationController.startRecordedSimulation(view);
								}
							});
						} else {
							startRecordedMenuItem.setEnabled(false);
						}
					}
					{
						MenuItem startRecordingMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						startRecordingMenuItem.setText("Start Recording...");
						startRecordingMenuItem.setEnabled(false);
						//TODO: select type - XML with events / GIF with animation
						//TODO: make submenu: Record as XML; Record as GIF animation; etc.
						//enter filename
					}
					{
						MenuItem stopRecordingMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						stopRecordingMenuItem.setText("Stop Recording");
						stopRecordingMenuItem.setEnabled(false);
						//TODO: poneje mojem da puskame paralelni zapisvaniq, kak 6te razgrani4avame koq da sprem?
						//MessageBox with the filename?
					}
					{
						new MenuItem(simulateMenu, SWT.SEPARATOR);
					}
					{
						final MenuItem delayMenuItem = new MenuItem(simulateMenu, SWT.CHECK);
						delayMenuItem.setText("Delay");
						delayMenuItem.setSelection(true);
						delayMenuItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								super.widgetSelected(arg0);
								//if(delayMenuItem.getSelection())
									//TODO: simulation.setDelay() - cannot be set before running simulation
							}
						});
					}
					{
						new MenuItem(simulateMenu, SWT.SEPARATOR);
					}
					//TODO: View History & Statistics
					{
						MenuItem addWatchMenuItem = new MenuItem(simulateMenu, SWT.PUSH);
						addWatchMenuItem.setText("Add Watch...\tCtrl+Shift+W");
						addWatchMenuItem.setAccelerator(SWT.CTRL + SWT.SHIFT + 'W');
						addWatchMenuItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								SimulationController.addWatch(view);
							}
						});
					}
					simulateMenuItem.setMenu(simulateMenu);
				}
				if(!DemoMain.inDemoMode)//remove this line
				{
					pluginsMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
					pluginsMenuItem.setText("&Plug-ins");
					Menu pluginsMenu = new Menu(pluginsMenuItem);
					pluginsMenuItem.setMenu(pluginsMenu);
				}
				if(!DemoMain.inDemoMode)//remove this line
				{
					Menu toolsMenu = constructMenu(mainMenu, "&Tools");
					{
						constructMenuItem(toolsMenu, "&Settings...", 0, SettingsEvent.class);
					}
				}
				if(!DemoMain.inDemoMode)//remove this line
				{
					MenuItem helpMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
					helpMenuItem.setText("&Help");
					{
						Menu helpMenu = new Menu(helpMenuItem);
						{
							// FIXME on click - exception; do something better
							constructMenuItem(helpMenu, "Drag an element to move it", 0, null);
							constructMenuItem(helpMenu, "Drag or Shift+drag to draw", 0, null);
						}
						{
							MenuItem webMenuItem = new MenuItem(helpMenu, SWT.PUSH);
							webMenuItem.setText("&www.ifigenia.org");
							webMenuItem.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									//TODO: open link...
								}
							});
						}
						{
							new MenuItem(helpMenu, SWT.SEPARATOR);
						}
						{
							MenuItem aboutMenuItem = new MenuItem(helpMenu, SWT.PUSH);
							aboutMenuItem.setText("&About");
							aboutMenuItem.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e)
									{
										AboutDialog dialog = new AboutDialog(
													view.getUIComponent()
															.getShell(),
													SWT.APPLICATION_MODAL
															| SWT.DIALOG_TRIM);
										dialog.setText(Constants.APPLICATION_NAME);
										dialog.open();
									}
								});
						}
						helpMenuItem.setMenu(helpMenu);
					}
				}
			}
		}
		return mainMenu;
	}

	@Override
	protected void syncUpdate(BaseObservable observable, Object arg) {
		// TODO enable/disable some items; type undo/redo descriptions; zoom level; checked items...
	}

	/**
	 * Gets the "Plug-ins" menu. This is an extension point for plug-ins - they
	 * can attach menu items here.
	 * 
	 * @return The "Plug-ins" menu.
	 */
	public MenuItem getPluginsMenu() {
		return pluginsMenuItem;
	}
}
