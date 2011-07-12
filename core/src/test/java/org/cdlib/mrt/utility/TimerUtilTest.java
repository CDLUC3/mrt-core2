/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.cdlib.mrt.utility.LoggerInf;
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
public class TimerUtilTest {

    private static final String NL = System.getProperty("line.separator");
    public TimerUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    
    @Test
    public void testLog()
    {
        try {
            //File log = FileUtil.getTempFile("tst", "txt");
            String path = "C:/Documents and Settings/dloy/My Documents/MRTMaven/work/timings/split-fix110711.log";
            System.out.println("PATH:" + path);
            File log = new File(path);
            TimerUtil util = new TimerUtil(log);
            util.process();
            System.out.println(util.getStatus("testLog", false));
            assertTrue(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);
        }
    }


    //@Test
    public void genericTimerTest()
    {
        File log = null;
        try {
            log = FileUtil.getTempFile("tst", "txt");
            PrintStream ps = new PrintStream(new FileOutputStream(log));
            printType(ps, "mytest", 2000);
            ps.close();
            String out = FileUtil.file2String(log);
            System.out.println("out>" + NL + out + NL + "<out" + NL);
            assertTrue(true);

            TimerUtil util = new TimerUtil(log);
            util.process();
            System.out.println(util.getStatus("main thread", true));
            System.out.println(util.getStatus("main NO thread", false));
            assertTrue(true);

            TimerUtil.Stat stat = util.getStat("mytest");
            assertTrue(stat != null);
            assertTrue(stat.count == 1);
            assertTrue(stat.timeMil > 1990);
            TimerUtil.Stat statMissing = util.getStat("xxtest");
            assertTrue(statMissing == null);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
            
        } finally {
            if (log != null) {
                try {
                    log.delete();
                } catch (Exception e) { }
            }
        }
    }

    //@Test
    public void genericTimerThread()
    {
        File log = null;
        try {
            log = FileUtil.getTempFile("tst", "txt");
            PrintStream ps = new PrintStream(new FileOutputStream(log));
            ExecutorService threadPool
                    = Executors.newFixedThreadPool(3);
            for(int i = 0; i < 10; i++){
                TThread t = new TThread(ps, "Thread test", i*10);
                threadPool.execute(t);
            }
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            ps.close();
            String out = FileUtil.file2String(log);
            System.out.println("thread>" + NL + out + "<thread" + NL);
            assertTrue(true);

            TimerUtil util = new TimerUtil(log);
            util.process();
            System.out.println(util.getStatus("thread thread", true));
            System.out.println(util.getStatus("thread NO thread", false));
            assertTrue(true);

            TimerUtil.Stat stat = util.getStat("Thread test");
            assertTrue(stat != null);
            assertTrue(stat.count == 10);
            assertTrue(stat.timeMil > 400);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);

        } finally {
            if (log != null) {
                try {
                    log.delete();
                } catch (Exception e) { }
            }
        }
    }
    
    public static void printType(PrintStream ps, String key, long sleep)
    {
        File log = null;
        try {
            String startKey = "some stuff" + TimerUtil.start(key) + NL;
            ps.print(startKey);
            Thread.sleep(sleep);
            String endKey = "other stuff" + TimerUtil.end(key) + NL;
            ps.print(endKey);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
            
        }
    }

    protected static class TThread
            implements Runnable
    {
        protected PrintStream ps = null;
        protected String key;
        protected long sleep;

        public TThread(PrintStream ps, String key, long sleep) {
            this.ps = ps;
            this.key = key;
            this.sleep = sleep;
        }

        public void run()
        {
            try {
                printType(ps, key, sleep);

            } catch (Exception ex) { }
        }
    }

}