/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;

import org.cdlib.mrt.core.MessageDigest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.cdlib.mrt.utility.MessageDigestType;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.StringUtil;

/**
 *
 * @author dloy
 */
public class MessageDigestTest {
    
    public MessageDigestTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
       @Test public void equalAndSetWork() {
       try {
            MessageDigest digest1 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", MessageDigestType.sha1);
            MessageDigest digest2 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", MessageDigestType.sha1);
            assertTrue("Equal construct matches",digest1.equals(digest2));
            MessageDigest digest3 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", MessageDigestType.sha256);
            assertTrue(!digest1.equals(digest3));
            MessageDigest digest4 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", "sHa256");
            assertFalse(digest1.equals(digest4));
            
        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
}
    @Test (expected=org.cdlib.mrt.utility.TException.INVALID_OR_MISSING_PARM.class)
    public void testException()
        throws TException
    {
        MessageDigest digest5 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", "shaxxx");
    }
}