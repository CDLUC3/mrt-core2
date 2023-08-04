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

import org.apache.logging.log4j.Level;
import org.cdlib.mrt.log.*;
import org.cdlib.mrt.utility.TException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.cdlib.mrt.log.utility.AddStateEntry;
import org.cdlib.mrt.log.utility.Log4j2Util;
import org.json.JSONObject;

public class TestUse {
    private static final Logger logger = LogManager.getLogger();
    private static final Logger logDebug = LogManager.getLogger("ConsoleLog");
    private static final Logger logJSON = LogManager.getLogger("JSONLog");
   
   
    public static void main(String args[])
        throws TException
    {
        try {
            TestUse test = new TestUse();
            test.doTest();
            
            
        } catch (Exception ex) {
            System.out.println("main Exception:" + ex);
            ex.printStackTrace();
            
        }
    } 
    
    public TestUse()
       throws TException
    {
    }
    
    public void doTest()
       throws TException
    {
        Log4j2Util.whichLog4j2("start");
        AddStateEntry stateEntry = AddStateEntry.getAddStateEntry("storage", "add");
        stateEntry.setTargetNode(2001L);
        stateEntry.setArk("ark:/13030/testarkentry");
        stateEntry.setVersion(15);
        stateEntry.setDurationMs(1234L);
        
        int tst=9;
        stateEntry.addLogStateEntry("logstate#" + tst);
        
        try {
            throw new TException.INVALID_OR_MISSING_PARM("Bad parm");
        } catch (TException tex) {
            logger.error("exception#" + tst, tex);
        }
        
        logger.error("Before#" + tst +": error");
        logger.warn("Before#" + tst +" warn");
        logger.info("Before#" + tst +" info");
        logger.debug("Before#" + tst +" debug");
        logger.trace("Before#" + tst +" trace");
        
        Log4j2Util.setRootLevel(Level.DEBUG);
        
        Log4j2Util.whichLog4j2("flip");
        logger.error("After#" + tst +": error");
        logger.warn("After#" + tst +" warn");
        logger.info("After#" + tst +" info");
        logger.debug("After#" + tst +" debug");
        logger.trace("After#" + tst +" trace");

    }
}
