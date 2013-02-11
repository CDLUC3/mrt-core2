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

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import org.cdlib.mrt.cloud.ManInfo;

import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * This object imports the formatTypes.xml and builds a local table of supported format types.
 * Note, that the ObjectFormat is being deprecated and replaced by a single format id (fmtid).
 * This change is happening because formatName is strictly a description and has no functional
 * use. The scienceMetadata flag is being dropped because the ORE Resource Map is more flexible
 * and allows for a broader set of data type.
 * 
 * @author dloy
 */
public class VersionMap
{
    private static final String NAME = "VersionMap";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    
    protected Identifier objectID = null;
    protected int current = -1;
    protected long actualSize = 0;
    protected long totalSize = 0;
    protected int totalCnt = 0;
    protected int actualCnt = 0;
    protected int originalFileCount = 0;
    protected long originalTotalSize = 0;
    protected int originalActualCount = 0;
    protected long originalActualSize = 0;
    protected DateState lastAddVersion = null;
    protected DateState lastDeleteObject = null;
    protected DateState lastDeleteVersion = null;
    protected DateState originalLastAddVersion = null;
    protected DateState originalLastDeleteVersion = null;
    protected int originalVersionCnt = 0;
    protected LoggerInf logger = null;
    
    protected ArrayList<ManInfo> manList = new ArrayList<ManInfo>();
    protected HashMap<String, VersionFileComponent> keyHash = new HashMap<String, VersionFileComponent>();
    protected LinkedHashList<String, VersionFileComponent> fileHashList = new LinkedHashList<String, VersionFileComponent>();

    public VersionMap(Identifier objectID, LoggerInf logger)
        throws TException
    {
        this.objectID = objectID;
        this.logger = logger;
        
    }

    public VersionMap(VersionMap inMap)
        throws TException
    {
        this.objectID = inMap.objectID;
        for (ManInfo man : manList) {
            inMap.add(man);
        }
    }

    public int size()
    {
        return manList.size();
    }
    public int getVersionCount()
    {
        return manList.size();
    }
       
    public ManInfo getManInfo(int inx)
    {
        if (inx >= manList.size()) return null;
        if (inx < 0) return null;
        return manList.get(inx);
    }
       
    public ManInfo getVersionInfo(int versionID)
    {
        if (versionID == 0) versionID = current;
        if (versionID < 0) return null;
        for (ManInfo manInfo : manList) {
            if (manInfo.versionID == versionID) return manInfo;
            
        }
        return null;
    }
       
    public List<FileComponent> getVersionComponents(int versionID)
    {
        ManInfo manInfo = getVersionInfo(versionID);
        if (manInfo == null) return null;
        if (versionID < 0) return null;
        return manInfo.components.getFileComponents();
    }
       
    public ComponentContent getVersionContent(int versionID)
    {
        ManInfo manInfo = getVersionInfo(versionID);
        if (manInfo == null) return null;
        if (versionID < 0) return null;
        return manInfo.components;
    }
       
    public int getVersionListInx(int versionID)
    {
        if (versionID < 0) return 0;
        for (int inx = 0; inx < manList.size(); inx++) {
            ManInfo manInfo = manList.get(inx);
            if (DEBUG) System.out.println("*****getVersionListInx - versionID=" + versionID + " - manInfo.versionID=" + manInfo.versionID);
            if (manInfo.versionID == versionID) return inx;
        }
        return -1;
    }
       
    public int getMaxVersion()
    {
        int maxVersion = -1;
        for (int inx = 0; inx < manList.size(); inx++) {
            ManInfo manInfo = manList.get(inx);
            if (manInfo.versionID > maxVersion) maxVersion = manInfo.versionID;
        }
        return maxVersion;
    }
       
    public void validateVersion()
        throws TException
    {
        if (DEBUG) System.out.println("**************************VALIDATEVERSION***********************");
        if (manList.size() == 0) {
            if (current > 0) {
                throw new TException.INVALID_ARCHITECTURE(MESSAGE + "validateVersion - current set but no version content - current=" + current);
            } else return;
        }
        
        int maxVersion = getMaxVersion();
        int manListSize = manList.size();
        if (DEBUG) System.out.println("validateVersion:"
                + " - maxVersion=" + maxVersion
                + " - manListSize=" + manListSize
                + " - current=" + current
                );
        for (int vix = 1; vix <= manListSize; vix++) {
            ManInfo vInfo = getVersionInfo(vix);
            if (vInfo == null) {
                throw new TException.INVALID_ARCHITECTURE(MESSAGE + "validateVersion - hole in versions:" + vix);
            }
        }
        if (maxVersion != manListSize) {
            throw new TException.INVALID_ARCHITECTURE(MESSAGE + "validateVersion - maxVersion != version list size "
                    + " - maxVersion=" + maxVersion
                    + " - manListSize=" + manListSize
                    );
        }
        if (current != manListSize) {
            throw new TException.INVALID_ARCHITECTURE(MESSAGE + "validateVersion - current != version list size "
                    + " - current=" + current
                    + " - manListSize=" + manListSize
                    );
        }
    }
    
    public void deleteFromManList(int delInx)
    {
        manList.remove(delInx);
        lastDeleteVersion = new DateState();
        rebuildHash();
    }
    
    public void rebuildHash()
    {
        keyHash.clear();
        fileHashList.clear();
        actualSize = 0;
        totalSize=0;
        totalCnt = 0;
        actualCnt = 0;
        for (ManInfo manInfo : manList) {
            addHash(manInfo);
        }
    }

    public void addVersion(
            List<FileComponent> components)
        throws TException
    {
        try {
            if (DEBUG) System.out.println("*******************ADDVERSION*********************");
            int versionID = getNextVersion();
            DateState created = new DateState();
            ManInfo testInfo = getVersionInfo(versionID);
            if (testInfo != null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "addVersion - versionID already exists:" + versionID);
            }
            validateVersion();
            ManInfo addMan = buildManInfo(versionID, created, components);
            current = versionID;
            add(addMan);
            validateVersion();
            lastAddVersion = created;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
        
    }

    public void updateVersion(
            List<FileComponent> components,
            String [] deleteList)
        throws TException
    {
        try {
            if (DEBUG) System.out.println("*******************updateVersion*********************");
            int versionID = getNextVersion();
            DateState created = new DateState();
            ManInfo testInfo = getVersionInfo(versionID);
            if (testInfo != null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "addVersion - versionID already exists:" + versionID);
            }
            ManInfo updateInfo = buildManInfo(versionID, created, components);
            ManInfo addInfo = getMergeInfo(updateInfo, deleteList);
            current = versionID;
            addInfo.versionID = versionID;
            add(addInfo);
            validateVersion();
            lastAddVersion = created;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
        
    }
    
    
    public void deleteCurrent()
        throws TException
    {
        try {
            if (DEBUG) System.out.println("****************************deleteCurrent***********************");
            if (false && current == 1) {
                throw new TException.REQUEST_INVALID(MESSAGE + "deleteLocalVersion - only one version - use cloud delete");
            }
            int delInx = getVersionListInx(current);
            if (delInx < 0) { //!!!!
                throw new TException.INVALID_ARCHITECTURE(MESSAGE + "deleteLocalVersion - current not found:" + current);
            }
            
            deleteFromManList(delInx);
            current = manList.size();
            lastDeleteVersion = new DateState();
            validateVersion();
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
        
    }

    public static ManInfo buildManInfo(
            int versionID, 
            DateState created, 
            List<FileComponent> components)
        throws TException
    {
        try {
            if (versionID <= 0) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "buildManInfo - versionID not valid:" + versionID);
            }
            if (created == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "buildManInfo - created date not supplied");
            }
            ManInfo info = new ManInfo();
            info.versionID = versionID;
            info.created = created;
            if (components == null) return info;
            info.setComponents(versionID, components);
            for (FileComponent component : components) {
                info.size += component.getSize();
                info.count++;
            }
            return info;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
        
    }
    
    public void add(ManInfo info)
        throws TException
    {
        int versionID = info.versionID;
        validateManInfo(info);
        manList.add(info);
        //lastAddVersion = new DateState();
        addHash(info);
    }
    
    public ManInfo getMergeInfoOriginal(
            ManInfo update,
            String [] deleteList)
        throws TException
    {
        try {
            int currentVersion = current;
            if (currentVersion == 0) {
                if ((deleteList != null) && (deleteList.length > 0)) {
                    throw new TException.REQUEST_INVALID(MESSAGE + "Delete list provided and no version exists");
                }
            }
            List<FileComponent> currentComponents = getVersionComponents(currentVersion);
            HashMap<String, FileComponent> hashMerge = new HashMap<String, FileComponent>();
            
            // build hash list
            if (currentComponents != null) {
                for (FileComponent currentComponent : currentComponents) {
                    hashMerge.put(currentComponent.getIdentifier(), currentComponent);
                }
            }
            
            // delete items from hash using delete list
            if ((deleteList != null) && (deleteList.length > 0)) {
                for (String fileID : deleteList) {
                    FileComponent deleteComponent = hashMerge.get(fileID);
                    if (deleteComponent == null) {
                        throw new TException.REQUEST_INVALID(MESSAGE + "Delete list component not found:" + fileID);
                    }
                    hashMerge.remove(fileID);
                }
            }
            
            // add updates
            if (update.components != null) {
                List<FileComponent> updateComponents = update.components.getFileComponents();
                for (FileComponent updateComponent : updateComponents) {
                    hashMerge.put(updateComponent.getIdentifier(), updateComponent);
                }
            }
            
            //convert from hash to ManInfo
            ArrayList<FileComponent> addComponents = new ArrayList<FileComponent>();
            Set<String> keys = hashMerge.keySet();
            for (String key : keys) {
                FileComponent addComponent = hashMerge.get(key);
                addComponents.add(addComponent);
                if (DEBUG) System.out.println(addComponent.dump("getMergeInfo - " + key));
            }
            ManInfo addInfo = new ManInfo();
            addInfo.components = new ComponentContent(addComponents);
            addInfo.size = getManifestSize(addInfo);
            addInfo.created = new DateState();
            addInfo.count = addComponents.size();
            
            return addInfo;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    public ManInfo getMergeInfo(
            ManInfo update,
            String [] deleteList)
        throws TException
    {
        try {
            int currentVersion = current;
            if (currentVersion == 0) {
                if ((deleteList != null) && (deleteList.length > 0)) {
                    throw new TException.REQUEST_INVALID(MESSAGE + "Delete list provided and no version exists");
                }
            }
            List<FileComponent> currentComponents = getVersionComponents(currentVersion);
            List<FileComponent> updateComponents = null;
            
            // add updates
            if (update.components != null) {
                updateComponents = update.components.getFileComponents();
            }
            List<FileComponent> addComponents = getMergeComponents(
                    updateComponents,
                    currentComponents,
                    deleteList);
            ManInfo addInfo = new ManInfo();
            addInfo.components = new ComponentContent(addComponents);
            addInfo.size = getManifestSize(addInfo);
            addInfo.created = new DateState();
            addInfo.count = addComponents.size();
            
            return addInfo;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public static ArrayList<FileComponent> getMergeComponents(
            List<FileComponent> updateComponents,
            List<FileComponent> currentComponents,
            String [] deleteList)
        throws TException
    {
        try {
            HashMap<String, FileComponent> hashMerge = new HashMap<String, FileComponent>();
            
            // build hash list
            if (currentComponents != null) {
                for (FileComponent currentComponent : currentComponents) {
                    if (DEBUG) System.out.println("getMergeComponents - current fileID:" + currentComponent.getIdentifier());
                    hashMerge.put(currentComponent.getIdentifier(), currentComponent);
                }
            }
            
            // delete items from hash using delete list
            if ((deleteList != null) && (deleteList.length > 0)) {
                for (String fileID : deleteList) {
                    if (DEBUG) System.out.println("getMergeComponents - delete fileID:" + fileID);
                    FileComponent deleteComponent = hashMerge.get(fileID);
                    if (deleteComponent == null) {
                        throw new TException.REQUEST_INVALID(MESSAGE + "Delete list component not found:" + fileID);
                    }
                    hashMerge.remove(fileID);
                }
            }
            
            // add updates
            if (updateComponents != null) {
                for (FileComponent updateComponent : updateComponents) {
                    hashMerge.put(updateComponent.getIdentifier(), updateComponent);
                }
            }
            
            //convert from hash to ManInfo
            ArrayList<FileComponent> addComponents = new ArrayList<FileComponent>();
            Set<String> keys = hashMerge.keySet();
            for (String key : keys) {
                FileComponent addComponent = hashMerge.get(key);
                addComponents.add(addComponent);
                if (DEBUG) System.out.println(addComponent.dump("getMergeInfo - " + key));
            }
            return addComponents;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public void addHash(ManInfo info)
    {
        int versionID = info.versionID;
        for (FileComponent component : info.components.getFileComponents()) {
            String key = component.getLocalID();
            long fileSize = component.getSize();
            totalSize += fileSize;
            totalCnt++;
            VersionFileComponent vfc = keyHash.get(key);
            if (vfc != null) continue;
            actualCnt++;
            actualSize += fileSize;
            vfc = new VersionFileComponent();
            vfc.versionID = versionID;
            vfc.key = component.getLocalID();
            vfc.component = component;
            keyHash.put(key, vfc);
            fileHashList.put(component.getIdentifier(), vfc);
            
        }
    }
    
    protected void validateManInfo(ManInfo info)
        throws TException
    {
        int manCnt = info.components.size();
        long manSize = getManifestSize(info);
        if (info.size <= 0) info.size = manSize;
        else if (info.size != manSize) {
            throw new TException.INVALID_OR_MISSING_PARM("validateManInfo"
                    + " - mismatch version=" + info.versionID
                    + " - info.size=" + info.size
                    + " - manSize=" + manSize
                    );
        }
    }
    
    /**
     * 
     * @return count of unique fileComponents
     */
    public int getUniqueCnt()
    {
        return keyHash.size();
    }
    
    public long getUniqueSize()
    {
        long size = 0;
        for (String key : keyHash.keySet()) {
            VersionFileComponent vfc = keyHash.get(key);
            size += vfc.component.getSize();
        }
        return size;
    }
    
    public long getManifestSize(ManInfo info)
    {
        long size = 0;
        List<FileComponent> components = info.components.getFileComponents();
        for (FileComponent component : components) {
            size += component.getSize();
        }
        return size;
    }
    
    
    
    public void setCloudComponent(FileComponent manifestComponent, boolean doFill)
        throws TException
    {
        try {
            List<VersionFileComponent> matchComponents = fileHashList.get(manifestComponent.getIdentifier());
            if (matchComponents == null) {
                if (doFill) fillComponent(manifestComponent);
                return;
            }
            for (VersionFileComponent matchVFC : matchComponents) {
                FileComponent matchComponent = matchVFC.component;
                if (isMatch(matchComponent, manifestComponent)) {
                    manifestComponent.setLocalID(matchVFC.key);
                    return;
                }
            }
            
            // keep key empty
            if (doFill) fillComponent(manifestComponent);
            return;
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            String msg = MESSAGE + "buildCloudComponent - Exception:" + ex;
            logger.logError(msg, 2);
            logger.logError(StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(msg);
        }
        
    }    
    
    public void fillComponent(FileComponent manifestComponent)
        throws TException
    {
        try {
            if (manifestComponent.getComponentFile() != null) return;
            URL url = manifestComponent.getURL();
            if (url == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "fillComponent - component URL missing");
            }
            File tmpFile = FileUtil.getTempFile("tmp", ".txt");
            FileUtil.url2File(logger, url, tmpFile);
            manifestComponent.setComponentFile(tmpFile);
            
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            String msg = MESSAGE + "buildCloudComponent - Exception:" + ex;
            logger.logError(msg, 2);
            logger.logError(StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(msg);
        }
        
    }
    
    public boolean isMatch(FileComponent matchComponent, FileComponent manifestComponent)
        throws TException
    {
        try {
            
            MessageDigest manifestDigest = manifestComponent.getMessageDigest();
            String manifestAlgorithm = manifestDigest.getJavaAlgorithm();
            String manifestValue = manifestDigest.getValue();
            long manifestSize = manifestComponent.getSize();
            
            
            MessageDigest matchDigest = matchComponent.getMessageDigest();
            String matchAlgorithm = matchDigest.getJavaAlgorithm();
            String matchValue = matchDigest.getValue();
            long matchSize = matchComponent.getSize();
            
            if (manifestSize != matchSize) return false;
            
            if (manifestAlgorithm.equals(matchAlgorithm)) {
                if (manifestValue.equals(matchValue)) return true;
                else return false;
            }
            fillComponent(manifestComponent);
            FixityTests manifestTest = new FixityTests(manifestComponent.getComponentFile(), matchAlgorithm, logger);
            FixityTests.FixityResult fixityTest = manifestTest.validateSizeChecksum(matchValue, matchValue, matchSize);
            if (fixityTest.checksumMatch) return true;
            else return false;
            
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            String msg = MESSAGE + "buildCloudComponent - Exception:" + ex;
            logger.logError(msg, 2);
            logger.logError(StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(msg);
        }
        
    }

    public int getActualCnt() {
        return actualCnt;
    }

    public long getActualSize() {
        return actualSize;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public long getTotalSize() {
        return totalSize;
    }
    

    public Identifier getObjectID() {
        return objectID;
    }

    public DateState getLastAddVersion() {
        return lastAddVersion;
    }

    public DateState getLastDeleteVersion() {
        return lastDeleteVersion;
    }

    public DateState getOriginalLastAddVersion() {
        return originalLastAddVersion;
    }

    public void setActualCnt(int actualCnt) {
        this.actualCnt = actualCnt;
    }

    public void setActualSize(long actualSize) {
        this.actualSize = actualSize;
    }

    public void setLastAddVersion(DateState lastAddVersion) {
        this.lastAddVersion = lastAddVersion;
    }

    public void setLastDeleteVersion(DateState lastDeleteVersion) {
        this.lastDeleteVersion = lastDeleteVersion;
    }

    public void setObjectID(Identifier objectID) {
        this.objectID = objectID;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setOriginalLastAddVersion(DateState originalLastAddVersion) {
        this.originalLastAddVersion = originalLastAddVersion;
    }

    public DateState getOriginalLastDeleteVersion() {
        return originalLastDeleteVersion;
    }

    public void setOriginalLastDeleteVersion(DateState originalLastDeleteVersion) {
        this.originalLastDeleteVersion = originalLastDeleteVersion;
    }

    public int getOriginalActualCount() {
        return originalActualCount;
    }

    public void setOriginalActualCount(int originalActualCount) {
        this.originalActualCount = originalActualCount;
    }

    public long getOriginalActualSize() {
        return originalActualSize;
    }

    public void setOriginalActualSize(long originalActualSize) {
        this.originalActualSize = originalActualSize;
    }

    public int getOriginalFileCount() {
        return originalFileCount;
    }

    public void setOriginalFileCount(int originalFileCount) {
        this.originalFileCount = originalFileCount;
    }

    public long getOriginalTotalSize() {
        return originalTotalSize;
    }

    public void setOriginalTotalSize(long originalTotalSize) {
        this.originalTotalSize = originalTotalSize;
    }

    public int getNextVersion() {
        if (current <= 0) return 1;
        return current + 1;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getOriginalVersionCnt() {
        return originalVersionCnt;
    }

    public void setOriginalVersionCnt(int originalVersionCnt) {
        this.originalVersionCnt = originalVersionCnt;
    }

    public DateState getLastDeleteObject() {
        return lastDeleteObject;
    }

    public void setLastDeleteObject(DateState lastDeleteObject) {
        this.lastDeleteObject = lastDeleteObject;
    }
    
    public DeltaStats getDeltaStats()
    {
        DeltaStats delta = new DeltaStats();
        delta.deltaActualCount = actualCnt - originalActualCount;
        delta.deltaActualSize = actualSize - originalActualSize;
        delta.deltaFileCount = totalCnt - originalFileCount;
        delta.deltaTotalSize = totalSize - originalTotalSize;
        return delta;
    }
    
    
    public String dump(String header) {
        StringBuffer buf = new StringBuffer();
        buf.append(MESSAGE + header + "\n"
                + " - actualSize=" + actualSize + "\n"
                + " - totalSize=" + totalSize + "\n"
                + " - totalCnt=" + totalCnt + "\n"
                + " - actualCnt=" + actualCnt + "\n"
                + " - maxVersion=" + getMaxVersion() + "\n"
                );
        
        if (lastAddVersion != null) {
            buf.append(
                " - lastAddVersion=" + lastAddVersion.getIsoDate() + "\n"
                );
        }
        if (lastDeleteVersion != null) {
            buf.append(
                " - lastDeleteVersion=" + lastDeleteVersion.getIsoDate() + "\n"
                );
        }
        buf.append(""
                + " - current=" + current + "\n"
                + " - originalFileCount=" + originalFileCount + "\n"
                + " - originalTotalSize=" + originalTotalSize + "\n"
                + " - originalActualCount=" + originalActualCount + "\n"
                + " - originalActualSize=" + originalActualSize + "\n"
                );
        if (originalLastAddVersion != null) {
            buf.append(
                " - originalLastAddVersion=" + originalLastAddVersion.getIsoDate() + "\n"
                );
        }
        if (originalLastDeleteVersion != null) {
            buf.append(
                " - originalLastDeleteVersion=" + originalLastDeleteVersion.getIsoDate() + "\n"
                );
        }
        return buf.toString();
    }
    
    public ManInfo addTest(int versionID, int outVersionID)
        throws TException
    {
            ComponentContent testContent = getVersionContent(versionID);
            ManInfo info = getVersionInfo(versionID);
            info.components = testContent;
            addVersion(testContent.getFileComponents());
            return info;
    }
    
    public FileComponent getFileComponent(int versionID, String fileID)
        throws TException
    {
        ComponentContent content = getVersionContent(versionID);
        if (content == null) {
            throw new TException.REQUESTED_ITEM_NOT_FOUND("versionID not found:" + versionID);
        }
        return content.getFileComponent(fileID);
        
    }
    
    public VersionStats getVersionStats(int versionID)
        throws TException
    {
        ComponentContent content = getVersionContent(versionID);
        if (content == null) {
            throw new TException.REQUESTED_ITEM_NOT_FOUND(MESSAGE + "getVersionStats: versionID not found:" + versionID);
        }
        VersionStats stats = new VersionStats();
        List<FileComponent> components = content.getFileComponents();
        for (FileComponent component : components) {
            stats.totalSize += component.getSize();
            stats.numFiles++;
            VersionFileComponent vfc = keyHash.get(component.getLocalID());
            if (vfc.versionID == versionID) {
                stats.totalActualSize += component.getSize();
                stats.numActualFiles++;
            }
        }
        return stats;
    }
    
    public LoggerInf getLogger() {
        return logger;
    }
    
    public static class VersionStats
    {
        public int numFiles = 0;
        public long totalSize = 0;
        public long numActualFiles = 0;
        public long totalActualSize = 0; 
    }
    
    public static class VersionFileComponent 
    {
        public int versionID = 0;
        public String key = null;
        public FileComponent component = null;
        
    }
    
    public static class DeltaStats
    {
        public int deltaFileCount = 0;
        public long deltaTotalSize = 0;
        public int deltaActualCount = 0;
        public long deltaActualSize = 0;
    }
}
