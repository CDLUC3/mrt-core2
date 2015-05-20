/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TallyTable;
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
import java.util.Properties;

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
public class TallyTableTest {
    protected static final String NAME = "TallyTableTest";
    protected static final String MESSAGE = NAME + ": ";

    protected File directory = null;

    public TallyTableTest() {
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
    public void testIt()
    {
        try {
            TallyTable tally1 = new TallyTable();
            for (int i=1; i<=5; i++) {
                tally1.bump("tally", i);
                tally1.bump("cnt");
            }
            testTally("1", tally1);

            File tallyFile = new File(directory, "tally.txt");
            tally1.saveTable(tallyFile);
            TallyTable tally2 = new TallyTable();
            tally2.loadTable(tallyFile);
            testTally("2", tally2);

            Properties tProp = tally2.getAsProperties();
            assertTrue(tProp.getProperty("tally").equals("15"));
            assertTrue(tProp.getProperty("cnt").equals("5"));

            tally1.set("set", 99);
            assertTrue(tally1.getCount("set") == 99);
            assertTrue(tally1.size() == 3);
            tally1.clear();
            assertTrue(tally1.size() == 0);

            long time1 = tally1.getTime();
            Thread.sleep(250);
            tally1.addDiffTime("diff", time1);
            long diffTime = tally1.getValue("diff");
            assertTrue("diff=" + diffTime, diffTime >= 249);


        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    protected void testTally(String name, TallyTable tally)
        throws Exception
    {
        long cnt = tally.getCount("cnt");
        assertTrue(name + ": cnt=" + cnt, cnt == 5);
        long tallyCnt = tally.getCount("tally");
        assertTrue(name + ": tallyCnt=" + tallyCnt, tallyCnt == 15);
    }
}