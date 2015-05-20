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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;


import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import org.cdlib.mrt.cloud.ManInfo;
import org.cdlib.mrt.cloud.VersionMap;
import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.jdom.Namespace;
import org.jdom.output.Format;

/**
 * This object imports the formatTypes.xml and builds a local table of supported format types.
 * Note, that the ObjectFormat is being deprecated and replaced by a single format id (fmtid).
 * This change is happening because formatName is strictly a description and has no functional
 * use. The scienceMetadata flag is being dropped because the ORE Resource Map is more flexible
 * and allows for a broader set of data type.
 * 
 * @author dloy
 */
public class ManifestXML
{
    private static final String NAME = "ObjectFormatXML";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    
    protected VersionMap versionMap = null;
    //protected Document doc = null;
    protected LoggerInf logger = null;

    public static final Namespace uc3Name = Namespace.getNamespace("http://uc3.cdlib.org/ontology/mrt/manifest");
    public static VersionMap getVersionMap(Identifier objectID, LoggerInf logger, InputStream xmlStream)
           throws TException
    {
        VersionMap versionMap = new VersionMap(objectID, logger);
        
        try {
            if (xmlStream == null) return versionMap;
            Document doc =  getDocument(xmlStream);
            if (DEBUG) System.out.println("Doc built");
            extractObjectInfo(doc, versionMap);
            if (versionMap.getCurrent() > 0) {
                extractManifests(doc, versionMap);
                versionMap.rebuildHash();
            }
            return versionMap;

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    /**
     * Input the formatTypes.xml and build a Document
     * @throws TException
     */
    protected static Document getDocument(InputStream inStream)
        throws TException
    {
        try {
            SAXBuilder builder = new SAXBuilder();
            if (DEBUG) System.out.println("Doc built");
            return builder.build(inStream);

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) { }
            }
        }
    }
    
    protected static void extractObjectInfo(Document doc, VersionMap versionMap)
        throws TException
    {
        
        try {
            Element root = doc.getRootElement();
            Element objectE = root.getChild("object", uc3Name);           
            if (objectE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "object element missing");
            }
            Attribute idA = objectE.getAttribute("id");
            if (idA == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "object attribute id missing");
            }
            String idS = idA.getValue();
            
            Element currentE = objectE.getChild("current", uc3Name);
            Element fileCountE = objectE.getChild("fileCount", uc3Name);
            Element totalSizeE = objectE.getChild("totalSize", uc3Name);
            Element actualCountE = objectE.getChild("actualCount", uc3Name);
            Element actualSizeE = objectE.getChild("actualSize", uc3Name);
            Element versionCountE = objectE.getChild("versionCount", uc3Name);
            Element lastAddVersionE = objectE.getChild("lastAddVersion", uc3Name);
            Element lastDeleteVersionE = objectE.getChild("lastDeleteVersion", uc3Name);
            
            if (currentE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "current version missing");
            }
            
            if (fileCountE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "fileCount missing");
            }
            if (totalSizeE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "totalSize missing");
            }
            if (actualCountE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "actualCount missing");
            }
            if (actualSizeE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "actualSize missing");
            }
            if (versionCountE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "versionCount missing");
            }
            if (lastAddVersionE == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "lastAddVersionE missing");
            }
            int current = getValue(currentE);
            int fileCount = getValue(fileCountE);
            long totalSize = getValueLong(totalSizeE);
            int actualCount = getValue(actualCountE);
            long actualSize = getValueLong(actualSizeE);
            int versionCount = getValue(versionCountE);
            
            versionMap.setCurrent(current);
            versionMap.setOriginalActualCount(actualCount);            
            versionMap.setOriginalFileCount(fileCount);            
            versionMap.setOriginalActualSize(actualSize);            
            versionMap.setOriginalTotalSize(totalSize);
            versionMap.setOriginalVersionCnt(versionCount);
            versionMap.setOriginalVersionCnt(versionCount);
            
            String lastAddVersionS = lastAddVersionE.getValue();
            DateState lastAddVersion = new DateState(lastAddVersionS);
            versionMap.setOriginalLastAddVersion(lastAddVersion);
            versionMap.setLastAddVersion(lastAddVersion);
            
            if (lastDeleteVersionE != null) {
                String lastDeleteVersionS = lastAddVersionE.getValue();
                DateState lastDeleteVersion = new DateState(lastDeleteVersionS);
                versionMap.setOriginalLastDeleteVersion(lastDeleteVersion);
                versionMap.setLastDeleteVersion(lastDeleteVersion);
            }
            

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected static int getValue(Element elem)
        throws Exception
    {
        String valS = elem.getValue();
        return Integer.parseInt(valS);
    }
    
    protected static long getValueLong(Element elem)
        throws Exception
    {
        String valS = elem.getValue();
        return Long.parseLong(valS);
    }
    
    public String dump(String header) {
        StringBuffer buf = new StringBuffer();
        versionMap.dump("ManifestXml");
        buf.append(versionMap.dump("versionMap"));
        return buf.toString();
    }
    
    public String dumpVersionMap(String header) {
        return versionMap.dump(header);
    }
        
    /**
     * Extract the 3 elements of the FormatObject from this xml and build table
     * @throws TException
     */
    protected static void extractManifests(Document doc, VersionMap versionMap)
        throws TException
    {
        try {
            XPath xpath = XPath.newInstance("//x:manifest");
            xpath.addNamespace("x", uc3Name.getURI());
            List list = xpath.selectNodes(doc);
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Element elem = (Element) iter.next();
                ManInfo info = new ManInfo();
                Attribute countA = elem.getAttribute("count");
                Attribute sizeA = elem.getAttribute("size");
                Attribute createdA = elem.getAttribute("created");               
                info.elem = elem;
                if (countA == null) {
                    throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "manifest missing count");
                }
                if (sizeA == null) {
                    throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "manifest missing size");
                }
                if (createdA == null) {
                    throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "manifest missing created");
                }
                info.size = sizeA.getLongValue();
                info.count = countA.getIntValue();
                
                String createdS = createdA.getValue();
                if (StringUtil.isNotEmpty(createdS)) {
                    info.created = new DateState(createdS);
                }
                info.count = countA.getIntValue();
                Element version = elem.getParentElement();
                if (version == null) {
                    throw new TException.INVALID_ARCHITECTURE("version doesn't exist");
                }
                Attribute versionIDA = version.getAttribute("id");
                if (versionIDA == null) {
                    throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "manifest missing count");
                }
                info.versionID = versionIDA.getIntValue();
                info.components = getVersionContent(info.versionID, info);
                if (DEBUG) System.out.println("SIZE=" + info.components.size()); //!!!
                versionMap.add(info);
                //System.out.println(info.dump("manifest"));
            }
            if (versionMap.getVersionCount() == 0) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "No manifest content found");
                
            }
        } catch (TException tex) {
            System.out.println(MESSAGE + "Exception:" + tex);
            tex.printStackTrace();
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected static ComponentContent getVersionContent(int versionID, ManInfo info)
         throws TException
    {
        ArrayList<FileComponent> components = new ArrayList<FileComponent>();
        
        try {
            if (info == null) return null;
            Element manE = info.elem;
           
            XPath xpath = XPath.newInstance("x:file");
            xpath.addNamespace("x", uc3Name.getURI());
            List list = xpath.selectNodes(manE);
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Element fileElem = (Element) iter.next();
                FileComponent component = getComponent(fileElem);
                components.add(component);
                if (DEBUG) System.out.println("getVesionContent:" + component.getIdentifier());
            }
            ComponentContent content = new ComponentContent(components);
            content.setVersionID(versionID);
            return content;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public static FileComponent getComponent(Element fileElem)
         throws TException
    {
        
        try {
            if (false) throw new TException.UNIMPLEMENTED_CODE("test");
            Attribute idA = fileElem.getAttribute("id");
            String id = idA.getValue();
            if (id.substring(0,1).equals("/") || id.substring(0,1).equals("\\")) {
                id = id.substring(1);
            }
            FileComponent component = new FileComponent();
            component.setIdentifier(id);
            if (DEBUG) System.out.println("Call getComponent:" + id);
            String digestType = getValue(fileElem, "digestType");
            String digest = getValue(fileElem, "digest");
            String sizeS = getValue(fileElem, "size");
            String creationDateS = getValue(fileElem, "creationDate");
            String key = getValue(fileElem, "key");
            component.addMessageDigest(digest, digestType);
            component.setSize(sizeS);
            component.setLastModifiedDate(creationDateS);
            component.setLocalID(key);
            Element mimeTypeE = fileElem.getChild("mimeType", uc3Name);
            if (mimeTypeE != null) {
                component.setMimeType(mimeTypeE.getValue());
            }
            
            
            return component;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected static String getValue(Element fileElem, String name)
        throws Exception
    {
        Element elem = fileElem.getChild(name, uc3Name);
        if (elem == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "element not found for key:" + name);
        }
        return elem.getValue();
        
    }

    
    public static Element buildManifestEntry(ManInfo info)
        throws TException
    {
        try {
            List<FileComponent> components = info.components.getFileComponents();
            Element manifest = new Element("manifest", uc3Name);
            manifest.setAttribute("count", "" + info.count);
            manifest.setAttribute("size", "" + info.size);
            manifest.setAttribute("created", "" + info.created.getIsoDate());
            for (FileComponent component : components) {
                Element componentElem = getComponentElem(component);
                manifest.addContent(componentElem);
            }
            return manifest;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public static Element getComponentElem(FileComponent component)
        throws TException
    {
        try {
            Element fileElem = new Element("file", uc3Name);
            String id = component.getIdentifier();
            if (id.substring(0,1).equals("/") || id.substring(0,1).equals("\\")) {
                id = id.substring(1);
            }
            if (StringUtil.isAllBlank(id)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "getComponentElem - identifier is empty");
            }
            fileElem.setAttribute("id", id);
            
            MessageDigest digest = component.getMessageDigest();
            if (digest == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "getComponentElem - digest missing");
            }
            String digestType = digest.getJavaAlgorithm();
            String digestValue = digest.getValue();
            Element digestTypeE = new Element("digestType", uc3Name);
            digestTypeE.addContent(digestType);
            fileElem.addContent(digestTypeE);
            Element digestValueE = new Element("digest", uc3Name);
            digestValueE.addContent(digestValue);
            fileElem.addContent(digestValueE);
            
            long size = component.getSize();
            if (size < 0 ) {
                if (digestType.equals("sha256") && digestValue.equals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")) {
                    // empty file
                    size = 0;
                } else {
                    System.out.println("WARNING: size not supplied - assumed zero length:" + id);
                    size = 0;
                }
            }
            Element sizeE = new Element("size", uc3Name);
            sizeE.addContent("" + size);
            fileElem.addContent(sizeE);
            
            DateState creation = component.getCreated();
            if (creation == null ) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "getComponentElem - creation date missing");
            }
            Element creationE = new Element("creationDate", uc3Name);
            creationE.addContent(creation.getIsoDate());
            fileElem.addContent(creationE);
            
            String mimeType = component.getMimeType();
            if (StringUtil.isNotEmpty(mimeType)) {
                Element mimeTypeE = new Element("mimeType", uc3Name);
                mimeTypeE.addContent(mimeType);
                fileElem.addContent(mimeTypeE);
            }
            
            String key = component.getLocalID();
            if (StringUtil.isAllBlank(key)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "getComponentElem - key missing");
            }
            Element keyE = new Element("key", uc3Name);
            keyE.addContent(key);
            fileElem.addContent(keyE);
            
            return fileElem;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public static String elementToString(Element elem)
        throws TException
    {
        try {
            Format format = Format.getCompactFormat().setIndent("  ");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(elem, bos);
            return bos.toString("utf-8");

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public static void buildOut(VersionMap versionMap,OutputStream out)
        throws TException
    {
        try {
            Document outDoc = new Document();
            Namespace name = Namespace.getNamespace("http://uc3.cdlib.org/ontology/mrt/manifest");
            Element outEle = new Element("objectInfo", uc3Name);
            outDoc.setRootElement(outEle);
            Element objectE = getObjectElem(versionMap);
            outEle.addContent(objectE);
            Element versionsE = getVersionsElem(versionMap);
            outEle.addContent(versionsE);
            String outS = elementToString(outEle);
            if (DEBUG) System.out.println("\nOUTPUT BuildOut:\n" + outS);
            byte[] outB = outS.getBytes("utf-8");
            out.write(outB);
            
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            try {
                out.close();
            } catch (Exception ex) { }
        }
    }
    
    protected static Element getObjectElem(VersionMap versionMap)
        throws TException
    {
        try {
            Element objectO = new Element("object", uc3Name);
            objectO.setAttribute("id", versionMap.getObjectID().getValue());
                
            Element currentO = new Element("current", uc3Name);
            currentO.addContent("" + versionMap.getCurrent());
            
            Element fileCountO = new Element("fileCount", uc3Name);
            fileCountO.addContent("" + versionMap.getTotalCnt());
            
            Element totalSizeO = new Element("totalSize", uc3Name);
            totalSizeO.addContent("" + versionMap.getTotalSize());
            
            Element actualCountO = new Element("actualCount", uc3Name);
            actualCountO.addContent("" + versionMap.getActualCnt());
            
            Element actualSizeO = new Element("actualSize", uc3Name);
            actualSizeO.addContent("" + versionMap.getActualSize());
            
            Element versionCountO = new Element("versionCount", uc3Name);
            versionCountO.addContent("" + versionMap.getVersionCount());
            
            Element lastAddVersionO = null;
            if (versionMap.getLastAddVersion() != null) {
                lastAddVersionO = new Element("lastAddVersion", uc3Name);
                lastAddVersionO.addContent(versionMap.getLastAddVersion().getIsoDate());
            }
            
            Element lastDeleteVersionO = null;
            if (versionMap.getLastDeleteVersion() != null) {
                System.out.println(MESSAGE + "lastDeleteVersion not null");
                lastDeleteVersionO = new Element("lastDeleteVersion", uc3Name);
                lastDeleteVersionO.addContent(versionMap.getLastDeleteVersion().getIsoDate());
            }
            
            objectO.addContent(currentO);
            objectO.addContent(fileCountO);
            objectO.addContent(totalSizeO);
            objectO.addContent(actualCountO);
            objectO.addContent(actualSizeO);
            objectO.addContent(versionCountO);
            if (lastAddVersionO != null) objectO.addContent(lastAddVersionO);
            if (lastDeleteVersionO != null) objectO.addContent(lastDeleteVersionO);
            return objectO;
            
        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected static Element getVersionsElem(VersionMap versionMap)
        throws TException
    {
        try {
            Element versionsE = new Element("versions", uc3Name);
            for (int iv = 1; true; iv++) {
                ManInfo info = versionMap.getVersionInfo(iv);
                if (info == null) break;
                Element versionE = new Element("version", uc3Name);
                versionE.setAttribute("id", "" + iv);
                versionsE.addContent(versionE);
                //Element manifestE = getManifestElem(info);
                Element manifestE = buildManifestEntry(info);
                versionE.setContent(manifestE);
            }
            return versionsE;
            
        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected static void addComponent(Element fileE, String name, String value)
        throws TException
    {
        try {
            Element nameE = new Element(name, uc3Name);
            fileE.setContent(nameE);
            nameE.addContent(value);
            
        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
       
    public ManInfo getManInfo(int inx)
    {
        return versionMap.getManInfo(inx);
    }
       
    public int getVersionCnt()
    {
        return versionMap.size();
    }
    
    public static void main(String[] args) throws TException {
        /*
         * Important: Be sure to fill in your AWS access credentials in the
         *            AwsCredentials.properties file before you try to run this
         *            sample.
         * http://aws.amazon.com/security-credentials
         */
        ManInfo test = new ManInfo();
        InputStream propStream =  test.getClass().getClassLoader().
                getResourceAsStream("resources/TestProperties.properties");
        if (propStream == null) {
            System.out.println("Unable to find resource");
            return;
        }
        Properties xmlProp = new Properties();
        try {
            xmlProp.load(propStream);
            String xmlFileS = xmlProp.getProperty("xmlfile");
            System.out.println("Test file:" + xmlFileS);
            LoggerInf logger = new TFileLogger(NAME, 50, 50);
            File file = new File(xmlFileS);
            InputStream inStream = new FileInputStream(file);
            Identifier manID = new Identifier("ark:/13030/ghijk");
            VersionMap map = ManifestXML.getVersionMap(manID, logger, inStream);
            System.out.println("cnt=" + map.getVersionCount());
            System.out.println(map.dump("routine dump"));
            buildOut(map, null);
            
            testOut(map, 1, 3);
            testOut(map, 2, 4);
            
            System.out.println("**************************Test Delete**********************");
            map.deleteCurrent();
            buildOut(map, null);
            
            try {
                System.out.println("Test bad current");
                testOut(map,1,5);
            } catch (Exception ex) {
                System.out.println("TEST Exception:" + ex);
            }
            
            
            Identifier manNewID = new Identifier("ark:/13030/abcde");
            VersionMap newMap = getVersionMap(manNewID, logger, null);
            ManInfo addInfo = map.getVersionInfo(1);
            newMap.addVersion(addInfo.components.getFileComponents());
            buildOut(newMap, null);
/*          
            for (int i=0; true; i++) {
                ManInfo info = manXML.getManInfo(i);
                if (info == null) break;
                System.out.println(info.dump("dump[" + i + "]"));
            }
            for (int i=1; i<=manXML.getCurrent(); i++) {
                ArrayList<FileComponent> components = manXML.getVersionContent(i);
                dumpComponents(i, components);
            }
            
            //***********output
            manXML.testOut(1);
            manXML.testOut(2);
            
            System.out.println("****OUTPUT original****");
            manXML.buildOut(null);
            
            ArrayList<FileComponent> testContent = manXML.getVersionContent(1);
            manXML.addVersion(3, new DateState(), testContent);
            System.out.println("****OUTPUT add 3****");
            System.out.println(manXML.dumpVersionMap("versionMap"));
            manXML.buildOut(null);
            
            ArrayList<FileComponent> testContent2 = manXML.getVersionContent(2);
            manXML.addVersion(4, new DateState(), testContent2);
            System.out.println("****OUTPUT add 4****");
            System.out.println(manXML.dumpVersionMap("versionMap"));
            manXML.buildOut(null);

            manXML.deleteCurrent();
            System.out.println("****OUTPUT delete 4****");
            System.out.println(manXML.dumpVersionMap("versionMap"));
            manXML.buildOut(null);
            
            Identifier objectIDLocal = new Identifier("ark:/13030/ghijk");
            ManifestStaticXML manNew = new ManifestStaticXML(objectIDLocal, logger);
            ArrayList<FileComponent> testContentNew = manXML.getVersionContent(1);
            manNew.addVersion(1, new DateState(), testContentNew);
            System.out.println("****OUTPUT NEW****");
            System.out.println(manXML.dumpVersionMap("versionMap"));
            manNew.buildOut(null);
            
            try {
                manNew.deleteCurrent();
            } catch (Exception ex) {
                System.out.println("Delete a single version item:" + ex.toString());
            }
            */
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("NAME=" + ex.getClass().getName());
            System.out.println("Exception:" + ex);
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            
        }
        
    }
    
    public static void testOut(VersionMap map, int versionID, int versionOutID)
        throws TException
    {
        System.out.println("*************testOut:"
                + " - versionID=" + versionID
                + " - versionOutID=" + versionOutID
                );
        map.addTest(versionID, versionOutID);
        buildOut(map, null);
    }
    
    public static void dumpComponents(int versionID, ArrayList<FileComponent> components)
    {
        System.out.println("***Dump version:" + versionID);
        for (FileComponent component : components) {
            System.out.println(component.dump(component.getIdentifier()));
        }
        System.out.println("****************\n");
    }
}
