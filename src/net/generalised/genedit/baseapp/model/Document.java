package net.generalised.genedit.baseapp.model;

import java.io.IOException;

/**
 * @author Dimitar Dimitrov
 *
 */
public abstract class Document<T extends BaseModel> extends ModelWithHistory {

	private String fileName;
	
	private final T model;
	
	private boolean readOnly;
	
	/**
	 * Creates a new document for a given model. The document has no file name.
	 * 
	 * @param model must be non-null
	 */
	public Document(T model, boolean readOnly) {
		super();
		
		assert model != null;
		this.model = model;
		model.setParent(this);
		setFileName("");
		this.readOnly = readOnly;
	}
	
	/**
	 * Constructs a new document from a given file.
	 * 
	 * @param fileName
	 *            the file where the model is saved. This file will be parsed and a model will be constructed.
	 * @param readOnly specifies whether the document can be modified
	 * @throws IOException if the file does not exist or there is problem reading it
	 * @throws ParseException if the file is not a valid GN model
	 * @throws IllegalArgumentException if the file name is empty
	 */
	public Document(String fileName, boolean readOnly) throws IOException, ParseException, IllegalArgumentException {
		super();
		
		if (fileName != null && fileName.length() > 0) {
			model = load(fileName);
			model.setParent(this);
		} else {
			throw new IllegalArgumentException("fileName must be non-empty");
		}

		setFileName(fileName);
		this.readOnly = readOnly;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		if (fileName != null) {
			this.fileName = fileName;
		} else {
			this.fileName = "";
		}
		notifyObservers();
	}

	public T getModel() {
		return this.model;
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	/**
	 * Notifies all observers
	 */
	// TODO: remove this method?
	public void updateViews() {
		notifyObservers(new Command() {

			@Override
			public void execute() {
				// nothing
			}

			@Override
			public void unExecute() {
				// nothing
			}});
	}
	
	public abstract void save() throws IOException;
	
	protected abstract T load(String fileName) throws IOException, ParseException;
	
	public abstract void close();
}