/**
 *
 */
package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for CSVProcessor.java
 *
 * @version 2
 *
 * @author Prajjwal Bhandari
 * @author Hardarshan Kahlon
 * @author Ansar Butt
 * @author Alvaro Naranjo
 *
 */
public class CSVProcessorTest {

    PrintStream ps = new PrintStream(System.out);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     *
     */
    @Before
    public void setUp() {
    	System.setOut(new PrintStream(out));
    }

    /**
     *
     */
    @After
    public void tearDown() {
        out.reset();
    }

    /**
     * Test method for CSVProcessor#exit().
     */
    @Test
    public void testExit() {
        CSVProcessor.exit();
        assertEquals(CSVProcessor.canContinue, false);
    }

    /**
     * Test method for CSVProcessor#parseText(java.lang.String).
     *
     * case: invalid character throws a message
     */
    @Test
    public void testParseText() {
        CSVProcessor.parseText("tera\nasdsad");
        assertEquals("You've entered an illegal character, please try again.\n",
                out.toString());
    }

    /**
     * Test method for
     * CSVProcessor#parseAssign(java.lang.String, java.lang.String).
     * case: there are too many brackets in assign expression
     *
     * @throws ColumnIndexOutOfBoundsException if colIndex is out of bounds
     * @throws NoSuchTableException if table does not exist
     */
    @Test
    public void testParseAssign()
    		throws NoSuchTableException, ColumnIndexOutOfBoundsException {
        CSVProcessor.parseAssign("pi", "(asd(ads))");
        assertEquals("There are too many brackets, Please try again\n",
                out.toString());
    }

    /**
     * Test method for CSVProcessor#isValid(java.lang.String).
     */
    @Test
    public void testIsValid() {
        //all valid items
        assertTrue(CSVProcessor.isValid("asdasd"));

        //has a \r character, is not valid
        assertFalse(CSVProcessor.isValid("asd\rtasd"));
    }

    /**
     * Test method for CSVProcessor#isExit(java.lang.String).
     */
    @Test
    public void testIsExit() {
        //should only return true if is "exit"
        assertTrue(CSVProcessor.isExit("exit"));

        //not "exit"
        assertFalse(CSVProcessor.isExit("notExit"));
    }

    /**
     * Test method for CSVProcessor#isSave(java.lang.String[]).
     */
    @Test
    public void testIsSave() {
        //accepts proper declaration
        assertTrue(CSVProcessor.isSave(new String[]
        		{"save", "myT", "file.txt"}));

        //rejects b/c invalid number of declarations
        assertFalse(CSVProcessor.isSave(new String[]{"save", "table"}));

        //invalid save declaration
        assertFalse(CSVProcessor.isSave(new String[]{"saib", "t", "f"}));

        //check invalid file name declaration.
        //No check of invalid name declaration already dealt with by processor
        assertFalse(CSVProcessor.isSave(new String[]{"save", "t", "f1"}));

    }

    /**
     * Test method for CSVProcessor#isPrint(java.lang.String[]).
     */
    @Test
    public void testIsPrint() {
        //print should be valid
        assertTrue(CSVProcessor.isPrint(new String[]{"print", "foo"}));

        //print with less than 2 elements
        assertFalse(CSVProcessor.isPrint(new String[]{"print"}));

        //print with more than 2 elements
        assertFalse(CSVProcessor.isPrint(new String[]
        		{"print", "foo", "bar", "baz"}));

        //no print if bad print declaration
        assertFalse(CSVProcessor.isPrint(new String[]{"foo", "bar"}));

        //no print if bad name declaration
        assertFalse(CSVProcessor.isPrint(new String[]{"foo", "bar1"}));

        //no print b/c invalid number of declarations
        assertFalse(CSVProcessor.isPrint(new String[]{"print", "t", "f"}));
    }

    /**
     * Test method for CSVProcessor#isAssign(java.lang.String).
     */
    @Test
    public void testIsAssign() {
        //valid assign command
        assertTrue(CSVProcessor.isAssign("pi=fi(bi)"));

        //valid assign command with optionals
        assertTrue(CSVProcessor.isAssign("pi = outer join (somethign)"));

        //something is out of the brackets
        assertFalse(CSVProcessor.isAssign("pi=fi()bi"));

        //bracket is only partially open
        assertFalse(CSVProcessor.isAssign("pi=fi(bi"));
    }

    /**
     * Test method for CSVProcessor#isLoad(java.lang.String[]).
     */
    @Test
    public void testIsLoad() {
    	//valid load command without header declaration
    	assertTrue(CSVProcessor.isLoad(new String[]
    			{" load", " table ", "from ", "file"}));

    	//valid load command with true header declaration
    	assertTrue(CSVProcessor.isLoad(new String[]
    			{" load", "headed", " table ", "from ", "file"}));

    	//invalid load declaration
    	assertFalse(CSVProcessor.isLoad(new String[]
    			{" loading", " table ", "from ", "file"}));

    	//invalid table name declaration
    	assertFalse(CSVProcessor.isLoad(new String[]
    			{" load", " table1 ", "from ", "file"}));

    	//invalid from declaration
    	assertFalse(CSVProcessor.isLoad(new String[]
    			{" load", " table ", "of ", "file"}));

    	//invalid file name declaration
    	assertFalse(CSVProcessor.isLoad(new String[]
    			{" load", " table ", "from ", "home\file.txt"}));

    	//command with invalid number of declarations
    	assertFalse(CSVProcessor.isLoad(new String[]
    			{" load", " table ", "from ", "file", "NOW!"}));
    }

    /**
     * Test method for CSVProcessor#isRows(java.lang.String[]).
     */
    @Test
    public void testIsRows() {
        //valid
        assertTrue(CSVProcessor.isRows(new String[]{"rows", "t, 2, >, 6"}));

        //valid column name given
        assertTrue(CSVProcessor.isRows(
        		new String[]{"rows", "table, columnname, <, 4"}));

        //invalid comparator given
        assertFalse(CSVProcessor.isRows(new String[]{"rows", "file, 2, t, 5"}));

        //invalid b/c non integer given
        assertFalse(CSVProcessor.isRows(
        		new String[]{"rows", "file, pi, <, r"}));

        //note: no test for wrong number of commands, because the set-up makes
        //that scenario impossible
    }

    /**
     * Test method for
     * CSVProcessor#isColumns(java.lang.String[]).
     */
    @Test
    public void testIsColumns() {
        //valid int list given
        assertTrue(CSVProcessor.isColumns(new String[]{"columns", "t, 2, 3"}));

        //valid column name list given
        assertTrue(CSVProcessor.isColumns(
        		new String[]{"columns", "t, StudentName, StudentId"}));

        //invalid b/c non integer nor proper column name given
        assertFalse(CSVProcessor.isColumns(
        		new String[]{"columns", "file, 2, <, 4"}));

        //invalid b/c integer and non integer given
        assertFalse(CSVProcessor.isColumns(
        		new String[]{"columns", "file, 2, t "}));

        //note: no test for wrong number of commands, because the set-up makes
        //that scenario impossible
    }

    /**
     * Test method for
     * CSVProcessor#isProduct(java.lang.String[]).
     */
    @Test
    public void testIsProduct() {
        //valid
        assertTrue(CSVProcessor.isProduct(new String[]{"product", "ta, rer"}));

        //invalid table name
        assertFalse(CSVProcessor.isProduct(
        		new String[]{"product", "file, 2er"}));

        //invalid
        assertFalse(CSVProcessor.isProduct(
        		new String[]{"product", "file, 2, <, 4"}));

        //note: no test for wrong number of commands, because the set-up makes
        //that scenario impossible
    }

    /**
     * Test method for CSVProcessor#isJoin(java.lang.String[]).
     */
    @Test
    public void testIsJoin() {
        //valid by int list
        assertTrue(CSVProcessor.isJoin(new String[]{"join", "ta, rer, 4, 3"}));

        //valid by column name list
        assertTrue(CSVProcessor.isJoin(
        		new String[]{"join", "ta, rer, four, three"}));

        //invalid table name
        assertFalse(CSVProcessor.isJoin(new String[]{"join", "file, 2er, 4"}));

        //invalid int in int list
        assertFalse(CSVProcessor.isJoin(new String[]{"join", "file, 2, <, 4"}));

        //note: no test for wrong number of commands, because the set-up makes
        //that scenario impossible
    }

    /**
     * Test method for CSVProcessor#isJoin(java.lang.String[]).
     */
    @Test
    public void testIsNaturalJoin() {
        //valid
        assertTrue(CSVProcessor.isNaturalJoin(new String[]{"naturaljoin",
        "ta, rer"}));

        //invalid
       assertFalse(CSVProcessor.isNaturalJoin(new String[]{"naturaljoin",
        "<, 2"}));
    }

    /**
     * Test method for CSVProcessor#isJoin(java.lang.String[]).
     */
    @Test
    public void testIsOuterJoin() {
        //valid by int list
        assertTrue(CSVProcessor.isOuterJoin(
        		new String[]{"outer join", "ta, rer, 4, 3"}));

        //valid by column name list. Processor hands in "outer join" bundled
        assertTrue(CSVProcessor.isOuterJoin(
        		new String[]{"outer join", "ta, rer, four, three"}));

        //invalid
        assertFalse(CSVProcessor.isOuterJoin(
        		new String[]{"inner join", "ta, rer, 4, 3"}));
    }

    /**
     * Test method for CSVProcessor#isJoin(java.lang.String[]).
     */
    @Test
    public void testIsNaturalOuterJoin() {
        //valid processor hands in "outer naturaljoin" bundled to this function
        assertTrue(CSVProcessor.isNaturalOuterJoin(
        		new String[]{"outer naturaljoin", "ta, rer"}));

        //invalid
        assertFalse(CSVProcessor.isNaturalOuterJoin(
        			new String[]{"inner naturaljoin", "ta, rer"}));
    }


    /**
     * Test method for
     * CSVProcessor#isOrderBy(java.lang.String[]).
     */
    @Test
    public void testIsOrderBy() {
        //valid by column index
        assertTrue(CSVProcessor.isOrderBy(new String[]{"orderBy", "ta, 1"}));

        //valid by column name
        assertTrue(CSVProcessor.isOrderBy(new String[]{"orderBy", "ta, one"}));

        //invalid
        assertFalse(CSVProcessor.isOrderBy(
        		new String[]{"orderBy", "table, 2er"}));

        //invalid
        assertFalse(CSVProcessor.isOrderBy(
        		new String[]{"orderBy", "table, 2, <, 4"}));

        //note: no test for wrong number of commands, because the set-up makes
        //that scenario impossible
    }

    /**
     * Test method for
     * CSVProcessor#isDefine(java.lang.String[]).
     */
    @Test
    public void testIsDefine() {
    	//trivial test
    	assertTrue(CSVProcessor.isDefine(new String[]
    			{"define", "asd", "(String", "Int)"}));

    	//command not define
    	assertFalse(CSVProcessor.isDefine(new String[]
    			{"dfine", "asd", "(String", "Int)"}));

    	//Table name has invalid character
    	assertFalse(CSVProcessor.isDefine(new String[]
    			{"define", "asd1", "(String", "Int)"}));

    	//no parameter types
    	assertFalse(CSVProcessor.isDefine(new String[]
    			{"define", "asd"}));

    	//one item has wrong parameter type
    	assertFalse(CSVProcessor.isDefine(new String[]
    			{"define", "asd", "(Strng", "Int)"}));
    }

    /**
     * Test method for CSVProcessor#isCheck(java.lang.String)
     */
    @Test
    public void testIsCheck() {
    	//checking for tableName(columnHeaderName)
    	assertTrue(CSVProcessor.isCheck("check tabe(pi) references tale(yu)"));

    	//checking for tableName(columnNumber)
    	assertTrue(CSVProcessor.isCheck("check tabe(9) references tale(5)"));

    	//checking for multiple spaces
    	assertTrue(CSVProcessor.isCheck("check 					tabe(pisdf)"
    				+ "			references						 tale(yu)"));

    	//checking command where the first word is not check
    	assertFalse(CSVProcessor.isCheck("chck tabe(pisdf)"
    									+ " references tale(yu)"));

    	//where one of the tableNames has a number
    	assertFalse(CSVProcessor.isCheck("check tabe1(pisdf)"
    									+ " references tale(yu)"));

    	//where the term "references" isn't there
    	assertFalse(CSVProcessor.isCheck("check tabe1(pisdf) "
    									+ "referes tale(yu)"));

    	//where there are no spaces
    	assertFalse(CSVProcessor.isCheck("check" + "tabe(pisdf)"
    									+ "refererences" + "tale(yu)"));
    }
}
