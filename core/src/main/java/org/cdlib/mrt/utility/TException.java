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
 * mrt Exception
 * @author dloy
 */
public class TException
        extends java.lang.Exception
        implements StateInf
{
    // The status code associated with this error
    protected TExceptionEnum status;

    protected Exception remoteException = null;

    // any detail to be passed along as userMessage
    protected String detail = "";

    StackTraceElement traceElement = null;

    /**
     * Degenerate case - where only an exceptin is provided.
     * This defaults to a GENERAL_EXCEPTION status
     * @param ex remote exception to be saved
     */
    public TException(Exception ex)
    {
        status = TExceptionEnum.GENERAL_EXCEPTION;
        setTException(status, null, ex);
    }

    /**
     * The constructor set the status code and no message
     * @param status - the int status code associated with this exception
     * @param ex remote exception to be saved
     */
    public TException(TExceptionEnum status, Exception ex) 
    {
        setTException(status, null, ex);
    }

    /**
     * This constuctor sets the message and status code associated with this
     * exception.
     * @param status - the int status code
     * @param msg - the String message
     */
    public TException(TExceptionEnum status, String msg) 
    {
        setTException(status, msg, null);
    }

    /**
     * The constructor set the status code and no message
     * @param status - the int status code associated with this exception
     * @param msg - the String message
     * @param ex remote exception to be saved
     */
    public TException(TExceptionEnum status, String msg, Exception ex) {
        setTException(status, msg, ex);
    }

    /**
     * Set exception content based on whether a message or remote exception was
     * provided. If a remote exception is also a TException then the value of the
     * original TException is saved.
     * @param status the int status code associated with this exception
     * @param msg the String message
     * @param ex remote exception to be saved
     */
    protected void setTException(TExceptionEnum status, String msg, Exception ex)
    {
        if (ex == null) {
            this.status = status;
            detail = msg;
            remoteException = null;
            StackTraceElement [] ste = this.getStackTrace();
            traceElement = ste[0];

        } else if (ex instanceof TException) {
            TException fe = (TException)ex;
            remoteException = fe.getRemoteException();
            this.status = fe.getStatus();
            detail = fe.getDetail();
            StackTraceElement [] ste = fe.getStackTrace();
            traceElement = ste[0];

        } else if (ex instanceof TRuntimeException) {
            TRuntimeException fe = (TRuntimeException)ex;
            remoteException = fe.getRemoteException();
            this.status = fe.getStatus();
            detail = fe.getDetail();
            StackTraceElement [] ste = fe.getStackTrace();
            traceElement = ste[0];

        } else {
            remoteException = ex;
            this.status = status;
            detail = ex.toString();
            StackTraceElement [] ste = this.getStackTrace();
            traceElement = ste[0];
        }

        if (StringUtil.isNotEmpty(msg)) {
            if (remoteException != null) {
                if (detail.equals(remoteException.toString())) {
                    detail = msg;
                }
            }
        }
    }

    /**
     * return the status code
     */
    public TExceptionEnum getStatus () {
        return status;
    }

    /**
     * Return any remote exception stored as part of creating TException
     * @return remote exception
     */
    public Exception getRemoteException()
    {
        return remoteException;
    }

    /**
     * Return name of class where this exception occurred
     * @return exception originating exception
     */
    public String getClassName()
    {
        return traceElement.getClassName();
    }

    /**
     * Return name of method where exception occurred
     * @return originating name of method for this exception
     */
    public String getMethodName()
    {
       
        return traceElement.getMethodName();
    }

    /**
     * program code line number for this exception
     * @return program line number
     */
    public int getLineNumber()
    {
        return traceElement.getLineNumber();
    }

    /**
     * value of status
     */
    public String getStatusName () {
        return status.toString();
    }

    /**
     * return the status description
     */
    public String getStatusDescription () {
        return status.getDescription();
    }

    /**
     * return the status description
     */
    public int getHTTPResponse()
    {
        return status.getHttpResponse();
    }

    /**
     * return the description
     */
    public String getError () {
        return toString();
    }

    /**
     * return the stack trace
     */
    public String getTrace () {
        if (remoteException != null)
            return StringUtil.stackTrace(remoteException);
        else
            return StringUtil.stackTrace(this);
    }

    /**
     * location of exception
     * @return exception location
     */
    public String getLocation () {
        return traceElement.toString();
    }

    /**
     * toString returns this exception as a string
     * @return the status and message
     */
    @Override
    public String toString ()
    {
        String msg = getClassName() + "-" + getMethodName() + ":" + getLineNumber()  + " "
                + status + "[" + getDescription() + "] " + detail;
        if (remoteException != null) {
            if (!detail.equals(remoteException.toString())) {
                msg = msg + " - Exception:" + remoteException.toString();
            }
        }
        return msg;
    }

    /**
     * get a String message associated with an integer mnemonic code
     * @param int mnemonic integer associated with exception
     * @return String message from messages Hashtable
     */
    public static String getDescription (TExceptionEnum mnemonic) {
        if (mnemonic == null) return "";
        return mnemonic.toString();
    }

    /**
     * get a String message associated with the MExceptionEnum.--
     * non-static version
     * @return String message from messages Hashtable
     */
    public String getDescription () {
        try {
            TExceptionEnum mnemonic = getStatus();
            //convert String into an integer and pass to the method
            //that takes an integer
            return mnemonic.getDescription();
        }
        catch (Exception e) {
            return "";
        }
    }

   /**
     * set the String detail message to give more information on an error
     * @param String detail message
     */
    public void setDetail (String detail) {
        this.detail = detail;
    }

   /**
     * get the String detail message, more information on an error
     * @return String detail message
     */
    public String getDetail () {
        if (StringUtil.isEmpty(detail)) return "";
        return detail;
    }

    public String dump(String header)
    {
        String NL = System.getProperty("line.separator");
        if (StringUtil.isEmpty(header)) header = "";
        else header = header + NL;
        String remote = null;
            if (getRemoteException() == null) {
                remote = "none";
            } else {
                remote = "exists";
            }

            String dump =  header
                    + " - detail=" + getDetail() + NL
                    + " - statusName=" + getStatusName() + NL
                    + " - description=" + getDescription() + NL
                    + " - HTTPResponse=" + getHTTPResponse() + NL
                    + " - remote=" + remote + NL
                    + " - className=" + getClassName() + NL
                    + " - methodName=" + getMethodName() + NL
                    + " - lineNumber=" + getLineNumber() + NL
                    + " - toString=" + toString() + NL
                    + " - location=" + getLocation() + NL
                    + " - trace=" + getTrace();
            return dump;
    }


    /**
     * INVALID_ARCHITECTURE subclass
     * See TExceptionEnum value for description
     */
    public static class INVALID_ARCHITECTURE extends TException
    {
        public INVALID_ARCHITECTURE(Exception ex)
        {
            super(TExceptionEnum.INVALID_ARCHITECTURE, ex);
        }
        public INVALID_ARCHITECTURE(String msg)
        {
            super(TExceptionEnum.INVALID_ARCHITECTURE, msg);
        }
        public INVALID_ARCHITECTURE(String msg, Exception ex)
        {
            super(TExceptionEnum.INVALID_ARCHITECTURE, msg, ex);
        }
    }

    /**
     * REQUEST_INVALID subclass
     * See TExceptionEnum value for description
     */
    public static class REQUEST_INVALID extends TException
    {
        public REQUEST_INVALID(Exception ex)
        {
            super(TExceptionEnum.REQUEST_INVALID, ex);
        }
        public REQUEST_INVALID(String msg)
        {
            super(TExceptionEnum.REQUEST_INVALID, msg);
        }
        public REQUEST_INVALID(String msg, Exception ex)
        {
            super(TExceptionEnum.REQUEST_INVALID, msg, ex);
        }
    }

    /**
     * REQUEST_ELEMENT_UNSUPPORTED subclass
     * See TExceptionEnum value for description
     */
    public static class REQUEST_ELEMENT_UNSUPPORTED extends TException
    {
        public REQUEST_ELEMENT_UNSUPPORTED(Exception ex)
        {
            super(TExceptionEnum.REQUEST_ELEMENT_UNSUPPORTED, ex);
        }
        public REQUEST_ELEMENT_UNSUPPORTED(String msg)
        {
            super(TExceptionEnum.REQUEST_ELEMENT_UNSUPPORTED, msg);
        }
        public REQUEST_ELEMENT_UNSUPPORTED(String msg, Exception ex)
        {
            super(TExceptionEnum.REQUEST_ELEMENT_UNSUPPORTED, msg, ex);
        }
    }

    /**
     * CONCURRENT_UPDATE subclass
     * See TExceptionEnum value for description
     */
    public static class CONCURRENT_UPDATE extends TException
    {
        public CONCURRENT_UPDATE(Exception ex)
        {
            super(TExceptionEnum.CONCURRENT_UPDATE, ex);
        }
        public CONCURRENT_UPDATE(String msg)
        {
            super(TExceptionEnum.CONCURRENT_UPDATE, msg);
        }
        public CONCURRENT_UPDATE(String msg, Exception ex)
        {
            super(TExceptionEnum.CONCURRENT_UPDATE, msg, ex);
        }
    }

    /**
     * INVALID_OR_MISSING_PARM subclass
     * See TExceptionEnum value for description
     */
    public static class INVALID_OR_MISSING_PARM extends TException
    {
        public INVALID_OR_MISSING_PARM(Exception ex)
        {
            super(TExceptionEnum.INVALID_OR_MISSING_PARM, ex);
        }
        public INVALID_OR_MISSING_PARM(String msg)
        {
            super(TExceptionEnum.INVALID_OR_MISSING_PARM, msg);
        }
        public INVALID_OR_MISSING_PARM(String msg, Exception ex)
        {
            super(TExceptionEnum.INVALID_OR_MISSING_PARM, msg, ex);
        }
    }

    /**
     * INVALID_CONFIGURATION subclass
     * See TExceptionEnum value for description
     */
    public static class INVALID_CONFIGURATION extends TException
    {
        public INVALID_CONFIGURATION(Exception ex)
        {
            super(TExceptionEnum.INVALID_CONFIGURATION, ex);
        }
        public INVALID_CONFIGURATION(String msg)
        {
            super(TExceptionEnum.INVALID_CONFIGURATION, msg);
        }
        public INVALID_CONFIGURATION(String msg, Exception ex)
        {
            super(TExceptionEnum.INVALID_CONFIGURATION, msg, ex);
        }
    }

    /**
     * INVALID_DATA_FORMAT subclass
     * See TExceptionEnum value for description
     */
    public static class INVALID_DATA_FORMAT extends TException
    {
        public INVALID_DATA_FORMAT(Exception ex)
        {
            super(TExceptionEnum.INVALID_DATA_FORMAT, ex);
        }
        public INVALID_DATA_FORMAT(String msg)
        {
            super(TExceptionEnum.INVALID_DATA_FORMAT, msg);
        }
        public INVALID_DATA_FORMAT(String msg, Exception ex)
        {
            super(TExceptionEnum.INVALID_DATA_FORMAT, msg, ex);
        }
    }

    /**
     * GENERAL_EXCEPTION subclass
     * See TExceptionEnum value for description
     */
    public static class GENERAL_EXCEPTION extends TException
    {
        public GENERAL_EXCEPTION(Exception ex)
        {
            super(TExceptionEnum.GENERAL_EXCEPTION, ex);
        }
        public GENERAL_EXCEPTION(String msg)
        {
            super(TExceptionEnum.GENERAL_EXCEPTION, msg);
        }
        public GENERAL_EXCEPTION(String msg, Exception ex)
        {
            super(TExceptionEnum.GENERAL_EXCEPTION, msg, ex);
        }
    }

    /**
     * UNIMPLEMENTED_CODE subclass
     * See TExceptionEnum value for description
     */
    public static class UNIMPLEMENTED_CODE extends TException
    {
        public UNIMPLEMENTED_CODE(Exception ex)
        {
            super(TExceptionEnum.UNIMPLEMENTED_CODE, ex);
        }
        public UNIMPLEMENTED_CODE(String msg)
        {
            super(TExceptionEnum.UNIMPLEMENTED_CODE, msg);
        }
        public UNIMPLEMENTED_CODE(String msg, Exception ex)
        {
            super(TExceptionEnum.UNIMPLEMENTED_CODE, msg, ex);
        }
    }

    /**
     * USER_NOT_AUTHORIZED subclass
     * See TExceptionEnum value for description
     */
    public static class USER_NOT_AUTHORIZED extends TException
    {
        public USER_NOT_AUTHORIZED(Exception ex)
        {
            super(TExceptionEnum.USER_NOT_AUTHORIZED, ex);
        }
        public USER_NOT_AUTHORIZED(String msg)
        {
            super(TExceptionEnum.USER_NOT_AUTHORIZED, msg);
        }
        public USER_NOT_AUTHORIZED(String msg, Exception ex)
        {
            super(TExceptionEnum.USER_NOT_AUTHORIZED, msg, ex);
        }
    }

    /**
     * USER_NOT_AUTHENTICATED subclass
     * See TExceptionEnum value for description
     */
    public static class USER_NOT_AUTHENTICATED extends TException
    {
        public USER_NOT_AUTHENTICATED(Exception ex)
        {
            super(TExceptionEnum.USER_NOT_AUTHENTICATED, ex);
        }
        public USER_NOT_AUTHENTICATED(String msg)
        {
            super(TExceptionEnum.USER_NOT_AUTHENTICATED, msg);
        }
        public USER_NOT_AUTHENTICATED(String msg, Exception ex)
        {
            super(TExceptionEnum.USER_NOT_AUTHENTICATED, msg, ex);
        }
    }

    /**
     * EXTERNAL_SERVICE_UNAVAILABLE subclass
     * See TExceptionEnum value for description
     */
    public static class EXTERNAL_SERVICE_UNAVAILABLE extends TException
    {
        public EXTERNAL_SERVICE_UNAVAILABLE(Exception ex)
        {
            super(TExceptionEnum.EXTERNAL_SERVICE_UNAVAILABLE, ex);
        }
        public EXTERNAL_SERVICE_UNAVAILABLE(String msg)
        {
            super(TExceptionEnum.EXTERNAL_SERVICE_UNAVAILABLE, msg);
        }
        public EXTERNAL_SERVICE_UNAVAILABLE(String msg, Exception ex)
        {
            super(TExceptionEnum.EXTERNAL_SERVICE_UNAVAILABLE, msg, ex);
        }
    }

    /**
     * LOCKING_ERROR subclass
     * See TExceptionEnum value for description
     */
    public static class LOCKING_ERROR extends TException
    {
        public LOCKING_ERROR(Exception ex)
        {
            super(TExceptionEnum.LOCKING_ERROR, ex);
        }
        public LOCKING_ERROR(String msg)
        {
            super(TExceptionEnum.LOCKING_ERROR, msg);
        }
        public LOCKING_ERROR(String msg, Exception ex)
        {
            super(TExceptionEnum.LOCKING_ERROR, msg, ex);
        }
    }

    /**
     * FIXITY_CHECK_FAILS subclass
     * See TExceptionEnum value for description
     */
    public static class FIXITY_CHECK_FAILS extends TException
    {
        public FIXITY_CHECK_FAILS(Exception ex)
        {
            super(TExceptionEnum.FIXITY_CHECK_FAILS, ex);
        }
        public FIXITY_CHECK_FAILS(String msg)
        {
            super(TExceptionEnum.FIXITY_CHECK_FAILS, msg);
        }
        public FIXITY_CHECK_FAILS(String msg, Exception ex)
        {
            super(TExceptionEnum.FIXITY_CHECK_FAILS, msg, ex);
        }
    }

    /**
     * REQUESTED_ITEM_NOT_FOUND subclass
     * See TExceptionEnum value for description
     */
    public static class REQUESTED_ITEM_NOT_FOUND extends TException
    {
        public REQUESTED_ITEM_NOT_FOUND(Exception ex)
        {
            super(TExceptionEnum.REQUESTED_ITEM_NOT_FOUND, ex);
        }
        public REQUESTED_ITEM_NOT_FOUND(String msg)
        {
            super(TExceptionEnum.REQUESTED_ITEM_NOT_FOUND, msg);
        }
        public REQUESTED_ITEM_NOT_FOUND(String msg, Exception ex)
        {
            super(TExceptionEnum.REQUESTED_ITEM_NOT_FOUND, msg, ex);
        }
    }

    /**
     * REQUEST_ITEM_EXISTS subclass
     * See TExceptionEnum value for description
     */
    public static class REQUEST_ITEM_EXISTS extends TException
    {
        public REQUEST_ITEM_EXISTS(Exception ex)
        {
            super(TExceptionEnum.REQUEST_ITEM_EXISTS, ex);
        }
        public REQUEST_ITEM_EXISTS(String msg)
        {
            super(TExceptionEnum.REQUEST_ITEM_EXISTS, msg);
        }
        public REQUEST_ITEM_EXISTS(String msg, Exception ex)
        {
            super(TExceptionEnum.REQUESTED_ITEM_NOT_FOUND, msg, ex);
        }
    }

    /**
     * Attempt to read or write to remote external IO service fails
     */
    public static class REMOTE_IO_SERVICE_EXCEPTION extends TException
    {
        public REMOTE_IO_SERVICE_EXCEPTION(Exception ex)
        {
            super(TExceptionEnum.SQL_EXCEPTION, ex);
        }
        public REMOTE_IO_SERVICE_EXCEPTION(String msg)
        {
            super(TExceptionEnum.REMOTE_IO_SERVICE_EXCEPTION, msg);
        }
        public REMOTE_IO_SERVICE_EXCEPTION(String msg, Exception ex)
        {
            super(TExceptionEnum.REMOTE_IO_SERVICE_EXCEPTION, msg, ex);
        }
    }

    /**
     * SQL_EXCEPTION subclass
     * See TExceptionEnum value for description
     */
    public static class SQL_EXCEPTION extends TException
    {
        public SQL_EXCEPTION(Exception ex)
        {
            super(TExceptionEnum.SQL_EXCEPTION, ex);
        }
        public SQL_EXCEPTION(String msg)
        {
            super(TExceptionEnum.SQL_EXCEPTION, msg);
        }
        public SQL_EXCEPTION(String msg, Exception ex)
        {
            super(TExceptionEnum.SQL_EXCEPTION, msg, ex);
        }
    }

}