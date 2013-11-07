
package elements;

/**
 * Cell class, defines the minimum methods each Cell subclass should have. Used
 * to store an item of the type String, Integer or Class
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 *
 */
public abstract class Cell implements Comparable<Cell>, Cloneable {

    /*(non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public abstract boolean equals(Object obj);

    /*(non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public abstract int hashCode();


    /*(non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public abstract String toString();

    /**
     * Returns whether or not this instance of a Cell is a NumberCell or not.
     *
     * @return whether or not this Cell is a number cell.
     */
    public boolean isNumberCell() {
        return this instanceof NumberCell;
    }

    /*(non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Cell c) {

    	//if both are NumberCells, call the NumberCell's compare to methods
    	if (isNumberCell() && c.isNumberCell()) {
    		return ((NumberCell) this).getCell()
                        - ((NumberCell) c).getCell();
    	}

    	//if neither, or only one, of them is a NumberCell, compare as String
    	return this.toString().compareTo(c.toString());
    }
}