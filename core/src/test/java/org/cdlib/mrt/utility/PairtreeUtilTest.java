/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import org.cdlib.mrt.utility.PairtreeUtil;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.*;
import org.cdlib.mrt.core.Identifier;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

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
public class PairtreeUtilTest {
    protected static final String NAME = "PairtreeUtilTest";
    protected static final String MESSAGE = NAME + ": ";

    protected File directory = null;

    public PairtreeUtilTest() {
    }

    @Test
    public void test1()
    {
        char[] carr = new char[1];
        byte[] barr = new byte[1];
        String s = null;
        String value = null;
        String replace = "/:.";
        String replaceWith = "=+,";
        try {
            for (char c=0x0; c<=0xff; c++) {
                carr[0] = c;
                s = new String(carr);
                value = PairtreeUtil.getPairName(s);
                if ((c >= 0x0) && (c <0x21))
                    assertHex1(c, value);
                else if (c == 0x5c)
                    assertHex1(c, value);
                else if (c == 0x7f)
                    assertHex1(c, value);
                else if (c > 0x7f)
                    assertHex2(c, value);
                else if ("\"<?*=^+>|,".contains(s))
                    assertHex1(c, value);
                else if (replace.contains(s)) {
                    int pos = replace.indexOf(s);
                    if (pos >= 0) {
                        carr[0] = replaceWith.charAt(pos);
                        s = new String(carr);
                    }
                    assertChar(c, s, value);
                }
                else assertChar(c, s, value);
            }


        } catch (Exception ex) {
             System.out.println(MESSAGE + "Exception:" + ex);
             assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
        
    }
    
    protected void assertHex1(char test, String value)
    {
        String thex = hex(test);
        System.out.println("test1(" + test + "):" + hex(test) + " - value=" + value);
        assertTrue("Fails:" + test, value.length() == 3);
        assertTrue("Fails:" + test, value.charAt(0) == '^');
        assertTrue("Fails:" + test, value.contains(thex));
        
    }

    protected void assertHex2(char test, String value)
    {
        System.out.println("test2=(" + test + "):" + hex(test) + " - value=" + value);
        assertTrue("Fails:" + test, value.length() == 6);
        assertTrue("Fails:" + test, value.charAt(0) == '^');
        assertTrue("Fails:" + test, value.charAt(3) == '^');

    }

    protected void assertChar(char test, String match, String value)
    {
        System.out.println("testC=(" + test + "):" + hex(test) + " - value=" + value);
        assertTrue("Fails:" + hex(test), match.equals(value));

    }

    public static String hex(char c)
    {
        String hexstr = Integer.toString(c, 16);
        if (hexstr.length() == 1) hexstr = "0" + hexstr;
        return hexstr;
    }

    @Test
    public void testPairBackout()
    {
        try {
            File temp = FileUtil.getTempDir("base");
            Identifier id1 = new Identifier("abcdef1");
            Identifier id2 = new Identifier("abcdef2");
            File pair1 = PairtreeUtil.getPairDirectory(temp, id1.getValue());
            pair1.mkdirs();
            System.out.println("testPairBackout pair1:" + pair1.getCanonicalPath());
            File test1 = new File(temp, "ab/cd/ef/1/abcdef1");
            assertTrue(test1.exists());
            File pair2 = PairtreeUtil.getPairDirectory(temp, id2.getValue());
            pair2.mkdirs();
            System.out.println("testPairBackout pair2:" + pair2.getCanonicalPath());
            File test2 = new File(temp, "ab/cd/ef/2/abcdef2");
            assertTrue(test2.exists());
            File ab = new File(temp, "ab");
            boolean remove1 = PairtreeUtil.removePairDirectory(pair1);
            assertTrue(remove1);
            assertTrue(!test1.exists());
            assertTrue(test2.exists());
            boolean remove2 = PairtreeUtil.removePairDirectory(pair2);
            assertTrue(remove2);
            assertTrue(!ab.exists());
            assertTrue(!temp.exists());

        } catch (Exception ex) {
             System.out.println(MESSAGE + "Exception:" + ex);
             assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    protected void addFiles(File addDir)
    {
        try {
           for (int i=0; i < 5; i++) {
                String name = "file" + i + ".txt";
                File outFile = new File(addDir, name);
                FileUtil.string2File(outFile, name);
            }


        } catch (Exception ex) {
             System.out.println(MESSAGE + "Exception:" + ex);
             assertFalse(MESSAGE
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

}