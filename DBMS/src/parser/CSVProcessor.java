package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import elements.Table;

/**
 * Reads the user input and parses accordingly.
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 *
 */
public class CSVProcessor {

	/**
	 * A database to keep track of the Tables.
	 */
	private static Database database = new Database();
	/**
	 * Keeps tracks of whether or not the program should run.
	 */
	private static boolean canContinue = true;

	/**
	 * The main method for this class.
	 *
	 * @param args the command line arguments
	 *
	 */
	public static void main(String[] args) {
		//Read input
		java.util.Scanner scan = new Scanner(System.in);
		do {
			System.out.print("Please enter one of the commands: ");
			parseText(scan.nextLine().trim());
		} while (canContinue);
		System.out.println("Exiting the program.");
		scan.close();

	}

	/**
	 * Does any cleanUp required before exiting, sets the canContinue to
	 * false and flags the program for termination.
	 */
	private static void exit() {
		canContinue = false;
	}

	/**
	 * Parses
	 * <code>input</code> and reacts according to command in
	 * <code>input</code>
	 *
	 * @param input The string input by the user
	 */
	private static void parseText(String input) {
		String[] command = input.split(" "); //Split input into commands

		//check input entered by user follows extended syntax rules.
		try {
			if (!isValid(input)) {
				System.out.println("You've entered an illegal "
						+ "character, please try again.");
			} else if (isLoad(command)) {
				if (command.length == 4) {
					//if [header] not given assume no header
					database.load(false, command[1].trim(), command[3].trim());
				} else {
					database.load(true, command[2].trim(), command[4].trim());
				}
			} else if (isCheck(input)) {
				database.check(Arrays.copyOfRange(command, 1, command.length));
			} else if (isExit(input)) {
				exit();
			} else if (isSave(command)) {
				database.save(command[1], command[2]);
			} else if (isPrint(command)) {
				database.print(command[1]);
			} else if (isDefine(command)) {
				database.define(command);
			} else if (isAssign(input)) { //"words = words(anything)"
				String[] assignCmd = input.split("=", 2);
				//parseAssign (new tablename, assign command)
				parseAssign(assignCmd[0], assignCmd[1]);
			} else {
				System.out.println("The command: \"" + input
						+ "\" is not valid, Please try again.");
			}
			//Catch any possible exceptions; give meaningful message
		} catch (NoSuchTableException e) {
			System.out.println(e.getMessage());
		} catch (ColumnIndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("You just threw a " + e.getClass()
					+ ". Sorry about that.");
		}
	}

	/**
	 * Parses
	 * <code>input</code> and reacts according the assignment command in
	 * <code>input</code>
	 *
	 * @param tableName The name of the table to change
	 * @param cmd The assignment command which specifies what to do with the
	 * table
	 * @throws NoSuchTableException if any of the commands can't find the
	 * required Table
	 * @throws ColumnIndexOutOfBoundsException if any of the commands have
	 * been given a column index that is out of bounds
	 */
	private static void parseAssign(String tableName, String cmd)
			throws NoSuchTableException, ColumnIndexOutOfBoundsException {
		//extracting assign command parameters from cmd
		String[] splitCmd = cmd.trim().split("\\(|\\)");

		if (splitCmd.length != 2) {
			System.out.println("There are too many brackets, "
					+ "Please try again");
			return;
		}

		Table t;
		//Check assign parameters and call required methods
		if (isRows(splitCmd)) {
			t = database.rowsCheck(splitCmd[1]);
		} else if (isColumns(splitCmd)) {
			t = database.columnsCheck(splitCmd[1]);
		} else if (isProduct(splitCmd)) {
			t = database.product(splitCmd[1]);
		} else if (isJoin(splitCmd)) {
			t = database.join(splitCmd[1]);
		} else if (isOrderBy(splitCmd)) {
			t = database.orderBy(splitCmd[1]);
		} else if (isNaturalJoin(splitCmd)) {
			t = database.naturalJoin(splitCmd[1], false);
		} else if (isOuterJoin(splitCmd)) {
			t = database.outerJoin(splitCmd[1]);
		} else if (isNaturalOuterJoin(splitCmd)) {
			t = database.naturalJoin(splitCmd[1], true);
		} else {
			t = new Table(new ArrayList<Boolean>());
			System.out.println("Invalid command, please try again.");
		}

		if (t != null && t.size() != 0) { //if t is Table of right size
			database.add(tableName.trim(), t);
		} else {
			database.remove(tableName);
			System.out.println(tableName + "is an empty table."
					+ "\nIt has not been added to the database.");
		}
	}

	/**
	 * Checks the user input to see if it is a valid command, i.e. one
	 * without any \r, \n or \f characters
	 *
	 * @param cmd the user input
	 * @return <code>true</code> if user input does not have any \r,\n,\f or
	 * other non-standard characters, <code>false</code> if it does.
	 */
	private static boolean isValid(String cmd) {
		return !cmd.matches(".*(\\r|\\n|\\f).*");
	}

	/**
	 * Checks the user input to see if it is an exit command, i.e. of the
	 * form <code>exit</code>
	 *
	 * @param cmd the user input split along spaces
	 * @return whether the user input is of the form <code>exit</code>
	 */
	private static boolean isExit(String cmd) {
		return cmd.equalsIgnoreCase("exit");
	}

	/**
	 * Checks the user input to see if it is a save command, i.e. of the
	 * form <code>save tableName fileName</code>
	 *
	 * @param cmd the user input split along spaces
	 * @return whether the user input is of the form
	 * <code>save tableName fileName</code>
	 */
	private static boolean isSave(String[] cmd) {
		return cmd.length == 3 && cmd[0].equalsIgnoreCase("save")
				&& cmd[2].matches("[A-Za-z.]+");
	}

	/**
	 * Checks the given command to see if it's a valid check command, i.e.
	 * of the form
	 * <code>check tableName(column) reference tableName(column)</code>.
	 * The given <code>column</code> can be either a digit or a word.
	 *
	 * @param cmd the user input command
	 * @return whether or not the given cmd is of the form
	 * <code>check tableName(column) reference tableName(column)</code>
	 */
	private static boolean isCheck(String cmd) {
		return cmd.matches("check\\s+[A-Za-z]+\\s*\\(\\s*[A-Za-z0-9]+\\s*\\)"
				+ "\\s+references\\s+[A-Za-z]+\\s*\\(\\s*[A-Za-z0-9]+\\s*\\)");
	}

	/**
	 * Checks the user input to see if it is a define command, i.e. of the
	 * form <code>define tableName (string|int), (string|int) ... </code>
	 *
	 * @param cmd the user input split along spaces
	 * @return whether the user input is of the form
	 * <code>define tableName (string|int), (string|int) ... </code>
	 */
	private static boolean isDefine(String[] cmd) {
		//return false if there's under 3 arguments, cmd[0] isn't define
		//the given table name has non-alphabetic characters
		//or the column definitions are not given between parenthesis
		if (cmd.length < 3 || !cmd[0].trim().equalsIgnoreCase("define")
				|| !cmd[1].trim().matches("[A-Za-z]+")
				|| !cmd[2].contains("(")
				|| !cmd[cmd.length - 1].contains(")")) {
			return false;
		}
		//if there is a non-"String" or non-"Int" item, return false.
		for (int i = 2; i < cmd.length; i++) {

			//deal with (,), and , unwanted chars
			cmd[i] = cmd[i].replaceAll("\\(|\\)|,", "");

			//this program is case-insensitive
			if (!cmd[i].equalsIgnoreCase("string")
					&& !cmd[i].equalsIgnoreCase("int")) {
				return false;
			}
		}

		return true;

	}

	/**
	 * Checks the user input to see if it is a print command, i.e. of the
	 * form <code>print tableName</code>
	 *
	 * @param cmd the user input split along spaces
	 * @return whether the user input is of the form
	 * <code>save tableName</code>
	 */
	private static boolean isPrint(String[] cmd) {
		return cmd.length == 2
				&& cmd[0].trim().equalsIgnoreCase("print");
	}

	/**
	 * Checks the user input to see if it is an assign command, i.e. of
	 * the form <code>[outer] join(tableName(1),tableName(2), col(1a),
	 * col(1b),..., col(na),col(nb))</code>
	 *
	 * @param cmd the user input split along spaces
	 * @return whether or not cmd is of the form <code>[outer] join(
	 * tableName(1),tableName(2), col(1a), col(1b),..., col(na),col(nb))
	 * </code>
	 */
	private static boolean isOuterJoin(String[] cmd) {
		//"outer join" bundled, need to part it like: "outer", "join"
		String[] cmdSignature = cmd[0].split(" ");
		String[] specifications = {cmdSignature[1], cmd[1]};
		return cmdSignature[0].trim().equalsIgnoreCase("outer")
				&& isJoin(specifications);
	}

	/**
	 * Checks the user input to see if it is an assign command, i.e. of
	 * the form <code>[outer] naturaljoin(tableName(1),tableName(2))</code>
	 *
	 * @param cmd the user input split along spaces
	 * @return whether or not cmd is of the form <code>[outer] naturaljoin(
	 * tableName(1),tableName(2)) </code>
	 */
	private static boolean isNaturalOuterJoin(String[] cmd) {
		//"outer join" bundled, need to part it like: "outer", "join"
		String[] cmdSignature = cmd[0].split(" ");
		String[] specifications = {cmdSignature[1], cmd[1]};
		return cmdSignature[0].trim().equalsIgnoreCase("outer")
				&& isNaturalJoin(specifications);
	}

	/**
	 * Checks if user input is an assign command, i.e. of the form
	 * <code>varName = assignCmd(assignParam)</code> or
	 * <code>varName = [options] assignCmd(assignParam)</code>
	 *
	 * @param cmd the user input split along spaces
	 * @return whether the user input is of the form
	 * <code>varName = [options] assignCmd(assignParam)</code>
	 */
	private static boolean isAssign(String cmd) {
		return cmd.matches(
				"^\\s*[A-Za-z]+\\s*=\\s*[A-Za-z\\s]+\\s*\\(.*\\)\\s*$");
	}
	/**
	 * Checks the assign command to see if it's a valid load command, i.e.
	 * of the form <code>load [headed] <name> from <filename></code>
	 * and following BNF syntax rules.
	 *
	 * @param cmd the assign command where index[0] is the command keyword
	 * and elements at other indices are loading specifications.
	 * @return whether or not the given command is of the form
	 * <code>load [headed] <name> from <filename></code>
	 */
	private static boolean isLoad(final String[] cmd) {

		if (cmd.length == 4) {
			//check elements given in command are valid
			return cmd[0].trim().equalsIgnoreCase("load")
					&& cmd[1].matches("^\\s*[A-Za-z]+\\s*$")
					&& cmd[2].matches("^\\s*from\\s*$")
					&& cmd[3].matches("^\\s*[A-Za-z.]+\\s*$");
		}

		if (cmd.length == 5) {
			//check elements given in command are valid
			return cmd[0].trim().equalsIgnoreCase("load")
					&& cmd[1].matches("^\\s*headed\\s*$")
					&& cmd[2].matches("^\\s*[A-Za-z]+\\s*$")
					&& cmd[3].matches("^\\s*from\\s*$")
					&& cmd[4].matches("^\\s*[A-Za-z.]+\\s*$");
		}

		return false;
	}

	/**
	 * Checks the assign command to see if it's a valid rows command, i.e.
	 * of the form
	 * <code>rows(tableName,colNumber,comparator,value)</code>
	 *
	 * @param cmd assign command where index[0] is the command and index[1]
	 * is the parameters
	 * @return whether or not the given cmd is of the form
	 * <code>rows(tableName,colNumber,comparator,value)</code>
	 */
	private static boolean isRows(String[] cmd) {
		return cmd[0].trim().equalsIgnoreCase("rows")
				&& cmd[1].matches("^\\s*[A-Za-z]+\\s*"
						+ ",\\s*([0-9]+|[A-Za-z]+)\\s*"
						+ ",\\s*(<|<=|==|>=|>)\\s*" + ",\\s*[0-9]+\\s*$");
	}

	/**
	 * Checks the assign command to see if it's a valid columns command,
	 * i.e. of the form
	 * <code>columns(tableName,col(1),col(2),...,col(n))</code>, where
	 * col(i) is the i-th column number provided by the user.
	 *
	 * @param cmd assign command where index[0] is the command and index[1]
	 * is the parameters
	 * @return whether or not the given cmd is of the form
	 * <code>columns(tableName,col(1),col(2),...,col(n))</code>
	 */
	private static boolean isColumns(String[] cmd) {
		return cmd[0].trim().equalsIgnoreCase("columns")
				&& (cmd[1].matches("^\\s*[A-Za-z]+\\s*"
						+ "(,\\s*[0-9]+\\s*)+$")
						|| cmd[1].matches("^\\s*[A-Za-z]+\\s*"
								+ "(,\\s*[A-Za-z]+\\s*)+$"));

	}

	/**
	 * Checks the assign command to see if it's a valid columns command,
	 * i.e. of the form
	 * <code>product(tableName(1),tableName(2)</code> where tableName(1) and
	 * tableName(2) are the names of the first and second tables.
	 *
	 * @param cmd assign command where index[0] is the command and index[1]
	 * is the parameters
	 * @return whether or not the given cmd is of the form
	 * <code>join(tableName(1),tableName(2)</code>.
	 */
	private static boolean isProduct(String[] cmd) {
		return cmd[0].trim().equalsIgnoreCase("product")
				&& cmd[1].matches("^\\s*[A-Za-z]+\\s*"
						+ ",\\s*[A-Za-z]+\\s*$");
	}

	/**
	 * Checks the assign command to see if it's a valid columns command,
	 * i.e. of the form
	 * <code>join(tableName(1),tableName(2), col(1a),col(1b),...,
	 * col(na),col(nb))</code>. Where tableName(1) and tableName(2) are the
	 * names of the first and second tables that are to be joined and
	 * <col(ia),col(ib)> are the first, second column numbers, respectively,
	 * of the ith column pair provided by user. The command list must have
	 * an even number of columns for this to return true
	 *
	 * @param cmd assign command where index[0] is the command and index[1]
	 * is the parameters
	 * @return whether or not the given cmd is of the form
	 * <code>join(tableName(1),tableName (2), col(1a),col(1b),...,
	 * 			col(na),col(nb))</code>.
	 */
	private static boolean isJoin(String[] cmd) {
		//accept cmd: [<name>, <name>, <intpairs>]
		//or [<name>, <name>, <namepairs>]
		return cmd[0].trim().equalsIgnoreCase("join")
				&& (cmd[1].matches("^\\s*[A-Za-z]+\\s*"
						+ ",\\s*[A-Za-z]+\\s*"
						+ "(,\\s*[0-9]+\\s*,\\s*[0-9]+\\s*)+$")
						|| cmd[1].matches("^\\s*[A-Za-z]+\\s*"
								+ ",\\s*[A-Za-z]+\\s*"
								+ "(,\\s*[A-Za-z]+\\s*,\\s*[A-Za-z]+\\s*)+$"));
	}

	/**
	 * Checks the assign command to see if it's a valid orderBy command,
	 * i.e. of the form
	 * <code>orderBy(tableName,columnNumber)</code>
	 *
	 * @param cmd assign command where index[0] is the command and index[1]
	 * is the parameters
	 * @return whether or not the given cmd is of the form
	 * <code>orderBy(tableName,columnNumber)</code>
	 */
	private static boolean isOrderBy(String[] cmd) {
		return cmd[0].trim().equalsIgnoreCase("orderBy")
				&& cmd[1].matches("^\\s*[A-Za-z]+\\s*,\\s*"
						+ "([0-9]+|[A-Za-z]+)\\s*$");
	}

	/**
	 * Checks the assign command to see if it's a valid columns command
	 * i.e. of the form
	 * <code>naturaljoin(tableName(1),tableName(2)) </code>. Where
	 * tableName(1) and tableName(2) are the names of the first and second
	 * tables that are to be joined.
	 *
	 * @param cmd assign command where index[0] is the command and index[1]
	 * 				is the parameters
	 * @return whether or not the given cmd is of the form
	 * <code>naturaljoin(tableName(1),tableName (2))</code>.
	 */
	private static boolean isNaturalJoin(String[] cmd) {
		//accept cmd: [<name>, <name>]
		return cmd[0].trim().equalsIgnoreCase("naturaljoin")
				&& cmd[1].matches("^\\s*[A-Za-z]+\\s*"
						+ ",\\s*[A-Za-z]+\\s*$");
	}
}