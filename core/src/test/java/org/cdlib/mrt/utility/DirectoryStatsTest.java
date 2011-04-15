/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.DirectoryStats;
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
public class DirectoryStatsTest {

    public DirectoryStatsTest() {
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
    public void equalAndSetWork()
    {
        try {
            DirectoryStats ds1 = new DirectoryStats();
            ds1.setFileCnt(10);
            ds1.setFileSize(1000);
            DirectoryStats ds2 = new DirectoryStats();
            ds2.setFileCnt(30);
            ds2.setFileSize(3000);
            DirectoryStats ds3 = ds2.copy();

            ds2.subtract(ds1);
            assertTrue(ds2.getFileCnt() == 20);
            assertTrue(ds2.getFileSize() == 2000);

            ds3.add(ds1);
            assertTrue(ds3.getFileCnt() == 40);
            assertTrue(ds3.getFileSize() == 4000);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

}