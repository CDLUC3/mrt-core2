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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import static org.junit.Assert.*;

/**
 *
 * @author dloy
 */
public class HTTPUtilTest {

    public HTTPUtilTest() {
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
    public void TestGet()
    {
        try {
            HttpResponse response = HTTPUtil.getHttpResponse("http://google.com", 5000);
            int code = response.getStatusLine().getStatusCode();
            assertTrue("code=" + code, code == 200);
            HttpEntity entity = response.getEntity();
            byte [] buf = new byte[100000];
            long insize = 0;
            InputStream inStream = entity.getContent();
            while (true) {
                int incnt = inStream.read(buf);
                if (incnt < 0) break;
                insize += incnt;
            }
            assertTrue("size=" + insize, insize > 1000);

        } catch (Exception ex) {
            assertFalse("TestGet"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

    //@Test
    public void TestFTP()
    {
        try {
            String url = "ftp://ftp.funet.fi/pub/standards/RFC/rfc959.txt";
            boolean isFTP = HTTPUtil.isFTP(url);
            System.out.println("HTTPUtilTest: isFTP:" + isFTP);
            assertTrue(isFTP);
            InputStream inStream = HTTPUtil.getFTPInputStream(url, 5000);
            assertTrue(inStream != null);
            String disp = StringUtil.streamToString(inStream, "utf-8");
            System.out.println("HTTPUtilTest: TestFTP:" + disp);

        } catch (Exception ex) {
            assertFalse("TestGet"
                    + " - Exception:" + ex
                    + " - stack:" + StringUtil.stackTrace(ex)
                    , true);
        }
    }

}