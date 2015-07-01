/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.DirectoryStats;
import org.cdlib.mrt.utility.TFrame;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TRuntimeException;
import org.cdlib.mrt.utility.StringUtil;
/**
 *
 * @author dloy
 */
public class HttpGetNuxeoTest 
{
    protected static final String NAME = "HttpGetTest";
    protected static final String MESSAGE = NAME + ": ";
    
    private TFrame tFrame = null;
    private File testDir = null;

    public HttpGetNuxeoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            String propertyList[] = {
                    "testresources/TestLocal.properties"};
            tFrame = new TFrame(propertyList, NAME);
            
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex);
        }
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
    public void testBigCopy()
    {
        String key = "FileUtilTest";
        String pathS = "/replic/tasks/150508-storefail/out/lejonBrandyRGB.tif";
        long size = -1L;
        File out = new File(pathS);
        if (out.exists()) {
            out.delete();
        }
        String urlS = 
        //    "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/restAPI/default/01ad6477-eb2d-4f5b-8f0c-fab0066fec6a/export?format=XML";
        
        //  "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/01ad6477-eb2d-4f5b-8f0c-fab0066fec6a/file:content/AbandonedApartment.tif";
            
        //  "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/01ad6477-eb2d-4f5b-8f0c-fab0066fec6a/file:content/AbandonedApartment.tif";
        
        //   "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/restAPI/default/c82ca062-d4ae-42a8-87a4-564b21f82410/export?format=XML";
        
        //   "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/01ad6477-eb2d-4f5b-8f0c-fab0066fec6a/file:content/AbandonedApartment.tif";
        
             "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/032faa64-8492-4ca3-80fc-fbcd71912b96/file:content/lejonBrandyRGB.tif";
        try {
            URL url = new URL(urlS);
            if (url == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "fillComponent - component URL missing");
            }
            LoggerInf logger = LoggerAbs.getTFileLogger("testFormatter", 10, 10);
            System.out.println("HttpGetNuxeoTest: file=" + out.getAbsolutePath());
            HttpGet.getFile(url, out, size, 600000, logger);
            
            /*
            String disp = FileUtil.file2String(out);
            System.out.println("DISPLAY\n" + disp);
                    */
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            ex.printStackTrace();
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }
    
    //@Test
    public void testNuxeo()
    {
        String key = "testNuxeo";
        
        try {
            test(
                "AbandonedApartment.tif",
                "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/01ad6477-eb2d-4f5b-8f0c-fab0066fec6a/file:content/AbandonedApartment.tif"
            );
            test(
                "lejonBrandyRGB.tif",
                "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/032faa64-8492-4ca3-80fc-fbcd71912b96/file:content/lejonBrandyRGB.tif"
            );
            test(
                "LejonChampagneCorkRGB.ti",
                "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/65fd7c44-add7-415a-aa27-e84e892485e0/file:content/LejonChampagneCorkRGB.ti"
            );
            
            test(
                "GandDBurgundySceltoRGB.t",
                "https://merritt:urmFKaV8x8HJf@nuxeo-stg.cdlib.org/Nuxeo/nxbigfile/default/9d467997-36bc-4551-9e97-6ddb0bf3ca4f/file:content/GandDBurgundySceltoRGB.t"
            );
            
        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            ex.printStackTrace();
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }
    
    public static void test(String pathS, String urlS)
        throws Exception
    {
        String dirS = "/replic/tasks/150508-storefail/out";
        String key = "FileUtilTest";
        long size = -1L;
       
        
        try {
            File dir = new File(dirS);
            File out = new File(dir, pathS);
            if (out.exists()) {
                System.out.println("delete:" + out.getAbsolutePath());
                out.delete();
            }
            URL url = new URL(urlS);
            if (url == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "fillComponent - component URL missing");
            }
            LoggerInf logger = LoggerAbs.getTFileLogger("testFormatter", 10, 10);
            System.out.println("HttpGetNuxeoTest:\n "
                    + " - file=" + out.getAbsolutePath() + "\n"
                    + " - path=" + url.toString() + "\n"
            );
            FileUtil.url2File(logger, url, out);
                    
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            ex.printStackTrace();
            throw ex;
        }
    }


}