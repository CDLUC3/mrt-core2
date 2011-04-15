/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TRuntimeException;
import java.io.File;
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
public class PropertiesUtilTest {

    protected File directory = null;

    public PropertiesUtilTest() {
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
        String key = "PropertiesUtilTest";
        try {
            Properties prop = new Properties();
            prop.setProperty("A", "a");
            prop.setProperty("B", "b");
            prop.setProperty("C", "c");
            prop.setProperty("D", "d");
            prop.setProperty("Z", "26");
            Properties copyProp = PropertiesUtil.copyProperties(prop);
            assertTrue(PropertiesUtil.equals(prop, copyProp));
            copyProp.setProperty("D", "D");
            assertTrue(!PropertiesUtil.equals(prop, copyProp));
            String propS = PropertiesUtil.buildLoadProperties(prop);
            File propFile = new File(directory, "file.properties");
            FileUtil.string2File(propFile, propS);
            Properties fileProp = PropertiesUtil.loadFileProperties(propFile);
            assertTrue(PropertiesUtil.equals(prop, fileProp));
            int zval = PropertiesUtil.getInt(fileProp, "Z");
            assertTrue(zval == 26);
            int aval = PropertiesUtil.getInt(fileProp, "A");
            assertTrue(aval == -1);

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }



    }
}