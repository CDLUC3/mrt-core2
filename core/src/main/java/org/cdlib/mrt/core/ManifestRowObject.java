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

import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;

/**
 * <pre>
 * Dflat Object manifest row support
 * This routine is used in conjunction with Manifest for parsing the manifest
 * process line. This manifest is saved in Storage
 *
 * For this format this consists of:
 * file name
 * checksum type
 * checksum
 * file size
 * last modified date String (or -) if not spplied
 * </pre>
 *
 * @author  David Loy
 */
public class ManifestRowObject
        extends ManifestRowCheckmAbs
        implements ManifestRowInf, FileComponentContentInf

{
    protected static final String NAME = "ManifestRowObject";
    protected static final String MESSAGE = NAME + ": ";

    protected static final String[] cols = {
        "#%prefix | nfo:<" + Checkm.Prefix.nfo.toString() + ">",
        "#%prefix | nie:<" + Checkm.Prefix.nie.toString() + ">",
        "#%prefix | mrt:<" + Checkm.Prefix.mrt.toString() + ">",
        "#%columns | nfo:fileName | nfo:hashAlgorithm | nfo:hashValue | nfo:fileSize | nfo:fileLastModified | nie:mimeType"
    };


    protected static final String[] profiles = {
        Checkm.REGISTRY + "store/manifest/mrt-object-manifest",
        "http://uc3.cdlib.org/registry/mrt/mrt-object-manifest"
    };

    public ManifestRowObject(LoggerInf logger)
        throws TException
    {
        super(logger, profiles, cols);
    }

    public ManifestRowObject(LoggerInf logger, Checkm checkm)
        throws TException
    {
        super(logger, checkm);
    }

    @Override
    public ManifestRowCheckmAbs getManifestRow()
            throws TException
    {
        if (checkm == null) {
            throw new TException.INVALID_CONFIGURATION("getManifestRow - checkm missing");
        }
        ManifestRowObject addRow = new ManifestRowObject(m_logger, checkm);
        return addRow;
    }

    public String [] getColumns()
    {
        return cols;
    }

      public String [] getProfiles()
    {
        return profiles;
    }
}
