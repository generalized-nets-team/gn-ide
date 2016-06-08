package net.generalised.genedit.view;

import net.generalised.genedit.baseapp.StringUtil;
import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.simulation.model.embedded.javascript.JavaScriptRunner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Undefined;

import com.cloudgarden.resource.SWTResourceManager;

public class RunScriptView extends BaseView {

	private final GeneralizedNet gn;

	public RunScriptView(BaseView parent, GeneralizedNet gn) {
		super(parent, gn.getParent(GnDocument.class));
		this.gn = gn;
	}

	@Override
	protected Widget createUIComponent(Widget parent) {
		Composite composite = new Composite((Composite) parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setLayout(new FillLayout());
		// TODO set monospaced font in both text views
		final StyledText sourceText = new StyledText(sashForm, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		final Text resultText = new Text(sashForm, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		resultText.setEditable(false);
		sashForm.setWeights(new int[]{2, 1});
		GridData sashData = new GridData();
		sashData.horizontalAlignment = SWT.FILL;
		sashData.verticalAlignment = SWT.FILL;
		sashData.grabExcessHorizontalSpace = true;
		sashData.grabExcessVerticalSpace = true;
		sashForm.setLayoutData(sashData);
		
		final Color successColor = resultText.getForeground().equals(resultText.getBackground()) ? 
				new Color(resultText.getDisplay(), 0, 0, 0) : 
					resultText.getForeground(); // strange bug on 64-bit Linux
		
		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.WRAP | SWT.VERTICAL);
		final ToolItem itemReadOnly = new ToolItem(toolBar, SWT.CHECK);
		itemReadOnly.setText("Read-only");
		itemReadOnly.setEnabled(false);
		itemReadOnly.setSelection(true);
		ToolItem itemRun = new ToolItem(toolBar, SWT.PUSH);
	    itemRun.setText("Run Script");
	    itemRun.setToolTipText("Run the JavaScript code in the upper text area");
	    itemRun.setImage(SWTResourceManager.getImage(Constants.RESOURCES_PATH + "run.png"));
	    itemRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String sourceCode = sourceText.getText();
				if (! StringUtil.isNullOrEmpty(sourceCode)) {
					boolean success = true;
					String resultAsString = "";
					try {
						// TODO make this undoable, if readOnly is false
						boolean readOnly = itemReadOnly.getSelection();
						Object result = JavaScriptRunner.runJavaScript(gn, sourceCode, "", readOnly);
						resultAsString = result != null ? result.toString() : "null";
						if (result instanceof Undefined) {
							success = false;
							resultAsString = "undefined";
						}
					} catch (EcmaError e) {
						resultAsString = "ERROR: " + e.getMessage();
						success = false;
					} catch (EvaluatorException e) {
						success = false;
						resultAsString = "ERROR: " + e.getMessage();
					}
					if (success) {
						resultText.setForeground(successColor);
					} else {
						resultText.setForeground(new Color(null, 0xFF, 0, 0));
					}
					resultText.setText(resultAsString);
				}
			}
		});
		
		sashForm.layout();
		composite.layout();
		return composite;
	}
}
