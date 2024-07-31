/*
Copyright (c) 2005-2012, Regents of the University of California
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
/**
 *
 * @author dloy
 */
package org.cdlib.mrt.log.test;

import org.cdlib.mrt.utility.TException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdlib.mrt.log.utility.AddStateEntryGen;
import org.cdlib.mrt.log.utility.Log4j2Util;
import org.json.JSONObject;

public class TestElement {
    private static final Logger logger = LogManager.getLogger();
   
   
    public static void main(String args[])
        throws TException
    {
        try {
            TestElement test = new TestElement();
            test.doTest();
            
            
        } catch (Exception ex) {
            System.out.println("main Exception:" + ex);
            ex.printStackTrace();
            
        }
    } 
    
    public TestElement()
       throws TException
    {
    }
    
    public void doTest()
       throws TException
    {
        Log4j2Util.whichLog4j2("start");
        
        try {
        JSONObject jsonRoot = new JSONObject();
        jsonRoot.put("BiggyNum", 123456789);
        AddStateEntryGen.addEntry("info", jsonRoot);
        
        
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("SomeKey", "yowza");
        AddStateEntryGen.addEntry("info", jsonRoot);
        
        JSONObject jsonRoot2 = new JSONObject();
        jsonRoot2.put("BiggyNum2", 1234567890);
        jsonRoot2.put("SomeKey2", "yowza2");
        AddStateEntryGen.addEntry("info", jsonRoot2);
        } catch (Exception er) {
            System.out.println(er);
        }
    }
}
