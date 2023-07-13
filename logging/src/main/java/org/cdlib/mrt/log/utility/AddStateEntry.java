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
    private Long node = null;
    private Long length = null;
    private Long durationMs = null;
    private String key = null;
    private Identifier objectID = null;
    private String localids = null;
   
    private static final Logger LOGGER = LogManager.getLogger("JSONLog");
        
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
        
    public void addLogStateEntry(String logKey, JSONObject jsonState)
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
                LOGGER.log(Level.INFO, jsonNode);
                
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
            if (node != null) {
                jsonID.put("node", node);
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
            if (length != null) {
                jsonContent.put("length", length);
            }
            if (durationMs != null) {
                jsonContent.put("durationMs", durationMs);
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

    public JSONObject addLogKey(String logKey, JSONObject jsonState)
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

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getNode() {
        return node;
    }

    public void setNode(Long node) {
        this.node = node;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Identifier getObjectID() {
        return objectID;
    }

    public void setObjectID(Identifier objectID) {
        this.objectID = objectID;
    }

    public void setArk(String objectIDS) 
        throws TException
    {
        this.objectID = new Identifier(objectIDS);
    }
    
    public String getLocalids() {
        return localids;
    }

    public void setLocalids(String localids) {
        this.localids = localids;
    }
}
