/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.DirectoryStats;
import org.cdlib.mrt.utility.TFrame;
import java.io.File;
import java.io.FileOutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TRuntimeException;
import org.cdlib.mrt.utility.StringUtil;
/**
 *
 * @author dloy
 */
public class FileUtilTest 
{
    protected static final String NAME = "FileUtilTest";
    protected static final String MESSAGE = NAME + ": ";
    
    private TFrame tFrame = null;
    private File testDir = null;

    public FileUtilTest() {
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
            String propertyList[] = {
                    "testresources/TestLocal.properties"};
            tFrame = new TFrame(propertyList, NAME);
            
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex);
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDirFunctions()
    {
        String key = "FileUtilTest";
        try {
            File testDir = FileUtil.getTempDir(key, null);
            assertTrue(testDir.exists());
            FileUtil.populateTest(testDir, "", 0, 5);
            assertTrue("Populate file", true);
            DirectoryStats dirStats = FileUtil.getDirectoryStats(testDir);

            assertTrue("dirStats.fileCnt=" + dirStats.fileCnt
                    + " - dirStats.fileSize=" + dirStats.fileSize,
                    (dirStats.fileCnt == 120) && (dirStats.fileSize == 1080));
            File txtDir = new File(testDir, "A/AB/ABC/ABCD");
            assertTrue(txtDir.exists());
            String tName = "ABCDE.txt";
            File txt = new File(txtDir, "ABCDE.txt");
            assertTrue(txt.exists());
            String txtS = FileUtil.file2String(txt);
            assertTrue(tName.equals(txtS));
                        
            File tmpFile = FileUtil.copy2Temp(txt);
            String txt2S = FileUtil.file2String(tmpFile);
            assertTrue(tName.equals(txt2S));


            File testDir2 = FileUtil.getTempDir(key, null);
            FileUtil.copyDirectory(testDir, testDir2);
            
            DirectoryStats dirStats2 = FileUtil.getDirectoryStats(testDir);
            assertTrue("dirStats2.fileCnt=" + dirStats2.fileCnt
                    + " - dirStats2.fileSize=" + dirStats2.fileSize,
                    (dirStats2.fileCnt == 120) && (dirStats2.fileSize == 1080));

            FileUtil.deleteDir(testDir2);
            assertTrue(!testDir2.exists());

            File testDir3 = FileUtil.getTempDir(key, null);
            FileUtil.copyFile("A/AB/ABC/ABCD/ABCDE.txt", testDir, testDir3);
            File copyFileTxt = new File(testDir3, "A/AB/ABC/ABCD/ABCDE.txt");
            assertTrue(copyFileTxt.exists());
            String cftxtS = FileUtil.file2String(copyFileTxt);
            assertTrue(tName.equals(cftxtS));

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void testBackout()
    {
        String key = "testBackout";
        try {
            File tempDir = FileUtil.getTempDir(key, null);
            assertTrue(tempDir.exists());
            File txtABCD = new File(tempDir, "A/AB/ABC/ABCD");
            txtABCD.mkdirs();
            for (int i=0; i < 5; i++) {
                String name = "file" + i + ".txt";
                File outFile = new File(txtABCD, name);
                FileUtil.string2File(outFile, name);
            }
            dumpDir("txtABCD", txtABCD);

            File txtABCE = new File(tempDir, "A/AB/ABC/ABCE");
            txtABCE.mkdirs();
            for (int i=0; i < 5; i++) {
                String name = "file" + i + ".txt";
                File outFile = new File(txtABCE, name);
                FileUtil.string2File(outFile, name);
            }
            dumpDir("txtABCE", txtABCE);

            File txtABC = txtABCD.getParentFile();
            File txtAB = txtABC.getParentFile();
            File txtA = txtAB.getParentFile();
            
            System.out.println("Parent:" + txtABC.getCanonicalPath());
            dumpDir("txtABC children", txtABC);
            FileUtil.deleteDir(txtABCD);
            dumpDir("txtABC deleteDir(ABCD)", txtABC);
            assertTrue(!txtABCD.exists());

            FileUtil.deleteEmptyPath(txtABC);
            dumpDir("txtABC deleteEmpty path(ABC)", txtABC);
            assertTrue(txtABC.exists());

            FileUtil.deleteDir(txtABCE);
            dumpDir("txtABC deleteDir(ABCE)", txtABC);
            assertTrue(!txtABCE.exists());

            File txtB = new File(tempDir, "B");
            txtB.mkdir();
            dumpDir("tempDir add txtB", tempDir);
            
            FileUtil.deleteEmptyPath(txtABC);
            dumpDir("tempDir deleteEmptyPath(ABC)", tempDir);
            assertTrue(txtB.exists());
            assertTrue(tempDir.exists());
           
            FileUtil.deleteEmptyPath(txtB);
            dumpDir("testDir deleteEmptyPath(B) ", tempDir);
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void testURLEncodeFilePath()
    {
        try {
            testURLEncodeFilePath(
                    "/direct/abc,def/ghi[jklm]nop.txt",
                    "/direct/abc%2Cdef/ghi%5Bjklm%5Dnop.txt"
                    );
            testURLEncodeFilePath(
                    "/producer/President Yudof.mp4",
                    "/producer/President%20Yudof.mp4"
                    );

        } catch (Exception ex) {
            String msg = "testURLEncodeFilePath"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex);
            System.out.println(msg);
            assertFalse(msg, true);
        }
    }

    private void testURLEncodeFilePath(String path, String match)
    {
        try {
            String uri = FileUtil.getURLEncodeFilePath(path);
            String msg = "testURLEncodeFilePath "
                    + " - path=" + path
                    + " - match=" + match
                    + " - uri=" + uri;
            System.out.println(msg);
            assertTrue(msg, uri.equals(match));

        } catch (Exception ex) {
            String msg = "testURLEncodeFilePath"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex);
            System.out.println(msg);
            assertFalse(msg, true);
        }
    }


    @Test
    public void testBackoutStop()
    {
        String key = "testBackout";
        try {
            File tempDir = FileUtil.getTempDir(key, null);
            assertTrue(tempDir.exists());
            File txtABCD = new File(tempDir, "A/AB/ABC/ABCD");
            txtABCD.mkdirs();
            for (int i=0; i < 5; i++) {
                String name = "file" + i + ".txt";
                File outFile = new File(txtABCD, name);
                FileUtil.string2File(outFile, name);
            }
            dumpDir("txtABCD", txtABCD);

            File txtA = new File(tempDir, "A");
            dumpDir("testBackoutStop-txtA", txtA);
            File txtAB = new File(tempDir, "AB");
            dumpDir("testBackoutStop-txtAB", txtAB);
            File txtABC = new File(tempDir, "A/AB/ABC");
            dumpDir("testBackoutStop-txtABC", txtABC);
            FileUtil.deleteDir(txtABCD);
            dumpDir("testBackoutStop-txtABC delete(ABCD)", txtABC);

            FileUtil.deleteEmptyPath(txtABC, txtA);
            dumpDir("testBackoutStop-txtA deleteEmpty path(ABC)", txtA);
            assertTrue(txtA.exists());
            assertTrue(!txtAB.exists());

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void testCopyReg()
    {
        String key = "testCopyReg";
        try {
            System.out.println("Enter " + key);
            File tempDir = FileUtil.getTempDir(key, null);
            assertTrue(tempDir.exists());

            File txtA = new File(tempDir, "A");
            txtA.mkdirs();
            for (int i=0; i < 5; i++) {
                String name = "file" + i + ".txt";
                File outFile = new File(txtA, name);
                FileUtil.string2File(outFile, name);
            }
            dumpDir("txtA", txtA);
            File txtC = new File(tempDir, "C");
            FileUtil.copyDirectory(txtA, txtC);
            File txtAC = new File(txtA, "stuff");
            txtC.renameTo(txtAC);
            String dflat = "Dflat/0.18";
            File dflatFile = new File(txtA, "0=dflat_0.18");
            FileUtil.string2File(dflatFile, dflat);
            dumpDir("txtA final", txtA);

            File txtB = new File(tempDir, "B");
            txtB.mkdir();

            FileUtil.copyReg("xxx.*", txtA, txtB);
            dumpDir("testCopyReg-txtA", txtB);
            testCnt("testCopyReg-txtA", txtB, 0);

            FileUtil.copyReg("file.*", txtA, txtB);
            dumpDir("testCopyReg-txtB", txtB);
            testCnt("testCopyReg-txtB", txtB, 5);

            FileUtil.copyReg("st.*", txtA, txtB);
            dumpDir("testCopyReg-stB", txtB);
            testCnt("testCopyReg-stB", txtB, 6);

            FileUtil.copyReg("0\\=.*", txtA, txtB);
            dumpDir("testCopyReg-0=", txtB);
            testCnt("testCopyReg-0=", txtB, 7);

            File txtBStuff = new File(txtB, "stuff");
            dumpDir("testCopyReg-stuff", txtBStuff);
            testCnt("txtBStuff", txtBStuff, 5);

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    private void testCnt(String header, File testFile, int expectedCnt)
    {
        File [] files = testFile.listFiles();
        assertTrue(header + " Fails - files=" + files.length
                + " - expectedCnt=" + expectedCnt, files.length == expectedCnt);
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

    @Test
    public void testInc()
    {
        String key = "FileUtilTest";
        try {
            File temp = FileUtil.getTempFile("tmp", "tmp");
            FileUtil.incFile(temp, 20);
            String incS = FileUtil.file2String(temp);
            assertTrue("incS=" + incS, incS.equals("20"));
            FileUtil.incFile(temp, 30);
            incS = FileUtil.file2String(temp);
            assertTrue("incS=" + incS, incS.equals("50"));


        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void testTmp()
    {
        try {
            File temp = FileUtil.getTempFile("tmp", "tmp");
            temp = FileUtil.getTempFile(null, "tmp");
            temp = FileUtil.getTempFile("tmp", null);
            temp = FileUtil.getTempFile(null, null);
            temp = FileUtil.getTempFile("", "tmp");
            temp = FileUtil.getTempFile("tmp", "");
            temp = FileUtil.getTempFile("", "");
            temp = FileUtil.getTempFile("xx", "");
            assertTrue(true);


        } catch (Exception ex) {
            assertFalse("TestTmp"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }


}