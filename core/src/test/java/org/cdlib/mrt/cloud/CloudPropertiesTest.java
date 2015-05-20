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
public class CloudPropertiesTest {

    public CloudPropertiesTest() {
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
        CloudProperties cloudProp = new CloudProperties();
        try {
            cloudProp.setProperty("HeAd1", "head1");
            assertTrue(cloudProp.getProperty("hEaD1").equals("head1"));

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    @Test
    public void Test2()
        throws TException
    {
        Properties prop = new Properties();
        try {
            prop.setProperty("HeAd1", "head1");
            CloudProperties cloudProp = new CloudProperties(prop);
            assertTrue(cloudProp.getProperty("hEaD1").equals("head1"));

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    @Test
    public void Test3()
        throws TException
    {
        CloudProperties cloudProp = new CloudProperties();
        try {
            cloudProp.setProperty("AA", "a");
            cloudProp.setProperty("BB", "b");
            cloudProp.setProperty("CC", "c");
            cloudProp.setProperty("DD", "d");
            cloudProp.setProperty("EE", "e");
            System.out.println(cloudProp.dump("dump cloudProp"));
            Properties metaProp = cloudProp.buildMetaProperties();
            
            System.out.println(PropertiesUtil.dumpProperties("metaProp", metaProp));
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    
    @Test
    public void Test4()
        throws TException
    {
        Properties prop = new Properties();
        try {
            prop.setProperty("X-Object-Meta-Aa", "a");
            prop.setProperty("X-Object-Meta-Bb", "b");
            prop.setProperty("header.X-Object-Meta-Cc", "c");
            prop.setProperty("header.X-Object-Meta-Dd", "d");
            prop.setProperty("somethingE", "e");
            prop.setProperty("somethingF", "f");
            CloudProperties cloudProp = new CloudProperties();
            cloudProp.setFromMetaProperties(prop);
            
            System.out.println(cloudProp.dump("Test4"));
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
}