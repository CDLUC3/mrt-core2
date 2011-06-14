/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.security;
import org.cdlib.mrt.core.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TFileLogger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.DOMParser;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.HTTPUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.LinkedHashList;
import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class SecurityUtilTest {

    protected static final String NAME = "SecurityUtilTest";
    protected static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");
    protected static final String PW = "THESE are the times that try men's souls.";


    public SecurityUtilTest() {
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


    //@Test
    public void Dummy()
        throws TException
    {
        assertTrue(true);
    }

    @Test
    public void TestProperties()
        throws TException
    {
        try {
            String testIn = "abcdefghijkomNOPQRSTUVWXYZ0123456789";
            test(testIn, PW);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    public void test(String text, String key)
        throws TException
    {
        try {
            String enc =  SecurityUtil.desEncrypt(text, key);
            System.out.println("enc=" + enc);
            String dec =  SecurityUtil.desDecrypt(enc, key);
            System.out.println("dec=" + dec);
            assertTrue(text.equals(dec));

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }


}