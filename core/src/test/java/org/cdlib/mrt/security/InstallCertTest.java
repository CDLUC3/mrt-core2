/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.security;
import org.cdlib.mrt.core.*;
import java.io.File;
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

import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class InstallCertTest {

    protected static final String NAME = "LDAPAuthenticationTest";
    protected static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");

    //protected final static String HOSTS = "badger.cdlib.org:1636";
    //protected final static String HOSTS = "dp01.cdlib.org:1636";
    //protected final static String HOSTS = "coot.ucop.edu:1636";
    //protected final static String HOSTS = "ferret.cdlib.org:1636";
    //protected final static String HOSTS = "dp08.cdlib.org:1636";
    //protected final static String HOSTS = "merritt.cdlib.org:1636";
    //protected final static String HOSTS = "uc3-mrt-wrk1-stg.cdlib.org:1636";
    protected final static String HOSTS = "uc3-ldap-dev.cdlib.org:1636";
    public InstallCertTest() {
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
        try {

            String [] hostNames = {
                   //"badger.cdlib.org:1636",
                   //"uc3-mrt-wrk1-stg.cdlib.org:1636"
                   //"uc3-ldap-dev.cdlib.org"
                    "uc3-ldap-dev.cdlib.org:1636"
                   //"uc3-mrt-wrk2-stg.cdlib.org:1636"
    //protected final static String HOSTS = "dp01.cdlib.org:1636";
                    //"coot.ucop.edu:1636",
                    //"ferret.cdlib.org:1636",
                    //"dp08.cdlib.org:1636"
    //protected final static String HOSTS = "merritt.cdlib.org:1636";
            };
            File outFile = new File("jssecacerts");
            
            outFile = new File("/replic/mrtHomes/sword/jssecacert2");
            for (String hostName : hostNames) {
                testit(hostName, outFile);
            }

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    public void testit(String hostName, File outFile)
        throws TException
    {
        try {
            System.out.println("hostName=" + hostName + "\n"
                    + "outFile=" + outFile.getCanonicalPath() + "\n"
                    );
            InstallCert.install(hostName, "1", null, outFile);
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }


}