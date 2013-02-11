/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.XMLUtil;
import java.util.Properties;
import java.util.Set;
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
public class ZooCodeUtilTest {

    protected static final String NL = System.getProperty("line.separator");

    public ZooCodeUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }



    @Test
    public void testEncode()
    {
        try {
            Properties prop = getProp();
            System.out.println(PropertiesUtil.dumpProperties("test prop", prop));
            byte[] bytes = ZooCodeUtil.encodeItem(prop);
            String disp = new String(bytes, "utf-8");
            System.out.println("Encode xml:\n" + disp);
            Properties propDecode = ZooCodeUtil.decodeItem(bytes);
            Set<String> keys = (Set)propDecode.keySet();
            for (String key : keys) {
                String value1 = prop.getProperty(key);
                if (value1 == null) assertTrue("key missing 1:" + key, false);
                String value2 = propDecode.getProperty(key);
                System.out.println("test "
                            + " - key=" + key
                            + " - value1=" + value1
                            + " - value2=" + value2
                        );
                if (value2 == null) assertTrue("key missing 2:" + key, false);
                if (!value1.equals(value2)) {
                    assertTrue("values differ for " + key
                            + " - value1=" + value1
                            + " - value2=" + value2
                            , false);
                }
            }
            assertTrue(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);
        }
    }
    
    private Properties getProp()
    {
        Properties prop = new Properties();
        prop.setProperty("keya", "value a");
        prop.setProperty("keyb", "value b");
        prop.setProperty("keyc", "value c");
        prop.setProperty("keyd", "value d");
        return prop;
    }

}