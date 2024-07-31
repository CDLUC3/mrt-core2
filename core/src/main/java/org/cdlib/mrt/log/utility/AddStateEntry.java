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
/**
 *
 * @author dloy
 */
package org.cdlib.mrt.log.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.core.Identifier;
import org.json.JSONObject;

public class AddStateEntry {
    private String service = null;
    private String serviceProcess = null;
    private Integer version = null;
    private Long sourceNode = null;
    private Long targetNode = null;
    private Integer currentVersion = null;
    private Integer addVersions = null;
    private Long addFiles = null;
    private Long addBytes = null;
    private Integer versions = null;
    private Long files = null;
    private Long bytes = null;
    private Integer deleteVersions = null;
    private Long deleteFiles = null;
    private Long deleteBytes = null;
    private Long durationMs = null;
    private Long startMs = null;
    private String key = null;
    private Identifier objectID = null;
    private String localids = null;
   
    protected static final Logger LOGGER = LogManager.getLogger("JSONLog");
        
    public void addLogStateEntry(String logKey)
        throws TException
    {
        JSONObject jsonState = buildStateJSON();
        try {
            System.out.println("jsonState:" + jsonState.toString(2));
            
        } catch (Exception ex) { }
        if (false) return;
        try {
            addLogStateEntry(logKey, jsonState);
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
        
    public void addLog(String levelS, String logKey)
        throws TException
    {
        JSONObject jsonState = buildStateJSON();
        try {
            System.out.println("jsonState:" + jsonState.toString(2));
            
        } catch (Exception ex) { }
        if (false) return;
        try {
            if (durationMs == null) {
                durationMs = System.currentTimeMillis() - startMs;
            }
            Level logLevel = Level.getLevel(logKey);
            addLogStateEntry(logLevel, logKey, jsonState);
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
        
    public static void addLogStateEntry(String logKey, JSONObject jsonState)
        throws TException
    {
        addLogStateEntry(Level.INFO, logKey, jsonState);
    }
        
    public static void addLogStateEntry(Level level, String logKey, JSONObject jsonState)
        throws TException
    {
        
        try {
            System.out.println("jsonState:" + jsonState.toString(2));
            
        } catch (Exception ex) { }
        if (false) return;
        JSONObject jsonRoot = addLogKey(logKey, jsonState);
        try {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonRoot.toString());
                LOGGER.log(level, jsonNode);
                
            } catch (Exception ex) {
                throw new TException.GENERAL_EXCEPTION(ex);
            }
            //ObjectMessage objMsg = new ObjectMessage(jsonState);
            //LOGGER.info(objMsg);
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
        
    // default ServiceState
    public static AddStateEntry getAddStateEntry(String service, String serviceProcess)
    {
        return new AddStateEntry(service, serviceProcess);
    }
    
    protected AddStateEntry(String service, String serviceProcess)
    {
        this.service = service;
        this.serviceProcess = serviceProcess;
        this.startMs = System.currentTimeMillis();
    }

    public JSONObject buildService()
        throws TException
    {
        try {
            JSONObject jsonService = new JSONObject();
            if ((service == null) || (serviceProcess == null)) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND("service and serviceProcess required");
            }
            jsonService.put("service", service);
            jsonService.put("serviceProcess", serviceProcess);
            return jsonService;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public JSONObject buildID()
        throws TException
    {
        try {
            JSONObject jsonID = new JSONObject();
            if (sourceNode != null) {
                jsonID.put("sourceNode", sourceNode);
            }
            if (targetNode != null) {
                jsonID.put("targetNode", targetNode);
            }
            if (version != null) {
                jsonID.put("version", version);
            }
            if (objectID != null) {
                jsonID.put("ark", objectID.getValue());
            }
            if (localids != null) {
                jsonID.put("localids", localids);
            }
            return jsonID;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public JSONObject buildContent()
        throws TException
    {
        try {
            JSONObject jsonContent = new JSONObject();
            if (durationMs != null) {
                jsonContent.put("durationMs", durationMs);
            }
            if (addBytes != null) {
                jsonContent.put("addBytes", addBytes);
            }
            if (addFiles != null) {
                jsonContent.put("addFiles", addFiles);
            }
            if (addVersions != null) {
                jsonContent.put("addVersions", addVersions);
            }
            if (deleteBytes != null) {
                jsonContent.put("deleteBytes", deleteBytes);
            }
            if (deleteFiles != null) {
                jsonContent.put("deleteFiles", deleteFiles);
            }
            if (deleteVersions != null) {
                jsonContent.put("deleteVersions", deleteVersions);
            }
            if (bytes != null) {
                jsonContent.put("bytes", bytes);
            }
            if (files != null) {
                jsonContent.put("files", files);
            }
            if (versions != null) {
                jsonContent.put("versions", versions);
            }
            return jsonContent;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public JSONObject buildStateJSON()
        throws TException
    {
        try {
            JSONObject jsonState = new JSONObject();
            
            JSONObject jsonService = buildService();
            JSONObject jsonID = buildID();
            JSONObject jsonContent = buildContent();
            
            jsonState.put("serviceInfo", jsonService);
            if (jsonID.length() > 0)
                jsonState.put("id", jsonID);
            if (jsonContent.length() > 0)
                jsonState.put("content", jsonContent);
            System.out.println("jsonState - " + jsonState.toString(2));
            return jsonState;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    public static JSONObject addLogKey(String logKey, JSONObject jsonState)
        throws TException
    {
        try {
            if (logKey == null) logKey = "ServiceState";
            JSONObject jsonRoot = new JSONObject();
            jsonRoot.put(logKey, jsonState);
            System.out.println("root - " + jsonRoot.toString(2));
            return jsonRoot;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceProcess() {
        return serviceProcess;
    }

    public void setServiceProcess(String serviceProcess) {
        this.serviceProcess = serviceProcess;
    }

    public Integer getVersion() {
        return version;
    }

    public AddStateEntry setVersion(Integer version) {
        this.version = version;
        return this;
    }
    
    public Long getDurationMs() {
        return durationMs;
    }

    public AddStateEntry setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
        return this;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public AddStateEntry setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
        return this;
    }

    public Integer getAddVersions() {
        return addVersions;
    }

    public AddStateEntry setAddVersions(Integer addVersions) {
        this.addVersions = addVersions;
        return this;
    }

    public Long getAddFiles() {
        return addFiles;
    }

    public AddStateEntry setAddFiles(Long addFiles) {
        this.addFiles = addFiles;
        return this;
    }

    public Integer getDeleteVersions() {
        return deleteVersions;
    }

    public Long getAddBytes() {
        return addBytes;
    }

    public AddStateEntry setAddBytes(Long addBytes) {
        this.addBytes = addBytes;
        return this;
    }

    public AddStateEntry setDeleteVersions(Integer deleteVersions) {
        this.deleteVersions = deleteVersions;
        return this;
    }

    public Long getDeleteFiles() {
        return deleteFiles;
    }

    public AddStateEntry setDeleteFiles(Long deleteFiles) {
        this.deleteFiles = deleteFiles;
        return this;
    }
    

    public Long getDeleteBytes() {
        return deleteBytes;
    }

    public AddStateEntry setDeleteBytes(Long deleteBytes) {
        this.deleteBytes = deleteBytes;
        return this;
    }

    public String getKey() {
        return key;
    }

    public AddStateEntry setKey(String key) {
        this.key = key;
        return this;
    }

    public Identifier getObjectID() {
        return objectID;
    }

    public AddStateEntry setObjectID(Identifier objectID) {
        this.objectID = objectID;
        return this;
    }

    public AddStateEntry setArk(String objectIDS) 
        throws TException
    {
        this.objectID = new Identifier(objectIDS);
        return this;
    }
    
    public String getLocalids() {
        return localids;
    }

    public AddStateEntry setLocalids(String localids) {
        this.localids = localids;
        return this;
    }

    public Long getSourceNode() {
        return sourceNode;
    }

    public AddStateEntry setSourceNode(Long sourceNode) {
        this.sourceNode = sourceNode;
        return this;
    }

    public AddStateEntry setSourceNode(Integer sourceNodeI) {
        if (sourceNodeI == null) return this;
        long sourceNodeL = sourceNodeI;
        this.sourceNode = sourceNodeL;
        return this;
    }

    public Long getTargetNode() {
        return targetNode;
    }

    public AddStateEntry setTargetNode(Long targetNode) {
        this.targetNode = targetNode;
        return this;
    }

    public Integer getVersions() {
        return versions;
    }

    public AddStateEntry setVersions(Integer versions) {
        this.versions = versions;
        return this;
    }

    public Long getFiles() {
        return files;
    }

    public AddStateEntry setFiles(Long files) {
        this.files = files;
        return this;
    }

    public Long getBytes() {
        return bytes;
    }

    public AddStateEntry setBytes(Long bytes) {
        this.bytes = bytes;
        return this;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
