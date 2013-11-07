package elements;

import java.util.Comparator;

/**
 * Compares two rows of a table on a given index.
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 */
public class TableComparator implements Comparator<Row> {

    /**
     * Stores which column index to sort by.
     */
    private final int indexToCompare;

    /**
     * Instantiates TableComparator and sets the indexToCompare.
     *
     * @param index the column index to compare
     */
    public TableComparator(final int index) {
        indexToCompare = index;
    }

    /**
     * This is used to sort the Tables, in Database#orderBy(String).
     *
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare(final Row o1, final Row o2) {
        //work around to avoid thrown exception
        return o1.getRow().get(indexToCompare).
                compareTo(o2.getRow().get(indexToCompare));
    }
}