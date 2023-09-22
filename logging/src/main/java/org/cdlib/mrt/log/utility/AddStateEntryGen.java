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
import java.util.Properties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.core.Identifier;
import org.json.JSONObject;

public class AddStateEntryGen {
    private String keyPrefix = null;
    private String service = null;
    private String serviceProcess = null;
    private String fileID = null;
    private Integer version = null;
    private Long processNode = null;
    private Long sourceNode = null;
    private Long targetNode = null;
    private Integer currentVersion = null;
    private Integer versions = null;
    private Integer attempts = null;
    private Long files = null;
    private Long bytes = null;
    private Long durationMs = null;
    private Long startMs = null;
    private String key = null;
    private String status = null;
    private Identifier objectID = null;
    private Identifier ownerID = null;
    private String localids = null;
    private Properties properties = null;
   
    protected static final Logger LOGGER = LogManager.getLogger();
        
    public void addLogStateEntry(String logKey)
        throws TException
    {
        addLogLevel(Level.INFO, logKey);
    }   
    
    public void addLogStateEntry(String levelS, String logKey)
        throws TException
    {
         Level logLevel = Level.getLevel(levelS);
         addLogLevel(logLevel, logKey);
    }
    
    public static void addLogStateEntry(String logKey, JSONObject jsonState)
        throws TException
    {
        addLogJson(Level.INFO, logKey, jsonState);
    }
        
    public void addLogLevel(Level logLevel, String logKey)
        throws TException
    {
        JSONObject jsonState = buildStateJSON();
        try {
            if (durationMs == null) {
                durationMs = System.currentTimeMillis() - startMs;
            }
            addLogJson(logLevel, logKey, jsonState);
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
        
    public void addLog(String levelS, String logKey)
        throws TException
    {
         addLogStateEntry(levelS, logKey);
    }
        
    public static void addLogJson(Level level, String logKey, JSONObject jsonState)
        throws TException
    {
    
        JSONObject jsonRoot = addLogKey(logKey, jsonState);
        try {
            System.out.println("jsonState:" + jsonState.toString(2));
            
        } catch (Exception ex) { }
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
        
    public static void addLogStateEntry(String levelS, String logKey, JSONObject jsonState)
        throws TException
    {
        Level level = Level.toLevel(levelS, Level.INFO);
        addLogJson(level, logKey, jsonState);
    }
        
    // default ServiceState
    public static AddStateEntryGen getAddStateEntryGen(String keyPrefix, String service, String serviceProcess)
    {
        return new AddStateEntryGen(keyPrefix, service, serviceProcess);
    }
    
    protected AddStateEntryGen(String keyPrefix, String service, String serviceProcess)
    {
        this.keyPrefix = keyPrefix;
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
            // Note that node is output as string key - opensearch uses delimiters 9,501
            if (sourceNode != null) {
                jsonID.put("sourceNode", "" + sourceNode);
            }
            if (targetNode != null) {
                jsonID.put("targetNode", "" + targetNode);
            }
            if (processNode != null) {
                jsonID.put("processNode", "" + processNode);
            }
            if (objectID != null) {
                jsonID.put("ark", objectID.getValue());
            }
            if (version != null) {
                jsonID.put("version", version);
            }
            if (ownerID != null) {
                jsonID.put("owner", ownerID.getValue());
            }
            if (fileID != null) {
                jsonID.put("fileid", fileID);
            }
            if (localids != null) {
                jsonID.put("localids", localids);
            }
            if (key != null) {
                jsonID.put("key", key);
            }
            return jsonID;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public JSONObject buildProperties()
        throws TException
    {
        try {
            JSONObject jsonProperties = new JSONObject();
            if (properties == null) return null;
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                jsonProperties.put(key, value);
            }
            return jsonProperties;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public JSONObject buildContent()
        throws TException
    {
        try {
            JSONObject jsonContent = new JSONObject();
            if (status != null) {
                jsonContent.put(keyPrefix + "Status", status);
            }
            if (durationMs != null) {
                jsonContent.put(keyPrefix + "DurationMs", durationMs);
            }
            if (bytes != null) {
                jsonContent.put(keyPrefix + "Bytes", bytes);
            }
            if (files != null) {
                jsonContent.put(keyPrefix + "Files", files);
            }
            if (versions != null) {
                jsonContent.put(keyPrefix + "Versions", versions);
            }
            if (attempts != null) {
                jsonContent.put(keyPrefix + "Attempts", attempts);
            }
            if ((bytes != null) && (durationMs != null) && (durationMs != 0)) {
                double bytesPerMs = (double)bytes/(double)durationMs;
                jsonContent.put(keyPrefix + "BytesPerMs", bytesPerMs);
            }
            if ((files != null) && (files != 0) && (durationMs != null)) {
                double msPerFile = (double)durationMs/(double)files;
                jsonContent.put(keyPrefix + "MsPerFile", msPerFile);
            }
            if ((files != null) && (versions != null) && (versions != 0)) {
                double filesPerVersions = (double)files/(double)versions;
                jsonContent.put(keyPrefix + "FilesPerVersions", filesPerVersions);
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
            JSONObject jsonProp = buildProperties();
            JSONObject jsonContent = buildContent();
            
            jsonState.put("serviceInfo", jsonService);
            if (jsonID.length() > 0)
                jsonState.put("id", jsonID);
            if (jsonProp != null) {
                jsonState.put("properties", jsonProp);
            }
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
            return jsonRoot;
            
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }
    
    /**
     * Add one or more JSON entries in log4j2
     * @param levelS string form of log4j2 output
     * @param jsonEntry entries to be added to ecs output
     * @throws TException 
     * Example
     *      JSONObject jsonRoot2 = new JSONObject();
     *      jsonRoot2.put("BiggyNum2", 1234567890);
     *      jsonRoot2.put("SomeKey2", "yowza2");
     *       AddStateEntryGen.addEntry("info", jsonRoot2);
     */
    public static void addEntry(String levelS, JSONObject jsonEntry)
        throws TException
    {
        try {
            Level level = Level.toLevel(levelS, Level.INFO);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonEntry.toString());
            LOGGER.log(level, jsonNode);
            
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

    public AddStateEntryGen setVersion(Integer version) {
        this.version = version;
        return this;
    }
    
    public Long getDurationMs() {
        return durationMs;
    }

    public AddStateEntryGen setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
        return this;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public AddStateEntryGen setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
        return this;
    }
    
    public String getKey() {
        return key;
    }

    public AddStateEntryGen setKey(String key) {
        this.key = key;
        return this;
    }

    public Identifier getObjectID() {
        return objectID;
    }

    public AddStateEntryGen setObjectID(Identifier objectID) {
        this.objectID = objectID;
        return this;
    }

    public AddStateEntryGen setArk(String objectIDS) 
        throws TException
    {
        this.objectID = new Identifier(objectIDS);
        return this;
    }

    public Identifier getOwnerID() {
        return ownerID;
    }

    public AddStateEntryGen setOwnerID(Identifier ownerID) {
        this.ownerID = ownerID;
        return this;
    }

    public AddStateEntryGen setOwner(String ownerIDS) 
        throws TException
    {
        this.ownerID = new Identifier(ownerIDS);
        return this;
    }
    
    public String getLocalids() {
        return localids;
    }

    public AddStateEntryGen setLocalids(String localids) {
        this.localids = localids;
        return this;
    }

    public Long getSourceNode() {
        return sourceNode;
    }

    public AddStateEntryGen setSourceNode(Long sourceNode) {
        this.sourceNode = sourceNode;
        return this;
    }

    public AddStateEntryGen setSourceNode(Integer sourceNodeI) {
        if (sourceNodeI == null) return this;
        long sourceNodeL = sourceNodeI;
        this.sourceNode = sourceNodeL;
        return this;
    }

    public Long getProcessNode() {
        return processNode;
    }

    public AddStateEntryGen setProcessNode(Long processNode) {
        this.processNode = processNode;
        return this;
    }
    
    public AddStateEntryGen setProcessNode(Integer processNodeI) {
        if (processNodeI == null) return this;
        long processNodeL = processNodeI;
        this.processNode = processNodeL;
        return this;
    }

    public Long getTargetNode() {
        return targetNode;
    }

    public AddStateEntryGen setTargetNode(Long targetNode) {
        this.targetNode = targetNode;
        return this;
    }

    public Integer getVersions() {
        return versions;
    }

    public AddStateEntryGen setVersions(Integer versions) {
        this.versions = versions;
        return this;
    }

    public Long getFiles() {
        return files;
    }

    public AddStateEntryGen setFiles(Long files) {
        this.files = files;
        return this;
    }

    public Long getBytes() {
        return bytes;
    }

    public AddStateEntryGen setBytes(Long bytes) {
        this.bytes = bytes;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getStatus() {
        return status;
    }

    public AddStateEntryGen setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getFileID() {
        return fileID;
    }

    public AddStateEntryGen setFileID(String fileID) {
        this.fileID = fileID;
        return this;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
