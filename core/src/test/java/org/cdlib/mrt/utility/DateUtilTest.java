/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.DateUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


import java.util.Date;

/**
 *
 * @author dloy
 */
public class DateUtilTest {

    public DateUtilTest() {
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
            Date currentDate = DateUtil.getCurrentDate();
            String dateS = DateUtil.getIsoDate(currentDate);
            Date date = DateUtil.getIsoDateFromString(dateS);
            String mcDate = currentDate.toString();
            String mfDate = date.toString();
            assertTrue("currentDate=" + currentDate + " - date=" + date, mcDate.equals(mfDate));

            dateS = DateUtil.getCurrentIsoDate();
            date = DateUtil.getIsoDateFromString(dateS);
            String date2S = DateUtil.getIsoDate(date);
            assertTrue(dateS.equals(date2S));

            String testPattern = "yyyy-MM-dd'T'HH:mm:ss-S-Z";
            currentDate = DateUtil.getCurrentDate();
            dateS = DateUtil.getDateString(currentDate, testPattern);
            date = DateUtil.getDateFromString(dateS, testPattern);
            assertTrue("dateS=" + dateS + " - currentDate=" + currentDate + " - date=" + date, currentDate.equals(date));

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void badDate()
    {
        try {

            String dateS = "-";
            Date date = DateUtil.getIsoDateFromString(dateS);
            System.out.println(date);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

}