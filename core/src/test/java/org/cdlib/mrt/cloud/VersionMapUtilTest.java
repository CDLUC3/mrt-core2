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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;



import org.cdlib.mrt.cloud.ManInfo;
import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;

/**
 *
 * @author dloy
 */
public class VersionMapUtilTest {

    public VersionMapUtilTest() {
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
    public void defaultTest()
        throws TException
    {
        assertTrue(true);
    }

    //@Test
    public void test()
        throws TException
    {
        try {
  
            VersionMap map = VersionMapUtil.getMap("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F20775%2Fbb03080025");
            String baseURL = "http://uc3-mrt-store-dev.cdlib.org:35121/content/910";
            //VersionMap map = VersionMapUtil.getMap("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk48g93wd");
            //VersionMap map = VersionMapUtil.getMap("http://uc3-mrt-store-stg.cdlib.org:35121/manifest/2111/ark%3A%2F99999%2Ffk4sf3ctb");
            int current = map.getCurrent();
            System.out.println("current=" + current);
            
            System.out.println("*************TEST 1**************");
            
            File testOut = new File("C:/Documents and Settings/dloy/My Documents/MRTMaven/test/out");
            for (int i=1; i<=current; i++) {
                validateComponents(map, i);
                VersionMapUtil.getVersionFiles (map, i, baseURL, testOut);
            }
            assert(true);
            
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    private void validateComponents(VersionMap map, int versionID)
        throws TException
    {
        List<FileComponent> fileComponents = VersionMapUtil.getVersion(map, versionID);
        System.out.println("\n*****validateComponent" 
                + " - versionID=" + versionID
                + " - count=" + fileComponents.size()
                );
        for (FileComponent component : fileComponents) {
            System.out.println(component.dump(component.getIdentifier()));
        }
    }
}