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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;

import org.cdlib.mrt.formatter.*;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.LoggerAbs;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;


import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class PingStateTest {

    protected static final String NAME = "DCTest";
    protected static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");
    protected LoggerInf logger = null;
    public PingStateTest() {
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
            logger = LoggerAbs.getTFileLogger("testFormatter", 10, 10);
        } catch (Exception ex) {
            logger = null;
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void pingTest()
        throws TException
    {
        try {
            FormatterInf formatter = FormatterAbs.getXMLFormatter(logger);
            testIt1(formatter, false);
            testIt1(formatter, true);


        } catch (Exception ex) {
            
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    public void testIt1(FormatterInf formatter, boolean gc)
        throws TException
    {
        try {
            setUp();
            PingState pingState = new PingState("PingStateTest", gc);
            pingState.tally.bump("test");
            pingState.tally.bump("test");
            pingState.prop.setProperty("myTest", "what will it do");
            System.out.println(formatIt(formatter, pingState));
            assertTrue(true);


        } catch (Exception ex) {
            
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    public void test2(boolean gc)
        throws TException
    {
        try {
            setUp();
            DumpIt[] dumps = new DumpIt[4];
            
            dumps[0] = new DumpIt("ANVL", FormatterAbs.getANVLFormatter(logger));
            dumps[1] = new DumpIt("JSON", FormatterAbs.getJSONFormatter(logger));
            dumps[2] = new DumpIt("XML", FormatterAbs.getXMLFormatter(logger));
            dumps[3] = new DumpIt("XHTML", FormatterAbs.getXHTMLFormatter(logger));
                    
            
            long startTime = DateUtil.getEpochUTCDate();
            PingState pingState = new PingState("PingStateTest", gc);
            pingState.tally.bump("test");
            pingState.tally.bump("test");
            pingState.prop.setProperty("myTest", "what will it do");
            pingState.bumpCmd("test2a", startTime);
            pingState.bumpCmd("test2b", startTime);
            pingState.bumpCmd("test2b", startTime);
            for (DumpIt dump: dumps) {
                printIt("test2(" + gc + ").." + dump.type, dump.formatter, pingState);
                assertTrue(true);
            }


        } catch (Exception ex) {
            
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    @Test
    public void testIt2()
        throws TException
    {
            test2(false);
            test2(true);
    }
   
    public String formatIt(
            FormatterInf formatter,
            StateInf responseState)
    {
        try {
           ByteArrayOutputStream outStream = new ByteArrayOutputStream(5000);
           PrintStream  stream = new PrintStream(outStream, true, "utf-8");
           formatter.format(responseState, stream);
           stream.close();
           byte [] bytes = outStream.toByteArray();
           String retString = new String(bytes, "UTF-8");
           return retString;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            System.out.println("Trace:" + StringUtil.stackTrace(ex));
            assertFalse("Exception:" + ex, true);
            return null;
        }
    }
   
    public void printIt(
            String header,
            FormatterInf formatter,
            StateInf responseState)
    {
        try {
            System.out.println("\n\n***> " + header + " <*****************************\n"
                + formatIt(formatter, responseState) + "\n"
                + "---------------------------\n\n"
                    );

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            System.out.println("Trace:" + StringUtil.stackTrace(ex));
            assertFalse("Exception:" + ex, true);
        }
    }
    
    public static class DumpIt {
        public String type = null;
        public FormatterInf formatter = null;
        public DumpIt(String type,  FormatterInf formatter) 
        {
            this.type = type;
            this.formatter = formatter;
        }
    }

}