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

package org.cdlib.mrt.core;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.TallyTable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Return general runtime information about this servlet
 * @author dloy
 */
public class PingState
        implements Serializable, StateInf
{
    private DateState dateTime = new DateState();
    private String serviceName = null;
    private long freeMemory = 0;
    private long totalMemory = 0;
    private long maxMemory = 0;
    private long threadCnt = 0;
    private long peakThreadCnt = 0;
    public TallyTable tally = new TallyTable();
    public Properties prop = new Properties();
    public boolean doGC = false;
    
    /**
     * Initialize Ping state
     * @param doGC true=garbage collect; false=no garbage collect
     */
    public PingState(String serviceName, boolean doGC)
        throws TException
    {
        if (serviceName.isEmpty()) {
            throw new TException.INVALID_OR_MISSING_PARM("PingState: missing service name");
        }
        this.serviceName = serviceName;
        set(doGC);
    }
    
    public PingState(String serviceName)
        throws TException
    {
        this(serviceName, false);
    }
    
    /**
     * Set system values in ping response
     * @param doGC true=garbage collect; false=no garbage collect
     */
    public void set(boolean doGC)
        throws TException
    {
        if (doGC) System.gc();
        freeMemory = Runtime.getRuntime().freeMemory();
        totalMemory = Runtime.getRuntime().totalMemory();
        maxMemory = Runtime.getRuntime().maxMemory();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        threadCnt = threads.getThreadCount();
        peakThreadCnt = threads.getPeakThreadCount();
    }
    
    public void bumpCmd(String key, long startTime)
    {
        tally.bump(key + ".cnt");
        long endTime = DateUtil.getEpochUTCDate();
        tally.bump(key + ".time", endTime - startTime);
    }
    
    /**
     * Get locally set statistic values in Tally
     * @return statistic values
     */
    public Map<String, Long> getLocalStatistics()
    {
        if (tally.size() == 0) return null;
        return tally.getTallyMap();
    }
    
    /**
     * Get locally set property values
     * @return local property values
     */
    public Map<String, String> getLocalProperties()
    {
        if (prop.size() == 0) return null;
        Hashtable<String, String> table = (Hashtable)prop;
        return table;
    }
    
    public long getFreeMemory() {
        return freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getThreadCnt() {
        return threadCnt;
    }

    public long getPeakThreadCnt() {
        return peakThreadCnt;
    }

    public String getDateTime() {
        return dateTime.getIsoDate();
    }

    public String getServiceName() {
        return serviceName;
    }
    
}
