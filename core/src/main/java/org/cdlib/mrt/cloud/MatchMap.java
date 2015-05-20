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
package org.cdlib.mrt.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import org.cdlib.mrt.cloud.ManInfo;

import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.ManifestRowAdd;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.URLEncoder;

/**
 * This object imports the formatTypes.xml and builds a local table of supported format types.
 * Note, that the ObjectFormat is being deprecated and replaced by a single format id (fmtid).
 * This change is happening because formatName is strictly a description and has no functional
 * use. The scienceMetadata flag is being dropped because the ORE Resource Map is more flexible
 * and allows for a broader set of data type.
 * 
 * @author dloy
 */
public class MatchMap
{
    private static final String NAME = "MatchMap";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    
    protected VersionMap mapOne = null;
    protected VersionMap mapTwo = null;
    protected LoggerInf logger = null;
    protected boolean missingMap = false;
    protected boolean differentCurrent = false;
    protected boolean differentContent = false;
    protected boolean subset = false;
    
    public MatchMap(VersionMap mapOne, VersionMap mapTwo, LoggerInf logger)
        throws TException
    {
        this.mapOne = mapOne;
        this.mapTwo = mapTwo;
        this.logger = logger;
    }
    
    public void compare()
        throws TException
    {
        if ((mapOne == null) || (mapTwo == null)) {
            missingMap = true;
            return;
        }
        long currentOne = mapOne.getCurrent();
        long currentTwo = mapTwo.getCurrent();
        subset = isSubsetOneTwo(currentOne, currentTwo);
        if (currentOne != currentTwo) {
            differentCurrent = true;
            return;
        }
        if (!subset) {
            differentContent = true;
        }
        
    }
    
    public boolean isDifferent()
    {
        return missingMap | differentCurrent | differentContent;
    }
    
    protected boolean sameOneTwo()
    {
        long current = mapOne.getCurrent();
        //ManInfo getVersionInfo(int versionID)
        for (int i=1; i<=current; i++) {
            ManInfo infoOne = mapOne.getVersionInfo(i);
            ManInfo infoTwo = mapTwo.getVersionInfo(i);
            if ((infoOne.size != infoTwo.size) ||  (infoOne.count != infoTwo.count)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isSubsetOneTwo(long currentOne, long currentTwo)
    {
        subset = true;
        long current = currentOne <= currentTwo ? currentOne : currentTwo;
        //ManInfo getVersionInfo(int versionID)
        for (int i=1; i<=current; i++) {
            ManInfo infoOne = mapOne.getVersionInfo(i);
            ManInfo infoTwo = mapTwo.getVersionInfo(i);
            if ((infoOne.size != infoTwo.size) ||  (infoOne.count != infoTwo.count)) {
                return false;
            }
        }
        return true;
    }

    public boolean isMissingMap() {
        return missingMap;
    }

    public boolean isDifferentCurrent() {
        return differentCurrent;
    }

    public boolean isDifferentContent() {
        return differentContent;
    }

    public boolean isSubset() {
        return subset;
    }
    
    public String dump(String header)
    {
        return "compare:" + header + "\n"
                    + " - isMissingMap=" + isMissingMap() + "\n"
                    + " - isDifferent=" + isDifferent() + "\n"
                    + " - isDifferentCurrent=" + isDifferentCurrent() + "\n"
                    + " - isDifferentContent=" + isDifferentContent() + "\n"
                    + " - isSubset=" +isSubset() + "\n";
    }
    
}
