package net.generalised.genedit.model.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic sparse matrix. Indices can be of any type, not only numeric.
 * 
 * 
 * @author Dimitar Dimitrov
 *
 * @param <K> the index type. It is not limited to numbers only
 * @param <V> the value type
 */
public class IndexMatrix<K, V> {

//	private int rows;
//	private int columns;
	private final Map<Pair, V> data;
	
	public IndexMatrix() {
//		this.rows = rows;
//		this.columns = columns;
		data = new HashMap<Pair, V>();
	}
	
//	public int getRows() {
//		return rows;
//	}
//
//	public void setRows(int rows) {
//		this.rows = rows;
//		//TODO: delete rows... 
//	}
//
//	public int getColumns() {
//		return columns;
//	}
//
//	public void setColumns(int columns) {
//		this.columns = columns;
//		//TODO: delete columns...
//	}
//
	public V getAt(K row, K column) {
		V value = data.get(new Pair(row, column));
		return value;
	}
	
	public void setAt(K row, K column, V value) {
		Pair pair = new Pair(row, column); 
		if (value == null)
			data.remove(pair);
		else data.put(pair, value);
	}
	
	public Collection<V> getAllValues() { //TODO: make iterator!
		return data.values();
	}
	
	public boolean isEmpty() {
		return data.size() == 0;
	}
	
	/**
	 * Mutable pair of two objects.
	 * 
	 */
	private class Pair {
		private K left;

		private K right;

		/**
		 * @param left
		 * @param right
		 */
		public Pair(K left, K right) {
			super();
			this.left = left;
			this.right = right;
		}

		/**
		 * @return the left
		 */
		public K getLeft() {
			return left;
		}

		/**
		 * @return the right
		 */
		public K getRight() {
			return right;
		}

		/**
		 * @param left
		 *            the left to set
		 * @modifies this.
		 */
		public void setLeft(K left) {
			this.left = left;
		}

		/**
		 * @param right
		 *            the right to set
		 * @modifies this.
		 */
		public void setRight(K right) {
			this.right = right;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + left.hashCode();
			result = PRIME * result + right.hashCode();
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Pair other = (Pair) obj;
			if (left != other.left)
				return false;
			if (right != other.right)
				return false;
			return true;
		}
	}
}
