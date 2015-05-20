/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.cloud;
import java.io.BufferedReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Properties;



import org.cdlib.mrt.cloud.ManInfo;
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
public class ManifestSubsetTest {

    public ManifestSubsetTest() {
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
    public void testSubset()
        throws TException
    {
        try {
            
            //dump("testresources/ark+=13030=BIG--manifest.xml");
            assertTrue(subset(
                    "testresources/ark+=13030=BIG2--manifest.xml",
                    "testresources/ark+=13030=BIG--manifest.xml"
                    ));
            assertFalse(subset(
                    "testresources/ark+=13030=BIG--manifest.xml",
                    "testresources/ark+=13030=BIGDiffSize--manifest.xml"
                    ));
            assertFalse(subset(
                    "testresources/ark+=13030=BIG--manifest.xml",
                    "testresources/ark+=13030=BIGDiffMan--manifest.xml"
                    ));
            assertTrue(subset(
                    "testresources/ark+=13030=BIG2--manifest.xml",
                    null
                    ));
            assertFalse(subset(
                    "testresources/ark+=13030=BIG--manifest.xml",
                    "testresources/ark+=13030=BIG2--manifest.xml"
                    ));
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }    
    @Test
    public void testMatch()
        throws TException
    {
        try {
            
            //dump("testresources/ark+=13030=BIG--manifest.xml");
            assertTrue(match(
                    "testresources/ark+=13030=BIG--manifest.xml",
                    "testresources/ark+=13030=BIG--manifest.xml"
                    ));
            assertTrue(match(
                    "testresources/ark+=13030=BIG2--manifest.xml",
                    "testresources/ark+=13030=BIG2--manifest.xml"
                    ));
            assertFalse(match(
                    "testresources/ark+=13030=BIG2--manifest.xml",
                    null
                    ));
            assertFalse(match(
                    "testresources/ark+=13030=BIG2--manifest.xml",
                    "testresources/ark+=13030=BIG--manifest.xml"
                    ));
            assertFalse(match(
                    "testresources/ark+=13030=BIG--manifest.xml",
                    "testresources/ark+=13030=BIG2--manifest.xml"
                    ));
            assertFalse(match(
                    "testresources/ark+=13030=BIG--manifest.xml",
                    "testresources/ark+=13030=BIGDiffSize--manifest.xml"
                    ));
            assertFalse(match(
                    "testresources/ark+=13030=BIG--manifest.xml",
                    "testresources/ark+=13030=BIGDiffMan--manifest.xml"
                    ));
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

   
    public boolean subset(String path1, String path2)
        throws TException
    {
        try {
            VersionMap map1 = getVersionMap(path1);
            VersionMap map2 = null;
            if (StringUtil.isNotEmpty(path2)) {
                map2 = getVersionMap(path2);
            }
            boolean subsetB = map1.isThisSubset(map2);
            System.out.println("Test subset=" + subsetB + "\n"
                    + " - path1=" + path1 + "\n"
                    + " - path2=" + path2 + "\n"
                    );
            return subsetB;
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex
                    + " - path1=" + path1
                    + " - path2=" + path2
                    );
            ex.printStackTrace();
            throw new TException(ex);
            
        }
    }

   
    public boolean match(String path1, String path2)
        throws TException
    {
        try {
            VersionMap map1 = getVersionMap(path1);
            VersionMap map2 = null;
            if (StringUtil.isNotEmpty(path2)) {
                map2 = getVersionMap(path2);
            }
            boolean matchB = map1.isMatch(map2);
            System.out.println("Test match=" + matchB + "\n"
                    + " - path1=" + path1 + "\n"
                    + " - path2=" + path2 + "\n"
                    );
            return matchB;
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex
                    + " - path1=" + path1
                    + " - path2=" + path2
                    );
            ex.printStackTrace();
            throw new TException(ex);
            
        }
    }
    
    public VersionMap getVersionMap(String path)
        throws TException
    {
        try {
            LoggerInf logger = new TFileLogger("TestBuild", 50, 50);
            ManInfo test = new ManInfo();
            InputStream inStream =  test.getClass().getClassLoader().
                getResourceAsStream(path);
            if (inStream == null) {
                throw new TException.INVALID_OR_MISSING_PARM("unable to find path:" + path);
            }
            return ManifestSAX.buildMap(inStream, logger);
            
        }  catch (Exception ex) {
            System.out.println("Exception - path=" + path + " ex:" + ex);
            throw new TException(ex);
            
        }
    }

}