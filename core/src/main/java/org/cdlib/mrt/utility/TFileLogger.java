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
*********************************************************************/
package org.cdlib.mrt.utility;

import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.text.SimpleDateFormat;

/**
 * Stores informational and error messages in a sequential file.
 *
 * @author dloy
 */

public class TFileLogger
    extends LoggerAbs
    implements LoggerInf
{
    protected static final String DEFAULTNAME = "mrt";
    protected FileWriter m_logFile;
    protected String m_dateQualifier = "";
    protected int m_messageMaxLevel = 10;
    protected int m_errorMaxLevel = 10;
    protected String EOL = System.getProperty("line.separator");
    protected String m_mFrameID = "";
    protected boolean includeThreadName = false;
    protected boolean defaultFlush = false;
    protected Properties initializeProperties = null;
    protected String m_filePath = null;

    public TFileLogger(String mFrameID,
            int messageMaxLevel,
            int errorMaxLevel)
    {
        if (StringUtil.isNotEmpty(mFrameID)) m_mFrameID = mFrameID;
        m_messageMaxLevel = messageMaxLevel;
        m_errorMaxLevel = errorMaxLevel;
    }

    public TFileLogger(String mFrameID,
            LoggerInf.LogLevel messageLogLevel,
            LoggerInf.LogLevel errorLogLevel)
    {
        if (StringUtil.isNotEmpty(mFrameID)) m_mFrameID = mFrameID;
        m_messageMaxLevel = messageLogLevel.getLevel();
        m_errorMaxLevel = errorLogLevel.getLevel();
    }

    public TFileLogger(String mFrameID, String filePath, Properties prop)
        throws TException
    {
        if (StringUtil.isNotEmpty(mFrameID)) m_mFrameID = mFrameID;
        initializeProperties = prop;
        initialize(mFrameID, filePath, prop);
    }

    /**
     * Initialize the logger inside of a framework. Uses framework
     * properties to configure the logger, as follows:
     *
     *<UL>
     * <LI>fileLogger.message.maximumLevel - The maximum significance of informational
     * messages that will be logged
     * <LI>fileLogger.error.maximumLevel - The maximum significance of error
     * messages that will be logged
     * <LI>fileLogger.path - The path to the directory in which the log file
     * will be written
     * <LI>fileLogger.name - The base name of the log file
     * </UL>
     * @param fw - Framework in which the logger is operating
     */
    public void initialize(String mFrameID, String filePath, Properties prop)
        throws TException
    {

        try {
            if (filePath == null)
            {
                throw new TException.INVALID_OR_MISSING_PARM(
                        "MFileLogger - filePath required");
            }
            m_filePath = filePath;
            m_messageMaxLevel = Integer.parseInt(
                    prop.getProperty("fileLogger.message.maximumLevel", "10"));
            m_errorMaxLevel = Integer.parseInt(
                    prop.getProperty("fileLogger.error.maximumLevel", "10"));

            String fileName =
                    prop.getProperty("fileLogger.name");
            if (StringUtil.isEmpty(fileName)) {
                fileName = DEFAULTNAME;
            }
            String threadProp =
                    prop.getProperty("fileLogger.includeThreadName");
            if ( StringUtil.isNotEmpty(threadProp)) {
                String ttest = threadProp.toLowerCase();
                if (ttest.equals("y") || ttest.equals("yes")) includeThreadName = true;
            }

            String flushProp =
                    prop.getProperty("fileLogger.defaultFlush");
            defaultFlush = false;
            if ((flushProp != null) && flushProp.length() > 0) {
                String ftest = flushProp.toLowerCase();
                if (ftest.equals("t") || ftest.equals("true")) defaultFlush = true;
            }

            String now = getNow();
            m_logFile = new FileWriter(
                                filePath + fileName + now + ".log", true);
            /*
            System.out.println(
                new Date() + "    (00) " + getFrameworkID() +
                "Redirecting log messages to: " +
                filePath + fileName + now + ".log - defaultFlush=" + defaultFlush);
             */

            m_dateQualifier = now;

        } catch(Exception e) {
            System.out.println(
                new Date() + " ** (00) " + getFrameworkID() +
                "Logger failed to create message log file");
            System.out.println(
                new Date() + " ** (00) " + getFrameworkID() +
                "Exception:" + e);
            System.out.println(
                new Date() + " ** (00) " + getFrameworkID() +
                StringUtil.stackTrace(e));
            if (e instanceof TException) {
                throw (TException)e;
            }
            else throw new TException.GENERAL_EXCEPTION(e);
        }
  }


    /**
     * Initialize the logger with no framework present. Default configuration
     * values remain in place.
     * @param frameworkID Framework identifier to disply in log entries
     *
     */
    public void initialize(String frameworkID)
    {
        m_mFrameID = frameworkID;
    }

    /**
     * Log an informational message to a file
     * Default: no flush
     *
     * @param message - Text of message to be logged
     * @param significance - Level of significance (0 - 10) of the message
     */
    @Override
    public void logMessage(String message, LogLevel logLevel)
    {
        int significance = getLogLevelValue(logLevel);
        logMessage(message, significance, defaultFlush);
    }

    /**
     * Log an informational message to a file
     *
     * @param message - Text of message to be logged
     * @param significance - Level of significance (0 - 10) of the message
     * @param flushLog - flush log at completion of this log output
     */
    @Override
    public void logMessage(String message, LogLevel logLevel, boolean flushLog)
    {
        int significance = getLogLevelValue(logLevel);
        logMessage(message, significance, flushLog);
    }

    /**
     * Log an error message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of significance (0 - 10) of the message
     * @param source - Source of the message
     */
    @Override
    public void logError(String message, LogLevel logLevel)
    {
          int significance = getLogLevelValue(logLevel);
          logError(message, significance);
    }

    /**
     * is this logLevel high enough to be message logged
     * @param logLevel logging level
     * @return true=level will be logged
     */
    @Override
    public boolean isMessageLoggable(LogLevel logLevel)
    {
          int significance = getLogLevelValue(logLevel);
          if (significance >= m_messageMaxLevel) return true;
          return false;
    }

    /**
     * is this logLevel high enough to be error logged
     * @param logLevel logging level
     * @return true=level will be logged
     */
    @Override
    public boolean isErrorLoggable(LogLevel logLevel)
    {
          int significance = getLogLevelValue(logLevel);
          if (significance >= m_errorMaxLevel) return true;
          return false;
    }

    /**
     * Log an informational message to a file
     * Default: no flush
     *
     * @param message - Text of message to be logged
     * @param significance - Level of significance (0 - 10) of the message
     */
    @Override
    public void logMessage(String message, int significance)
    {
        logMessage(message, significance, defaultFlush);
    }

    /**
     * Log an informational message to a file
     *
     * @param message - Text of message to be logged
     * @param significance - Level of significance (0 - 10) of the message
     * @param flushLog - flush log at completion of this log output
     */
    @Override
    public void logMessage(String message, int significance, boolean flushLog)
    {
        if (significance <= m_messageMaxLevel)
        {
            String logEntry =
                new Date() + "    (" +
                (significance < 10? "0" : "") +
                significance + ") " + getFrameworkID() + message;
             try
            {
                if (m_logFile == null)
                {
                    System.out.println(logEntry);
                }
                else
                {
                    // If the date has rolled over, open a new log file
                     if (!m_dateQualifier.equals(getNow()))
                    {
                        initialize(m_mFrameID, m_filePath, initializeProperties);
                    }
                    m_logFile.write(logEntry + EOL);
                    if (flushLog) flush();
                 }
            }
            catch(Exception e)
            {
                System.out.println(logEntry);
            }
        }
   }

    /**
     * Log an error message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of significance (0 - 10) of the message
     * @param source - Source of the message
     */
    @Override
    public void logError(String message, int significance)
    {
        if (significance <= m_errorMaxLevel)
        {
            String logEntry =
                new Date() + " ** (" +
                (significance < 10? "0" : "") +
                significance + ") " + getFrameworkID() + message;
             try
            {
                if (m_logFile == null)
                {
                    System.out.println(logEntry);
                }
                else
                {
                    // If the date has rolled over, open a new log file
                     if (!m_dateQualifier.equals(getNow()))
                    {
                        initialize(m_mFrameID, m_filePath, initializeProperties);
                    }
                    m_logFile.write(logEntry + EOL);
                    flush();
                 }
            }
            catch(Exception e)
            {
                System.out.println(logEntry);
            }
        }
   }

    /**
     * Return a string representation of the current date, for use in qualifying
     * the log file name. The string returned is determined by the current date/time
     * and a format pattern string specified in property "fileLogger.qualifier".
     *
     * @return The formatted string representation of the current date/time
     */
    protected String getNow()
    {
        if (initializeProperties == null) return "";

        try
        {
            Date date = Calendar.getInstance().getTime();
            String fileQualifier = initializeProperties.getProperty("fileLogger.qualifier");
            return
                fileQualifier == null? "" :
                new SimpleDateFormat(
                    fileQualifier).format(date);

        }
        catch(Exception e)
        {
            return "";
        }
    }

    /** Flushes the log
     *
     */
    @Override
    public void flush()
    {
        try
        {
            m_logFile.flush();
        }
        catch(Exception e)
        {
            // Do nothing
        }
    }

    /** Closes the log
     *
     */
    @Override
    public void close()
    {
        try
        {
            m_logFile.close();
        }
        catch(Exception e)
        {
            // Do nothing
        }
    }

    /**
     * get threshold for message creation
     * @return message creation threshold (e.g. <=)
     */
    @Override
    public int getMessageMaxLevel()
    {
        return m_messageMaxLevel;
    }

    /**
     * get threshold for error message creation
     * @return error message creation threshold (e.g. <=)
     */
    @Override
    public int getErrorMaxLevel()
    {
        return m_errorMaxLevel;
    }

    /**
     * Get the FrameworkID - imbed thread name if requested
     * @return displayable log name
     */
    protected String getFrameworkID()
    {
        String dispName = "";
        if (includeThreadName) {
            if (m_mFrameID == null)
                dispName = "[" + Thread.currentThread().getName()+ "] ";
            else
                dispName = "[" + m_mFrameID + ":" + Thread.currentThread().getName()+ "] ";
        } else {
            dispName = m_mFrameID == null? "" : "[" + m_mFrameID + "] ";
        }
        return dispName;
    }

    protected int getLogLevelValue(LogLevel logLevel)
    {
        int significance = 0;
        if (logLevel == null) significance = 0;
        significance = logLevel.getLevel();
        return significance;
    }
}