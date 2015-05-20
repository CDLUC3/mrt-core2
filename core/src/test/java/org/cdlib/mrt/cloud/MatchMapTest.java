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
public class MatchMapTest {

    public MatchMapTest() {
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
        TestIt("#1", "testresources/ark+=13030=BIG--manifest.xml", "testresources/ark+=13030=BIG2--manifest.xml");
        TestIt("#2", "testresources/ark+=13030=BIG--manifest.xml", "testresources/ark+=13030=BIG--manifest.xml");
        TestIt("#3", "testresources/ark+=13030=BIG2--manifest.xml", "testresources/ark+=13030=BIG2Drop--manifest.xml");
        TestIt("#4", "testresources/ark+=13030=BIG--manifest.xml", null);
        TestIt("#5", null, "testresources/ark+=13030=BIG2--manifest.xml");
    }
    
    
    public void TestSave(String header, String str1, String str2)
        throws TException
    {
        ManInfo test = new ManInfo();
        InputStream big1Stream =  test.getClass().getClassLoader().
                getResourceAsStream(str1);
        InputStream big2Stream =  test.getClass().getClassLoader().
                getResourceAsStream(str2);
        try {
            LoggerInf logger = new TFileLogger("MatchMap", 50, 50);
            VersionMap mapOne = ManifestSAX.buildMap(big1Stream, logger);
            VersionMap mapTwo = ManifestSAX.buildMap(big2Stream, logger);
            MatchMap matchMap = new MatchMap(mapOne, mapTwo, logger);
            matchMap.compare();
            System.out.println(matchMap.dump("compare:" + header));
            assertTrue(true);
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("NAME=" + ex.getClass().getName());
            System.out.println("Exception:" + ex);
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            
        }
    }
    
    
    public void TestIt(String header, String str1, String str2)
        throws TException
    {
        ManInfo test = new ManInfo();
        VersionMap mapOne = null;
        VersionMap mapTwo = null;
        try {
            LoggerInf logger = new TFileLogger("MatchMap", 50, 50);
            if (str1 != null) {
                InputStream big1Stream =  test.getClass().getClassLoader().
                        getResourceAsStream(str1);
                mapOne = ManifestSAX.buildMap(big1Stream, logger);
            }
            if (str2 != null) {
                InputStream big2Stream =  test.getClass().getClassLoader().
                        getResourceAsStream(str2);
                mapTwo = ManifestSAX.buildMap(big2Stream, logger);
            }
            MatchMap matchMap = new MatchMap(mapOne, mapTwo, logger);
            matchMap.compare();
            System.out.println(matchMap.dump("compare:" + header));
            assertTrue(true);
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("NAME=" + ex.getClass().getName());
            System.out.println("Exception:" + ex);
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            
        }
    }
}