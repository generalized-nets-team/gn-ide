package net.generalised.genedit.baseapp.model;

// import net.generalised.genedit.demo.DemoMain;

/**
 * @author Dimitar Dimitrov
 *
 */
public class ModelWithHistory extends BaseModel implements BaseObservable {

	private CommandManager commands;
	
	private boolean modified;
	
	private final BaseObservableImpl observableHelper;
	
	public ModelWithHistory() {
		this.commands = new CommandManager();
		this.modified = false;
		this.observableHelper = new BaseObservableImpl();
	}
	
	@Override
	public void execute(Command command) {
		/*
		 * TODO
		 * da proverqva dali target == this; dobre, ama... vsqka li commanda da ima?
		 * 
		 * document?.execute: if command instanceof Transaction { document.pushTransaction(command); if (command.firstRun {command.perform(); spisyk trqbva ve4e da e pylen; firstRun=false} [else] execute; assert popTransaction()==command}
		   else if transactionStack.nonempty add to top.subCommands; else execute    //!!!!!opravi tozi algoritym, super bugav e!
		   notifyObservers...
		 */

		commands.firstExecute(command);

		// modified = !DemoMain.inDemoMode;//true
		
		BaseModel affectedObject = command.getAffectedObject();
		if (affectedObject == null) {
			affectedObject = this;
		}
		//TODO: construct argument for notifyObservers
		//za6to se otkazahme ot affectedObject.notify i vsi4ki nagore... vse pak edni 6te gledat GN, drugi Doc...
		notifyObservers(command);
	}
	
	public void undo(int levels) {
		//be6e: if ! simul...
		int actual = commands.undo(levels);
		if (actual > 0) {
			// this.modified = !DemoMain.inDemoMode;//true
			notifyObservers();//TODO: izmisli kvo da se podava, 4e inak Tree ne se update-va
		}
		//TODO: ako se varnem v sastoqnie, koeto e saved, this.modified = false
	}
	
	public void redo(int levels) {
		//be6e: if ! simul...
		int actual = commands.redo(levels);
		if (actual > 0) {
			// this.modified = !DemoMain.inDemoMode;//true
			notifyObservers();//TODO: izmisli kvo da se podava, 4e inak Tree ne se update-va
		}
	}
	
	public void resetCommandHistory() {
		commands = new CommandManager();
		notifyObservers();//?
	}

	public boolean isModified() {
		return modified;
	}
	
	protected void setModified(boolean modified) {
		this.modified = modified;
	}

	public void addObserver(BaseObserver observer) {
		observableHelper.addObserver(observer);
	}

	public int countObservers() {
		return observableHelper.countObservers();
	}

	public void deleteObserver(BaseObserver observer) {
		observableHelper.deleteObserver(observer);
	}

	public void deleteObservers() {
		observableHelper.deleteObservers();
	}

	public void notifyObservers() {
		observableHelper.notifyObservers();
	}

	public void notifyObservers(Object arg) {
		observableHelper.notifyObservers(arg);
	}

}
