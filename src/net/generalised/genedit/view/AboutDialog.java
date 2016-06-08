package net.generalised.genedit.view;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;


public class AboutDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	static private Button buttonOK;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		AboutDialog inst = new AboutDialog(shell, SWT.NULL);
		inst.open();
	}

	public AboutDialog(Shell parent, int style) {
		super(parent, style);
	}

	public void open() {
		Shell parent = getParent();
		dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		dialogShell.setLayout(new GridLayout());
		{//by Mitex
			buttonOK = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonOK.setText("OK");
			buttonOK.setSize(60, 30);
			buttonOK.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					dialogShell.close();
				}
			});
		}
		dialogShell.layout();
		dialogShell.pack();
		dialogShell.open();
		Display display = dialogShell.getDisplay();
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
}
