
/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved
*********************************************************************/

package org.cdlib.mrt.core;

import java.net.URL;
import org.cdlib.mrt.utility.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TFileLogger;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.Header;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;



import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.util.EntityUtils;
import org.cdlib.mrt.utility.HTTPUtil;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpParams;


/**
 * AddClient for Storage
 * @author  loy
 */

public class StorageAddClient
{
    private static final String NAME = "StorageAddClient";
    private static final String MESSAGE = NAME + ": ";
    protected final static String NL = System.getProperty("line.separator");
    protected final static String FORMAT_NAME_POST = "t";
    protected final static String FORMAT_NAME_MULTIPART = "response-form";

    protected LoggerInf logger = null;

    public StorageAddClient(LoggerInf logger)
    {
        this.logger = logger;
    }

    public StorageAddClient()
    {
        this.logger = new TFileLogger(NAME, 5, 5);
    }

    public Properties addVersion(
            String link,
            String nodeIDS,
            String objectIDS,
            String localContext,
            String localID,
            String manifestS,
            String urlS,
            String validate,
            String formatType)
        throws TException
    {
        File testFile = null;
        File manifestFile = null;
        try {
            if (StringUtil.isEmpty(link)) {
                throw new TException.INVALID_OR_MISSING_PARM("link required");
            }
            if (StringUtil.isEmpty(objectIDS)) {
                throw new TException.INVALID_OR_MISSING_PARM("objectID required");
            }
            Identifier objectID = new Identifier(objectIDS);
            Integer nodeID = null;
            if (StringUtil.isNotEmpty(nodeIDS)) {
                try {
                    nodeID = Integer.parseInt(nodeIDS);
                } catch (Exception ex) { }
                if (nodeID == 0) {
                    throw new TException.INVALID_OR_MISSING_PARM("nodeID invalid");
                }
            }
            if (StringUtil.isNotEmpty(manifestS)) {
                testFile = FileUtil.getTempFile("txt", "txt");
                manifestFile = testFile;
            }
            if (StringUtil.isNotEmpty(urlS)) {
                testFile = FileUtil.url2TempFile(logger, urlS);
            }
            Long size = null;
            String type = null;
            String value = null;
            if (StringUtil.isNotEmpty(validate)) {
                validate = validate.toLowerCase();
                if (validate.equals("none")) { }
                else if (validate.equals("add")) {
                    type = "sha-256";
                    FixityTests fixity = new FixityTests(testFile, type, logger);
                    size = testFile.length();
                    value = fixity.getChecksum();
                } if (validate.equals("bad")) {
                    type = "sha-256";
                    size = testFile.length();
                    value = "aaaaaa";
                }
            }
            HttpResponse  response = sendAddMultipart(
                link,
                nodeID,
                objectID,
                localContext,
                localID,
                manifestFile,
                urlS,
                size,
                type,
                value,
                formatType);
            return processResponse(response);

        } catch (Exception ex) {
            log(MESSAGE + "Exception:" + ex, 3);
            log(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(ex);

        } finally {
            if (testFile != null) {
                try {
                    testFile.delete();
                } catch (Exception ex) { }
            }
        }

    }

    public Properties addVersion(
            String link,
            int nodeID,
            Identifier objectID,
            String localContext,
            String localID,
            String manifestS,
            String urlS,
            Long size,
            String type,
            String value,
            String formatType,
            int timeout,
            int retry)
        throws TException
    {
        File testFile = null;
        File manifestFile = null;
        try {
            if (StringUtil.isEmpty(link)) {
                throw new TException.INVALID_OR_MISSING_PARM("link required");
            }
            if (objectID == null) {
                throw new TException.INVALID_OR_MISSING_PARM("objectID required");
            }
            if (StringUtil.isNotEmpty(manifestS)) {
                manifestFile = new File(manifestS);
                if (!manifestFile.exists()) {
                    throw new TException.INVALID_OR_MISSING_PARM(
                            "ManifestFile does not exist:" + manifestFile.getCanonicalPath());
                }
                testFile = FileUtil.copy2Temp(manifestFile);
            }
            if (StringUtil.isNotEmpty(urlS)) {
                testFile = FileUtil.url2TempFile(logger, urlS);
            }
            HttpResponse  response = sendAddMultipartRetry(
                link,
                nodeID,
                objectID,
                localContext,
                localID,
                manifestFile,
                urlS,
                size,
                type,
                value,
                formatType,
                timeout,
                retry);
            return processResponse(response);

        } catch (Exception ex) {
            log(MESSAGE + "Exception:" + ex, 3);
            log(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(ex);

        } finally {
            if (testFile != null) {
                try {
                    testFile.delete();
                } catch (Exception ex) { }
            }
        }

    }


    /**
     * getObject with timeout and retry
     * @param requestURL build inputStream to this URL
     * @param timeout milliseconds for timeout
     * @param retry number of retry attemps
     * @return InputStream to URL service
     * @throws org.cdlib.mrt.utility.TException
     */
     public HttpResponse sendAddMultipartRetry(
            String link,
            Integer nodeID,
            Identifier objectID,
            String localContext,
            String localID,
            File manifestFile,
            String urlS,
            Long size,
            String type,
            String value,
            String formatType,
            int timeout,
            int retry)
        throws TException
    {
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                HttpResponse  response = sendAddMultipart(
                    link,
                    nodeID,
                    objectID,
                    localContext,
                    localID,
                    manifestFile,
                    urlS,
                    size,
                    type,
                    value,
                    formatType,
                    timeout);
                return response;

            } catch (Exception ex) {
                exSave = ex;
            }
        }
        if (exSave instanceof TException) {
            throw (TException) exSave;
        }
        throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                MESSAGE + "sendAddMultipartRetry"
                + " Exception:" + exSave);
    }

     /**
      * Send a multipart storage add request
      * @param link base storage URL
      * @param nodeID node identifier
      * @param objectID object identifier
      * @param localContext authorization context for localID
      * @param localID local identifier
      * @param manifestFile manifest as file (exclusive of urlS)
      * @param urlS link to manifest (exclusive of manifestFile)
      * @param size size of manifest data
      * @param type checksumType of manifest data
      * @param value checksum of manifest data
      * @param formatType output format type
      * @param timeout milli-seconds for timeout
      * @param retry number of retries
      * @return response properties
      * @throws TException
      */
     public Properties sendAddMultipartRetryProperties(
            String link,
            Integer nodeID,
            Identifier objectID,
            String localContext,
            String localID,
            File manifestFile,
            String urlS,
            Long size,
            String type,
            String value,
            String formatType,
            int timeout,
            int retry)
        throws TException
    {
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                HttpResponse  response = sendAddMultipart(
                    link,
                    nodeID,
                    objectID,
                    localContext,
                    localID,
                    manifestFile,
                    urlS,
                    size,
                    type,
                    value,
                    formatType,
                    timeout);
                return processResponse(response);

            } catch (Exception ex) {
                exSave = ex;
            }
        }
        if (exSave instanceof TException) {
            throw (TException) exSave;
        }
        throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                MESSAGE + "sendAddMultipartRetry"
                + " Exception:" + exSave);
    }

    protected Properties processResponse(HttpResponse response)
        throws TException
    {
        try {
            Properties resultProp = new Properties();
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if ((statusCode >= 300) || (statusCode < 200)) {
                resultProp.setProperty("add.status", "" + statusCode);
            }
            HttpEntity resEntity = response.getEntity();
            String responseState = StringUtil.streamToString(resEntity.getContent(), "utf-8");
            if (StringUtil.isNotEmpty(responseState)) {
                resultProp.setProperty("add.state", responseState);
                System.out.println("mrt-response:" + responseState);
            }
            Header [] headers = response.getAllHeaders();
            for (Header header : headers) {
                resultProp.setProperty(
                        "header." + header.getName(),
                        header.getValue());
            }
            System.out.println(PropertiesUtil.dumpProperties("!!!!sendArchiveMultipart!!!!", resultProp, 100));

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                System.out.println("Chunked?: " + resEntity.isChunked());
            }
            if (resEntity != null) {
                try {
                    EntityUtils.consume(resEntity);
                } catch (Exception e) { }
            }
            return resultProp;

        } catch (Exception ex) {
            String msg = "Exception:" + StringUtil.stackTrace(ex);
            log(MESSAGE + "Exception:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(ex);
        }

    }

    public HttpResponse sendAddMultipart(
            String link,
            Integer nodeID,
            Identifier objectID,
            String localContext,
            String localID,
            File manifest,
            String url,
            Long size,
            String type,
            String value,
            String formatType)
        throws TException
    {
        return sendAddMultipart(
            link,
            nodeID,
            objectID,
            localContext,
            localID,
            manifest,
            url,
            size,
            type,
            value,
            formatType,
            12*60*60*1000);
    }

    public HttpResponse sendAddMultipart(
            String link,
            Integer nodeID,
            Identifier objectID,
            String localContext,
            String localID,
            File manifest,
            String url,
            Long size,
            String type,
            String value,
            String formatType,
            int timeout)
        throws TException
    {
        Properties stringProp = new Properties();
        try {
            if (objectID == null) {
                throw new TException.INVALID_OR_MISSING_PARM("objectID not supplied");
            }
            String objectIDS = objectID.getValue();
            String nodeIDS = "";
            if (nodeID != null) {
                nodeIDS = "/" + nodeID;
            }
            String addVersionURLS = link + "/add"
                    + nodeIDS
                    + "/"+ URLEncoder.encode(objectIDS, "utf-8");
            log(MESSAGE + "addCLient:"
                    + " - nodeID=" + nodeID
                    + " - objectIDS=" + objectIDS
                    + " - context=" + localContext
                    + " - identifier=" + localID
                    + " - size=" + size
                    + " - type=" + type
                    + " - value=" + value
                    + " - formatType=" + formatType
                    + " - addVersionURL=" + addVersionURLS
                    + " - url=" + url
                    + " - manifest=" + manifest
                    ,10);
            if (StringUtil.isNotEmpty(localContext)) {
                stringProp.setProperty("local-context", localContext);
            }
            if (StringUtil.isNotEmpty(localID)) {
                stringProp.setProperty("local-identifier", localID);
            }
            if (size != null) {
                stringProp.setProperty("size", "" + size);
            }
            if (StringUtil.isNotEmpty(type)) {
                stringProp.setProperty("digest-type", type);
            }
            if (StringUtil.isNotEmpty(value)) {
                stringProp.setProperty("digest-value", value);
            }
            if (StringUtil.isNotEmpty(url)) {
                stringProp.setProperty("url", url);
            }
            if (StringUtil.isNotEmpty(formatType)) {
                stringProp.setProperty("responseForm", formatType);
            }
            Map<String, File> fileParts = new HashMap();
            if ((manifest != null) && manifest.exists()) {
                fileParts.put("manifest", manifest);
            }
            //HttpResponse response = HTTPUtil.postMultipartHeader(addVersionURLS, null, stringProp, fileParts, timeout);
            HttpResponse response = HTTPUtil.postMultipartHttpResponse(addVersionURLS, stringProp, fileParts, timeout);
            return response;

        } catch (TException tex) {
            log(MESSAGE + "Exception:" + tex, 3);
            log(MESSAGE + "Trace:" + StringUtil.stackTrace(tex), 10);
            throw tex;

        } catch (Exception ex) {
            log(MESSAGE + "Exception:" + ex, 3);
            log(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(ex);

        }
    }

    public HttpResponse sendAddPost(
            String link,
            int nodeID,
            Identifier objectID,
            String context,
            String identifier,
            File manifest,
            String url,
            Long size,
            String type,
            String value,
            String formatType)
        throws TException
    {
        return sendAddMultipart(
            link,
            nodeID,
            objectID,
            context,
            identifier,
            manifest,
            url,
            size,
            type,
            value,
            formatType,
            12*60*60*1000);
    }

    public void log(String msg, int lvl)
    {
        if (logger == null) System.out.println(msg);
        else logger.logMessage(msg, lvl);
    }

    protected void addNameValue(List <NameValuePair> nvps, String key, String value)
    {
        if (StringUtil.isEmpty(key)) return;
        if (StringUtil.isEmpty(value)) return;
        nvps.add(new BasicNameValuePair(key, value));
    }
}
