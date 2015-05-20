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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.commons.lang3.StringEscapeUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ArrayList;


import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import org.cdlib.mrt.cloud.ManInfo;
import static org.cdlib.mrt.cloud.ManifestXML.buildOut;
import static org.cdlib.mrt.cloud.ManifestXML.getVersionMap;
import static org.cdlib.mrt.cloud.ManifestXML.testOut;
import org.cdlib.mrt.cloud.VersionMap;
import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.FileUtil;
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
public class ManifestSAX
    extends DefaultHandler
{
    private static final String NAME = "ManifestSAX";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUGLOW = false;
    private static final boolean DEBUGHIGH = false;
    
    protected VersionMap versionMap = null;
    //protected Document doc = null;
    protected LoggerInf logger = null;
    protected SAXParser saxParser = null;
    protected Properties currentProp = null;
    protected ManInfo manInfo = null;
    protected FileComponent fileComponent = null;
    protected ArrayList<FileComponent> components = null;
    protected String key = null;
    protected String value = null;
    
    
   public static void main(String[] args) throws TException {
        
        mainOriginal(args);
    }
    
    public static void mainTestBadMan(String[] args) throws TException {
        /*
         * Important: Be sure to fill in your AWS access credentials in the
         *            AwsCredentials.properties file before you try to run this
         *            sample.
         * http://aws.amazon.com/security-credentials
         */
        
        try {
            String xmlFileS = "/replic/tasks/150429-manprob/man.xml"; //!!!
            System.out.println("Test file:" + xmlFileS);
            LoggerInf logger = new TFileLogger(NAME, 50, 50);
            File file = new File(xmlFileS);
            InputStream inStream = new FileInputStream(file);
            //Identifier manID = new Identifier("ark:/13030/ghijk");
            VersionMap map = ManifestSAX.buildMap(inStream, logger);
            int current = map.getCurrent();
            System.out.println(map.dump("SAX"));
            File tmp = FileUtil.getTempFile("tmp", ".txt");
            String base = "http://mystore:9999/content/9010";
            for (int ver=1; ver<=current; ver++) {
                int cnt = map.buildAddManifest(base, ver, tmp);
                if (true) {
                    String out = FileUtil.file2String(tmp);
                    System.out.println("***ADDMAP(" + ver + "):" + cnt + "\n"
                            + out
                            );
                }
            }
            
            File tmpFile = FileUtil.getTempFile("xxx", ".xml");
            ManifestStr.buildManifest(map, tmpFile);
            String tmpString = FileUtil.file2String(tmpFile);
            System.out.println("*****************STR:\n" + tmpString);
            
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
    
    public static void mainOriginal(String[] args) throws TException {
        /*
         * Important: Be sure to fill in your AWS access credentials in the
         *            AwsCredentials.properties file before you try to run this
         *            sample.
         * http://aws.amazon.com/security-credentials
         */
        ManInfo test = new ManInfo();
        InputStream propStream =  test.getClass().getClassLoader().
                getResourceAsStream("testresources/ManifestXMLProperties.properties");
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
            //Identifier manID = new Identifier("ark:/13030/ghijk");
            VersionMap map = ManifestSAX.buildMap(inStream, logger);
            int current = map.getCurrent();
            System.out.println(map.dump("SAX"));
            File tmp = FileUtil.getTempFile("tmp", ".txt");
            String base = "http://mystore:9999/content/9010";
            for (int ver=1; ver<=current; ver++) {
                int cnt = map.buildAddManifest(base, ver, tmp);
                if (true) {
                    String out = FileUtil.file2String(tmp);
                    System.out.println("***ADDMAP(" + ver + "):" + cnt + "\n"
                            + out
                            );
                }
            }
            
            File tmpFile = FileUtil.getTempFile("xxx", ".xml");
            ManifestStr.buildManifest(map, tmpFile);
            String tmpString = FileUtil.file2String(tmpFile);
            System.out.println("*****************STR:\n" + tmpString);
            
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
    
    public static VersionMap buildMap(InputStream inStream, LoggerInf logger)
        throws TException
    {
        ManifestSAX manifestSAX = new ManifestSAX(logger);
        manifestSAX.process(inStream);
        return manifestSAX.getVersionMap();
    }
    
    public ManifestSAX(LoggerInf logger)
        throws TException
    {
        try {
            this.logger = logger;
            SAXParserFactory factory = SAXParserFactory.newInstance();
            saxParser = factory.newSAXParser();
            versionMap = new VersionMap(null, logger);
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }
    
    public void process(InputStream input)
        throws TException
    {
        try {
            saxParser.parse(input, this);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception cex) { }
            }
        }
    }

    public void startElement(String uri, String localName,String qName, 
            Attributes attributes) throws SAXException 
    {
        if (DEBUGLOW) System.out.println("startElement"
                + " - uri=" + uri
                + " - localName=" + localName
                + " - qName=" + qName
                );
        value = "";
        if (qName.equals("object")) {
            versionMap.setObjectID(toID(attributes.getValue("id")));
            currentProp = new Properties();
            return;
        }
        if (qName.equals("version")) {
            manInfo = new ManInfo();
            manInfo.versionID = toInt(attributes.getValue("id"));
            currentProp = new Properties();
            return;
        }
        if (qName.equals("manifest")) {
            manInfo.count = toInt(attributes.getValue("count"));
            manInfo.size = toLong(attributes.getValue("size"));
            manInfo.created = toDate(attributes.getValue("created"));
            components = new ArrayList();
            return;
        }
        if (qName.equals("file")) {
            fileComponent = new FileComponent();
            String id = attributes.getValue("id");
            id  = StringEscapeUtils.unescapeXml(id);
            fileComponent.setIdentifier(id);
            if (DEBUGHIGH && !attributes.getValue("id").equals(fileComponent.getIdentifier())) {
                System.out.println("***************ManifestSAX:"
                        + "\n - attr=" + attributes.getValue("id")
                        + "\n - file=" + fileComponent.getIdentifier()
                );
            }
            currentProp = new Properties();
            return;
        }
        key = qName;
    }

    public void endElement(String uri, String localName,
            String qName) throws SAXException 
    {
        try {
            if (qName.equals(key)) {
                currentProp.setProperty(key, value);
                return;
            }
            if (qName.equals("object")) {
                setObject();
                return;
            }
            if (qName.equals("version")) {
                manInfo.setComponents(manInfo.versionID, components);
                versionMap.add(manInfo);
                return;
            }
            if (qName.equals("file")) {
                setFile(fileComponent);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("qName=" + qName + " - exception:" + ex.toString());
        }

    }

    public void characters(char ch[], int start, int length) throws SAXException 
    {
        String temp = new String(ch, start, length);
        //temp = temp.trim();
        value += temp;
        if (DEBUGLOW) System.out.println(" ** "
                + " - start=" + start
                + " - length=" + length
                + " - value=" + value
                );
    }

    protected void setObject()
    {
        versionMap.setCurrent(toPropInt("current"));
        versionMap.setOriginalFileCount(toPropInt("fileCount"));
        versionMap.setOriginalTotalSize(toPropLong("totalSize"));
        //versionMap.setActualCnt(toPropInt("actualCount"));
        //versionMap.setActualSize(toPropLong("actualSize"));
        versionMap.setLastAddVersion(toPropDate("lastAddVersion"));
        versionMap.setLastDeleteVersion(toPropDate("lastDeleteVersion"));
        
        if (DEBUGLOW) System.out.println("*********lastAddVersion:" + versionMap.getLastAddVersion().getIsoDate());
    }

    protected void setFile(FileComponent fileComponent)
    {
        try {
            String digestType = currentProp.getProperty("digestType");
            String digestValue = currentProp.getProperty("digest");
            if (DEBUGLOW) System.out.println(" ** "
                    + " - digestType=" + digestType
                    + " - digestValue=" + digestValue
                    );
            MessageDigest digest = new MessageDigest(digestValue, digestType);
            fileComponent.addMessageDigest(digestValue, digestType);
            fileComponent.setSize(toPropLong("size"));
            fileComponent.setCreated(toPropDate("creationDate"));
            String tmpkey = currentProp.getProperty("key");
            tmpkey = StringEscapeUtils.unescapeXml(tmpkey);
            fileComponent.setLocalID(tmpkey);
            if (DEBUGHIGH && !currentProp.getProperty("key").equals(fileComponent.getLocalID())) {
                System.out.println("***************ManifestSAX:"
                        + "\n - prop=" + currentProp.getProperty("key")
                        + "\n - key-=" + fileComponent.getLocalID()
                );
            }
            String mimeType = currentProp.getProperty("mimeType");
            if (!StringUtil.isAllBlank(mimeType)) {
                fileComponent.setMimeType(mimeType);
            }
            if (DEBUGHIGH) System.out.println(fileComponent.dump("setFile"));
            components.add(fileComponent);


        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("setFile fileComponent=" + fileComponent.getIdentifier() + " - exception:" + ex.toString());
        }
    }

    protected int toPropInt(String inkey)
    {
        String value = currentProp.getProperty(inkey);
        return toInt(value);
    }

    protected long toPropLong(String inkey)
    {
        String value = currentProp.getProperty(inkey);
        return toLong(value);
    }

    protected Identifier toPropID(String inkey)
    {
        String value = currentProp.getProperty(inkey);
        return toID(value);
    }

    protected DateState toPropDate(String inkey)
    {
        try {
            String value = currentProp.getProperty(inkey);
            return toDate(value);

        } catch (Exception ex) {
            throw new RuntimeException("toID key=" + key + " - exception:" + ex.toString());
        }
    }

    protected int toInt(String value)
    {
        if (StringUtil.isAllBlank(value)) return 0;
        return Integer.parseInt(value);
    }

    protected long toLong(String value)
    {
        if (StringUtil.isAllBlank(value)) return 0;
        return Long.parseLong(value);
    }

    protected Identifier toID(String value)
    {
        try {
            if (StringUtil.isAllBlank(value)) return null;
            return new Identifier(value);              

        } catch (Exception ex) {
            throw new RuntimeException("toID key=" + key + " - exception:" + ex.toString());
        }
    }

    protected DateState toDate(String value)
    {
        try {
            if (StringUtil.isAllBlank(value)) return null;
            DateState date = new DateState(value);
            return date;              

        } catch (Exception ex) {
            throw new RuntimeException("toID key=" + key + " - exception:" + ex.toString());
        }
    }

    public VersionMap getVersionMap() {
        return versionMap;
    }
};