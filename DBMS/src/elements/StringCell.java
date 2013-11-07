/**
 *
 */
package elements;

/**
 * An implementation of the Cell class that uses a String to Store items.
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 */
public final class StringCell extends Cell {

    /**
     * To store the value of this cell.
     */
    private String cell;

    /**
     * Creates a StringCell Object having the value of the given
     * <code>item</code>.
     *
     * @param item the value of this cell.
     */
    public StringCell(final String item) {
        this.cell = item;
    }

    /**
     * Gets the current value of this cell.
     *
     * @return the current Cell
     */
    public String getCell() {
        return cell;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        return prime * result + ((cell == null) ? 0 : cell.hashCode());
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
    	if (cell == obj) {
    		return true;
    	}

 	   if (obj == null || !(obj instanceof StringCell)) {
		   return false;
 	   }

        if (cell == null) {
            return ((StringCell) obj).cell == null;
        }

       return cell.equals(((StringCell) obj).cell);
    }

    /*
     *  (non-Javadoc)
     * @see elements.Cell#toString()
     */
    @Override
    public String toString() {
    	if (cell == null) {
    		return "";
    	}
        return cell;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public StringCell clone() {
        return new StringCell(cell);
    }
}