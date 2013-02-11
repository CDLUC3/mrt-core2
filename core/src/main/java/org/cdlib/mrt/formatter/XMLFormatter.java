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

import java.io.PrintStream;
import org.cdlib.mrt.utility.XMLUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * XML output display formatter
 * @author dloy
 */
public class XMLFormatter
        extends FormatterAbs
        implements FormatterInf
{
    protected XMLMapper mapper = null;
    protected String mapperName = null;

    public XMLFormatter(LoggerInf logger)
            throws TException
    {
        super(logger);
        formatterType = FormatterInf.Format.xml;
    }

    public XMLFormatter(String mapperName, LoggerInf logger)
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
            mapperName = "resources/XMLFormatNS.properties";
        }
        mapper = XMLMapper.getXMLMapper(mapperName, state);
        writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", stream);
        return 0;
    }

    @Override
    protected void printEnd(PrintStream stream)
            throws TException
    {
    }

    @Override
    protected void printStart(String name, boolean isFirst, int lvl, PrintStream stream)
            throws TException
    {

        if (isFirst && (lvl==0)) {}
        else write(NL, stream);
        addLvl(lvl, stream);
        name = XMLUtil.encodeName(name);
        if (isFirst && (lvl==0)) {
            String header = mapper.getHeader(name);
            write("<" + header + ">", stream);
        } else {
            name = mapper.getName(name);
            write("<" + name + ">", stream);
        }
    }

    @Override
    protected void printClose(String name, int lvl, PrintStream stream)
            throws TException
    {
        write(NL, stream);
        addLvl(lvl, stream);
        name = XMLUtil.encodeName(name);
        name = mapper.getName(name);
        write("</" + name + ">", stream);
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
        if (value == null) value = "";
        write(NL, stream);
        addLvl(lvl, stream);
        value = XMLUtil.encodeValue(value);
        name = XMLUtil.encodeName(name);
        name = mapper.getName(name);
        write("<" + name + ">"+ value + "</" + name + ">", stream);
    }
}
