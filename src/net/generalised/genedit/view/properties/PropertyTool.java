package net.generalised.genedit.view.properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public abstract class PropertyTool {
	
	protected final Composite parent;

	protected final Listener listener;
	
	public PropertyTool(Composite parent, Listener listener) {
		this.parent = parent;
		this.listener = listener;
	}
	
	public abstract Control getEditorControl();
}
