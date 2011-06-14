/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.core;

import org.cdlib.mrt.core.Identifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.TException;

/**
 *
 * @author dloy
 */
public class IdentifierTest {

    public IdentifierTest() {
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

    @Test
    public void TestIt()
        throws TException
    {
        try {
            Identifier id = new Identifier("http://this.is.a.test",Identifier.Namespace.URL);
            assertTrue(id.getValue().equals("http://this.is.a.test"));
            assertTrue(id.getNamespace() == Identifier.Namespace.URL);
            id = new Identifier("13030/abcdefg");
            assertTrue(id.getValue().equals("13030/abcdefg"));
            assertTrue(id.getNamespace() == Identifier.Namespace.ARK);

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    @Test (expected=org.cdlib.mrt.utility.TException.INVALID_OR_MISSING_PARM.class)
    public void testException1()
        throws TException
    {
        Identifier id = new Identifier(null, Identifier.Namespace.URL);
    }

    @Test (expected=org.cdlib.mrt.utility.TException.INVALID_OR_MISSING_PARM.class)
    public void testException2()
        throws TException
    {
        Identifier id = new Identifier("ABCEDF", null);
    }
}