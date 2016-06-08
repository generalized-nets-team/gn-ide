package net.generalised.genedit.view;

import net.generalised.genedit.model.errors.Problem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class ProblemsComposite extends Composite {

	private List problems;
	//TODO: add icons...
	
	public ProblemsComposite(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		this.setLayout(new FillLayout());
		{
			problems = new List(this, SWT.NONE);
		}
		this.layout();
	}
	
	// TODO: On double click - go to problem location
	
	public void add(Problem problem) {
		//TODO: da slaga i timestamp?
		String message;
		if (problem.isError())
			message = "Error: " + problem.getMessage();
		else
			message = "Warning: " + problem.getMessage(); 
		problems.add(message);
	}
	
	public void clear() {
		problems.removeAll();
	}
}
