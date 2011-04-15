/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.tools.loader;

import org.cdlib.mrt.tools.loader.BatchLoader;
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
public class StoreLoaderTest
{
    protected static final String NAME = "StoreLoaderTest";
    protected static final String MESSAGE = NAME + ": ";

    protected static final String NL = System.getProperty("line.separator");
    private LoggerInf logger = null;
    private Properties props = null;
    
    public StoreLoaderTest() {
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

    @Test
    public void dummy()
    {
        assertTrue(true);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    //@Test
    public void testStoreLoader()
         throws TException
    {
        TFrame framework = null;
        try {
           String propertyList[] = {
                "testresources/StoreManifest.properties"};
            framework = new TFrame(propertyList, "StoreLoader");

            // Create an instance of this object
            StoreLoader test = new StoreLoader(framework);
            test.processList();
            assertTrue(true);
        }
        catch(Exception e)
        {
            if (framework != null)
            {
                framework.getLogger().logError(
                    "Main: Encountered exception:" + e, 0);
                framework.getLogger().logError(
                        StringUtil.stackTrace(e), 10);
            }
        }
    }
}
