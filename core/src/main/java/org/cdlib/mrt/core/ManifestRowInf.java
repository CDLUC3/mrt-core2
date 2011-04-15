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

import org.cdlib.mrt.utility.TException;

/**
 *
 * @author  David Loy
 */
public interface ManifestRowInf
{
    /**
     * Process Manifest line.
     * This line is passed from Manifest. This method parses the content of
     * this Manifest line and saves it locally
     * @param line a line delimited String extracted from a Manifest
     * @throws TException process exception
     */
    public void setRow(String line)
            throws TException;

    /**
     * Build a Manifest line for output to a manifest file
     * @return Manifest line
     * @throws TException process exception
     */
    public String getLine()
            throws TException;

    /**
     * Get ManifestRow object based on passed line
     * @param line Manifest line
     * @return ManifestRow object built from this line
     * @throws TException process exception
     */
    public ManifestRowInf getManifestRow(String line)
            throws TException;

    /**
     * Any specialized EOF handling required.
     * e.g. The last line of a Post manifest is #@EOF
     * @param prevLine last line of manifest
     * @throws TException process exception
     */
    public void handleEOF(String prevLine)
            throws TException;

    /**
     * Process all manifest headers beginning with the form #%
     * The headers exist before any processed manifest line
     * @param headers array of Strings containing each identified header
     * @throws TException
     */
    public void handleHeaders(String [] headers)
            throws TException;

    /**
     * Return the checkm headers
     * @return checkm headers
     */
    public String [] getHeaders();


    /**
     * Return the checkm headers
     * @return checkm headers
     */
    public String [] getOutputHeaders();

    /**
     * If profile associated with this manifest then return value
     * @return manifest profile
     */
    public String getProfile();
}
