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
public class ManifestSAXTest {

    public ManifestSAXTest() {
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
    public void TestBuild()
        throws TException
    {
        try {
            
            VersionMap map1 = getMap("testresources/ark+=13030=m5q81h1p--manifest.xml");
            int current = map1.getCurrent();
            //System.out.println(map.dump("SAX"));
            String base = "http://mystore:9999/content/9010";
            File tmp = FileUtil.getTempFile("tmp", ".txt");
            for (int ver=1; ver<=current; ver++) {
                int cnt = map1.buildAddManifest(base, ver, tmp);
                if (true) {
                    String out = FileUtil.file2String(tmp);
                    System.out.println("***ADDMAP(" + ver + "):" + cnt + "\n"
                            + out
                            );
                }
            }
            tmp.delete();
            File tmpFile = FileUtil.getTempFile("xxx", ".xml");
            ManifestStr.buildManifest(map1, tmpFile);
            String tmpString = FileUtil.file2String(tmpFile);
            System.out.println("*****************STR:\n" + tmpString);
            InputStream stream2 = new FileInputStream(tmpFile);
            LoggerInf logger = new TFileLogger("TestBuild", 50, 50);
            VersionMap map2 = ManifestSAX.buildMap(stream2, logger);
            assertTrue(map2.isThisSubset(map1));
            assertTrue(map1.isThisSubset(map2));
            tmpFile.delete();
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    @Test
    public void TestSubset()
        throws TException
    {
        try {
            
            VersionMap mapTo = getMap("testresources/ark+=13030=m5q81h1p--manifest.xml");
            VersionMap mapFrom = getMap("testresources/ark+=13030=m5q81h1p--manifest.xml");
            VersionMap mapBig = getMap("testresources/ark+=13030=BIG--manifest.xml");
            VersionMap mapOther = getMap("testresources/ark+=90135=q1w9573b--manifest.xml");
            assertTrue(mapFrom.isThisSubset(null));
            assertTrue(mapFrom.isThisSubset(mapTo));
            mapTo.deleteCurrent();
            assertTrue(mapFrom.isThisSubset(mapTo));
            assertFalse(mapTo.isThisSubset(mapFrom));
            //assertTrue(mapFrom.isThisSubset(mapBig));
            assertTrue(mapOther.isThisSubset(mapOther));
            assertFalse(mapOther.isThisSubset(mapBig));
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    //@Test
    public void TestSubset3()
        throws TException
    {
        try {
            VersionMap mapOther = getMap("testresources/ark+=90135=q1w9573b--manifest.xml");
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    public VersionMap getMap(String path)
        throws TException
    {
        ManInfo test = new ManInfo();
        LoggerInf logger = new TFileLogger("TestBuild", 50, 50);
        InputStream inStream =  test.getClass().getClassLoader().
                getResourceAsStream(path);
        try {
            
            return ManifestSAX.buildMap(inStream, logger);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            throw new TException(ex);
        }
    }
}