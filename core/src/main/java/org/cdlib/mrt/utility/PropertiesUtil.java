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

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.ServletRequest;
import java.io.InputStream;

/**
 * @author  DLoy
 */

public class PropertiesUtil 
{
    protected static final String NL = System.getProperty("line.separator");

    
    /**
     * Copy an existing set of properties to another properties
     *
     * @param fromProp properties to copy from
     * @return copied  properties 
     */    
    public static Properties copyProperties(
        Properties fromProp)
    {
        Properties toProp = new Properties();

        if (fromProp == null) return null;
        
        Enumeration e = fromProp.propertyNames();
        String key = null;
        String value = null;

        while( e.hasMoreElements() )
        {
           key = (String)e.nextElement();
           value = fromProp.getProperty(key);
           toProp.setProperty(new String(key), new String(value));
        }
        return toProp; 
    }

    /**
     * compare 2 Properties
     * @param prop1 first Properties
     * @param prop2 second Properties
     * @return true=values of same keys match
     */
    public static boolean equals(
        Properties prop1,
        Properties prop2)
    {
        if ((prop1 == null) && (prop2 == null)) return true;
        if (prop1 == null) return false;
        if (prop2 == null) return false;
        if (prop1.size() != prop2.size()) return false;
        Enumeration e = prop1.propertyNames();
        String key = null;
        String value1 = null;
        String value2 = null;

        while( e.hasMoreElements() )
        {
           key = (String)e.nextElement();
           value1 = prop1.getProperty(key);
           value2 = prop2.getProperty(key);
           if (!value1.equals(value2)) return false;
        }
        return true;
    }
    
    /**
     * Copy single property key-value
     * @param prop Property to copy to
     * @param key key of property
     * @param value value of property
     */
    public static void copyProperty(
            Properties prop,
            String key,
            String value)
    {
        if (prop == null) return;
        if ((key==null) || (key.length() == 0)) return;
        if ((value==null) || (value.length() == 0)) return;
        prop.setProperty(new String(key), new String(value));
    }
    
    /**
     *
     * Build string containing display form of Properties contents
     *
     * @param msg Display message for this dump
     * @param prop Properties file to be dumped
     * @return String containing display 
     */    
    public static String dumpProperties(String msg, Properties prop)
    {
        if (prop == null) return "(empty)";
        
        Enumeration e = prop.propertyNames();
        String key = null;
        String value = null;
        StringBuffer buf = new StringBuffer(200);
        buf.append(msg + ":[");

        while( e.hasMoreElements() )
        {
           key = (String)e.nextElement();
           value = prop.getProperty(key);
           if (value != null) {
               buf.append(" " + key + "=\"" + value + "\" ");
           }
        }
        buf.append("]");
        return buf.toString(); 
    }

    /**
     *
     * Build string containing display form of Properties contents
     *
     * @param msg Display message for this dump
     * @param prop Properties file to be dumped
     * @return String containing display
     */
    public static String buildLoadProperties(Properties prop)
    {
        if (prop == null) return "";

        Enumeration e = prop.propertyNames();
        String key = null;
        String value = null;
        StringBuffer buf = new StringBuffer(200);

        while( e.hasMoreElements() )
        {
           key = (String)e.nextElement();
           value = prop.getProperty(key);
           if (value != null) {
               if (buf.length() > 0) buf.append(NL);
               buf.append(key + ": " + value );
           }
        }
        return buf.toString();
    }
    
    /**
     *
     * Build string containing display form of Properties contents
     *
     * @param msg Display message for this dump
     * @param prop Properties file to be dumped
     * @param maxlen maximum length for prop to add
     * @return String containing display 
     */    
    public static String dumpProperties(String msg, Properties prop, int maxlen)
    {
        if (prop == null) return "(empty)";
        
        Enumeration e = prop.propertyNames();
        String key = null;
        String value = null;
        StringBuffer buf = new StringBuffer(200);
        buf.append(msg + ":[");
        String add = "";

        while( e.hasMoreElements() )
        {
           key = (String)e.nextElement();
           value = prop.getProperty(key);
           add = value;
           if (value != null) {
               if (value.length() > maxlen) {
                   add = value.substring(0, maxlen) + "...";
               }
               buf.append(" " + key + "=\"" + add + "\" ");
           }
        }
        buf.append("]");
        return buf.toString(); 
    }
     
    public static Properties addRequestToProperties(ServletRequest request)
    {
        if (request == null) return null;
        Enumeration list = request.getParameterNames();
        String name = null;
        String value = null;
        Properties retprop = new Properties();
        while (list.hasMoreElements()) {
            name = (String)list.nextElement();
            value = request.getParameter(name);
            retprop.put(name, value);
        }
        if (retprop.size() == 0) return null;
        return retprop;
    }

    /**
     * Loads properties from one or more properties files.
     *
     * @param Logger, to be used to write log messages, and to call
     *      "getClassLoader( )".
     * @param propertiesFileName A property file name. 
     */
    public static Properties loadProperties(String propertiesFileName)
        throws TException
    {
        String textMessage;
        Properties props = new Properties( );

        if (StringUtil.isEmpty(propertiesFileName))
        {
            textMessage = "PropertiesUtil.loadProperties( ):  method was " +
                "passed a null pointer for its second parameter (a " +
                "\"String[ ]\")";
            throw new TException.GENERAL_EXCEPTION(
                textMessage);
        }

        ClassLoader classLoader = props.getClass( ).getClassLoader( );

        try {
            InputStream stream =
                classLoader.getResourceAsStream(propertiesFileName);
            props.load(stream);
            try {
                stream.close();
            } catch (Exception ex) {}

        } catch(Exception e)  {
            textMessage = "PropertiesUtil.loadProperties( ):  Warning:  " +
                "Failed to load properties from file \"" +
                propertiesFileName + "\".  Exception:  \"" + e +
                "\", stack trace = \"" + StringUtil.stackTrace(e) + "\"";
            throw new TException.GENERAL_EXCEPTION(textMessage);
        }

        return(props);
    }
    
    /**
     * Loads properties from a properties files.
     * @param loadFile Properties file to be loaded
     * @return Properties
     * @throws org.cdlib.mrt.utility.MException
     */
    public static Properties loadFileProperties(File loadFile)
        throws TException
    {
        String textMessage;
        Properties props = new Properties( );
        InputStream stream = null;

        if ((loadFile == null) || !loadFile.exists() || !loadFile.isFile()) {
            textMessage = "PropertiesUtil: loadFileProperties - Invalid loadFile";
            throw new TException.INVALID_OR_MISSING_PARM( textMessage);
        }

        try {
            stream = new FileInputStream(loadFile);
            props.load(stream);

        } catch(Exception ex)  {
            textMessage = "PropertiesUtil: loadFileProperties -  Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION(textMessage);

        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception ex) {}

        }

        return(props);
    }
    
    /**
     * return an int value from a properties
     * @param prop Properties to be searched
     * @param key key of numeric value
     * @return integer numeric
     */
    public static int intProp(Properties prop, String key)
    {
        if (key == null) return 0;
        String valS = prop.getProperty(key);
        if ((valS == null) || (valS.length() == 0)) return 0;
        return Integer.parseInt(valS);
    }
    
    /**
     * return an int value from a properties
     * @param prop Properties to be searched
     * @param key key of numeric value
     * @return integer numeric
     */
    public static int getInt(Properties prop, String key)
    {
        
        if (key == null) return -1;
        String valS = prop.getProperty(key);
        if ((valS == null) || (valS.length() == 0)) return -1;
        int val = -1;
        try {
            val = Integer.parseInt(valS);
        } catch (Exception ex) {
            val = -1;
        }
        return val;
    }

    public static LinkedHashList<String, String> prop2LinkedHashList(Properties prop)
    {
        if (prop == null) return null;
        if (prop.size() == 0) return null;
        System.out.println(dumpProperties("prop2", prop));
        LinkedHashList<String, String> retList = new LinkedHashList<String, String>(prop.size());
        Enumeration list = prop.keys();
        String name = null;
        String value = null;
        while (list.hasMoreElements()) {
            name = (String)list.nextElement();
            value = prop.getProperty(name);
            retList.put(name, value);
        }
        return retList;
    }
}
