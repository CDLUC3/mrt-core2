/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;

import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.FileComponent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import org.cdlib.mrt.core.ManifestRowCheckmAbs;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;

/**
 *
 * @author dloy
 */
public class ManifestRowTest {
    protected LoggerInf logger = null;

    public ManifestRowTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        logger = new TFileLogger("ManifestRowTest", 1000, 1000);
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
    public void testPost()
        throws TException
    {
        try {
            File tempDir = FileUtil.getTempDir("testURL");
            File t1 = new File(tempDir, "test.txt");
            FileUtil.string2File(t1, "this is a test - and only a test");
            FileComponent component = new FileComponent();
            component.setIdentifier("test.txt");
            component.setURL("http://myserver.org:8888/this/is/a/test");
            setDigestSize(component, t1);
            component.setCreated();
            test("test1", component);
            component.setLocalID("thisiSLocal");
            test("test2", component);
            component.setPrimaryID("thisIsPrimaryID");
            test("test3", component);

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }
    }


    protected void test(String msg, FileComponent componentIn)
        throws TException
    {

        log(componentIn.dump("++++" + msg + "++++"));
        ManifestRowAbs.ManifestType manifestType = ManifestRowAbs.ManifestType.batch;
        ManifestRowCheckmAbs rowIn
                = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestType, logger);
        ManifestRowCheckmAbs rowOut
                = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestType, logger);
        rowIn.setFileComponent(componentIn);
        String lineIn = rowIn.getLine();
        log(lineIn);
        rowOut.setRow(lineIn);
        String lineOut = rowOut.getLine();
        log("***>line:" + lineOut);
        assertTrue(lineIn.equals(lineOut));

    }


    protected void log(String msg)
    {
        logger.logMessage(msg, 0);
    }

    protected void setDigestSize( FileComponent fileComponent, File testFile)
        throws TException
    {
        try {
            FixityTests fixityTest = new FixityTests(testFile, "sha-256", logger);
            fileComponent.addMessageDigest(fixityTest.getChecksum(), fixityTest.getChecksumJavaAlgorithm());
            fileComponent.setSize(fixityTest.getInputSize());
            logger.logMessage("setFixity set", 10);

        } catch (Exception ex) {
            throw new TException(ex);

        }
    }
    
}