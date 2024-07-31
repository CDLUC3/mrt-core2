/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Enumeration;
import java.util.TimeZone;
import java.io.File;
import java.io.FileInputStream;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;

/**
 *
 * @author dloy
 */
public class ManifestRowBatchTest {
    protected LoggerInf logger = null;

    protected final static String NL = System.getProperty("line.separator");
    public static final String[] linesxx = {
                "#%prefix | nfo:<http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#>",
                "#%prefix | nie:<http://www.semanticdesktop.org/ontologies/2007/03/22/nie#>",
                "#%prefix | mrt:<http://merritt.ontology.cdlib.org/ns#>",
                "#%columns | nfo:fileURL nfo:hashAlgorithm nfo:hashValue nfo:fileSize nfo:fileCreated mrt:targetFilePath nie:primaryID mrt:localID nie:mimeType"
            };

   public static final String[] batchHeader1 = {
                "#%profile | http://uc3.cdlib.org/registry/mrt/mrt-batch-manifest"
            };

   public static final String[] batchHeader2 = {
                "#%profile | http://uc3.cdlib.org/registry/ingest/manifest/mrt-single-file-batch-manifest"
            };

   public static final String[] batchHeader3 = {
                "#%profile | http://uc3.cdlib.org/registry/ingest/manifest/mrt-batch-manifest"
            };

   public static final String[] batchHeader4 = {
                "#%profile | http://uc3.cdlib.org/registry/ingest/manifest/mrt-container-batch-manifest"
            };

   public static final String [] batchData = {
                "http://localhost:28080/feederout/1383111061164171/manifest1.txt | sha256 | a05f45e03e11d730f4e787a898e07c26eb760fb5aa27472b1ca9864ed900dc1c | 739 | 2010-06-02T13:54:43-07:00 | manifest1.txt | ark://ark1",
                "http://localhost:28080/feederout/1383111061164171/manifest2.txt | sha256 | 4555c33a333b94e9fde19cc8e2e9169c36ae1e920e485150f619812d55f2bce7 | 278 | 2010-06-02T13:54:44-07:00 | manifest2.txt |  | local://id1",
                "http://localhost:28080/feederout/1383111061164171/manifest3.txt | sha256 | 4555c33a333b94e9fde19cc8e2e9169c36ae1e920e485150f619812d55f2bce7 | 278 | 2010-06-02T13:54:44-07:00 | manifest3.txt |  | local://id2 | me | my title | 2010-06-02T13:54:44-07:00",
                "http://localhost:28080/feederout/1383111061164171/manifest4.txt | sha256 | 4555c33a333b94e9fde19cc8e2e9169c36ae1e920e485150f619812d55f2bce7 | 278 | 2010-06-02T13:54:44-07:00 | manifest4.txt |  | local://id3 | me |  | 2010-06-02T13:54:44-07:00"
            };

    public ManifestRowBatchTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
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
    public void testBatch()
        throws TException
    {
        try {
            testIt("testBatch1",
                ManifestRowAbs.ManifestType.batch,
                batchHeader1,
                batchData);
            
            testIt("testBatch2",
                ManifestRowAbs.ManifestType.batch,
                batchHeader2,
                batchData);
            testIt("testBatch3",
                ManifestRowAbs.ManifestType.batch,
                batchHeader3,
                batchData);
            testIt("testBatch4",
                ManifestRowAbs.ManifestType.batch,
                batchHeader4,
                batchData);

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }

    }
    @Test
    public void testBuildBatch()
        throws TException
    {
        try {
            buildManifest("testBatch1",
                batchHeader1,
                batchData);
            buildManifest("testBatch2",
                batchHeader2,
                batchData);
           
            buildManifest("testBatch3",
                batchHeader3,
                batchData);
            buildManifest("testBatch4",
                batchHeader4,
                batchData);

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }

    }

    public void testIt(
            String dispHeader,
            ManifestRowAbs.ManifestType manifestType,
            String [] headers,
            String [] data
            )
        throws TException
    {
        try {
            System.out.println("****" + dispHeader + "****");
            System.out.println("testIT"
                    + " - manifestType=" + manifestType.toString()
                    );
            System.out.println("TestIT headers");
            for (String header : headers) {
                System.out.println("header:" + header);
            }
            ManifestRowCheckmAbs rowInFact
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestType, logger);
            ManifestRowCheckmAbs rowOut
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestType, logger);
            rowInFact.handleHeaders(headers);
            rowOut.handleHeaders(headers);
            assertTrue(true);

            for (String lineIn : data) {
                ManifestRowCheckmAbs rowIn = rowInFact.getManifestRow(lineIn);

                System.out.println("+++testIt(" + dispHeader + "): - getProfile:" + rowIn.getProfile());
                FileComponent componentIn = rowIn.getFileComponent();
                rowOut.setFileComponent(componentIn);
                System.out.println(componentIn.dump("testIt-componentIn[" + lineIn + "]"));
                FileComponent componentOut = rowOut.getFileComponent();
                System.out.println(componentOut.dump("testIt-componentOut[" + lineIn + "]"));
                String lineOut = rowOut.getLine();
                String ermsg ="****>diff"
                        + " - lineIn =[" + lineIn + "]"
                        + " - lineOut=[" + lineOut + "]";
                System.out.println(ermsg);
                assertTrue(ermsg, lineIn.equals(lineOut));
            }
            assertTrue(true);


        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    protected void testED(String in)
    {
        String enc = Checkm.enc(in);
        String dec = Checkm.dec(enc);
        boolean match = dec.equals(in);
        System.out.println("testED"
                + " - in=[" + in + "]"
                + " - match=[" + match + "]"
                + " - enc=[" + enc + "]"
                + " - dec=[" + dec + "]"
                );
        assertTrue(match);

    }

    public void buildManifest(
            String dispHeader,
            String [] headers,
            String [] data
            )
        throws TException
    {
        System.out.println("####" + dispHeader + "####");
        ManifestRowAbs.ManifestType manifestInType = ManifestRowAbs.ManifestType.batch;
        ManifestRowAbs.ManifestType manifestOutType = ManifestRowAbs.ManifestType.batch;
        File outManifestFile = null;
        try {
            String profile = Checkm.getProfile(headers[0]);
            if (profile == null) {
                throw new TException.INVALID_OR_MISSING_PARM("badData=" + headers[0]);
            }
            System.out.println("Profile=" + profile);
            outManifestFile = FileUtil.getTempFile("tmp", "txt");
            ManifestRowCheckmAbs rowInFact
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestInType, logger);
    
            ManifestRowCheckmAbs rowOut
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestOutType, logger);
    
            Manifest manifestOut = Manifest.getManifest(logger, profile, manifestOutType);    
            manifestOut.openOutput(outManifestFile);
            assertTrue(true);

            for (String lineIn : data) {
                ManifestRowCheckmAbs rowIn = rowInFact.getManifestRow(lineIn);
                FileComponent component = rowIn.getFileComponent();
                rowOut.setFileComponent(component);
                manifestOut.write(rowOut);
            }
            manifestOut.writeEOF();
            manifestOut.closeOutput();
            String outFileS = FileUtil.file2String(outManifestFile);
            System.out.println("---MANIFEST---"
                    + NL
                    + outFileS);
            
            int pos = outFileS.indexOf(profile);

            System.out.println("---MATCH---"
                    + NL
                    + " - profile=" + profile
                    + " - pos=" + pos
                    );
            assertTrue(pos > 0);


        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);

        } finally {
            if (outManifestFile != null) {
                try {
                    outManifestFile.delete();
                } catch (Exception e) { }
            }
        }
    }


    public void copyManifest(
            String dispHeader,
            ManifestRowAbs.ManifestType manifestInType,
            ManifestRowAbs.ManifestType manifestOutType,
            File inManifestFile,
            File outManifestFile,
            String [] headers,
            String [] data
            )
        throws TException
    {
        try {
            Manifest manifest = Manifest.getManifest(logger, manifestInType);
            Manifest manifestOut = Manifest.getManifest(logger, manifestOutType);
            manifestOut.openOutput(outManifestFile);
            Enumeration<ManifestRowInf> enumRow = manifest.getRows(new FileInputStream(inManifestFile));
            ManifestRowCheckmAbs rowIn = null;
            ManifestRowCheckmAbs rowOut
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestOutType, logger);
            while (enumRow.hasMoreElements()) {
                rowIn = (ManifestRowCheckmAbs)enumRow.nextElement();
                rowOut.setFileComponent(rowIn.getFileComponent());
                manifestOut.write(rowOut);
            }
            manifestOut.writeEOF();
            manifestOut.closeOutput();
            System.out.println("***MANIFEST2***" + FileUtil.file2String(outManifestFile));

            return;

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }



  
    
}