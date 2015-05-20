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
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.http.HttpResponse;

import org.cdlib.mrt.cloud.ManInfo;

import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.ManifestRowAdd;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.core.StorageAddClient;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.utility.URLEncoder;

/**
 * This object imports the formatTypes.xml and builds a local table of supported format types.
 * Note, that the ObjectFormat is being deprecated and replaced by a single format id (fmtid).
 * This change is happening because formatName is strictly a description and has no functional
 * use. The scienceMetadata flag is being dropped because the ORE Resource Map is more flexible
 * and allows for a broader set of data type.
 * 
 * @author dloy
 */
public class VersionData
{
    private static final String NAME = "VersionData";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    
    protected Identifier objectID = null;
    protected int node = 0;
    protected int current = -1;
    protected VersionMap map = null;
    protected String storageBase = null;
    protected File outputDir = null;
    protected LoggerInf logger = null;
    protected File dataDir = null;
    protected File manifestDir = null;
    protected String mapLink = null;

    public VersionData(URL manifestXML, File outputDir, LoggerInf logger)
        throws TException
    {
        if (manifestXML == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "manifestXML missing");
        }
        this.mapLink = manifestXML.toString();
        this.outputDir = outputDir;
        this.logger = logger;
        try {
            dataDir = new File(outputDir, "data");
            dataDir.mkdir();
            manifestDir = new File(outputDir, "manifest");
            manifestDir.mkdir();
            map = VersionMap.getVersionMap(manifestXML.toString());
            this.current = map.getCurrent();
            this.objectID = map.getObjectID();
            this.node = map.getNode();
            this.storageBase = map.getStorageBase();
            validate();
            System.out.println(dump("manifestXML"));
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

    public VersionData(VersionMap map, File outputDir, LoggerInf logger)
        throws TException
    {
        if (map == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "map missing");
        }
        this.map = map;
        this.outputDir = outputDir;
        this.logger = logger;
        try {
            dataDir = new File(outputDir, "data");
            dataDir.mkdir();
            manifestDir = new File(outputDir, "manifest");
            manifestDir.mkdir();
            this.current = map.getCurrent();
            this.objectID = map.getObjectID();
            this.node = map.getNode();
            this.storageBase = map.getStorageBase();
            validate();
            System.out.println(dump("versionMap"));
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

    public VersionData(Identifier objectID, String storageBase, int node, File outputDir, LoggerInf logger)
        throws TException
    {
        this.objectID = objectID;
        this.storageBase = storageBase;
        this.node = node;
        this.outputDir = outputDir;
        this.logger = logger;
        validate();
        try {
            this.mapLink =  new String(storageBase 
                                + "/manifest"
                                + "/" + node
                                + '/' + URLEncoder.encode(objectID.getValue(), "utf-8")
                                );
            System.out.println("MapLink:" + mapLink);
            map = VersionMap.getVersionMap(mapLink);
            dataDir = new File(outputDir, "data");
            dataDir.mkdir();
            manifestDir = new File(outputDir, "manifest");
            manifestDir.mkdir();
            this.current = map.getCurrent();
            System.out.println(dump("identifier"));
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    private void validate()
        throws TException
    {
        if (this.objectID == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "objectID missing");
        }
        if (this.node <= 0) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "nodemissing");
        }
        if (StringUtil.isAllBlank(this.storageBase)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "storageBase missing");
        }
        if (this.outputDir == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "objectID missing");
        }
        if (this.logger == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "logger missing");
        }
    }
    
    public String dump(String header)
    {
        return "***Dump VersionData -" + header + "***\n"
                + " - mapLink=" + mapLink + "\n"
                + " - objectID=" + objectID.getValue() + "\n"
                + " - storageBase=" + storageBase + "\n"
                + " - node=" + node + "\n"
                + " - current=" + current + "\n";
    }
    
    public int buildManifests()
        throws TException
    {
        try {
            System.out.println("buildManifests entered:" + current);
            int totCnt = 0;
            for (int v=1; v <= current; v++) {
                String name = "version-" + v + ".txt";
                File versionManifest = new File(manifestDir, name);
                int bldCnt = map.buildAddManifest(storageBase + "/content/" + node, v, versionManifest);
                totCnt += bldCnt;
            }
            return totCnt;
                
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
        
    }
    
    public int buildContent()
        throws TException
    {
        try {
            System.out.println("buildContent entered:" + current);
            int totCnt = 0;
            for (int v=1; v <= current; v++) {
                String name = "" + v;
                File versionDir = new File(dataDir, name);
                versionDir.mkdir();
                addComponentsList(v, versionDir);
            }
            return totCnt;
                
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
        
    }
    
    public int copyContent(String copyStorageBase, int copyNode, Identifier copyObjectID)
        throws TException
    {
        try {
            System.out.println("buildManifests entered:" + current);
            StorageAddClient client = new StorageAddClient(logger);
            for (int v=1; v <= current; v++) {
                String name = "version-" + v + ".txt";
                File versionManifest = new File(manifestDir, name);
                Properties response = copyVersion(client, copyStorageBase, copyNode, copyObjectID, versionManifest);
                System.out.println(PropertiesUtil.dumpProperties("***response:" + v, response));
            }
            return current;
                
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
        
    }
    
    public Properties copyVersion(StorageAddClient client, String copyStorageBase, int copyNode, Identifier copyObjectID, File versionManifest)
        throws TException
    {
        try {
            System.out.println("buildManifests entered:" + current);
            Properties retProp = client.sendAddMultipartRetryProperties(
                copyStorageBase,
                copyNode,
                copyObjectID,
                null,
                null,
                versionManifest,
                null,
                versionManifest.length(),
                null,
                null,
                "xml",
                60000,
                3);
            return retProp;
                
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
        
    }
    
    public int addComponentsList(int versionID, File versionDir)
        throws TException
    {
        try {
            List<FileComponent> components = map.getVersionComponents(versionID);
            int versionCnt = 0;
            for (FileComponent component : components) {
                addComponentFile(versionID, versionDir, component);
                versionCnt++;
            }
            return versionCnt;
                
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
        
    }
    
    public File addComponentFile(int versionID, File versionDir, FileComponent component )
        throws TException
    {
        try {
            String id = component.getIdentifier();
            String [] parts = id.split("\\/");
            String name = parts[parts.length - 1];
            File compDir = versionDir;
            for (int p=0; p<(parts.length - 1); p++) {
                compDir = new File(compDir, parts[p]);
                compDir.mkdir();
            }
            File compFile = new File(compDir, name);
            saveComponentContent(versionID, compFile, id, name);
            return compFile;
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
        
    }
    
    public void saveComponentContent(int versionID, File componentFile, String id, String name )
        throws TException
    {
        URL fileLink = null;
        try {
            fileLink = new URL(storageBase
                    + "/content"
                    + '/' + node
                    + '/' + URLEncoder.encode(objectID.getValue(), "utf-8")
                    + '/' + versionID
                    + '/' + URLEncoder.encode(id, "utf-8")
                    );
            if (DEBUG) System.out.println("get URL:" + fileLink);
            FileUtil.url2File(logger, fileLink, componentFile, 3);
            //fileLink = new URL(fileURLS + '/' + manifestURLName); // <-BAD FORM for testing ONLY
        } catch (Exception ex) {
            throw new TException.INVALID_DATA_FORMAT(MESSAGE
                    + "getPOSTManifest"
                    + " - passed URL format invalid: getFileURL=" + fileLink
                    + " - Exception:" + ex);
        }
        
    }

    /**
     * Main method
     */
    public static void main(String args[])
    {
        main4(args);
    }

    /**
     * Main method
     */
    public static void main1(String args[])
    {
        System.out.println(NAME + " entered");
        try
        {
            String propertyList[] = {
                "resources/TestVersionData.properties"};
            TFrame mFrame = new TFrame(propertyList, NAME);
            Properties prop = mFrame.getProperties();
            System.out.println(PropertiesUtil.dumpProperties("test", prop));
            LoggerInf localLogger = mFrame.getLogger();
            String localObjectIDS = mFrame.getProperty("objectID");
            Identifier localObjectID = new Identifier(localObjectIDS);
            String localStorageBase = mFrame.getProperty("storageBase");
            String localOutputDirS = mFrame.getProperty("outputDir");
            File localOutputDir = new File(localOutputDirS);
            String nodeS  = mFrame.getProperty("node");
            int node = Integer.parseInt(nodeS);
            VersionData test = new VersionData(localObjectID, localStorageBase, node, localOutputDir, localLogger);
            test.buildManifests();
            test.buildContent();

        }  catch(Exception ex)  {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
        }
    }

    /**
     * Main method
     */
    public static void main2(String args[])
    {
        System.out.println(NAME + " entered");
        try
        {
            String propertyList[] = {
                "resources/TestVersionData.properties"};
            TFrame mFrame = new TFrame(propertyList, NAME);
            Properties prop = mFrame.getProperties();
            System.out.println(PropertiesUtil.dumpProperties("test", prop));
            LoggerInf localLogger = mFrame.getLogger();
            String manifestURLS = mFrame.getProperty("manifestURL");
            URL manifestURL = new URL(manifestURLS);
            String localOutputDirS = mFrame.getProperty("outputDir");
            File localOutputDir = new File(localOutputDirS);
            VersionData test = new VersionData(manifestURL, localOutputDir, localLogger);
            test.buildManifests();
            test.buildContent();

        }  catch(Exception ex)  {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
        }
    }

    /**
     * Main method
     */
    public static void main3(String args[])
    {
        System.out.println(NAME + " entered");
        try
        {
            String propertyList[] = {
                "resources/TestVersionData.properties"};
            TFrame mFrame = new TFrame(propertyList, NAME);
            Properties prop = mFrame.getProperties();
            System.out.println(PropertiesUtil.dumpProperties("test", prop));
            LoggerInf localLogger = mFrame.getLogger();
            String manifestURLS = mFrame.getProperty("manifestURL");
            URL manifestURL = new URL(manifestURLS);
            String localOutputDirS = mFrame.getProperty("outputDir");
            File localOutputDir = new File(localOutputDirS);
            VersionData test = new VersionData(manifestURL, localOutputDir, localLogger);
            test.buildManifests();
            Identifier copyObjectID = new Identifier("ark:/13030/zzzaa");
            int outVersion = test.copyContent("http://blake.cdlib.org:28080/storage", 50, copyObjectID);
            System.out.println("outVersion=" + outVersion);

        }  catch(Exception ex)  {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
        }
    }
    
    public static void main4(String args[])
    {
        System.out.println(NAME + " entered");
        try
        {
            String propertyList[] = {
                "resources/TestVersionData.properties"};
            TFrame mFrame = new TFrame(propertyList, NAME);
            Properties prop = mFrame.getProperties();
            System.out.println(PropertiesUtil.dumpProperties("test", prop));
            LoggerInf localLogger = mFrame.getLogger();
            String localObjectIDS = mFrame.getProperty("objectID");
            Identifier localObjectID = new Identifier(localObjectIDS);
            String localStorageBase = mFrame.getProperty("storageBase");
            String localOutputDirS = mFrame.getProperty("outputDir");
            File localOutputDir = new File(localOutputDirS);
            String nodeS  = mFrame.getProperty("node");
            int node = Integer.parseInt(nodeS);
            VersionData test = new VersionData(localObjectID, localStorageBase, node, localOutputDir, localLogger);
            test.buildManifests();
            test.buildContent();

        }  catch(Exception ex)  {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
        }
    }

    public Identifier getObjectID() {
        return objectID;
    }

    public int getNode() {
        return node;
    }

    public int getCurrent() {
        return current;
    }

    public VersionMap getMap() {
        return map;
    }

    public String getStorageBase() {
        return storageBase;
    }
    
    
}
