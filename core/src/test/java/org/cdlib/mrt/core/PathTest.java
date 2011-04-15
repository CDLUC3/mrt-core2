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
import java.io.ByteArrayInputStream;
import java.io.InputStream;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.cdlib.mrt.utility.DOMParser;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.XSLTUtil;


import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class PathTest {

    protected static final String NAME = "PathTest";
    protected static final String MESSAGE = NAME + ": ";
    public static final boolean DEBUG = true;
    public PathTest() {
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
            testIt(
                    // "/*[local-name()='mets']/*[local-name()='dmdSec']//*[local-name()='qualifieddc']//*",
                    "/*[local-name()='mets']" +
                    "/*[local-name()='dmdSec']" +
                    "/*[local-name()='mdWrap']" +
                    "/*[local-name()='xmlData']",
                    "*[local-name()='qualifieddc']",
                    "mets-dc.xml");
            testIt(
                    // "/*[local-name()='mets']/*[local-name()='dmdSec']//*[local-name()='qualifieddc']//*",
                    "/*[local-name()='mets']" +
                    "/*[local-name()='dmdSec']" +
                    "/*[local-name()='mdWrap']" +
                    "/*[local-name()='xmlData']",
                    "*[local-name()='mods']",
                    "mets-mods.xml");
            //testMets("mets-mods.xml");
            assertTrue(true);


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    public void testIt(
            String path1,
            String path2,
            String resourceName
            )
        throws TException
    {
        try {

            LoggerInf logger = new TFileLogger(NAME, 50, 50);

            InputStream metsStream = getResource(resourceName);
            String metsS = StringUtil.streamToString(metsStream, "utf-8");
            System.out.println("METS:" + metsS);
            metsStream = getResource(resourceName);
            Document mets = getDocument(metsStream, logger);
            findIt(path1, path2, mets, logger);
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    protected void findIt (
            String path1,
            String path2,
            Document doc,
            LoggerInf logger)
        throws TException
    {

        try {

            Element root = doc.getDocumentElement();
            //extract all of the file elements
            NodeList list = null;
            //to accommodate possibility that namespace is not default,
            //use xpath local-name for this whole section.
            list = DOMParser.getNodeList(root, path1, logger);
            int size1 = list.getLength();
            Element fileNode = (Element)list.item(0);
            if (DEBUG) System.out.println("***size1=" + size1);

            if (size1 == 0)  System.out.println("NOT FOUND");
            else {
                System.out.println("1 FOUND");

                fileNode = (Element)list.item(0);

                list = DOMParser.getNodeList(fileNode, path2, logger);
                int size2 = list.getLength();
                if (DEBUG) System.out.println("***size2=" + size2);

                if (size2 == 0)  System.out.println("2 NOT FOUND");
                else System.out.println("2 FOUND");
                }

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
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