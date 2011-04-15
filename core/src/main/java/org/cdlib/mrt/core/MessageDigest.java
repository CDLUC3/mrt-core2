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
package org.cdlib.mrt.core;

import java.io.Serializable;
import org.cdlib.mrt.utility.MessageDigestValue;
import org.cdlib.mrt.utility.MessageDigestType;
import org.cdlib.mrt.utility.StateStringInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TExceptionEnum;
import org.cdlib.mrt.utility.TRuntimeException;
import org.cdlib.mrt.utility.StringUtil;
/**
 * Conainer for a Message Digest including checksum type and checksum value
 * @author dloy
 */
public class MessageDigest
        implements Serializable,StateStringInf
{

    protected String value;
    protected MessageDigestType algorithm = null;
    protected boolean debugDump = false;
    public void setDebugDump(boolean debugDump) {
        this.debugDump = debugDump;
    }

    /**
     * Constructor
     * @param value checksum value
     * @param algorithm checksum type
     */
    public MessageDigest(String value, MessageDigestType algorithm)
            throws TException
    {
        if (StringUtil.isEmpty(value) || (algorithm == null)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "MessageDigest value or algorithm is invalid: value=" + value
                    + " - algorithm=" + algorithm);
        }
        this.value = value;
        this.algorithm = algorithm;
    }

    /**
     * Constructor
     * This form of the constructor requires a supported form of the checksum type after
     * normalization. If no supported form is found then a runtime exception is thrown
     * @param value checksum value
     * @param algorithmS String form of checksum type
     */
    public MessageDigest(String value, String algorithmS)
        throws TException
    {
        if (debugDump) {
            System.out.println("MessageDigest entered value=" + value
                        + " - algorithm=" + algorithmS);
        }
        if (StringUtil.isEmpty(value) || StringUtil.isEmpty(algorithmS)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "MessageDigest value or algorithm is invalid: value=" + value
                    + " - algorithm=" + algorithmS);
        }
        this.algorithm = getAlgorithm(algorithmS);
        if (this.algorithm == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "MessageDigest algorithm is not supported: algorithm=" + algorithmS);
        }
        this.value = value;
    }

    public static MessageDigestType getAlgorithm(String algorithmS)
    {
        if (StringUtil.isEmpty(algorithmS)) {
            return null;
        }
        return MessageDigestValue.getAlgorithm(algorithmS);
    }

    /**
     * Return checksum value
     * @return checksum value
     */
    public String getValue() {
        return value;
    }

    /**
     * Return checksum type
     * @return checksum type
     */
    public MessageDigestType getAlgorithm() {
        return algorithm;
    }

    /**
     * Return checksum type as used by Java
     * @return Java checksum type
     */
    public String getJavaAlgorithm() {
        if (algorithm == null) return null;
        return algorithm.getJavaAlgorithm();
    }

    /**
     * Matches one message digest against another
     * @param in message digest to be matched against this message digest
     * @return true=match, false=no match
     */
    public boolean equals(MessageDigest in) {
        if (in == null) return false;
        if (this.algorithm != in.algorithm) return false;
        if (StringUtil.isEmpty(in.value) && StringUtil.isEmpty(this.value)) return true;
        if (this.value.equals(in.value)) return true;
        return false;
    }

    @Override
    public String toString()
    {
        return getAlgorithm() + "=" + getValue();
    }

    public static void main(String args[])
    {
        try {
            MessageDigest digest1 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", MessageDigestType.sha1);
            MessageDigest digest2 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", MessageDigestType.sha1);
            if (digest1.equals(digest2)) {
                System.out.println("digests1 and 2 equal");
            } else {
                System.out.println("digests1 and 2 NOT equal");
            }
            MessageDigest digest3 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", MessageDigestType.sha256);
            MessageDigest digest4 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", "sHa256");
            if (digest3.equals(digest4)) {
                System.out.println("digests 3 and 4 equal");
            } else {
                System.out.println("digests 3 and 4 NOT equal");
            }
            MessageDigest digest5 = new MessageDigest("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12", "shaxxx");
        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            System.out.println("Trace:" + StringUtil.stackTrace(ex));
        }
    }
}
