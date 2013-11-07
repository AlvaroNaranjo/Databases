
package elements;

/**
 * An implementation of the Cell class that uses an Integer to Store items.
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 */
public final class NumberCell extends Cell {
    /**
     * To store the value of this cell.
     */
    //deliberately using an Integer to allow for Object operations
    private Integer cell;

    /**
     * Sets the value of this NumberCell.
     *
     * @param item the value of this cell.
     */
    public NumberCell(final Integer item) {
        this.cell = item;
    }

    /**
     * Sets the value of this NumberCell by parsing the given String into an
     * Integer.
     *
     * @param item the value of this cell.
     *
     * @throws NumberFormatException if the given String cannot be converted to
     * an Integer.
     */
    public NumberCell(final String item) {
    	//avoiding possible nullPointer on Integer#parseInt(String)
        this((item == null) ? null : Integer.parseInt(item));
    }

   /**
    * Gets the value of <code>cell</code>.
    *
    * @return the current Cell
    */
   public int getCell() {
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

	   if (obj == null || !(obj instanceof NumberCell)) {
		   return false;
	   }

	   if (cell == null) {
            return ((NumberCell) obj).cell == null;
	   }

	   return cell.equals((((NumberCell) obj).cell));
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
       return "" + cell;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#clone()
    */
   @Override
   public NumberCell clone() {
       return new NumberCell(cell);
   }
}