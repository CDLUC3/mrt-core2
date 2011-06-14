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

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.security.Base64;

/**
 * From: http://javaalmanac.com/egs/javax.crypto/DesString.html
 * @author  David Loy
 */
public class DesEncrypter 
{
    Cipher ecipher;
    Cipher dcipher;
    
    // 8-byte Salt
    byte[] salt = {
            (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
            (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };
    
    // Iteration count
    int iterationCount = 19;
    
    DesEncrypter(String passPhrase)
            throws TException
    {
        try {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(
                    "PBEWithMD5AndDES").generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());
    
            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
    
            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
    
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
        
    DesEncrypter(SecretKey key)
        throws TException
    {       
        try {
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
    
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    
    public String encrypt(String str)
        throws TException
    {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");
    
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
    
            // Encode bytes to base64 to get a string
            return Base64.encodeBytes(enc);
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    public String decrypt(String str)
        throws TException
    {
        try {
            // Decode base64 to get bytes
            byte[] dec = Base64.decode(str);
    
            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);
    
            // Decode using utf-8
            return new String(utf8, "UTF8");
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
}