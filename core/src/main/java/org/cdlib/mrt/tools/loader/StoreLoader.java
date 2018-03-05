/*
Copyright (c) 2005-2009, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
*********************************************************************/
package org.cdlib.mrt.tools.loader;

import java.io.File;
import java.net.URL;

import java.util.Properties;

import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.ListProcessor;
import org.cdlib.mrt.utility.ListProcessorSimple;

import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.core.StorageAddClient;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Run a Storage load test
 *
 * @author dloy
 */

public class StoreLoader
        extends ListProcessorSimple
        implements ListProcessor
{

    protected String NAME = "StoreLoader";
    protected String MESSAGE = NAME + ":";
    public static final String LS =  System.getProperty("line.separator");
    public static final int DEFAULTDELTA = 25;
    public static boolean DEBUG = true;
    public static boolean SUBMIT = true;


    protected String checksumType = null;
    protected File processList = null;
    protected File extractBaseDirectory = null;
    protected File outputManifestDirectoryBase = null;
    protected URL outputManifestBaseURL = null;
    protected URL componentURLBase = null;

    protected URL outputManifestURL = null;
    protected File outputBatchFile = null;

    protected URL storageBase = null;
    protected int nodeID = 0;

    protected StorageAddClient storeAddClient = null;


    /**
     * Main method
     */
    public static void main(String args[])
    {
        TFrame framework = null;
        try
        {

            String propertyList[] = {
                "testresources/BatchManifest.properties"};
            framework = new TFrame(propertyList, "StoreLoader");
            
            // Create an instance of this object
            StoreLoader test = new StoreLoader(framework);

            test.processList();
        }
        catch(Exception e)
        {
            if (framework != null)
            {
                framework.getLogger().logError(
                    "Main: Encountered exception:" + e, 0);
                framework.getLogger().logError(
                        StringUtil.stackTrace(e), 10);
            }
        }
    }
    
    public StoreLoader(TFrame framework)
        throws TException
    {
        super(framework);
        initializeStoreLoader();
    }

    @Override
    protected void initialize(TFrame fw)
        throws TException
    {
        if (DEBUG) System.out.println("CALL BatchLoader initialize");
        try {
            m_clientProperties = fw.getProperties();
            Properties clientProperties = m_clientProperties;
            System.out.println(PropertiesUtil.dumpProperties(MESSAGE, clientProperties));

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "Unable to load db manager: Exception:" + ex);
        }
    }

    protected void initializeStoreLoader()
        throws TException
    {
        if (DEBUG) System.out.println("CALL BatchLoader initialize");
        try {
            Properties clientProperties = m_clientProperties;
            System.out.println(PropertiesUtil.dumpProperties(MESSAGE, clientProperties));

            String fileName = null;
            fileName = clientProperties.getProperty("processList");
            if (StringUtil.isEmpty(fileName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "inputManifestFile required");
            }
            processList = new File(fileName);
            if (!processList.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "inputManifestFile does not exist:" + fileName);
            }

            fileName = clientProperties.getProperty("extractBaseDirectory");
            if (StringUtil.isEmpty(fileName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "extractBaseDirectory required");
            }
            extractBaseDirectory = new File(fileName);
            if (!extractBaseDirectory.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "extractBaseDirectory does not exist:" + fileName);
            }

            fileName = clientProperties.getProperty("outputManifestDirectoryBase");
            if (StringUtil.isEmpty(fileName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestDirectoryBase required");
            }
            outputManifestDirectoryBase = new File(fileName);
            if (!outputManifestDirectoryBase.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestDirectory does not exist:" + fileName);
            }
            outputManifestDirectoryBase = new File(outputManifestDirectoryBase, "" + System.nanoTime());
            outputManifestDirectoryBase.mkdir();

            String urlName = null;
            urlName = clientProperties.getProperty("outputManifestBaseURL");
            if (StringUtil.isEmpty(urlName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestBaseURL required");
            }
            outputManifestBaseURL = new URL(urlName);

            urlName = clientProperties.getProperty("storageBase");
            if (StringUtil.isEmpty(urlName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "storageBase required");
            }
            storageBase = new URL(urlName);

            String nodeIDS = clientProperties.getProperty("nodeID");
            if (StringUtil.isEmpty(nodeIDS)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "nodeID required");
            }
            nodeID = Integer.parseInt(nodeIDS);

            urlName = clientProperties.getProperty("componentURLBase");
            if (StringUtil.isEmpty(urlName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "componentURLBase required");
            }
            componentURLBase = new URL(urlName);


            checksumType = clientProperties.getProperty("checksumType");
            if (StringUtil.isEmpty(checksumType)) {
                checksumType = "SHA-256";
            }

            storeAddClient = new StorageAddClient(logger);

            if (DEBUG) System.out.println(MESSAGE + "initialize end:::"
                    + " - checksumType:" + checksumType
                    + " - outputManifestDirectoryBase:" + outputManifestDirectoryBase
                    + " - outputManifestBaseURL:" + outputManifestBaseURL
                    + " - componentURLBase:" + componentURLBase
                    );
        } catch (TException tex) {
            System.out.println(tex);
            throw tex;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "Unable to load db manager: Exception:" + ex);
        }
    }
        

    
    /**
     * Name of EnrichmentList
     * @return name to be applied for identifying process properties
     */
    @Override
    public String getName() 
    {
        return "StoreLoader";
    }

    /**
     * <pre>
     * Process an incoming line which contains
     * 1) ark
     * 2) sequence number
     * Use the ark for issuing an addVersion to a storage service
     * </pre>
     * @param line line containing ark and sequence number
     * @param processProp runtime properties
     * @throws TException process exception
     */
    @Override
    public void process(
            String line,
            Properties processProp)
	throws TException
    {
        File manifest = null;
        File batchManifest = null;
        try {
            log("LINE:" + line);
            String[] items = line.split("\\s*\\|\\s*");
            //String[] items = line.split("\\s.\\|\\s.");
            if ((items == null) || (items.length < 2)) {
                throw new TException.INVALID_DATA_FORMAT(
                        MESSAGE + "entry format invalid: line=" + line
                        + " - linecnt=" + items.length);
            }
            LoaderInfo loaderInfo = new LoaderInfo();
            File extractDirectory = new File(extractBaseDirectory, items[0]);
            String primaryIDS = items[1];
            if (StringUtil.isEmpty(primaryIDS)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "primaryID required");
            }
            Identifier objectID = new Identifier(primaryIDS);

            String localID = null;
            String localContext = null;
            if (items.length >= 4) {
                localContext = items[2];
                localID = items[3];
            }
            FileComponent batchComponent = new FileComponent();
            LoaderManifest loaderManifest = processManifest(items[0], batchComponent, extractDirectory, localID, primaryIDS);
            File loaderManifestFile = loaderManifest.loaderManifest;
            String loaderManifestS = FileUtil.file2String(loaderManifestFile);
            log("Add extract Directories loaderManifest=" + loaderManifestS);

            Properties  storeProp = addVersion(
                nodeID,
                objectID,
                localContext,
                localID,
                loaderManifestFile,
                batchComponent.getSize(),
                batchComponent.getMessageDigest(checksumType));
           if (storeProp == null) {
               System.out.println("***storeProp null***");
           } else {
               System.out.println(PropertiesUtil.dumpProperties("Add", storeProp));
           }

        }  catch(TException fex){
            log(
                MESSAGE + " Exception: " + fex);
            m_status.bump(line + ".fail");
            throw fex;

        } catch(Exception ex){
            log(
                MESSAGE + " Exception: " + ex);
           log(
                MESSAGE + " StackTrace: " + StringUtil.stackTrace(ex));
            m_status.bump(line + ".fail");
            throw new TException.GENERAL_EXCEPTION(ex);

        }  finally {
            try {
                if (manifest != null)
                    manifest.delete();
            } catch (Exception ex) {}
        }
    }


    protected LoaderManifest processManifest(
            String dirName,
            FileComponent batchComponent,
            File extractDirectory,
            String localID,
            String primaryID
            )
        throws TException
    {
        File loaderManifestFile = null;
        URL ingestURL = null;

        try {
            String manifestName = "manifest.txt";
            loaderManifestFile = new File(outputManifestDirectoryBase, manifestName);
            ingestURL = new URL(outputManifestBaseURL, manifestName);
            batchComponent.setLocalID(localID);
            batchComponent.setIdentifier(primaryID);
            batchComponent.setURL(ingestURL);
            batchComponent.setIdentifier(manifestName);
            batchComponent.setComponentFile(loaderManifestFile);
            URL localComponentURLBase = new URL(componentURLBase.toString() + "/" + dirName);

            log("++++++++++++++compURLBase:"
                    + " - componentURLBase=" + componentURLBase
                    + " - dirName=" + dirName
                    + " - localComponentURLBase=" + localComponentURLBase
                    );

            LoaderManifest loaderManifest = LoaderManifest.run(
                    ManifestRowAbs.ManifestType.add,
                    checksumType,
                    batchComponent,
                    extractDirectory,
                    loaderManifestFile,
                    localComponentURLBase,
                    logger);
            return loaderManifest;

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
        }
    }

    protected void log(String msg)
    {
        System.out.println(msg);
    }

    public Properties addVersion(
            int nodeID,
            Identifier objectID,
            String localContext,
            String localID,
            File manifest,
            Long size,
            MessageDigest manifestMessageDigest)
        throws TException
    {
        try {
            String type = null;
            String value = null;
            if (manifestMessageDigest != null) {
                type = manifestMessageDigest.getJavaAlgorithm();
                value = manifestMessageDigest.getValue();
            }
            String storageBaseS = storageBase.toString();
            System.out.println("****addVersion"
                    + " - storageBaseS=" + storageBaseS
                    + " - nodeID=" + nodeID
                    + " - objectID=" + objectID
                    + " - objectID=" + objectID
                    + " - localContext=" + localContext
                    + " - localID=" + localID
                    + " - size=" + size
                    + " - type=" + type
                    + " - value=" + value
                    );
            if (!SUBMIT) return null;
            return storeAddClient.sendAddMultipartRetryProperties(
                    storageBaseS,
                    nodeID,
                    objectID,
                    localContext,
                    localID,
                    manifest,
                    null,
                    size,
                    type,
                    value,
                    "xml",
                    5000,
                    3);

        } catch (Exception ex) {
            logger.logError(MESSAGE + "Exception:" + ex, 3);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(ex);

        }

    }

    @Override
    public void end()
        throws TException
    { 
        try {
            if (outputManifestDirectoryBase == null) return;
            boolean delete = FileUtil.deleteDir(outputManifestDirectoryBase);
            if (delete) {
                System.out.println(MESSAGE 
                    + "delete:" + outputManifestDirectoryBase.getCanonicalPath());
            } else {
                
                System.out.println(MESSAGE 
                    + "delete fails:" + outputManifestDirectoryBase.getCanonicalPath());
            }
            
        } catch (Exception ex) {
            
        }

    }
}
