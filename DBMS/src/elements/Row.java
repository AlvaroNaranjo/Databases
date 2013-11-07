/**
 *
 */
package elements;

import java.util.ArrayList;
import java.util.List;

import parser.ColumnIndexOutOfBoundsException;

/**
 * Creates a Row of items, each stored in a {@link java.lang.String}
 *
 * @version 2-N1
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 *
 */
 public class Row implements Cloneable {

	/**
	 * All the items in this Row.
	 */
	private List<Cell> row;

    /**
     * Create a new empty Row
     */
    public Row() {
		row = new ArrayList<Cell>();
    }

    /**
     * Create a new Row, populate it with the {@link java.lang.String}s in
     * <code>c</code> and set the maximum length to the size of
     * <code>c</code>
     *
	 * @param c A List of {@link java.lang.String} to populate the string.
     *
     */
     public Row(List<Cell> c) {
        this();
		row.addAll(c);
    }

    /**
     * Create a new Row, populate it with the {@link java.lang.String}s in
     * <code>c</code> and set the maximum length to the size of
     * <code>c</code>
     *
     * @param defn A List of Booleans consisting of <code>true</code> where the
     * given Cell is expected to be a {@link NumberCell} and <code>false</code>
     * where the given Cell is expected to be a {@link StringCell}.
     *
     * @param c A List of {@link java.lang.String} to populate the row.
     *
     */
    public Row(List<Boolean> defn, List<Cell> c) {
    	this();

    	if (defn.size() != c.size()) {
    		return;
    	}

		for (int i = 0; i < defn.size(); i++) {
			if (defn.get(i) != c.get(i).isNumberCell()) {
				return;
			}
		}

    	row.addAll(c);
    }

    /* Only Constructors above this point */

    /**
	 * Return true if applying isNumberCell to every Cell in Row produces
	 * the same List of Booleans as defn.
	 * @param defn Specifies the type of value each Cell in Row should hold
	 * @return boolean
	 */
    public boolean hasDefinition(List<Boolean> defn) {
    	//Compare number of columns
    	if (defn.size() != this.size()) {
    		return false;
    	}

    	java.util.Iterator<Boolean> b = defn.iterator();
    	for (Cell c: row) {
    		if (b.next() != c.isNumberCell()) {
    			return false;
    		}
    	}

    	return true;
    }

    /**
     * Add a {@link java.lang.String} to the Row if size permits.
     *
     * @param c The {@link java.lang.String} to add to the Row
     * @return true if addition was successful, false otherwise
     */
     public boolean add(Cell c) {
		return row.add(c);
    }

    /**
	 * Add a List of {@link java.lang.String}s to the string, if all the
	 * items can fit
     *
     * @param c A List of {@link java.lang.String} to add to this Row
     * @return Whether or not the addition was successful
     */
     public boolean addAll(List<Cell> c) {
		return row.addAll(c);
    }

    /**
     * Adds all the Cells from the given Row to this row.
     *
     * @param r The Row to add
     * @return <code>true</code> if the addition was successful
     * @see #addAll(List)
     */
     public boolean addAll(Row r) {
        return addAll(r.row);
    }

    /**
     * Change the value of the {@link java.lang.String} at
     * <code>index</code>. Provided both are of the same type and there is an
     * item on
	 * <code>index</code> for this string
     *
     *
     * @param index the index which is to be changed
     * @param c the new {@link java.lang.String}
     * @return the element previously at the specified location
     */
     public Cell changeCell(int index, Cell c) {
		return (index >= 0
                        && index < size()) ? row.set(index, c) : null;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((row == null) ? 0 : row.hashCode());
	    return result;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        // if the obj isn't a Row or has a different size, then it's not equal
        if (obj instanceof Row && ((Row) obj).size() == size()) {
            Row r = (Row) obj;
            // check each and every cell for equality
            for (int i = 0; i < size(); i++) {
                try {
                    if (!(getCell(i).equals(r.getCell(i)))) {
                        return false;
                    }
                } catch (ColumnIndexOutOfBoundsException e) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

	/*(non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = "";
		java.util.Iterator<Cell> it = row.iterator();
        while (it.hasNext()) {
            s += it.next().toString() + (it.hasNext() ? "," : "");
        }
        return s;
    }

    /* Getters and Setters Past this Point */
    /**
	 * Get the entire string
     *
	 * @return this string
     */
    public List<Cell> getRow() {
		return row;
    }

    /**
     * Get the Cell at the specified index
     *
     * @param index the index of the cell to return
     *
     * @return the cell at <code>index</code>if index is between 0 and size()
     * @throws ColumnIndexOutOfBoundsException if the given index is less than 0
     * or greater than size()
     */
    public Cell getCell(int index) throws ColumnIndexOutOfBoundsException {
        if (index >= size() || index < 0) {
            throw new ColumnIndexOutOfBoundsException("" + index);
        }
		return row.get(index);
    }

    /**
	 * Get the current size of this string
     *
	 * @return the size of this string
     */
    public int size() {
		return row.size();
    }

    /**
	 * Clear the string
     */
    public void clear() {
		row.clear();
    }

    /**
     * Clones this current Row
     *
	 * @return a deep copy of this string.
     */
    @Override
	public Row clone() {
        return new Row(this.getRow());
    }
 }