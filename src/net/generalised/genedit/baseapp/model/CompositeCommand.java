package net.generalised.genedit.baseapp.model;

import java.util.Collections;
import java.util.List;

/**
 * @author Dimitar Dimitrov
 *
 */
public abstract class CompositeCommand extends Command {

	private List<Command> subCommands;
	
	public CompositeCommand(String description) {
		super(description);
		this.subCommands = null;
	}

	public CompositeCommand() {
		this("");
	}

	@Override
	public final void execute() {
		if (subCommands == null) {
			subCommands = fillSubCommands();
			if (subCommands == null) {
				throw new RuntimeException("invalid transaction command: method fillSubCommands should return a list, which can be empty");
			}
		}
		int index = 0;
		try {
			while (index < subCommands.size()) {
				subCommands.get(index).execute();
				++index;
			}
		} finally {
			if (index < subCommands.size()) {
				while (--index >= 0) {
					subCommands.get(index).unExecute();
					// TODO: this can cause exception too
					// XXX: 1st command moje da e omazala ne6to, neq nqma kak da undo-vame! taka 4e nqma smisyl ot tiq gluposti
				}
			}
		}
	}

	@Override
	public final void unExecute() {
		int index = subCommands.size() - 1;
		try {
			while(index >= 0) {
				subCommands.get(index).unExecute();
				--index;
			}
		} finally {
			if (index >= 0) {
				while (++index < subCommands.size()) {
					subCommands.get(index).execute();
					// TODO: this can cause exception too
				}
			}
		}
	}

	protected abstract List<Command> fillSubCommands();
	
	/**
	 * @return the firstRun
	 */
	public boolean isFirstRun() {
		return subCommands == null;
	}

	/**
	 * @return the subCommands
	 */
	public List<Command> getSubCommands() {
		return Collections.unmodifiableList(subCommands);
	}

}
