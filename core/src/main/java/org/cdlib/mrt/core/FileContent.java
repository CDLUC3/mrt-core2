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
import org.cdlib.mrt.core.FileComponent;
import java.io.File;
import java.io.InputStream;

import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TRuntimeException;

/**
 * Returns both File and File state information
 * @author dloy
 */
public class FileContent 
        implements FileContentInf
{
    protected FileComponent fileContent = null;
    protected InputStream inputStream = null;
    protected File file = null;
    protected String fileDisposition = null;

    public static final String DEFAULT_CHECKSUMTYPE = "SHA-256";
    protected static final String NL = System.getProperty("line.separator");

    public String getFileDisposition() {
        return fileDisposition;
    }

    public void setFileDisposition(String fileDisposition) {
        this.fileDisposition = fileDisposition;
    }


    /**
     * Constructore
     * @param fileState FileState data for object
     * @param inputStream input stream to response data
     * @param file Response file
     */
    protected FileContent(
            FileComponent fileState,
            InputStream inputStream,
            File file)
    {
        this.fileContent = fileState;
        this.inputStream = inputStream;
        this.file = file;
    }

    /**
     * Constructor that validates passed arguments
     * @param file response file
     * @param logger process logger
     */
    protected FileContent(
            File file,
            String checksumType,
            LoggerInf logger)
    {
        this.file = file;
        if ((file == null) || !file.exists()) {
            throw new TRuntimeException.INVALID_OR_MISSING_PARM("FileContent - addFile - file missing");
        }
        if (logger == null) {
            throw new TRuntimeException.INVALID_OR_MISSING_PARM("FileContent - addFile - logger missing");
        }
        addFile(file, checksumType, logger);
    }


    /**
     * Factory class to return FileContent
     * @param fileState FileState data for object
     * @param inputStream input stream to response data
     * @param file Response file
     * @return Created FileContent object
     */
    public static FileContent getFileContent(
            FileComponent fileState,
            InputStream inputStream,
            File file)
    {
        return new FileContent(fileState, inputStream, file);
    }

    /**
     * Factory class for FileContent when FileState does not exist
     * @param file response file
     * @param logger process logger
     * @return FileContent object containing File and FileState
     */
    public static FileContent getFileContent(
            File file,
            LoggerInf logger)
    {
        return new FileContent(file, DEFAULT_CHECKSUMTYPE, logger);
    }

    /**
     * Factory class for FileContent when FileState does not exist
     * @param file response file
     * @param logger process logger
     * @return FileContent object containing File and FileState
     */
    public static FileContent getFileContent(
            File file,
            String checksumType,
            LoggerInf logger)
    {
        return new FileContent(file, checksumType, logger);
    }

    @Override
    public FileComponent getFileComponent() {
        return fileContent;
    }

    /**
     * Set FileState value
     * @param fileState set using this FileState
     */
    public void setFileState(FileComponent fileState) {
        this.fileContent = fileState;
    }

    @Override
    public File getFile() {
        return file;
    }

    /**
     * Set File
     * @param file set using this File
     */
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Set InputStream
     * @param inputStream set using this InputStream
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Build FileState information from file
     * @param file response file
     * @param logger process logger
     */
    protected void addFile(
            File file, String checksumType, LoggerInf logger)
    {
        try {
            FixityTests fixity = new FixityTests(file, checksumType, logger);
            fileContent = new FileComponent();
            fileContent.addMessageDigest(fixity.getChecksum(), fixity.getChecksumType().toString());
            fileContent.setSize(file.length());

        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION("FileContent - addFile - Exception:" + ex);
        }

    }

    public String dump(String header)
    {
        try {
            StringBuffer buf = new StringBuffer();
            buf.append(header + NL);
            if (file == null) {
                buf.append("File is null");
            } else {
                buf.append(" - fileName:" + file.getCanonicalFile() + NL);
                buf.append(" - fileSize:" + file.length() + NL);
            }
            if (fileContent == null) {
                buf.append("FileContent is null");
            } else {
                buf.append(fileContent.dump("FileContent") + NL);
            }
            if (StringUtil.isEmpty(fileDisposition)) {
                buf.append("fileDisposition is null");
            } else {
                buf.append(" - fileDisposition:" + fileDisposition + NL);
            }
            return buf.toString();

        } catch (Exception ex) {
            return "WARNING Exception during dump:" + ex;
        }
    }
}
