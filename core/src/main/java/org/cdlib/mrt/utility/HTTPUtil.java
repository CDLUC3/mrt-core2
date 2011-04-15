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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.StatusLine;
import org.apache.http.Header;
/**
 * This class will contain utilities for HTTP transactions
 *
 * @author  dloy
 */
public class HTTPUtil {


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
                return entity.getContent();
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURL
                    );

        } catch( TException tex ) {
            System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
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
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.socket.timeout", new Integer(timeout));
        params.setParameter("http.connection.timeout", new Integer(timeout));

        //System.out.println("!!!!:" + MESSAGE + "getSerializeObject.requestURL=" + requestURL);
        try {
            HttpClient httpclient = new DefaultHttpClient(params);
            HttpGet httpget = new HttpGet(requestURL);
            HttpResponse response = httpclient.execute(httpget);
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
            StatusLine statusLine = response.getStatusLine();
            if (response == null) {
                throw new TException.EXTERNAL_SERVICE_UNAVAILABLE("HTTPUtil.response2Property - No response");
            }
            int statusCode = statusLine.getStatusCode();
            resultProp.setProperty("response.status", "" + statusCode);
            
            resEntity = response.getEntity();
            String responseState = StringUtil.streamToString(resEntity.getContent(), "utf-8");
            if (StringUtil.isNotEmpty(responseState)) {
                resultProp.setProperty("response.value", responseState);
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
            return resultProp;

        } catch (Exception ex) {
            String msg = "Exception:" + StringUtil.stackTrace(ex);
            System.out.println("response2Property Exception:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(ex);
            
        } finally {
            if (resEntity != null) {
                try {
                    resEntity.consumeContent();
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
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.socket.timeout", new Integer(timeout));
        params.setParameter("http.connection.timeout", new Integer(timeout));

        //System.out.println("!!!!:" + MESSAGE + "getSerializeObject.requestURL=" + requestURL);
        try {
            HttpClient httpclient = new DefaultHttpClient(params);
            HttpDelete httpDelete = new HttpDelete(requestURL);
            HttpResponse response = httpclient.execute(httpDelete);
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
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.socket.timeout", new Integer(timeout));
        params.setParameter("http.connection.timeout", new Integer(timeout));

        //System.out.println("!!!!:" + MESSAGE + "getSerializeObject.requestURL=" + requestURL);
        try {
            HttpClient httpclient = new DefaultHttpClient(params);
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
            HttpResponse response = httpclient.execute(httppost);
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
