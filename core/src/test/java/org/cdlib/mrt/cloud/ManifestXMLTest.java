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
public class ManifestXMLTest {

    public ManifestXMLTest() {
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
    public void TestEmpty()
        throws TException
    {
        System.out.println("TestEmpty entered");
        File tmpFile = FileUtil.getTempFile("tmp", ".xml");
        try {
            Identifier objectID = new Identifier("ark:/13030/abcde");
            LoggerInf logger = new TFileLogger("testmanifest", 50, 50);
            VersionMap versionMap = new VersionMap(objectID, logger);
            //VersionMap map = ManifestXML.getVersionMap(objectID, logger, manifestStream);
            FileOutputStream ofs = new FileOutputStream(tmpFile);
            ManifestXML.buildOut(versionMap, ofs);
            String xmlS = FileUtil.file2String(tmpFile);
            System.out.println(xmlS);
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    @Test
    public void TestIt()
        throws TException
    {
        System.out.println("ManifestXMLTest entered");
        File tmpFile = FileUtil.getTempFile("tmp", ".xml");
        try {
            ManInfo test = new ManInfo();
            String resourceName = "testresources/test-manifest.xml";
            InputStream manifestStream =  test.getClass().getClassLoader().
                getResourceAsStream(resourceName);
            if (manifestStream == null) {
                throw new TException.INVALID_OR_MISSING_PARM("stream not found:"  + resourceName);
            }
            String inXMLS = StringUtil.streamToString(manifestStream, "utf-8");
            System.out.println("***inXMLS\n" + inXMLS);
            assertTrue(true);
            ByteArrayInputStream bais = new ByteArrayInputStream(inXMLS.getBytes("utf-8"));
            //System.out.println("dump:\n" + dump);
            Identifier objectID = new Identifier("ark:/13030/abcde");
            LoggerInf logger = new TFileLogger("testmanifest", 50, 50);
            VersionMap map = ManifestXML.getVersionMap(objectID, logger, bais);
            FileOutputStream ofs = new FileOutputStream(tmpFile);
            ManifestXML.buildOut(map, ofs);
            String outXMLS = FileUtil.file2String(tmpFile);
            System.out.println("***outXMLS(" + outXMLS.length() + ")\n" + outXMLS);
            assertTrue(inXMLS.equals(outXMLS));
            
            ManInfo manInfo = map.getVersionInfo(0);
            System.out.println("manInfo.versionID=" + manInfo.versionID
                    + " - current=" + map.getCurrent());
            assertTrue(manInfo.versionID == map.getCurrent());
            List<FileComponent> components = map.getVersionComponents(0);
            System.out.println("components length=" + components.size());
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    @Test
    public void TestDeleteAdd()
        throws TException
    {
        System.out.println("TestDelete entered");
        File tmpFile = FileUtil.getTempFile("tmp", ".xml");
        try {
            ManInfo test = new ManInfo();
            String resourceName = "testresources/test-manifest.xml";
            InputStream manifestStream =  test.getClass().getClassLoader().
                getResourceAsStream(resourceName);
            if (manifestStream == null) {
                throw new TException.INVALID_OR_MISSING_PARM("stream not found:"  + resourceName);
            }
            String inXMLS = StringUtil.streamToString(manifestStream, "utf-8");
            System.out.println("***TestDelete inXMLS\n" + inXMLS);
            assertTrue(true);
            ByteArrayInputStream bais = new ByteArrayInputStream(inXMLS.getBytes("utf-8"));
            //System.out.println("dump:\n" + dump);
            Identifier objectID = new Identifier("ark:/13030/abcde");
            LoggerInf logger = new TFileLogger("testmanifest", 50, 50);
            VersionMap map = ManifestXML.getVersionMap(objectID, logger, bais);
            int startCurrent = map.getCurrent();
            int startVersionCount = map.getVersionCount();
            
            map.deleteCurrent();
            int deleteCurrent = map.getCurrent();
            int deleteVersionCount = map.getVersionCount();
            
            assertTrue(startCurrent == (deleteCurrent+1));
            assertTrue(startVersionCount == (deleteVersionCount+1));
            
            FileOutputStream ofs = new FileOutputStream(tmpFile);
            ManifestXML.buildOut(map, ofs);
            String deleteXMLS = FileUtil.file2String(tmpFile);
            System.out.println("***TestDelete deleteXMLS(" + deleteXMLS.length() + ")\n" + deleteXMLS);
            assertFalse(inXMLS.equals(deleteXMLS));
            
            List<FileComponent> components = map.getVersionComponents(1);
            map.addVersion(components);
            
            int addCurrent = map.getCurrent();
            int addVersionCount = map.getVersionCount();
            assertTrue(addCurrent == (deleteCurrent+1));
            assertTrue(addVersionCount == (deleteVersionCount+1));
            
            ofs = new FileOutputStream(tmpFile);
            ManifestXML.buildOut(map, ofs);
            String addXMLS = FileUtil.file2String(tmpFile);
            System.out.println("***TestDelete addXMLS(" + addXMLS.length() + ")\n" + addXMLS);
            assertFalse(inXMLS.equals(addXMLS));
            
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
}