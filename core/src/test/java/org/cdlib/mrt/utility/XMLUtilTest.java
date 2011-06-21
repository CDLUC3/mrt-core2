/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.XMLUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dloy
 */
public class XMLUtilTest {

    protected static final String NL = System.getProperty("line.separator");

    protected String test1 ="-test.<init>-test";
    protected String result1 = "-test.&lt;init&gt;-test";
    protected String test2 = "-test.&lt;init&gt;-test";
    protected String result2 = "-test.&amp;lt;init&amp;gt;-test";
    protected String test3 = "-test.don't-test";
    protected String result3 = "-test.don&apos;t-test";
    protected String test4 = "-test.\"nut\"-test";
    protected String result4 = "-test.&quot;nut&quot;-test";

    protected String test5 ="-test.<init>-test";
    protected String result5 = "-test.&#60;init&#62;-test";
    protected String test6 = "-test.&lt;init&gt;-test";
    protected String result6 = "-test.&#38;lt;init&#38;gt;-test";
    protected String test7 = "-test.don't-test";
    protected String result7 = "-test.don&#39;t-test";
    protected String test8 = "-test.\"nut\"-test";
    protected String result8 = "-test.&#34;nut&#34;-test";
    public XMLUtilTest() {
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
    public void testEncode()
    {
        try {
            testEncode(test1, result1);
            testEncode(test2, result2);
            testEncode(test3, result3);
            testEncode(test4, result4);
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void testDecode()
    {
        try {
            testDecode(result1, test1);
            //testDecode(result2, test2);
            testDecode(result3, test3);
            testDecode(result4, test4);
            
            testDecode(result5, test5);
            testDecode(result6, test6);
            testDecode(result7, test7);
            testDecode(result8, test8);
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    protected void testEncode(String in, String match)
    {
        try {
            System.out.println("***testEncode");
            System.out.println("in*=" + in);
            String out = XMLUtil.encodeValue(in);
            System.out.println("out=" + out);
            assertTrue("FAIL: out=" + out + " - match=" + match, out.equals(match));
            
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }

    }

    protected void testDecode(String in, String match)
    {
        try {
            System.out.println("***testDecode");
            System.out.println("in*=" + in);
            System.out.println("mch=" + match);
            String out = XMLUtil.decode(in);
            System.out.println("out=" + out);
            assertTrue(out.equals(match));

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }

    }

}