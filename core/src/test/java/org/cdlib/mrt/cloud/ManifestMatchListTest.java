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
public class ManifestMatchListTest {
    private BufferedReader reader = null;
    private long inCnt = 0;
    private long matchCnt = 0;
    private long failCnt = 0;
    private long notFoundCnt = 0;

    public ManifestMatchListTest() {
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
            
            //dump("testresources/ark+=13030=BIG--manifest.xml");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F13030%2Fm52b8w1p");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F20775%2Fbb03080025");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F20775%2Fbb21509964");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F90135%2Fq15h7d7h");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000k86");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000k9p");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kb5");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kcn");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kd4");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kfm");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kg3");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000khk");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kj2");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kkj");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000km1");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000knh");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kp0");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kqg");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000krz");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000ksf");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000ktx");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kvd");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kww");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kxc");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk4000kzv");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk40298nz");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk40298pf");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk40298qx");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk40298rd");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk40298sw");
            match("http://uc3-mrt-store-dev.cdlib.org:35121/manifest/910/ark%3A%2F99999%2Ffk40298tc");
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    //@Test
    public void testList()
        throws TException
    {
        try {
            File testFile = new File("C:/Documents and Settings/dloy/My Documents/MRTMaven/test/cloud/dev-manifests.txt");
            setList(testFile);
            while(true) {
                String testURL = reader.readLine();
                if (testURL == null) break;
                match(testURL);
                inCnt++;
                if ((inCnt % 50) == 0) System.out.println("(" + inCnt + ") - matchCnt=" + matchCnt 
                        + " - failCnt=" + failCnt
                        + " - notFoundCnt=" + notFoundCnt
                        );
            }
            System.out.println("FINAL (" + inCnt + ") - matchCnt=" + matchCnt 
                        + " - failCnt=" + failCnt
                        + " - notFoundCnt=" + notFoundCnt
                        );
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

   
    public void match(String urlS)
        throws TException
    {
        File tFile = null;
        File oFile = null;
        try {
            LoggerInf logger = new TFileLogger("TestBuild", 50, 50);
            URL url = new URL(urlS);
            tFile = FileUtil.getTempFile("tmpin", ".txt");
            //System.out.println("*** test url:" + url.toString());
            FileUtil.url2File(logger, url, tFile);
            //System.out.println("tFile size=" + tFile.length());
            assertTrue(tFile.length() > 0);
            //System.out.println("IN:\n" + FileUtil.file2String(tFile, "utf-8"));
            InputStream in = getStream(tFile);
            VersionMap inMap = ManifestSAX.buildMap(in, logger);
            oFile = FileUtil.getTempFile("oFile", ".txt");
            ManifestStr.buildManifest(inMap, oFile);
            InputStream oStream = new FileInputStream(oFile);
            VersionMap outMap = ManifestSAX.buildMap(oStream, logger);
            matchCnt++;
            assertTrue(inMap.isMatch(outMap));
            
        }  catch (Exception ex) {
            if (ex.toString().contains("REQUESTED_ITEM_NOT_FOUND")) {
                System.out.println("Not found:" + urlS);
                notFoundCnt++;
                return;
            }
            System.out.println("Exception:" + ex);
            failCnt++;
            
        } finally {
            try {
                tFile.delete();
            } catch (Exception ex) { }
            try {
                oFile.delete();
            } catch (Exception ex) { }
        }
    }
    
    public InputStream getStream(File tFile)
        throws TException
    {
        try {
            return new FileInputStream(tFile);
            
        } catch (Exception ex) {
            throw new TException (ex);
        }
    }

    protected void setList(File listFile)
        throws TException
    {
        try {
            FileInputStream inStream = new FileInputStream(listFile);
            DataInputStream in = new DataInputStream(inStream);
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));


        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

}