package net.generalised.genedit.baseapp.model;

import java.util.List;

/**
 * @author Dimitar Dimitrov
 *
 */
public abstract class AutoCompositeCommand extends CompositeCommand {

	/**
	 * @param description
	 */
	public AutoCompositeCommand(String description) {
		super(description);
	}

	/**
	 * 
	 */
	public AutoCompositeCommand() {
		super();
	}

	@Override
	protected List<Command> fillSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	protected abstract void userExecute();
}
