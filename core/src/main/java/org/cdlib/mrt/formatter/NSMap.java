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



import org.cdlib.mrt.utility.StateInf;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Vector;

import org.cdlib.mrt.utility.LoggerAbs;;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Create a Name Space Map for formatting
 * @author dloy
 */
public class NSMap
{
    private static final String NAME = "NSMap";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    private static final String PREFIX = "producer/";

    protected String resourceName = null;
    protected BufferedReader resourceBR = null;
    protected Vector<NSEntry> mapList = new Vector<NSEntry>();
    protected LoggerInf logger = null;

    public NSMap(
            String resourceName,
            LoggerInf logger)
        throws TException
    {
        this.resourceName = resourceName;
        this.logger = logger;
        validate();
        if (DEBUG) System.out.println("****MapList size=" + mapList.size());
    }
    
    protected void validate()
        throws TException
    {
        try {
            if (logger == null) {
                logger = LoggerAbs.getTFileLogger("testFormatter", 0, 0);
            }
            if (StringUtil.isEmpty(resourceName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "resourceName does not exist");
            }
            buildReaderFile();
            buildResourceTable();
            if (size() == 0) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "mapList is empty");
            }

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
        }
    }

    protected void buildResourceTable()
        throws TException
    {
        try {
            while(true) {
                String line = resourceBR.readLine();
                if (line == null) break;
                addLine(line);
                if (DEBUG) System.out.println("add line:" + line);

            }

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            
        } finally {
            try {
                resourceBR.close();
            } catch (Exception ex){} ;
        }
    }

    protected void addLine(String line)
        throws TException
    {
        try {
            if (StringUtil.isEmpty(line)) return;
            if (line.startsWith("#")) return;
            String [] parts = line.split("\\s*\\|\\s*");
            if (DEBUG) {
                log("addLine:" + line + " - parts.length=" + parts.length);
                for (int i=0; i<parts.length; i++) {
                    log("part[" + i + "]:" + parts[i]);
                }
            }
            if (parts.length < 4) return;
            NSEntry entry = new NSEntry();
            entry.name = norm(parts[0]);
            entry.className = norm(parts[1]);
            entry.ns = norm(parts[2]);
            entry.ext = norm(parts[3]);
            if (parts.length > 4) entry.id = norm(parts[4]);
            if (parts.length > 5) entry.resource = norm(parts[5]);
            
            mapList.add(entry);
            if (DEBUG) System.out.println(entry.dump("***ENTRY***")); //!!!!

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }
    }
    
    protected String norm(String in)
    {
        if (in == null) return null;
        in = in.trim();
        if (StringUtil.isEmpty(in)) return null;
        if (StringUtil.isAllBlank(in)) return null;
        return in;
    }
    

    protected void log(String msg)
    {
        if (!DEBUG) return;
        System.out.println(MESSAGE + msg);
    }

    public NSEntry getEntry(StateInf state)
    {
        if (state == null) return null;
        
        String stateS = state.getClass().getName();
        if (DEBUG) System.out.println(MESSAGE + "stateS=" + stateS);
        return getEntry(stateS);
        
    }

    public NSEntry getEntry(String stateName)
    {
        if (stateName == null) return null;
        for (NSEntry entry : mapList) {
            if (DEBUG) System.out.println("getEntry:"
                + " - stateName=" + stateName
                + " - entry.className=" + entry.className
                );
            if (stateName.contains(entry.className)) {
                return entry;
            }
        }
        return null;
        
    }


    protected void buildReaderFile()
        throws TException
    {
        try {
            InputStream inStream = getClass().getClassLoader().
                getResourceAsStream(resourceName);
            if ((inStream == null)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "Manifest resource not found:" + resourceName);
            }
            resourceBR = new BufferedReader(new InputStreamReader(inStream));

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex.toString());
        }
    }
    public Vector<NSEntry> getMapList() {
        return mapList;
    }
    
    
    public int size() {
        return mapList.size();
    }
    
    public NSEntry get(int i)
    {
        if ((i+1) > mapList.size()) return null;
        if (i < 0) return null;
        return mapList.get(i);
    }

    public String getResourceName() {
        return resourceName;
    }
    
    
    public static class NSEntry {
        public String name = null;
        public String className = null;
        public String ns = null;
        public String ext = null;
        public String id = null;
        public String resource = null;
        public String dump(String header) {
            StringBuffer buf = new StringBuffer();
            buf.append("NSEntry:" + header);
            String msg = ""
                    + " - name=" + name
                    + " - className=" + className
                    + " - ns=" + ns
                    + " - ext=" + ext
                    + " - id=" + id
                    + " - resource=" + resource
                    ;
            buf.append(msg);
            return buf.toString();
            
        }
    }
}
