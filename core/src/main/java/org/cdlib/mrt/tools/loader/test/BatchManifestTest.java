/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.tools.loader.test;

import org.cdlib.mrt.tools.loader.BatchManifest;
import org.cdlib.mrt.tools.loader.LoaderInfo;
import org.cdlib.mrt.formatter.FormatterInf;
import org.cdlib.mrt.formatter.FormatterAbs;
import org.cdlib.mrt.core.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.MessageDigestType;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.LoggerAbs;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;

/**
 *
 * @author dloy
 */
public class BatchManifestTest
{
    protected static final String NAME = "BatchManifestTest";
    protected static final String MESSAGE = NAME + ": ";

    protected static final String NL = System.getProperty("line.separator");
    private LoggerInf logger = null;
    private Properties props = null;
    
    public BatchManifestTest() {
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
            if (logger == null) {
                logger = LoggerAbs.getTFileLogger("testFormatter", 10, 10);
            }
            if (props == null) {
                String propertyList[] = {
                        "testresources/TestLocal.properties"};
                TFrame tFrame = new TFrame(propertyList, NAME);
                props = tFrame.getProperties();
            }

        } catch (Exception ex) {
            logger = null;
        }
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
    public void testBatchIngest()
    {
        try {
            LoaderInfo load1 = new LoaderInfo();
            File load1File = new File(
                    "C:/Documents and Settings/dloy/My Documents/CDL6/tomcat_base/webapps/collections/data/"
                    + "cdl_pub_owner"
                    );
            load1.extractDirectory=load1File;
            load1.localID = "load1";
            load1.primaryID = "ark:/13030/load1";
            load1.title = "title: load1";
            load1.creator = "creator: load1";

            LoaderInfo load2 = new LoaderInfo();
            File load2File = new File(
                    "C:/Documents and Settings/dloy/My Documents/CDL6/tomcat_base/webapps/collections/data/"
                    + "cdl_pub_service_level_agreement"
                    );
            load2.extractDirectory=load2File;
            load2.localID = "load2";
            load2.primaryID = "ark:/13030/load2";
            load2.title = "title: load2";

            LoaderInfo load3 = new LoaderInfo();
            File load3File = new File(
                    "C:/Documents and Settings/dloy/My Documents/CDL6/tomcat_base/webapps/collections/data/"
                    + "mrt_admin_owner"
                    );
            load3.extractDirectory=load2File;
            load3.localID = "load3";
            load3.primaryID = "ark:/13030/load3";
            load3.title = "title: load3";
            load3.creator = "creator: load3";
            Vector<LoaderInfo> loaders = new  Vector<LoaderInfo>();
            loaders.add(load1);
            loaders.add(load2);
            loaders.add(load3);
            File outputManifestDirectory = new File(
                    "C:/Documents and Settings/dloy/My Documents/CDLMerritt/data/loader/test"
                    );

            URL outputManifestBaseURL = new URL("http://localhost:28080/outdata");
            URL componentURLBase = new URL("http://localhost:28080/indata");
            BatchManifest bmanifest = BatchManifest.run(
                    "SHA-256",
                    loaders,
                    outputManifestDirectory,
                    outputManifestBaseURL,
                    componentURLBase,
                    logger
                );
            assertTrue(true);
        } catch (Exception ex) {
            assertFalse("TestTmp"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

}