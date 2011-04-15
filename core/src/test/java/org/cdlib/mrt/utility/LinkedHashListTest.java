/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.LinkedHashList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Vector;



/**
 *
 * @author dloy
 */
public class LinkedHashListTest {

    public LinkedHashListTest() {
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
    public void build()
    {
        try {
            LinkedHashList<String, String> list = new LinkedHashList<String, String>(20);
            list.put("key3", "value2");
            list.put("key3","value3");
            list.put("key1", "value1");
            list.put("key1","value2");
            list.put("key2", "value1");
            list.put("key3", "value1");
            list.put("key3","value2");
            Vector<String> values = null;
            for (String key: list.keySet()) {
                values = list.get(key);
                dump(key, values);
                if (values != null) {
                    int cnt = list.getCnt(key);
                    System.out.println(key + ": "+ values.size()
                        + " - cnt=" + cnt);
                    assertTrue(values.size() == cnt);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            assertFalse("Exception:" + ex, true);
        }
    }
    
    public void dump(String key, Vector<String> list)
    {
        try {
            if (StringUtil.isEmpty(key)) {
                assertTrue(false);
            }
            if ((list == null) || (list.size() == 0)) {
                assertTrue("list invalid", false);
            }
            System.out.println("***Dump key=" + key);
            for (String value: list) {
                System.out.println("-value=" + value);
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            assertFalse("Exception:" + ex, true);
        }
    }



}