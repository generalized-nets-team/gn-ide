package net.generalised.genedit.view;

import net.generalised.genedit.baseapp.model.BaseObservable;
import net.generalised.genedit.baseapp.model.BaseObserver;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.FunctionReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.cloudgarden.resource.SWTResourceManager;

public class FunctionViewComposite extends Composite implements BaseObserver {

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private final GnDocument document;
	
	private StyledText text;
	
	private boolean modified;
	
	private final FunctionReference function;
	
	private String originalText;
	
	public FunctionViewComposite(Composite parent, int style, 
			GnDocument document, FunctionReference function) {
		super(parent, style);
		this.document = document;
		this.function = function;
		this.modified = false;
		document.addObserver(this); //TODO: deleteObserver?
		initGUI();
	}

	private void initGUI() {
		//TODO: cannot set the tab title here...
		this.setLayout(new FillLayout());
		{
			// SashForm sashForm = new SashForm(this, SWT.VERTICAL);
			// TODO RunScriptView too
			
			text = new StyledText(this, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			//text.setText(function.getDefinition());
			originalText = document.getModel().getFunctions();
			text.setText(document.getModel().getFunctions());
			// TODO scroll to function if != null
			// TODO Linux?
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				text.setFont(SWTResourceManager.getFont("Courier", 0, SWT.NONE));
			}
			text.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					// TODO should Undo-Redo be different from main undo/redo? no
					document.getModel().setFunctions(text.getText());
					
					modified = true;
				}
			});
		}
		//TODO: same in Close Listener?
		text.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				//TODO: do this often - if Ctrl+S is pressed, changes are lost!
				// TODO above is probably fixed, but if the user presses Ctrl+Z/Y...
				if (modified && !document.isInSimulationMode()) {
					//TODO: if the function was initially empty, the name may not be changed accurately
					
					document.getModel().setFunctions(originalText);
					document.getModel().undoableSet("functions", text.getText());
					originalText = document.getModel().getFunctions();
					modified = false;
				}
			}
			
		});
		this.layout();
	}

	public void update(BaseObservable arg0, Object arg1) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				// TODO if modified this function, ask to reload the code and lose changes
				// most of the time the modification will be made in this composite
				text.setText(document.getModel().getFunctions());
				originalText = document.getModel().getFunctions();
				// TODO scroll to previous position, if available
			}
		});
	}

}
