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
public class ManifestMatchPathTest {
    private BufferedReader reader = null;
    private long inCnt = 0;
    private long matchCnt = 0;
    private long failCnt = 0;
    private long notFoundCnt = 0;

    public ManifestMatchPathTest() {
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
    public void test()
        throws TException
    {
        try {
            
            //dump("testresources/ark+=13030=BIG--manifest.xml");
            match("testresources/ark+=13030=m5q81h1p--manifest.xml");
            match("testresources/ark+=13030=BIG--manifest.xml");
            match("testresources/ark+=90135=q1w9573b--manifest.xml");
            assertTrue(true);
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

   
    public void match(String path)
        throws TException
    {
        File oFile = null;
        try {
            LoggerInf logger = new TFileLogger("TestBuild", 50, 50);
            ManInfo test = new ManInfo();
            InputStream inStream =  test.getClass().getClassLoader().
                getResourceAsStream(path);
            if (inStream == null) {
                System.out.println("unable to find path:" + path);
                assertTrue(false);
            }
            VersionMap inMap = ManifestSAX.buildMap(inStream, logger);
            oFile = FileUtil.getTempFile("oFile", ".txt");
            ManifestStr.buildManifest(inMap, oFile);
            InputStream oStream = new FileInputStream(oFile);
            VersionMap outMap = ManifestSAX.buildMap(oStream, logger);
            matchCnt++;
            assertTrue(inMap.isMatch(outMap));
            System.out.println("MATCH:" + path);
            
        }  catch (Exception ex) {
            if (ex.toString().contains("REQUESTED_ITEM_NOT_FOUND")) {
                System.out.println("Not found:" + path);
                notFoundCnt++;
                return;
            }
            System.out.println("Exception - path=" + path + " ex:" + ex);
            ex.printStackTrace();
            assertTrue(false);
            
        } finally {
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