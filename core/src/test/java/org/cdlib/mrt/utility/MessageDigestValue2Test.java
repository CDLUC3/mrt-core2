/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import java.io.File;
import java.io.FileInputStream;
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
public class MessageDigestValue2Test {

    public MessageDigestValue2Test() {
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
    public void dummyTest()
    {
        assertTrue(true);
    }
    
    //@Test
    public void TestPadCRC()
    {
        try {
            File test = new File("C:/Documents and Settings/dloy/My Documents/Bugs/Storage/ucsd/1-1.pdf");
            InputStream stream = new FileInputStream(test);
       
            doFixity(stream,
                    "crc-32",
                    "0a253086",
                    1265228);
        } catch (Exception ex) {
            assertFalse("TestPadCRC"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }


    public void doFixity(InputStream inStream, String processChecksum, String match, int matchSize)
    {
        try {
            LoggerInf logger = new TFileLogger("MessageDigestValueTest", 10, 10);
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

        } catch (Exception ex) {
            assertFalse("TestTmp"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }


}