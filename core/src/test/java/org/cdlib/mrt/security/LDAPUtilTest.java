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
public class LDAPUtilTest {

    protected static final String NAME = "LDAPAuthenticationTest";
    protected static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");
    protected final static String UID = "dloy";
    protected final static String PW = "Huz8dloy";
    //protected final static String HOSTS = "ldaps://coot.ucop.edu:1636";
    //protected final static String HOSTS = "ldaps://dp08.cdlib.org:1636";
    protected final static String HOSTS = "ldaps://ferret.cdlib.org:1636";
    //protected final static String HOSTS = "ldaps://coot.ucop.edu:1636";
    //protected final static String HOSTS = "ldaps://badger.cdlib.org:1636";
    protected final static String HOST = "ldap://badger.cdlib.org:1389";


    public LDAPUtilTest() {
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


    @Test
    public void Dummy()
        throws TException
    {
        assertTrue(true);
    }

    //@Test
    public void TestProperties()
        throws TException
    {
        String ldaps = HOSTS;
        String ldap = HOST;
        try {
            LinkedHashList<String,String> prop = LDAPUtil.getUserProperties(
                    ldaps,
                    "merritt",
                    "merritt");
            assertTrue(prop != null);
            System.out.println(LDAPUtil.dump("TestProperties", prop));

            prop = LDAPUtil.getUserProperties(
                    ldaps,
                    "merritt",
                    "xxx");
            assertTrue(prop == null);

            prop = LDAPUtil.getUserProperties(
                    ldap,
                    "merritt",
                    "merritt");
            assertTrue(prop != null);
            System.out.println(LDAPUtil.dump("TestProperties", prop));

            prop = LDAPUtil.getUserProperties(
                    ldap,
                    "merritt",
                    "xxx");
            assertTrue(prop == null);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    @Test
    public void TestAuthorize()
        throws TException
    {
        try {
            boolean there = LDAPUtil.isAuthorized(HOSTS, UID, PW, "merritt_demo");
            //boolean there = LDAPUtil.isAuthorizedOriginal(UID, PW, "merritt_demo");
            System.out.println(there);
            assertTrue(there);

            System.out.println("***FAIL USER***");
            there = LDAPUtil.isAuthorized(HOSTS, "XXX", PW, "merritt_demo");
            //boolean there = LDAPUtil.isAuthorizedOriginal(UID, PW, "merritt_demo");
            System.out.println(there);
            assertFalse(there);

            System.out.println("***FAIL PASSWORD***");
            there = LDAPUtil.isAuthorized(HOSTS, UID, "XXX", "merritt_demo");
            //boolean there = LDAPUtil.isAuthorizedOriginal(UID, PW, "merritt_demo");
            System.out.println(there);

            assertFalse(there);


            System.out.println("***FAIL GROUP***");
            there = LDAPUtil.isAuthorized(HOSTS, UID, PW, "XXX");
            //boolean there = LDAPUtil.isAuthorizedOriginal(UID, PW, "merritt_demo");
            System.out.println(there);
            assertFalse(there);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    //@Test
    public void TestProfile()
        throws TException
    {
        try {
            //String there = LDAPUtil.getProfile("ldaps://badger.cdlib.org:1636", "merritt", "merritt", "demo_merritt");

            String there = LDAPUtil.getProfile(HOSTS, UID, PW, "merritt_demo");
            System.out.println(there);
            assertTrue(StringUtil.isNotEmpty(there));

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }


}