/*********************************************************************
    Copyright 2004 Regents of the University of California
    All rights reserved
*********************************************************************/
package org.cdlib.mrt.utility;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * This class is a collection of XML Utility functions.
 * They are all static and so can be called without instanciating this class.
 *
 * @author  David Loy
 */
public class XSLTUtil
{
    /**
     * Converts a standard text string so it can be included as XML text
     * @param text String to be converted for inclusion within XML
     * @return XML predefined entity encoded string
     */
    public static String charToEntity(String text)
    {
        char c = '\0';
        if (text == null) return null;
        StringBuffer buf = new StringBuffer(text.length() * 3);
        for (int i=0; i < text.length(); i++) {
            c = text.charAt(i);
            switch (c) {
                case '<': buf.append("&lt;"); break;
                case '>': buf.append("&gt;"); break;
                case '&': buf.append("&amp;"); break;
                case '\'': buf.append("&apos;"); break;
                case '"': buf.append("&quot;"); break;
                default: buf.append(c);
            }
        }
        return buf.toString();
    }

   
    public static String xslConvert(
            String xml,
            String idXSL,
            Hashtable parmTable,
            LoggerInf logger)
        throws TException
    {
        String message = "XSLUtil - xslConvert:";
        if ((xml == null)
            || (xml.length() == 0)
            || (idXSL == null))
        {
            throw new TException.INVALID_OR_MISSING_PARM(
                message + "Missing parameter for XSL processing");
        }

        StreamSource sourceXML = new StreamSource(new StringReader(xml));
        StreamSource sourceXSL = new StreamSource(idXSL);
        return xslConvert(sourceXML, sourceXSL, parmTable, logger);
    }

    /**
     * Convert xml using an xslt stylesheet
     *
     * @param dip the DI Package to be formated
     * @param parmTable table of parms to be applied to style sheet
     * @return Transformmed METS for zip formatted response
     */
    public static String xslConvert(
            InputStream xmlIS,
            String idXSL,
            Hashtable parmTable,
            LoggerInf logger)
        throws TException
    {
        String message = "XSLUtil - xslConvert:";
        if (xmlIS == null || idXSL == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                message + "Missing parameter for XSL processing");
        }

        StreamSource sourceXML = new StreamSource(xmlIS);
        StreamSource sourceXSL = new StreamSource(idXSL);
        return xslConvert(sourceXML, sourceXSL, parmTable, logger);
    }

    /**
     * Convert xml using an xslt stylesheet
     *
     * @param xmlIS XML input stream to be formatted
     * @param xslIS XSL input stream
     * @param parmTable table of parms to be applied to style sheet
     * @param logger LoggerInf for messages
     * @return Transformmed XML in string format
     */
    public static String xslConvert(
            InputStream xmlIS,
            InputStream xslIS,
            Hashtable parmTable,
            LoggerInf logger)
        throws TException
    {
        String message = "XSLUtil - xslConvert:";
        if (xmlIS == null || xslIS == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                message + "Missing parameter for XSL processing");
        }

        StreamSource sourceXML = new StreamSource(xmlIS);
        StreamSource sourceXSL = new StreamSource(xslIS);
        return xslConvert(sourceXML, sourceXSL, parmTable, logger);
    }

    /**
     * Convert xml using an xslt stylesheet
     *
     * @param dip the DI Package to be formated
     * @param parmTable table of parms to be applied to style sheet
     * @return Transformmed METS for zip formatted response
     */
    public static String xslConvert(
            StreamSource sourceXML,
            StreamSource sourceXSL,
            Hashtable parmTable,
            LoggerInf logger)
        throws TException
    {
        String message = "XSLUtil - xslConvert:";
        try {

            if (
                (sourceXSL == null)
                || (sourceXML == null)
                ) {
                throw new TException.INVALID_OR_MISSING_PARM(
                    message + "Missing parameter for XSL processing");
            }

            StringWriter writerString = new StringWriter();
            StreamResult resultXML = new StreamResult(writerString);

            TransformerFactory transformerFactory =
                TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(sourceXSL);

            setParms(transformer, parmTable);
            transformer.transform(sourceXML, resultXML);
            String result = writerString.toString();

            return result;

        }  catch(TException fe) {
            if (logger != null)
            {
                logger.logError (fe.getMessage(), 0);
            }
            throw fe;

        } catch(Exception e) {
            TException fe = new TException.GENERAL_EXCEPTION(e);
            if (logger != null)
            {
                logger.logError (e.getMessage(), 0);
            }
            throw fe;
        }

    }

    /**
     * set transform parameters based on url query
     *
     * @param transform xalan transformer
     * @param table key-value table
     *
     * @return xml with specially formatted entities
     */
    public static void setParms(Transformer transform, Hashtable table)
    {
        if ((table == null) || (transform == null)) return;
        String name = null;
        String value = null;
        Enumeration keys = table.keys();
        while (keys.hasMoreElements()) {
            name = (String)keys.nextElement();
            value = (String)table.get(name);
            transform.setParameter(name, value);
        }
    }

}
