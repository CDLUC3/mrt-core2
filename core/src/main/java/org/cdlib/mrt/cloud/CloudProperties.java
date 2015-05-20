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
*********************************************************************/
package org.cdlib.mrt.cloud;
import org.cdlib.mrt.utility.StringUtil;

import java.util.Properties;
import java.util.Set;
import org.cdlib.mrt.utility.PropertiesUtil;




/**
 * Cloud Properties - case insensitive Properties
 * @author dloy
 */
public class CloudProperties
{
    private Properties prop = new Properties();
    
    public CloudProperties() { }
    public CloudProperties(Properties localProp)
    {
        Set keys = localProp.keySet();
        for(Object keyO : keys) {
            String key = (String)keyO;
            String value = localProp.getProperty(key);
            setProperty(key, value);
        }
    }
    
    public Properties buildMetaProperties()
    { 
        Properties metaProp = new Properties();
        Set keys = prop.keySet();
        for(Object keyO : keys) {
            String key = (String)keyO;
            String metaKey = getMetaKey(key);
            String value = prop.getProperty(key);
            metaProp.setProperty(metaKey, value);
        }
        return metaProp;
        
    }
    
    public static String getMetaKey(String key)
    {
        key = key.toLowerCase();
        key = StringUtil.upperCaseFirst(key);
        key = "X-Object-Meta-" + key;
        return key;
    }
    
    public void setFromMetaProperties(Properties metaProp)
    {
        Set metaKeys = metaProp.keySet();
        for(Object keyO : metaKeys) {
            String key = (String)keyO;
            String value = metaProp.getProperty(key);
            key = key.toLowerCase();
            String localKey = null;
            if (key.startsWith("x-object-meta-")) {
                localKey = key.substring(14);
            } else if (key.startsWith("header.x-object-meta-")) {
                localKey = key.substring(21);
            }
            if (localKey != null) {
                prop.setProperty(localKey, value);
            }
        }
    }
    
    public void setProperty(String key, String value)
    {
        if (StringUtil.isEmpty(key)) return;
        if (StringUtil.isEmpty(value)) return;
        prop.setProperty(key.toLowerCase(), value);
    }
    
    public String getProperty(String key)
    {
        if (StringUtil.isEmpty(key)) return null;
        return prop.getProperty(key.toLowerCase());
    }
    
    public Properties getProperties()
    {
        return prop;
    }
    
    public int size()
    {
        return prop.size();
    }
    
    public void clear()
    {
        prop.clear();
    }
    
    public String toString()
    {
        return prop.toString();
    }
    
    public String dump(String header)
    {
        return PropertiesUtil.dumpProperties(header, prop);
    }
    
    public Set getKeySet()
    {
        return prop.keySet();
    }
}

