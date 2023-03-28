/*
Copyright (c) 2005-2010, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
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
**********************************************************/

package org.cdlib.mrt.utility;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

//import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.Header;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.entity.BasicHttpEntity;

import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpParams;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.StatusLine;
import org.apache.http.Header;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
/**
 * This class will contain utilities for HTTP transactions
 *
 * @author  dloy
 */
public class HTTPUtil {
    private static final boolean DEBUG = false;
	
    /**
     * Send this manifestFile to mrt store
     * @param manifestFile
     * @return
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public static InputStream getObject(String requestURL, int timeout)
        throws TException
    {
        if (isFTP(requestURL)) return getFTPInputStream(requestURL, timeout);
        try {
            HttpResponse response = getHttpResponse(requestURL, timeout);
	    int responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null && (responseCode >= 200 && responseCode < 300)) {
                if (DEBUG) {
                    System.out.println("ContentLength=" + entity.getContentLength());
                    System.out.println("isChunked=" + entity.isChunked());
                    System.out.println("isStreaming=" + entity.isStreaming());
                }
                return entity.getContent();
            }
            if (responseCode == 404) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURL
                    + " - responseCode:" + responseCode
                    );
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURL
                    + " - responseCode:" + responseCode
                    );

        } catch( TException tex ) {
            if (DEBUG) System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            if (DEBUG) System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }

    /**
     * Send this manifestFile to mrt store
     * @param manifestFile
     * @return
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public static HttpEntity getObjectEntity(String requestURL, int timeout, long startByte, long endByte)
        throws TException
    {
        try {
            HttpResponse response = getHttpResponse(requestURL, timeout, startByte, endByte);
	    int responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null && (responseCode >= 200 && responseCode < 300)) {
                if (DEBUG) {
                    System.out.println("ContentLength=" + entity.getContentLength());
                    System.out.println("isChunked=" + entity.isChunked());
                    System.out.println("isStreaming=" + entity.isStreaming());
                }
                return entity;
            }
            if (responseCode == 404) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURL
                    + " - responseCode:" + responseCode
                    );
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURL
                    + " - responseCode:" + responseCode
                    );

        } catch( TException tex ) {
            if (DEBUG) System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            if (DEBUG) System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }


    /**
     * Send this manifestFile to mrt store
     * @param manifestFile
     * @return
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public static HttpEntity getObjectEntity(String requestURL, int timeout)
        throws TException
    {
        try {
            HttpResponse response = getHttpResponse(requestURL, timeout);
	    int responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null && (responseCode >= 200 && responseCode < 300)) {
                if (DEBUG) {
                    System.out.println("ContentLength=" + entity.getContentLength());
                    System.out.println("isChunked=" + entity.isChunked());
                    System.out.println("isStreaming=" + entity.isStreaming());
                }
                return entity;
            }
            if (responseCode == 404) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURL
                    + " - responseCode:" + responseCode
                    );
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURL
                    + " - responseCode:" + responseCode
                    );
            
        } catch( TException tex ) {
            if (DEBUG) System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            if (DEBUG) System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
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
    public static InputStream getObject(String requestURL, int timeout, int retry)
        throws TException
    {
        InputStream inStream = null;
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                inStream = getObject(requestURL, timeout);
                return inStream;

            } catch (TException.REQUEST_INVALID tex) {
                throw tex;

            } catch (Exception ex) {
                exSave = ex;
            }
        }
        throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                "HTTPUTIL: getObject"
                + " - requestURL=" + requestURL, exSave);
    }


    /**
     * getObject with timeout and retry
     * @param requestURL build inputStream to this URL
     * @param timeout milliseconds for timeout
     * @param retry number of retry attemps
     * @return InputStream to URL service
     * @throws org.cdlib.mrt.utility.TException
     */
    public static InputStream getObject404(String requestURL, int timeout, int retry)
        throws TException
    {
        InputStream inStream = null;
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                inStream = getObject(requestURL, timeout);
                return inStream;

            } catch (TException.REQUEST_INVALID tex) {
                throw tex;

            } catch (TException.REQUESTED_ITEM_NOT_FOUND rinf) {
                throw rinf;

            } catch (Exception ex) {
                exSave = ex;
            }
        }
        throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                "HTTPUTIL: getObject"
                + " - requestURL=" + requestURL, exSave);
    }

    /**
     * Delete
     * @param requestURL delete URL
     * @param timeout timeout in .001 seconds
     * @return delete response
     * @throws TException
     */
    public static InputStream deleteObject(String requestURL, int timeout)
        throws TException
    {
        try {
            HttpResponse response = deleteHttpResponse(requestURL, timeout);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: deleteObject- Error during HttpClient processing");

        } catch( TException tex ) {
            System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: deleteObject- Exception:" + ex);
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
    public static InputStream deleteObject(String requestURL, int timeout, int retry)
        throws TException
    {
        InputStream inStream = null;
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                inStream = deleteObject(requestURL, timeout);
                return inStream;

            } catch (TException.REQUEST_INVALID tex) {
                throw tex;

            } catch (Exception ex) {
                exSave = ex;
            }
        }
        throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                "HTTPUTIL: deleteObject"
                + " - requestURL=" + requestURL
                + " Exception:" + exSave);
    }


    /**
     * Perform a GET operation
     * @param requestURL get request
     * @param timeout connection timeout
     * @return http response
     * @throws TException process exception
     */
    public static HttpResponse getHttpResponse(String requestURL, int timeout)
        throws TException
    {
        
        try {
            
            HttpClient httpClient = getHttpClient(requestURL, timeout);
            HttpGet httpget = new HttpGet(requestURL);
	    httpget.addHeader("Accept", "*/*");
	    //httpget.addHeader("Transfer-Encoding", "chunked");
            
            HttpResponse response = httpClient.execute(httpget);
            if (response != null) {
                return response;
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getHttpResponse- Error during HttpClient processing");

        } catch( java.net.UnknownHostException uhe) {
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: Unknown host Exception:" + uhe
                    + " - URL:" + requestURL);

        } catch( IllegalArgumentException iae ) {
            System.out.println("trace:" + StringUtil.stackTrace(iae));
            throw new TException.REQUEST_INVALID("HTTPUTIL: getObject- Exception:" + iae);

        } catch( TException tex ) {
            System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }


    /**
     * Perform a GET operation
     * @param requestURL get request
     * @param timeout connection timeout
     * @return http response
     * @throws TException process exception
     */
    public static HttpResponse getHttpResponse(String requestURL, int timeout, long startByte, long endByte)
        throws TException
    {
        
        try {
            HttpClient httpClient = getHttpClient(requestURL, timeout);
            HttpGet httpget = new HttpGet(requestURL);
	    httpget.addHeader("Accept", "*/*");
            httpget.addHeader("Range", "bytes=" + startByte + "-" + endByte);
	    //httpget.addHeader("Transfer-Encoding", "chunked");
            
            HttpResponse response = httpClient.execute(httpget);
            if (response != null) {
                return response;
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getHttpResponse- Error during HttpClient processing");

        } catch( java.net.UnknownHostException uhe) {
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: Unknown host Exception:" + uhe
                    + " - URL:" + requestURL);

        } catch( IllegalArgumentException iae ) {
            System.out.println("trace:" + StringUtil.stackTrace(iae));
            throw new TException.REQUEST_INVALID("HTTPUTIL: getObject- Exception:" + iae);

        } catch( TException tex ) {
            System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }

    /**
     * Get structured properties from a Get request
     * @param requestURL build inputStream to this URL
     * @param timeout milliseconds for timeout
     * @param retry number of retry attemps
     * @return Properties generated from HTTPResponse
     * @throws TException process Exception
     */
    public static Properties getObjectProperties(String requestURL, int timeout, int retry)
        throws TException
    {
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                HttpResponse response = getHttpResponse(requestURL, timeout);
                return response2Property(response);

            } catch (TException.REQUEST_INVALID tex) {
                throw tex;

            } catch (Exception ex) {
                exSave = ex;
            }
        }
        throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                "HTTPUTIL: getObject"
                + " - requestURL=" + requestURL
                + " Exception:" + exSave);
    }

    /**
     * Get structured properties from a Get request
     * @param requestURL build inputStream to this URL
     * @param timeout milliseconds for timeout
     * @param retry number of retry attemps
     * @return Properties generated from HTTPResponse
     * @throws TException process Exception
     */
    public static HttpResponse getObjectResponse(String requestURL, int timeout, int retry)
        throws TException
    {
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                return getHttpResponse(requestURL, timeout);

            } catch (TException.REQUEST_INVALID tex) {
                throw tex;

            } catch (Exception ex) {
                exSave = ex;
            }
        }
        throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                "HTTPUTIL: getObject"
                + " - requestURL=" + requestURL
                + " Exception:" + exSave);
    }
    
    public static Properties response2Property(HttpResponse response)
        throws TException
    {
        HttpEntity resEntity = null;
        try {
            Properties resultProp = new Properties();
            if (response == null) {
                throw new TException.EXTERNAL_SERVICE_UNAVAILABLE("HTTPUtil.response2Property - No response");
            }
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            resultProp.setProperty("response.status", "" + statusCode);
            resultProp.setProperty("response.line", "" + statusLine);
            resultProp.setProperty("response.phrase", statusLine.getReasonPhrase());
            
            Header [] headers = response.getAllHeaders();
            for (Header header : headers) {
                resultProp.setProperty(
                        "header." + header.getName(),
                        header.getValue());
            }
            
            try {
                resEntity = response.getEntity();
                if (resEntity == null) return resultProp;
            } catch (Exception ex) {
                return resultProp;
            }
            String responseState = StringUtil.streamToString(resEntity.getContent(), "utf-8");
            if (StringUtil.isNotEmpty(responseState)) {
                resultProp.setProperty("response.value", responseState);
                if (DEBUG) System.out.println("mrt-response:" + responseState);
            }
            if (DEBUG) {
                System.out.println(PropertiesUtil.dumpProperties("!!!!sendArchiveMultipart!!!!", resultProp, 100));

                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                    System.out.println("Chunked?: " + resEntity.isChunked());
                }
            }
            return resultProp;

        } catch (Exception ex) {
            String msg = "Exception:" + StringUtil.stackTrace(ex);
            System.out.println("response2Property Exception:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(ex);
            
        } finally {
            if (resEntity != null) {
                try {
                    EntityUtils.consume(resEntity);
                } catch (Exception e) { }
            }
        }

    }



    /**
     * Perform a DELETE operation
     * @param requestURL get request
     * @param timeout connection timeout
     * @return http response
     * @throws TException process exception
     */
    public static HttpResponse deleteHttpResponse(String requestURL, int timeout)
        throws TException
    {
        try {
            HttpClient httpClient = getHttpClient(requestURL, timeout);
            HttpDelete httpDelete = new HttpDelete(requestURL);
            HttpResponse response = httpClient.execute(httpDelete);
            if (response != null) {
                return response;
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getHttpResponse- Error during HttpClient processing");

        } catch( IllegalArgumentException iae ) {
            System.out.println("trace:" + StringUtil.stackTrace(iae));
            throw new TException.REQUEST_INVALID("HTTPUTIL: getObject- Exception:" + iae);

        } catch( TException tex ) {
            System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }

    /**
     * Send this manifestFile to mrt store
     * @param manifestFile
     * @return
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public static InputStream postObject(String requestURL, Properties prop, int timeout)
        throws TException
    {
        try {
            HttpResponse response = postHttpResponse(requestURL, prop, timeout);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: postObject- Error during HttpClient processing");

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }
    
    
    

    /**
     * Send this manifestFile to mrt store
     * @param manifestFile
     * @return
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public static HttpResponse postHttpResponse(String requestURL, Properties prop, int timeout)
        throws TException
    {
        try {
            HttpClient httpClient = getHttpClient(requestURL, timeout);
            List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
            Enumeration e = prop.propertyNames();
            String key = null;
            String value = null;
            while( e.hasMoreElements() )
            {
                key = (String)e.nextElement();
                value = prop.getProperty(key);
                if (StringUtil.isNotEmpty(value)) {
                    formparams.add(new BasicNameValuePair(key, value));
                }
            }
            UrlEncodedFormEntity entityForm = new UrlEncodedFormEntity(formparams, "UTF-8");

            HttpPost httppost = new HttpPost(requestURL);
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httppost.setEntity(entityForm);
            HttpResponse response = httpClient.execute(httppost);
            if (response != null) {
                return response;
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: postObject- Error during HttpClient processing");

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }
    
    /**
     * Do multipart post
     * @param requestURL request URL
     * @param stringParts multipart String bodies
     * @param fileParts multipart File bodies
     * @param timeout client timeout
     * @return HttpReponse from POST request
     * @throws TException 
     */
    public static HttpResponse postMultipartHeader(
            String requestURL, 
            Properties mainHeaders,
            Properties stringParts, 
            Map<String, File> fileParts,
            int timeout)
        throws TException
    {
        try {
            HttpClient httpClient = getHttpClient(requestURL, timeout);
            HttpPost httppost = new HttpPost(requestURL);
            //CloseableHttpClient httpClient = HttpClients.createDefault();
            //HttpPost uploadFile = new HttpPost("...");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            //add string parts
            Enumeration e = stringParts.propertyNames();
            String key = null;
            String value = null;
            while( e.hasMoreElements() )
            {
                key = (String)e.nextElement();
                value = stringParts.getProperty(key);
                if (StringUtil.isNotEmpty(value)) {
                    builder.addTextBody(key, value, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
                }
            }
            
            //add file parts
            if (fileParts != null) {
                Set<String> keys = fileParts.keySet();
                for (String setKey : keys) {
                    File addFile = fileParts.get(setKey);
                    builder.addBinaryBody(setKey, addFile);
                }
            }
            
            //add main headers
            HttpEntity multipart = builder.build();
            httppost.setEntity(multipart);
            // add headers
            if ((mainHeaders != null) && (mainHeaders.size() > 0)) {
                Enumeration emh = mainHeaders.propertyNames();
                String mhkey = null;
                String mhvalue = null;
                while( emh.hasMoreElements() )
                {
                    mhkey = (String)emh.nextElement();
                    mhvalue = mainHeaders.getProperty(mhkey);
                    if (StringUtil.isNotEmpty(mhvalue)) {
                        httppost.addHeader(mhkey, mhvalue);
                    }
                }
            }
            HttpResponse response = httpClient.execute(httppost);

            return response;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }

    /**
     * Return InputStream from multipart request
     * @param requestURL request URL
     * @param stringParts multipart String bodies
     * @param fileParts multipart File bodies
     * @param timeout client timeout
     * @return response stream from multipart request
     * @throws TException 
     */
    public static InputStream postMultipartObject2(
            String requestURL, 
            Properties stringParts, 
            Map<String, File> fileParts,
            int timeout)
        throws TException
    {
        try {
            HttpResponse response = postMultipartHttpResponse2(requestURL, stringParts, fileParts, timeout);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: postObject- Error during HttpClient processing");

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }
    
    /**
     * Do multipart post
     * @param requestURL request URL
     * @param stringParts multipart String bodies
     * @param fileParts multipart File bodies
     * @param timeout client timeout
     * @return HttpReponse from POST request
     * @throws TException 
     */
    public static HttpResponse postMultipartHttpResponse2(
            String requestURL, 
            Properties stringParts, 
            Map<String, File> fileParts,
            int timeout)
        throws TException
    {
        try {
            HttpClient httpClient = getHttpClient(requestURL, timeout);
            HttpPost httppost = new HttpPost(requestURL);
            //CloseableHttpClient httpClient = HttpClients.createDefault();
            //HttpPost uploadFile = new HttpPost("...");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            Enumeration e = stringParts.propertyNames();
            String key = null;
            String value = null;
            while( e.hasMoreElements() )
            {
                key = (String)e.nextElement();
                value = stringParts.getProperty(key);
                if (StringUtil.isNotEmpty(value)) {
                    builder.addTextBody(key, value, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
                }
            }
            if (fileParts != null) {
                Set<String> keys = fileParts.keySet();
                for (String setKey : keys) {
                    File addFile = fileParts.get(setKey);
                    builder.addBinaryBody(setKey, addFile);
                }
            }
            
            HttpEntity multipart = builder.build();
            httppost.setEntity(multipart);
            HttpResponse response = httpClient.execute(httppost);

            return response;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }


    /**
     * Return InputStream from multipart request
     * @param requestURL request URL
     * @param stringParts multipart String bodies
     * @param fileParts multipart File bodies
     * @param timeout client timeout
     * @return response stream from multipart request
     * @throws TException 
     */
    public static InputStream putMultipartObject(
            String requestURL, 
            Properties stringParts, 
            Map<String, File> fileParts,
            int timeout)
        throws TException
    {
        try {
            HttpResponse response = putMultipartHttpResponse(requestURL, stringParts, fileParts, timeout);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: postObject- Error during HttpClient processing");

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }
    
    /**
     * Do multipart post
     * @param requestURL request URL
     * @param stringParts multipart String bodies
     * @param fileParts multipart File bodies
     * @param timeout client timeout
     * @return HttpReponse from POST request
     * @throws TException 
     */
    public static HttpResponse putMultipartHttpResponse(
            String requestURL, 
            Properties stringParts, 
            Map<String, File> fileParts,
            int timeout)
        throws TException
    {
        try {
            HttpClient httpClient = getHttpClient(requestURL, timeout);
            HttpPut httpPut = new HttpPut(requestURL);
            //CloseableHttpClient httpClient = HttpClients.createDefault();
            //HttpPost uploadFile = new HttpPost("...");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            Enumeration e = stringParts.propertyNames();
            String key = null;
            String value = null;
            while( e.hasMoreElements() )
            {
                key = (String)e.nextElement();
                value = stringParts.getProperty(key);
                if (StringUtil.isNotEmpty(value)) {
                    builder.addTextBody(key, value, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
                }
            }
            if (fileParts != null) {
                Set<String> keys = fileParts.keySet();
                for (String setKey : keys) {
                    File addFile = fileParts.get(setKey);
                    builder.addBinaryBody(setKey, addFile);
                }
            }
            
            HttpEntity multipart = builder.build();
            httpPut.setEntity(multipart);
            HttpResponse response = httpClient.execute(httpPut);

            return response;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }
    
    /**
     * Return InputStream from multipart request
     * @param requestURL request URL
     * @param stringParts multipart String bodies
     * @param fileParts multipart File bodies
     * @param timeout client timeout
     * @return response stream from multipart request
     * @throws TException 
     */
    public static InputStream postMultipartObject(
            String requestURL, 
            Properties stringParts, 
            Map<String, File> fileParts,
            int timeout)
        throws TException
    {
        try {
            HttpResponse response = postMultipartHttpResponse(requestURL, stringParts, fileParts, timeout);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: postObject- Error during HttpClient processing");

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION("HTTPUTIL: getObject- Exception:" + ex);
        }
    }
    
    /**
     * Do multipart post
     * @param requestURL request URL
     * @param stringParts multipart String bodies
     * @param fileParts multipart File bodies
     * @param timeout client timeout
     * @return HttpReponse from POST request
     * @throws TException 
     */
    public static HttpResponse postMultipartHttpResponse(
            String requestURL, 
            Properties stringParts, 
            Map<String, File> fileParts,
            int timeout)
        throws TException
    {
        return postMultipartHttpResponse2(requestURL, stringParts, fileParts,timeout);
    }
    
    public static HttpResponse getHttpResponse(URL hrefURL,  int timeout, int retry)
        throws TException
    {
        if (hrefURL == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "getRetry - href missing");
        }
        try {
            String href = hrefURL.toString();
            int responseCode = 0;
            HttpResponse httpResponse = null;
            Exception exc = null;
            for (int i=0; i<retry; i++) {
                exc = null;
                try {
                    httpResponse =  HTTPUtil.getHttpResponse(href, timeout);
                    if (httpResponse != null) break;
                } catch (Exception ex) {
                    exc = ex;
                    continue;
                }
            }
            if (exc != null) {
                if (exc instanceof TException) {
                    throw exc;
                }
                throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                        "HTTPUtil: getHttpResponse - Exception:" + exc);
            }
            return httpResponse;

        } catch (Exception ex) {
            throw new TException(ex);

        }
    }

    public static boolean isFTP(String urlS)
    {
        if (urlS == null) return false;
        URL url = null;
        try {
            url = new URL(urlS);
        } catch (Exception ex) {
            return false;
        }
        if (!url.getProtocol().equals("ftp")) {
            return false;
        }
        return true;
    }

    public static InputStream getFTPInputStream(String ftpURLS, int timeout)
        throws TException
    {
        URL ftpURL = null;
        try {
            ftpURL = new URL(ftpURLS);
        } catch (Exception ex) {
            throw new TException.INVALID_DATA_FORMAT("getFTPInputStream: Invalid URL format", ex);
        }
        String urlCanonical = ftpURL.toString();
        if (!urlCanonical.startsWith("ftp:")) {
            throw new TException.INVALID_DATA_FORMAT("getFTPInputStream: Not ftp URL");
        }
        return getTimeoutInputStream(ftpURL, timeout);
    }


    /**
     * Make a connection using URL and perform timeout test using threads
     * @param sourceURL connect to this URL
     * @param timeout connection timeout in seconds
     * @param fw framework instance
     */
    public static InputStream getTimeoutInputStream(
            URL sourceURL,
            int timeout)
        throws TException
    {
        String message = "HTTPUtil.getTimeoutConnection: ";
        InputStream inputStream = null;
        try {
            URLConnectionTimeout urlConnectionTimeout =
                new URLConnectionTimeout(sourceURL);
            Thread urlThread = null;

            urlThread = new Thread(urlConnectionTimeout);
            urlThread.start();
            urlThread.join(timeout);
            // The url Connection method has completed or the thread timed out, let's find out which

            Exception connectException = urlConnectionTimeout.getException();
            if (urlThread.isAlive()) {
                // We have timed out.  Interrupt connection
                urlThread.interrupt();
                throw new TException.EXTERNAL_SERVICE_UNAVAILABLE("Timed out waiting for response from server: " + sourceURL.toString());

            } if (connectException != null) {
                throw new TException.EXTERNAL_SERVICE_UNAVAILABLE("Error during connection", connectException);

            }  else {
                inputStream = urlConnectionTimeout.getInputStream();
                if ( inputStream == null)  {
                    throw new TException.EXTERNAL_SERVICE_UNAVAILABLE("Failed to get a connection");
                }
            }
            return inputStream;

        } catch(TException tex) {
            throw tex;

        } catch(Exception ex){
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(" Exception: " + ex);
        }
    }

    public static LinkedHashList<String, String> getQuery(String url)
    {
        LinkedHashList<String, String> params = new LinkedHashList();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                try {
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = URLDecoder.decode(pair[1], "UTF-8");
                    params.put(key,value);
                } catch (Exception ex) { }
            }
        }
        return params;

    }
    
    public static HttpClient createHttpClient_AcceptsUntrustedCerts() 
        throws Exception
    {
        HttpClientBuilder b = HttpClientBuilder.create();
 
        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        b.setSslcontext( sslContext);

        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        b.setConnectionManager( connMgr);

        // finally, build the HttpClient;
        //      -- done!
        HttpClient client = b.disableCookieManagement().build();
        return client;
    }
    
    //non-https version
    public static HttpClient getHttpClient(int timeout)
        throws Exception
    {       
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).setConnectionRequestTimeout(timeout).setCookieSpec(CookieSpecs.STANDARD).build();
            HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            return httpClient;
    }
    
    public static HttpClient getHttpClient(String requestURL, int timeout)
        throws Exception
    {       
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).setConnectionRequestTimeout(timeout).setCookieSpec(CookieSpecs.STANDARD).build();
            HttpClient httpClient = null;
            if (requestURL.toLowerCase().startsWith("https:")) {
                httpClient = createHttpClient_AcceptsUntrustedCerts();
            } else if (requestURL.toLowerCase().startsWith("http:")) {
                httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            }
            return httpClient;
    }

    public static final String getBasicAuthenticationHeader(String username, String password) {
    	    String valueToEncode = username + ":" + password;
    	    return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    private static class URLConnectionTimeout implements Runnable
    {

        private URL url;
        private Exception ex = null;
        private URLConnection urlConnection = null;
        private InputStream inputStream = null;

        /** constructor **/
        public URLConnectionTimeout(URL url)
        {
            this.url = url;
        }

        /**
         * Send a message to a designated destination
         *
         * @param
         * @return
         */
        public void run ()
        {

                try {
                    // attempt to open connection
                    this.urlConnection = this.url.openConnection();

                    // explicitly set io constraints - prevent caching
                    this.urlConnection.setDoInput(true);
                    this.urlConnection.setUseCaches(false);
                    this.urlConnection.connect();
                    inputStream = this.urlConnection.getInputStream();

                } catch (Exception ex) {
                    this.ex = ex;
                }
        }

        public Exception getException() {
            return ex;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public URL getUrl() {
            return url;
        }

        public URLConnection getUrlConnection() {
            return urlConnection;
        }

    }
}
