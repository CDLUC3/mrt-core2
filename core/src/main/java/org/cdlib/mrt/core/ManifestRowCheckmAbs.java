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
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * <pre>
 * Dflat POST manifest row support
 * This routine is used in conjunction with Manifest for parsing the manifest
 * process line. This manifest is passed as part of a POST addVersion process.
 *
 * For this format this consists of:
 * URL for accessing file
 * checksum type
 * checksum
 * file size
 * last modified date String (or -) if not spplied
 * file name
 * </pre>
 *
 * @author  David Loy
 */
public abstract class ManifestRowCheckmAbs
        extends ManifestRowAbs
        implements ManifestRowInf, FileComponentContentInf

{
    protected static final String NAME = "ManifestRowCheckmAbs";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = false;

    protected Checkm checkm = null;
    protected FileComponent fileComponent = null;

    public ManifestRowCheckmAbs(LoggerInf logger, Checkm checkm)
        throws TException
    {
        super(logger);
        if (checkm == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "missing checkm");
        }
        this.checkm = checkm;
    }

    public ManifestRowCheckmAbs(
            LoggerInf logger,
            String [] profiles,
            String [] cols)
        throws TException
    {
        super(logger);
        String[] headers = getHeaders(profiles, cols);
        this.checkm = new Checkm(logger, headers);
    }

    public ManifestRowCheckmAbs(
            String outputProfile,
            LoggerInf logger,
            String [] profiles,
            String [] cols)
        throws TException
    {
        super(logger);
        String[] headers = getHeaders(outputProfile, profiles, cols);
        if (DEBUG) dumpHeaders("ManifestRowCheckmAbs headers", headers);
        this.checkm = new Checkm(logger, headers);
    }

    protected void dumpHeaders(String msg, String[] headers)
    {
        System.out.println(msg);
        for (String header : headers) {
            System.out.println(header);
        }
    }
    @Override
    public FileComponent getFileComponent() {
        return fileComponent;
    }

    @Override
    public void setFileComponent(FileComponent fileState) {
        this.fileComponent = fileState;
    }

    public void handleHeaders(String [] comments)
        throws TException
    {
        //System.out.println("ManifestRowCheckmAbs handleHeaders entered");
        String [] profiles = getProfiles();
        checkm.handleHeaders(comments, profiles);
    }

    @Override
    public void setRow(String line)
            throws TException
    {
        fileComponent = new FileComponent();
        checkm.setRow(fileComponent, line);
        if (DEBUG) System.out.println(fileComponent.dump("ManifestRowCheckmAbs-setRow"));
    }

    @Override
    public String getLine()
        throws TException
    {
        return checkm.getLine(fileComponent);
    }

    @Override
    public void handleEOF(String prevLine)
        throws TException
    {

        if (prevLine == null) {
            String msg = MESSAGE + "Empty manifest";
            throw new TException.INVALID_DATA_FORMAT(msg);

        }
        prevLine = prevLine.toLowerCase();
        if (prevLine.startsWith("#%eof")) {

        } else {
            String msg = MESSAGE + "EOF without #%EOF terminator header"
                    + " - prevLine=" + prevLine
                    ;
            throw new TException.INVALID_DATA_FORMAT(msg);
        }
    }

    public ManifestRowCheckmAbs getManifestRow(String line)
            throws TException
    {
        if (checkm == null) {
            throw new TException.INVALID_CONFIGURATION("getManifestRow - checkm missing");
        }
        ManifestRowCheckmAbs addRow = getManifestRow();
        addRow.setRow(line);
        return addRow;
    }


    @Override
    public String [] getHeaders()
    {
        String [] profiles = getProfiles();
        return getHeaders(profiles[0]);
    }

    public String [] getHeaders(String profile)
    {
        String [] columns = getColumns();
        String [] out = new String[columns.length + 2];
        out[0] = Checkm.CHECKMHD;
        out[1] = Checkm.PROFILE + " | " + profile;
        for (int i=0; i < columns.length; i++) {
            out[i+2] = columns[i];
        }
        return out;
    }

    public static String[] getHeaders(
            String [] profiles,
            String [] columns)
    {
        String [] out = new String[columns.length + 2];
        out[0] = Checkm.CHECKMHD;
        out[1] = Checkm.PROFILE + " | " + profiles[0];
        for (int i=0; i < columns.length; i++) {
            out[i+2] = columns[i];
        }
        return out;
    }

    @Override
    public String [] getOutputHeaders()
    {
        return checkm.getSaveComments();
    }

    public static String[] getHeaders(
            String outputProfile,
            String [] profiles,
            String [] columns)
        throws TException
    {
        String setProfile = profiles[0];
        if (StringUtil.isNotEmpty(outputProfile)) {
            boolean match = false;
            for (String profile : profiles) {
                if (DEBUG) System.out.println("-profile=" + profile + " - setProfile=" + setProfile);
                if (outputProfile.equals(profile)) match = true;
            }
            if (!match) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "getHeader - profile is invalid");
            }
            setProfile = outputProfile;
        }
        String [] out = new String[columns.length + 2];
        out[0] = Checkm.CHECKMHD;
        out[1] = Checkm.PROFILE + " | " + setProfile;
        for (int i=0; i < columns.length; i++) {
            out[i+2] = columns[i];
        }
        return out;
    }

    public String getProfile()
    {
        return checkm.getLocalProfile();
    }

    public abstract ManifestRowCheckmAbs getManifestRow()
            throws TException;

    public abstract String[] getColumns();

    public abstract String[] getProfiles();

}
