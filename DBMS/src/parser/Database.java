package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import elements.Cell;
import elements.NumberCell;
import elements.Row;
import elements.StringCell;
import elements.Table;

/**
 * Creates a Database, consisting of {@link java.lang.String} as its key and a
 * {@link elements.Table} as its keys.
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 *
 */
public class Database {

	/**
	 * Keeps track of all the tables and their names.
	 */
	private Map<String, Table> database = new HashMap<String, Table>();

	/**
	 * Adds the given {@link Table} <code>t</code> to the {@link Map} with
	 * the given {@link String} <code>s</code> as key.
	 *
	 * @param s The name of the table
	 * @param t The table to add
	 */
	void add(String s, Table t) {
		if (t != null) {
			this.database.put(s, t);
		}
	}

	/**
	 * Prints the given {@link Table} to the standard Output, or a message
	 * stating that the table can't be found.
	 *
	 * @param tableName The name of the {@link Table} to be printed
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 */
	void print(String tableName) throws NoSuchTableException {
		System.out.println(getTable(tableName));
	}

	/**
	 * Writes a {@link Table} to the given file
	 *
	 * @param tableName the name of the {@link Table} to be written
	 * @param fileName the name of the {@link java.io.File} in which to save the
	 * {@link Table}
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 */
	void save(String tableName, String fileName)
			throws NoSuchTableException {
		File file = new File(fileName);

		// if file can't be written to, do nothing
		if (file.exists() && !file.canWrite()) {
			System.out.println("The file " + fileName
					+ " cannot be written to.");
			return;
		}
		// Explicitly ensure that the file can be written to and read from
		file.setReadable(true);
		file.setWritable(true);
		try {
			Table t = getTable(tableName);
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(t.toString());
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the {@link Table} specified by <code>tableName</code>.
	 *
	 * @param tableName the name of the {@link Table} to get
	 * @return the {@link Table} if it exists, <code>null</code> if it doesn't.
	 * @throws NoSuchTableException thrown if the database does not have that
	 * Table
	 */
	Table getTable(String tableName) throws NoSuchTableException {
		if (!database.containsKey(tableName)) {
			throw new NoSuchTableException(tableName);
		}

		return database.get(tableName);
	}

	/**
	 * Assigns a Table Object representing the contents of csv formatted file
	 * fileName to variable tableName.
	 * tableName must have a definition given with define command.
	 * Otherwise throws NoSuchTableException.
	 * File fileName must be saved or exist in the current directory.
	 * If file not found assign no Table to tableName.
	 * If contents of fileName do not agree with tableName's definition
	 * due to column type or number mismatch NumberFormatException or Exception
	 * gets thrown and no change in tableName's value.
	 *
	 * @param 	headed 		Specifies if a table has a header
	 * @param 	tableName 	The name of the table to load
	 * @param 	fileName 	The name of the file to load
	 * @throws IOException
	 * @throws NoSuchTableException if table was not given definition
	 */
	void load(final boolean headed, final String tableName,
			final String fileName) throws IOException, NoSuchTableException {

		//get the table from the database, throws NoSuchTableException
		Table temp = new Table(getTable(tableName).getDefinition());

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));

			String inputStr;
			List<String> header;

			//If headed read header from first line in fileName and set it
			if (headed && (inputStr = br.readLine())  != null) {
				header = Arrays.asList(inputStr.split(","));
				if (!(header.size() == temp.getColSize())) { //mismatches defn
					br.close();
					throw new Exception();
				}
				temp.setHeader(header);
			}

			while ((inputStr = br.readLine()) != null) { //read next line
				Row r = new Row();
				//to confirm cell type
				Iterator<Boolean> b = temp.getDefinition().iterator();
				for (String s : inputStr.split(",")) { //add to row
					r.add(b.next() ? new NumberCell(s) : new StringCell(s));
				}

				if (!r.hasDefinition(temp.getDefinition())) { //mismatches defn
					br.close();
					throw new Exception();
				}
				temp.add(r.clone()); //add to table
			}

			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to load: "
					+ "The specified file " + fileName + " does not exist");
			return;
		} catch (NumberFormatException e) { //thrown if there's a type mismatch
			System.out.println("Unable to load: The table you are loading "
					+ tableName + " has contents differing from definition");
			return;
		} catch (NoSuchElementException e) { //Thrown by Iterator#next()
			//if number of columns differs from definition.
			System.out.println("Unable to load: The table you are loading "
					+ tableName + " has contents differing from definition");
			return;
		} catch (Exception e) {
			System.out.println("Unable to load: The table you are loading "
					+ tableName + " has contents differing from definition");
			return;
		}

		database.put(tableName, temp);
	}

	/**
	 * Taking a String expression as input, returns the Table result of running
	 * columns method on input if input is table name and a list of integers,
	 * else finds the index of the columns being referenced by header name and
	 * then returns the result of running columns on input with new indices.
	 *
	 * @param expr A String consisting of a table name, followed by either a
	 * list of integers or a list of headers.
	 * @return a Table result from the columns expression.
	 * @throws NoSuchTableException if table name is invalid.
	 * @throws ColumnIndexOutOfBoundsException if indices of the columns are
	 * incorrect.
	 */
	Table columnsCheck(String expr) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {
		//Split string expression to parse input
		String[] cmd = expr.split(",");
		Table inputTable = getTable(cmd[0].trim());

		//If no columns are specified, none are to be kept - result empty table
		if (cmd.length == 1) {
			return new Table(new ArrayList<Boolean>());
		}

		//If the first column is referenced by a digit, then the rest are too
		if (cmd[1].trim().matches("^\\d+$")) {
			return columns(expr);
		}

		//Create a StringBuffer to keep track of all the columns as their
		//indices are matched with their headers
		StringBuffer newExpr = new StringBuffer();
		//Append table name
		newExpr = newExpr.append(cmd[0]);

		for (int i = 1; i < cmd.length; i++) {
			newExpr.append("," + inputTable.getColumnNumber(cmd[i].trim()));
		}
		return columns(newExpr.toString());
	}

	/**
	 * Taking a String expression as input, converts column reference by header
	 * to reference by integer index (if necessary) and returns the result of
	 * running the rows method on new string input, a Table.
	 * @param expr a String consisting of table name followed by column index
	 * (or header name), a comparator and compare-to value.
	 * @return a Table result from the rows expression.
	 * @throws NoSuchTableException if table name is invalid.
	 * @throws ColumnIndexOutOfBoundsException if indices of the column are
	 * invalid.
	 */
	Table rowsCheck(String expr) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {
		//Split string expression to parse input
		String[] cmd = expr.split(",");
		Table inputTable = getTable(cmd[0].trim());

		//If column is referenced by index, send user input to rows expression.
		if (cmd[1].trim().matches("^\\d+$")) {
			return rows(expr);
		}
		StringBuffer newExpr = new StringBuffer();
		//Append table name
		newExpr = newExpr.append(cmd[0]);
		//Find index of column from header and append to newExpr
		newExpr = newExpr.append(","
				+ inputTable.getColumnNumber(cmd[1].trim()));

		//Append remaining parts of original input to newExpr
		for (int i = 2; i < cmd.length; i++) {
			newExpr.append("," + cmd[i]);
		}
		return rows(newExpr.toString());
	}

	/* =============ASSIGNMENT COMMANDS===================== */
	/**
	 * Returns a Table Object with only the listed columns contained.
	 *
	 * @param expr The string containing the columns expression
	 * @return Table newTable with all unspecified columns from input Table
	 * removed.
	 *
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if the column number asked for is
	 * out of Bounds
	 */
	Table columns(String expr) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {
		String[] cmd = expr.split(","); // split into tableName & columnNums

		Table inputTable = getTable(cmd[0].trim());

		if (cmd.length == 1) { // no colIndex means nothing to keep
			return new Table(new ArrayList<Boolean>());
		}

		//Convert sublist of the Strings of ints in cmd into an int List
		List<Integer> indexList = stringArrayToIntList(cmd, 1);
		//Get boolean definitions of each column in inputTable as a List
		List<Boolean> defnList = inputTable.getDefinition(indexList);
		//Initialize a list of headers that will be implemented after loop
		List<String> headers = new ArrayList<String>(inputTable.getHeader());

		Table newTable = new Table(defnList, headers);

		for (Row r : inputTable.getAllRows()) { //for each row in table
			Row newRow = new Row();
			for (int col : indexList) { //go through all the indices
				headers.add(inputTable.getHeader(col));
				newRow.add(r.getCell(col)); // keep if they are in the list
			}
			newTable.add(newRow.clone()); // clone to avoid destruction
		}
		return newTable;
	}

	/**
	 * Starting from the input index from an input array of strings, returns
	 * a list of all of the strings as integers.
	 *
	 * @param stringArray An array of strings.
	 * @param starterIndex An integer referring to index of stringArray
	 * from which parsing with begin.
	 * @return intList An list of integers containing all the required
	 * strings in stringArray converted to integers.
	 */
	List<Integer> stringArrayToIntList(String[] stringArray,
			int starterIndex) {
		List<Integer> intList = new ArrayList<Integer>();
		//Starting from index starterIndex, parse stringArray's elements
		for (int i = starterIndex; i < stringArray.length; i++) {
			//Convert strings into intergers and add into intList
			intList.add(Integer.parseInt(stringArray[i].trim()));
		}
		return intList;
	}

	/**
	 * From an input Table, returns its copy with only rows whose specified
	 * column has suitable values compared to given value, using a comparator.
	 *
	 * @param expr A String containing the rows command
	 * @return Table newTable containing only the rows from the input Table
	 * whose column meets the compare condition.
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if column number is out of bounds
	 */
	Table rows(String expr) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {
		// split into tableName, col, comparator, val
		String[] cmd = expr.split(",");
		Table inputTable = getTable(cmd[0].trim());
		int colNum = Integer.parseInt(cmd[1].trim());
		String comparator = cmd[2].trim();
		Cell value = inputTable.getDefinition(colNum)
				? new NumberCell(cmd[3].trim())
		: new StringCell(cmd[3].trim());

				// new Table to add amended rows to
				Table newTable = new Table(inputTable.getDefinition());
				newTable.setHeader(inputTable.getHeader());

				for (Row r : inputTable.getAllRows()) {

					Cell c = r.getCell(colNum);
					//Check parameters; check if each cell matches the condition
					if (comparator.contains("=") && c.equals(value)) {
						newTable.add(r);
					} else if (comparator.contains("<")
							&& c.compareTo(value) < 0) {
						newTable.add(r);
					} else if (comparator.contains(">")
							&& c.compareTo(value) > 0) {
						newTable.add(r);
					}
				}
				return newTable;
	}

	/**
	 * Return a new table that is the Cartesian product of table the two tables
	 * referred to by the variables.
	 *
	 * @param expr the product expression
	 * @return The Cartesian product of the two tables specified in expr
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 */
	Table product(String expr) throws NoSuchTableException {
		String[] split = expr.split(","); // parsing the command
		Table table1 = getTable(split[0].trim());
		Table table2 = getTable(split[1].trim());

		List<Boolean> newDefn = new ArrayList<Boolean>(table1.getDefinition());
		newDefn.addAll(table2.getDefinition());

		List<String> productHeaders = new ArrayList<String>(table1.getHeader());
		productHeaders.addAll(table2.getHeader());

		Table productTable = new Table(newDefn, productHeaders);

		if (table1.size() == 0) { //if table1 is empty
			//return empty table if table2 is empty, or table2 if it is not
			return table2.size() == 0 ? productTable : table2;
		} else if (table2.size() == 0) {
			return table1; //table1 is obviously not empty, so return
		}

		// add each Row from table2 to one from table 1,
		for (Row r1 : table1.getAllRows()) {
			for (Row r2 : table2.getAllRows()) {
				Row tempRow = r1.clone();
				tempRow.addAll(r2.clone());
				productTable.add(tempRow.clone());
			}
		}
		return productTable;
	}

	/**
	 * Return a new table that is the Cartesian product of the two tables
	 * referred to by the variables, but that includes only rows that satisfy a
	 * condition.
	 *
	 * @param expr the join expression
	 * @return The Cartesian product of the two tables specified by the
	 * expression containing only the rows that have equal column values
	 * indicated in the integer pair list or column name list.
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if one of the given indices are
	 * out of bounds
	 */
	Table join(String expr) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {
		String[] split = expr.split(","); // split the command

		Table t1 = getTable(split[0].trim());
		Table t2 = getTable(split[1].trim());

		List<Integer> colT1 = new ArrayList<Integer>();
		List<Integer> colT2 = new ArrayList<Integer>();

		if (t1.size() == 0 || t2.size() == 0) {
			return new Table(new ArrayList<Boolean>());
		}

		//assume that if one columnName is not a number, none are
		if (!split[2].trim().matches("\\d+")) {
			for (int i = 2; i <= split.length - 2; i += 2) {
				colT1.add(t1.getColumnNumber(split[i].trim()));
				colT2.add(t2.getColumnNumber(split[i + 1].trim()));
			}
		} else {
			for (int i = 2; i <= split.length - 2; i += 2) {
				colT1.add(Integer.parseInt(split[i].trim()));
				colT2.add(Integer.parseInt(split[i + 1].trim()));
			}
		}

		return join(t1, t2, colT1, colT2);
	}

	/**
	 * Return a new table that is the Cartesian product of the two tables
	 * referred to by the variables, but that includes only rows that satisfy a
	 * condition.
	 *
	 * @param expr the join expression
	 * @return The Cartesian product of the two tables specified by the
	 * expression containing only the rows that have equal column values
	 * indicated in the integer pair list.
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if one of the given indices are
	 * out of bounds
	 */
	Table join(Table t1, Table t2, List<Integer> colT1, List<Integer> colT2)
			throws NoSuchTableException, ColumnIndexOutOfBoundsException {

		List<Boolean> joinDefn = new ArrayList<Boolean>(t1.getDefinition());
		joinDefn.addAll(t2.getDefinition());

		List<String> joinHeaders = new ArrayList<String>(t1.getHeader());
		joinHeaders.addAll(t2.getHeader());

		//compare the definitions of the two tables
		for (int i = 0; i < colT1.size(); i++) {
			if (t1.getDefinition(colT1.get(i))
					!= (t2.getDefinition(colT2.get(i)))) {
				colT1.remove(i);
				colT2.remove(i);
			}
		}

		Table joinTable = new Table(joinDefn, joinHeaders);

		for (Row r1 : t1.getAllRows()) {
			for (Row r2 : t2.getAllRows()) {
				if (joinCheck(r1, r2, colT1, colT2)) { //compare with all pairs
					Row tempRow = r1.clone(); //clone() = non-destructive
					tempRow.addAll(r2.clone());
					joinTable.add(tempRow.clone());
				}
			}
		}
		return joinTable;
	}

	/**
	 * Return a new table that is the Cartesian product of the two tables
	 * referred to by the variables, but the columns to be compared are
	 * determined entirely by column names.
	 *
	 * @param expr the natural join expression
	 * @return The Cartesian product of the two tables specified by the
	 * expression containing only the rows that have equal column values and
	 * equal column names.
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if one of the given indices are
	 * out of bounds
	 */

	Table naturalJoin(String expr, Boolean outer) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {

		String[] split = expr.split(","); // split the command

		Table table1 = getTable(split[0].trim());
		Table table2 = getTable(split[1].trim());

		//compare the column headers and add column numbers to expr if equal
		for (int i = 0; i < table1.getColSize(); i++) {
			for (int j = 0; j < table2.getColSize(); j++) {
				if (table1.getHeader(i).equals(table2.getHeader(j))) {
					expr += "," + i + "," + j;
				}
			}
		}

		//if there are no tables with the same headers, return an empty table
		if (Arrays.equals(expr.split(","), split)) {
			return new Table(new ArrayList<Boolean>());
		}
		if (outer) {
			return outerJoin(expr);
		}
		return join(expr);
	}

	/**
	 * Return true or false based on relationship of the rows according to the
	 * column values specified by the integer pair list.
	 *
	 * @param row1 The first {@link Row} to compare
	 * @param row2 The second {@link Row} to compare
	 * @param intPairs an {@link java.util.ArrayList} which represents the
	 * columns that need to be compared
	 * @return true if the two rows have equal column values for each integer
	 * pair specified in intPairs, false otherwise
	 * @throws ColumnIndexOutOfBoundsException if one of the given indices are
	 * out of bounds
	 */
	boolean joinCheck(Row r1, Row r2, List<Integer> col1, List<Integer> col2)
			throws ColumnIndexOutOfBoundsException {

		// if the the two cells at aren't the same
		for (int i = 0; i < col1.size(); i++) {
			if (!r1.getCell(col1.get(i)).equals(r2.getCell(col2.get(i)))) {
				return false; // they shouldn't be added
			}

		}
		return true;
	}

	/**
	 * Return true or false based on relationship of the rows according to the
	 * column values specified by the integer pair list.
	 *
	 * @param row1 The first {@link Row} to compare
	 * @param row2 The second {@link Row} to compare
	 * @param intPairs an {@link java.util.ArrayList} which represents the
	 * columns that need to be compared
	 * @return true if the two rows have equal column values for each integer
	 * pair specified in intPairs, false otherwise
	 * @throws ColumnIndexOutOfBoundsException if one of the given indices are
	 * out of bounds
	 */
	boolean joinCheck(Row r1, Row r2, List<Integer> intPairs)
			throws ColumnIndexOutOfBoundsException {

		List<Integer> c1 = new ArrayList<Integer>();
		List<Integer> c2 = new ArrayList<Integer>();

		// if the the two cells at aren't the same
		for (int i = 0; i <= intPairs.size() - 2; i += 2) {
			c1.add(intPairs.get(i));
			c2.add(intPairs.get(i + 1));
		}
		return joinCheck(r1, r2, c1, c2);
	}

	/**
	 * Return a new table that is the Cartesian product of the two tables
	 * referred to by the variables, but that includes only rows that satisfy a
	 * condition. There may may be rows in one table that have no corresponding
	 * row in the other table to combine with. Every such row gets combined
	 * with a row of empty cells.
	 *
	 * @param expr the outer join expression
	 * @return The Cartesian product of the two tables specified by the
	 * expression containing the rows that have equal column values
	 * indicated in the integer pair list or column name list appended with
	 * the rows that do not satisfy this condition.
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if one of the given indices are
	 * out of bounds
	 */
	Table outerJoin(String expr) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {

		String[] split = expr.split(","); // split the command
		List<Integer> intPairs = new ArrayList<Integer>();
		Table table1 = getTable(split[0].trim());
		Table table2 = getTable(split[1].trim());

		//assume that if one columnName is not a number, none are
		if (!split[2].trim().matches("\\d+")) {
			for (int i = 2; i <= split.length - 2; i += 2) {
				intPairs.add(table1.getColumnNumber(split[i].trim()));
				intPairs.add(table2.getColumnNumber(split[i + 1].trim()));
			}
		} else {
			for (int j = 2; j < split.length; j++) {
				intPairs.add(Integer.valueOf(split[j].trim()));
			}
		}

		//compare the definitions of the two tables
		for (int i = 0; i + 2 < intPairs.size(); i += 2) {
			if (table1.getDefinition(intPairs.get(i)) != (table2
					.getDefinition(intPairs.get(i + 1)))) {
				intPairs.remove(i);
				intPairs.remove(i + 1);
			}

		}
		return outerJoinCreate(table1, table2, intPairs);
	}

	/**
	 * Return a new table that is the Cartesian product of the two tables
	 * referred to by the variables, but that includes only rows that satisfy a
	 * condition. Keeps track of rows that have not been combined.
	 *
	 * @param table1 the first table to be joined
	 * @param table2 the second table to be joined
	 * @param intPairs specifies which columns need to be checked
	 * @return The Cartesian product of the two tables specified by the
	 * expression containing only the rows that have equal column values
	 * indicated in the integer pair list or column name list appended with
	 * the rows that do not satisfy this condition.
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if one of the given indices are
	 * out of bounds
	 */
	Table outerJoinCreate(Table table1, Table table2, List<Integer> intPairs)
			throws NoSuchTableException, ColumnIndexOutOfBoundsException {

		List<Boolean> joinDefinitions = new ArrayList<Boolean>();
		List<String> joinHeaders = new ArrayList<String>();

		joinDefinitions.addAll(table1.getDefinition());
		joinDefinitions.addAll(table2.getDefinition());
		joinHeaders.addAll(table1.getHeader());
		joinHeaders.addAll(table2.getHeader());

		//create result table and lists representing table1 and table2 rows
		Table joinTable = new Table(joinDefinitions, joinHeaders);
		ArrayList<Row> table1Rows = new ArrayList<Row>(table1.getAllRows());
		ArrayList<Row> table2Rows = new ArrayList<Row>(table2.getAllRows());

		for (Row r1 : table1.getAllRows()) {
			for (Row r2 : table2.getAllRows()) {
				if (joinCheck(r1, r2, intPairs)) { //compare with all int pairs
					if (table1Rows.contains(r1)) { //remove row that is joined
						table1Rows.remove(r1); //removes row from table1's rows
					}
					if (table2Rows.contains(r2)) {
						table2Rows.remove(r2); //removes row from table2's rows
					}
					Row tempRow = r1.clone(); //clone() = non-destructive
					tempRow.addAll(r2.clone());
					joinTable.add(tempRow.clone());
				}
			}
		}
		return addEmptyCellRows(table1, table2, joinTable, table1Rows,
				table2Rows);
	}

	/**
	 * Return a new table that adds the rows which had no corresponding
	 * row from the other table to combine with. These rows are combined with
	 * empty cells.
	 *
	 * @param table1 the first table to be joined
	 * @param table2 the second table to be joined
	 * @param joinTable the Cartesian product of two tables that satisfy a
	 * condition
	 * @param table1Rows the rows from table1 that did not satisfy the required
	 * condition
	 * @param table2Rows the rows from table2 that did not satisfy the required
	 * condition
	 * @return The Cartesian product of the two tables specified by the
	 * expression containing only the rows that have equal column values
	 * appended with the rows that do not satisfy this condition.
	 * indicated in the integer pair list or column name list.
	 */

	Table addEmptyCellRows(Table table1, Table table2, Table joinTable,
			ArrayList<Row> table1Rows, ArrayList<Row> table2Rows) {

		Row emptyRowTable1 = addEmptyCells(table1);
		Row emptyRowTable2 = addEmptyCells(table2);

		//add an empty row to the rows in table1 that did not match
		for (Row row: table1Rows) {
			Row tempRow = row.clone();
			tempRow.addAll(emptyRowTable2);
			//add this combined row to the final joinTable
			joinTable.add(tempRow);
		}

		//add an empty row to the rows in table2 that did not match
		for (Row row: table2Rows) {
			Row tempRow = emptyRowTable1.clone();
			tempRow.addAll(row);
			//add this combined row to the final joinTable
			joinTable.add(tempRow);
		}
		return joinTable;
	}

	/**
	 * Return a empty row of the table, with each cell type corresponding
	 * to the definition of its column.
	 *
	 * @param tableName the table for which the empty row must be constructed
	 * @return a empty row of the table, with each cell type corresponding
	 * to the definition of its column.
	 */
	Row addEmptyCells(Table tableName) {

		Row emptyRowTable = new Row();

		for (Boolean def: tableName.getDefinition()) { //check each definition
			if (def) { //add an empty NumberCell if the definition is Integer
				emptyRowTable.add(new NumberCell((Integer) null));
			} else { //add an empty StringCell if the definition is String
				emptyRowTable.add(new StringCell(null));
			}
		}
		return emptyRowTable;
	}

	/**
	 * Takes a String consisting of
	 * <code>Table1(col) references Table2(col2)</code>,
	 * and checks to see if the cell at <code>col</code> of each Row in Table1
	 * occurs at least once in the <code>col2</code> of the Table2<br/>
	 * <br/>
	 * Note: this is not a reflexive check.
	 *
	 * @param s A string of the form
	 * <code>Table1(col) references Table2(col2)</code>
	 *

	 * @throws ColumnIndexOutOfBoundsException If either <code>col<code>
	 * or <code>col2</code> aren't columns in <code>Table1</code> and
	 * <code>Table1</code>, respectively.
	 * @throws NoSuchTableException if given name for <code>Table1</code> or
	 *             <code>Table2</code> don't match any Table in Database.
	 */
	void check(String[] s) throws ColumnIndexOutOfBoundsException,
	NoSuchTableException {

		//index 0 = firstTable; 1 = relevant Column
		String[] param1 = s[0].split("\\(|\\)");
		//index 0 = secondTable; 1 = relevant Column
		String[] param2 = s[2].split("\\(|\\)");
		//param[1] is references

		Table t1 = getTable(param1[0].trim());
		Table t2 = getTable(param2[0].trim());

		Table unReferenced = new Table(t1.getDefinition());
		if (!param1[1].matches("^\\d+$") || !param2[1].matches("^\\d+$")) {
			unReferenced.addAll(check(t1, t2,
					t1.getColumnNumber(param1[1].trim()),
					t2.getColumnNumber(param2[1].trim())));
		} else {
			unReferenced.addAll(check(t1, t2,
					Integer.parseInt(param1[1].trim()),
					Integer.parseInt(param2[1].trim())));
		}

		//Prints out the result
		if (unReferenced.size() == 0) { //if all there are no unreferenced rows
			System.out.println("Yes: " + s[0] + " references " + s[2]);
		} else { //if there are, print them
			System.out.print("These rows in " + s[0] + " do not reference "
					+ s[2] + ":\n" + unReferenced.toString());
		}
	}

	/**
	 * Finds all the Rows in <code>t1</code> whose cells at <code>col1</code>
	 * don't match any of the cells at <code>col2</code> of <code>t2</code>.
	 * Where <code>col1</code> and <code>col2</code> are the column numbers
	 * for <code>t1</code> and <code>t2</code>, respectively.<br/>
	 * <br/>
	 * Note: this is not a reflexive check.
	 *
	 * @param t1 The table whose column is referencing another Table.
	 * @param t2 The table whose column is being referenced.
	 * @param col1 The column that is referencing another column.
	 * @param col2 The column that is being referenced.
	 *
	 * @return All the Rows of Table1 whose cell at <code>col</code> don't
	 * match the cell at <code>col2</code> of any Row in Table2.
	 *
	 * @throws ColumnIndexOutOfBoundsException If either <code>col</code>
	 * or <code>col2</code> aren't columns in <code>Table1</code> and
	 * <code>Table1</code>, respectively.
	 */
	List<Row> check(final Table t1, final Table t2,
			final int col1, final int col2)
					throws ColumnIndexOutOfBoundsException {
		//assume all rows in t1 have no match in t2
		List<Row> unmatchedRows = new LinkedList<Row>(t1.clone().getAllRows());

		//for all rows in r1
		for (Row r1: t1.getAllRows()) {
			Cell c = r1.getCell(col1); //backing up for efficiency
			for (Row r2: t2.getAllRows()) {
				//if the cell matches any row's cell at(col2)
				if (c.equals(r2.getCell(col2))) {
					unmatchedRows.remove(r1); //remove it
					break;
				}
			}
		}

		//return whatever is left, i.e. unmatched rows
		return unmatchedRows;
	}



	/**
	 * Takes in a String in the form of "tableName,columnNumber" and returns a
	 * copy of the {@link Table} <code>tableName</code> sorted according to
	 * the <code>columnNumber</code>. If no such table exists, then a
	 * {@link NoSuchTableException} is thrown.
	 *
	 * @param expr the orderBy command arguments
	 * @return a copy of the {@link Table} <code>tableName</code> sorted
	 * according to <code>colNum</code>
	 * @throws NoSuchTableException if the table doesn't exist in the database
	 * @throws ColumnIndexOutOfBoundsException if given column number is out of
	 * bounds
	 */
	Table orderBy(String expr) throws NoSuchTableException,
	ColumnIndexOutOfBoundsException {
		String[] sortCmd = expr.split(",");

		Table t = getTable(sortCmd[0].trim()).clone();

		int colNumber = sortCmd[1].trim().matches("[0-9]+")
				? Integer.parseInt(sortCmd[1].trim())
						: t.getColumnNumber(sortCmd[1].trim());

				return (t.sort(colNumber));
	}

	/**
	 * Clears the database.
	 */
	void clear() {
		database.clear();
	}

	/**
	 * Removes {@link Table} <code>tableName</code> from database.
	 *
	 * @param tableName the name which the Table was stored under
	 * @return the removed Table, or <code>null</code> if it doesn't exist
	 */
	Table remove(String tableName) {
		return database.remove(tableName);
	}

	/**
	 * Define a {@link Table} and adds it to the database under the given name.
	 * The definition is derived from <code>command</code>, and is a List of
	 * boolean used to create a Table, it's <code>true</code> if the given
	 * command is <code>int</code> and is <code>false</code> if it's
	 * <code>string</code>.
	 *
	 * @param command the name of the given table and a list containing either
	 * "String" or "Int"
	 */
	void define(String[] command) {
		List<Boolean> definition = new ArrayList<Boolean>();

		//due to structure of the program, command[i] cannot be anything
		//but String or Int, therefore no need to account for it.
		for (int i = 2; i < command.length; i++) {
			definition.add(command[i].equalsIgnoreCase("int") ? true : false);
		}

		//once again, no validity check necessary , since no invalid
		//input can get this far
		database.put(command[1], new Table(definition));

		System.out.println(command[1] + " is now defined.");
	}
}
