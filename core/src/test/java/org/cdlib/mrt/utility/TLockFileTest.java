/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.TRuntimeException;
import java.io.File;
import java.util.Vector;

import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TException;;
import org.cdlib.mrt.utility.TLockFile;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dloy
 */
public class TLockFileTest {
    protected static final String NAME = "TestMLockFile";
    protected static final String MESSAGE = NAME + ": ";

    protected LoggerInf logger = null;
    protected File directory = null;
    protected File bumpFile = null;
    public static final String lockFileName = "testlock.txt";
    protected static final int threadCnt = 20;
    protected Vector<Thread> threadList = new Vector<Thread>(threadCnt);

    public TLockFileTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            logger = new TFileLogger("lockFile", 10, 10);
            directory = FileUtil.getTempDir("locktest", logger);
            bumpFile = new File(directory, "bumpfile.txt");

        } catch (Exception ex) {
            throw new TRuntimeException.INVALID_DATA_FORMAT("Setup fails: " + ex);
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIt()
    {
        String key = "TLockFileTest";
        try {
            runIt();
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    @Test
    public void testStale()
    {
        String key = "testStale";
        String lockFileName = "teststalelock.txt";
        TLockFile fileLock = null;
        try {
            System.out.println("lockFileName=" + lockFileName);
            fileLock = TLockFile.getTLockFile(directory, lockFileName, 10);
            int i=0;
            for (i=0; i <= 15; i++) {
                log("testStale:" + i,0);
                TLockFile fileLock2 = TLockFile.getReplaceStaleTLockFile(directory, lockFileName, 10);
                if (fileLock2.isActiveLock()) {
                    if (i >= 9) {
                        assertTrue(true);
                    } else {
                        assertTrue(false);
                    }
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {}

            }
            assertTrue(false);

        } catch (Exception ex) {
            System.out.println("key=" + key + " - Exception:" + ex);
            assertFalse("key=" + key
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    public void runIt()
        throws Exception
    {
       startThreads(threadCnt, directory, bumpFile);
    }

    /**
     * Start threads to a maximum of processThreadCnt
     * Check to see if any active threads and adjust count based on how
     * many are still active.
     */
    public void startThreads(
            int processThreadCnt,
            File directory,
            File bumpFile)
    {
        log(MESSAGE + "startThreads threadCnt=" + processThreadCnt, 10);
        try {
            for (int i=0; i < processThreadCnt; i++) {
                // the processFeeder is a thread routine to
                // cycle the process level requests to the feeder
                ProcessQueue processFeeder = new ProcessQueue(directory, bumpFile);
                Thread thread = new Thread(processFeeder);
                thread.start();
                threadList.add(thread);
                logger.logMessage(MESSAGE + "starting Thread:" + thread.getName(), 10, true);
            }
            joinStopThreads(0);
            //Thread.sleep(60000);


        }  catch(Exception e) {
            log(
                MESSAGE + "Failed to initialize. Exception: " +
                e,
                0);
        }
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
        if (threadList == null) return;
        int interuptCnt = 0;
        for (int i=threadList.size() - 1; i >= 0 ; i--) {
            thread = (Thread)threadList.get(i);
            if (thread.isAlive()) {
                try {
                    thread.join(interval);
                } catch (Exception ex) {
                    log(MESSAGE + "warning - thread join exception:" + ex, 0);
                }
            }
            threadList.remove(i);
            String threadName = thread.getName();
            log('[' + threadName + "] *****> removed", 0 );
        }
        return;
    }

    protected void log(String msg, int lvl)
    {
        System.out.println(msg);
        logger.logMessage(msg, 0, true);
    }

    /**
     * ProcessQue handles all process off of the database work queue
     */
    protected class ProcessQueue implements Runnable
    {
        File directory = null;
        File bumpFile = null;
        String threadName = null;
        TLockFile fileLock = null;
        ProcessQueue(File directory, File bumpFile) {
            this.directory = directory;
            this.bumpFile = bumpFile;
        }

        /**
         * process queue
         */
        public void run() {
            try {
                System.out.println("lockFileName=" + lockFileName);
                fileLock =
                        TLockFile.getReplaceTLockFile(directory, lockFileName, 300, 300, 3, 3);
                Thread t = Thread.currentThread();
                threadName = t.getName();
                TLockFile.LockContent lockContent = fileLock.getLockContent();
                //System.out.println("lockContent=" + lockContent);
                log("[" + threadName + "]:"
                        + " isActive=" + fileLock.isActiveLock()
                        + " lockContent:" + lockContent.dump()
                        , 0);

                if (fileLock.isActiveLock()) {
                    incFile(bumpFile, 1);
                    boolean val = fileLock.remove();
                    log("[" + threadName + "] *****>active remove:" + val
                        , 0);

                } else {
                    log("[" + threadName + "] ********>not active"
                            , 0);
                }
            } catch (Exception ex) {
                log("run exception:" + ex, 0);
                log(StringUtil.stackTrace(ex), 0);
            }
        }

        protected void incFile(File incFile, int inc)
            throws TException
        {
            FileUtil.incFile(incFile, inc);
            System.out.println("[" + threadName + "] ********>BUMP");
        }
    }

}