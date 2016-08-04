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
import org.apache.http.HttpEntity;
/**
 *
 * @author dloy
 * This routine is specifically designed to handle dropped connections during an http request
 */
public class HttpGet {
    protected static final String NAME = "HttpGet";
    protected static final String MESSAGE = NAME + ": ";
    private static final boolean DEBUG = false;
    protected static final int BUFSIZE = 126000;
    public final static long SEGMENT = 400000000L;
    public final static long MAX_RETRY = 3; //no content length only
    protected long testLength = 0;
    protected long contentLength = 0;
    protected int timeout = 0;
    protected URL contentURL = null;
    protected LoggerInf logger = null;
    protected File outFile = null;
    
    public static void getFile(URL contentURL, File outFile, int timeout, LoggerInf logger)
        throws TException
    {
        HttpGet get = getHttpGet(contentURL, outFile, -1, timeout, logger);
        get.build();
    }
    
    public static void getFile(URL contentURL, File outFile, long testLength, int timeout, LoggerInf logger)
        throws TException
    {
        HttpGet get = getHttpGet(contentURL, outFile, testLength, timeout, logger);
        get.build();
    }
    
    
    public static InputStream getStream(URL contentURL, int timeout, LoggerInf logger)
        throws TException
    {
        return setTempGet(contentURL, -1L, timeout, logger);
    }
    
    public static InputStream getStream(URL contentURL, long testLength, int timeout, LoggerInf logger)
        throws TException
    {
        return setTempGet(contentURL, testLength, timeout, logger);
    }
    
    public static DeleteOnCloseFileInputStream setTempGet(URL contentURL, long testLength, int timeout, LoggerInf logger)
        throws TException
    {
        try {
            File tmpFile = FileUtil.getTempFile("HttpGetTemp", ".txt");
            HttpGet get =  new HttpGet(contentURL, tmpFile, -1L, timeout, logger);
            get.build();
            DeleteOnCloseFileInputStream inStream = new DeleteOnCloseFileInputStream(tmpFile);
            return inStream;
            
        } catch (TException tex) {
            throw tex;
        
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    public static HttpGet getHttpGet(URL contentURL, File outFile, long testSize, int timeout, LoggerInf logger)
        throws TException
    {
        return new HttpGet(contentURL, outFile, testSize, timeout, logger);
    }
    
    public HttpGet(URL contentURL, File outFile, long testSize, int timeout, LoggerInf logger)
        throws TException
    {
        this.contentURL = contentURL;
        this.testLength = testSize;
        this.timeout = timeout;
        this.logger = logger;
        this.outFile = outFile;
        try {
            outFile.delete();
        } catch (Exception fex) { }
    }
    
    
    
    public void build()
        throws TException
    { 
        try {
            HttpEntity entity = HTTPUtil.getObjectEntity(contentURL.toString(), timeout);
            contentLength = entity.getContentLength();
            //contentLength = -1; //!!!TEST
            if (contentLength < 1) {
                buildNoContentLength(entity);
            } else {
                buildContentLength(entity);
            }
        
        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "url2File - Exception:" + ex + " - name:" + outFile.getName();
            throw new TException.GENERAL_EXCEPTION( err);


        }
    }
            
    /**
     * This routine will perform multiple calls to fix broken connection
     * when content-length exists
     * @param entity response entity
     * @throws TException 
     */       
    public void buildContentLength(HttpEntity entity)
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
            InputStream inStream = entity.getContent();
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
                inStream = url2Stream( contentURL.toString(),  startByte, endByte);
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
    protected void buildNoContentLength(HttpEntity entity)
        throws TException
    { 
        try {
            if (true) System.out.println(MESSAGE + "buildNoContentLength build"
                        + " - url=" + contentURL.toString()
                        + " - contentLength=" + contentLength
                        + " - testLength=" + testLength
            );
            InputStream inStream = null;
            TException texSave = null;
            for (int retry = 0; retry < MAX_RETRY; retry++) {
                try {
                    inStream = entity.getContent();
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
                entity = HTTPUtil.getObjectEntity(contentURL.toString(), timeout);
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
            HttpEntity entity = HTTPUtil.getObjectEntity(urlS, timeout, startByte, endByte);
            return entity.getContent();

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "url2File - Exception:" + ex + " - name:" + outFile.getName();
            throw new TException.GENERAL_EXCEPTION( err);


        }

    }
}
