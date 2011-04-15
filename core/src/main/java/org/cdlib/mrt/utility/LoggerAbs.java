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

import java.util.Properties;

/**
 * Primarily used as a Factory class for LoggerInf
 * @author dloy
 */
public abstract class LoggerAbs
    implements LoggerInf
{

    /**
     * Factory class for sysout default using explicit numeric levels
     * @param mFrameID log message identifier
     * @param messageMaxValue verbosity value for process messages
     * @param errorMaxValue verbosity value for error messages
     * @return TFileLogger
     * @throws TException
     */
    public static TFileLogger getTFileLogger(
            String mFrameID,
            int messageMaxValue,
            int errorMaxValue)
        throws TException
    {
        try {
            TFileLogger mFileLogger
                    = new TFileLogger(mFrameID, messageMaxValue, errorMaxValue);
            return mFileLogger;

        } catch (Exception ex) {
            String msg = " LoggerAbs getMFileLogger Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( msg);
        }

    }

    /**
     * Factory class for sysout default using explicit verbose levels
     * @param mFrameID log message identifier
     * @param messageMaxLevel verbosity level for process messages
     * @param errorMaxLevel verbosity level for error messages
     * @return TFileLogger
     * @throws TException
     */
    public static TFileLogger getTFileLogger(
            String mFrameID,
            LoggerInf.LogLevel messageLogLevel,
            LoggerInf.LogLevel errorLogLevel)
        throws TException
    {
        try {
            TFileLogger mFileLogger
                    = new TFileLogger(mFrameID, messageLogLevel, errorLogLevel);
            return mFileLogger;

        } catch (Exception ex) {
            String msg = " LoggerAbs getMFileLogger Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( msg);
        }

    }

    /**
     * Factory class for file output defined by prop values
     * @param mFrameID log message identifier
     * @param filePath file path to file log destination
     * @param prop
     * <pre>
     * fileLogger.message.maximumLevel=5 <- numeric message verbosity
     * fileLogger.error.maximumLevel=10 <- numeric error verbosity
     * fileLogger.name=mrt <- log name
     * fileLogger.qualifier=yyMMdd <- log name date form
     * </pre>
     * @return TFileLogger
     * @throws TException
     */
    public static TFileLogger getTFileLogger(
            String mFrameID,
            String filePath,
            Properties prop)
        throws TException
    {
        try {
            /*
            System.out.println("LoggerAbs: getMFileLogger"
                    + " - mFrameID=" + mFrameID
                    + " - filePath=" + filePath
                    );
             */
            TFileLogger mFileLogger
                    = new TFileLogger(mFrameID, filePath, prop);
            return mFileLogger;

        } catch (Exception ex) {
            String msg = " LoggerAbs getMFileLogger Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( msg);
        }

    }
}
