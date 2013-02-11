/*********************************************************************
    Copyright 2005 Regents of the University of California
    All rights reserved   
*********************************************************************/

package org.cdlib.mrt.utility;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.cdlib.mrt.utility.DateUtil;

/**
 * Generalized counting queue 
 *
 * Copied - DLoy
 */
public class TallyTable {
    protected static final String NAME = "TallyTable";
    protected static final String MESSAGE = NAME + ":";
    public static final String LS =  System.getProperty("line.separator");

    protected Hashtable<String,Long> queue = new Hashtable<String,Long>();
        
    /**
     * increment counts using another CountQueue
     * @param inCount CountQueue to be added
     */
    public void bump(TallyTable inCount)
    {
        if ((inCount == null) || (inCount.size() == 0)) return;
        Enumeration e = inCount.queue.keys();
        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            Long amount = inCount.queue.get(name);
            bump(name, amount);
        }
    }
    
    /**
     * increment a counter with this name by 1 and save
     * @param name of the counter to be incremented
     */    
    public void bump(String name) 
    {
        bump(name, 1);
    }
    
    /**
     * increment a counter with this name and save
     * @param name of the counter to be incremented
     * @param amount to bump counter
     */
    public synchronized void bump(String name, long amount)
    {
        //System.out.println("BUMP name=" + name + " - amount=" + amount);
        if ((name == null) || (name.length() == 0)) return;

        Long total = queue.get(name);
        if (total == null) {
            total = new Long(0);
        }
        total += amount;
        queue.put(name, total);
        
        //System.out.println(message + "bump called:" + name + "=" + cnt.total);
    }

    /**
     * set tally value
     * @param name of the counter to be incremented
     * @param amount to bump counter
     */
    public synchronized void set(String name, long amount)
    {
        //System.out.println("BUMP name=" + name + " - amount=" + amount);
        if ((name == null) || (name.length() == 0)) return;
        queue.put(name, amount);
    }
    
    // Retrieve number of items in current queue
    public synchronized void clear() {
        queue.clear();
        //System.out.println(message + "clear called");
    }

    /**
     *  Add difference in time from arg to now
     * @param key - name of key to be incremented
     * @param from start time value
     * @return current time
     */
    public long addDiffTime(String key, long from)
    {
        long to = DateUtil.getEpochUTCDate();
        bump(key, to - from);
        return to;
    }


    /*
     * get float division
     * @param numerator numerator key
     * @param divisor divisor key
     * @raturn float divition if falues exist - otherwise null
     */
    public Double divide(String numerator, String divisor)
    {
        Long numeratorL = getValue(numerator);
        Long divisorL = getValue(divisor);
        if (numeratorL == null) return null;
        if (divisorL == null) return null;
        double numeratorD =  (double)numeratorL;
        double divisorD = (double)divisorL;
        if (divisorD == 0) return null;
        double result = numeratorD/divisorD;
        return new Double(result);
    }


    /*
     * Get value
     * @param key key of value to be returned
     * @return Long value for match key, or null
     */
    public Long getValue(String key)
    {
        if (StringUtil.isEmpty(key)) return null;
        return queue.get(key);
    }

    /**
     *  Add difference in time from arg to now
     * @param key - name of key to be incremented
     * @param from start time value
     * @return current time
     */
    public long getTime()
    {
        return DateUtil.getEpochUTCDate();
    }
    
    /**
     * increment a counter with this name and save
     * @param name of the counter to be incremented
     * @param amount to bump counter
     */
    public synchronized String dump()
    {
        //System.out.println(message + "dump queue size=" + queue.size());
        if (queue.size() == 0) return "";
        StringBuffer buf = new StringBuffer(queue.size() * 50);
        // sorted keys output  thanks to T. GUIRADO for the tip!
    	Vector v = new Vector(queue.keySet());
        Collections.sort(v);
        Iterator it = v.iterator();
        String key = null;
        long value = 0;
        while (it.hasNext()) {
            key =  (String)it.next();
            value =  queue.get(key);
            buf.append(" - " + key + "=" + value);
            //System.out.println(message + "append" + " - " + key + "=" + value);
        }
        return buf.toString();
    }

    /**
     * increment a counter with this name and save
     * @param name of the counter to be incremented
     * @param amount to bump counter
     */
    public synchronized String dumpProp()
    {
        //System.out.println(message + "dump queue size=" + queue.size());
        if (queue.size() == 0) return "";
        StringBuffer buf = new StringBuffer(queue.size() * 50);
        // sorted keys output  thanks to T. GUIRADO for the tip!
    	Vector v = new Vector(queue.keySet());
        Collections.sort(v);
        Iterator it = v.iterator();
        String key = null;
        long value = 0;
        while (it.hasNext()) {
            key =  (String)it.next();
            value =  queue.get(key);
            buf.append(key + "=" + value + LS);
            //System.out.println(message + "append" + " - " + key + "=" + value);
        }
        return buf.toString();
    }


    /**
     * Build sorted array of tally keys
     * @return
     */
    public String [] getCountKeys()
    {
        if (queue.size() == 0) return null;
        Vector v = new Vector(queue.keySet());
        Collections.sort(v);
        return (String[]) v.toArray(new String[0]);
    }

    /**
     * Get the count for this key
     * @param key key used for extracting tally count
     * @return tally count
     */
    public long getCount(String key)
    {
        Long value = queue.get(key);
        if (value == null) return 0;
        return value;
    }

    /**
     * number of tally items
     * @return number of tally items
     */
    public int size()
    {
        return queue.size();
    }

    /**
     * Load tally count from file
     * @param loadFile file to be loaded for tally
     * @throws TException
     */
    public void loadTable(File loadFile)
            throws TException
    {
        Properties prop = PropertiesUtil.loadFileProperties(loadFile);
        try {
            Enumeration e = prop.propertyNames();
            String key = null;
            String valueS = null;
            while( e.hasMoreElements() ) {
               key = (String)e.nextElement();
               valueS = prop.getProperty(key);
               if (valueS != null) {
                   Long value = Long.parseLong(valueS);
                   queue.put(key, value);
               }
            }

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "loadTable - Exception:" + ex);
        }
    }

    public Properties getAsProperties()
    {
        Properties prop = new Properties();
        Enumeration e = queue.keys();
        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            Long amount = queue.get(name);
            prop.setProperty(name, amount.toString());
        }
        return prop;
    }

    public void saveTable(File loadFile)
            throws TException
    {
        try {
            Properties prop = getAsProperties();
            String list = PropertiesUtil.buildLoadProperties(prop);
            FileUtil.string2File(loadFile, list);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "saveTable - Exception:" + ex);
        }
    }
    
    public Map getTallyMap()
    {
        return queue;
    }
}
