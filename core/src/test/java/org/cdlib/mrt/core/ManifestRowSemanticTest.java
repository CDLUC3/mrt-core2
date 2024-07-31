/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;

import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.ManifestRowCheckmAbs;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowInf;
import org.cdlib.mrt.core.Checkm;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Enumeration;
import java.util.TimeZone;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
public class ManifestRowSemanticTest {
    protected LoggerInf logger = null;

    protected final static String NL = System.getProperty("line.separator");
    public static final String[] linesxx = {
                "#%prefix | nfo:<http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#>",
                "#%prefix | nie:<http://www.semanticdesktop.org/ontologies/2007/03/22/nie#>",
                "#%prefix | mrt:<http://merritt.ontology.cdlib.org/ns#>",
                "#%columns | nfo:fileURL nfo:hashAlgorithm nfo:hashValue nfo:fileSize nfo:fileCreated mrt:targetFilePath nie:primaryID mrt:localID nie:mimeType"
            };
    public static final String[] addHeaders = {
                "#%profile | http://uc3.cdlib.org/registry/store/manifest/mrt-add-manifest",
                "#%prefix | nfo:<http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#>",
                "#%prefix | nie:<http://www.semanticdesktop.org/ontologies/2007/01/19/nie/#>",
                "#%prefix | mrt:<http://merritt.cdlib.org/terms#>",
                "#%columns | nfo:fileURL | nfo:hashAlgorithm | nfo:hashValue | nfo:fileSize | nfo:fileLastModified | nfo:fileName"
            };

   public static final String [] addData = {
                "http://localhost:28080/feederout/1383111061164171/manifest1.txt | sha256 | a05f45e03e11d730f4e787a898e07c26eb760fb5aa27472b1ca9864ed900dc1c | 739 | 2010-06-02T13:54:43-07:00 | manifest1.txt",
                "http://localhost:28080/feederout/1383111061164171/manifest2.txt | sha256 | 4555c33a333b94e9fde19cc8e2e9169c36ae1e920e485150f619812d55f2bce7 | 278 | 2010-06-02T13:54:44-07:00 | manifest2.txt"
            };


    public static final String[] objectHeaders = {
                "#%profile | http://uc3.cdlib.org/registry/store/manifest/mrt-object-manifest",
                "#%prefix | nfo:<http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#>",
                "#%prefix | nie:<http://www.semanticdesktop.org/ontologies/2007/01/19/nie/#>",
                "#%prefix | mrt:<http://merritt.cdlib.org/terms#>",
                "#%columns | nfo:fileName | nfo:hashAlgorithm | nfo:hashValue | nfo:fileSize | nfo:fileLastModified"
            };


    public static final String [] objectData = {
                "manifest1.txt | sha256 | a05f45e03e11d730f4e787a898e07c26eb760fb5aa27472b1ca9864ed900dc1c | 739 | 2010-06-02T13:54:43-07:00",
                "manifest2.txt | sha256 | 4555c33a333b94e9fde19cc8e2e9169c36ae1e920e485150f619812d55f2bce7 | 278 | 2010-06-02T13:54:44-07:00"
            };


    public static final String[] ingestHeaders = {
                "#%profile | http://uc3.cdlib.org/registry/ingest/manifest/mrt-ingest-manifest"
            };


   public static final String [] ingestData = {
                "http://localhost:28080/feederout/1383111061164171/manifest1.txt | sha256 | a05f45e03e11d730f4e787a898e07c26eb760fb5aa27472b1ca9864ed900dc1c | 739 | 2010-06-02T13:54:43-07:00 | manifest1.txt | image/jpeg",
                "http://localhost:28080/feederout/1383111061164171/manifest2.txt | sha256 | 4555c33a333b94e9fde19cc8e2e9169c36ae1e920e485150f619812d55f2bce7 | 278 | 2010-06-02T13:54:44-07:00 | manifest2.txt | image/tiff"
            };


   public static final String[] batchHeaders = {
                "#%profile | http://uc3.cdlib.org/registry/mrt/mrt-batch-manifest"
            };

   public static final String [] batchData = {
                "http://localhost:28080/feederout/1383111061164171/manifest1.txt | sha256 | a05f45e03e11d730f4e787a898e07c26eb760fb5aa27472b1ca9864ed900dc1c | 739 | 2010-06-02T13:54:43-07:00 | manifest1.txt | ark://ark1",
                "http://localhost:28080/feederout/1383111061164171/manifest2.txt | sha256 | 4555c33a333b94e9fde19cc8e2e9169c36ae1e920e485150f619812d55f2bce7 | 278 | 2010-06-02T13:54:44-07:00 | manifest2.txt |  | local://id"
            };

    public ManifestRowSemanticTest() {
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
    public void testAdd()
        throws TException
    {
        try {
            testIt("testAdd",
                ManifestRowAbs.ManifestType.add,
                addHeaders,
                addData);


        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }

    }

    @Test
    public void testObject()
        throws TException
    {
        try {
            testIt("testObject",
                ManifestRowAbs.ManifestType.object,
                objectHeaders,
                objectData);


        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }

    }

    @Test
    public void testIngest()
        throws TException
    {
        try {
            testIt("testIngest",
                ManifestRowAbs.ManifestType.ingest,
                ingestHeaders,
                ingestData);


        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }

    }

    @Test
    public void testBatch()
        throws TException
    {
        try {
            testIt("testBatch",
                ManifestRowAbs.ManifestType.batch,
                batchHeaders,
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

    @Test
    public void testManifest()
    {
        try {
            System.out.println("****testManifest****");
            File tempFile = FileUtil.getTempFile("aaa", "txt");
            buildManifest(
                    "testManifest-Add-Object",
                ManifestRowAbs.ManifestType.add,
                ManifestRowAbs.ManifestType.object,
                addHeaders,
                addData,
                tempFile
                );
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    @Test
    public void testEncDec()
    {
        try {
            System.out.println("****testEncDec****");
            testED("ABCD");
            testED(" ABCD ");
            testED("ab|cd");
            testED("  ab|cd ");

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
            ManifestRowAbs.ManifestType manifestInType,
            ManifestRowAbs.ManifestType manifestOutType,
            String [] headers,
            String [] data,
            File outManifestFile
            )
        throws TException
    {
        try {
            System.out.println("****buildManifest****");
            ManifestRowCheckmAbs rowInFact
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestInType, logger);
            ManifestRowCheckmAbs rowOut
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestOutType, logger);
            Manifest manifestOut = Manifest.getManifest(logger, manifestOutType);
            manifestOut.openOutput(outManifestFile);
            assertTrue(true);

            for (String lineIn : data) {
                ManifestRowCheckmAbs rowIn = rowInFact.getManifestRow(lineIn);
                System.out.println("***getProfile:" + rowIn.getProfile());
                FileComponent component = rowIn.getFileComponent();
                rowOut.setFileComponent(component);
                manifestOut.write(rowOut);
            }
            manifestOut.writeEOF();
            manifestOut.closeOutput();
            System.out.println("***MANIFEST***"
                    + NL
                    + FileUtil.file2String(outManifestFile));
            
            assertTrue(true);


        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            assertFalse("TestIT exception:" + ex, true);
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