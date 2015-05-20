/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.cloud;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;



import org.cdlib.mrt.cloud.ManInfo;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;

/**
 *
 * @author dloy
 */
public class CloudListTest {

    public CloudListTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}


    @Test
    public void Test1()
        throws TException
    {
        CloudList list = new CloudList();
        try {
            list.add("container1", "key1", 10, "etag1", "type1", "modified1");
            list.add("container2", "key2", 20, "etag2", "type2", "modified2");
            list.add("container3", "key3", 30, "etag3", "type3", "modified3");
            list.add("container4", "key4", 40, "etag4", "type4", "modified4");
            assertTrue(list.size() == 4);
            System.out.println(list.dump("Test"));
            CloudList.CloudEntry entry = list.get(2);
            assertTrue(entry.container.equals("container3"));
            assertTrue(entry.key.equals("key3"));
            assertTrue(entry.etag.equals("etag3"));
            assertTrue(entry.size == 30);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
}