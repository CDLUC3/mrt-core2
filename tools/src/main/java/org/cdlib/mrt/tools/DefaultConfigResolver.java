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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author DLoy
 * Class used to 
 *
 */
public abstract class DefaultConfigResolver implements UC3ConfigResolver
{
    
    private String ssmPath = null;
    private String defaultReturn = null;
    
    public DefaultConfigResolver(String prefix) 
    { 
        if (StringUtil.isAllBlank(prefix)) {
            setPath(System.getenv("SSM_ROOT_PATH"));
        } else {
            setPath(prefix);
        }
    }
    
    public DefaultConfigResolver() 
    { 
        setPath(System.getenv("SSM_ROOT_PATH"));
    }
    
    protected void setPath(String prefix)
    {
        if (prefix == null) return;
        if (!prefix.endsWith("/")) {
                prefix += "/";
        }
        this.ssmPath = prefix;
    }
    
    public abstract String getResolvedValue(String parameterName)
        throws TException;
    
    public String getResolvedStorageNode(long num)
        throws TException
    {
    	return getResolvedValue(ssmPath + "cloud/nodes/" + num);
    }

    public String getSsmPath() {
        return ssmPath;
    }

    public void setSsmPath(String ssmPath) {
        this.ssmPath = ssmPath;
    }
    
	public String getValueOrDefault(String a, String def) {
		if (a != null) {
			return a;
		}
		if (def != null) {
			return def;
		}
		if (this.defaultReturn != null) {
			return this.defaultReturn;
		}
		return null;
	}

	@Override
	public void setDefaultReturn(String defaultReturn) {
		this.defaultReturn = defaultReturn;
	}

	public static Pattern pToken = Pattern.compile("\\{!(ENV|SSM):\\s*([^\\}!]*)(!DEFAULT:\\s([^\\}]*))?\\}");

	@Override
	public String resolveConfigValue(String s) throws RuntimeConfigException {
		Matcher m = pToken.matcher(s);
		if (m.matches()) {
			String type = getValueOrDefault(m.group(1), "");
			String key = getValueOrDefault(m.group(2), "");
			String def = getValueOrDefault(m.group(4), null);

			String ret = null;
			if (type.equals("ENV")) {
				ret = getValueOrDefault(System.getenv(m.group(2)), def);
			}
			if (type.equals("SSM")) {
			    try {
                    String value = getResolvedValue(key);
					ret = getValueOrDefault(value, def);
			    } catch(Exception e) {
			    	ret = def;
			    }
			}

			if (ret == null) {
				throw new RuntimeConfigException("Cannot resolve " + s);
			}
			return ret;
		}
		return s;
	}

}
