package net.generalised.genedit.baseapp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.generalised.genedit.baseapp.model.ListCommand.ListChange;

/**
 * ...
 * The parent property of all objects in this list can be automatically set to this list.
 * The list does not allow nulls.  
 * 
 * @author Dimitar Dimitrov
 *
 */
public class BaseModelList<T extends BaseModel> extends BaseModel implements List<T> {

	private final List<T> objects;

	private boolean autoParent;
	
	public BaseModelList() {
		this(true);
	}
	
	public BaseModelList(boolean autoParent) {
		objects = new ArrayList<T>();
		this.autoParent = autoParent;
	}
	
	public boolean add(T e) {
		if (e == null) {
			throw new NullPointerException("the list does not allow nulls as elements");
		}
		if (objects.add(e)) {
			if (autoParent) {
				e.setParent(this);
			}
			//setChanged();//hmm, nqmame nujda ot notify, 6tom imame komandi; ama ako modelyt e view?
			//notifyObservers();
			return true;
		}
		return false;
	}

	public void add(int index, T element) {
		if (element == null) {
			throw new NullPointerException("the list does not allow nulls as elements");
		}
		if (autoParent) {
			element.setParent(this);
		}
		objects.add(index, element);
	}

	public boolean addAll(Collection<? extends T> c) {
		// if you implement undoableAddAll, do not call undoableAdd - this will fire too many changes
		boolean modified = false;
		for (T element : c) {
			if (element != null) {
				if (autoParent) {
					element.setParent(this);
				}
				boolean lastAddModified = add(element);
				if (lastAddModified) {
					modified = true;
				}
			}
		}
		return modified;
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		//TODO ...
		throw new UnsupportedOperationException();
	}

	public void clear() {
		if (autoParent) {
			for (T element : objects) {
				element.setParent(null);
			}
		}
		objects.clear();
	}

	public boolean contains(Object o) {
		return objects.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return objects.contains(c);
	}

	public T get(int index) {
		return objects.get(index);
	}

	public int indexOf(Object o) {
		return objects.indexOf(o);
	}

	public boolean isEmpty() {
		return objects.isEmpty();
	}

	public Iterator<T> iterator() {
		return Collections.unmodifiableList(objects).iterator();
	}

	public int lastIndexOf(Object o) {
		return objects.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return Collections.unmodifiableList(objects).listIterator(); //TODO: allow modifications
	}

	public ListIterator<T> listIterator(int index) {
		return Collections.unmodifiableList(objects).listIterator(index); //TODO: allow modifications
	}

	public boolean remove(Object o) {
		if (o instanceof BaseModel && objects.remove(o)) {
			if (autoParent) {
				((BaseModel)o).setParent(null);
			}
			return true;
		}
		return false;
	}

	public T remove(int index) {
		T result = objects.remove(index);
		if (result != null) {
			if (autoParent) {
				result.setParent(null);
			}
		}
		return result;
	}

	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public T set(int index, T element) {
		if (element == null) {
			throw new NullPointerException("nulls are not allowed in BaseModeList");
		}
		T old = objects.get(index); // this may throw IndexOutOfBoundsException
		assert old != null; // we do not allow nulls in the list, let's check again
		if (autoParent) {
			old.setParent(null);
			element.setParent(this);
		}
		objects.set(index, element);
		return old;
	}

	public int size() {
		return objects.size();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object[] toArray() {
		return objects.toArray();
	}

	public <S> S[] toArray(S[] a) {
		return objects.toArray(a);
	}

	public void undoableAdd(T element) {
		execute(ListCommand.create(this, ListChange.ADD, element, -1));
	}
	
	public void undoableSet(int index, T element) {
		execute(ListCommand.create(this, ListChange.SET, element, index));
	}
	
	public void undoableRemove(T element) {
		execute(ListCommand.create(this, ListChange.REMOVE, element, -1));
	}
	
	public void undoableRemove(int index) {
		execute(ListCommand.create(this, ListChange.REMOVE, null, index));
	}
	
	@Override
	public String toString() {
		return objects.toString();
	}
}
