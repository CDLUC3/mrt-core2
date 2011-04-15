
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;


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
                testFile = FileUtil.getTempFile("txt", "txt");
                manifestFile = testFile;
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
                resEntity.consumeContent();
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
            URL addVersionURL = new URL(addVersionURLS);
            log(MESSAGE + "addCLient:"
                    + " - nodeID=" + nodeID
                    + " - objectIDS=" + objectIDS
                    + " - context=" + localContext
                    + " - identifier=" + localID
                    + " - size=" + size
                    + " - type=" + type
                    + " - value=" + value
                    + " - formatType=" + formatType
                    + " - addVersionURL=" + addVersionURL
                    + " - url=" + url
                    + " - manifest=" + manifest
                    ,10);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(addVersionURL.toString());
            MultipartEntity reqEntity = new MultipartEntity();
            if (StringUtil.isNotEmpty(localContext)) {
                StringBody body = new StringBody(localContext);
                reqEntity.addPart("local-context", body);
            }
            if (StringUtil.isNotEmpty(localID)) {
                StringBody body = new StringBody(localID);
                reqEntity.addPart("local-identifier", body);
            }
            if (StringUtil.isNotEmpty(type)) {
                StringBody body = new StringBody(type);
                reqEntity.addPart("digest-type", body);
            }
            if (StringUtil.isNotEmpty(value)) {
                StringBody body = new StringBody(value);
                reqEntity.addPart("digest-value", body);
            }
            if (StringUtil.isNotEmpty(url)) {
                StringBody body = new StringBody(url);
                reqEntity.addPart("url", body);
            }
            if (StringUtil.isNotEmpty(formatType)) {
                StringBody body = new StringBody(formatType);
                reqEntity.addPart(FORMAT_NAME_MULTIPART, body);
            }
            if ((manifest != null) && manifest.exists()) {
                FileBody file = new FileBody(manifest);
                reqEntity.addPart("manifest", file);
            }

            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            return response;

        } catch (Exception ex) {
            log(MESSAGE + "Exception:" + ex, 3);
            log(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
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
            String formatType,
            int timeout)
        throws TException
    {
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
            URL addVersionURL = new URL(addVersionURLS);
            log(MESSAGE + "addCLient:"
                    + " - nodeID=" + nodeID
                    + " - objectIDS=" + objectIDS
                    + " - context=" + localContext
                    + " - identifier=" + localID
                    + " - size=" + size
                    + " - type=" + type
                    + " - value=" + value
                    + " - formatType=" + formatType
                    + " - addVersionURL=" + addVersionURL
                    + " - url=" + url
                    + " - manifest=" + manifest
                    ,10);
            HttpParams params = new BasicHttpParams();
            params.setParameter("http.socket.timeout", new Integer(timeout));
            params.setParameter("http.connection.timeout", new Integer(timeout));

            HttpClient httpclient = new DefaultHttpClient(params);
            HttpPost httppost = new HttpPost(addVersionURL.toString());
            MultipartEntity reqEntity = new MultipartEntity();
            if (StringUtil.isNotEmpty(localContext)) {
                StringBody body = new StringBody(localContext);
                reqEntity.addPart("local-context", body);
            }
            if (StringUtil.isNotEmpty(localID)) {
                StringBody body = new StringBody(localID);
                reqEntity.addPart("local-identifier", body);
            }
            if (StringUtil.isNotEmpty(type)) {
                StringBody body = new StringBody(type);
                reqEntity.addPart("digest-type", body);
            }
            if (StringUtil.isNotEmpty(value)) {
                StringBody body = new StringBody(value);
                reqEntity.addPart("digest-value", body);
            }
            if (StringUtil.isNotEmpty(url)) {
                StringBody body = new StringBody(url);
                reqEntity.addPart("url", body);
            }
            if (StringUtil.isNotEmpty(formatType)) {
                StringBody body = new StringBody(formatType);
                reqEntity.addPart(FORMAT_NAME_MULTIPART, body);
            }
            if ((manifest != null) && manifest.exists()) {
                FileBody file = new FileBody(manifest);
                reqEntity.addPart("manifest", file);
            }

            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            return response;

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
        try {
            if (objectID == null) {
                throw new TException.INVALID_OR_MISSING_PARM("objectID not supplied");
            }
            String objectIDS = objectID.getValue();
            String addVersionURLS = link + "/add"
                    + "/"+ nodeID
                    + "/"+ URLEncoder.encode(objectIDS, "utf-8");
            URL addVersionURL = new URL(addVersionURLS);
            log(MESSAGE + "addCLient:"
                    + " - nodeID=" + nodeID
                    + " - objectIDS=" + objectIDS
                    + " - context=" + context
                    + " - identifier=" + identifier
                    + " - size=" + size
                    + " - type=" + type
                    + " - value=" + value
                    + " - formatType=" + formatType
                    + " - addVersionURL=" + addVersionURL
                    + " - url=" + url
                    + " - manifest=" + manifest
                    ,10);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(addVersionURL.toString());

            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            addNameValue(nvps, "local-context", context);
            addNameValue(nvps, "local-identifier", identifier);
            addNameValue(nvps, "digest-type", type);
            addNameValue(nvps, "digest-value", value);
            addNameValue(nvps, "url", url);
            addNameValue(nvps, FORMAT_NAME_POST, formatType);
            if ((manifest != null) && manifest.exists()) {
                String manifestS = FileUtil.file2String(manifest);
                addNameValue(nvps, "manifest", manifestS);
            }

            httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            return response;

        } catch (Exception ex) {
            log(MESSAGE + "Exception:" + ex, 3);
            log(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(ex);

        }
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
