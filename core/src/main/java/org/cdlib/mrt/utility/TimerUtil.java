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
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Format:
 * | ***TIMEUTIL*** | <thread name> | <START | STOP > | <user key> | <time in milliseconds>
 * @author  David Loy
 */
public class TimerUtil
        implements Callable, Runnable
{
    private static final String NAME = "TimerUtil";
    private static final String MESSAGE = NAME + ": ";
    private static final String BASEKEY = "***TIMERUTIL***";
    private static final int LOGSTAT = 20;
    private static final String DELIM = " | ";
    private static final String START = "START";
    private static final String END = "END";
    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;

    protected File inLogFile = null;
    protected BufferedReader inLogBR = null;

    // status count
    protected Hashtable<String, Long> accumTimer = new Hashtable<String, Long>(100);
    protected Hashtable<String, Long> startEventt = new Hashtable<String, Long>(100);
    protected Hashtable<String, Long> accumCounter = new Hashtable<String, Long>(100);

    protected String processStatus = null;
    protected Exception runException = null;

    public TimerUtil(File inLogFile)
        throws TException
    {
        try {
            if (DEBUG) System.out.println("initialize TimerUtil");
            this.inLogFile = inLogFile;
            buildReaderFile();

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public void process()
        throws TException
    {
        try {
            while(true) {
                String line = inLogBR.readLine();
                if (line == null) break;
                addLine(line);
                if (DEBUG) System.out.println("add line:" + line);

            }
            processStatus = getStatus("process", true);
            if (DEBUG) System.out.println("out=" + processStatus);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public void run()
    {
        try {
            process();

        } catch (TException tex) {
            runException = tex;
        }
    }

    public String call()
    {
        try {
            process();
            return processStatus;

        } catch (TException tex) {
            runException = tex;
            return tex.toString();
        }
    }


    protected void buildReaderFile()
    {
        try {
            if ((inLogFile == null) || !inLogFile.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "log file not found");
            }
            FileInputStream fstream = new FileInputStream(inLogFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            inLogBR = new BufferedReader(new InputStreamReader(in));

        } catch (Exception ex) {
            throw new TRuntimeException.GENERAL_EXCEPTION(ex.toString());
        }
    }

    protected void addLine(String line)
    {
        if (StringUtil.isEmpty(line)) return;
        if (!line.contains(BASEKEY)) return;
        try {
            if (DEBUG) System.out.println("line=" + line);
            String parts[] = line.split(" \\| ");
            if (parts.length < 5) return;
            int base = 0;
            for (base = 0; base < parts.length; base++) {
                //System.out.println("part[" + base + "]:" + parts[base]);
                if (parts[base].equals(BASEKEY)) break;
            }
            if (DEBUG) System.out.println("base=" + base);
            if (base == parts.length) return;
            String threadName = parts[base + 1];
            String type = parts[base + 2];
            String key = parts[base + 3];
            String milS = parts[base + 4];
            long mil = Long.parseLong(milS);
            String accumKey = key + DELIM + threadName;
            Long startMil = startEventt.get(accumKey);
            if (type.equals(START)) {
                if (startMil != null) startEventt.remove(accumKey);
                startEventt.put(accumKey, mil);

            } else if (type.equals(END)) {
                if (startMil == null) return;
                long difMil = mil - startMil;
                addAccum(accumKey, difMil);
                addAccum(key + DELIM + " ", difMil);
                startEventt.remove(accumKey);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TRuntimeException.GENERAL_EXCEPTION(ex.toString());
        }
    }

    public void addAccum(String key, long difMil)
    {
        if (StringUtil.isEmpty(key)) return;
        Long num = accumTimer.get(key);
        Long cnt = accumCounter.get(key);
        if (num != null) {
            num += difMil;
            accumTimer.put(key, num);
        } else {
            accumTimer.put(key, difMil);
        }
        if (cnt != null) {
            cnt += 1;
            accumCounter.put(key, cnt);
        } else {
            cnt = (long)1;
        }
        accumCounter.put(key, cnt);
    }

    public static String start(LoggerInf logger, String key)
    {
        String msg = normMsg(START, key);
        if (DEBUG) System.out.println("start=" + msg);
        logMsg(logger, msg);
        return msg;
    }

    public static String end(LoggerInf logger, String key)
    {
        String msg = normMsg(END, key);
        logMsg(logger, msg);
        return msg;
    }

    public static String start(String key)
    {
        return normMsg(START, key);
    }

    public static String end(String key)
    {
        return normMsg(END, key);
    }

    public static void logMsg(LoggerInf logger, String msg)
    {
        log(logger, msg);
    }

    protected static void log(LoggerInf logger, String key)
    {
        if (DEBUG) System.out.println("maxLevel=" + logger.getMessageMaxLevel());
        if (logger.getMessageMaxLevel() <  LOGSTAT) return;
        logger.logMessage(key, 0, true);
        if (DEBUG) System.out.println("msg=" + key);
    }

    protected static String normKey(String type, String key)
    {
        if (key == null) key = "";
        Thread t = Thread.currentThread();
        String name = t.getName();
        return  DELIM + BASEKEY + DELIM + name + DELIM + type + DELIM + key;
    }

    protected static String normMsg(String type, String key)
    {
        String msg = normKey(type, key);
        long timeMil = DateUtil.getEpochUTCDate();
        return msg + DELIM + timeMil;
    }
    
    /* 
     * sequential dump of status values
     * @param loglvl log verbose for dump
     * @param sys System verbose for dump
     */
    public String getStatus(String header, boolean dumpThread)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(NL + "*****" + header+ "*****" + NL);
        buf.append("Status:" + NL);
        Vector sKeys = new Vector(accumTimer.keySet());
        Collections.sort(sKeys);
        String key = null;
        for (int i=0; i < sKeys.size(); i++) {
            key = (String)sKeys.get(i);
            if (!dumpThread) {
                String [] keyParts = key.split(" \\| ");
                if (DEBUG) System.out.println("getStatus"
                        + " - keyParts.length=" + keyParts.length
                        + " - keyParts[1]=\"" + keyParts[1] + "\""
                        + " - isEmpty=" + StringUtil.isAllBlank(keyParts[1])
                        );
                if ((keyParts.length <= 1) || !StringUtil.isAllBlank(keyParts[1])) continue;
            }
            Long timeMil = accumTimer.get(key);
            Long cnt = accumCounter.get(key);
            String dispKey = "[" + key + "]:";
            if ((cnt == null)) {
                buf.append("[" + key +"] count=NULL" + NL);
            } else if (timeMil == null) {
                buf.append("[" + key +"] time=NULL" + NL);
            } else {
                float avg = (float)timeMil / (float)cnt;
                buf.append("[" + key +"] count=" + cnt
                        + " - time=" + timeMil
                        + " - avg=" + avg
                        + NL);
            }
        }
        return buf.toString();
    }

    public Hashtable<String, Long> getAccumCounter() {
        return accumCounter;
    }

    public Hashtable<String, Long> getAccumTimer() {
        return accumTimer;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public Stat getStat(String key)
    {
        String accumKey = key + DELIM + " ";
        Long timeMil = accumTimer.get(accumKey);
        if (timeMil == null) return null;
        Long count = accumCounter.get(accumKey);
        if (count == null) return null;
        return new Stat(timeMil, count, key);
    }

    public static class Stat {
        public long timeMil = 0;
        public long count = 0;
        public String key = null;
        public Stat(
                long timeMil,
                long count,
                String key)
        {
            this.timeMil = timeMil;
            this.count = count;
            this.key = key;
        }

        public long getCount() {
            return count;
        }

        public String getKey() {
            return key;
        }

        public long getTimeMil() {
            return timeMil;
        }
    }
}