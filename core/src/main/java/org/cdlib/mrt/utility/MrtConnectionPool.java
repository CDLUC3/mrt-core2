package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.*;
import java.sql.*;
import java.util.*;

class ConnectionReaper extends Thread {

    private MrtConnectionPool pool;
    private final long delay=300000;

    ConnectionReaper(MrtConnectionPool pool) {
        this.pool=pool;
    }

    @Override
    public void run() {
        while(true) {
           try {
              sleep(delay);
           } catch( InterruptedException e) { }
           boolean ok = pool.reapConnections();
           if (!ok) break;
        }
    }
}

public class MrtConnectionPool {

    private Vector connections;
    private String url, user, password;
    final private long timeout=60000;
    private ConnectionReaper reaper;
    final private int poolsize=10;

    public static MrtConnectionPool getMrtConnectionPool(String url, String user, String password)
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
            return new MrtConnectionPool(url, user, password);

        } catch (Exception ex) {
            System.out.println("MrtConnectionPool Exception:" + ex);
            throw new TException(ex);
        }
    }

    protected MrtConnectionPool(String url, String user, String password)
    {
        this.url = url;
        this.user = user;
        this.password = password;
        connections = new Vector(poolsize);
        reaper = new ConnectionReaper(this);
        reaper.start();
   }

    public synchronized boolean reapConnections()
    {
        if (connections == null) return false;
        long stale = System.currentTimeMillis() - timeout;
        Enumeration connlist = connections.elements();
        while((connlist != null) && (connlist.hasMoreElements())) {
            MrtConnection conn = (MrtConnection)connlist.nextElement();

            if((conn.inUse()) && (stale >conn.getLastUse()) &&
                                            (!conn.validate())) {
                removeConnection(conn);
            }
        }
        return true;
   }

    public synchronized void closeConnections()
    {
        Enumeration connlist = connections.elements();

        while((connlist != null) && (connlist.hasMoreElements())) {
            MrtConnection connection = (MrtConnection)connlist.nextElement();
            removeConnection(connection);
            try {
                connection.close();
                System.out.println("removeConnection: connection closed");
            } catch (Exception ex) { }
            System.out.println("MrtConnectionPool: Connection closed");
        }

        connections = null;
    }

    private synchronized void removeConnection(MrtConnection conn) {

        connections.removeElement(conn);
    }


    public synchronized Connection getConnection() throws SQLException
    {
        MrtConnection c;
        for(int i = 0; i < connections.size(); i++) {
           c = (MrtConnection)connections.elementAt(i);
           if (c.lease()) {
              return c.conn;
           }
        }

        Connection conn = DriverManager.getConnection(url, user, password);
        c = new MrtConnection(conn, this);
        c.lease();
        connections.addElement(c);
        return c.conn;
    }

    public synchronized Connection getConnection(boolean autoCommit) throws SQLException
    {
        Connection conn = getConnection();
        conn.setAutoCommit(autoCommit);
        return conn;
    }

   public synchronized void returnConnection(MrtConnection conn) {
      conn.expireLease();
   }
}
