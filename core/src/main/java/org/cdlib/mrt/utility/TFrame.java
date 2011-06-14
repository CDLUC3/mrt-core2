/*
Copyright (c) 2005-2006, Regents of the University of California
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

import org.cdlib.mrt.utility.LoggerAbs;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

public class TFrame {

    protected static final String NAME = "MFrame";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = false;
    protected static LoggerInf.LogLevel loadExceptionLevel = LoggerInf.LogLevel.INFO;

    protected MProperties m_properties = new MProperties();
    protected LoggerInf m_logger;
    protected String m_frameworkID;
    protected HttpServletRequest m_httpServletRequest = null;


    /**
     * Registered classes
     */
    protected Hashtable m_classRegistry = new Hashtable(200);

    /**
     * Registered instances. When a single instance of a registered
     * class is desired, an instance is added to this registry.
     */
    protected Hashtable m_instanceRegistry = new Hashtable(200);

     /**
     * Constructs a framework, loading properties from one or more property
     * files. Files are loaded by the class loader as resources.
     *
     * @param propertiesFileNames An array of property file names. Names are
     * relative to the base of the classpath.
     * @param frameworkID Identifier for the framework. The identifier will
     * appear in log entries.
     */
    public TFrame(String[] propertiesFileNames)
        throws TException
    {
       this(propertiesFileNames, null);
    }

   /**
     * Constructs a framework, loading properties from one or more property
     * files. Files are loaded by the class loader as resources.
     *
     * @param propertiesFileNames An array of property file names. Names are
     * relative to the base of the classpath.
     */
    public TFrame(String[] propertiesFileNames, String frameworkID)
        throws TException
    {
        this(propertiesFileNames, frameworkID, null);
    }

   /**
    * Constructs a framework, loading properties from one or more property
    * files. Files are loaded by the class loader as resources.
    *
    * @param propertiesFileNames An array of property file names. Names are
    * relative to the base of the classpath.
    * @param frameworkID used on log entries
    * @param servicePath optional path to a file based properties
    */
    public TFrame(String[] propertiesFileNames, String frameworkID, String servicePathName)
        throws TException
    {
        // Create a default logger to send messages to standard out while we
        // build the framework.

        m_frameworkID = frameworkID;

        logDebug("MFrame entered");
        for (String name : propertiesFileNames) {
            logDebug("MFrame file=" + name);
        }
        try
        {
            m_logger = LoggerAbs.getTFileLogger(
                frameworkID,
                LoggerInf.LogLevel.SEVERE,
                LoggerInf.LogLevel.SEVERE);

            // Load properties from the properties files specified in constructor
            // arguments
            //System.out.println("+++Before loadProperties");
            m_properties.loadProperties(m_logger, propertiesFileNames);
            loadServiceProperties(servicePathName);

            // Register all classes specified in the framework properties
            registerClasses();

            // Set the logger to the logger class specified in the properties
            // files and initialize it with configuration properties
            String logPath = getProperty("fileLogger.path");
            if (StringUtil.isNotEmpty(logPath)) {
                m_logger = loadLogger(logPath);
            }

        } catch(Exception e) {
            m_logger.logError(
                "MFrame: Failed to initialize the framework. Exception: " + e,
                LoggerInf.LogLevel.SEVERE);
            if (e instanceof TException) {
                throw (TException)e;
            }
            throw new TException.GENERAL_EXCEPTION(e);
        }
    }

    /**
     * Load properties from an optional supplied path.
     * If the path is supplied and not valid then an exception will occur
     * @param servicePathName Name of current property that contains a File path to service based properties
     */
    protected void loadServiceProperties(String servicePathName)
        throws TException
    {
        logDebug("loadServiceProperties servicePathName=" + servicePathName);
        if (StringUtil.isEmpty(servicePathName)) return;
        String servicePath = null;
        try {
            servicePath = m_properties.getProperty(servicePathName);
            if (StringUtil.isEmpty(servicePath)) return;
            File serviceFile = new File(servicePath);
            InputStream fis = new FileInputStream(serviceFile);
            m_properties.load(fis);
            m_logger.logMessage("Service Properties loaded from:" + servicePath, 3);
            
        } catch (Exception ex) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "loadPropertiesPath: passed sevicePathName invalid:"
                    + servicePath + ": Exception:" + ex);
           
        }
    }


     /**
      * Loads the registered logger. The logger is loaded by reading the framework
      * property 'Logger' and instantiating an object for the class named in the
      * property's value.
      * <P>
      * The object that is instantiated is expected to implement the Logger
      * interface. If it does not, a default Logger is created and returned.
      *
      * @return The Logger object
      */
    protected LoggerInf loadLogger(String logPath)
        throws TException
    {
        try
        {
            LoggerInf newLog = LoggerAbs.getTFileLogger(getMFrameID(), logPath, getProperties());
            return newLog;

        } catch(Exception e) {
            m_logger.logError(
                "MFrame: Failed to load Logger. Exception: " + e,
                loadExceptionLevel);
            if (e instanceof TException) {
                throw (TException) e;
            }
            throw new TException.GENERAL_EXCEPTION(e);
        }
    }

    /**
     * save servlet requeest
     * @param httpServletRequest current servlet request
     */
    public void setHttpServletRequest(HttpServletRequest httpServletRequest)
    {
        m_httpServletRequest = httpServletRequest;
    }

    /**
     * return servlet requeest
     * @return current servlet request
     */
    public HttpServletRequest getHttpServletRequest()
    {
        return m_httpServletRequest;
    }

    /**
     * Returns the current logger
     *
     * @return The Logger object
     */
    public LoggerInf getLogger()
    {
        return m_logger;
    }


    /**
     * Return a deep clone of framework properties
     *
     * @return the framework properties with each element copied
     */
    public Properties getProperties()
    {
        return m_properties.getProperties();
    }

    /**
     * Gets a framework property
     *
     * @param propertyName Name of the property whose value is to be retrieved
     * @return The value of the property
     */
    public String getProperty(String propertyName)
    {
        return m_properties.getProperty(propertyName);
    }

    public String getProperty(String propertyName, String propertyDefault)
    {
        try
        {
            // If property not found, returns "null"
            return m_properties.getProperty(propertyName, propertyDefault);
        }
        catch(Exception e)
        {
            return null;
        }
    }



    /**
     * Gets a resource using the class loader
     *
     * @param resourceName Name of the file containing the resource. The name
     * may include a relative path which is interpreted as being relative to
     * the path "resources/" below the classpath root.
     * @return An inputstream for the resource
     */
    public InputStream getResource(String resourceName)
        throws TException
    {
        try
        {
            return getClass().getClassLoader().
                getResourceAsStream("resources/" + resourceName);
        }
        catch(Exception e)
        {
               m_logger.logError(
                "MFrame: Failed to get the AdminManager for entity: " +
                "Failed to get resource: " +
                resourceName +
                " Exception: " + e,
                0);
           throw new TException.GENERAL_EXCEPTION(
                "Failed to get a resource. Exception: " + e);
        }
    }

    /**
     * Saves a framework property if property does not exist
     *
     * @param propertyName Name of the property to be saved
     * @param propertyValue Value of the property to be saved
     */
    public void addNewProperty(String propertyName, String propertyValue)
    {
        try
        {
            String test = m_properties.getProperty(propertyName);
            if (test != null) return;
            m_properties.setProperty(propertyName, propertyValue);

        }
        catch(Exception e)
        {
            return;
        }
    }
    /**
     * Sets a framework property and adds it if it doesn't already exist
     *
     * @param propertyName Name of the property to be saved
     * @param propertyValue Value of the property to be saved
     */
    public void setProperty(String propertyName, String propertyValue)
    {
        try
        {
            m_properties.setProperty(propertyName, propertyValue);
        }
        catch(Exception e)
        {
            return;
        }
    }


    /**
     * Gets a class associated with a property name in the
     * framework's properties.
     *
     * @param propertyName The name of a property in the framework
     * properties that has the class name as its corresponding value
     * @return A Class object for the class
     */
    protected Class getComponentClass(String propertyName)
    {
       String className = getProperty(propertyName);
       try
        {
            /**************************
            m_logger.logMessage(
                "MFrame: Loading class " + className + " as " +
                propertyName,
                0);
            ***************************/
            Class classObj = Class.forName(className);
            return classObj;
        }
        catch(Exception e)
        {
            m_logger.logError(
                "MFrame: Failed to load class " +
                className + " as " +
                propertyName,
                loadExceptionLevel);
            return null;
        }
    }

    /**
     * Registers classes by examining the framework properties and extracting
     * values for all keys starting with "class". Each
     * value is assumed to be the name of a class to be registered.
     *
     */
    protected void registerClasses()
    {
        try
        {
             m_logger.logMessage(
                "MFrame: Registering classes..." ,
                loadExceptionLevel);
            Enumeration keys = m_properties.getProperties().keys();
            while (keys.hasMoreElements())
            {
                String key = (String)keys.nextElement();
                if (key.toLowerCase().startsWith("class."))
                {
                    try
                    {
                        Class componentClass = getComponentClass(key);
                        m_classRegistry.put(key.substring(6), componentClass);
                    }
                    catch(Exception e)
                    {
                        m_logger.logError(
                            "Warning: Can not find class: " +
                            key,
                            loadExceptionLevel);
                    }
                }
            }
             m_logger.logMessage(
                "MFrame: Registered " + m_classRegistry.size() +
		" classes" ,
                LoggerInf.LogLevel.INFO);
        }
        catch(Exception e)
        {
              m_logger.logError(
                "MFrame: Failed to register classes",
                LoggerInf.LogLevel.INFO);
        }
    }

    /**
     * Creates an instance of a registered class.
     *
     * @param registryKey The value of the registry key associated with the
     * registered class. This is the value to the right of "class." in the
     * properties file entry for the class.
     *
     * @return An object of the class
     * @throws InstantiationException if the object can not be
     * instantiated
     */
    public Object instantiate(String registryKey)
        throws TException
    {
       try
        {
            Class classObj = (Class)m_classRegistry.get(registryKey);
            Object object = classObj.newInstance();
            initialize(object);
            return object;
        }
        catch(Exception e)
        {
            m_logger.logError(
                "MFrame: Failed to instantiate " +
                registryKey +
                " Exception: " + e,
                0);
            throw new TException.GENERAL_EXCEPTION(
                "Failed to instantiate "+registryKey);
        }
    }

    /**
     * Creates instances of all registered classes sharing a common registry
     * key
     *
     * @param registryKey The value of the registry key associated with the
     * registered class. This is the value to the right of "class." in the
     * properties file entry for the class. More than one class is associated
     * with the registry key by appending ".n" to the right of the key, where
     * "n" varies from 1 to the number of classes associated with the key. For
     * example, multiple classes are associated with the key "foo" as follows:
     * <SL>
     * <LI> class.foo.1 = org.cdlib.mstor.ingest.FooFirst
     * <LI> class.foo.2 = org.cdlib.mstor.ingest.FooSecond
     * <LI> class.foo.3 = org.cdlib.mstor.ingest.FooThird
     *
     * @return A vector containing the instantiated objects
     * @throws InstantiationException if the object can not be
     * instantiated
     */
    public Vector instantiateAll(String registryKey)
        throws TException
    {
       Vector instances = new Vector(10,10);
       try
        {
            // Try to instantiate objects, starting with ".1". Break
            // when no object is found with ".n"
            for (int i = 1; i < 1001; i++)
            {
                try
                {
                    Class classObj =
                        (Class)m_classRegistry.get(
                            registryKey + "." +
                            i);
                    Object object = classObj.newInstance();
                    initialize(object);
                    instances.add(object);
                }
                catch(Exception e)
                {
                    break;
                }
            }
            return instances;
       }
        catch(Exception e)
        {
            m_logger.logError(
                "MFrame: Failed to instantiate (all) " +
                registryKey +
                " Exception: " + e,
                0);
            throw new TException.GENERAL_EXCEPTION(
                "Failed to instantiate (all) "+registryKey);
        }
    }

    /**
     * Gets the single instance of each class associated with a registry
     * key.
     *
     * @param registryKey The value of the registry key associated with the
     * registered class. This is the value to the right of "class." in the
     * properties file entry for the class. More than one class is associated
     * with the registry key by appending ".n" to the right of the key, where
     * "n" varies from 1 to the number of classes associated with the key. For
     * example, multiple classes are associated with the key "foo" as follows:
     * <SL>
     * <LI> class.foo.1 = org.cdlib.mstor.ingest.FooFirst
     * <LI> class.foo.2 = org.cdlib.mstor.ingest.FooSecond
     * <LI> class.foo.3 = org.cdlib.mstor.ingest.FooThird
     *
     * @return A vector containing the single instance objects
     * @throws InstantiationException if the object can not be
     * instantiated
     */
    public Vector getSingleInstances(String registryKey)
        throws TException
    {
       Vector instances = new Vector(10,10);
       try
        {
            // Try to instantiate objects, starting with ".1". Break
            // when no object is found with ".n"
            for (int i = 1; i < 1001; i++)
            {
                try
                {
                    if (m_classRegistry.get(registryKey + "." + i) == null)
                    {
                        break;
                    }
                    instances.add(
                        getSingleInstance(registryKey + "." + i));
                }
                catch(Exception e)
                {
                    break;
                }
            }
            return instances;
       }
        catch(Exception e)
        {
            m_logger.logError(
                "MFrame: Failed to get single instances (all) for " +
                registryKey +
                " Exception: " + e,
                0);
            throw new TException.GENERAL_EXCEPTION(
                "Failed to get single instances (all) for "+ registryKey);
        }
    }

    /**
     * Gets the single instance of a 'singleton' patterned
     * class. Checks the instance registry to see if an instance
     * of the desired class has already been created, and returns
     * the single instance if it has. If there is no existing
     * instance, one is created, registered and returned.
     *
     * @param registryKey The value of the registry key associated with the
     * registered class and instance. This is the value to the right of "class."
     * in the properties file entry for the class.
     *
     * @return An object of the class
     * @throws InstantiationException if the object can not be
     * instantiated
     */
    public Object getSingleInstance(String registryKey)
        throws TException
    {
       try
        {
            Class classObj = (Class)m_classRegistry.get(registryKey);

            String className = classObj.getName();
            Object object = m_instanceRegistry.get(className);
            if (object == null)
            {
                object = classObj.newInstance();
                if (object != null)
                {
                    initialize(object);
                    m_instanceRegistry.put(className, object);
                }
            }
            return object;
        }
        catch(Exception e)
        {
            m_logger.logError(
                "MFrame: Failed to get single instance of \"" +
                registryKey +
                "\", Exception:  \"" + e + "\", Stack Trace:  " +
                StringUtil.stackTrace(e),
                0);
            throw new TException.GENERAL_EXCEPTION(
                  "Failed to get single instance of "+registryKey);
        }
    }

    /**
     * If object implements MFrameComponent, initialize
     * it.
     *
     * @param object Object to be initialized
     */
    protected void initialize(Object object)
    {
        if (object instanceof TFrameComponent)
        {
            ((TFrameComponent)object).initialize(this);
            m_logger.logMessage(
                "MFrame: Initialized object: " + object.getClass().getName(),
                10);
        }
    }

    /**
     * Returns all framework properties
     *
     * @param none
     * @return the Properties object within the framework
     */
    public Properties getAllProperties( )
    {
        return m_properties.getProperties();
    }

    /**
     * Returns the service identifier
     *
     * @return the identifier
     */
    public String getMFrameID()
    {
        return m_frameworkID;
    }

    /**
     * Returns a generic handler for a specified type of service.
     *
     * @param key type of class
     * @param service An identifier of the service
     * @return An object that implements the GenericHandler
     * that is registered to generic for the specified service
     * @throws InstantiationException if an object can not be instantiated
     */
     public Object getRegistry(String key, String service)
    {
        String registryKey = key;
        if (StringUtil.isNotEmpty(service))
            registryKey += "." + service;
        try
        {
             Object formatter =
                getSingleInstance(registryKey);
            return formatter;
        }
        catch(Exception e)
        {
              m_logger.logError(
                "Framework: Failed to get a generic formatter: " +
                registryKey,
                0);
               throw new TRuntimeException (
                TExceptionEnum.GENERAL_EXCEPTION,
                  "Failed to instantiate: " + registryKey);
        }
    }

    protected void logDebug(String msg)
    {
        if (!DEBUG) return;
        System.out.println(msg);
    }
}

