/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.tools.loader.test;

import org.cdlib.mrt.tools.loader.StoreLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


import java.util.Properties;

import org.cdlib.mrt.utility.TFrame;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.LoggerAbs;
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
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
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