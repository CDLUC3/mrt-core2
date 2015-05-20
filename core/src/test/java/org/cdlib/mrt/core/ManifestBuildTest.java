/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;

import org.cdlib.mrt.core.ManifestBuild;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 *
 * @author dloy
 */
public class ManifestBuildTest {

    public ManifestBuildTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void TestIt()
        throws TException
    {
        try {
            File tempDir = FileUtil.getTempDir("testURL");
            File t1 = new File(tempDir, "abc,def");
            t1.mkdir();
            File t2 = new File(t1, "ghi+jkl=m");
            t2.mkdir();
            File xFile = new File(t2, "zyx[jklm]nop.txt");
            FileUtil.string2File(xFile, "abcdefghijklmnopqrstuvwxyz");
            File yFile = new File(t2, "abcdefg[jklm]nop.txt");
            FileUtil.string2File(yFile, "ABCDEFGHIJKOMNOPQRSTUVWXYZ");
            File zFile = new File(t2, "mrt-xxx.txt");
            FileOutputStream zStream = new FileOutputStream(zFile);
            zStream.close();
            dumpDir("TestIt", t2);
            File manifestFile = new File(tempDir, "manifest.txt");
            ManifestBuild.PropInfo propInfo = ManifestBuild.getPostManifest("http://myserve:350/store",
                    tempDir,
                    manifestFile);
            String manout = FileUtil.file2String(manifestFile);
            System.out.println("***manout***");
            System.out.println(manout);
            System.out.println("*********************");
            String testout1 = "http://myserve:350/store/abc%2Cdef/ghi%2Bjkl%3Dm/abcdefg%5Bjklm%5Dnop.txt | sha256 | 4f1c93ade2c34db17882aa285e49c52d057f0a6b200bab260f9e455747fa2281 | 26 |  | abc,def/ghi+jkl=m/abcdefg[jklm]nop.txt";
            String testout2 = "http://myserve:350/store/abc%2Cdef/ghi%2Bjkl%3Dm/zyx%5Bjklm%5Dnop.txt | sha256 | 71c480df93d6ae2f1efad1447c66c9525e316218cf51fc8d9ed832f2daf18b73 | 26 |  | abc,def/ghi+jkl=m/zyx[jklm]nop.txt";

            System.out.println("manifestFile=" + manout);
            assertTrue("testout1=" + testout1 + " -- manout=" + manout, manout.contains(testout1));
            assertTrue("testout2=" + testout2 + " -- manout=" + manout, manout.contains(testout2));

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }
    }


    private void dumpDir(String header, File infile)
        throws Exception
    {
            if ((infile == null) || !infile.exists()) {
                System.out.println(header + ": does not exist");
                return;
            }
            File [] files = infile.listFiles();
            if (files.length == 0) {
                System.out.println(header + ": empty");
                return;
            }
            for (File file: files) {
                String name = file.getCanonicalPath();
                System.out.println(header + ":" + name);
            }
    }
    
}