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
package org.cdlib.mrt.utility;

import java.io.File;
import java.util.Date;

/**
 * Propriety locking class. May be replaced with normalized mechanism in java 7.
 * Allows locking to nano second range but is not specified in dflat to a form that
 * is independant of this java implementation.
 * @author dloy
 */
public class TLockFile
{
    protected static final String NAME = "MFrameFileLock";
    protected static final String MESSAGE = NAME + ": ";

    //protected static final String DATEPAT = "yyyy-MM-dd'T'HH:mm:ss'('S')'Z";
    protected static final int NOMATCH_ATTEMPTS = 200;
    protected File lockFile = null;
    protected boolean activeLock = false;
    protected LockContent lockContent = null;
    protected LockContent currentLock = null;
    protected File m_base = null;
    protected String m_lockName = null;
    protected boolean debug = false;

    /**
     * turn on/off debugging level
     * @param debug true=logging turned on, false=logging turned off
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Factory method
     * @param base base File directory to contain this lock
     * @param lockName name of lock being created
     * @return TLockFile object for this lock
     * @throws TException
     */
    public static TLockFile getTLockFile(
            File base,
            String lockName)
        throws TException
    {
        return new TLockFile(base, lockName);
    }

    /**
     * Factory method
     * @param base base File directory to contain this lock
     * @param lockName name of lock being created
     * @return TLockFile object for this lock
     * @throws TException
     */
    public static TLockFile getTLockFile(
            File base,
            String lockName,
            int staleTimeSeconds)
        throws TException
    {
        return new TLockFile(base, lockName, staleTimeSeconds);
    }

    /**
     * Factory method - this form allows multiple retry attempts on the lock
     * @param base base File directory to contain this lock
     * @param lockName name of lock being created
     * @param matchAttempts number of retries attempted
     * @param waitTimeSeconds wait time in seconds between retries
     * @return TLockFile object for this lock
     * @throws TException
     */
    public static TLockFile getTLockFile(
            File base,
            String lockName,
            int matchAttempts,
            int waitTimeSeconds)
        throws TException
    {
        TLockFile fileLock = new TLockFile(base, lockName);
        if (!fileLock.isActiveLock()) {
            fileLock.retryLock(NOMATCH_ATTEMPTS, matchAttempts, waitTimeSeconds);
        }
        return fileLock;
    }

    /**
     * Factory method - will replace lock if exceeds certain age
     * if does not exceed age then it attempts to build lock
     * @param base base File directory to contain this lock
     * @param lockName name of lock being created
     * @param staleTimeSeconds used to set lock staleTime value
     * @param replaceTimeSeconds if age of lock exceeds replace time then force replacement
     * @param matchAttempts number of retries attempted
     * @param waitTimeSeconds wait time in seconds between retries
     * @return TLockFile object for this lock
     * @throws TException
     */
    public static TLockFile getReplaceTLockFile(
            File base,
            String lockName,
            int staleTimeSeconds,
            int replaceTimeSeconds,
            int matchAttempts,
            int waitTimeSeconds)
        throws TException
    {
        TLockFile fileLock = new TLockFile(base, lockName, staleTimeSeconds);
        if (!fileLock.isActiveLock()) {
            fileLock.replaceStaleLock();
        }
        if (!fileLock.isActiveLock()) {
            fileLock.replaceLock(replaceTimeSeconds);
            fileLock.retryLock(NOMATCH_ATTEMPTS, matchAttempts, waitTimeSeconds);
        }
        return fileLock;
    }
    
    /**
     * Factory method - will replace lock if exceeds stale age
     * @param base base File directory to contain this lock
     * @param lockName name of lock being created
     * @param staleTimeSeconds used to set lock staleTime value
     * @return TLockFile object for this lock
     * @throws TException
     */
    public static TLockFile getReplaceStaleTLockFile(
            File base,
            String lockName,
            int staleTimeSeconds)
        throws TException
    {
        TLockFile fileLock = new TLockFile(base, lockName, staleTimeSeconds);
        if (!fileLock.isActiveLock()) {
            fileLock.replaceStaleLock();
        }
        return fileLock;
    }

    /**
     * Constructor
     * @param base base File directory to contain this lock
     * @param lockName name of lock being created
     * @throws TException
     */
    public TLockFile(File base, String lockName)
            throws TException
    {
        this.m_base = base;
        this.m_lockName = lockName;
        lockContent = buildLockContent(null);
        //lockFile = getLockFile(base, lockName);
        setLock();
    }

    public TLockFile(File base, String lockName, Integer staleTimeSeconds)
            throws TException
    {
        this.m_base = base;
        this.m_lockName = lockName;
        lockContent = buildLockContent(staleTimeSeconds);
        lockFile = getLockFile(base, lockName);
        setLock();
    }

    /**
     * <pre>
     * set the lock
     * - attempt to write lock file
     * - reread lock - if my lock then lock is active
     * - otherwise lock is inactive
     * </pre>
     * @throws TException
     */
    protected void setLock()
            throws TException
    {
        File testFile = null;
        while (true) {
            testFile = getLockFile(m_base, m_lockName);
            if (!testFile.exists()) break;
            LockContent localLock = extractLockContent(testFile);
            if (localLock == null) continue;
            currentLock = localLock;
            activeLock = false;
            if (lockContent.contentString.equals(localLock.contentString)) {
                activeLock = true;
            }
            log("setLock: lockFile exist:"
                    + " - activeLock=" + activeLock
                    + " - current->" + currentLock.dump());
            return;
        }
        log("setLock: lockFile does not exist");
        try {
            FileUtil.string2File(testFile, lockContent.contentString);

        } catch (Exception fnfe) {
            activeLock = false;
            log("Warning:" + fnfe);
            return;
        }

        LockContent localLock = extractLockContent(testFile);
        if (localLock == null) {
            FileUtil.string2File(testFile, lockContent.contentString);
            log("setLock:Reread missing fail");
            localLock = extractLockContent(testFile);
        }

        if (!lockContent.contentString.equals(localLock.contentString)) {
            activeLock = false;
            currentLock = localLock;
            log("Reread fail");
        } else {
            activeLock = true;
            currentLock = lockContent;
            log("Reread match:");
        }
    }

    /**
     * get requested lock file
     * @param base base File directory to contain this lock
     * @param lockName name of lock being created
     * @return lock file in this directory with this name
     * @throws TException
     */
    protected File getLockFile(File base, String lockName)
            throws TException
    {
        if ((base == null) || !base.exists()) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "MFrameFileLock - file base is missing");
        }
        if (StringUtil.isEmpty(lockName)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "MFrameFileLock - lockName is missing");
        }
        File localLockFile = new File(base, lockName);
        return localLockFile;
    }

    /**
     * Lock info extracted from file
     * @param localExtractFile file containing lock information
     * @return extracted parsed lock info
     * @throws TException
     */
    protected LockContent extractLockContent(File localExtractFile)
            throws TException
    {
        try {
            String contentString = FileUtil.file2String(localExtractFile);
            return new LockContent(contentString);
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * Used to create a new LockContent object using a stale date
     * @param staleSeconds seconds from current when lock is stale
     * @return LockContent lock object
     */
    protected LockContent buildLockContent(Integer staleSeconds)
    {
        LockContent content = new LockContent(staleSeconds);
        return content;
    }

    /**
     * replace existing lock with this lock if existing lock
     * exceeds the number of seconds provided. This is used to replace a stale
     * lock probable created in a failed process
     * @param seconds if age of lock exceeds this value then replace lock
     * @throws TException
     */
    public void replaceLock(int seconds)
            throws TException
    {
        if (isActiveLock()) return;
        if ((lockContent == null) || (currentLock == null)) {
            return;
        }
        
        if (lockContent.contentString.equals(currentLock.contentString)) {
            activeLock = true;
            return;
        }
        
        // do not do an override if a staleDate was supplied in current lock
        if (currentLock.staleDate != null) return;

        int diffSeconds = getDiffCurrent();
        if (diffSeconds >= seconds) {
            log("replaceLock - diff:" + diffSeconds);
            boolean delete =  lockFile.delete();
            if (delete) {
                setLock();
                log("replaceLock - lock replaced:" + diffSeconds);
            }
        } else {
            log("replaceLock - lock NOT replaced:" + diffSeconds);
        }
    }

    /**
     * Return the difference in seconds between current lock and an extracted lock
     * @return second difference between locks
     */
    protected int getDiffCurrent()
    {
        long timeCurrent = currentLock.creationDate.getTime();
        long timeContent = lockContent.creationDate.getTime();
        long diff = timeCurrent - timeContent;
        int seconds = (int)diff /1000;
        return seconds;
    }

    /**
     * <pre>
     * Retry setting lock -
     * Issue several threads waiting on same lock. Odd change on lock if another
     * thread has grabbed it as opposed to lock status not changing
     * - if this lock is current then lock is active and return
     * - attempt is made to build lock
     * - see if written lock is my lock
     * - if yes then lock is active
     * - if no, see if lock was grabbed by some other process since my last attempt
     * - if different process, then reissue lock
     * </pre>
     * @param noMatchAttempts number of attempts to set lock
     * @param matchAttempts number of attempts to match that lock to this lock
     * @param waitTimeSeconds number of seconds between match attemts
     * @throws TException
     */
    public void retryLock(
            int noMatchAttempts,
            int matchAttempts,
            int waitTimeSeconds)
        throws TException
    {
        if (currentLock == null) {
            log("retryLock: current lock null");
            return;
        }
        if (lockContent.contentString.equals(currentLock.contentString)) {
            activeLock = true;
            return;
        }
        LockContent localContent = currentLock;
        log("retryLock: lockContent:" + lockContent.dump());
        for (int nma = 0; nma < noMatchAttempts; nma++) {
            for (int ma = 0; ma < matchAttempts; ma++) {
                setLock();
                log("retryLock: currentLock:" + currentLock.dump());
                if (isActiveLock()) return;
                if (lockContent.contentString.equals(currentLock.contentString)) {
                    log("retryLock: lock content match");
                    activeLock = true;
                    return;
                }
                if (!localContent.contentString.equals(currentLock.contentString)) {
                    log("current lock does not match previous:");
                    log("local:" + localContent.dump());
                    log("curre:" + currentLock.dump());
                    break;
                }
                try {
                    log("retryLock sleep");
                    Thread.sleep(waitTimeSeconds * 1000);
                } catch (Exception ex) {
                    throw new TException.GENERAL_EXCEPTION(
                            MESSAGE + "retryLock - Exception:" + ex);
                }
            }
            localContent = currentLock;
        }
    }

    /**
     * replace existing lock with this lock if existing lock
     * stale time exceeds current time
     * @throws TException process exception
     */
    public void replaceStaleLock()
            throws TException
    {
        if (isActiveLock()) return;
        if ((lockContent == null) || (currentLock == null)) {
            return;
        }

        if (lockContent.contentString.equals(currentLock.contentString)) {
            activeLock = true;
            return;
        }
        if (currentLock.staleDate == null) return;

        Date currentDate = DateUtil.getCurrentDate();
        if (currentDate.getTime() > currentLock.staleDate.getTime()) {
            log("replaceStaleLock - stale:" + currentLock.staleDate);
            boolean delete =  lockFile.delete();
            if (delete) {
                setLock();
                log("replaceStaleLock - lock replaced:" + currentLock.staleDate);
            }
        } else {
            log("replaceStaleLock - lock NOT replaced:" + currentLock.staleDate);
        }
    }


    /**
     * status of lock
     * @return true=active, false=not set
     */
    public boolean isActiveLock()
    {
        return activeLock;
    }

    /**
     * get this lock file object
     * @return
     */
    public File getLockFile() {
        return lockFile;
    }

    /**
     * get lock info for this object
     * @return
     */
    public LockContent getLockContent() {
        return lockContent;
    }

    /**
     * delete lock
     * @return true=lock deleted, false=unable to delete lock
     * @throws TException
     */
    public boolean remove()
            throws TException
    {
        if (!activeLock) return false;
        boolean removeit = false;
        for (int i=0; i < 100; i++) {
            removeit = lockFile.delete();
            if (removeit) break;
        }
        return removeit;
    }

    /**
     * class containing lock information
     */
    public class LockContent
    {
        String contentString = null;
        public Date creationDate = null;
        public long nanoTime = System.nanoTime();
        public Date staleDate = null;
        public boolean isTLockFile = false;
        public String dump()
        {
            try {
            return "LockContent"
                    + " - contentString=" + contentString
                    + " - creationDate=" + creationDate.toString()
                    + " - nanoTime=" + nanoTime
                    + " - staleDate=" + staleDate.toString()
                    ;
            } catch (Exception ex) {
                log("dump--" + StringUtil.stackTrace(ex));
                return "";
            }
        }

        /**
         * Constructor for new LockContent based on current time
         * @param staleSeconds
         */
        public LockContent(Integer staleSeconds)
        {
            creationDate = DateUtil.getCurrentDate();
            nanoTime = System.nanoTime();
            if (staleSeconds != null) {
                staleDate = DateUtil.getCurrentDatePlus(staleSeconds*1000);
            }
            isTLockFile = true;
            contentString = "Lock:" 
                + " " + DateUtil.getIsoDate(creationDate)
                + " nano:" + nanoTime;
            if (staleDate != null) {
                contentString += " " + DateUtil.getIsoDate(staleDate);
            }
            log("LockContent1:" + dump());
        }
        

    /**
     * Lock info extracted from string
     * @param lockContent lock file content as string
     * @return extracted parsed lock info
     * @throws TException process exception
     */
    protected LockContent(String lockContent)
            throws TException
    {
        try {
            contentString = lockContent;
            String [] parts = contentString.split(" ");
            if ((parts.length < 2) || (parts.length > 4)){
                throw new TException.INVALID_DATA_FORMAT(
                        "extractLockContent not valid");
            }
            if (!parts[0].equals("Lock:")) {
                throw new TException.INVALID_DATA_FORMAT(
                        "unknown lock type:" + parts[0]);
            }
            creationDate
                    = DateUtil.getIsoDateFromString(parts[1]);
            if (parts.length > 2) {
                if (parts[2].startsWith("nano:")) {
                    String nanoS = parts[2].substring(5);
                    nanoTime = Long.parseLong(nanoS);
                    isTLockFile = true;
                } else {
                    nanoTime = creationDate.getTime() * 1000;
                }
            }
            if (parts.length > 3) {
                staleDate
                    = DateUtil.getIsoDateFromString(parts[3]);
            }

            log("LockContent2:" + dump());
        } catch (Exception ex) {
            log("LockContent2 - Exception:" + ex);
        }

    }
        
        @Override
        public String toString()
        {
            return contentString;
        }
    }

    /**
     * debug logging
     * @param msg log this message to sysout
     */
    protected void log(String msg)
    {
        if (!debug) return;
        Thread t = Thread.currentThread();
        String name = t.getName();
        msg = "[" + name + "]:" + msg;
        System.out.println(msg);
        //logger.logMessage(msg, 0, true);
    }

}
