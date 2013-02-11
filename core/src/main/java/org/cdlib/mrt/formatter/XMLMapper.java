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
    protected static final String NL = System.getProperty("line.separator");
    protected static final String NAME = "XMLMapper";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = false;
    protected StateInf state = null;
    protected Properties prop = null;
    protected String stateS = null;
    protected String globalNameSpace = null;
    protected String nsMapBase = null;
    protected NSMap.NSEntry entry = null;

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
        stateS = state.getClass().getName();
        if (DEBUG) System.out.println(MESSAGE + "stateS=" + stateS);
        prop = getProperties(resourceName);
        if (DEBUG) System.out.append(PropertiesUtil.dumpProperties(MESSAGE, prop));
        nsMapBase = prop.getProperty("nsMapBase");
        if (StringUtil.isEmpty(nsMapBase)) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "NSMapBase property not found");
        }
        String nsMapName = prop.getProperty("nsMapName");
        if (DEBUG) System.out.println(MESSAGE + "nsMapName=" + nsMapName);
        NSMap map = new NSMap(nsMapName, null);
        if ((map == null) || (map.size() == 0)) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "NSMap not found:" + nsMapName);
        }
        if (DEBUG) System.out.println(MESSAGE + "FOUND nsMapName=" + nsMapName);
        entry = map.getEntry(state);
        
        if (entry == null) {
            throw new TException.INVALID_OR_MISSING_PARM (
                    MESSAGE + "NSMap.Entry not found:" 
                    + " - nsMapName=" + nsMapName
                    + " - stateS=" + stateS
                    );
                    
        }
        
        
        if (StringUtil.isAllBlank(entry.ns)) globalNameSpace = null;
        else if (StringUtil.isEmpty(entry.ns)) globalNameSpace = null;
        globalNameSpace=entry.ns;
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
//fixsubmit.header=xmlns:fixsub='http://uc3.cdlib.org/ontology/mrt/fixity/submit'
        
        String header = null;
        String headBase = "='" + nsMapBase + entry.ext + "'";
        String prefix = "xmlns";
        if (StringUtil.isNotEmpty(globalNameSpace)) {
            header = globalNameSpace + ':' + name + " xmlns:" + globalNameSpace + headBase;
        } else {
            header = name + " xmlns" + headBase;
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
        nameSpace = entry.ns;
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
        return entry.id;
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
        return entry.resource;
    }


    public String getNameSpaceURI()
        throws TException
    {
        return nsMapBase + entry.ext + "/";
    }
    
    public String getNameSpacePrefix()
        throws TException
    {
        return entry.ns;
    }

    public String getXHTML()
        throws TException
    {
        String prefix = prop.getProperty("xhtml");
        return prefix;
    }

    public String getSemanticBase()
        throws TException
    {
        String prefix = prop.getProperty("semanticBase");
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
    
    public String dump(String header)
    {
        return 
                "XMLMapper " + header + " dump:"
                + " - stateS=" + stateS
                + " - entry****" + NL
                + entry.dump("ENTRY") + NL
                + PropertiesUtil.dumpProperties("DUMP PROP", prop)
                
                ;
    }

}
