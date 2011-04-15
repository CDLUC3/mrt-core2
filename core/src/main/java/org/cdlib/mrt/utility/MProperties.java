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

import org.cdlib.mrt.utility.TException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class MProperties {
    protected static LoggerInf.LogLevel fileLoadLevel = LoggerInf.LogLevel.DEBUG;

    protected Properties builtProperties = new Properties();

    /**
    * @return as Properties
    */
    public Properties getProperties () {
        return builtProperties;
    }

    /**
     * Loads properties from one or more properties files.
     *
     * @param propertiesFileNames An array of property file names. Names are
     * relative to the base of the classpath.
     */
    public void loadProperties(LoggerInf logger, String[] propertiesFileNames)
        throws TException
    {
        ClassLoader classLoader =
            getClass().getClassLoader();

        int filesLoaded = 0;
        for (int i = 0; i < propertiesFileNames.length ; i++)
        {
            try
            {
                if (propertiesFileNames[i] == null) continue;
                logger.logMessage(
                    "MFrame: Loading properties from: " +
                    propertiesFileNames[i],
                    fileLoadLevel);
                InputStream stream =
                    classLoader.getResourceAsStream(propertiesFileNames[i]);
                builtProperties.load(stream);
                filesLoaded++;
            }
            catch(Exception e)
            {
                logger.logError(
                    "MFrame: Warning: Failed to load framework properties from: " +
                    propertiesFileNames[i] +
                    " Exception: " + e,
                    fileLoadLevel);
                // Modified (ssugarman): Don't throw exception if at least one
                // properties stream was loaded.
                // Modified (ssugarman): Don't throw exception if at least one
                // properties stream was loaded.
                if ((i == (propertiesFileNames.length - 1)) && (filesLoaded == 0))
                {
                    // Rethrow the exception
                    throw new TException.GENERAL_EXCEPTION(
                        "Failed to load at least one properties file");
                }
            }
        }
        //dump(logger, "MProperties");
    }

    public void load (InputStream input)
        throws TException{
        try {
            builtProperties.load(input);
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public String getProperty (String key) {
        return builtProperties.getProperty(key);
    }
    
    public String getProperty (String key, String defaultValue)
    {
        return builtProperties.getProperty(key, defaultValue);
    }

    public void addProperties (String resource)
            throws TException
    {
        Properties local = PropertiesUtil.loadProperties(resource);
        addProperties(local);
    }

    public void setProperty (String key, String value) {
        builtProperties.setProperty(key, value);
    }

    public void addProperties (Properties table) {
        Enumeration keys = table.keys();
        while (keys.hasMoreElements())
        {
            String key = (String)keys.nextElement();
            String value = table.getProperty(key);
            builtProperties.setProperty(key, value);
        }
    }


    /**
     *
     * Build string containing display form of Properties contents
     *
     * @param msg Display message for this dump
     * @param prop Properties file to be dumped
     * @return String containing display
     */
    public void dump(LoggerInf logger, String msg)
    {
        //if (lvl > logger.getErrorMaxLevel()) return;

        Enumeration e = builtProperties.propertyNames();
        String key = null;
        String value = null;
        while( e.hasMoreElements() )
        {
           key = (String)e.nextElement();
           value = builtProperties.getProperty(key);
           if (value != null) {
               System.out.println(" " + key + "=\"" + value + "\" ");
           }
        }
        return;
    }
}

