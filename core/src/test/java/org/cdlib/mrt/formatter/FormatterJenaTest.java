/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.formatter;

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
import java.util.Vector;

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
public class FormatterJenaTest
{
    protected static final String NL = System.getProperty("line.separator");
    private LoggerInf logger = null;
    
    public FormatterJenaTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test 
    public void testBasic()
    {
       testIt("testReference1", "testresources/basic-prop-test-NS.properties");
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
   public void testIt(String header, String mapName)
   {
       String ret = null;
       try {
           if (logger == null)
               logger = LoggerAbs.getTFileLogger("testFormatter", 10, 10);
           
           FormatterInf turtle = FormatterAbs.getTurtleFormatter(
                   mapName,
                   logger);

           FormatterInf rdf = FormatterAbs.getRDFFormatter(
                   mapName,
                   logger);
           TestA testA = new TestA();
           ret = formatIt(turtle, testA);
           System.out.println("********** map:" + mapName);
           System.out.println("*** " + header + " Turtle ***" + NL + ret);
           ret = formatIt(rdf, testA);
           System.out.println("*** " + header + " RDF ***" + NL + ret);

           assertTrue(true);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
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
            assertFalse("Exception:" + ex, true);
            return null;
        }
    }

    @Test (expected=org.cdlib.mrt.utility.TException.INVALID_OR_MISSING_PARM.class)
    public void testException()
        throws TException
    {
        MessageDigest digest5 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", "shaxxx");
    }

    private class TestA implements StateInf
    {
        public int getId()
        {
            return 10;
        }
        
        public String getValue()
        {
            return "this is a value";
        }

        public Integer getIdentifier()
        {
            return 12345;
        }

        public String getResource()
        {
            return "http://Merritt.cdlib.org/mytest";
        }
        

        public Integer getNull()
        {
            return null;
        }

        public Integer getInteger()
        {
            return 0;
        }

        public LinkedHashList getHashMap()
        {
            LinkedHashList<String, String> list = new LinkedHashList<String, String>(20);
            list.put("key3", "value2");
            list.put("key3","value3");
            list.put("key1", "value1");
            list.put("key1","value2");
            list.put("key2", "value1");
            list.put("key3", "value1");
            list.put("key3","value2");
            return list;
        }

        public LinkedHashList getManyURLsSameName()
        {
            try {
                String key = "node";
                LinkedHashList<String, String> list = new LinkedHashList<String, String>(20);
                String urlS = "http://url.base";
                for (int i=0; i < 10; i++) {
                    
                    list.put(key, "" + new URL(urlS + i));
                }
                return list;
                
            } catch (Exception ex) {
                return null;
            }
        }

        public LinkedHashList getManyURLsDiffName()
        {
            try {
                String key = "node";
                LinkedHashList<String, String> list = new LinkedHashList<String, String>(20);
                String urlS = "http://url.base";
                for (int i=0; i < 10; i++) {

                    list.put(key + "-" + i, "" + new URL(urlS + i));
                }
                return list;

            } catch (Exception ex) {
                return null;
            }
        }
         
    }
}