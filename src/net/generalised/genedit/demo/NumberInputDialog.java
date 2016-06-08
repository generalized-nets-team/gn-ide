package net.generalised.genedit.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NumberInputDialog extends Dialog {
	Double[] values;

	/**
	 * @param parent
	 */
	public NumberInputDialog(Shell parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public NumberInputDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return
	 */
	public Double[] open(final String... messages) {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL);
		shell.setText("Моля въведете стойност(и)");

		shell.setLayout(new GridLayout(2, true));

		Label[] labels = new Label[messages.length];
		final Text[] texts = new Text[messages.length];
		values = new Double[messages.length]; 
		
		for (int i = 0; i < messages.length; ++i) {
		
			Label label = new Label(shell, SWT.NULL);
			label.setText(messages[i]);
			labels[i] = label;
	
			final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
			text.setText("");
			texts[i] = text;
		}
		
		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("OK");
		buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		shell.setDefaultButton(buttonOK);
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");

		for (int i = 0; i < messages.length; ++i) {
			final int j = i;
			texts[i].addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event event) {
					try {
						values[j] = new Double(texts[j].getText());
						buttonOK.setEnabled(true);
					} catch (Exception e) {
						buttonOK.setEnabled(false);
					}
				}
			});
			
		}

		buttonOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				for (int i = 0; i < messages.length; ++i) {
					values[i] = null;
				}
				shell.dispose();
			}
		});
		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE)
					event.doit = false;
			}
		});

		shell.pack();
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return values;
	}

	public static void main(String[] args) {
		Shell shell = new Shell();
		NumberInputDialog dialog = new NumberInputDialog(shell);
		System.out.println(dialog.open(new String[]{"test1", "test2"}));
	}
}