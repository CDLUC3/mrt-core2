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

package org.cdlib.mrt.security;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.security.DesEncrypter;

/**
 * 
 * @author  David Loy
 */
public class SecurityUtil 
{


        
    /**
     * Do an MD5 encryption on a string
     * <br>Note that the encryption does utf-8 conversion of password to byte array
     * @param password string to encrypt
     * @return encrypted String
     */
    public static String encrypt(String password)
        throws TException
    {
        return encrypt(password, "MD5");
    }    
    
    
    /**
     * Do an MD5 encryption on a string
     * <br>Note that the encryption does utf-8 conversion of password to byte array
     * @param password string to encrypt
     * @param algorithm encryption algorithm type: MD5
     * @return encrypted String
     */
    public static String encrypt(String password, String type)
        throws TException
    {
        String retval = null;
        
        try {
            byte [] byteArr = password.getBytes("utf-8");
            java.security.MessageDigest md = MessageDigest.getInstance ( type ) ;
            md.update ( byteArr );
            byte[] digest = md.digest() ;
            retval = Base64.encodeBytes(digest);
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
        return retval;
    }    
    
    /**
     * Use DES to encrypt a string
     * @param text text to be DES encrypted
     * @return encrypted String
     */
    public static String desEncrypt(String text, String tkey)
        throws TException
    {
    try {
        
        // Create encrypter/decrypter class
        DesEncrypter encrypter = new DesEncrypter(tkey);
    
        // Encrypt
        return encrypter.encrypt(text);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }        
    
    
    /**
     * Use DES to decrypt a string
     * @param text encrypted string
     * @return decrypted String
     */
    public static String desDecrypt(String text, String tkey)
        throws TException
    {
    try {
        
        // Create encrypter/decrypter class
        DesEncrypter encrypter = new DesEncrypter(tkey);
    
        // Decrypt
        return encrypter.decrypt(text);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    public static String encodeBase64(InputStream inputStream)
        throws TException
    {
        try {

	    // Read input stream into a byte array outputstream in 2k chunks
	    byte [] byteArray =new byte[2048];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((inputStream.read(byteArray)) != -1) {
                byteArrayOutputStream.write(byteArray);
            }

            // Convert to base64 
	    return encodeBase64(byteArrayOutputStream.toByteArray());

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    public static String encodeBase64(byte[] byteArray)
        throws TException
    {
        try {

            // Convert to base64 
            return Base64.encodeBytes(byteArray);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public static String encodeBase64(String str)
        throws TException
    {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encode bytes to base64 to get a string
            return Base64.encodeBytes(utf8);
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    
    public static String decodeBase64(String str)
        throws TException
    {
        try {
            // Decode base64 to get bytes
            byte[] dec = Base64.decode(str);
    
            // Decode using utf-8
            return new String(dec, "UTF8");
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
}
