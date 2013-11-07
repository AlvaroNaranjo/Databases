package parser;

/**
 * A new Exception thrown whenever the column asked for is out of bounds
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 *
 */
public class ColumnIndexOutOfBoundsException extends Exception {

    /**
     * Exception is serialised
     */
    private static final long serialVersionUID = -2625240016179058005L;

    /**
     * A new Exception that is thrown whenever the user asks for a column that
     * is out of bounds
     *
     * @param message the index which is out of bounds
     */
    public ColumnIndexOutOfBoundsException(String message) {
        super("The column \"" + message + "\" cannot be reached.\n"
                + "Aborting current action");
    }
}