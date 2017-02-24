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
*********************************************************************/
package org.cdlib.mrt.cloud;
import org.cdlib.mrt.utility.StringUtil;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.MessageDigest;




/**
 * Cloud Properties - case insensitive Properties
 * @author dloy
 */
public class CloudList
{
    private ArrayList<CloudEntry> list = new ArrayList();
    
    public CloudList() { }
    public void add(String container,String key,long size,String etag, String contentType, String lastModified)
    {
        CloudEntry entry = new CloudEntry(container, key, size, etag, contentType, lastModified);
        list.add(entry);
    }
    public void add(
            String container,
            String key,
            long size,
            String etag, 
            String contentType, 
            String lastModified,
            MessageDigest digest,
            String storageClass)
    {
        CloudEntry entry = new CloudEntry(container, key, size, etag, contentType, lastModified, digest, storageClass);
        list.add(entry);
    }
    
    public void add(CloudEntry entry)
    {
        list.add(entry);
    }
    
    public CloudEntry get(int inx)
    {
        if (inx < 0) return null;
        if (inx >= list.size()) return null;
        return list.get(inx);
    }
    
    public int size()
    {
        return list.size();
    }
    
    public ArrayList<CloudEntry> getList()
    {
        return list;
    }
        
    public String dump(String header)
    {
        StringBuffer buf = new StringBuffer();
        int inx = 0;
        for (CloudEntry entry : list) {
            buf.append(entry.dump("" + inx) + "\n");
            inx++;
        }
        return "CloudList(" + size() + ")-" + header + "\n"
                + buf.toString() + "\n";
    }
    
    
    
    public static class CloudEntry
    {
        public String container = null;
        public String key = null;
        public long size = 0;
        public String etag = null;
        public String contentType = null;
        public String lastModified = null;
        public MessageDigest digest = null;
        public String storageClass = null;
        public int version = 0;
        public DateState created = null;
        public String fileID = null;
        
        public CloudEntry(
                String container, String key, long size, String etag, String contentType, String lastModified)
        {
            this.container = container;
            this.key = key;
            this.size = size;
            this.etag = etag;
            this.lastModified = lastModified;
            this.contentType = contentType;
        }
        public CloudEntry(
                String container, 
                String key, 
                long size, 
                String etag, 
                String contentType, 
                String lastModified,
                MessageDigest digest,
                String storageClass)
        {
            this.container = container;
            this.key = key;
            this.size = size;
            this.etag = etag;
            this.lastModified = lastModified;
            this.contentType = contentType;
            this.digest = digest;
            this.storageClass = storageClass;
        }

        public CloudEntry() {
        }
        
        public Properties getProp() {
            Properties prop = new Properties();
            set(prop, "container", container);
            set(prop, "key", key);
            set(prop, "size", "" + size);
            set(prop, "etag", etag);
            set(prop, "lastModified", lastModified);
            set(prop, "contentType", contentType);
            set(prop, "storageClass", storageClass);
            set(prop, "version", "" + version);
            if (digest != null) {
                set(prop, "digest", digest.getJavaAlgorithm() + ":" + digest.getValue());
            }
            return prop;
        }
        
        public void set(Properties prop, String key, String value) {
            if (StringUtil.isEmpty(key)) return;
            if (StringUtil.isEmpty(value)) return;
            prop.setProperty(key, value);
        }
        
        public String dump(String header)
        {
            String createdS = "null";
            if (created != null) createdS = created.getIsoDate();
            String digestS = "null";
            if (digest != null) digestS = digest.getValue();
            return "CloudEntry-" + header + "\n"
                    + " - container:" + container + "\n"
                    + " - key:" + key + "\n"
                    + " - size:" + size + "\n"
                    + " - etag:" + etag + "\n"
                    + " - lastModified:" + lastModified + "\n"
                    + " - contentType:" + contentType + "\n"
                    + " - digest:" + digestS + "\n"
                    + " - storageClass:" + storageClass + "\n"
                    + " - version:" + version + "\n"
                    + " - created:" + createdS + "\n";
        }

        
        public String dumpLine()
        {
            MessageDigest digest = getDigest();
            String digestS = null;
            if (digest != null) {
                digestS = digest.getValue();
            }
            return "ENTRY:"
                    + " - container:" + container
                    + " - key:" + key
                    + " - size:" + size
                    + " - etag:" + etag
                    + " - lastModified:" + lastModified
                    + " - contentType:" + contentType
                    + " - digest:" + digestS
                    ;
        }
        
        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getContainer() {
            return container;
        }

        public void setContainer(String container) {
            this.container = container;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }

        public MessageDigest getDigest() {
            return digest;
        }

        public void setDigest(MessageDigest digest) {
            this.digest = digest;
        }

        public String getStorageClass() {
            return storageClass;
        }

        public void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public DateState getCreated() {
            return created;
        }

        public void setCreated(DateState created) {
            this.created = created;
        }

        public String getFileID() {
            return fileID;
        }

        public void setFileID(String fileID) {
            this.fileID = fileID;
        }
        
        public CloudEntry sSize(long size) {
            this.size = size;
            return this;
        }

        public CloudEntry sContainer(String container) {
            this.container = container;
            return this;
        }

        public CloudEntry sKey(String key) {
            this.key = key;
            return this;
        }

        public CloudEntry sEtag(String etag) {
            this.etag = etag;
            return this;
        }

        public CloudEntry sContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public CloudEntry sLastModified(String lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public CloudEntry sDigest(MessageDigest digest) {
            this.digest = digest;
            return this;
        }

        public CloudEntry sStorageClass(String storageClass) {
            this.storageClass = storageClass;
            return this;
        }

        public CloudEntry sVersion(int version) {
            this.version = version;
            return this;
        }

        public CloudEntry sCreated(DateState created) {
            this.created = created;
            return this;
        }

        public CloudEntry sFileID(String fileID) {
            this.fileID = fileID;
            return this;
        }
    }
}

