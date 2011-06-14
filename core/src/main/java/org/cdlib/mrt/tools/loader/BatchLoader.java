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

import org.cdlib.mrt.utility.URLEncoder;
import java.util.Properties;
import java.util.Vector;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.StatusLine;

import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.ListProcessor;
import org.cdlib.mrt.utility.ListProcessorSimple;

import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.core.FileContent;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.ManifestRowIngest;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Run a Storage load test
 *
 * @author dloy
 */

public class BatchLoader
        extends ListProcessorSimple
        implements ListProcessor
{
    protected String NAME = "BatchLoader";
    protected String MESSAGE = NAME + ":";
    public static final String LS =  System.getProperty("line.separator");
    public static boolean DEBUG = true;
    public static boolean SENDINGEST = false;


    protected String checksumType = null;
    protected File processList = null;
    protected File extractBaseDirectory = null;
    protected File outputManifestDirectoryBase = null;
    protected URL outputManifestBaseURL = null;
    protected URL componentURLBase = null;

    protected URL outputManifestURL = null;
    protected File outputBatchFile = null;
    protected Vector<LoaderInfo> extractDirectories =
               new Vector<LoaderInfo>();
    protected String profile = null;
    protected URL ingestBase = null;


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
            framework = new TFrame(propertyList, "BatchLoader");
            
            // Create an instance of this object
            BatchLoader test = new BatchLoader(framework);

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
    
    public BatchLoader(TFrame framework)
        throws TException
    {
        super(framework);
        initializeBatchLoader();
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

    protected void initializeBatchLoader()
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

            long nano = System.nanoTime();
            outputManifestDirectoryBase = new File(fileName);
            if (!outputManifestDirectoryBase.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestDirectory does not exist:" + fileName);
            }
            outputManifestDirectoryBase = new File(outputManifestDirectoryBase, "" + nano);
            outputManifestDirectoryBase.mkdir();

            String urlName = null;
            urlName = clientProperties.getProperty("outputManifestBaseURL");
            if (StringUtil.isEmpty(urlName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestBaseURL required");
            }
            outputManifestBaseURL = new URL(urlName.toString() + "/" + nano);

            urlName = clientProperties.getProperty("componentURLBase");
            if (StringUtil.isEmpty(urlName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "componentURLBase required");
            }
            componentURLBase = new URL(urlName);


            checksumType = clientProperties.getProperty("checksumType");
            if (StringUtil.isEmpty(checksumType)) {
                checksumType = "SHA-256";
            }

            profile = clientProperties.getProperty("profile");
            if (StringUtil.isEmpty(profile)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "profile required");
            }

            urlName = clientProperties.getProperty("ingestBase");
            if (StringUtil.isEmpty(urlName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "ingestBase required");
            }
            ingestBase = new URL(urlName);

            if (DEBUG) System.out.println(MESSAGE + "initialize end:::"
                    + " - checksumType:" + checksumType
                    + " - extractDirectories.size:" + extractDirectories.size()
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
        return "BatchLoader";
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
            String[] items = line.split(" \\| ");
            //String[] items = line.split("\\s.\\|\\s.");
            if ((items == null) || (items.length < 2)) {
                throw new TException.INVALID_DATA_FORMAT(
                        MESSAGE + "entry format invalid: line=" + line
                        + " - linecnt=" + items.length);
            }
            LoaderInfo loaderInfo = new LoaderInfo();
            loaderInfo.extractDirectory = new File(extractBaseDirectory, items[0]);
            loaderInfo.primaryID = items[1];
            if (items.length >= 3) {
                loaderInfo.localID = items[2];
            }
            if (items.length >= 4) {
                loaderInfo.creator = items[3];
            }
            if (items.length >= 5) {
                loaderInfo.title =  items[4];
            }
            extractDirectories.add(loaderInfo);
            log("Add extract Directories");

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


    protected void log(String msg)
    {
        System.out.println(msg);
    }

    @Override
    public void end()
        throws TException
    {
        try {
            if (DEBUG) System.out.println(MESSAGE + "end  - process Batch:"
                    + " - checksumType:" + checksumType
                    + " - extractDirectories.size:" + extractDirectories.size()
                    + " - outputManifestDirectoryBase:" + outputManifestDirectoryBase
                    + " - outputManifestBaseURL:" + outputManifestBaseURL
                    + " - componentURLBase:" + componentURLBase
                    );
            BatchManifest manifest = BatchManifest.run(
                    checksumType,
                    extractDirectories,
                    outputManifestDirectoryBase,
                    outputManifestBaseURL,
                    componentURLBase,
                    logger
                );
            File manifestFile = manifest.getOutputBatchFile();

            if (DEBUG) System.out.println("***manifest***" + FileUtil.file2String(manifestFile));
            URL ingestURL = new URL(ingestBase.toString() + "");
            Properties ingestStatus = sendIngest(
                    ingestURL,
                    profile,
                    manifestFile);
            log(dumpStatus(NAME + " status", ingestStatus));

        } catch (TException tex) {
            System.out.println(MESSAGE + tex);
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            throw new TException.GENERAL_EXCEPTION(ex);
        }

    }

    /**
     * Send constructed request to ingest
     * @param ingestURL batch ingest service
     * @param profile process profile
     * @param manifestInfo batch manifest information
     * @return Feeder service state
     * @throws TException process exception
     */
    protected Properties sendIngest(
            URL ingestURL,
            String profile,
            File manifestFile)
        throws TException
    {
        try {
            String ingestType = "batchManifest";
            System.out.println("***sendIngest"
                    + " - ingestURL=" + ingestURL
                    + " - manifestFile=" + manifestFile.getCanonicalPath()
                    + " - profile=" + profile
                    + " - ingestType=" + ingestType
                    );
            if (!SENDINGEST) return getDummyIngestStatus(); // switch out
            HttpResponse response = sendIngestMultipart(
                null,
                ingestURL,
                manifestFile,
                profile,
                ingestType);
            return getIngestStatus(response);

        } catch (Exception ex) {
            String msg = MESSAGE + "exception:" + ex;
            logger.logError(msg, 3);
            logger.logError("trace" + StringUtil.stackTrace(ex), 10);
            if (ex instanceof TException) {
                TException tex = (TException)ex;
                throw tex;
            }
            throw new TException.GENERAL_EXCEPTION(msg);
        }
    }

    /**
     * Send ingest request
     * @param ingestID
     * @param url URL of ingest service
     * @param ingestPackage batch file
     * @param profileIn ingest profile
     * @param type type of ingest request
     * @return http client response
     * @throws TException process exceptions
     */
    protected HttpResponse sendIngestMultipart(
            String ingestID,
            URL url,
            File ingestPackage,
            String profileIn,
            String type)
        throws TException
    {
        try {
            String ingestURLS = url.toString() + "/poster/submit";
            if (StringUtil.isNotEmpty(ingestID)) {
                ingestURLS += "/" + URLEncoder.encode(ingestID, "utf-8");
            }
            URL ingestURL = new URL(ingestURLS);
            log(MESSAGE + "runTest entered2:"
                    + " - ingestID:" + ingestID
                    + " - url:" + ingestURL.toString()
                    + " - profile:" + profileIn
                    + " - type:" + type);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ingestURL.toString());
            httppost.addHeader("accept", "text/xml; q=1");

            StringBody profileSB = new StringBody(profileIn);
            StringBody typeSB = new StringBody(type);
            FileBody file = new FileBody(ingestPackage);

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("profile", profileSB);
            reqEntity.addPart("type", typeSB);
            reqEntity.addPart("package", file);

            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            return response;

        } catch (Exception ex) {
            log(MESSAGE + "Exception:" + ex);
            log(MESSAGE + "Trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(ex);

        }
    }


    protected Properties getIngestStatus(HttpResponse response)
        throws TException
    {
        try {
            Properties resultProp = new Properties();
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if ((statusCode >= 300) || (statusCode < 200)) {
                resultProp.setProperty("status", "failure");
            } else {
                resultProp.setProperty("status","0");
                resultProp.setProperty("statusSummary", "Success");
                resultProp.setProperty("statusMessage", "");
                resultProp.setProperty("statusLine", statusLine.toString());
            }
            HttpEntity resEntity = response.getEntity();
            String responseState = StringUtil.streamToString(resEntity.getContent(), "utf-8");
            if (StringUtil.isNotEmpty(responseState)) {
                resultProp.setProperty("info", responseState);
                System.out.println("mrt-response:" + responseState);
            }
            System.out.println(PropertiesUtil.dumpProperties("!!!!sendArchiveMultipart!!!!", resultProp, 100));

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                System.out.println("Chunked?: " + resEntity.isChunked());
            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }
            return resultProp;

        } catch (Exception ex) {
            logger.logError(MESSAGE + "Exception:" + ex, 3);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(ex);

        }

    }

    protected Properties getDummyIngestStatus()
        throws TException
    {
            Properties resultProp = new Properties();
            resultProp.setProperty("status","0");
            resultProp.setProperty("statusSummary", "Success");
            resultProp.setProperty("statusMessage", "This is a status message");
            resultProp.setProperty("info", "This is a response state");
            return resultProp;
    }

    protected String dumpStatus(String header, Properties statusProp)
    {
        String status = PropertiesUtil.dumpProperties(header, statusProp);
        return status;
    }
}
