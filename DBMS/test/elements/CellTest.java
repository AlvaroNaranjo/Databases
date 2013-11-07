/**
 *
 */
package elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Prajjwal Bhandari
 *
 */
public class CellTest {
	Cell c, c1;
	StringCell sc;
	NumberCell nc;

	/**
	 * @throws java.lang.Exception possible exception thrown
	 */
	@Before
	public void setUp() throws Exception {
		sc = new StringCell("pi");
		nc = new NumberCell(5);

		c = new StringCell(nc.toString());
		c1 = new NumberCell(51);
	}

	/**
	 * @throws java.lang.Exception possible exception thrown
	 */
	@After
	public void tearDown() throws Exception {
		//resetting any changes that might have been done previously
		sc = null;
		nc = null;
		c = null;
		c1 = null;
	}
	/**
	 * Test method for {@link elements.Cell#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertTrue(nc.equals(new NumberCell(nc.getCell())));

		assertFalse(nc.equals(c));

		assertTrue(c.equals(new StringCell(((StringCell) c).getCell())));

		assertTrue(sc.equals(new StringCell(sc.getCell())));

                assertTrue(new StringCell(null).equals(new StringCell(null)));

		assertTrue(c1.equals(new NumberCell(((NumberCell) c1).getCell())));
	}

	/**
	 * Test method for {@link elements.Cell#toString()}.
	 */
	@Test
	public void testToString() {
		StringCell test1 = new StringCell(null);
		NumberCell test2 = new NumberCell((Integer) null);

		//Checking StringCell when cell is null
		assertEquals("", test1.toString());

		//Checking NumberCell when cell is null
		assertEquals("", test2.toString());

		//Checking StringCell
		assertEquals("pi", sc.toString());

		//checking numberCell
		assertEquals("5", nc.toString());
	}

	/**
	 * Test method for {@link elements.Cell#isNumberCell()}.
	 */
	@Test
	public void testIsNumberCell() {
		assertTrue(nc.isNumberCell()); //trivial case
		assertTrue(c1.isNumberCell());

		assertFalse(sc.isNumberCell()); //trivial case
		assertFalse(c.isNumberCell()); //for Cell types

		//checking that it can handle things that were once numberCells properly
		c1 = new StringCell("");
		assertFalse(c1.isNumberCell());
	}

	/**
	 * Test method for {@link elements.Cell#compareTo(elements.Cell)}.
	 */
	@Test
	public void testCompareTo() {
		//comparing two NumberCells does an integer comparison
		assertEquals(-395, nc.compareTo(new NumberCell(400)));

		//compare a NumberCell = NumberCell to a Cell = NumberCell
		assertEquals(-46, nc.compareTo(c1));

		//comparing two StringCells works properly
		assertTrue(sc.compareTo(new StringCell("fill")) > 0);

		//compare a StringCell = StringCell to a Cell = StringCell
		assertTrue(sc.compareTo(c) > 0);

		//checking that a String comparison is done when one of the comparators
		//is a NumberCell and the other one a StringCell
		assertTrue(new StringCell("5").compareTo(new NumberCell(15)) != 10);
	}
}
