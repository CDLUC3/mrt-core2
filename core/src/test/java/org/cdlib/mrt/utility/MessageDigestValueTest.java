/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.MessageDigestValue;
import org.cdlib.mrt.utility.MessageDigestType;
import java.io.InputStream;
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
public class MessageDigestValueTest {

    public MessageDigestValueTest() {
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
    public void testHexTo64BitToHex()
    {
        try {
            testHexTo64BitToHex("2a5de3f6f44cfb38e56ead632eb89f40");
            testHexTo64BitToHex("39d4750525f551deae9a565c709271c983cddb3a15203edd5e20d8e366f19713");
            testHexTo64BitToHex("2ed83e4c");
            
        } catch (Exception ex) {
            assertFalse("TestMD5"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }
    
    
    public void testHexTo64BitToHex(String hex)
    {
        try {
            System.out.println("testHexTo64BitToHex hex= " + hex);
            String testHexTo64Bit = MessageDigestValue.getChecksumBit64(hex);
            System.out.println("testHexTo64BitToHex testHexTo64Bit= " + testHexTo64Bit);
            String test64BitToHex = MessageDigestValue.getChecksumHex(testHexTo64Bit);
            System.out.println("testHexTo64BitToHex test64BitToHex= " + test64BitToHex);
            
            assertTrue(hex.equals(test64BitToHex));
            
        } catch (Exception ex) {
            assertFalse("TestMD5"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void TestMD5()
    {
        try {
            doFixity("MD5",
                    "2a5de3f6f44cfb38e56ead632eb89f40",
                    6842);
        } catch (Exception ex) {
            assertFalse("TestMD5"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void TestMD5Bit64()
    {
        try {
            String hexS = doFixity("MD5",
                    "2a5de3f6f44cfb38e56ead632eb89f40",
                    6842);
            String bit64 = MessageDigestValue.getChecksumBit64(hexS);
            System.out.println("****TestMD5Bit64 Bit64=\"" + bit64 + "\"");
            assertTrue(bit64.equals("Kl3j9vRM+zjlbq1jLrifQA=="));
            
        } catch (Exception ex) {
            assertFalse("TestMD5"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void TestSHA256()
    {
        try {
            doFixity("SHA-256",
                    "39d4750525f551deae9a565c709271c983cddb3a15203edd5e20d8e366f19713",
                    6842);
        } catch (Exception ex) {
            assertFalse("TestSHA256"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void TestCRC32()
    {
        try {
            doFixity("CRC-32",
                    "2ed83e4c",
                    6842);
        } catch (Exception ex) {
            assertFalse("TestSHA256"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void TestAdler32()
    {
        try {
            doFixity("Adler-32",
                    "e2897f65",
                    6842);
        } catch (Exception ex) {
            assertFalse("TestSHA256"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    public String doFixity(String processChecksum, String match, int matchSize)
    {
        try {
            LoggerInf logger = new TFileLogger("MessageDigestValueTest", 10, 10);
            InputStream inStream = ClassLoader.getSystemResourceAsStream("testresources/testfix.jpg");
            assertTrue(inStream != null);
            MessageDigestValue fixityTest = new MessageDigestValue(inStream, processChecksum, logger );
            long size = fixityTest.getInputSize();
            String checksum = fixityTest.getChecksum();
            MessageDigestType checksumType = fixityTest.getChecksumType();
            assertTrue("TestIt"
                    + " - size=" + size
                    + " - checksum=" + checksum,
                    size==matchSize
                            && checksum.equals(match)
                    );
            return checksum;
            
        } catch (Exception ex) {
            assertFalse("TestTmp"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }   return null;
    }


    @Test
    public void TestException()
    {
        try {
            String processChecksum = "MD5";
            LoggerInf logger = new TFileLogger("FixityTestsTest", 10, 10);
            InputStream inStream = ClassLoader.getSystemResourceAsStream("testresources/testfix.jpg");

            try {
                FixityTests tests = new FixityTests((InputStream)null, processChecksum, logger );
            } catch (Exception ex) {
                assertTrue(ex instanceof TException.INVALID_OR_MISSING_PARM);
            }

            try {
                FixityTests tests = new FixityTests(inStream, null, logger );
            } catch (Exception ex) {
                assertTrue(ex instanceof TException.INVALID_OR_MISSING_PARM);
            }

            try {
                FixityTests tests = new FixityTests(inStream, processChecksum, null );
            } catch (Exception ex) {
                assertTrue("Exception:" + ex
                        + "trace:" + StringUtil.stackTrace(ex),
                        ex instanceof TException.INVALID_OR_MISSING_PARM);
            }



        } catch (Exception ex) {
            assertFalse("TestTmp"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }


}