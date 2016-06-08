package net.generalised.genedit.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.generalised.genedit.baseapp.SettingsManager;
import net.generalised.genedit.baseapp.controller.Controller;
import net.generalised.genedit.baseapp.model.ParseException;
import net.generalised.genedit.fileimport.controller.ImportController.LoadGnEvent;
import net.generalised.genedit.main.MainConfigProperties;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.dataaccess.GnParseException;
import net.generalised.genedit.model.dataaccess.xmlwrite.GnXmlWriter;
import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.simulation.model.SimulationException;
import net.generalised.genedit.view.Constants;
import net.generalised.genedit.view.FunctionViewComposite;
import net.generalised.genedit.view.GnView;
import net.generalised.genedit.view.MainForm;
import net.generalised.genedit.view.TreeView;
import net.generalised.genedit.view.MainMenu.CheckGnForErrorsEvent;
import net.generalised.genedit.view.MainMenu.CloseFileEvent;
import net.generalised.genedit.view.MainMenu.ExitEvent;
import net.generalised.genedit.view.MainMenu.ExportToBitmapEvent;
import net.generalised.genedit.view.MainMenu.NewFileEvent;
import net.generalised.genedit.view.MainMenu.OpenFileEvent;
import net.generalised.genedit.view.MainMenu.ReloadFileEvent;
import net.generalised.genedit.view.MainMenu.SaveAsFileEvent;
import net.generalised.genedit.view.MainMenu.SaveFileEvent;
import net.generalised.genedit.view.MainMenu.SettingsEvent;
import net.generalised.genedit.view.TreeView.TreeDoubleClickEvent;
import net.generalised.genedit.view.properties.PropertiesView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;


/**
 * Controller class for file operations.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class MainController {
	
	/**
	 * Opens a new tab in the main window.
	 * 
	 * @param view the main form of the application
	 * @param gn the GN model that has to be open
	 */
	//TODO: different tabs need to be supported
	public static void openTab(final MainForm view, final GeneralizedNet gn) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {

				final CTabFolder cTabFolder = view.getCTabFolder();
				
				CTabItem cTabItem = new CTabItem(cTabFolder, SWT.NONE);
				cTabItem.setData(gn);
				cTabItem.setText(gn.getName());//TODO: duplicate code!
				//cTabItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH+"gn.gif"));
		
				final GnView gnView = new GnView(view, gn);
				gnView.initUIComponent(cTabFolder);
				cTabItem.setControl((Composite) gnView.getUIComponent());
		
				cTabFolder.setSelection(cTabFolder.getItemCount() - 1);
				
				view.getUIComponent().getShell().layout();
			}
		});

	}
	
	// TODO Can't we omit the 2nd argument? But how to ensure that event.source is always MainForm?
	@Controller
	public void openNewFile(NewFileEvent event, MainForm view) {
		//GeneralizedNet gn = GeneralizedNet.generateMinimalValidGn();
		String name = SettingsManager.getInstance().getProperty(MainConfigProperties.NEW_GN_NAME);
		GeneralizedNet gn = new GeneralizedNet(name);
		openNewGn(view, gn);
	}
	
	@Controller
	public boolean closeCurrentDocument(CloseFileEvent event, MainForm view) {
		return closeCurrentDocument(view);
	}
	
	private boolean closeCurrentDocument(MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			
			if (document.isModified()) {
				MessageBox box = new MessageBox(view.getUIComponent().getShell(),
						SWT.YES + SWT.NO + SWT.CANCEL + SWT.ICON_WARNING);
				box.setText(Constants.APPLICATION_NAME);
				box.setMessage("Save changes to current file?");
				int choice = box.open();
				if (choice == SWT.CANCEL)
					return false;
				if (choice == SWT.YES) {
					if (document.isInSimulationMode())
						try {
							document.getSimulation().stop();
						} catch (SimulationException e) {
							e.printStackTrace();
						}
					if (saveFile(view) == false)
						return false;
				}
			}
			
			document.deleteObservers();//TODO: BaseViews automatically remove themselves, but on dispose
			int count = view.getCTabFolder().getItemCount();
			for (int i = 0; i < count; ++i) {
				CTabItem currentTab = view.getCTabFolder().getItem(0);
				Control c = currentTab.getControl();
				currentTab.setControl(null);
				c.setVisible(false);//c.dispose();?
				currentTab.dispose();
			}
			document.close();
			view.setDocument(null);
			view.getUIComponent().getShell().setText(Constants.APPLICATION_NAME); //? isn't it automatic?
		}
		return true;
	}
	
	private static final String[] XGN_FILTER_EXTENSIONS = {"*.xml", "*.*"};
	
	@Controller
	public boolean openFile(OpenFileEvent event, MainForm view) {
		File file = event.getFile();
		openFile(view, file != null ? file.getAbsolutePath() : null);
		return true;
	}
	
	/**
	 * Opens a new GN model. Since the application has single document interface, current document is closed.
	 * 
	 * @param view the main form
	 * @param fileName the file that has to be open. If this is null, a {@link FileDialog} appears.
	 */
	private void openFile(final MainForm view, String fileName) {
		if (closeCurrentDocument(view) == false) return;
		if (fileName == null) {
			// TODO: to open more than one document - open new instance (is parallel simulation possible?)
			FileDialog fileDialog = new FileDialog(view.getUIComponent().getShell(), SWT.OPEN);
			fileDialog.setText("Open Generalized Net");
			fileDialog.setFilterExtensions(XGN_FILTER_EXTENSIONS);
			fileName = fileDialog.open();
		}
		final String fName = fileName;
		if (fName != null) { // this is not if-else!!!
			//even fileDialog.open() allows to type a name of nonexistent file!

			//TODO: opened xml can be gn model or a snapshot
			//  (model with saved state or simply current state (then display a warning)) 
			//  - 3 kinds of files!

			Thread openThread = new Thread(new Runnable() {
				public void run() {
					try {
						GnDocument document = new GnDocument(fName, false);
						view.setDocument(document);
						openTab(view, document.getModel());
//						Display.getDefault().syncExec(new Runnable() {
//							public void run() {
//								ViewUtil.checkErrorsWithoutMessage(view);
//							}
//						});
					} catch (IOException e) {
						e.printStackTrace();
						ViewUtil.showErrorMessageBox("Error opening file \"" + fName + "\"");
					} catch (ParseException e) { //TODO: there are IllegalArgumentException's too...
						e.printStackTrace();
						ViewUtil.showErrorMessageBox("\"" + fName +
								"\" is not a valid GeneralizedNet model, or uses newer version of GN Schema.\n\n"
								+ e.getMessage());
					}
				}});
			openThread.start();
		}
	}
	
	public void openNewGn(MainForm view, GeneralizedNet gn) {
		if (closeCurrentDocument(view) == false)
			return;
		GnDocument document = new GnDocument(gn, false);
		view.setDocument(document);
		openTab(view, document.getModel());
	}
	
	@Controller
	public void openNewGn(LoadGnEvent event, MainForm view) {
		openNewGn(view, event.getGn());
	}
	
	@Controller
	public boolean saveFile(SaveFileEvent event, MainForm view) {
		return saveFile(view);
	}

	private boolean saveFile(MainForm view) {
		GnDocument document = view.getDocument();
		if (document != null) {
			//TODO: if no transitions and places - cannot save! make new XSD

			//This should be disabled during simulation, because the original model will be overwritten!
			if (document.isInSimulationMode()) {
				ViewUtil.showErrorMessageBox("Cannot save GN model during simulation.\nThis version cannot save the current state of the model, but you can experiment with Save As.");
				return false;
			}
			
			if (document.getFileName().length() == 0) {
				return saveAsFile(view);
			}
			else {
				saveFile(document);
			}
		}
		return true;		
	}
	
	@Controller
	public boolean saveAsFile(SaveAsFileEvent event, MainForm view) {
		return saveAsFile(view);
	}
	
	/**
	 * Saves current GN model in a new XML file.
	 * 
	 * @param view the main form of the application
	 * @return false iff the user does not select a file name
	 */
	private boolean saveAsFile(MainForm view) {
		GnDocument document = view.getDocument();
		if (document != null) {
			//During simulation this will save current state, not the model!
			//if (in simulation mode) MessageBox...

			FileDialog fileDialog = new FileDialog(view.getUIComponent().getShell(), SWT.SAVE);
			fileDialog.setText("Save Generalized Net As");
			fileDialog.setFilterExtensions(XGN_FILTER_EXTENSIONS);
			//TODO: propose file name - escape(gn.name)+".xml"
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				//TODO: ask for replace confirmation!
				//TODO: imeto moje da e relative???
				document.setFileName(selectedFile);
				saveFile(document);
			} else
				return false;
		}
		return true;
	}

	private static void saveFile(final GnDocument doc) {
		// FIXME: use the thread; fix the bugs: on close file, canvas.redraw is called after canvas is disposed
//		Thread saveThread = new Thread(new Runnable() {
//			public void run() {
				try {
					doc.save();
				} catch (IOException e) {
					e.printStackTrace();
					ViewUtil.showErrorMessageBox("Error saving file \"" + doc.getFileName() + "\"");
					// TODO: in previous version here was "return
					// false", but this is not easy achievable in the
					// thread, also this will hang the application?
				}
//			}
//		});
//		saveThread.start();
	}
	
	@Controller
	public void reloadCurrentDocument(ReloadFileEvent event, MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			String fileName = document.getFileName();
			if (fileName.length() > 0) {
				closeCurrentDocument(view);
				openFile(view, fileName);
			}
		}
	}
	
	@Controller
	public void exportToBitmap(ExportToBitmapEvent event, MainForm view) {
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell());
		box.setMessage("To export the model to raster graphic format (such as BMP, PNG, GIF, JPEG),\nmaximize the window to show the whole model,\npress the Print Screen key, and paste into a graphic editor like Paint.");
		box.open();
	}
	
	@Controller
	public void closeApplication(ExitEvent event, MainForm view) {
		if (closeCurrentDocument(view)) {
			Display.getDefault().getActiveShell().close();
		}
	}
	
	/**
	 * Opens a new tab that displays the XML content that will be written if the user selects "Save As"
	 * 
	 * @param view the main form
	 */
	public static void viewXmlSource(MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			CTabFolder cTabFolder = view.getCTabFolder();
			// TODO check if already opened
			
			CTabItem cTabItem = new CTabItem(cTabFolder, SWT.NONE);
			cTabItem.setText("XML Source");
			// TODO: ViewSourceComposite singleton, observer...
			//XML source is common for all GNs!
	
			Text text = new Text(cTabFolder, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			GnXmlWriter parser = GnXmlWriter.getInstance();
			String gnAsString = "(error)";
			try {
				//TODO: why not true? but then the title would be misleading
				gnAsString = parser.gnToXml(view.getDocument().getModel(), false, true);
			} catch (GnParseException e) {
				e.printStackTrace();
			}
			text.setText(gnAsString);
			
			//TODO: Courier in Linux? Mac?
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				text.setFont(SWTResourceManager.getFont("Courier", 0, SWT.NONE)); // does not work on Linux!
			}
			
			cTabItem.setControl(text);
	
			cTabFolder.setSelection(cTabFolder.getItemCount() - 1);
		}
	}

	@Controller
	public static void checkErrors(CheckGnForErrorsEvent event, MainForm view) {
		checkErrors(view);
	}
	
	/**
	 * Calls the error checker for current document. If there are problems
	 * found, they are displayed in the Problems composite.
	 * 
	 * @param view
	 *            main form
	 * @return true, if current document has no errors; warnings are not
	 *         considered as errors.
	 */
	private static boolean checkErrors(MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			boolean hasNoErrors = ViewUtil.checkErrorsWithoutMessage(view);
			
			if (hasNoErrors) {
				MessageBox box = new MessageBox(Display.getDefault()
						.getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
				box.setText(Constants.APPLICATION_NAME);
				box.setMessage("No errors found.");
				box.open();
			}
			
			return hasNoErrors;
		} else
			return true;
	}

	@Controller
	public void onTreeDoubleClick(TreeDoubleClickEvent event, TreeView treeView) {
		MainForm mainForm = treeView.getParent(MainForm.class);
		List<Object> selectedItems = mainForm.getCurrentGnView().getSelection().getObjects();
		if (selectedItems.size() > 0 && selectedItems.get(0) instanceof FunctionReference) {
			FunctionReference function = (FunctionReference) selectedItems.get(0);
			if (function.getFunctionName().length() > 0) {
				openFunctionEditor(mainForm, function);
			} else {
				openFunctionEditor(mainForm, null);
			}
		}
	}

	// XXX
	public static void onPredicatesMatrixDoubleClick(
			PropertiesView propertiesView, FunctionReference function) {
		
		MainForm mainForm = propertiesView.getParent(MainForm.class);
		
		//if (mainForm.getCurrentGnView().getGn().getFunctions().contains(function)) {
			openFunctionEditor(mainForm, function);
		//}
	}
	
	public static void openFunctionEditor(MainForm mainForm) {
		openFunctionEditor(mainForm, null);
	}
	
	private static void openFunctionEditor(MainForm mainForm, FunctionReference function) {
		CTabFolder cTabFolder = mainForm.getCTabFolder();
		CTabItem cTabItem = new CTabItem(cTabFolder, SWT.NONE);
		
		// TODO check if already opened
		
		//cTabItem.setData(function);
		//TODO: name must be in format <function> (<GN>)
		//cTabItem.setText(function.getFunctionName());//TODO: this should be updatable!
		cTabItem.setText("Functions");
		cTabItem.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "function.png"));

		Composite composite = new FunctionViewComposite(cTabFolder,
				SWT.NONE, mainForm.getDocument(), function);
		cTabItem.setControl(composite);

		cTabFolder.setSelection(cTabFolder.getItemCount() - 1);

	}
	
	@Controller
	public void editSettings(SettingsEvent event, MainForm view) {
		try {
			SettingsManager.getInstance().store();
			
			// TODO open text editor tab in main window; buttons: Save, Cancel, Load Default
			
			SettingsManager.getInstance();
			String message = "Open file \"" + SettingsManager.CONFIG_FILE.getAbsolutePath()
					+ "\" and edit it manually in a plain text editor. Then reload the application";
			
			MessageBox box = new MessageBox(view.getUIComponent().getShell(), SWT.OK | SWT.ICON_INFORMATION);
			box.setText(Constants.APPLICATION_NAME);
			box.setMessage(message);
			box.open();

		} catch (IOException e) {
			e.printStackTrace();
			ViewUtil.showExceptionMessageBox(e);
		}
	}
}
