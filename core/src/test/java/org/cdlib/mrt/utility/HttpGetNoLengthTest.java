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
public class HttpGetNoLengthTest 
{
    protected static final String NAME = "HttpGetTest";
    protected static final String MESSAGE = NAME + ": ";
    
    private TFrame tFrame = null;
    private File testDir = null;

    public HttpGetNoLengthTest() {
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
    public void testCopy()
    {
        String key = "FileUtilTest";
        String pathS = "/replic/tasks/150508-storefail/out/tnolen.txt";
        //long size = 5L;
        //long size = 27512L;
        long size = -1L;
        File out = new File(pathS);
        if (out.exists()) {
            out.delete();
        }
        String urlS = "http://uc3-mrt-inv-dev.cdlib.org:28080/test/bug/150508/manifest.txt";
        //String urlS = "http://uc3-mrt-inv-dev.cdlib.org:28080/test/bug/150508/xxx.txt";
        //String urlS = "http://xxx-mrt-inv-dev.cdlib.org:28080/test/bug/150508/manifest.txt";
        //String urlS = "yyyy://uc3-mrt-inv-dev.cdlib.org:28080/test/bug/150508/manifest.txt";
       
        try {
            URL url = new URL(urlS);
            if (url == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "fillComponent - component URL missing");
            }
            LoggerInf logger = LoggerAbs.getTFileLogger("testFormatter", 10, 10);
            HttpGet.getFile(url, out, size, 600000, logger);
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


}