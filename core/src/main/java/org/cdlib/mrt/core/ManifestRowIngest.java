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
 * Ingest manifest row support
 * This routine is used in conjunction with Manifest for parsing the manifest
 * process line. This manifest is passed as part of an ingest process.
 *
 * For this format this consists of:
 * URL for accessing file
 * checksum type
 * checksum
 * file size
 * last modified date String (or -) if not spplied
 * file name
 * mimeType
 * </pre>
 *
 * @author  David Loy
 */
public class ManifestRowIngest
        extends ManifestRowCheckmAbs
        implements ManifestRowInf, FileComponentContentInf

{
    protected static final String NAME = "ManifestRowIngest";
    protected static final String MESSAGE = NAME + ": ";


    protected static final String[] cols = {
        "#%prefix | nfo:<" + Checkm.Prefix.nfo.toString() + ">",
        "#%prefix | nie:<" + Checkm.Prefix.nie.toString() + ">",
        "#%prefix | mrt:<" + Checkm.Prefix.mrt.toString() + ">",
        "#%columns | nfo:fileURL | nfo:hashAlgorithm | nfo:hashValue | nfo:fileSize | nfo:fileLastModified | nfo:fileName | nie:mimeType"
    };


    protected static final String[] profiles = {
        Checkm.REGISTRY + "ingest/manifest/mrt-ingest-manifest",
        "http://uc3.cdlib.org/registry/mrt/mrt-ingest-manifest"
    };

    public ManifestRowIngest(LoggerInf logger)
        throws TException
    {
        super(logger, profiles, cols);
    }

    public ManifestRowIngest(LoggerInf logger, Checkm checkm)
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
        ManifestRowIngest addRow = new ManifestRowIngest(m_logger, checkm);
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
