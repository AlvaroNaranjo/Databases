package elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import parser.ColumnIndexOutOfBoundsException;

/**
 * Creates a Table out of Row Objects.
 *
 * @version 2-N1
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 */

public class Table {

	/**
	 * To store whether or not this table is headed.
	 */
	private boolean       hasHead;

	/**
	 * The header of this table.
	 */
	private List<String>  header;

	/**
	 * The definition of this Table. A value of <code>true</code> means that
	 * the column at the given index is a NumberCell, and one of
	 * <code>false</code> means that the column at the given index is a
	 * StringCell.
	 */
	private final List<Boolean> definition;

	/**
	 * Keeps track of the column size of this table
	 */
	private final int     colSize;

	/**
	 * Keeps track of all the Rows in this table
	 */
	private List<Row>     table;

	/**
	 * Create a new Table with the given definition and without a header. The
	 * column size is set to the length of the definition list.
	 *
	 * @param defn The definition of this table, has <code>true</code> for
	 *            NumberCell and <code>false</code> for StringCell.
	 */
	public Table(List<Boolean> defn) {
		header = new ArrayList<String>();
		table = new ArrayList<Row>();
		colSize = defn.size();
		definition = defn;
	}

	/**
	 * Create a new Table with the given definition and the header. The
	 * column size is set to the length of the definition list.<br/>
	 *
	 * The header is only added if it has the same size as the definiton List.
	 *
	 * @param defn The definition of this table, has <code>true</code> for
	 *            NumberCell and <code>false</code> for StringCell.
	 * @param head a List containing the header for this table,
	 */
	public Table(List<Boolean> defn, List<String> head) {
		this(defn);
		if (head.size() == colSize) {
			setHeader(head);
		}
	}

	/**
	 * Add a given {@link Row} to the table, provided it has the same number
	 * of columns as this table
	 *
	 * @param r the {@link Row} to add
	 * @return whether or not the addition was successful
	 */
	public boolean add(Row r) {
		return (r.hasDefinition(definition)) ? table.add(r) : false;
	}

	/**
	 * Add a given List of {@link Row} to the table, given all {@link Row}
	 * have the same number of columns as this table.
	 *
	 * @param rows the List of {@link Row} to add
	 * @return whether or not the addition was successful
	 */
	public boolean addAll(List<Row> rows) {
		for (Row r : rows) {
			if (!r.hasDefinition(definition)) {
				return false;
			}
		}

		return table.addAll(rows);

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colSize;
		result = prime * result
				+ ((definition == null) ? 0 : definition.hashCode());
		result = prime * result + (hasHead ? 1231 : 1237);
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	/* (non-javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (null == obj) { //null is not equal to non null
			return false;
		}

		if (this == obj) {
			return true;
		}

		// Object needs do be a Table with the same size
		if (obj instanceof Table && ((Table) obj).size() == table.size()) {
			Table other = (Table) obj;

			if (other.hasHead != hasHead
					|| !other.header.equals(header)
					|| !other.definition.equals(definition)) {

				return false;
			}

			for (int i = 0; i < table.size(); i++) {
				// Two tables are equal if all Rows are equal
				if (!((Table) obj).getRow(i).equals(this.getRow(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the all the Rows in the table
	 *
	 * @return all the rows
	 */
	public List<Row> getAllRows() {
		return table;
	}

	/**
	 * Returns the ith Row if it exists, or null if it doesn't.
	 *
	 * @param i the index
	 * @return the Row at <code>i</code>
	 */
	public Row getRow(int i) {
		return (0 <= i && i < size()) ? table.get(i) : null;
	}

	/**
	 * Returns the number of columns in the table.
	 *
	 * @return the number of columns in the table.
	 */
	public int getColSize() {
		return colSize;
	}

	/**
	 * Finds the numerical value of the column the given colName refers to
	 * and returns the column number this refers to.
	 *
	 * @param colName The name of the column Header.
	 * @return the the column number the given header corresponds to.
	 * @throws ColumnIndexOutOfBoundsException if the given columnName is
	 *              not in the table
	 */
	public int getColumnNumber(String colName)
			throws ColumnIndexOutOfBoundsException {
		if (!(header.contains(colName))) {
			throw new ColumnIndexOutOfBoundsException(colName);
		}

		return header.indexOf(colName);
	}

	/**
	 * Finds out whether or not this instance of a Table has a head.
	 *
	 * @return whether or not this table has a header
	 */
	public boolean hasHead() {
		return hasHead;
	}

	/**
	 *Removes the header for this table.
	 */
	public void removeHeader() {
		hasHead = false;
		header = new ArrayList<String>();
	}

	/**
	 * Returns the header for this table.
	 *
	 * @return the header for this table.
	 */
	public List<String> getHeader() {
		return header;
	}

	/**
	 * Finds the header of the column at the given index, or an empty string
	 * if this table doesn't have a header.
	 *
	 * @param colNum the index of the column whose header to get
	 * @return the header for this table, for column at <code>index</code>.
	 *         If this table has no header, returns an empty String
	 * @throws ColumnIndexOutOfBoundsException if given <code>index</code>
	 * is below 0, or greater than the table's column size.
	 */
	public String getHeader(int colNum)
			throws ColumnIndexOutOfBoundsException {
		if (colNum < 0 || colNum >= colSize) {
			throw new ColumnIndexOutOfBoundsException("" + colNum);
		}

		return hasHead ? header.get(colNum) : "";
	}

	/**
	 * Changes the header of this table to the user-specified one.<br/>
	 *
	 *  If the user specified header is <code>null</code> or an empty list,
	 *  removes this table's header.
	 *
	 * @param newHeader the header to set
	 */
	public void setHeader(List<String> newHeader) {
		if (newHeader == null || newHeader.size() == 0) {
			removeHeader();
			return;
		}

		hasHead = true;
		this.header = newHeader;
	}

	/**
	 * @return the definition
	 */
	public List<Boolean> getDefinition() {
		return definition;
	}

	/**
	 * Gets the definition of the table at the user Specified index.
	 *
	 * @param colNum The index of the column whose definition to get
	 * @return whether or not the given index is a numberCell
	 * @throws ColumnIndexOutOfBoundsException if the column doesn't exist
	 */
	public boolean getDefinition(int colNum)
			throws ColumnIndexOutOfBoundsException {
		if (colNum < 0 || colNum >= colSize) {
			throw new ColumnIndexOutOfBoundsException("" + colNum);
		}

		return definition.get(colNum);
	}

	/**
	 * Gets the definition of the table at the given indices.
	 *
	 * @param intList an integer List
	 * @return a boolean List containing the definition of the column index
	 *         specified in intList
	 * @throws ColumnIndexOutOfBoundsException if one of the given numbers
	 *             in the intList is less than 0, or greater than this
	 *             table's column number
	 */
	public List<Boolean> getDefinition(List<Integer> intList)
			throws ColumnIndexOutOfBoundsException {
		List<Boolean> defnList = new ArrayList<Boolean>();

		for (int i : intList) {
			defnList.add(getDefinition(i));
		}
		return defnList;
	}

	/**
	 * Returns the size of the table.
	 *
	 * @return the size of this table
	 */
	public int size() {
		return table.size();
	}

	/**
	 * Finds and returns the cell at (rowIndex, colNum)
	 *
	 * @param rowIndex the index of the row to get
	 * @param colNum The index of the column to get
	 * @return The cell at (<code>rowIndex,colNum</code>).
	 *              <code>null</code> if the row is out of bounds,
	 * @throws ColumnIndexOutOfBoundsException if the column asked for is
	 *              unavailable, or invalid
	 */
	public Cell getCell(int rowIndex, int colNum)
			throws ColumnIndexOutOfBoundsException {
		return table.get(rowIndex).getCell(colNum);
	}

	/**
	 * Sorts this {@link Table} by some column and returns the new sorted
	 * {@link Table}.
	 * If the given <code>colNum</code> is too large, or too small, sort
	 * using the 1st row.
	 *
	 * @param colNum the index of the column to sort
	 * @return the sorted {@link Table}
	 * @throws ColumnIndexOutOfBoundsException if given column number is out
	 *              of bounds
	 */
	public Table sort(int colNum) throws ColumnIndexOutOfBoundsException {
		// NOTE: this is destructive BY DESIGN
		if (colNum < 0 || colNum >= colSize) {
			throw new ColumnIndexOutOfBoundsException("" + colNum);
		}
		// use the Collection's sort method
		Collections.sort(this.getAllRows(),
				new TableComparator(colNum));
		return this;
	}

	/*(non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = "";
		if (hasHead) {
			Iterator<String> it = header.iterator();
			while (it.hasNext()) {
				s += it.next() + (it.hasNext() ? "," : "\n");
			}
		}

		for (Row r : table) {
			s += r.toString() + "\n";
		}
		return s;
	}

	/**
	 * Deep copies this table and returns a new one with the same
	 * number of elements
	 *
	 * @return a deep copy of the Table
	 */
	@Override
	public Table clone() {
		Table t = new Table(definition, header);
		for (Row r : table) {
			t.add(r.clone());
		}
		return t;
	}
}