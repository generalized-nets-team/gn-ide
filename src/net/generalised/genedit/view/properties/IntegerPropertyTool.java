package net.generalised.genedit.view.properties;

import net.generalised.genedit.model.common.IntegerInf;

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

//TODO: may extend StringPropertyTool 
public class IntegerPropertyTool extends PropertyTool {

	private String string;
	private boolean infinity;
	
	public IntegerPropertyTool(TableItem tableItem, Listener listener, boolean infinity) {
		super(tableItem.getParent(), listener);
		this.string = tableItem.getText(1);
		this.infinity = infinity;
	}
	
	public IntegerPropertyTool(Composite parent, String string, Listener listener, boolean infinity) {
		super(parent, listener);
		this.string = string;
		this.infinity = infinity;
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
					try {
						if (infinity) {
							if (text.getText().toLowerCase().startsWith("inf"))
								event.data = IntegerInf.POSITIVE_INFINITY;
							else {
								int number = Integer.parseInt(text.getText());
								event.data = new IntegerInf(number);
							}
						} else {
							event.data = new Integer(text.getText());
						}
						listener.handleEvent(event);
					} catch (NumberFormatException ex) {
					}
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
