/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.security;
import org.cdlib.mrt.core.*;
import java.io.File;
import java.io.InputStream;
import java.util.List;
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
import org.cdlib.mrt.security.CertsLDAP;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.HTTPUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;

import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class CertsLDAPTest {

    protected static final String NAME = "LDAPAuthenticationTest";
    protected static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");

    //protected final static String HOSTS = "badger.cdlib.org:1636";
    //protected final static String HOSTS = "dp01.cdlib.org:1636";
    //protected final static String HOSTS = "coot.ucop.edu:1636";
    //protected final static String HOSTS = "ferret.cdlib.org:1636";
    //protected final static String HOSTS = "dp08.cdlib.org:1636";
    //protected final static String HOSTS = "merritt.cdlib.org:1636";
    protected final static String HOSTS = "uc3-mrt-wrk1-stg.cdlib.org:1636";
    protected final static String HOSTSXX = "ldaps://uc3-mrt-wrk1-stg.cdlib.org:1636";
    public CertsLDAPTest() {
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
    public void Test()
        throws TException
    {
        File outFile = null;
        try {

            String [] hostNames = {
                   //"badger.cdlib.org:1636",
                   "uc3-mrt-wrk1-stg.cdlib.org:1636",
                   "uc3-ldap.cdlib.org:1636"
                   //"uc3-mrt-wrk2-stg.cdlib.org:1636"
    //protected final static String HOSTS = "dp01.cdlib.org:1636";
                    //"coot.ucop.edu:1636",
                    //"ferret.cdlib.org:1636",
                    //"dp08.cdlib.org:1636"
    //protected final static String HOSTS = "merritt.cdlib.org:1636";
            };
            //outFile = FileUtil.getTempFile("rdf", "");
            for (String hostName : hostNames) {
                testit(hostName);
            }

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
            
        }
    }

    public void testit(String hostName)
        throws TException
    {
        CertsLDAP cl = null;
        File certFile = null;
        String lastProp = null;
        try {
            certFile = new File("jssecacerts-create3");
            cl = new CertsLDAP(certFile, hostName);
            LinkedHashList<String,String> prop = null;
            prop = cl.find(
                    "merritt",
                    "merritt");
            assertTrue(prop != null);
            /*
            prop = cl.find(
                    "dloy",
                    "xxxxx");
            assertTrue(prop == null);
                    */
            System.out.println(LDAPUtil.dump("TestProperties", prop));
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
            
        }
    }

    //@Test
    public void testAuth()
        throws TException
    {
        CertsLDAP cl = null;
        File certFile = null;
        String lastProp = null;
        try {
            certFile = new File("jssecacerts-create3");
            String hostName = "uc3-mrt-wrk1-stg.cdlib.org:1636";
            cl = new CertsLDAP(certFile, hostName);
            boolean auth = cl.isAuthorized(
                    "merritt",
                    "merritt",
                    "merritt_demo",
                    "sfisher"
                    );
            System.out.println("testAuth auth=" + auth);
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
            
        }
    }


    //@Test
    public void testContains()
        throws TException
    {
        CertsLDAP cl = null;
        File certFile = null;
        String lastProp = null;
        try {
            //certFile = new File("jssecacerts-create3");
            certFile = new File("/replic/mrtHomes/sword/jssecacert");

            //String hostName = "uc3-mrt-wrk1-stg.cdlib.org:1636";
            //String hostName = "uc3-ldap-stg-lb.cdlib.org:1636";
            String hostName = "uc3-ldap-dev.cdlib.org:1636";
            cl = new CertsLDAP(certFile, hostName);
            List<String> profiles = null;
            String submitter = "ucb_dash_submitter";
            //String submitter = "dloy";
            if (false) profiles = cl.getNames(
                    "merritt",
                    "merritt",
                    //"xxx",
                    //"ucop_dash_submitter"
                    submitter
                    //"sfisher"
                    //"xxxx"
                    //"dloy"
                    //"pwillett"
                    //"mreyes"
                    );
            if (false) profiles = cl.getNames(
                    submitter,
                    "8L75av8z",
                    //"xxx",
                    submitter
                    //"sfisher"
                    //"xxxx"
                    //"dloy"
                    //"pwillett"
                    //"mreyes"
                    );
            if (true) profiles = cl.getNames(
                    "dloy",
                    "xxx",
                    //"xxx",
                    "dloy"
                    //"sfisher"
                    //"xxxx"
                    //"dloy"
                    //"pwillett"
                    //"mreyes"
                    );
            if (profiles == null) {
                System.out.println("testContains null");
            } else {
                System.out.println("For:" + submitter);
                System.out.println("testContains:" + profiles.size());
                for (String profile : profiles) {
                    System.out.println("profile>" + profile + "<");
                }
            }
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
            
        }
    }
    
    //@Test
    public void testExtract()
        throws TException
    {
        CertsLDAP cl = null;
        File certFile = null;
        String lastProp = null;
        try {
            //certFile = new File("jssecacerts-create3");
            certFile = new File("/replic/mrtHomes/sword/jssecacert");

            //String hostName = "uc3-mrt-wrk1-stg.cdlib.org:1636";
            //String hostName = "uc3-ldap-stg-lb.cdlib.org:1636";
            String hostName = "uc3-ldap-dev.cdlib.org:1636";
            cl = new CertsLDAP(certFile, hostName);
            List<String> profiles = null;
            String submitter = "ucb_dash_submitter";
            //String submitter = "dloy";
            if (false) profiles = cl.getNames(
                    "merritt",
                    "merritt",
                    //"xxx",
                    //"ucop_dash_submitter"
                    submitter
                    //"sfisher"
                    //"xxxx"
                    //"dloy"
                    //"pwillett"
                    //"mreyes"
                    );
            if (false) profiles = cl.getNames(
                    submitter,
                    "8L75av8z",
                    //"xxx",
                    submitter
                    //"sfisher"
                    //"xxxx"
                    //"dloy"
                    //"pwillett"
                    //"mreyes"
                    );
            if (true) profiles = cl.extractProfiles(
                    "dloy",
                    "xxx",
                    //"xxx",
                    "dloy"
                    //"sfisher"
                    //"xxxx"
                    //"dloy"
                    //"pwillett"
                    //"mreyes"
                    );
            if (profiles == null) {
                System.out.println("testContains null");
            } else {
                System.out.println("For:" + submitter);
                System.out.println("testContains:" + profiles.size());
                for (String profile : profiles) {
                    System.out.println("profile>" + profile + "<");
                }
            }
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
            
        }
    }

}