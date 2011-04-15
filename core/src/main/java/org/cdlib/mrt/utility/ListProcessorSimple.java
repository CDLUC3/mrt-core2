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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TRuntimeException;
import org.cdlib.mrt.utility.StringUtil;
/**
 * Provides a tool for processing large lists of items for Enrichment
 * @author  David Loy
 */
public class ListProcessorSimple
        extends ListProcessorBase
        implements ListProcessor
{
    protected String NAME = "ListProcessorSimple";
    protected String MESSAGE = NAME + ": ";
    
    // client properties
    protected Properties m_clientProperties = null;
    
    public ListProcessorSimple(TFrame framework)
        throws TException
    {
       super(framework);
       initialize(framework); 
    }
    
    /**
     * Process on EnrichmentList item
     * @param item item from list to be processed
     * @param processProp properties to be used for list processing
     * @param status counts
     */
    public void process(
            String item,
            Properties processProp)
	throws TException
    {
        throw new TRuntimeException.UNIMPLEMENTED_CODE(
                "Extension required for EnrichmentListSimple to support process");
    }

    /**
     * Process on EnrichmentList item
     * @param item item from list to be processed
     * @param processProp properties to be used for list processing
     * @param status counts
     */
    public void end()
	throws TException
    {
        throw new TRuntimeException.UNIMPLEMENTED_CODE(
                "Extension required for EnrichmentListSimple to support process");
    }
    
    /**
     * Name of EnrichmentList
     * @return name to be applied for identifying process properties
     */
    public String getName(){
        throw new TRuntimeException.UNIMPLEMENTED_CODE(
                "Extension required for EnrichmentListSimple to support getName");
    }
    
    protected void initialize (TFrame framework)
        throws TException
    {
        System.out.println("CALL ListProcessorSimple");
        try {
            System.out.println("initialize EnrichmentListSimple");
            String propertiesKey = "EnrichmentList." + getName() + ".properties";
            m_clientProperties = getFileProperties(propertiesKey);
        
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION( ex.toString());
        }        
    }


    protected Properties getClientProperties()
    {
        return m_clientProperties;
    }

    /**
     * Find list to process and sequentially process the list
     * Note properties:
     * start - beginning offset of list to process
     * last - last offset of list to process
     *
     */
    public void processList()
        throws TException
    {
        System.out.println(MESSAGE + "listProcessorSimple entered");
        BufferedReader br = null;
        try {
            String processList = m_clientProperties.getProperty("processList");
            if (StringUtil.isEmpty(processList)) {
                throw new TException.INVALID_OR_MISSING_PARM(
                        MESSAGE + "processList missing");
            }
            int start = getIntProp("start", 0);
            int last = getIntProp("last", 100000000);
            int errMaxAllowed = getIntProp("errMaxAllowed", 10);
            int inCnt = 0;
            log("Process list" + processList, 5, 5);
            FileInputStream fstream = new FileInputStream(processList);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            int errCnt = 0;
            for (int i=0; true; i++ ) {
                line = br.readLine();
                if (line == null) break;
                if (i < start) continue;
                if (i >= last) break;
                try {
                    process(line, m_clientProperties);
                    
                } catch (Exception ex) {
                    logger.logError(MESSAGE + "ProcessList(" + i + "):"
                            + " - line=" + line
                            + " - Exception:" + ex
                            , 0);
                    log("process Exception trace:" + StringUtil.stackTrace(ex),5,5);
                    errCnt++;
                    if (errCnt > errMaxAllowed) break;
                }
            }
            if (errCnt > errMaxAllowed) {
                throw new TException.GENERAL_EXCEPTION(
                        MESSAGE + "processList - Error Count exceeded: errors=" + errCnt);
            }
            end();

        } catch (Exception ex) {
            logger.logError(MESSAGE + "ProcessList - Exception:" + ex, 0);
            log("trace:" + StringUtil.stackTrace(ex), 0, 0);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "ProcessList - Exception:" + ex);
            
        } finally {
            try {
                br.close();
            } catch (Exception ex) { }
            logger.logMessage(m_status.dump(), 0, true);
        }       
    }
    
    
    /**
     * Return int value based on m_clientProperties Properties
     * @param key key to search
     * @param defaultValue default if key is not found in m_clientProperties
     * @return converted integer value
     */
    protected int getIntProp(String key, int defaultValue)
    {
        if (StringUtil.isEmpty(key)) return defaultValue;
        String value = m_clientProperties.getProperty(key);
        if (StringUtil.isEmpty(value)) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            throw new TRuntimeException.INVALID_DATA_FORMAT(
                    MESSAGE + "getIntProp fails: key=" + key + " - value=" + value);
        }
    }
}
