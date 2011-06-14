package org.cdlib.mrt.utility;
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

import java.util.Enumeration;
import java.util.Properties;
/**
 *
 * @author  David Loy
 */
public class PropertiesMapper
{
    protected Properties map = new Properties();

    public PropertiesMapper(String prefix, Properties inputProperties)
    {
        if (inputProperties == null) return;

        Enumeration e = inputProperties.propertyNames();
        while( e.hasMoreElements() )
        {
           String key = (String)e.nextElement();
           if (!key.startsWith(prefix)) continue;
           String value = inputProperties.getProperty(key);
           String newKey = key.substring(prefix.length());
           map.put(newKey, value);
        }
    }

    public String match(String path)
    {
        Enumeration e = map.propertyNames();
        System.out.println("test=" + path);
        while( e.hasMoreElements() )
        {
           String key = (String)e.nextElement();
           String value = map.getProperty(key);
           if (path.matches(value)) {
               return key;
           }
        }
        return null;
    }
    
    public static void main(String args[])
    {
        Properties prop = new Properties();
        prop.setProperty("HandlerMap.ADD", ".*/add/.*");
        prop.setProperty("Flowers", "/delete/xxx");
        prop.setProperty("HandlerMap.STARTDELETE", "^/delete/.*");
        prop.setProperty("HandlerMap.DELETE", ".*/delete/.*");
        prop.setProperty("bingo", "bingo");

        PropertiesMapper propertiesMapper = new PropertiesMapper("HandlerMap", prop);
        propertiesMapper.dump();
        propertiesMapper.testMatch("/add/bbb/ccc");
        propertiesMapper.testMatch("/aaa/add/ccc");
        propertiesMapper.testMatch("/aaa/bbb/add");
        propertiesMapper.testMatch("/bingo/stuff");
        propertiesMapper.testMatch("/bingo/stuff/delete/");
        propertiesMapper.testMatch("/delete/it/");
    }

    public void testMatch(String value)
    {
       String result = match(value);
       if (result == null) {
           System.out.println("No match: " + value);
       } else {
           System.out.println("match: " + value
                   + " - key=" + result
                   );
       }
    }

    public void dump()
    {

        Enumeration e = map.propertyNames();
        while( e.hasMoreElements() )
        {
           String key = (String)e.nextElement();
           String value = map.getProperty(key);
           System.out.println("ADD: "
                   + " - key=" + key
                   + " - value=" + value
                   );
        }
    }
}
