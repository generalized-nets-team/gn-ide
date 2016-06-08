package net.generalised.genedit.view;

import java.io.File;

import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.main.Main;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.view.MainMenu.CloseFileEvent;
import net.generalised.genedit.view.MainMenu.OpenFileEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.cloudgarden.resource.SWTResourceManager;

/**
 * The main form of this application.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class MainForm extends BaseView {

	private CTabFolder cTabFolder;
	
	private Menu mainMenu;

	private GnDocument document;

	public MainForm() {
		super(null);
	}
	
	@Override
	public Composite getUIComponent() {
		return (Composite) super.getUIComponent();
	}
	
	@Override
	public Widget createUIComponent(Widget parent) {
		Composite composite = new Composite((Shell) parent, SWT.NULL);
		
		//TODO: insert this in all forms?
		{
			//Register as a resource user - SWTResourceManager will
			//handle the obtaining and disposing of resources
			SWTResourceManager.registerResourceUser(composite);
		}

		Rectangle screenSize = Display.getDefault().getPrimaryMonitor().getBounds();
		composite.setSize((int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8));
		// TODO: this.setBackgroundImage( logo );
		/*GridLayout thisLayout = new GridLayout(1, true);
		thisLayout.marginWidth = 5;
		thisLayout.marginHeight = 5;
		thisLayout.numColumns = 1;
		thisLayout.makeColumnsEqualWidth = true;
		thisLayout.horizontalSpacing = 5;
		thisLayout.verticalSpacing = 5;
		this.setLayout(thisLayout);*/
		composite.setLayout(new FillLayout());
		{
			cTabFolder = new CTabFolder(composite, SWT.CLOSE | SWT.BORDER);
			/*GridData cTabFolderLData = new GridData();
			cTabFolderLData.grabExcessHorizontalSpace = true;
			cTabFolderLData.horizontalAlignment = GridData.FILL;
			cTabFolderLData.grabExcessVerticalSpace = true;
			cTabFolderLData.verticalAlignment = GridData.FILL;
			cTabFolder.setLayoutData(cTabFolderLData);*/
			cTabFolder.setSelection(0);
			cTabFolder.addCTabFolder2Listener(new CTabFolder2Listener(){

				public void close(CTabFolderEvent event) {
					if (document.getModel() == cTabFolder.getSelection().getData()) { //root GN
						event.doit = false;
					}
				}

				public void maximize(CTabFolderEvent event) {
				}

				public void minimize(CTabFolderEvent event) {
				}

				public void restore(CTabFolderEvent event) {
				}

				public void showList(CTabFolderEvent event) {
				}
			});
			composite.getShell().addShellListener(new ShellListener(){

				public void shellActivated(ShellEvent arg0) {
				}

				public void shellClosed(ShellEvent e) {
					e.doit = dispatchEvent(CloseFileEvent.class); 
				}

				public void shellDeactivated(ShellEvent arg0) {
				}

				public void shellDeiconified(ShellEvent arg0) {
				}

				public void shellIconified(ShellEvent arg0) {
				}
				
			});
			DropTarget dropTarget = new DropTarget(composite, 
					DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);//?
			dropTarget.setTransfer(new Transfer[]{FileTransfer.getInstance()});
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
					if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
						String[] fileNames = (String[])event.data;
						String fileName = fileNames[0];
						
						MainForm.this.dispatchEvent(new OpenFileEvent(new File(fileName)));
					}
				}

				public void dropAccept(DropTargetEvent event) {
				}
				
			});
		}
		{
			MainMenu menu = new MainMenu(this);
			menu.initUIComponent(composite);
			mainMenu = (Menu) menu.getUIComponent();
			composite.getShell().setMenuBar(mainMenu);
		}
		//TODO: CoolBar for Simulation
		composite.layout();
		
		return composite;
	}

	public CTabFolder getCTabFolder() {//TODO: change with something more abstract - BaseView
		return cTabFolder;
	}

	public GnDocument getDocument() {
		return document;
	}
	
	public void setDocument(GnDocument document) {
		if (this.document != null) {
			this.document.deleteObserver(this);
		}
		this.document = document;
		if (document != null) {
			this.document.addObserver(this);
		}
		update(this, null);//FIXME - ugly; update cannot be called with null as 1st arg
	}
	
	@Override
	public void syncUpdate(BaseObservable observable, Object argument) {
		//TODO: getCurrent() ? NullPointerException
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String title = "";
				if (document != null) {
					title = computeWindowTitle(document.getModel(), document.getFileName());
					//TODO: if another gn is selected, why not use getCurrentGnView...?
				} else {
					title = Constants.APPLICATION_NAME;
				}
				getUIComponent().getShell().setText(title);
				//TODO: if some GN or function has changed its name - update cTabFolders...
			}
		});
	}

	public GnView getCurrentGnView() {
		Control result = cTabFolder.getSelection().getControl();
		//FIXME too ugly
		for (BaseView child : getChildren()) {
			if (child.getUIComponent().equals(result)) {
				return (GnView) child;
			}
		}
//		if (result instanceof GnView) {
//			return (GnView) result;
//		}
		return null;
	}
	
	/**
	 * Computes the string that will be displayed on the title bar.
	 * It depends on the file name, the application name, etc. 
	 * 
	 * @param gn the currently open GN model
	 * @param fileName the filename where it is saved
	 * @return the new string that will be displayed on the title bar
	 */
	private String computeWindowTitle(GeneralizedNet gn, String fileName) {
		StringBuffer result = new StringBuffer();
		result.append(gn.getName());
		if (fileName != null && fileName.length() > 0) {
			java.io.File file = new java.io.File(fileName);
			result.append(" (");
			result.append(file.getName() + ")");
		}
		if (document.isModified()) {
			result.append('*');
		}
		result.append(" - ");
		result.append(Constants.APPLICATION_NAME);
		return result.toString();
	}

	// Use {@link Main} class instead.
	public static void main(String[] args) {
		Main.main(args);
	}
}
