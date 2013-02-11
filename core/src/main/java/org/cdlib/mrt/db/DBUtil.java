/******************************************************************************
Copyright (c) 2005-2012, Regents of the University of California
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
*******************************************************************************/
package org.cdlib.mrt.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.SQLUtil;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;


/**
 * This interface defines the functional API for a Curational Storage Service
 * @author dloy
 */
public class DBUtil
{

    protected static final String NAME = "DBUtil";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected static final String NL = System.getProperty("line.separator");


    
    protected DBUtil() {}


    public static Properties[] cmd(
            Connection connection,
            String cmd,
            LoggerInf logger)
        throws TException
    {
        if (StringUtil.isEmpty(cmd)) {
            throw new TException.INVALID_OR_MISSING_PARM("cmd not supplied");
        }
        if (connection == null) {
            throw new TException.INVALID_OR_MISSING_PARM("connection not supplied");
        }
        if (logger == null) {
            throw new TException.INVALID_OR_MISSING_PARM("logger not supplied");
        }
        try {
            PreparedStatement pstmt = connection.prepareStatement (cmd);
            ResultSet resultSet = pstmt.executeQuery();
            Properties [] results = SQLUtil.getResult(resultSet,logger);
            if (logger.getMessageMaxLevel() >= 10) {
                for (Properties result : results) {
                    logger.logMessage(PropertiesUtil.dumpProperties(MESSAGE + "getOperation", result), 10);
                }
            }
            return results;

        } catch(Exception e) {
            String msg = "Exception"
                + " - cmd=" + cmd
                + " - exception:" + e;

            logger.logError(MESSAGE + "getOperation - " + msg, 0);
            throw new TException.SQL_EXCEPTION(msg, e);
        }
     }


    public static boolean exec(
            Connection connection,
            String replaceCmd,
            LoggerInf logger)
        throws TException
    {
        if (StringUtil.isEmpty(replaceCmd)) {
            throw new TException.INVALID_OR_MISSING_PARM("replaceCmd not supplied");
        }
        if (connection == null) {
            throw new TException.INVALID_OR_MISSING_PARM("connection not supplied");
        }
        try {

            Statement statement = connection.createStatement();
            ResultSet resultSet = null;
            boolean works = statement.execute(replaceCmd);
            return works;

        } catch(Exception e) {
            String msg = "Exception"
                + " - sql=" + replaceCmd
                + " - exception:" + e;

            logger.logError(MESSAGE + "exec - " + msg, 0);
            System.out.println(msg);
            throw new TException.SQL_EXCEPTION(msg, e);
        }
    }

    public static String buildModify(Properties prop)
    {
        Enumeration e = prop.propertyNames();
        String key = null;
        String value = null;
        StringBuffer buf = new StringBuffer();
        while( e.hasMoreElements() )
        {
           key = (String)e.nextElement();
           value = prop.getProperty(key);
           if (buf.length() > 0) buf.append(",");
           buf.append(key + "='"  + SQLUtil.sqlEsc(value) + "'");
        }
        return buf.toString();
    }

    /**
     * Build a select query from a set of properties
     * Individual search elements are adjusted based on presents of % for triggering a like relation.
     * If the element is a url, then a trailing * is used to trigger the like.
     * @param prop
     * @return 
     */
    public static String buildSelect(Properties prop)
    {
        Enumeration e = prop.propertyNames();
        String key = null;
        String value = null;
        StringBuffer buf = new StringBuffer();
        while( e.hasMoreElements() )
        {
            key = (String)e.nextElement();
            value = prop.getProperty(key);
            if (StringUtil.isEmpty(value)) continue;
            if (buf.length() > 0) {
                buf.append(" and ");
            }
            
            String eq = "=";
            if (isURL(value)) {
                if (value.contains("*")) {
                    eq = " like ";
                    value = value.replace("%", "\\%");
                    value = value.replace("*", "%");
                }
            } else {
                if (value.contains("%")) eq = " like ";
            }
                
           buf.append(key + eq + "'"  + SQLUtil.sqlEsc(value) + "'");
        }
        return buf.toString();
    }

    protected static boolean isURL(String value) 
    {
        String test = "";
        if (value.length() <= 8) return false;
        test = value.substring(0,8).toLowerCase();
        if (test.contains("://")) return true;
        return false;
    }

    protected static void log(String msg)
    {
        if (!DEBUG) return;
        System.out.println(MESSAGE + msg);
    }
}

