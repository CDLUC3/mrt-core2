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
public class ManifestStrTest {

    public ManifestStrTest() {
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
    public void dump()
        throws TException
    {
        try {
            
            //dump("testresources/ark+=13030=BIG--manifest.xml");
            dump("testresources/ark+=13030=m5q81h1p--manifest.xml");
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

   
    public void dump(String path)
        throws TException
    {
        try {
            
            VersionMap mapBig = getMap(path);
            String mapBigS = dumpManifest(path);
            System.out.println("DUMP IN:\n" + mapBigS);
            String xmlBigS = getXML(mapBig);
            System.out.println("DUMP OUT:\n" + xmlBigS);
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
    
    
    public String dumpManifest(String path)
        throws TException
    {
        ManInfo test = new ManInfo();
        InputStream inStream =  test.getClass().getClassLoader().
                getResourceAsStream(path);
        try {
            
            return StringUtil.streamToString(inStream, "utf-8");
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            throw new TException(ex);
        }
    }
    
    
    
    public String getXML(VersionMap map)
        throws TException
    {
        File tmpFile = FileUtil.getTempFile("ManifestStrTest", ".xml");
        try {
            ManifestStr.buildManifest(map, tmpFile);
            String out = FileUtil.file2String(tmpFile);
            return out;
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            throw new TException(ex);
            
        } finally {
            if (tmpFile != null) {
                try {
                    tmpFile.delete();
                } catch (Exception ex) { }
            }
        }
    }
}