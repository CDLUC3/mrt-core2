/*
Copyright (c) 2005-2012, Regents of the University of California
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
package org.cdlib.mrt.cloud;

import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.LinkedHashMap;
import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.XMLUtil;

/**
 * This object imports the formatTypes.xml and builds a local table of supported format types.
 * Note, that the ObjectFormat is being deprecated and replaced by a single format id (fmtid).
 * This change is happening because formatName is strictly a description and has no functional
 * use. The scienceMetadata flag is being dropped because the ORE Resource Map is more flexible
 * and allows for a broader set of data type.
 * 
 * @author dloy
 */
public class ManifestStr
    extends DefaultHandler
{
    private static final String NAME = "ManifestStr";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUGLOW = false;
    private static final boolean DEBUGHIGH = true;
    private static final String head = "<?xml version=\"1.1\"?>"
            + NL + "<objectInfo xmlns=\"http://uc3.cdlib.org/ontology/mrt/manifest\">";
    private static final String tail = "objectInfo";
    
    private int lvl = 0;
    protected VersionMap versionMap = null;
    protected OutputStream outStream = null;
    
    public static void buildManifest(VersionMap versionMap, OutputStream outStream)
        throws TException
    {
        ManifestStr manifestStr = new ManifestStr(versionMap, outStream);
    }
    
    public static void buildManifest(VersionMap versionMap, File outFile)
        throws TException
    {
        ManifestStr manifestStr = new ManifestStr(versionMap, outFile);
    }
    
    public ManifestStr(VersionMap versionMap, OutputStream outStream)
        throws TException
    {
        this.versionMap = versionMap;
        this.outStream = outStream;
        process();
    }
    
    public ManifestStr(VersionMap versionMap, File outFile)
        throws TException
    {
        try {
            this.versionMap = versionMap;
            this.outStream = new FileOutputStream(outFile);
            process();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected String enc(String in)
    {
        try {
            return XMLUtil.encodeValue(in);
        } catch (Exception ex) {
            return in;
        }
    }
    
    protected void out(String in)
    {
        try {
            byte[] bytes = in.getBytes("utf-8");
            outStream.write(bytes);
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.toString());
        }
    }
    
    protected void addEle(String ele, String id)
    {
        addlvl();
        out("<" + ele);
        if (StringUtil.isNotEmpty(id)) {
            id = enc(id);
            out(" id=\"" + id + "\"");
        }
        out(">\n");
        lvl += 1;
    }
    
    protected void addEle(String ele, LinkedHashMap<String, String> map)
    {
        addlvl();
        out("<" + ele );
        for (String attr : map.keySet()) {
            String attS = map.get(attr).toString();
            attS = enc(attS);
            out(" " + attr + "=\"" + attS + "\"");
        }
        out(">\n");
        lvl += 1;
    }
    
    protected void addSimple(String ele, String value)
        throws TException
    {
        if (StringUtil.isAllBlank(value)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "Required element missing:" + value);
        }
        value = enc(value);
        addlvl();
        out("<" + ele + ">" + value + "</" + ele + ">\n");
    }
    
    protected void endEle(String ele)
    {
        lvl -= 1;
        addlvl();
        out("</" + ele + ">\n");
    }
    
    protected void addlvl()
    {
        for (int i=0; i < lvl; i++)
        {
            out("  ");
        }
    }
    
    private void process()
        throws TException
    {
        try {
            addHeader();
            addObject();
            addVersions();
            addTail();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception ex) { }
            }
        }
    }
    
    protected void addHeader()
    {   
        out(head + "\n");
        lvl += 1;
    }
    
    protected void addTail()
    {   
        endEle(tail);
    }
    
    protected void addObject()
        throws TException
    {
        if (versionMap.getObjectID() != null) addEle("object", versionMap.getObjectID().getValue());
        else addEle("object", (String)null);
        addSimple("current", "" + versionMap.getCurrent());
        addSimple("fileCount", "" + versionMap.getTotalCnt());
        addSimple("totalSize", "" + versionMap.getTotalSize());
        addSimple("actualCount", "" + versionMap.getActualCnt());
        addSimple("actualSize", "" + versionMap.getActualSize());
        addSimple("versionCount", "" + versionMap.getVersionCount());
        DateState date = versionMap.getLastAddVersion();
        if (date != null) {
            addSimple("lastAddVersion", date.getIsoDate());
        }
        date = versionMap.getLastDeleteVersion();
        if (date != null) {
            addSimple("lastDeleteVersion", date.getIsoDate());
        }
        endEle("object");
    }
    
    protected void addVersions()
        throws TException
    {
        if (versionMap.getVersionCount() == 0) return;
        addEle("versions", (String)null);
        for (int i=0; i < versionMap.getVersionCount(); i++) {
            ManInfo info = versionMap.getManInfo(i);
            addEle("version", "" + info.versionID);
            LinkedHashMap<String, String> manAttr = new LinkedHashMap();
            manAttr.put("count", "" + info.count);
            manAttr.put("size", "" + info.size);
            manAttr.put("created", "" + info.created.getIsoDate());
            addEle("manifest", manAttr);
            ComponentContent content = info.components;
            List<FileComponent> components = content.getFileComponents();
            for(FileComponent component : components) {
                addComponent(component);
            }
            endEle("manifest");
            endEle("version");
        }
        endEle("versions");
    }
    
    protected void addComponent(FileComponent component)
        throws TException
    {
        addEle("file", component.getIdentifier());
        
        MessageDigest digest = component.getMessageDigest();
        if (digest != null) {
            addSimple("digestType", digest.getJavaAlgorithm());
            addSimple("digest", digest.getValue());
        }
        addSimple("size", "" + component.getSize());
        DateState created = component.getCreated();
        if (created != null) addSimple("creationDate", created.getIsoDate());
        if (component.getMimeType() != null) {
            addSimple("mimeType", component.getMimeType());
        }
        addSimple("key", component.getLocalID());
        endEle("file");
    }
}