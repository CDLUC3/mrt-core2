/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dloy
 */
public class StringUtilTest {
    protected static final String NAME = "StringUtilTest";
    protected static final String MESSAGE = NAME + ": ";

    protected File directory = null;

    public StringUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            directory = FileUtil.getTempDir("propest");

        } catch (Exception ex) {
            throw new TRuntimeException.INVALID_DATA_FORMAT("PropertiesUtilTest: Setup fails: " + ex);
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void compress()
    {
        try {
            String t1 = "    A AA  A   A    ";
            String t2 = "xxxxAxAAxxAxxxAxxxx";
            String o1 = StringUtil.compress(t1);
            assertTrue("o1=>" + o1 + "<",o1.equals(" A AA A A "));
            String o2 = StringUtil.compress(t2, "x");
            assertTrue("o2=>" + o2 + "<",o2.equals("xAxAAxAxAx"));



        } catch (Exception ex) {
             System.out.println(MESSAGE + "Exception:" + ex);
             assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void isNumeric()
    {
        String key = "SerializeUtilTest";
        String test = null;
        try {
            String t1 = " 12345 ";
            String t2 = "123456789";
            assertFalse(StringUtil.isNumeric(t1));
            assertFalse(StringUtil.isNumeric(""));
            assertTrue(StringUtil.isNumeric(t2));


        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void isEmpty()
    {
        String test = null;
        try {
            assertTrue(StringUtil.isEmpty(""));
            assertTrue(StringUtil.isEmpty((String)null));
            assertTrue(StringUtil.isEmpty((StringBuffer)null));
            assertTrue(StringUtil.isEmpty(new StringBuffer("")));
            byte [] bytes = new byte[0];
            assertTrue(StringUtil.isEmpty(bytes));
            assertTrue(StringUtil.isEmpty((byte[])null));

            assertFalse(StringUtil.isNotEmpty(""));
            assertFalse(StringUtil.isNotEmpty((String)null));
            assertFalse(StringUtil.isNotEmpty((StringBuffer)null));
            assertFalse(StringUtil.isNotEmpty(new StringBuffer("")));
            assertFalse(StringUtil.isNotEmpty(bytes));
            assertFalse(StringUtil.isNotEmpty((byte[])null));

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    
    }

    @Test
    public void isAllBlank()
    {
        String test = null;
        try {
            assertTrue(StringUtil.isAllBlank(""));
            assertTrue(StringUtil.isAllBlank(null));
            assertTrue(StringUtil.isAllBlank("              "));
            assertFalse(StringUtil.isAllBlank("      |        "));
            assertFalse(StringUtil.isAllBlank("#"));
            assertFalse(StringUtil.isAllBlank("abcd"));
            assertFalse(StringUtil.isAllBlank("ab cd"));

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }

    }
}