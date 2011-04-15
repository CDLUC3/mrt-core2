/*
Copyright (c) 2005-2009, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.cdlib.mrt.utility.TallyTable;
import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TRuntimeException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
/**
 * Provides a tool for processing large lists of items for Enrichment
 * @author  David Loy
 */
public abstract class ListProcessorBase
{
    protected String NAME = "ListProcessorBase";
    protected String MESSAGE = NAME + ": ";
    public final String TOTALBYTES = "totalBytes";
    public final String TOTALTIME = "totalTime";
    public final String PROCESSTIME = "processTime";
    public final String PROCESSCNT = "processCnt";
    
    // base Framework control class
    protected TFrame m_framework = null;
    
    // base logging
    protected LoggerInf logger = null;
    
    // status count
    protected TallyTable m_status = null;
    
    protected ListProcessorBase(TFrame framework)
    {
        initialize(framework);
    }
    
    private void initialize (TFrame framework)
    {       
        try {
            System.out.println("initialize EnrichmentListBase");
            m_framework = framework;
            logger = framework.getLogger();
            m_status = new TallyTable();
        
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex.toString());
        }        
    }
    
    
    /**
     * Do both framework logging and System
     * @param msg message to be displayed
     * @param loglvl verbose level log output
     * @param syslvl verbose level System.out output
     */
    protected void log(String msg, int loglvl, int syslvl)
    {
        if (m_framework.getLogger().getMessageMaxLevel() >= loglvl) {
            logger.logMessage(MESSAGE + msg, loglvl, true);
        }
        if (logger.getMessageMaxLevel() >= syslvl) {
            System.out.println(msg);
        }
    }
    
    /**
     * Get properties from using a file name to load
     * @param nameKey key name of file to be extracted for creating properties
     */
    protected Properties getFileProperties(String nameKey)
        throws Exception
    {
        String propFile = m_framework.getProperty(nameKey);
        if (propFile == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                  MESSAGE + "Exception missing " + nameKey);
        }
        log("propFile=" + propFile, 0, 5);
        InputStream inputStream = new FileInputStream(propFile);
        Properties prop = new Properties();
        prop.load(inputStream);
        inputStream.close();
        log(PropertiesUtil.dumpProperties(nameKey,prop), 0, 5);
        if (prop.size() == 0) {
            throw new TException.INVALID_OR_MISSING_PARM(
                  MESSAGE + "Empty prop file:" + nameKey);
        }
        log(PropertiesUtil.dumpProperties(nameKey,prop), 0, 5);
        return prop;
    }
    
    /**
     * copy property from one Properties to another and throw exception if not found
     * @param inProp from Properties
     * @param outProp to Properties
     * @param key key of property to be moved
     */
    protected void requiredProp(Properties inProp, Properties outProp, String key)
        throws TException
    {
        if ((inProp == null) || (outProp == null) || StringUtil.isEmpty(key)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "requiredProp - missing required parm");
        }
        String value = inProp.getProperty(key);
        if (StringUtil.isEmpty(value)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "requiredProp - missing required property:" + key);
        }
        outProp.setProperty(key, value);
    }
}
