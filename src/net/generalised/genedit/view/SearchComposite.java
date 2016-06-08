package net.generalised.genedit.view;

import java.util.List;

import net.generalised.genedit.baseapp.model.BaseModel;
import net.generalised.genedit.baseapp.view.BaseView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Dimitar Dimitrov
 *
 */
public class SearchComposite extends BaseView {

	public SearchComposite(BaseView parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	private List<BaseModel> results;

	@Override
	protected Widget createUIComponent(Widget parent) {
		return new Composite((Composite) parent, SWT.NONE);
	}
	
	/* TODO: when no results - search text box, options:
	 * search in: object ids and friendly names; functions; 
	 * case-sensitive; whole words only
	 * advanced, in future: search fragment - transition with 3 inputs and 1 output, etc.
	 * replace?: this requires sequential search...
	 * 
	 * when results is not empty:
	 * table with search match and type (place id, place reference, function definition, ...)
	 * when clicked, set selection to the clicked item, so it will automatically be selected in graphical view
	 * also button for new search and re-run current search
	 * 
	 */
	
	//TODO: context sensitive: if default screen - find anywhere in GN model,
	//if Function editor - find in function only?
	//if XML Source - find in source only?

}
