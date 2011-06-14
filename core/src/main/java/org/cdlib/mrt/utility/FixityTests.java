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
import java.util.Properties;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TRuntimeException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Perform Fixity tests
 * @author dloy
 */
public class FixityTests
        extends MessageDigestValue
{
    protected static final String NAME = "FixityTests";
    protected static final String MESSAGE = NAME + ": ";
    protected static final int BUFSIZE = 129024;
    protected static final boolean DEBUG = false;


 

    /**
     * Constructur - primary form
     * Performs checksum creation on file and extacts file size
     * @param file file used for fixity data creation
     * @param checksumType
     * @param inLogger
     * @throws org.cdlib.mrt.utility.TException
     */
    public FixityTests(
            File file,
            String checksumType,
            LoggerInf inLogger)
        throws TException
    {
        super(file, checksumType, inLogger);
    }


    /**
     * Constructer - primary form
     * Performs checksum creation on file and extacts file size
     * @param ioStream Input Stream from digital object for fixity
     * @param checksumType
     * @param inLogger
     * @throws org.cdlib.mrt.utility.TException
     */
    public FixityTests(
            InputStream ioStream,
            String checksumType,
            LoggerInf inLogger)
        throws TException
    {
        super(ioStream, checksumType, inLogger);
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
            throw new TException.INVALID_OR_MISSING_PARM("FixityTests - null logger");
        }
        return logger;
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
     * Add property if non-null
     * @param prop Properties to contain key-value
     * @param key property key
     * @param value property
     */
    protected void addProp(Properties prop, String key, String value)
    {
        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) return;
        prop.setProperty(key, value);        
    }
    
    /**
     * Get the download size and checksum for this file
     */
    public FixityResult validateSizeChecksum(
            String passedChecksum,
            String passedChecksumTypeS,
            long passedFileSize)
        throws TException
    {

        if (logger.getMessageMaxLevel() >= 10) {
            String msg = MESSAGE + "validateSizeChecksum entered:"
                    + " inputFileSize=" + inputSize 
                    + " - passedFileSize=" + passedFileSize
                    + " - checksumType=" + checksumType
                    + " - passedChecksumType=" + passedChecksumTypeS
                    + " - checksum=" + checksum 
                    + " - passedChecksum=" + passedChecksum;
            logger.logMessage(msg ,5, true);
        }
        if (StringUtil.isEmpty(passedChecksum)) {
            throw new TException.INVALID_OR_MISSING_PARM("passedChecksum missing");
        }
        if (StringUtil.isEmpty(passedChecksumTypeS)) {
            throw new TException.INVALID_OR_MISSING_PARM("passedChecksumType missing");
        }
        MessageDigestType passedChecksumType = getAlgorithm(passedChecksumTypeS);
        if (passedChecksumType != checksumType) {
            throw new TException.INVALID_OR_MISSING_PARM(
                MESSAGE + "validateSizeChecksum - checksum types do not match:"
                    + " test checksumType=" + checksumType 
                    + " - passed passedChecksumType=" + passedChecksumType );
        }
        FixityResult result = new FixityResult();
        if (passedFileSize == inputSize) {
            result.fileSizeMatch = true;
        }
        if (StringUtil.isNotEmpty(passedChecksum) 
            && StringUtil.isNotEmpty(checksum)) {
            
            if (passedChecksum.equals(checksum)) {
                result.checksumMatch = true;
            }
        }
        if (logger.getMessageMaxLevel() >= 10) {
            String msg = MESSAGE + "validateSizeChecksum result:"
                    + " result.fileSizeMatch=" + result.fileSizeMatch
                    + " result.checksumMatch=" + result.checksumMatch;
            logger.logMessage(msg ,0, true);
            //System.out.println(msg);
        }
        return result;
    }

    /**
     * Result of fixity match
     */
    public class FixityResult
    {
        public boolean fileSizeMatch = false;
        public boolean checksumMatch = false;
        
    
        /**
         * Dump the content of this object to a string for logging
         * @param header header displayed in log entry
         */
        public String dump(String header)
        {
            StringBuffer buf = new StringBuffer(1000);
            buf.append(header + " [");

            buf.append(dumpEntry("fileSizeMatch", "" + fileSizeMatch));            
            buf.append(dumpEntry("checksumType", "" + checksumMatch));
            return buf.toString();
        }
    }
}
