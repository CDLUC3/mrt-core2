/*
Copyright (c) 2005-2026, Regents of the University of California
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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Set;
import java.net.ProxySelector;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
//import org.apache.http.entity.mime.MultipartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class will contain utilities for HTTP transactions
 *
 * @author  dloy
 * 
 * USE Proxy:
            String proxyHost = "uc3-mrtingest-stg02.cdlib.org";
            int proxyPort = 65002;
            File outFile = new File("/home/loy/tmp/http/example.txt");
            
            HashMap<String, String> headers = new HashMap<>();
            HTTPGetUtil getUtil = HTTPGetUtil.build(proxyHost, proxyPort, headers);
            FileUtil.url2File(urlS, outFile, getUtil
 */
public class HTTPGetUtil {
    private static final boolean DEBUG = false;
    private static final boolean RESPONSE = true;
    private static final Logger log4j = LogManager.getLogger();
    protected static final int DEFAULT_TIMEOUT = 3600000;
    
    protected HttpClient httpClient = null;
    //protected ProxySelector proxy = null;
    protected HashMap<String,String> headers = new HashMap<>();
    protected int timeout = -1;
    
    
    /**
     * Build HTTPGetUtil
     * @param proxyHost service name (not url) of forward proxy - null=no proxy
     * @param proxyPort service port of forward prosy - if proxy required
     * @param headers map of headers to be used for each GET request
     * @return HTTPGETUtil
     * @throws TException 
     */
    public static HTTPGetUtil build(String proxyHost, Integer proxyPort, HashMap headers)
        throws TException
    {
        return new HTTPGetUtil(DEFAULT_TIMEOUT, proxyHost, proxyPort, headers);
    }
    
    /**
     * Build HTTPGetUtil
     * @param timeout process timeout
     * @param proxyHost service name (not url) of forward proxy - null=no proxy
     * @param proxyPort service port of forward prosy - if proxy required
     * @param headers map of headers to be used for each GET request
     * @return HTTPGETUtil
     * @throws TException 
     */
    public static HTTPGetUtil build(int timeout, String proxyHost, Integer proxyPort, HashMap headers)
        throws TException
    {
        return new HTTPGetUtil(timeout, proxyHost, proxyPort, headers);
    }
    
    protected HTTPGetUtil(int timeout, String proxyHost, Integer proxyPort, HashMap headers)
        throws TException
    {
        if (proxyHost != null) {
            if ((proxyPort == null) || (proxyPort < 1)) {
                throw new TException.INVALID_OR_MISSING_PARM("HTTPNewUtil: proxy server but now port");
            }
            //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)))
                .build();
        } else {
            httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();
        }
        this.timeout = timeout;
        if ((headers != null) && (!headers.isEmpty())){
            this.headers = headers;
        }
    }
    
    /**
     * Get java HttpResponse for Get 
     * @param requestURLS Url for content
     * @return java HttpResponse
     * @throws TException 
     */
    public HttpResponse<InputStream> getStreamResponse(String requestURLS)
        throws TException
    {
        try  {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(requestURLS))
                    .GET();
            requestBuilder = addHeaders(requestBuilder);
            HttpRequest request = requestBuilder.build();
            
            // 2. Build your GET request
            //HttpRequest request = HttpRequest.newBuilder()
            //        .uri(URI.create(requestURLS))
            //        .GET()
            //        .build();

            // 3. Send request and specify the InputStream body handler
            HttpResponse<InputStream> response = httpClient.send(
                    request, 
                    HttpResponse.BodyHandlers.ofInputStream()
            );
            
            int responseCode = response.statusCode();
            // 4. Process the stream safely using try-with-resources
            if ((responseCode >= 200) && (responseCode < 300)) {
                return response;
            
            } if (responseCode == 404) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(
                    "HTTPUTIL: getObject- Object not found"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURLS
                    + " - responseCode:" + responseCode
                  
                );
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURLS
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
     * @param requestURLS url for content
     * @return InputStream content
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public InputStream getObject(String requestURLS)
        throws TException
    {
        return getStreamResponse(requestURLS).body();
    }
    
    public HttpRequest.Builder addHeaders(HttpRequest.Builder requestBuilder)
    {
        if (headers.isEmpty()) return requestBuilder;
        Set<String> keys = headers.keySet();
        for (String key: keys) {
            String value = headers.get(key);
            requestBuilder.header(key, value);
        }
        return requestBuilder;
    }
    
    /**
     * Send this manifestFile to mrt store
     * @param requestURLS stream source of content
     * @param startByte starting byte (from 0) for extraction
     * @param endByte ending byte (from 0) for extraction
     * @return InputStream for content
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public HttpResponse<InputStream> getStreamResponse(String requestURLS, long startByte, long endByte)
        throws TException
    {
        try  {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(requestURLS))
                    .GET();
	    requestBuilder.header("Accept", "*/*");
            requestBuilder.header("Range", "bytes=" + startByte + "-" + endByte);
            requestBuilder = addHeaders(requestBuilder);
            HttpRequest request = requestBuilder.build();
            
            // 2. Build your GET request
            //HttpRequest request = HttpRequest.newBuilder()
            //        .uri(URI.create(requestURLS))
            //        .GET()
            //        .build();

            // 3. Send request and specify the InputStream body handler
            HttpResponse<InputStream> response = httpClient.send(
                    request, 
                    HttpResponse.BodyHandlers.ofInputStream()
            );
            
            int responseCode = response.statusCode();
            // 4. Process the stream safely using try-with-resources
            if ((responseCode >= 200) && (responseCode < 300)) {
                return response;
            
            } if (responseCode == 404) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(
                    "HTTPUTIL: getObject- Object not found"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURLS
                    + " - responseCode:" + responseCode
                  
                );
            }
            throw new TException.EXTERNAL_SERVICE_UNAVAILABLE(
                    "HTTPUTIL: getObject- Error during HttpClient processing"
                    + " - timeout:" + timeout
                    + " - URL:" + requestURLS
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
    public InputStream getObjectStream(String requestURLS, long startByte, long endByte)
        throws TException
    {
        return getStreamResponse(requestURLS, startByte, endByte).body();
    }

    public static boolean isChunked(HttpResponse response)
    {
        return response.headers()
        .firstValue("Transfer-Encoding")
        .map(value -> value.equalsIgnoreCase("chunked"))
        .orElse(false);
    }

    /**
     * getObject with timeout and retry
     * @param requestURL build inputStream to this URL
     * @param timeout milliseconds for timeout
     * @param retry number of retry attemps
     * @return InputStream to URL service
     * @throws org.cdlib.mrt.utility.TException
     */
    public InputStream getObject(String requestURL, int timeout, int retry)
        throws TException
    {
        InputStream inStream = null;
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                inStream = getObject(requestURL);
                return inStream;

            } catch (TException.REQUEST_INVALID | TException.REQUESTED_ITEM_NOT_FOUND tex) {
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
     * Used to trace IT calls
     * @param response http response
     * @param goodStatus expected status to skip dump
     */
    public static void dumpHttpResponse(HttpResponse<InputStream> response, int goodStatus)
    {
        
        try {
            if (response == null) {
                System.out.println("dumpHttpResponse null response");
                return;
            }
            int status = response.statusCode();
            System.out.println("dumpHttpResponse status:" + status);
            if (status != goodStatus) {
                return;
            }
            InputStream inStream = response.body();
            String inS = StringUtil.streamToString(inStream, "utf8");
            System.out.println("dumpHttpResponse:" + inS);

        } catch( IllegalArgumentException iae ) {
            System.out.println("trace:" + StringUtil.stackTrace(iae));
           

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
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
    public Properties getObjectProperties(String requestURL, int retry)
        throws TException
    {
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                HttpResponse<InputStream> response = getStreamResponse(requestURL);
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
    public HttpResponse<InputStream> getObjectResponse(String requestURL, int retry)
        throws TException
    {
        Exception exSave = null;
        for (int i=0; i < retry; i++) {
            try {
                HttpResponse<InputStream> response = getStreamResponse(requestURL);
                return response;

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
    
    public static Properties response2Property(HttpResponse<InputStream> response)
        throws TException
    {
        
        try {
            Properties resultProp = new Properties();
            if (response == null) {
                throw new TException.EXTERNAL_SERVICE_UNAVAILABLE("HTTPUtil.response2Property - No response");
            }
            
            int statusCode = response.statusCode();
            
            resultProp.setProperty("response.status", "" + statusCode);
            response.headers();
            HttpHeaders headers = response.headers();
            Map<String, List<String>> map = headers.map();
            Set<String> keys = map.keySet();
            for (String key : keys) {
                List<String> headList = map.get(key);
                if (headList.size() > 0) {
                    String first = headList.getFirst();
                    resultProp.setProperty(
                            "header." + key,
                            first);
                }
            }
            String responseState = StringUtil.streamToString(response.body(), "utf-8");
            if (StringUtil.isNotEmpty(responseState)) {
                resultProp.setProperty("response.value", responseState);
                if (DEBUG) System.out.println("mrt-response:" + responseState);
            }
            if (DEBUG) {
                System.out.println(PropertiesUtil.dumpProperties("!!!!sendArchiveMultipart!!!!", resultProp, 100));

                System.out.println("----------------------------------------");

            }
            return resultProp;

        } catch (Exception ex) {
            String msg = "Exception:" + StringUtil.stackTrace(ex);
            System.out.println("response2Property Exception:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(ex);
            
        } finally {
        }

    }



    public static final String getBasicAuthenticationHeader(String username, String password) {
    	    String valueToEncode = username + ":" + password;
    	    return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
    
    public void addBasidAuthenticationHeader(String username, String password)
    {
        String authValue = getBasicAuthenticationHeader(username, password);
        headers.put("Authorization", authValue);
    }
    
    public static void addBasidAuthenticationToLocalHeader(String username, String password, HashMap<String,String> localHeaders)
    {
        String authValue = getBasicAuthenticationHeader(username, password);
        localHeaders.put("Authorization", authValue);
    }
    
    public Long getContentLength(String requestURL)
            throws TException
    {
        Long length = null;
        HttpRequest.Builder requestBuilder  = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .method("HEAD", HttpRequest.BodyPublishers.noBody()); // Sets method to HEAD
        requestBuilder = addHeaders(requestBuilder);
        HttpRequest request = requestBuilder.build();
        
         try {
            // 2. Send the request
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() == 200) {
                // 3. Extract the Content-Length header safely
                OptionalLong size = response.headers().firstValueAsLong("Content-Length");
                
                if (size.isPresent()) {
                    System.out.println("Content Size: " + size.getAsLong() + " bytes");
                    length = size.getAsLong();
                   
                } else {
                    System.out.println("Content-Length header not provided by the server.");
                    length = null;
                }
            } else {
                System.out.println("Request failed with status code: " + response.statusCode());
            }
            return length;

        } catch (Exception ex) {
            throw new TException(ex);
        }
         
         
    }

    public HttpClient getHttpClient() {
        return httpClient;
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
