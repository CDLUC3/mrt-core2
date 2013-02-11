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
public class NSMapTest
{
    protected static final String NL = System.getProperty("line.separator");
    private LoggerInf logger = null;
    
    public NSMapTest() {
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
    public void testNSMap()
    {
       String ret = null;
       String testName = "resources/XMLManifestNS.txt";
       //String testName = "testresources/basic-man-test-NoNS.txt";
       try {
           setUp();
           NSMap map = new NSMap(testName, logger);
           System.out.println("Map size=" + map.size());
           assert(map.size() > 1);
           NSMap.NSEntry entry = null;
           for (int i=0; i<50; i++) {
               entry = map.get(i);
               if (entry == null) break;
               System.out.println("[" + i + "]:" + entry.dump("" + i));
           }
           test(map, "PrimaryIDState");
           test(map, "FixityServiceState");
           assertTrue(true);
            
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }
    
    public void test(NSMap map, String name)
    {
        
           NSMap.NSEntry entry = map.getEntry(name);
           assertTrue(entry != null);
           assertTrue(name.contains(entry.className));
           System.out.println("Match:" + name);
    }
}