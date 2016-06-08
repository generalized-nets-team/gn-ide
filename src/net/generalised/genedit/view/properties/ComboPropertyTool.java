package net.generalised.genedit.view.properties;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

public class ComboPropertyTool extends PropertyTool {

	private String string;
	private List<String> items;
	private boolean allowNull;
	
	private final String NEW = "New...";
	private final String NONE = "(None)";
	
	public ComboPropertyTool(TableItem tableItem, Listener listener, List<String> items, boolean allowNull) {
		super(tableItem.getParent(), listener);
		this.string = tableItem.getText(1);
		this.items = items;
		this.allowNull = allowNull;
	}
	
	public ComboPropertyTool(Composite parent, String text, Listener listener, List<String> items, boolean allowNull) {
		super(parent, listener);
		this.string = text;
		this.items = items;
		this.allowNull = allowNull;
	}
	
	protected void execute(Combo combo) {
		Event event = new Event();
		String selected = combo.getItems()[combo.getSelectionIndex()];
		if (selected.equals(NEW))
			selected = "";
		else if (selected.equals(NONE))
			selected = null;
		event.data = selected;
		listener.handleEvent(event);
	}
	
	@Override
	public Control getEditorControl() {
		final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (allowNull)
			combo.add(NONE);
		for (String item : items) {
			combo.add(item);
		}
		combo.add(NEW);
		
		int index = items.indexOf(string);
		if (allowNull)
			++index;
		//if index = -1 -> exception
		combo.select(index);
		
		//combo.addSelectionListener(new ComboSelectionAdapter());
		combo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					execute(combo);
					combo.dispose();
				}
				if (e.character == SWT.ESC) {
					combo.dispose();
				}
			}
		});
		combo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				//execute(combo); //TODO: unfortunately, too many problems with disposed widget
				combo.dispose();
			}
		});

		return combo;
	}
	
//	private class ComboSelectionAdapter extends SelectionAdapter {
//
//		@Override
//		public void widgetDefaultSelected(SelectionEvent e) {
//			// TODO Auto-generated method stub
//			super.widgetDefaultSelected(e);
//		}
//
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			Event event = new Event();
//			event.data = combo.getItems()[combo.getSelectionIndex()];
//			listener.handleEvent(event);
//			//super.widgetSelected(e);
//			combo.dispose();
//		}
//		
//	}

}
