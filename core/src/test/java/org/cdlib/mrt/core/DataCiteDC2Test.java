/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;
import java.io.InputStream;
import java.util.Vector;
import static org.cdlib.mrt.core.DataCiteDCTest.MESSAGE;
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
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.XMLUtil;

import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class DataCiteDC2Test {

    protected static final String NAME = "DataCiteDC2Test";
    protected static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");
    public DataCiteDC2Test() {
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

    
    @Test
    public void testDataCite2DC_2_2()
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);

            System.out.println(MESSAGE + "***testDataCite2DC_2_2***");
            String dataCiteS = getResource("datacite-dc-2.2.xml");
            System.out.println("DataCite:\n" + dataCiteS);
            String dc = DataciteConvert.dataCite2dc(dataCiteS, logger);
            System.out.println("DublinCore:\n" + dc);
            assertTrue(dc.length() > 0);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    @Test
    public void testDataCite2DC_3()
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);

            System.out.println(MESSAGE + "***testDataCite2DC_3***");
            String dataCiteS = getResource("datacite-dc-3.xml");
            System.out.println("DataCite:\n" + dataCiteS);
            String dc = DataciteConvert.dataCite2dc(dataCiteS, logger);
            System.out.println("DublinCore:\n" + dc);
            assertTrue(dc.length() > 0);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    

    @Test
    public void testRemoveHeader()
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);
            System.out.println("***testRemoveHeader***");
            String dataCiteS = getResource("datacite-dc-2.2.xml");
            System.out.println("DataCite:\n" + dataCiteS.substring(0, 200));
            String remove = XMLUtil.removeXMLHeader(dataCiteS);
            System.out.println("---Remove:\n" + remove.substring(0,200));
            assertTrue("fails:" + remove.substring(0,20), remove.startsWith("<resource"));


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    


    /**
     * Gets a resource using the class loader
     *
     * @param resourceName Name of the file containing the resource. The name
     * may include a relative path which is interpreted as being relative to
     * the path "resources/" below the classpath root.
     * @return An inputstream for the resource
     */
    public String getResource(String resourceName)
        throws TException
    {
        try
        {
            InputStream dataCiteStream = getClass().getClassLoader().
                getResourceAsStream("testresources/" + resourceName);
            return StringUtil.streamToString(dataCiteStream, "utf-8");
        }
        catch(Exception e)
        {
               System.out.println(
                "MFrame: Failed to get the AdminManager for entity: " +
                "Failed to get resource: " +
                resourceName +
                " Exception: " + e);
           throw new TException.GENERAL_EXCEPTION(
                "Failed to get a resource. Exception: " + e);
        }
    }

}