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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import javax.xml.transform.*;

import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.XSLTUtil;

/**
 * XML output display formatter
 * @author dloy
 */
public class XHTMLFormatter
        extends FormatterAbs
        implements FormatterInf
{
    protected XMLFormatter xmlFormatter = null;
    protected String mapperName = null;
    protected XMLMapper mapper = null;
    private boolean debug = false;

    public XHTMLFormatter(LoggerInf logger)
            throws TException
    {
        super(logger);
        xmlFormatter = FormatterAbs.getXMLFormatter(logger);
        formatterType = FormatterInf.Format.xhtml;
    }

    public XHTMLFormatter(String mapperName, LoggerInf logger)
            throws TException
    {
        super(logger);
        xmlFormatter = FormatterAbs.getXMLFormatter(mapperName, logger);
        formatterType = FormatterInf.Format.xhtml;
        this.mapperName = mapperName;
    }

    @Override
    public void format(StateInf stateFile, PrintStream stream)
            throws TException
    {
        File tempFile = null;
        File formatFile = null;
        try {
            if (StringUtil.isEmpty(mapperName)) {
                mapperName = "resources/XMLFormatNS.properties";
            }
            if (debug) System.out.println("!!!!" + MESSAGE + "format - mapperNameame=" + mapperName);
            mapper = XMLMapper.getXMLMapper(mapperName, stateFile);
            tempFile = FileUtil.getTempFile("xml", "xml");
            FileOutputStream outStream = new FileOutputStream(tempFile);
            PrintStream xmlStream = new PrintStream(outStream, true, "utf-8");
            xmlFormatter.format(stateFile, xmlStream);
            if (debug) System.out.println("!!!!XHTMLFormatter xml=" + FileUtil.file2String(tempFile));
            formatFile = transform(tempFile);
            writeStream(formatFile, stream);


        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            if (debug) System.out.println("!!!!Exception trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION (
                    "XHTMLFormatter exception:" + ex);
            
        } finally {
            if (tempFile != null) {
                try {
                    tempFile.delete();
                } catch (Exception ex) {}
            }
            if (formatFile != null) {
                try {
                    formatFile.delete();
                } catch (Exception ex) {}
            }
        }
    }

    protected void writeStream(
            File formatFile,
            PrintStream stream)
        throws TException
    {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(formatFile);
            byte [] buf = new byte[10000];
            while (true) {
                int len = inputStream.read(buf);
                if (len < 0) break;
                stream.write(buf, 0, len);
            }
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(
                    "XHTMLFormatter exception:" + ex);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ex) { }
            }
        }

    }

    protected File transform(
            File xmlFile)
        throws TException
    {
        Properties xsltParms = null;
        String stylesheet = mapper.getXHTML();
        String merrittBase = mapper.getSemanticBase();
        if (StringUtil.isNotEmpty(merrittBase)) {
            xsltParms = new Properties();
            xsltParms.setProperty("merrittBase", merrittBase);
        }
        if (debug) System.out.println("!!!!" + MESSAGE + "transform - stylesheet name=" + stylesheet);
        InputStream xslStream =  null;
        InputStream xmlStream =  null;
        try {
            File outTemp = FileUtil.getTempFile("xhtml", "xhtml");
            xslStream =  getClass().getClassLoader().
                getResourceAsStream(stylesheet);
            xmlStream = new FileInputStream(xmlFile);

            String response = XSLTUtil.xslConvert(
                xmlStream,
                xslStream,
                xsltParms,
                logger);
            if (debug) System.out.println("!!!!XHTMLFormatter response=" + response);
            FileUtil.string2File(outTemp, response);
            return outTemp;

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

    @Override
    protected int printBegin(StateInf state, PrintStream stream)
            throws TException
    {
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
    }
}
