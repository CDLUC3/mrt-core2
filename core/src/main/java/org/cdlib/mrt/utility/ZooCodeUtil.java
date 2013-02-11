/*
Copyright (c) 2011, Regents of the University of California
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


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;



/**
 * Basic manager for Queuing Service
 * @author mreyes
 */
public class ZooCodeUtil
{

    private static final String NAME = "ZooCodeUtil";
    private static final String MESSAGE = NAME + ": ";
    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = true;


    public static void setProp(Properties prop, String key, String value)
    {
        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) return;
        prop.setProperty(key, value);
    }
    
    public static byte[] encodeItem(Properties row)
       throws TException
    {
        try {
            if ((row == null) || (row.size() == 0)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "encodeItem - missing row");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream(100000);
            encodeItem(row, baos);
            return baos.toByteArray();

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    public static void encodeItem(Object object, OutputStream outStream)
        throws TException
    {
       try {
            final XMLEncoder encoder = new XMLEncoder(outStream);
            encoder.writeObject(object);
            encoder.close();

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            try {
                outStream.close();
            } catch (Exception ex) { }
        }
    }

   public static Properties decodeItem(byte[] bytes)
       throws TException
   {
        try {
            if ((bytes == null) || (bytes.length == 0)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "decodeItem - missing bytes");
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Properties row = (Properties)decodeItem(bais);
            return row;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
   }

   public static Object decodeItem(InputStream inStream)
       throws TException
   {
       try {

              // Use XMLDecoder to read the same XML file in.
              final XMLDecoder decoder = new XMLDecoder(inStream);
              Object object = decoder.readObject();
              decoder.close();
              return object;


        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            try {
                inStream.close();
            } catch (Exception ex) { }
        }
    }
   
    
    public static String testBase(String base)
        throws TException
    {
        if (StringUtil.isAllBlank(base)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "queue zookeeper base reauired");
        }
        if (!base.startsWith("/")) base = "/" + base;
        return base;
    }
   
   public static String printStatus(byte status)
   {
       int statusI = status;
       switch (status) {
           case 0: return "PENDING";
           case 1: return "CONSUMED";
           case 2: return "DELETED";
           case 3: return "FAILED";
           case 4: return "COMPLETED";
           default: return "UNKNOWN:" + statusI;
       }
   }
   
}
