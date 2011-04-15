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
import org.cdlib.mrt.utility.StateStringInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * General digital identifier
 * @author dloy
 */
public class Identifier
        implements Serializable, StateStringInf
{


    /**
     * Supported Identifier types
     */
    public enum Namespace {
        ARK, DOI, Handle, Local, URI, URL, URN, Unspecified
    }
    protected String value = null;
    protected Namespace namespace = Namespace.Unspecified;

    /**
     * Constructor
     * @param value identifier value
     * @param namespace identifier type
     */
    public Identifier(String value, Namespace namespace)
        throws TException
    {
        if (StringUtil.isEmpty(value) || (namespace == null)) {
            throw new TException.INVALID_OR_MISSING_PARM("Identifier parameter missing");
        }
        this.value = value;
        this.namespace = namespace;
    }

    /**
     * Constructor - Namespace default is ARK
     * @param value identifier value
     */
    public Identifier(String value)
        throws TException
    {
        if (StringUtil.isEmpty(value)) {
            throw new TException.INVALID_OR_MISSING_PARM("Identifier parameter missing");
        }
        this.value = value;
        this.namespace = Namespace.ARK;
    }

    /**
     * Return identifier value
     * @return identifier value
     */
    public String getValue() {
        return value;
    }

    /**
     * Return identifier type
     * @return identifier type
     */
    public Namespace getNamespace() {
        return namespace;
    }


    @Override
    public String toString()
    {
        return getValue();
    }
}
