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

/**
 * Defines behavior of classes that store informaational and error messages.
 * @author dloy
 */

public interface LoggerInf
{
    /**
     * Enumeration of logging levels and corresponding verbosity level
     */

    public enum LogLevel
    {
        SEVERE(0, "Server level exceptions"),
        WARNING(2, "Server level warning"),
        UPDATE_EXCEPTION(3, "Exception during processing of update"),
        BAD_REQUEST_FORMAT(5, "Request was improperly formatted"),
        INFO(7, "Information level message"),
        DEBUG(10, "Standard debugging level"),
        DEBUG2(15, "Debugging level 2"),
        DEBUG3(20, "Debugging level 3"),
        DEBUG_LOW(100, "Lowest level debug");

        protected final int level; // name of product
        protected final String description; // version of product

        LogLevel(int level, String version) {
            this.level = level;
            this.description = version;
        }

        public int getLevel()   { return level; }
        public String getVersion() { return description; }


        public static LogLevel valueOf(int n)
        {
            for (LogLevel p : LogLevel.values()) {
                if (p.getLevel() == n) {
                    return p;
                }
            }
            return null;
        }
    }
    
   /**
     * Log an informational message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of signifiance (0 - 10) of the message
     * @param flushLog - flush log at completion of this log output
     */
    public void logMessage(String message, int significance, boolean flushLog);
    
   /**
     * Log an informational message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of signifiance (0 - 10) of the message
     */
    public void logMessage(String message, int significance);

    /**
     * Log an error message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of signifiance (0 - 10) of the message
     */
    public void logError(String message, int significance);

   /**
     * Log an informational message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of signifiance of the message
     * @param flushLog - flush log at completion of this log output
     */
    public void logMessage(String message, LogLevel significance, boolean flushLog);


    /**
     * Log an error message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of signifiance of the message
     */
    public void logError(String message, LogLevel significance);

   /**
     * Log an informational message
     *
     * @param message - Text of message to be logged
     * @param significance - Level of signifiance (0 - 10) of the message
     */
    public void logMessage(String message, LogLevel significance);
    
    /**
     * Flush the log
     *
     */
    public void flush();
    
    /**
     * Closes the log
     *
     */
    public void close();
    
        
    /**
     * get threshold for message creation
     * @return message creation threshold (e.g. <=)
     */
    public int getMessageMaxLevel();

    /**
     * is this logLevel high enough to be message logged
     * @param logLevel logging level
     * @return true=level will be logged
     */
    public boolean isMessageLoggable(LogLevel logLevel);

    /**
     * is this logLevel high enough to be error logged
     * @param logLevel logging level
     * @return true=level will be logged
     */
    public boolean isErrorLoggable(LogLevel logLevel);
        
    /**
     * get threshold for error message creation
     * @return error message creation threshold (e.g. <=)
     */
    public int getErrorMaxLevel();
    
}
