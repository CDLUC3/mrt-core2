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

import java.io.*;
import java.security.MessageDigest;
import java.util.zip.Checksum;
import java.util.zip.Adler32;
import java.util.zip.CRC32;


/**
 * Perform Fixity tests
 * @author dloy
 */
public class MessageDigestValue
{
    protected static final String NAME = "MessageDigestValue";
    protected static final String MESSAGE = NAME + ": ";
    protected static final int BUFSIZE = 32768;
    protected static final boolean DEBUG = false;
        
    protected String checksum = null;
    protected MessageDigestType checksumType = null;
    protected long inputSize = 0;
    protected LoggerInf logger = null;

    /**
     * Constructur - primary form
     * Performs checksum creation on file and extacts file size
     * @param file file used for fixity data creation
     * @param checksumType
     * @param inLogger
     * @throws org.cdlib.mrt.utility.TException
     */
    public MessageDigestValue(
            File file,
            String checksumTypeS,
            LoggerInf inLogger)
        throws TException
    {
        try {
            if ((file == null) || !file.exists()){
                throw new TException.INVALID_OR_MISSING_PARM(
                        "Missing file");
            }
            setChecksumType(checksumTypeS);
            logger = testLog(inLogger);
            InputStream inputStream = new FileInputStream(file);
            setSizeChecksum(inputStream);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }


    /**
     * Constructer - primary form
     * Performs checksum creation on file and extacts file size
     * @param inputStream Input Stream from digital object for fixity
     * @param checksumTypeS string form checksum type
     * @param inLogger logger
     * @throws org.cdlib.mrt.utility.TException
     */
    public MessageDigestValue(
            InputStream inputStream,
            String checksumTypeS,
            LoggerInf inLogger)
        throws TException
    {
            if (inputStream == null) {
                throw new TException.INVALID_OR_MISSING_PARM(
                        "Missing file");
            }
            setChecksumType(checksumTypeS);
            logger = testLog(inLogger);
            setSizeChecksum(inputStream);
    }

    /**
     * Convert checksum type as string to enum form
     * @param checksumTypeS string form of checksum type
     * @throws TException
     */
    protected void setChecksumType(String checksumTypeS)
        throws TException
    {
        if (StringUtil.isEmpty(checksumTypeS)) {
                throw new TException.INVALID_OR_MISSING_PARM(
                        "Missing checksumType");
        }
        checksumType = getAlgorithm(checksumTypeS);
    }

    /**
     * Determine which form of checksum handler should be used for building
     * digest value (checksum)
     * @param inputStream stream used for creating checksum
     * @throws TException process exception
     */
    protected void setSizeChecksum(InputStream inputStream)
            throws TException
    {
        if (checksumType  == MessageDigestType.crc32) {
            CRC32 checksum32 = new CRC32();
            setSizeChecksum32(checksum32, inputStream);

        } else if (checksumType  == MessageDigestType.adler32) {
            Adler32 checksum32 = new Adler32();
            setSizeChecksum32(checksum32, inputStream);

        } else {
            setSizeChecksum(inputStream, checksumType.getJavaAlgorithm());
        }
    }

    /**
     * Convert a string form of checksum type to an enum form
     * lowercase and strip punctuation
     * @param algorithmS String form of checksum type
     * @return
     */
    public static MessageDigestType getAlgorithm(String algorithmS)
    {
        if (StringUtil.isEmpty(algorithmS)) {
            return null;
        }
        algorithmS = algorithmS.toLowerCase();
        algorithmS = StringUtil.strip(algorithmS, "-");
        try {
            return MessageDigestType.valueOf(algorithmS);
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * Throw exception if log is null
     * @param logger logger to check
     * @return logger if non-null
     * @throws org.cdlib.mrt.utility.TException null log
     */
    protected LoggerInf testLog(LoggerInf logger)
        throws TException
    {
        if (logger == null) {
            throw new TException.INVALID_OR_MISSING_PARM("Logger not supplied");
        }
        return logger;
    }

    
    /**
     * Dump the content of this object to a string for logging
     * @param header header displayed in log entry
     */
    public String dump(String header)
    {
        StringBuffer buf = new StringBuffer(1000);
        buf.append(header + " [");

        buf.append(dumpEntry("checksum", checksum));
        buf.append(dumpEntry("checksumType", checksumType.toString()));
        buf.append(dumpEntry("inputSize", "" + inputSize));
        return buf.toString();
    }

    /**
     * format entry for log
     * @param key format key
     * @param value format value
     * @return formated key=value
     */
    protected String dumpEntry(String key, String value)
    {
        if (StringUtil.isEmpty(key)) return "";
        if (StringUtil.isEmpty(value)) return "";
        return " - " + key + "=\"" + value + "\"";
    }
    
    /**
     * return constructed checksum
     * @return checksum
     */
    public String getChecksum()
    {
        return checksum;
    }
    
    /**
     * return constructed checksum type
     * @return checksumType
     */
    public MessageDigestType getChecksumType()
    {
        return checksumType;
    }

    public String getChecksumJavaAlgorithm()
    {
        return checksumType.getJavaAlgorithm();
    }
    
    /**
     * return match file size
     * @return fileSize
     */
    public long getInputSize()
    {
        return inputSize;
    }
    

    /**
     * Get size and checksum for newer checksum types
     * @param inputStream stream used for calculating checksum
     * @param checksumType string form checksum type
     * @throws TException process exception
     */
    protected void setSizeChecksum(InputStream inputStream, String checksumType)
        throws TException
    {
        byte[] buf = new byte[BUFSIZE];
        if (inputStream == null) {
             throw new TException.INVALID_OR_MISSING_PARM(
                    "setSizeChecksum - missing inStream");
        }
        if (checksumType == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "setSizeChecksum - missing checksumType");
        }
        try {
            MessageDigest algorithm = 
                MessageDigest.getInstance(checksumType);
            algorithm.reset();
            int len;
            inputSize = 0;
            while ((len = inputStream.read(buf)) >= 0) {
                inputSize += len;
                algorithm.update(buf, 0, len);
            }
            byte[] digest = algorithm.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<digest.length;i++) {
                String val = Integer.toHexString(0xFF & digest[i]);
                if (val.length() == 1) val = "0" + val;
                hexString.append(val);
            }
            checksum = hexString.toString();

            if (logger.getMessageMaxLevel() >= 10) {
                String msg = MESSAGE + "fileLen=" + inputSize
                        + " - " + checksumType + "=" + checksum;
                if (DEBUG) System.out.println(msg);
                logger.logMessage(msg, 10);
            }
           
        } catch (Exception ex) {
            throw makeTException(null, ex);

        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {}
        }
    }

    /**
     * Create checksums for older types: CRC32 and Adler32
     * @param checksum32 checksum handler
     * @param inputStream stream used for checksum creation
     * @throws TException process exception
     */
    protected void setSizeChecksum32(Checksum checksum32, InputStream inputStream)
        throws TException
    {
        byte[] buf = new byte[BUFSIZE];
        if (inputStream == null) {
             throw new TException.INVALID_OR_MISSING_PARM(
                    "setSizeChecksum - missing inStream");
        }
        try {
            checksum32.reset();
            int len;
            inputSize = 0;
            while ((len = inputStream.read(buf)) >= 0) {
                inputSize += len;
                checksum32.update(buf, 0, len);
            }
            long checksumL = checksum32.getValue();
            checksum = Long.toHexString(checksumL);
            checksum = StringUtil.leftPad(checksum, 8, '0');
            if (logger.getMessageMaxLevel() >= 10) {
                String msg = MESSAGE + "fileLen=" + inputSize
                        + " - " + checksumType + "=" + checksum;
                if (DEBUG) System.out.println(msg);
                logger.logMessage(msg, 10);
            }
        } catch (Exception ex) {
            throw makeTException(null, ex);

        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {}
        }
    }
    protected TException makeTException(String header, Exception ex)
    {
        TException tex = null;
        if (ex instanceof TException) {
            tex = (TException)ex;
        } else {
            tex = new TException.GENERAL_EXCEPTION(header, ex);
        }
        logger.logError(tex.toString(), 0);
        logger.logError(tex.dump(NAME), 10);
        return tex;
    }


    
    public Result getResult()
    {
        return new Result(inputSize, checksum, checksumType);
    }

    public class Result
    {

        public String checksum = null;
        public MessageDigestType checksumType = null;
        public long inputSize = 0;
        public Result(
                long inputSize,
                String checksum,
                MessageDigestType checksumType)
        {
            this.inputSize = inputSize;
            this.checksum = checksum;
            this.checksumType = checksumType;
        }

    }
}
