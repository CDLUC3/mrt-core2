/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved
*********************************************************************/
package org.cdlib.mrt.utility;

import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xpath.XPathAPI;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




public class DOMParser {
    private static final String NAME = "DOMParser";
    private static final String MESSAGE = NAME + ": ";

    private static final String JAXP_SCHEMA_LANGUAGE
        = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA
        = "http://www.w3.org/2001/XMLSchema";


    /**
     * Return concatinated string text values for all text nodes
     * that are children of this node
     * Contains logger
     *
     * @param elem base element to extract text nodes
     * @param Logger Logger to receive messages, if any
     * @return string containing concatinated text nodes
     */
    public static String getSimpleElementText( Node node, LoggerInf logger )
        throws TException
    {
        try
        {
            if (node == null) return null;
            StringBuffer sb = new StringBuffer();
            NodeList children = node.getChildNodes();
            for(int i=0; i<children.getLength(); i++) {
                Node child = children.item(i);
                if ( child.getNodeType() == Node.TEXT_NODE ) {
                sb.append( child.getNodeValue() );
                }
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            if (logger != null)
            {
                logger.logError (e.getMessage(), 0);
            }
            if (e instanceof TException)
                throw (TException)e;
            else
                throw new TException.GENERAL_EXCEPTION(
                     "DOMParser.getSimpleElementText: " + e.getMessage());
        }
    }

    /**
     * parse xml contained from input stream
     * InputStream
     * Contains logger
     *
     * param Logger Logger to receive messages, if any
     * @return root element node for DOM
     */
    public static Document doParse(InputStream istream, LoggerInf logger)
        throws TException
    {
        return doParse(istream, logger, false, true, false);
    }

    /**
     * parse xml contained from input stream
     * InputStream
     * Contains logger
     *
     * @param istream input stream to be parsed
     * @param logger framework log for output
     * @param validate flag - true=require xml validation, false=don't
     * @param nsAware flag - true=force name space awareness, false=don't
     * @return root element node for DOM
     */
    public static Document doParse(
        InputStream istream, LoggerInf logger, boolean validate, boolean nsAware, boolean expandEntities)
        throws TException
    {

        try {
            DocumentBuilder db = setup(logger, validate, nsAware, expandEntities);
            if (validate == true){
                db.setErrorHandler(new SAXErrorHandler(logger, false));
            }
            Document doc = db.parse(istream);
            istream.close();
            return doc;
        }
        catch (Exception e)
        {
            if (logger != null)
            {
                logger.logError ("DOMParser.doParse: " + e, 0);
            }
            //throw new FrameworkException(ex);
            if (e instanceof TException)
                throw (TException)e;
            else
                throw new TException.GENERAL_EXCEPTION(
                    "DOMParser.doParse: " + e);
        }
    }


    /**
     * get parser
     * Contains logger
     *
     * @param Logger Logger to receive messages, if any
     * @return true=setup worked false=exception
     */
    public static DocumentBuilder setup(
                LoggerInf logger, boolean validate, boolean nsAware, boolean expandEntities)
        throws TException
    {
        DocumentBuilder db = null;

        try {
            DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
            //to make faster, turn off validation
            //this may need to be a switch
            if ( logger != null){
                logger.logMessage ("DOMParser.setup: validate is " + validate, 15);
                logger.logMessage ("DOMParser.setup: nsAware is " + nsAware, 15);
            }
            try {
                dbf.setValidating(validate);

            }
            catch (Exception v){
                if (logger != null){
                    logger.logError ("DOMParser.setup: unable set validate: " + v, 3);
                }
            }
            //we do need namespaces sometimes
            try {
                dbf.setNamespaceAware(nsAware);
            }
            catch (Exception n){
                if (logger != null){
                    logger.logError ("DOMParser.setup: unable set nsAware: " + n, 3);
                }
            }

            if (validate == true){
                try {
                    dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                }
                catch (Exception c){
                    if (logger != null)   {
                        logger.logError ("DOMParser.setup: unable set schema: " + c, 3);
                    }
                }
            }
            //Expand entities ("&something;") or not, depending
            //on parameter
            dbf.setExpandEntityReferences(expandEntities);
            if (logger != null){
                logger.logMessage ("DOMParser.setup: done setting options ", 15);
            }

            db = dbf.newDocumentBuilder();
            if(validate == true){
                try {
                    SAXErrorHandler seh = new SAXErrorHandler(logger, false);
                    db.setErrorHandler(seh);
                }
                catch (Exception d){
                    logger.logError ("DOMParser.setup: unable set errorhandler: " + d, 3);
                }
            }
            return db;

        } catch (Exception ex) {
            if (logger != null)
            {
                logger.logError ("DOMParser.setup: " + ex, 0);
            }
            if (ex instanceof TException)
                throw (TException) ex;
            else
                throw new TException.GENERAL_EXCEPTION(
                    "DOMParser.setup: " + ex);
        }
    }

    /**
     * Same as above, fewer parameters
     *
     * Logging routine not added since the original code for this
     * method did no have a "throw" in the catch (TransformerException te)
     * which indicates that it may not be worth logging.
     *
     * @param node org.w3c.dom.Node to start with
     *        within a document (including a SOAP message)
     * @param String XPath expression to evaluate
     * @return nodeList List of nodes that fit the expression
     */
    public static NodeList getNodeList(
            Node node,
            String expression,
            LoggerInf logger)
        throws TException
    {

        // apply the XPath expression to the node
        // (this uses XPath API within xalan)
        // Documentation of "XObjects" is at
        // http://xml.apache.org/xalan-j/apidocs/org/apache/xpath/objects/XObject.html
        NodeList foundNodes = null;

        try {
            foundNodes = XPathAPI.selectNodeList(node, expression);
        }
        catch (Exception e)
        {
            if (logger != null)
            {
                logger.logError (e.getMessage(), 0);
            }
            if (e instanceof TException)
                throw (TException)e;
            else
                throw new TException.GENERAL_EXCEPTION(
                    "DOMParser.getNodeList: " + e.getMessage());
        }
        return foundNodes;
    }

    /**
     * Stub method:
     * Return first decendant element matching a specific string name
     * (xpath=name[1]
     * @param elem starting element to begin decendant node extraction
     * @param name of child node to be matched
     * @return first element matching name
     */
    public static Element getFirstNode(Element elem, String name )
        throws TException
    {
        //return null;
        return getFirstNode (elem, name, null);
    }

    /**
     * Return first decendant element matching a specific string name
     * (xpath=name[1]
     * Contains logger
     *
     * @param elem starting element to begin decendant node extraction
     * @param name of child node to be matched
     * @param Logger Logger to receive messages, if any
     * @return first element matching name
     */
    public static Element getFirstNode(Element elem, String name, LoggerInf logger)
        throws TException
    {
        try
        {
            NodeList nl = elem.getElementsByTagName(name);
            for (int n=0; n<nl.getLength(); n++) {
                Element element = (Element)nl.item(n);
                return element;
            }
            return null;
        }
        catch (Exception e)
        {
            if (logger != null)
            {
                logger.logError (e.getMessage(), 0);
            }
            if (e instanceof TException)
                throw (TException)e;
            else
                throw new TException.GENERAL_EXCEPTION(
                    "DOMParser.getFirstNode: " + e.getMessage());
        }
    }

    /**
     * Convert node to string
     * @param node Node to be converted
     * @return String
     */
    public static String convertNodeToString(Node node) throws TransformerException {

        // Create dom source for the document
        DOMSource domSource=new DOMSource(node);

        // Create a string writer
        StringWriter stringWriter=new StringWriter();

        // Create the result stream for the transform
        StreamResult result = new StreamResult(stringWriter);

        // Create a Transformer to serialize the document
        TransformerFactory tFactory = getXalanTransformer();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");

        // Transform the document to the result stream
        transformer.transform(domSource, result);
        return stringWriter.toString();
    }

    public static TransformerFactory getXalanTransformer()
    {
        return new org.apache.xalan.processor.TransformerFactoryImpl();
    }

}
