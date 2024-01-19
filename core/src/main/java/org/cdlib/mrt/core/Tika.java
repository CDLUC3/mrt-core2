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
package org.cdlib.mrt.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.mime.MediaType;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TException;
/**
 * Container class for Storage ERC content
 * @author dloy
 */
public class Tika
{
    private static final String NAME = "Tika";
    private static final String MESSAGE = NAME + ": ";
    private static final boolean DEBUG = false;
    
    protected LoggerInf logger = null;
    protected Detector detector = null;
    protected MediaType mediaType = null;
    protected HashMap<String, String> alternates = new HashMap();
    protected ArrayList<String> keys = new ArrayList();
 
    public static Tika getTika(
            LoggerInf logger)
        throws TException
    {
        try {
            if (logger == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE 
                        + "getVersionMap - objectID missing");
            }
            Tika storeFile = new Tika(logger);
            return storeFile;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
            
        }
    } 
        
    public Tika(LoggerInf logger)
        throws TException
    {
        this.logger = logger;
        build();
    }

    private void build()
        throws TException
    {
        try {
            //TikaConfig config = new TikaConfig();
            detector = new DefaultDetector();
            buildAlternates();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
            
        }
    }
    
    /*
     * These are alternative mappings based on extension
     */
    private void buildAlternates()
    {
        /*
            Gzip (.gz)             application/gzip
            Zip (.zip)               application/zip
            Bzip (.bz)             application/x-bzip
            Bzip2 (.bz2)         application/x-bzip2
            Tar (.tar)              application/x-tar
            (.tar.gz)                application/x-gtar
         */
        add(".ttl", "plain/turtle");
        add(".tar.gz", "application/x-gtar");
        add(".tgz", "application/x-gtar");
        add(".gz", "application/gzip");
        add(".gzip", "application/gzip");
        add(".zip", "application/zip");
        add(".tar", "application/x-tar");
        add(".bz", "application/x-bzip");
        add(".bz2", "application/x-bzip2");
        add(".cpio", "application/x-cpio");
        add(".ar", "application/x-archive");
        add(".hdr", "image/vnd.radiance");
    }
    
    private void add(String key, String mime)
    {
        keys.add(key);
        alternates.put(key, mime);
    }
    
    private String getAlternate(String fileID)
    {
        for (String key : keys) {
            String fileIDLower = fileID.toLowerCase();
            if (fileIDLower.endsWith(key)) {
                if (DEBUG) System.out.println("***Alternate found:"
                        + " - key=" + key
                        + " - mime=" + alternates.get(key)
                        );
                return alternates.get(key);
            }
        }
        return null;
    }

    /**
     * Returns a tika Media based on the input stream and file Extension
     * @param componentStream input stream
     * @param filenameWithExtension name with extension
     * @return MediaType containing Mime Type
     * @throws TException 
     * Note that closing the InputStream is REQUIRED (this version) - not performed by Tika
     */
    public MediaType getMediaType(InputStream componentStream, String filenameWithExtension)
        throws TException
    {
        TikaInputStream stream = null;
        try {
            stream = TikaInputStream.get(componentStream);
            Metadata metadata = new Metadata();
            metadata.add(TikaCoreProperties.RESOURCE_NAME_KEY, filenameWithExtension);
            MediaType mediaType = detector.detect(stream, metadata);
            return mediaType;

            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ex) { }
            }
            if (componentStream != null) {
                try {
                    componentStream.close();
                } catch (Exception ex) { }
            }
        }
    }

    /**
     * Return MediaType just using File
     * @param componentFile
     * @return
     * @throws TException 
     */
    public MediaType getMediaType(File componentFile)
        throws TException
    {
        try {
            InputStream componentStream = new FileInputStream(componentFile);
            return getMediaType(componentStream, componentFile.getName());

            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
            
        }
    }
    
    /**
     * Get mime type using just a File
     * @param componentFile
     * @return MimeType
     * @throws TException 
     */
    public String getMimeType(File componentFile)
        throws TException
    {
        try {
            InputStream componentStream = new FileInputStream(componentFile);
            return getMimeType(componentStream, componentFile.getName());

            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
            
        }
    }

    public String getMimeType(InputStream componentStream, String fileID)
        throws TException
    {
        String mimeType = null;
        try {
            int pos = fileID.lastIndexOf("/");
            String filenameWithExtension = fileID.substring(pos+1);
            mimeType = getAlternate(fileID);
            if (mimeType != null) return mimeType;
            MediaType mediaType = getMediaType(componentStream, filenameWithExtension);
            return mediaType.toString();

            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            if (componentStream != null) {
                try {
                    componentStream.close();
                } catch (Exception ex) { }
            }
        }
    }

    /**
     * Set mime type using Tika
     * @param component file component with componentFile set
     * @throws TException 
     */
    public void setTika(FileComponent component, File testFile, String fileID)
        throws TException
    {
        try {
            if (testFile == null) {
                throw new TException.INVALID_CONFIGURATION(MESSAGE + "file not found:" + component.getIdentifier());
            }
            InputStream stream = new FileInputStream(testFile);
            setTika(component, stream, fileID);
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

    /**
     * Set mime type using Tika
     * @param component file component with componentFile set
     * @throws TException 
     */
    public void setTika(FileComponent component, InputStream stream, String fileID)
        throws TException
    {
        try {
            if (stream == null) {
                throw new TException.INVALID_CONFIGURATION(MESSAGE + "stream not found:" + component.getIdentifier());
            }
            String mimeType = getMimeType(stream, fileID);
            component.setMimeType(mimeType);
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

}

