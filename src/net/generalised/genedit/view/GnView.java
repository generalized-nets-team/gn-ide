package net.generalised.genedit.view;

import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.errors.Problem;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.view.graphical.GraphicalView;
import net.generalised.genedit.view.properties.PropertiesView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import com.cloudgarden.resource.SWTResourceManager;

public class GnView extends BaseView {
	private SashForm sashForm;
	private SashForm sashFormLeft;
	private GraphicalView graphicalView;
	private TreeView treeView;
	private PropertiesView propertiesView;
	private RunScriptView runScriptView;
	private Composite compositeProblems;
	private CTabFolder cTabFolder;
	private CTabItem cTabItemProblems;
	private CTabItem cTabItemProperties;
	private CTabItem cTabItemRunScript;

	private final GeneralizedNet gn;
	
	private final Selection selection;
	
	public GeneralizedNet getGn() {
		return gn;
	}

//	/**
//	* Auto-generated main method to display this 
//	* org.eclipse.swt.widgets.Composite inside a new Shell.
//	*/
//	public static void main(String[] args) {
//		showGUI();
//	}
//		
//	/**
//	* Auto-generated method to display this 
//	* org.eclipse.swt.widgets.Composite inside a new Shell.
//	*/
//	public static void showGUI() {
//		Display display = Display.getDefault();
//		Shell shell = new Shell(display);
//		GnViewComposite inst = new GnViewComposite(shell, SWT.NULL);
//		Point size = inst.getSize();
//		shell.setLayout(new FillLayout());
//		shell.layout();
//		if(size.x == 0 && size.y == 0) {
//			inst.pack();
//			shell.pack();
//		} else {
//			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
//			shell.setSize(shellBounds.width, shellBounds.height);
//		}
//		shell.open();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch())
//				display.sleep();
//		}
//	}

	public GnView(BaseView parent, GeneralizedNet gn) {
		super(parent);
		this.gn = gn;
		GnDocument document = gn.getParent(GnDocument.class);
		this.selection = new Selection(document);
	}

	@Override
	protected Composite createUIComponent(Widget parentComponent) {
		Composite composite = new Composite((Composite) parentComponent, SWT.NONE);
		composite.setLayout(new FillLayout());
		{
			sashForm = new SashForm(composite, SWT.HORIZONTAL);
			{
				sashFormLeft = new SashForm(sashForm, SWT.VERTICAL);
				{
					graphicalView = new GraphicalView(this, gn); 
					graphicalView.initUIComponent(sashFormLeft);
				}
				{
					cTabFolder = new CTabFolder(sashFormLeft, SWT.BOTTOM);
					{
						cTabItemProperties = new CTabItem(cTabFolder, SWT.NONE);
						cTabItemProperties.setText("Properties");
						cTabItemProperties.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "properties.gif"));
						propertiesView = new PropertiesView(this, gn);
						propertiesView.initUIComponent(cTabFolder);
						cTabItemProperties.setControl((Control) propertiesView.getUIComponent());
					}
					{
						cTabItemProblems = new CTabItem(cTabFolder, SWT.NONE);
						cTabItemProblems.setText("Problems");
						cTabItemProblems.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "error.png"));
						compositeProblems = new ProblemsComposite(cTabFolder, SWT.NONE);
						cTabItemProblems.setControl(compositeProblems);
					}
					{
						cTabItemRunScript = new CTabItem(cTabFolder, SWT.NONE);
						cTabItemRunScript.setText("Run JavaScript");
						cTabItemRunScript.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "function.png"));
						runScriptView = new RunScriptView(this, gn);
						runScriptView.initUIComponent(cTabFolder);
						cTabItemRunScript.setControl((Control) runScriptView.getUIComponent());
					}
					cTabFolder.setSelection(0);
				}
				sashFormLeft.setWeights(new int[]{3,1});
			}
			{
				treeView = new TreeView(this, gn);
				treeView.initUIComponent(sashForm);
			}
			sashForm.setWeights(new int[]{3, 1});
		}
		composite.layout();
		return composite;
	}

	/**
	 * @return the selection
	 */
	public Selection getSelection() {
		return selection;
	}

	public int getZoom() {
		return ((GraphicalView)graphicalView).getZoom();
	}
	
	public void setZoom(int zoom) {
		((GraphicalView)graphicalView).setZoom(zoom);
	}
	
	public boolean isShowGrid() {
		return ((GraphicalView)graphicalView).isShowGrid();
	}
	
	public void setShowGrid(boolean showGrid) {
		((GraphicalView)graphicalView).setShowGrid(showGrid);
	}

	public boolean isSnapToGrid() {
		return ((GraphicalView)graphicalView).isSnapToGrid();
	}
	
	public void setSnapToGrid(boolean snapToGrid) {
		((GraphicalView)graphicalView).setSnapToGrid(snapToGrid);
	}
	
	public void addProblem(Problem problem) {
		((ProblemsComposite)compositeProblems).add(problem);
		cTabFolder.setSelection(cTabItemProblems);
		//TODO: set title Problems (5) - it's very useful  
	}

	public void clearProblems() {
		((ProblemsComposite)compositeProblems).clear();
		cTabFolder.setSelection(cTabItemProperties);
	}
}
