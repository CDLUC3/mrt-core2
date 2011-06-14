/*
 * DataManagerMYSQL.java
 *
 * Created on January 23, 2004, 2:50 PM
 */
/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved   
*********************************************************************/

package org.cdlib.mrt.utility;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

/**
 * Framework MySQL common routines
 * @author  David Loy
 */

public class SQLUtil

{
    protected static final String NAME = "SQLUtil";
    protected static final String MESSAGE = NAME + ": ";
    
    
    /** Return a row from a table
     *
     * @param tableName The name of the table for the insert
     * @param whereProp Contains a Properties list of name value pairs 
     * for setting a select WHERE option
     * @param extractKeys column names to be extracted
     * @param connection JDBC connection
     *
     * @return A result object containing status and message
     * <p>The row values are available as a Properties object
     * available by calling the DataManagerMYSQL method getRow()
     * <br>Note that a <b>status</b> and <b>message</b>values are required on this response
     */
    public static Properties[] cmd(
            String sqlQuery,
            Connection connection,
            LoggerInf logger)
        throws TException
    {
        
        try {            

            ResultSet resultSet =
                executeSQLRequest(
                    sqlQuery, connection);
            return getResult(resultSet, logger);

        } catch (TException tex) {
            throw tex;

        } catch(Exception e) {
            if (logger != null) {
                logger.logError(MESSAGE + "cmd"
                        + " - sqlQuery=" + sqlQuery
                        + " - Exception:" + e
                        , 0);
                logger.logError(MESSAGE + "cmd-SQLException trace:" 
                        + StringUtil.stackTrace(e), 10);
            }
            log("stack:" + StringUtil.stackTrace(e));
            throw new
                TException.GENERAL_EXCEPTION(
                MESSAGE + "getResult cmd failed", e);

        }
 
    }
    /** Return a row from a table
     *
     * @param tableName The name of the table for the insert
     * @param whereProp Contains a Properties list of name value pairs 
     * for setting a select WHERE option
     * @param extractKeys column names to be extracted
     * @param connection JDBC connection
     *
     * @return A result object containing status and message
     * <p>The row values are available as a Properties object
     * available by calling the DataManagerMYSQL method getRow()
     * <br>Note that a <b>status</b> and <b>message</b>values are required on this response
     */
    public static Properties[] getResult(
            ResultSet resultSet,
            LoggerInf logger)
        throws TException
    {
        Vector <Properties> result = new Vector<Properties>(10);
        try {    
            
            // extract all rows if there are any
            if (resultSet != null) {
                while (resultSet.next()) {
                    Properties retProp = getSelectResults(resultSet);
                    if (retProp != null) result.add(retProp);
                }
            }

	    // Close result set and statement
            if (resultSet != null) {
                if (resultSet.getStatement() != null) resultSet.getStatement().close();
                resultSet.close();
            }

            Properties [] props = result.toArray(new Properties[0]);
            return props;
            


        } catch (TException tex) {
            throw tex;

        } catch(Exception e) {
            if (logger != null) {
                logger.logError(MESSAGE + "cmd"
                        + " - Exception:" + e
                        , 0);
                logger.logError(MESSAGE + "cmd-Exception trace:"
                        + StringUtil.stackTrace(e), 10);
            }
            log("stack:" + StringUtil.stackTrace(e));
            throw new
                TException.GENERAL_EXCEPTION(
                MESSAGE + "getResult cmd failed", e);

        }
 
    }
    
    public static void log(String msg)
    {
        System.out.println(msg);
    }

    /**
     * Executes a SQL request against a specified database using the
     * supplied connection
     *
     * @param connection Connection object to use for the call
     * @param request The SQL request to be executed
     * @return A ResultSet object
     * @exception SQLException If the request can not be executed
     */
    public static ResultSet executeSQLRequest(
        String sqlRequest,
        Connection connection)
        throws SQLException
    {
        try
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = null;
            if (statement.execute(sqlRequest))
            {
                resultSet = statement.getResultSet();
            }

            return resultSet;
        }
        catch(SQLException se)
        {
            throw se;
        }
        catch(Exception e)
        {
            log(
                "DataManagerJDBC: Failed to execute SQL request: " +
                sqlRequest + " Exception: " + e);
            throw new SQLException("Failed to execute SQL request");
        }
    }
    

   
    /** get a list from elements in list
     *
     * @param resultSet jdbc set for extraction
     * @return comma delimited list for Select 
     */
    public static Properties getSelectResults(
            ResultSet resultSet)
        throws Exception
    {
        int selectCnt = 0;
        if (resultSet == null) return null;
        
        String key = null;
        String value = null;
        Properties prop = new Properties();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCnt = rsmd.getColumnCount();
        for (int i=1; i <= columnCnt; i++) {
            try {
                key = rsmd.getColumnLabel(i);
                value = resultSet.getString(key);
            }
            catch (Exception ex) {

                try {
                    key = rsmd.getColumnName(i);
                    value = resultSet.getString(key);
                } catch (Exception ex2) {
                    value = null;
                }
                if (value == null) {
                    log(
                        "getSelectResults(" + i + ") - columnCnt=" 
                            + columnCnt + " Exception=" + ex);
                }
            }
            
            if (value != null) {
                    prop.setProperty(key, value);
                    selectCnt++;
                }
        }
        if (selectCnt == 0) return null;
        return prop;
    }



        /**
         * escape the special characters needed for sql statements.
         * This is null (ASCII 00), backslash, single and double quotes.
         * @param in - the string to process
         * @return the processed string
         */
        public static String sqlEsc (String in)
        {

        // The following values are used in sqlEsc()
            String sqlEscapeFrom =
         "\\" +  "\'"; //  "\000" +  "\"" + "\n" +
         // "\t" + "\r" + "\b" + "%" + "_";
            String [] sqlEscapeTo =
        {"\\\\", "\\'" }; // "\\\000",  "\\\"", "\\n",
         // "\\t", "\\r", "\\b", "\\%", "\\_"};
            return StringUtil.xchange (in, sqlEscapeFrom, sqlEscapeTo); 
        }
    
}
