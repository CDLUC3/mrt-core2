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
package org.cdlib.mrt.formatter;



import com.hp.hpl.jena.rdf.model.*;


import java.io.PrintStream;
import java.util.Vector;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * XML output display formatter
 * @author dloy
 */
public class JENAFormatter
        extends FormatterAbs
        implements FormatterInf
{

    public final static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    protected XMLMapper mapper = null;
    protected Model model = null;
    protected String ns = null;
    protected String nsPrefix = null;
    protected String refName = null;
    protected String idName = null;
    protected String id = null;
    protected String objectRef = null;
    protected LinkedHashList<String, String> map = new LinkedHashList<String, String>(100);
    protected String mapperName = null;

    public JENAFormatter(LoggerInf logger)
            throws TException
    {
        super(logger);
        formatterType = FormatterInf.Format.xml;
    }

    public JENAFormatter(String mapperName, LoggerInf logger)
            throws TException
    {
        super(logger);
        formatterType = FormatterInf.Format.xml;
        this.mapperName = mapperName;
    }

    @Override
    protected int printBegin(StateInf state, PrintStream stream)
            throws TException
    {
        if (StringUtil.isEmpty(mapperName)) {
            mapperName = "resources/RDFFormatter.properties";
        }
        mapper = XMLMapper.getXMLMapper(mapperName, state);

        // initialize
        model = ModelFactory.createDefaultModel();

        ns = mapper.getNameSpaceURI();
        if (StringUtil.isEmpty(ns)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE
                    + "JENAFormatter - namespace not found");
        }
        nsPrefix = mapper.getNameSpacePrefix();
        if (StringUtil.isEmpty(nsPrefix)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE
                    + "JENAFormatter - namespace prefix not found");
        }

        refName = mapper.getResourceName();
        idName = mapper.getIDName();
        if (StringUtil.isEmpty(refName) && StringUtil.isEmpty(idName)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE
                    + "JENAFormatter - either refname or idName must be supplied");
        }

        return 0;
    }

    @Override
    protected void printEnd(PrintStream stream)
            throws TException
    {
        Resource object = null;
        if (StringUtil.isNotEmpty(objectRef)) {
            object = model.createResource(objectRef);

        } else if (StringUtil.isNotEmpty(id)) {
            object = model.createResource(ns + id);

        } else {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE
                    + "JENAFormatter - neither refname nor idName found");
        }
        model.setNsPrefix(nsPrefix, ns);

        
        Vector<String> values = null;
        String value = null;
        for (String key: map.keySet()) {
            Property localProp = ResourceFactory.createProperty(ns + key);
            values = map.get(key);

            for (int i=0; i<values.size(); i++) {
                value = values.get(i);
                if (value.contains("http://")) {
                    object.addProperty(localProp, model.createResource(value));
                } else {
                    long val = 0;
                    try {
                        val = Long.parseLong(value);
                        object.addLiteral(localProp, val);
                    } catch (Exception ex) {
                        object.addLiteral(localProp, value);
                    }
                }
            }
        
        /*
            if (values.size() == 1) {
                value = values.get(0);
                object.addLiteral(localProp, value);

            } else {
                Bag bag = model.createBag();
                object.addProperty(localProp, bag);
                for (int i=0; i<values.size(); i++) {
                    value = values.get(i);
                    bag.add(value);
                }
            }
        */
        }
        String jenaFormat = null;
        switch (formatterType) {
            case rdf: jenaFormat = "RDF/XML"; break;
            case turtle: jenaFormat = "TURTLE"; break;
        }
        if (StringUtil.isEmpty(jenaFormat)) {
            throw new TException.INVALID_OR_MISSING_PARM("JENAFormatter.printEnd - format type not found");
        }
        model.write(stream, jenaFormat);
    }

    @Override
    protected void printStart(String name, boolean isFirst, int lvl, PrintStream stream)
            throws TException
    {
    }

    @Override
    protected void printClose(String name, int lvl, PrintStream stream)
            throws TException
    {
    }

    @Override
    protected void print(
            String name,
            String value,
            boolean isFirst,
            boolean isNumeric,
            int lvl,
            PrintStream stream)
        throws TException
    {
        if (value == null) return;
        map.put(name, value);
        if (StringUtil.isNotEmpty(refName) && name.equals(refName)) {
            objectRef = value;
        }
        if (StringUtil.isNotEmpty(idName) && name.equals(idName)) {
            id = value;
        }
    }
}