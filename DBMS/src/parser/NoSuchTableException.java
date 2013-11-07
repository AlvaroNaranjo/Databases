/**
 *
 */
package parser;

/**
 * This exception is thrown when ever the user asks for a table that has not
 * been added to the Database
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 *
 */
public class NoSuchTableException extends Exception {

    /**
     * Since Exception is Serializable
     */
    private static final long serialVersionUID = 658079738602580543L;

    /**
     * Create a new Exception
     *
     * @param tableName The name of the table that threw this Exception
     */
    public NoSuchTableException(String tableName) {
        super("The table \"" + tableName + "\" doesn't exist.");
    }
}