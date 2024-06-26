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
package org.cdlib.mrt.log.utility;

import java.net.URI;
import java.net.URL;
import org.apache.logging.log4j.Level;
import org.cdlib.mrt.utility.TException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class Log4j2Util {
    
    public static void setRootLevel(String levelS)
        throws TException
    {
        try {
            Level level = Level.toLevel(levelS, Level.INFO);
            setRootLevel(level);
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    public static void setRootLevel(Level level)
        throws TException
    {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();
            LoggerConfig rootConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            Level beforeLvl = rootConfig.getLevel();
            if (beforeLvl == level) {
                System.out.println("setRootLevel match:" + level.toString());
                return;
            }
            System.out.println("setRootLevel before:" + beforeLvl);
            rootConfig.setLevel(level);
            context.updateLoggers();
            String msg = "setRootLevel after:" + rootConfig.getLevel();
            System.out.println(msg);
            LogManager.getLogger().info(msg);
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    public static void setLoggerLevel(String loggerName, String levelS)
        throws TException
    {
        try {
            Level level = Level.toLevel(levelS, Level.INFO);
            setLoggerLevel(loggerName, level);
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    public static void setLoggerLevel(String loggerName, Level level)
        throws TException
    {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
            Level beforeLvl = loggerConfig.getLevel();
            if (beforeLvl == level) {
                System.out.println("setRootLevel match:" + level.toString());
                return;
            }
            System.out.println("setLoggerLevel(" + loggerName + ") before:" + beforeLvl);
            loggerConfig.setLevel(level);
            context.updateLoggers();
            String msg = "setLoggerLevel(" + loggerName + ") after:" + loggerConfig.getLevel();
            System.out.println(msg);
            LogManager.getLogger().info(msg);
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    public static String getRootLevel()
        throws TException
    {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();
            LoggerConfig rootConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            return rootConfig.getLevel().toString();
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    public static Logger getLoggerResource(String logConfigName)
       throws Exception
    {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        URL resource = Log4j2Util.class.getClassLoader().getResource(logConfigName);
        URI resourceURI = resource.toURI();
        context.setConfigLocation(resourceURI);
        context.updateLoggers();
        return LogManager.getLogger();
    }
    
    public static void whichLog4j2(String header)
    {
        try {
            if (header == null) header = "";
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            System.out.println(header + " Configuration found at "+ctx.getConfiguration().toString());
            LogManager.getLogger().warn("whichLog4j2: warn");
            LogManager.getLogger().info("whichLog4j2: info");
            LogManager.getLogger().debug("whichLog4j2: debug");
            LogManager.getLogger().trace("whichLog4j2: trace");
        } catch (Exception ex) {
            System.out.println("whichLog4j2 Exception:" + ex);
        }
    }
}
