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

import org.cdlib.mrt.utility.*;
import com.jolbox.bonecp.*;
import java.sql.*;


/**
 * Database Connection Pool explicitly set up to run with BoneCP
 * @author dloy
 */
public class DBConnectionPool
{

    protected static final String NAME = "DBConnectionPool";
    protected static final String MESSAGE = NAME + ": ";
    private String url, user, password;
    final private long timeout=60000;
    protected BoneCP connectionPool = null;

    public static DBConnectionPool getDBConnectionPool(String url, String user, String password)
        throws TException
    {
        try {
            if (StringUtil.isEmpty(url)) {
                throw new TException.INVALID_OR_MISSING_PARM("url not supplied");
            }
            if (StringUtil.isEmpty(user)) {
                throw new TException.INVALID_OR_MISSING_PARM("user not supplied");
            }
            if (password == null) {
                throw new TException.INVALID_OR_MISSING_PARM("password not supplied");
            }
            return new DBConnectionPool(url, user, password);

        } catch (Exception ex) {
            System.out.println("MrtConnectionPool Exception:" + ex);
            throw new TException(ex);
        }
    }

    protected DBConnectionPool(String url, String user, String password)
            throws TException
    {
        try {
            this.url = url;
            this.user = user;
            this.password = password;

            if (connectionPool != null) {
                System.out.println(MESSAGE + "connection already exists - no startup");
            }
            BoneCPConfig config = new BoneCPConfig();	// create a new configuration object
            config.setJdbcUrl(url);	      // set the JDBC url
            config.setUsername(user);         // set the username
            config.setPassword(password);     // set the password
            config.setConnectionTimeoutInMs(timeout);
            //config.setDeregisterDriverOnClose(true);
            connectionPool= new BoneCP(config); 	// setup the connection pool

        } catch (Exception ex) {
            throw new TException(ex);
        }
   }

    public synchronized void closeConnections()
    {
        try {
            //trace("closeConnection");
            if (connectionPool != null) {
                connectionPool.close();
                System.out.println(MESSAGE + "closeConnection performed");
            }
        } catch (Exception ex) {
            System.out.println("Exception on closeConnection: " + ex);
            ex.printStackTrace();
            
        } finally {
            connectionPool = null;
        }
    }


    public synchronized Connection getConnection() throws SQLException
    {
        if (connectionPool == null) return null;
        try {
            return connectionPool.getConnection();
            
        } catch (SQLException sqlEx) {
            System.out.println("Exception on connection:" + sqlEx);
            //sqlEx.printStackTrace();
            return null;
        }
    }

    public synchronized Connection getConnection(boolean autoCommit) throws SQLException
    {
        Connection conn = getConnection();
        if (conn == null) return null;
        conn.setAutoCommit(autoCommit);
        return conn;
    }
    
    public void trace(String header)
    {
        
            try {
                throw new TException.GENERAL_EXCEPTION("test");
            } catch (Exception testex) {
                System.out.println("***WARNING TRACE: " + header);
                testex.printStackTrace();
            }
    }
}
