/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.SerializeUtil;
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
public class SerializeUtilTest {

    protected File directory = null;

    public SerializeUtilTest() {
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
        String key = "SerializeUtilTest";
        try {
            SerializeTest test1 = new SerializeTest(10, 20);
            File serializeFile = new File(directory, "test1.txt");
            SerializeUtil.serialize(test1, serializeFile);
            SerializeTest test2 = (SerializeTest)SerializeUtil.deserialize(serializeFile);
            assertTrue(test1.equals(test2));
            assertTrue(test2.equals(test1));

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }



    }

    public static class SerializeTest
            implements Serializable
    {
        int a = -1;
        int b = -1;

        public SerializeTest(int a, int b)
        {
            this.a = a;
            this.b = b;
        }

        public boolean equals(SerializeTest test)
        {
            if ((this.a == test.a) && (this.b == test.b)) return true;
            return false;
        }

    }
}