package net.generalised.genedit.view.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class StringPropertyTool extends PropertyTool {

	private String string;
	
	public StringPropertyTool(TableItem tableItem, Listener listener) {
		super(tableItem.getParent(), listener);
		this.string = tableItem.getText(1);
	}
	
	public StringPropertyTool(Composite parent, String string, Listener listener) {
		super(parent, listener);
		this.string = string;
	}
	
	@Override
	public Control getEditorControl() {
		final Text text = new Text(parent, SWT.NONE);
		text.setText(string);
		text.selectAll();
		text.addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e) {
				text.dispose();
			}
		});
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					
					Event event = new Event();
					event.data = text.getText();
					listener.handleEvent(event);
					
					text.dispose();
				}
				if (e.character == SWT.ESC) {
					text.dispose();
				}
			}
		});
		return text; 
	}

}
