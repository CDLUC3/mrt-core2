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
- Neither the resourceName of the University of California nor the resourceNames of its
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.cdlib.mrt.utility.DOMParser;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.XMLUtil;
import org.cdlib.mrt.utility.XSLTUtil;

/**
 * retrieve data if ns is a manifest
 * @author loy
 */
public class DataciteConvert
{

    protected static final String NAME = "DataciteConvert";
    protected static final String MESSAGE = NAME + ": ";

    protected static final boolean DEBUG = false;
    
    public enum StyleEnum {

    kernel2_1("http://datacite.org/schema/kernel-2.1", 
            "http://schema.datacite.org/meta/kernel-2.1/metadata.xsd", 
            "kernel2.1_to_oaidc.xsl", 
            "2.1"),
    kernel2_2("http://datacite.org/schema/kernel-2.2", 
            "http://schema.datacite.org/meta/kernel-2.2/metadata.xsd", 
            "kernel2.2_to_oaidc.xsl", 
            "2.2"),
    kernel2_3("http://datacite.org/schema/kernel-2.3", 
            "http://schema.datacite.org/meta/kernel-2.3/metadata.xsd", 
            "kernel2.3_to_oaidc.xsl", 
            "2.3"),
    kernel3_1("http://datacite.org/schema/kernel-3.1", 
            "http://schema.datacite.org/meta/kernel-3/metadata.xsd", 
            "kernel3.1_to_oaidc.xsl", 
            "3.1"),
    kernel3("http://datacite.org/schema/kernel-3", 
            "http://schema.datacite.org/meta/kernel-3/metadata.xsd", 
            "kernel3_to_oaidc.xsl", 
            "3.1");

    //kernelerr("http://datacite.org/schema/kernel-2.2", "kernel3_to_oaidc.xsl", "2.1"),
    protected String ns = null;
    protected String schema = null;
    protected String resourceName = null;
    protected String version = null;

        StyleEnum(String ns, String schema, String resourceName, String version)
        {
            this.ns = ns;
            this.schema = schema;
            this.resourceName = resourceName;
            this.version = version;
        }

        /**
         * Return the storage ns
         * @return storage ns
         */
        public String getNS()
        {
            return this.ns;
        }

        /**
         * Return DataCite schema
         * @return DataCite schema
         */
        public String getSchema() {
            return schema;
        }

        /**
         * return the description of the storage
         * @return storage description
         */
        public String getResourceName()
        {
            return this.resourceName;
        }

        /**
         * return DataCite version
         * @return DataCite version
         */
        public String getVersion() {
            return version;
        }

        /**
         * Match the Storage ns to ns and description
         * @param ns storage ns
         * @param resourceName storage description
         * @return StyleEnum
         */
        public static StyleEnum matchValue(String value)
        {
            if (StringUtil.isEmpty(value)) return null;
            for (StyleEnum p : StyleEnum.values()) {
                if (value.contains(p.getNS())) {
                    return p;
                }
            }
            return null;
        }
    }
    
    /**
     * Convert DataCite format to standard dc
     * @param mods MODS xml string
     * @param logger file logger
     * @return OAI dc xml String
     * @throws TException process exception
     */
    public static String dataCite2dc(
            String dataCite,
            LoggerInf logger)
        throws TException
    {
        InputStream xslStream =  null;
        InputStream xmlStream =  null;
        DataciteConvert temp = new DataciteConvert();
        try {
            if (DEBUG) System.out.println("REPLACE:" + dataCite);
            StyleEnum se = StyleEnum.matchValue(dataCite);
            if (se == null) {
                throw new TException.INVALID_DATA_FORMAT(
                        MESSAGE + "no stylesheet found for:\n"
                        + dataCite);
            }
            String resourceName = se.getResourceName();
            System.out.println("***" + MESSAGE + "resourceName=" + resourceName);
            xslStream =  temp.getClass().getClassLoader().
                getResourceAsStream("resources/stylesheets/" + resourceName);
            xmlStream = new ByteArrayInputStream(dataCite.getBytes("utf-8"));

            String response = XSLTUtil.xslConvert(
                xmlStream,
                xslStream,
                null,
                logger);
            
            if ((response == null) || (response.length() == 0) || !response.contains("<")) {
                throw new TException.INVALID_DATA_FORMAT(
                        MESSAGE + "unable to convert:\n"
                        + dataCite);
            }
            if (DEBUG) System.out.println("!!!!XHTMLFormatter response=" + response);
            return response;

        }  catch (TException tex) {
            System.out.println(StringUtil.stackTrace(tex));
            throw tex;

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
}
