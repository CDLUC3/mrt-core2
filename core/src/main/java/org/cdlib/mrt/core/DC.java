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
**********************************************************/
package org.cdlib.mrt.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.cdlib.mrt.utility.DOMParser;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.XMLUtil;
import org.cdlib.mrt.utility.XSLTUtil;

/**
 * retrieve data if type is a manifest
 * @author loy
 */
public class DC
{

    protected static final String NAME = "DC";
    protected static final String MESSAGE = NAME + ": ";

    protected static final boolean DEBUG = false;

    protected DC() { }

    /**
     * Build a LinkedHashList containing DC values extracted from METS
     * @param metsDoc METS Document
     * @param logger file logger
     * @return list of (repeating) DC values
     * @throws TException process exception
     */
    public static LinkedHashList getDC (
            Document metsDoc,
            LoggerInf logger)
        throws TException
    {

        try {

            Element root = metsDoc.getDocumentElement();
            LinkedHashList<String, String> returnList = new LinkedHashList<String, String>();
            //extract all of the file elements
            NodeList list = null;
            //to accommodate possibility that namespace is not default,
            //use xpath local-name for this whole section.
            list = DOMParser.getNodeList(root,
                    "/*[local-name()='mets']" +
                    "/*[local-name()='dmdSec']" +
                    "/*[local-name()='mdWrap']" +
                    "/*[local-name()='xmlData']",
                logger);
            int size = list.getLength();
            Element fileNode = null;
            if (DEBUG) System.out.println("***size=" + size);

            if (size == 0) {
                throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "METS does not contain dmdSec");
            }
            
            for (int i=0; i<size; i++) {
                fileNode = (Element)list.item(i);
                NodeList listMODS = DOMParser.getNodeList(fileNode, "*[local-name()='mods']", logger);
                int modsSize = listMODS.getLength();
                if (modsSize > 0) {
                    return getDCFromModsMets(metsDoc, logger);
                }
                NodeList listDC = DOMParser.getNodeList(fileNode, "*[local-name()='qualifieddc']", logger);
                int dcSize = listDC.getLength();
                if (dcSize > 0) {
                    return getMetsDC(metsDoc, logger);
                }
                continue;
            }

            return returnList;


        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
        }
    }

    /**
     * Extract DC from METS
     * @param mets METS record
     * @param logger file logger
     * @return list of DC values
     * @throws TException
     */
    public static LinkedHashList getMetsDC (
            Document mets,
            LoggerInf logger)
        throws TException
    {

        try {

            Element root = mets.getDocumentElement();
            LinkedHashList<String, String> returnList = new LinkedHashList<String, String>();
            //extract all of the file elements
            NodeList list = null;
            //to accommodate possibility that namespace is not default,
            //use xpath local-name for this whole section.
            list = DOMParser.getNodeList(root,
                    "/*[local-name()='mets']" +
                    "/*[local-name()='dmdSec']" +
                    "/*[local-name()='mdWrap']" +
                    "/*[local-name()='xmlData']" +
                    "/*[local-name()='qualifieddc']/*",
                logger);
            int size = list.getLength();
            Element fileNode = null;
            if (DEBUG) System.out.println("***size=" + size);

            if (size == 0) {
                throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "METS does not contain dmdSec");
            }
            for (int i=0; i<size; i++) {
                fileNode = (Element)list.item(i);
                String name = fileNode.getLocalName();
                String content = DOMParser.getSimpleElementText(fileNode, logger);
                content = XMLUtil.decode(content);
                returnList.put(name, content);
            }
            return returnList;

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
        }
    }


    /**
     * From a string DC build list of DC values
     * @param dc String form of DC
     * @param logger file Logger
     * @return list of dc values
     * @throws TException process exception
     */
    public static LinkedHashList<String, String> getOAIDC (
            String dc,
            LoggerInf logger)
        throws TException
    {
        try {
            ByteArrayInputStream bas = new ByteArrayInputStream(dc.getBytes("utf-8"));
            Document doc = DOMParser.doParse(bas, logger);
            Element root = doc.getDocumentElement();
            LinkedHashList<String, String> returnList = new LinkedHashList<String, String>();
            //extract all of the file elements
            NodeList list = null;
            //to accommodate possibility that namespace is not default,
            //use xpath local-name for this whole section.
            list = DOMParser.getNodeList(root,
                "/*[local-name()='dc']/*",
                logger);
            int size = list.getLength();
            Element fileNode = null;
            if (DEBUG) System.out.println("***size=" + size);

            if (size == 0) {
                return returnList;
            }
            for (int i=0; i<size; i++) {
                fileNode = (Element)list.item(i);
                String name = fileNode.getLocalName();
                String content = DOMParser.getSimpleElementText(fileNode, logger);
                content = XMLUtil.decode(content);
                if (StringUtil.isNotEmpty(content)) {
                    returnList.put(name, content);
                }
            }
            if (returnList.size() == 0) {
                throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "DC contains no data");
            }
            return returnList;

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
        }
    }

    /**
     * From a DOM METS containing MODS return list of DC values
     * @param mets METS for MODS/DC extraction
     * @param logger file logger
     * @return list of DC values
     * @throws TException
     */
    public static LinkedHashList<String, String>  getDCFromModsMets (
            Document mets,
            LoggerInf logger)
        throws TException
    {

        try {

            String mods = DC.getMetsMods(mets, logger);
            if (DEBUG) System.out.println("getDCFromModsMets MODS:" + mods);
            String dc = DC.mods2dc(mods, logger);
            LinkedHashList<String, String> list = getOAIDC(dc, logger);
            return list;

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
        }
    }

    /**
     * Create a MODS xml extracting from METS
     * @param mets METS DOM
     * @param logger file logger
     * @return MODS as XML String
     * @throws TException process exception
     */
    public static String getMetsMods (
            Document mets,
            LoggerInf logger)
        throws TException
    {

        try {

            Element root = mets.getDocumentElement();
            NodeList list = null;
            //to accommodate possibility that namespace is not default,
            //use xpath local-name for this whole section.
            list = DOMParser.getNodeList(root,
                    "/*[local-name()='mets']" +
                    "/*[local-name()='dmdSec']" +
                    "/*[local-name()='mdWrap']" +
                    "/*[local-name()='xmlData']" +
                    "/*[local-name()='mods']",
                logger);
            int size = list.getLength();
            if (DEBUG) System.out.println("***size=" + size);

            if (size == 0) {
                throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "METS does not contain mods");
            }

            Element modsNode = (Element)list.item(0);
            String mods = DOMParser.convertNodeToString(modsNode);
            if (DEBUG) System.out.println("MODS:" + mods);
            return mods;

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
        }
    }

    public static void dumpList(
            String header,
            Element root,
            String path,
            LoggerInf logger)
        throws TException
    {
        System.out.println("DUMPLIST HEADER:" + header);
        try {

            String name = null;
            NodeList list = DOMParser.getNodeList(
                    root,
                path,
                logger);
            int size = list.getLength();
            if (size ==  0) {
                System.out.println("Empty list");
                return;
            }
            Element fileNode = null;

            for (int i=0; i<size; i++) {
                fileNode = (Element)list.item(i);
                name = fileNode.getNodeName();
                String content = DOMParser.getSimpleElementText(fileNode, logger);
                System.out.println(name + "=" + content);
            }

        } catch (TException fe) {
            throw fe;

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
            throw new TException(e);
        }
    }

    /**
     * Convert a MODS XML into a DC XML String
     * @param mods MODS xml string
     * @param logger file logger
     * @return OAI dc xml String
     * @throws TException process exception
     */
    public static String mods2dc(
            String mods,
            LoggerInf logger)
        throws TException
    {
        InputStream xslStream =  null;
        InputStream xmlStream =  null;
        DC temp = new DC();
        try {
            mods = mods.replace("\"http://www.loc.gov/mods/\"", "\"http://www.loc.gov/mods/v3\"");
            mods = mods.replace("\"http://www.loc.gov/mods\"", "\"http://www.loc.gov/mods/v3\"");
            mods = setModsNS(mods);
            if (DEBUG) System.out.println("REPLACE:" + mods);
            xslStream =  temp.getClass().getClassLoader().
                getResourceAsStream("resources/stylesheets/mods2dc.xsl");
            xmlStream = new ByteArrayInputStream(mods.getBytes("utf-8"));

            String response = XSLTUtil.xslConvert(
                xmlStream,
                xslStream,
                null,
                logger);
            response = response.replace("::", ":");
            response = response.replace("\\", "");
            if (DEBUG) System.out.println("!!!!XHTMLFormatter response=" + response);
            return response;

        }  catch (Exception e) {
            System.out.println(StringUtil.stackTrace(e));
            throw new TException.GENERAL_EXCEPTION(
                    "XHTMLFormatter exception:" + e);

        } finally {
            if (xslStream != null) {
                try {
                    xslStream.close();
                } catch (Exception ex) { }
            }
            if (xmlStream != null) {
                try {
                    xmlStream.close();
                } catch (Exception ex) { }
            }

        }
    }

    /**
     * Set a V3 name space in the MODS xml
     * @param xml MODS xml that might not contain a name space
     * @return xml with V3 name space
     * @throws TException process exception
     */
    public static String setModsNS(
            String xml)
        throws TException
    {
        try {
            int pos = xml.indexOf(":mods>");
            if (pos < 0) return xml;
            int loc = pos;
            for (loc=pos; loc>=0; loc--) {
                if (xml.charAt(loc) == '<') break;
            }
            if (loc >= 0) {
                if (xml.charAt(loc + 1) == '/') return xml;
                String prefix = xml.substring(loc + 1, pos);
                String ns = "xmlns:" + prefix + "=\"http://www.loc.gov/mods/v3\"";
                xml = xml.replace("<" + prefix + ":mods>",
                        "<" + prefix + ":mods " + ns + ">");
            }
            return xml;

        }  catch (Exception e) {
            System.out.println(StringUtil.stackTrace(e));
            throw new TException.GENERAL_EXCEPTION(
                    "setModsNS exception:" + e);
        }
   }
}
