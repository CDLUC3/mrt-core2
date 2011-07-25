/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;
import java.io.InputStream;
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
import org.cdlib.mrt.utility.StringUtil;

import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class DCTest {

    protected static final String NAME = "DCTest";
    protected static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");
    public DCTest() {
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
    public void TestBoth()
        throws TException
    {
        try {
            boolean dumpMets = false;
            LinkedHashList<String, String> retList = null;
            retList = testMets(dumpMets, "mets-dc.xml");
            assertTrue(retList.size() > 5);
            retList = testMets(dumpMets, "mets-mods.xml");
            assertTrue(retList.size() > 5);
            retList = testMets(dumpMets, "mets-mods2.xml");
            assertTrue(retList.size() > 5);
            retList = testMets(dumpMets, "mets-dc-no-qualifieddc.xml");
            assertTrue(retList.size() > 5);
            assertTrue(true);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    @Test
    public void TestNS()
        throws TException
    {
        try {
            testModsNS(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><mods:mods><mods:titleInfo>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><mods:mods xmlns:mods=\"http://www.loc.gov/mods/v3\"><mods:titleInfo>");
            testModsNS(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><qqqqq:mods><qqqqq:titleInfo>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><qqqqq:mods xmlns:qqqqq=\"http://www.loc.gov/mods/v3\"><qqqqq:titleInfo>");
            assertTrue(true);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    protected void testModsNS(String xmlBefore, String xmlAfter)
        throws TException
    {
        try {

            String insert = DC.setModsNS(xmlBefore);
            System.out.println("MODSNS=" + insert);

            assertTrue(
                    " - in:" + insert
                    + " - ret:" + insert
                    + " - exp:" + xmlAfter
                    , insert.equals(xmlAfter));


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    public LinkedHashList<String, String> testMets(
            boolean dumpMets,
            String resourceName
            )
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);

            InputStream metsStream = getResource(resourceName);
            String metsS = StringUtil.streamToString(metsStream, "utf-8");
            if (dumpMets) System.out.println("METS:" + metsS);
            metsStream = getResource(resourceName);
            Document mets = getDocument(metsStream, logger);
            LinkedHashList<String, String> list = DC.getDC (mets, logger);

            dumpList("*****" + resourceName + "*****", list);
            assertTrue(true);
            return list;


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    //@Test
    public void testMods()
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);

            InputStream metsStream = getResource("mets-mods.xml");
            String metsS = StringUtil.streamToString(metsStream, "utf-8");
            System.out.println("METS:" + metsS);
            metsStream = getResource("mets-mods.xml");
            Document mets = getDocument(metsStream, logger);
            LinkedHashList<String, String> list = new LinkedHashList<String, String>();
            DC.getDCFromModsMets (list, mets, logger);
            dumpList("*****TestMods*****", list);
            assertTrue(list.size() > 0);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    //@Test
    public void testDC()
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);

            InputStream metsStream = getResource("mets-dc.xml");
            String metsS = StringUtil.streamToString(metsStream, "utf-8");
            System.out.println("METS:" + metsS);
            metsStream = getResource("mets-dc.xml");
            Document mets = getDocument(metsStream, logger);
            LinkedHashList<String, String> list = new LinkedHashList<String, String>();
            DC.getMetsDC (list, mets, logger);
            dumpList("*****TestDC*****", list);
            assertTrue(list.size() > 0);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    //@Test
    public void testDCNoQualified()
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);

            InputStream metsStream = getResource("mets-dc-noqualifieddc.xml");
            String metsS = StringUtil.streamToString(metsStream, "utf-8");
            System.out.println("METS:" + metsS);
            metsStream = getResource("mets-dc.xml");
            Document mets = getDocument(metsStream, logger);
            
            LinkedHashList<String, String> list = new LinkedHashList<String, String>();
            DC.getMetsDC (list, mets, logger);
            dumpList("*****TestDC NOQUALIFIEDDC*****", list);
            assertTrue(list.size() > 0);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    protected void dumpList(String header, LinkedHashList<String, String> list)
            throws TException
    {
        try {
            System.out.println("Header:" + header);
            if ((list == null) || (list.size() == 0)) {
                System.out.println("dumpList empty");
            }
            for (String key : list.keySet()) {
                Vector<String> values = list.get(key);
                String value = null;
                System.out.println("Key:" + key);
                for (int i=0; i < values.size(); i++) {
                    value = values.get(i);
                    System.out.println("   [" + i + "]:" + value);
                }
            }
            
        } catch (Exception ex) {
            System.out.println(header + " - Exception:" + ex);
            throw new TException(ex);
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
    public InputStream getResource(String resourceName)
        throws TException
    {
        try
        {
            return getClass().getClassLoader().
                getResourceAsStream("testresources/" + resourceName);
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

    protected Document getDocument(InputStream instream, LoggerInf logger)
        throws TException
    {
        try {
            Document doc = DOMParser.doParse(instream, logger);
            return doc;

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            System.out.println(StringUtil.stackTrace(e));
            throw new TException(e);
        }
    }

}