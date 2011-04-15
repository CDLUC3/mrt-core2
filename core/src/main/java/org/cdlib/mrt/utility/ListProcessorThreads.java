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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;


/**
 * Provides a tool for processing large lists of items for Enrichment
 * Thread based processing
 * @author  David Loy
 */
public class ListProcessorThreads
        extends ListProcessorBase
        implements ListProcessor
{
    protected String NAME = "ListProcessorThreads";
    protected String MESSAGE = NAME + ": ";
    
    // client properties
    protected Properties m_clientProperties = null;
    
    // offset for beginning of list processing
    protected int m_startList = -1;
    
    // offset for end of list processing
    protected int m_lastList = -1;
    
    // maximum errors allowed while processing list
    protected int m_errMaxAllowed = -1;
    
    // current read count
    protected int m_inCnt = -1;
    
    // current error count
    protected int m_errCnt = 0;
    
    // list buffer
    protected BufferedReader m_list = null;
    
    // number of threads
    protected int m_threadCnt = 1;
    
    // Vector of current threads
    private Vector m_threadList = null;
    
    public ListProcessorThreads(TFrame TFrame)
    {
       super(TFrame);
       initialize(TFrame);
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
        throw new TException.UNIMPLEMENTED_CODE(
                "Extension required for ListProcessorThreads to support process");
    }
    
    /**
     * Name of EnrichmentList
     * @return name to be applied for identifying process properties
     */
    public String getName(){
        throw new TRuntimeException.UNIMPLEMENTED_CODE(
                "Extension required for EnrichmentListSimple to support getName");
    }
    
    private void initialize (TFrame TFrame)
    {
        try {
            System.out.println("initialize EnrichmentListSimple");
            String propertiesKey = "ListProcessor." + getName() + ".properties";
            m_clientProperties = getFileProperties(propertiesKey);
            String processList = m_clientProperties.getProperty("processList");
            setList();
            m_startList = getIntProp("start", 0);
            m_lastList = getIntProp("last", 100000000);
            validateListProps();
            m_errMaxAllowed = getIntProp("errMaxAllowed", 10);
            m_threadCnt = getIntProp("threadCnt", 1);
            log("m_startList=" + m_startList, 10, 10);
            log("m_lastList=" + m_lastList, 10, 10);
            log("processThreadCnt=" + m_threadCnt, 10, 10);
            m_threadList = new Vector(m_threadCnt);
            
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex);
        }        
    }

    protected Properties getClientProperties()
    {
        return m_clientProperties;
    }

    /**
     * Extract the value of file to a string (utf-8)
     * @param extractFile file to be extracted
     * @return string form of file content
     * @throws org.cdlib.mrt.utility.MException
     */
    public static String file2String(File extractFile, String encoding)
            throws TException
    {
        try {
            if ((extractFile == null) || !extractFile.exists()) {
                String err = "file2String - bad argument";
                throw new TException.INVALID_OR_MISSING_PARM(err);
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte [] buf = new byte [100000];
            FileInputStream inputStream = new FileInputStream(extractFile);
            while (true) {
                int len = inputStream.read(buf);
                if (len < 0) break;
                byteStream.write(buf, 0, len);
            }
            inputStream.close();
            byteStream.close();
            byte [] bytes = byteStream.toByteArray();
            String retValue = new String(bytes, encoding);
            return retValue;

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = "Could not create String - Exception:" + ex + " - name:" + extractFile.getName();
            throw new TException.GENERAL_EXCEPTION(err);
        }
    }

    /**
     * Call back method for handling end of process
     */
    protected void processCompletion()
    {
        // end of process handling
    }

    private void validateListProps()
    { 
          if(m_lastList < m_startList) {
              String msg = "List prop error: start > last"
                      + " - m_startList=" + m_startList
                      + " - m_lastList=" + m_lastList;
              log(msg, 0, 0);
              throw new TRuntimeException.INVALID_OR_MISSING_PARM(MESSAGE + msg);
          }
    }
    
    protected void setList()
    {
        try {
            String processList = m_clientProperties.getProperty("processList");
            if (StringUtil.isEmpty(processList)) {
                throw new TRuntimeException.INVALID_OR_MISSING_PARM(
                        MESSAGE + "setList - missing processList property");
            }
            log("Process list" + processList, 5, 5);
            File list = new File(processList);
                buildReaderFile(list);
            
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex.toString());
        }        
    }
    
    protected void buildReaderFile(File list)
    {
        try {
            FileInputStream fstream = new FileInputStream(list);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            m_list = new BufferedReader(new InputStreamReader(in));
            
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex.toString());
        }        
    }
    
    protected void run()
    {
        try {
            startThreads();
            
        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex.toString());
        }        
    }
    
    /**
     * Start threads to a maximum of processThreadCnt
     * Check to see if any active threads and adjust count based on how
     * many are still active.
     */
    public void startThreads()
    {
        log("startThreads threadCnt=" + m_threadCnt, 10, 10);
        try {
            int activeCnt = activeThreads();
            
            for (int i=activeCnt; i < m_threadCnt; i++) {
                // the processFeeder is a thread routine to 
                // cycle the process level requests to the feeder
                ProcessQueue processFeeder = new ProcessQueue();
                Thread thread = new Thread(processFeeder);
                thread.start();
                m_threadList.add(thread);
                log("starting Thread:" + thread.getName(), 10, 10);
                
            }
            
            
        }  catch(Exception e) {
            logger.logError(
                MESSAGE + "Failed to initialize. Exception: " +
                e,
                0);
            throw new TRuntimeException.GENERAL_EXCEPTION(
                    MESSAGE + "Failed to initialize. Exception: " + e);
        }
    }
    
    /**
     * stop all active threads (maybe)
     */
    public void destroy(){
        ThreadGroup Z = Thread.currentThread().getThreadGroup();
        Z.interrupt();
    }
    
 
    /**
     * Stick a stop thread object into queue for all active threads
     * Thread dies as each is picked up.
     */
    public void stopThreads()
    {
        closeList();
    }
 
    /**
     * Issua a join on threads 
     * - interrupt if thread remains active
     * - remove thread from cache
     * @param interval milliseconds before interupt
     */
    public void joinStopThreads(int interval)
    {
        Thread thread = null;
        if (m_threadList == null) return;
        int interuptCnt = 0;
        for (int i=m_threadList.size() - 1; i >= 0 ; i--) {
            thread = (Thread)m_threadList.get(i);
            if (thread.isAlive()) {
                try {
                    thread.join(interval);
                } catch (Exception ex) {
                    logger.logError(MESSAGE + "warning - thread join exception:" + ex, 10);
                }
                if (thread.isAlive()) {
                    thread.interrupt();
                    interuptCnt++;
                }
            }
            log("joinStopThreads - Close(" + i +  "):" + thread.getName(), 5, 5);
            m_threadList.remove(i);
            logger.logMessage(MESSAGE + "joinStopThreads(" + interuptCnt + "):" + thread.getName(), 5);
        }
        return;
    }
    
    /**
     * is stop been set
     */
    public boolean isStop()
    {
        if (m_list == null) return true;
        return false;
    }

    /**
     * stop active threads
     */
    public void stop()
    {
        stopThreads();
    }

    /**
     * return a count of active threads
     * Also, remove inactive threads from m_threadList container
     */
    public int activeThreads()
    {
        Thread thread = null;
        if (m_threadList == null) return 0;
        int active = 0;
        for (int i=m_threadList.size() - 1; i >= 0 ; i--) {
            thread = (Thread)m_threadList.get(i);
            if (thread.isAlive()) {
                active++;
            } else {
                m_threadList.remove(i);
                logger.logMessage(MESSAGE + "remove Thread:" + thread.getName(), 1                                      );
            }
        }
        return active;
    }
        
    /**
     * Find list to process and sequentially process the list
     * Note properties:
     * start - beginning offset of list to process
     * last - last offset of list to process
     *
     */
    protected void processList()
        throws TException
    {
        try {
            
            String line = null;
            for (int i=0; true; i++ ) {
                line = read();
                if (line == null) break;
                try {
                    log("m_errCnt:" + m_errCnt, 15, 15);
                    long startTime = m_status.getTime();
                    process(line, m_clientProperties);
                    m_status.addDiffTime(PROCESSTIME, startTime);
                    m_status.bump(PROCESSCNT);
                    
                } catch (Exception ex) {
                    logger.logError(MESSAGE + "ProcessList(" + i + "):"
                            + " - line=" + line
                            + " - Exception:" + ex
                            , 0);
                    log("process Exception trace:" + StringUtil.stackTrace(ex),5,5);
                    m_status.bump("process-exception");
                    m_status.bump("exception:" + line);
                    m_errCnt++;
                }
            }
            
        } catch (Exception ex) {
            logger.logError(MESSAGE + "ProcessList - Exception:" + ex, 0);
            log("trace:" + StringUtil.stackTrace(ex), 0, 0);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "ProcessList - Exception:" + ex);
            
        }   
    }
    
    private synchronized void closeList()
    {
        log("CLOSELIST ENTERED", 10, 10);
        if (m_list == null) return;
        try {
            m_list.close();
        } catch (Exception ex) { }
        m_list = null;
        complete();
    }
        
    /**
     * Find list to process and sequentially process the list
     * Note properties:
     * start - beginning offset of list to process
     * last - last offset of list to process
     *
     */
    protected synchronized String read()
        throws TException
    {
        try {
            if (m_list == null) return null;
            if (m_errCnt >= m_errMaxAllowed) {
                closeList();
                return null;
            }
            String line = null;
            while(true) {
                line = m_list.readLine();
                if (line == null) {
                    closeList();
                    return null;
                }
                m_inCnt++;
                m_status.bump("read");
                if (m_inCnt < m_startList) {
                    m_status.bump("skip");
                    continue;
                }
                if (m_inCnt > m_lastList) {
                    closeList();
                    return null;
                }
                log("ProcessList[" + Thread.currentThread().getName() + "](" + m_inCnt + "):" + line, 5, 5);                
                m_status.bump("process");
                return line;
            }
            
        } catch (Exception ex) {
            logger.logError(MESSAGE + "ProcessList - read Exception:" + ex, 0);
            log("trace:" + StringUtil.stackTrace(ex), 0, 0);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "ProcessList - Exception:" + ex);
            
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
    
    protected void complete()
    {
        try {
            
            log("COMPLETE ENTERED", 10, 10);
            CloseQueue processFeeder = new CloseQueue();
            Thread thread = new Thread(processFeeder);
            thread.start();
        } catch (Exception ex) {
            log("Complete Exception:" + ex, 5, 5);
        }
    }
    
    /**
     * ProcessQue handles all process off of the database work queue
     */
    protected class CloseQueue implements Runnable
    {
        /**
         * process queue
         */
        public void run() 
        {
            log("CloseQueue ENTERED", 10, 10);
            try {
                joinStopThreads(60000);

            } catch (Exception ex) {
                logger.logError(MESSAGE + "CloseQueue - Exception:" + ex, 0);
                log("trace:" + StringUtil.stackTrace(ex), 0, 0);
            } finally {
                dumpStatus();
                processCompletion();
            }
                
        }

        protected void dumpStatus()
        {
            logger.logMessage(m_status.dump(), 0);
            Double bytesPerMil = m_status.divide(TOTALBYTES, TOTALTIME);
            if (bytesPerMil != null) {
                log("BytesPerSec:" + (bytesPerMil* 1000.0),0,0);
            }
        }
            
    }
    /**
     * ProcessQue handles all process off of the database work queue
     */
    protected class ProcessQueue implements Runnable
    {
        /**
         * process queue
         */
        public void run() {
            try {
                processList();

            } catch (TException ex) {
                logger.logError(MESSAGE + "ProcessList - read Exception:" + ex, 0);
                log("trace:" + StringUtil.stackTrace(ex), 0, 0);
                throw new TRuntimeException.GENERAL_EXCEPTION(
                        ex.toString());
            }
                
        }
            
    }
}
