package net.generalised.genedit.baseapp.model;

import java.util.LinkedList;
import java.util.List;

public class CommandManager {

	private final static int DEFAULT_MAX_HISTORY = 100;//TODO: during simulation - unlimited?
	
	private final List<Command> commands;
	
	private int maxLength;
	
	private int currentCommandIndex;
	
	public CommandManager(int maxLength) {
		commands = new LinkedList<Command>();
		currentCommandIndex = 0;
		if (maxLength >= 1) {
			this.maxLength = maxLength;
		} else {
			this.maxLength = 1;
		}
	}
	
	public CommandManager() {
		this(DEFAULT_MAX_HISTORY);
	}
	
	public synchronized void firstExecute(Command command) {
		// TODO: if AutoCompositeCommand - just set a flag / push command to stack(how to determine if nested commands?) then execute (which should run userExecute)
		// if the flag is true - skip the following lines, but add to some list... then execute
		// be careful if nested auto commands!
		while (commands.size() > currentCommandIndex) {
			commands.remove(currentCommandIndex);
		}
		commands.add(command);
		if (commands.size() > maxLength) {
			commands.remove(0);
		}
		currentCommandIndex = commands.size();
		commands.get(currentCommandIndex - 1).execute();
	}
	
	/**
	 * @param levels
	 * @return Actual number of undone operations.
	 */
	public synchronized int undo(int levels) {
		int result = 0;
		if (commands.size() > 0) {
			for (result = 0; result < levels && currentCommandIndex > 0; ++result) {
				--currentCommandIndex;
				commands.get(currentCommandIndex).unExecute();
			}
		}
		return result;
	}
	
	/**
	 * @param levels
	 * @return Actual number of redone operations.
	 */
	public synchronized int redo(int levels) {
		int result = 0;
		for (result = 0; result < levels && currentCommandIndex < commands.size(); ++result) {
			commands.get(currentCommandIndex).execute();
			++currentCommandIndex;
		}
		return result;
	}
}
