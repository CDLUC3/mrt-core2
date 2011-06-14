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
public class FixityTestsTest {

    public FixityTestsTest() {
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

    public void doFixity(String processChecksum, String match, int matchSize)
    {
        try {
            LoggerInf logger = new TFileLogger("FixityTestsTest", 10, 10);
            InputStream inStream = ClassLoader.getSystemResourceAsStream("testresources/testfix.jpg");
            assertTrue(inStream != null);
            FixityTests fixityTest = new FixityTests(inStream, processChecksum, logger );
            long size = fixityTest.getInputSize();
            String checksum = fixityTest.getChecksum();
            MessageDigestType checksumType = fixityTest.getChecksumType();
            MessageDigestType processChecksumType = fixityTest.getAlgorithm(processChecksum);
            assertTrue("TestIt"
                    + " - size=" + size
                    + " - checksumType=" + checksumType
                    + " - checksum=" + checksum,
                    (size==matchSize)
                            && (checksumType == processChecksumType)
                            && checksum.equals(match)
                    );

            FixityTests.FixityResult result = fixityTest.validateSizeChecksum(checksum, checksumType.toString(), size);
            assertTrue(result.checksumMatch && result.fileSizeMatch);

            result = fixityTest.validateSizeChecksum(checksum, checksumType.toString(), size+1);
            assertTrue(result.checksumMatch && !result.fileSizeMatch);
            result = fixityTest.validateSizeChecksum(
                    "3a5de3f6f44cfb38e56ead632eb89f40",
                    processChecksum,
                    size);
            assertTrue(!result.checksumMatch && result.fileSizeMatch);

            try {
                result = fixityTest.validateSizeChecksum(checksum, processChecksum, size+1);
            } catch (Exception ex) {
                assertTrue(ex instanceof TException.INVALID_OR_MISSING_PARM);
            }

        } catch (Exception ex) {
            assertFalse("TestTmp"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
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