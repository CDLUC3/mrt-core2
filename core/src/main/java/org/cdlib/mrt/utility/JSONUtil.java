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

/**
 * This class is a collection of XML Utility functions.
 * They are all static and so can be called without instanciating this class.
 *
 * @author  David Loy
 */
public class JSONUtil
{
    /**
     * Replace XML special characters with Entity references
     * @params xmlString input string
     * @return encoded string
     */
    public static String encode(String text)
        throws TException
    {
        char c = '\0';
        if (text == null) return null;
        StringBuffer buf = new StringBuffer(text.length() * 3);
        for (int i=0; i < text.length(); i++) {
            c = text.charAt(i);
            switch (c) {
                case '\\': buf.append("\\\\"); break;
                case '"': buf.append('\\'+ '"'); break;
                default: buf.append(c);
            }
        }
        return buf.toString();
    }
   

    /**
     * Replace quote and escape in JSON string
     * @params text input string
     * @return decoded string
     */
    public static String decode(String text)
        throws TException
    {
            String encodedString = null;
            try {
                encodedString = text.replace("\\\\", "\\");
                encodedString = text.replace("\\\"", "\"");

            } catch (Exception e) {
                throw new TException.GENERAL_EXCEPTION( "XMLUtil.encodeXML: Error in encoding XML: " + text);
            }

        return encodedString;
    }

}
