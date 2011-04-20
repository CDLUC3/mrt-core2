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
public class XMLUtil
{

    /**
     * Replace XML special characters with Entity references
     * @params xmlString input string
     * @return encoded string
     */
    public static String encodeValue (String xmlString)
        throws TException
    {
            try {
                xmlString = xmlString.replace("&", "&amp;");
                xmlString = xmlString.replace("<", "&lt;");
                xmlString = xmlString.replace(">", "&gt;");
                xmlString = xmlString.replace("'", "&apos;");
                xmlString = xmlString.replace("\"", "&quot;");

            } catch (Exception e) {
                throw new TException (TExceptionEnum.GENERAL_EXCEPTION, "XMLUtil.encodeXML: Error in encoding XML: " + xmlString);
            }

        return xmlString;
    }

    /**
     * Replace XML special characters with Entity references
     * @params xmlString input string
     * @return encoded string
     */
    public static String encodeName (String xmlString)
        throws TException
    {
            String encodedString = null;
            try {
                encodedString = xmlString.replace("$", ".");
                char c = 0;
                StringBuffer buf = new StringBuffer(xmlString.length());
                String match = "._-"
                            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                            + "abcdefghijklmnopqrstuvwxyz"
                            + "0123456789";
                for (int i=0; i< encodedString.length(); i++) {
                    c = encodedString.charAt(i);
                    if (match.indexOf(c) < 0) {
                        buf.append("__");
                    } else buf.append(c);
                }
                return buf.toString();

            } catch (Exception e) {
                throw new TException (TExceptionEnum.GENERAL_EXCEPTION, "XMLUtil.encodeXML: Error in encoding XML: " + xmlString);
            }
    }


    /**
     * Replace Entity references with XML special characters
     * @params xmlString input string
     * @return decoded string
     */
    public static String decode (String xmlString)
        throws TException
    {
            String encodedString = xmlString;
            try {
                encodedString = encodedString.replace("&amp;", "&");
                encodedString = encodedString.replace("&lt;", "<");
                encodedString = encodedString.replace("&gt;", ">");
                encodedString = encodedString.replace("&apos;", "'");
                encodedString = encodedString.replace("&quot;", "\"");

            } catch (Exception e) {
                throw new TException.GENERAL_EXCEPTION( "XMLUtil.encodeXML: Error in encoding XML: " + xmlString);
            }

        return encodedString;
    }

}
