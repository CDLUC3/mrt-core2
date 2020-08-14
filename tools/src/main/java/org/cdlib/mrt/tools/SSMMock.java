/*
Copyright (c) 2005-2020, Regents of the University of California
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
*********************************************************************/

package org.cdlib.mrt.tools;

import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

import java.util.HashMap;

/**
 *
 * @author DLoy
 * Class used to 
 *
 */
public class SSMMock implements SSMInterface
{
    
    private String ssmPath = null;
    private HashMap<String, String> map = new HashMap<>();
    
    public SSMMock(String prefix) 
    { 
        if (StringUtil.isAllBlank(prefix)) {
            setPath(System.getenv("SSM_ROOT_PATH"));
        } else {
            setPath(prefix);
        }
    }
    
    public SSMMock() 
    { 
        setPath(System.getenv("SSM_ROOT_PATH"));
    }
    
    private void setPath(String prefix)
    {
        if (prefix == null) return;
        if (!prefix.endsWith("/")) {
                prefix += "/";
        }
        this.ssmPath = prefix;
    }
    
    public void add(String key, String value) {
    	map.put(key, value);
    }
    
    public String get(String parameterName)
        throws TException
    {
        if (StringUtil.isAllBlank(parameterName)) {
            throw new TException.INVALID_OR_MISSING_PARM("SSM parameter empty");
        }
        String init = parameterName.substring(0,1);
        String searchName = parameterName;
        if (!init.equals("/")) {
            if (ssmPath == null) {
                throw new TException.INVALID_OR_MISSING_PARM(
                    "SSM parameter is relative and no SSM_ROOT_PATH supplied:" 
                    + parameterName);
            }
            searchName = ssmPath + parameterName;
        }
        if (!map.containsKey(searchName)) {
        	throw new TException.INVALID_OR_MISSING_PARM("SSM key not found: " + searchName);
        }
        return map.get(searchName);
    }
    
    public String getNode(long num)
        throws TException
    {
    	return get(ssmPath + "cloud/nodes/" + num);
    }

    public String getSsmPath() {
        return ssmPath;
    }

    public void setSsmPath(String ssmPath) {
        this.ssmPath = ssmPath;
    }
    
}
