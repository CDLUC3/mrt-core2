/*
Copyright (c) 2005-2006, Regents of the University of California
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
*********************************************************************/
package org.cdlib.mrt.core;

import org.cdlib.mrt.utility.StateInf;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import java.io.InputStream;
import java.util.Enumeration;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

public class ComponentContent
        implements StateInf, Serializable
{
    protected LinkedHashMap<String, FileComponent> componentTable = new LinkedHashMap<String, FileComponent>(200);
    protected int status = -1;
    protected String statusMessage = null;
    protected int versionID = 0;
    protected Identifier objectID = null;
    protected LoggerInf logger = null;

    protected static final boolean debugDump = false;

    public ComponentContent(LoggerInf logger,
            Manifest manifest,
            InputStream manifestInputStream)
        throws TException
    {
        this.logger = logger;
        buildComponents(manifest, manifestInputStream);
    }

    public LinkedHashMap<String, FileComponent> getFileComponentTable()
    {
        return componentTable;
    }

    public Vector<FileComponent> getFileComponents()
    {
        Vector<FileComponent> fileList = new Vector(componentTable.size());
        Set<String> fileSet = componentTable.keySet();
        for (String key : fileSet) {
            if (StringUtil.isEmpty(key)) continue;
            FileComponent fileComponent = componentTable.get(key);
            if (fileComponent == null) continue;
            fileList.add(fileComponent);
        }
        return fileList;
    }

    public void addFileComponent(String key, FileComponent fileComponent)
    {
        if (StringUtil.isEmpty(key)) return;
        componentTable.put(key, fileComponent);
    }

    public FileComponent getFileComponent(String key)
    {
        if (StringUtil.isEmpty(key)) return null;
        return componentTable.get(key);
    }
    /**
     * From a post manifest InputStream
     * Return a VersionContent object
     * @param logger process logger
     * @param manifest post type manifest
     * @param manifestInputStream InputStream to manifest
     * @return VersionContent object defining a file content data
     * @throws TException process excepton
     */
    public void buildComponents(
            Manifest manifest,
            InputStream manifestInputStream)
        throws TException
    {
        try {
            Enumeration<ManifestRowInf> enumRow = manifest.getRows(manifestInputStream);
            FileComponentContentInf rowIn = null;
            FileComponent fileComponent = null;

            //ObjectManifest versionManifest = getDflatManifest(fullDirectory);
            while (enumRow.hasMoreElements()) {
                rowIn = (FileComponentContentInf)enumRow.nextElement();
                fileComponent = rowIn.getFileComponent();
                if (debugDump) {
                    System.out.println(fileComponent.dump("getVersionContent"));
                }
                addFileComponent(fileComponent.getIdentifier(), fileComponent);

            }
            return;

        } catch (Exception ex) {
            System.out.println("!!!!Stack trace:" + StringUtil.stackTrace(ex));
            throw makeGeneralTException(logger, "Unable to build VersionContent.", ex);

        } finally {
            try {
                manifestInputStream.close();
            } catch (Exception ex) {

            }
        }
    }

    /**
     * create TException and do appropriate logger
     * @param logger process logger
     * @param msg error message
     * @param ex encountered exception to convert
     * @return TException
     */
    public TException makeGeneralTException(
            LoggerInf logger,
            String msg,
            Exception ex)
    {
        logger.logError(msg + ex,
                LoggerInf.LogLevel.UPDATE_EXCEPTION);
        logger.logError(msg + " - trace:"
                + StringUtil.stackTrace(ex),
                LoggerInf.LogLevel.DEBUG);
        return new TException.GENERAL_EXCEPTION(
                msg +  "Exception:" + ex);
    }
}

