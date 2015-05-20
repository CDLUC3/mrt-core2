/******************************************************************************
Copyright (c) 2005-2012, Regents of the University of California
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
*******************************************************************************/
package org.cdlib.mrt.core;

import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.core.ProcessStatus;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;

/**
 * Run fixity
 * @author dloy
 */
public class ThreadHandler
{

    protected static final String NAME = "ThreadHandler";
    protected static final String MESSAGE = NAME + ": ";
    protected static boolean debug = false;
    protected static final boolean STATUS = true;
    
    protected Thread[] threads = null;
    protected int threadCnt = 5;
    protected long pollTime = 500;
    protected Exception saveException = null;
    protected boolean shutdown = false;
    protected LoggerInf logger = null;
 
    
    public static ThreadHandler getThreadHandler(
            long pollTime,
            int threadCnt,
            LoggerInf logger)
        throws TException
    {
        return new ThreadHandler(pollTime, threadCnt, logger);
    }
    
    protected ThreadHandler(
            long pollTime,
            int threadCnt,
            LoggerInf logger)
        throws TException
    {
        if (threadCnt < 1) threadCnt = 1;
        this.threadCnt = threadCnt;
        this.pollTime = pollTime;
        this.logger = logger;
        System.out.println(MESSAGE + "***threadCnt=" + threadCnt);
        initialThreads();
        
    }
 
    public void shutdown()
        throws TException
    {
        shutdown = true;
        try {
            if (threadCnt > 1) completeThreads();
        } catch (Exception ex) {
            System.out.println(MESSAGE + "WARNING shutdown:" + ex);
        }
    }
    
    public ProcessStatus runThread(Runnable  processItem)
    {
        dowhile:
        while (true) {
            if (shutdown) {
                return ProcessStatus.shutdown;
            }
            for (int i=0; i < threadCnt; i++) {
                Thread thread = threads[i];
                if (thread == null) {
                    threads[i] = new Thread(processItem);
                    if (debug) System.out.println("***Start[" + i + "]:" + DateUtil.getCurrentIsoDate());
                    
                    threads[i].start();
                    return ProcessStatus.queued;
                }
            }
            for (int i=0; i < threadCnt; i++) {
                Thread thread = threads[i];
                if (thread != null) {
                    if (!thread.isAlive()) {
                        threads[i] = null;
                        continue dowhile;
                    }
                }
            }
            try {
                Thread.sleep(pollTime);
            } catch (Exception ex) { }
        }
    }
    
    protected void completeThreads()
    {
        while_lab:
        while (true) {
            boolean done = true;
            for (int i=0; i < threadCnt; i++) {
                Thread thread = threads[i];
                if (thread == null) {
                    //System.out.println("Thread[" + i + "] null");
                    continue;
                }
                if (thread != null) {
                    if (!thread.isAlive()) {
                        if (debug) System.out.println("Complete:" + thread.getName());
                        threads[i] = null;
                        if (debug) System.out.println("Thread[" + i + "] died");
                        continue;
                    }
                }
                done = false;
                try {
                    for (int itest=0; itest < 60; itest++) {
                        Thread.sleep(1000);
                        if (!thread.isAlive()) {
                            if (debug) System.out.println("Complete:" + thread.getName());
                            threads[i] = null;
                            if (debug) System.out.println("Thread[" + i + "] died:" + itest);
                            continue while_lab;
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("WARNING exception completeThreads:" + ex);
                }
            }
            if (done) break;;
        }
        System.out.println("completeThreads - ended");
    }
    
    public int getActiveCnt()
    {
        int activeCnt = 0;
        for (int i=0; i < threadCnt; i++) {
            Thread thread = threads[i];
            if ((thread != null) && thread.isAlive()) {
                activeCnt++;
            }
        }
        return activeCnt;
    }
    
    protected void initialThreads()
    {
        threads = new Thread[threadCnt];
        for (int i=0; i < threadCnt; i++) {
            threads[i] = null;
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        ThreadHandler.debug = debug;
    }

    public int getThreadCnt() {
        return threadCnt;
    }
    
}

