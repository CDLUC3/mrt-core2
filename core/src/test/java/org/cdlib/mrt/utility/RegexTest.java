/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.RegexUtil;
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
public class RegexTest {

    public RegexTest() {
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
    public void failPat()
    {
        try {
                String data = "nfo:<http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#>";
                String pattern = "(.*)\\:\\<http\\://(.+)\\>";
                String [] matches = RegexUtil.listPattern(
                    data, pattern);
                if (matches != null) {
                    System.out.println("matches length=" + matches.length);

                for (String part : matches) {
                    System.out.println("matches[" + part + "]");
                }
                }
                assertTrue(matches != null);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void anotherPat()
    {
        try {
                String data = "nfo:<http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#>";
                String parts[] = data.split("\\:");
                System.out.println("anotherPat - parts.length=" + parts.length);
                for (String part : parts) {
                    System.out.println("part[" + part + "]");
                }
                String [] matches = new String[3];
                matches[0]= data;
                matches[1]=parts[0];
                matches[2]=parts[2].substring(2);
                for (String part : matches) {
                    System.out.println("matches[" + part + "]");
                }
                assertTrue(true);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }
}