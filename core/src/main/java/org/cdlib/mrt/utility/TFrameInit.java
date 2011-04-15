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


import org.cdlib.mrt.utility.TFrame;
import javax.servlet.ServletConfig;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
/**
 * Using ServletConfig initialize TFRame for properties extraction
 * @author dloy
 */
public class TFrameInit
{
    protected static final String NAME = "TFrameInit";
    protected static final String MESSAGE = NAME + ": ";

    protected TFrame tFrame = null;
    protected LoggerInf logger = null;

    /**
     * return logger
     * @return logger
     */
    public LoggerInf getLogger() {
        return logger;
    }

    /**
     * Return the TFrame property resolver
     * @return TFrame
     */
    public TFrame getTFrame() {
        return tFrame;
    }

    /**
     * Check InitParameters from web.xml to discover Properties files
     * then build TFrame properties resolver
     * @param servletConfig servlet configuration object
     * @param serviceName name of service for logging display
     * @throws TException
     */
    public TFrameInit(ServletConfig servletConfig, String serviceName)
            throws TException
    {

        //these parameters  come from the web.xml file
        String loggerFile = servletConfig.getInitParameter("loggerProperties");
        String serviceDefaultFile = servletConfig.getInitParameter("serviceProperties");
        String localFile = servletConfig.getInitParameter("localProperties");
        String[] propertyFiles =
              new String[] {loggerFile,
                            serviceDefaultFile,
                            localFile};

        instantiateTFrame(propertyFiles, serviceName);
        if (logger.getMessageMaxLevel() >= 10) {
            logger.logMessage(MESSAGE, 0);
            for (String propName: propertyFiles) {
                logger.logMessage("propName=" + propName, 0);
            }
        }
        localInit();
    }

    /**
     * Instantiat TFrame object
     * @param propertyFiles properties files resolved in order of String array
     * @param serviceName logger display serviceName
     * @throws TException
     */
    protected void instantiateTFrame(String[] propertyFiles,
            String serviceName)
        throws TException
    {
        try {
            tFrame = new TFrame(propertyFiles, serviceName);
            logger = tFrame.getLogger();
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex.toString());
        }
    }

    /**
     * Call back routine for local object initialization
     */
    protected void localInit()
    {
        return;
    }
}
