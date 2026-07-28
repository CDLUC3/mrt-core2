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
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.Optional;
/**
 *
 * @author dloy
 * This routine is specifically designed to handle dropped connections during an http request
 */
public class HttpGetNew {
    protected static final String NAME = "HttpGet";
    protected static final String MESSAGE = NAME + ": ";
    private static final boolean DEBUG = false;
    protected static final int BUFSIZE = 126000;
    public final static long SEGMENT = 400000000L;
    public final static long MAX_RETRY = 3; //no content length only
    
    protected long testLength = 0;
    protected long headerContentLength = -1;
    //protected Long contentLength = null;
    //protected int timeout = 0;
    protected URL contentURL = null;
    //protected LoggerInf logger = null;
    protected File outFile = null;
    protected HTTPGetUtil httpGetUtil = null;
    
    public static void getFile(URL contentURL, File outFile, HTTPGetUtil httpGetUtil)
        throws TException
    {
        HttpGetNew get = getHttpGet(contentURL, outFile, -1, httpGetUtil);
        get.build();
    }
    
    public static void getFile(URL contentURL, File outFile, long testLength, HTTPGetUtil httpGetUtil)
        throws TException
    {
        HttpGetNew get = getHttpGet(contentURL, outFile, testLength, httpGetUtil);
        get.build();
    }
    
    public static HttpGetNew getHttpGet(URL contentURL, File outFile, long testSize, HTTPGetUtil httpGetUtil)
        throws TException
    {
        return new HttpGetNew(contentURL, outFile, testSize, httpGetUtil);
    }
    
    public HttpGetNew(URL contentURL, File outFile, long testSize, HTTPGetUtil httpGetUtil)
        throws TException
    {
        this.httpGetUtil = httpGetUtil;
        this.contentURL = contentURL;
        this.testLength = testSize;
        this.outFile = outFile;
        try {
            outFile.delete();
        } catch (Exception fex) { }
    }
    
    
    
    public void build()
        throws TException
    { 
        try {
            HttpResponse response = httpGetUtil.getStreamResponse(contentURL.toString());
            headerContentLength = response.headers()
                .firstValueAsLong("content-length")
                .orElse(-1); // Returns -1 if the header is missing
            System.out.println("build content-length:" + headerContentLength);
            if (headerContentLength > -1) {
                System.out.println("contentLengh present:"+headerContentLength);
                buildContentLength(response, headerContentLength);
            } else {
                System.out.println("contentLengh NOT present");
                buildNoContentLength(response);
            }
        
        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "url2File - Exception:" + ex + " - name:" + outFile.getName();
            ex.printStackTrace();
            throw new TException.GENERAL_EXCEPTION( err);


        }
    }
            
    /**
     * This routine will perform multiple calls to fix broken connection
     * when content-length exists
     * @param entity response entity
     * @throws TException 
     */       
    public void buildContentLength(HttpResponse<InputStream> response, long contentLength)
        throws TException
    { 
        try {
            if (testLength > 0) {
                if (testLength != contentLength) {
                    throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "contentLength not equal testLength"
                        + " - url=" + contentURL.toString()
                        + " - contentLength=" + contentLength
                        + " - testLength=" + testLength
                    );
                } else {
                    System.out.println(MESSAGE + "contentLength matches testLength:" + testLength);
                }
            }
            
            
            if (DEBUG) System.out.println(MESSAGE + "build"
                        + " - url=" + contentURL.toString()
                        + " - contentLength=" + contentLength
                        + " - testLength=" + testLength
            );
            long length = 0;
            InputStream inStream = response.body();
            int failCnt = 0;
            int startCnt = 0;
            while (length < contentLength) {
                startCnt++;
                long tryLength = outFile.length();
                try {
                    stream2File(inStream, outFile, true);
                    break;
                } catch (Exception ex) {
                    System.out.println("WARNING unable to copy all content:" 
                            + " - outfile.length="+ outFile.length()
                            + " - contentLength="+ contentLength
                            + " - Exception:" + ex
                                    );
                    if (outFile.length() == tryLength) {
                        failCnt++;
                        if (failCnt >= 3) {
                            throw new TException(ex);
                        }
                    } else {
                        failCnt = 0;
                    }
                }
                long startByte = outFile.length();
                long endByte = contentLength - 1;
                System.out.println("HttpGet(" + startCnt + "):"
                        + " - startByte=" + startByte
                        + " - endByte=" + endByte
                );
                inStream = httpGetUtil.getObjectStream(contentURL.toString(), startByte, endByte);
                length = outFile.length();
            }
            if (DEBUG) System.out.println(MESSAGE + "End start counts=" + startCnt
                        + " - url=" + contentURL.toString()
                        + " - file=" + outFile.getCanonicalPath()
                        + " - contentLength=" + contentLength
                        + " - testLength=" + testLength
            );
        
        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "url2File - Exception:" + ex + " - name:" + outFile.getName();
            throw new TException.GENERAL_EXCEPTION( err);

        }
    }
    
    /**
     * This routine is called only when Content-Length not provided on GET response
     * @param entity response entity
     * @throws TException 
     */
    protected void buildNoContentLength(HttpResponse<InputStream> response)
        throws TException
    { 
        try {
            if (true) System.out.println(MESSAGE + "buildNoContentLength build"
                        + " - url=" + contentURL.toString()
                        + " - testLength=" + testLength
            );
            InputStream inStream = null;
            TException texSave = null;
            for (int retry = 0; retry < MAX_RETRY; retry++) {
                try {
                    inStream = response.body();
                    stream2File(inStream, outFile, false);
                    if ((testLength > 0) && (outFile.length() < testLength)) {
                        throw new TException.INVALID_DATA_FORMAT(MESSAGE 
                                + "content length does not match specified size"
                                + " - testLength=" + testLength
                                + " - outFile.length()=" + outFile.length()
                        );
                    }
                    if ((testLength > 0) && (outFile.length() > testLength)) {
                        texSave = new TException.INVALID_DATA_FORMAT(MESSAGE 
                                + "out file length > testLength "
                                + " - testLength=" + testLength
                                + " - outFile.length()=" + outFile.length()
                        );
                        break; //not recoverable error
                    }
                    return;
                } catch (TException tex) {
                    texSave = tex;
                    System.out.println("WARNING buildNoContentLength"
                            + " - Exception:" + tex
                                    );
                }
                if (retry == (MAX_RETRY - 1)) break;
                response = httpGetUtil.getStreamResponse(contentURL.toString());
            }
            throw texSave;
        
        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "url2File - Exception:" + ex + " - name:" + outFile.getName();
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }
    
    
    
    /**
     * Create a file from a stream
     * @param inStream stream used to create file
     * @param outFile file to create
     * @param append true=append to outFile
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void stream2File(InputStream inStream, File outFile, boolean append)
        throws TException
    {

        FileOutputStream outStream = null;
        int len = 0;
        byte [] buf = new byte[BUFSIZE];
        try {
            outStream = new FileOutputStream(outFile, append);

            int cnt = 0;
            while ((len = inStream.read(buf)) >= 0) {
                if (DEBUG && (cnt < 10)) {
                    cnt++;
                    System.out.println("len=" + len);
                }
                outStream.write(buf, 0, len);
            }
        
        } catch(Exception ex) {
            String err = MESSAGE + "Name:" + outFile.getName();
            if (DEBUG) {
                System.out.println("final len=" + len);
                ex.printStackTrace();
            }
            throw new TException.GENERAL_EXCEPTION( err, ex);


        } finally {
            try {
                //System.out.println("***FILE CLOSED***");
                inStream.close();
                outStream.close();
                
            } catch (Exception finex) { }
        }

    }
    
    public InputStream url2Stream( String urlS, long startByte, long endByte)
        throws TException
    {
        InputStream inStream = null;
        try {
            HttpResponse<InputStream> response = httpGetUtil.getStreamResponse(contentURL.toString(), startByte, endByte);
            return response.body();

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "url2File - Exception:" + ex + " - name:" + outFile.getName();
            throw new TException.GENERAL_EXCEPTION( err);


        }

    }
}
