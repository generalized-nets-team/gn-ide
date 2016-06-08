package net.generalised.genedit.model.gn;

import net.generalised.genedit.baseapp.model.BaseModel;
import net.generalised.genedit.baseapp.model.Command;
import net.generalised.genedit.model.common.IndexMatrix;

/**
 * ...
 * 
 * @param <T> ...should be immutable.
 * @author Dimitar Dimitrov
 * 
 */
public class TransitionMatrix<T> extends BaseModel {
	
	private final IndexMatrix<Place, T> valuesMatrix;
	private final T defaultValue;
	private final Class<T> valueClass;
	
	public static <V> TransitionMatrix<V> create(Transition transition, Class<V> valueClass) {
		return new TransitionMatrix<V>(transition, valueClass, null);
	}

	public static <V> TransitionMatrix<V> create(Transition transition, Class<V> valueClass, V defaultValue) {
		return new TransitionMatrix<V>(transition, valueClass, defaultValue);
	}

	private TransitionMatrix(Transition transition, Class<T> valueClass, T defaultValue) {
		super();
		if (transition == null) {
			throw new IllegalArgumentException("Transition cannot be null");
		}
		if (defaultValue == null && valueClass == null) {
			throw new IllegalArgumentException(
					"Defaul value cannot be null. Use the static create method instead of this constructor.");
		}
		setParent(transition);
		this.valuesMatrix = new IndexMatrix<Place, T>();
		this.defaultValue = defaultValue;
		if (valueClass != null) {
			this.valueClass = valueClass;
		} else {
			this.valueClass = (Class<T>) defaultValue.getClass();
		}
	}
	
	public TransitionMatrix(Transition transition, T defaultValue) {
		this(transition, null, defaultValue);
	}

	public Transition getTransition() {
		return (Transition) getParent();
	}

	public T getDefaultValue() {
		return defaultValue;
	}
	
	public void setAt(Place from, Place to, T value) {
		//TODO: check if from and to are in transition!
		if (value != null && ! value.equals(defaultValue))
			valuesMatrix.setAt(from, to, value);
		else
			valuesMatrix.setAt(from, to, null);
	}
	
	public T getAt(Place from, Place to) {
		T result = valuesMatrix.getAt(from, to);
		if (result != null)
			return result;
		else return defaultValue;
	}

	/**
	 * @return the valueClass
	 */
	public Class<T> getValueClass() {
		return valueClass;
	}
	
	public void undoableSetAt(Place from, Place to, T value) {
		execute(new EditTransitionMatrixCommand<T>(this, from, to, value));
	}
	
	public class EditTransitionMatrixCommand<V> extends Command {

		private final TransitionMatrix<V> matrix;
		private final Place from;
		private final Place to;
		private V value;
		
		public EditTransitionMatrixCommand(String description,
				TransitionMatrix<V> matrix, Place from, Place to, V value) {
			super(description);
			
			if (matrix == null || from == null || to == null) {
				throw new IllegalArgumentException("Argument cannot be null");
			}
			
			this.matrix = matrix;
			this.setAffectedObject(matrix);
			this.from = from;
			this.to = to;
			this.value = value;
		}

		public EditTransitionMatrixCommand(TransitionMatrix<V> matrix, Place from,
				Place to, V value) {
			this("Edit matrix", matrix, from, to, value);
		}
		
		private void swap() {
			V old = matrix.getAt(from, to) ;
			matrix.setAt(from, to, value);
			value = old;
		}
		
		@Override
		public void execute() {
			swap();
		}

		@Override
		public void unExecute() {
			swap();
		}

	}

}
