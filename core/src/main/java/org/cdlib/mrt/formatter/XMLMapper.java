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

package org.cdlib.mrt.formatter;


import java.io.InputStream;
import java.util.Properties;

import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.TException;

/**
 * Provide mapping of State to header and namespaces
 * Resource entries for XMLMapper take the form:
 * [typename].header= (required)
 * [typename].namespace= (optional - if not supplied then assumed empty or id provided)
 * [typename].id.[property name]= (optional)
 * @author dloy
 */
public class XMLMapper
{
    protected static final String NAME = "XMLMapper";
    protected static final String MESSAGE = NAME + ": ";
    public enum Type
    {
        access,
        authorize,
        feeder,
        fixentry,
        fixentries,
        fixserv,
        fixselect,
        fixsubmit,
        ingingest,
        ingqueue,
        ingbatch,
        ingjob,
        ingjobs,
        ingprofile,
        inghandler,
        strstore,
        strnode,
        strobject,
        strversion,
        strfile,
        strfixity,
        strprimary,
        exc,
        undef;
    }

    protected Type type;
    protected StateInf state = null;
    protected Properties prop = null;
    protected String globalNameSpace = null;
    protected final String ACCESS = "AccessServiceState";
    protected final String STORAGE = "StorageServiceState";
    protected final String NODE = "NodeState";
    protected final String OBJECT = "ObjectState";
    protected final String VERSION = "VersionState";
    protected final String FEEDER = "FeederServiceState";
    protected final String FILE = "FileState";
    protected final String FIXITY = "FileFixityState";
    protected final String PRIMARY = "PrimaryIDState";
    protected final String EXCEPTION = "TException";
    protected final String INGEST = "IngestServiceState";
    protected final String QUEUE = "QueueState";
    protected final String BATCH = "BatchState";
    protected final String JOB = "JobState";
    protected final String JOBS = "JobsState";
    protected final String PROFILE = "ProfileState";
    protected final String HANDLER = "HandlerState";
    protected final String AUTHORIZE = "AuthorizeState";
    protected final String QUEUE_ENTRY_STATE = "QueueEntryState";
    protected final String FIXITY_ENTRY = "FixityEntry";
    protected final String FIXITY_ENTRIES = "FixityEntriesState";
    protected final String FIXITY_SELECT = "FixitySelectState";
    protected final String FIXITY_SERVICE = "FixityServiceState";
    protected final String FIXITY_SUBMIT = "FixitySubmittedState";

    public static XMLMapper getXMLMapper(String resourceName, StateInf state)
        throws TException
    {
        return new XMLMapper(resourceName, state);
    }

    /**
     * State must be explicitly included match an instance to assign a type name
     * The type name is used for matchin resource properties
     * @param state implements StateInf
     * @throws TException
     */
    protected XMLMapper(String resourceName, StateInf state)
        throws TException
    {
        if (StringUtil.isEmpty(resourceName)) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "construct - missing resourceName");
        }
        if (state == null) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "construct - missing state");
        }
        this.state = state;
        String stateS = state.getClass().getName();
        //System.out.println(MESSAGE + "stateS=" + stateS);
        if (stateS.contains(ACCESS)) type = Type.access;
        else if (stateS.contains(STORAGE)) type = Type.strstore;
        else if (stateS.contains(NODE)) type = Type.strnode;
        else if (stateS.contains(OBJECT)) type = Type.strobject;
        else if (stateS.contains(VERSION)) type = Type.strversion;
        else if (stateS.contains(FILE)) type = Type.strfile;
        else if (stateS.contains(FIXITY)) type = Type.strfixity;
        else if (stateS.contains(PRIMARY)) type = Type.strprimary;
        else if (stateS.contains(EXCEPTION)) type = Type.exc;
        else if (stateS.contains(FIXITY_ENTRY)) type = Type.fixentry;
        else if (stateS.contains(FIXITY_ENTRIES)) type = Type.fixentries;
        else if (stateS.contains(FIXITY_SELECT)) type = Type.fixselect;
        else if (stateS.contains(FIXITY_SERVICE)) type = Type.fixserv;
        else if (stateS.contains(FIXITY_SUBMIT)) type = Type.fixsubmit;
        else if (stateS.contains(INGEST)) type = Type.ingingest;
        else if (stateS.contains(QUEUE)) type = Type.ingqueue;
        else if (stateS.contains(BATCH)) type = Type.ingbatch;
        else if (stateS.contains(JOB)) type = Type.ingjob;
        else if (stateS.contains(JOBS)) type = Type.ingjobs;
        else if (stateS.contains(PROFILE)) type = Type.ingprofile;
        else if (stateS.contains(HANDLER)) type = Type.inghandler;
        else if (stateS.contains(AUTHORIZE)) type = Type.authorize;
        else if (stateS.contains(QUEUE_ENTRY_STATE)) type = Type.ingqueue;
        else if (stateS.contains(FEEDER)) type = Type.feeder;
        else type = Type.undef;
        prop = getProperties(resourceName);
        this.state = state;
        globalNameSpace = getProperty("namespace");
    }

    /**
     * match property value of form [typename].
     * @param key match key of specific typename
     * @return match property - may be null
     * @throws TException
     */
    public String getProperty(String key)
        throws TException
    {
        if (StringUtil.isEmpty(key)) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "getProperty key missing");
        }
        String typeS = type.toString();
        String value = prop.getProperty(typeS + "." + key);
        if (StringUtil.isEmpty(value)) {
            value = prop.getProperty(key);
        }
        return value;
    }

    /**
     * return a header for the XML - this property is required
     * @return XML header
     * @throws TException XML not found
     */
    public String getHeader(String name)
        throws TException
    {
        if (StringUtil.isEmpty(name)) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "getProperty key missing");
        }

        String header = getProperty("header");

        if (StringUtil.isEmpty(header)) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "getHeader - required but not found:"
                    + type.toString() + "." + header
                    + " - name=" + name
                    );
        }
        if (StringUtil.isEmpty(globalNameSpace)) {
            header = name + " " + header;
        } else {
            header = globalNameSpace + ":" + name + " " + header;
        }

        return header;
    }

    /**
     * Get this name with namespace if supplied.
     * If individual namespace is supplied for name then use it
     * else use globalname space
     * else not supplied
     * @param name identifier name
     * @return name with extracted namespace
     * @throws TException
     */
    public String getName(String name)
        throws TException
    {
        String nameSpace = globalNameSpace;
        if (StringUtil.isEmpty(name)) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "getProperty key missing");
        }
        nameSpace = getProperty("name." + name);
        if (StringUtil.isEmpty(nameSpace)) {
            nameSpace = globalNameSpace;
        }
        if (StringUtil.isEmpty(nameSpace)) {
            nameSpace = "";
        } else {
            nameSpace = nameSpace + ":";
        }
        return nameSpace + name;
    }

    /**
     * Get name of ID used for this namespace
     * @param name identifier name
     * @return name with extracted namespace
     * @throws TException
     */
    public String getIDName()
        throws TException
    {
        String idName = getProperty("id");
        return idName;
    }

    /**
     * Get name of ID used for this namespace
     * @param name identifier name
     * @return name with extracted namespace
     * @throws TException
     */
    public String getResourceName()
        throws TException
    {
        String idName = getProperty("resource");
        return idName;
    }


    public String getNameSpaceURI()
        throws TException
    {
        String idName = getProperty("nsuri");
        return idName;
    }

    public String getNameSpacePrefix()
        throws TException
    {
        String prefix = getProperty("nsprefix");
        return prefix;
    }

    public String getXHTML()
        throws TException
    {
        String prefix = getProperty("xhtml");
        return prefix;
    }

    public String getSemanticBase()
        throws TException
    {
        String prefix = getProperty("semanticBase");
        return prefix;
    }

    /**
     * Gets a resource using the class loader
     *
     * @param resourceName Name of the file containing the resource. The name
     * may include a relative path which is interpreted as being relative to
     * the path "resources/" below the classpath root.
     * @return An inputstream for the resource
     */
    public Properties getProperties(String resourceName)
        throws TException
    {
        Properties prop = new Properties();
        InputStream inputStream =  null;
        try {
            inputStream =  getClass().getClassLoader().
                getResourceAsStream(resourceName);
            prop.load(inputStream);
            return prop;

        } catch(Exception e) {
               System.out.println(
                "MFrame: Failed to get the AdminManager for entity: " +
                "Failed to get resource: " +
                resourceName +
                " Exception: " + e);
           throw new TException.GENERAL_EXCEPTION(
                "MFrame: Failed to get the AdminManager for entity: " +
                "Failed to get resource: " +
                resourceName +
                " Exception: " + e);
        } finally {
            try{
                inputStream.close();
            } catch (Exception ex) { }
        }
    }

    /**
     * return enum XMLMapper enum type
     * @return XMLMapper enum type
     */
    public Type getType()
    {
        return type;
    }

}
