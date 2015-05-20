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
    protected int c = 0x15;
    protected String c15 = Character.toString((char)c);
    protected String test9 = "-test." + c15 + "-test";
    protected String result9 = "-test.&#21;-test";
    
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
            testEncode(test9, result9);
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }
    
    @Test
    public void testEncodeLoop()
    {
        try {
            System.out.println("TEST ENCODING");
            for (int c=0; c<126; c++) {
                String ch = Character.toString((char)c);
                String esc = XMLUtil.encodeValue(ch);
                System.out.print("Char - " + c + " - ");
                if (esc.equals(ch)) {
                    System.out.println("EQUALS");
                } else {
                    System.out.print("NOT EQUALS:" + esc);
                    String m = XMLUtil.decode(esc);
                    if (esc.contains("&#")) {
                        if (m.equals(ch)) {
                            System.out.println(" - esc match");
                        }
                        else {
                            System.out.println(" - no esc match:" + m);
                            assertTrue("No match for c:" + c + " - char:" + ch, false);
                        }
                    } else System.out.println("");
                }
            }
            assertTrue(true);
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void testDecode()
    {
        try {
            testDecode(result1, test1, 1);
            //testDecode(result2, test2);
            testDecode(result3, test3, 3);
            testDecode(result4, test4, 4);
            
            testDecode(result5, test5, 5);
            testDecode(result6, test6, 6);
            testDecode(result7, test7, 7);
            testDecode(result8, test8, 8);
            testDecode(result9, test9, 9);
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
            System.out.println("out='" + out + "'");
            System.out.println("mch='" + match + "'");
            System.out.println("test=" + out.equals(match));
            assertTrue("FAIL: out=" + out + " - match=" + match, out.equals(match));
            
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }

    }

    protected void testDecode(String in, String match, int test)
    {
        try {
            System.out.println("***testDecode:" + test);
            System.out.println("in*='" + in + "'");
            System.out.println("mch='" + match + "'");
            String out = XMLUtil.decode(in);
            System.out.println("out='" + out + "'");
            System.out.println("test=" + out.equals(match));
            if (!out.equals(match)) {
                System.out.println("length - out=" + out.length() + " - match=" + match.length());
            }
            assertTrue(out.equals(match));

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }

    }
    
    

}