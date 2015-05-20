/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.TException;
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
public class TExceptionTest {
    protected static final String NL = System.getProperty("line.separator");

    protected static final String MSG = "this is an exception";
    protected static final String DETAIL = "this is a detail";

    public TExceptionTest() {
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
    public void testException1()
    {
        try {
            throwTException();

        } catch (TException ex) {
            dumpException(1, ex);

            assertTrue( ex.getDetail().contains("java.lang.ArithmeticException:"));
            assertTrue( ex.getStatusName().equals("GENERAL_EXCEPTION"));
            assertTrue( ex.getDescription().equals("Unexpected Programmic exception"));
            assertTrue( ex.getHTTPResponse() == 500);
            assertTrue( ex.getClassName().equals("org.cdlib.mrt.utility.TExceptionTest"));
            assertTrue( ex.getMethodName().equals("throwTException"));
        }
    }

    @Test
    public void testException2()
    {
        try {
            throw new TException.USER_NOT_AUTHORIZED(MSG);

        } catch (TException ex) {
            dumpException(2, ex);
            assertTrue( ex.getDetail().equals(MSG));
            assertTrue( ex.getStatusName().equals("USER_NOT_AUTHORIZED"));
            assertTrue( ex.getDescription().equals("User not authorized"));
            assertTrue( ex.getHTTPResponse() == 401);
            assertTrue( ex.getClassName().equals("org.cdlib.mrt.utility.TExceptionTest"));
            assertTrue( ex.getMethodName().equals("testException2"));
        }
    }

    @Test
    public void testException3()
    {
        try {
            throwException3();

        } catch (TException ex) {
            try {
                throw new TException.GENERAL_EXCEPTION(ex);
            } catch (Exception nex) {
                dumpException(3, (TException)nex);
                assertTrue( ex.getDetail().equals(MSG));
                assertTrue( ex.getStatusName().equals("USER_NOT_AUTHORIZED"));
                assertTrue( ex.getDescription().equals("User not authorized"));
                assertTrue( ex.getHTTPResponse() == 401);
                assertTrue( ex.getClassName().equals("org.cdlib.mrt.utility.TExceptionTest"));
                assertTrue( ex.getMethodName().equals("throwException3"));
            }
        }
    }

    @Test
    public void testRuntimeException3()
    {
        try {
            throwRuntimeException3();

        } catch (TRuntimeException ex) {
            try {
                throw new TException.GENERAL_EXCEPTION(ex);
            } catch (TException tex) {
                dumpException(3, (TException)tex);
                assertTrue( tex.getDetail().equals(MSG));
                assertTrue( tex.getStatusName().equals("USER_NOT_AUTHORIZED"));
                assertTrue( tex.getDescription().equals("User not authorized"));
                assertTrue( tex.getHTTPResponse() == 401);
                assertTrue( tex.getClassName().equals("org.cdlib.mrt.utility.TExceptionTest"));
                assertTrue( tex.getMethodName().equals("throwRuntimeException3"));
            }
        }
    }


    @Test
    public void testException4()
    {
        try {
            throwException4();

        } catch (TException ex) {
            try {
                throw new TException.GENERAL_EXCEPTION(DETAIL, ex);
            } catch (TException nex) {
                dumpException(4, (TException)nex);
                
                assertTrue( nex.getDetail().equals(DETAIL));
                assertTrue( nex.getStatusName().equals("INVALID_OR_MISSING_PARM"));
                assertTrue( nex.getDescription().equals("Program required parm is missing or invalid"));
                assertTrue( nex.getHTTPResponse() == 500);
                assertTrue( nex.getClassName().equals("org.cdlib.mrt.utility.TExceptionTest"));
                assertTrue( nex.getMethodName().equals("throwException4"));
            }
        }
    }

    @Test
    public void testException5()
    {
        try {
            throwTException2();

        } catch (TException ex) {
            dumpException(5, ex);

            assertTrue( ex.getDetail().contains("java.lang.ArithmeticException:"));
            assertTrue( ex.getStatusName().equals("GENERAL_EXCEPTION"));
            assertTrue( ex.getDescription().equals("Unexpected Programmic exception"));
            assertTrue( ex.getHTTPResponse() == 500);
            assertTrue( ex.getClassName().equals("org.cdlib.mrt.utility.TExceptionTest"));
            assertTrue( ex.getMethodName().equals("throwTException2"));
        }
    }

    protected void throwException3()
        throws TException
    {
        throw new TException.USER_NOT_AUTHORIZED(MSG);
    }

    protected void throwRuntimeException3()
    {
        throw new TRuntimeException.USER_NOT_AUTHORIZED(MSG);
    }



    protected void throwException4()
        throws TException
    {
        try {
            throw new Exception("TEST EXCEPTION");

        } catch (Exception ex) {
            throw new TException.INVALID_OR_MISSING_PARM(ex);
        }
    }

    protected void dumpException(int testnum, TException ex)
    {
            System.out.println("****Dump Exception:" + testnum + NL
                    + ex.dump("TExceptionTest")
                );
    }

    public void throwTException()
        throws TException
    {
        try {
            int one = 1;
            int zero = 0;
            int err = one/zero;

        } catch (Exception ex) {

            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public void throwTException2()
        throws TException
    {
        try {
            int one = 1;
            int zero = 0;
            int err = one/zero;

        } catch (Exception ex) {

            throw new TException(ex);
        }
    }

}