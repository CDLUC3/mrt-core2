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

import org.cdlib.mrt.core.FileComponent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.cdlib.mrt.core.ManifestRowInf;
import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TExceptionEnum;
import org.cdlib.mrt.utility.TRuntimeException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
/**
 * Generalized line based processor where each line is some
 * functional unit.
 * Routine supports the reading and writing of manifest files
 * @author  David Loy
 */
public class Manifest implements Enumeration
{
    protected static final String NAME = "Manifest";
    protected static final String MESSAGE = NAME + ": ";
    protected static final String MANIFESTEOF = "#%eof";

    protected String prefix = "ObjectManifest.";
    protected ManifestRowInf rowFactory = null;
    protected BufferedReader br = null;
    protected OutputStreamWriter m_ow = null;
    protected LoggerInf logger = null;
    protected String line = null;
    protected String prevLine = null;
    protected boolean eof = true;

    /**
     * Manifest Factory
     * @param logger process logger
     * @param rowType row handler
     * @return Manifest object using specific row handler
     * @throws TException
     */
    public static Manifest getManifest(
            LoggerInf logger,
            ManifestRowAbs.ManifestType rowType)
        throws TException
    {
        if (rowType == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "rowType parm is missing");
        }
        ManifestRowInf row = ManifestRowAbs.getManifestRow(rowType, logger);
        return new Manifest(logger, row);
    }

    /**
     * Manifest Factory
     * @param logger process logger
     * @param rowType row handler
     * @return Manifest object using specific row handler
     * @throws TException
     */
    public static Manifest getManifest(
            LoggerInf logger,
            String profile,
            ManifestRowAbs.ManifestType rowType)
        throws TException
    {
        if (rowType == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "rowType parm is missing");
        }
        ManifestRowInf row = ManifestRowAbs.getManifestRow(rowType, profile, logger);
        return new Manifest(logger, row);
    }


    /**
     * Constructor
     * @param logger process logger
     * @param rowFactory row handling object
     * @throws TException process exception
     */
    public Manifest(
            LoggerInf logger,
            ManifestRowInf rowFactory)
        throws TException
    {
        if ((rowFactory == null)
                || (logger == null)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "required parm is missing");
        }
        this.rowFactory = rowFactory;
        this.logger = logger;
    }

    /**
     * Enumeration handling
     * @param inputManifestFile line based manifest file
     * @return Enumeration object
     * @throws TException
     */
    public Enumeration<ManifestRowInf> getRows(File inputManifestFile)
        throws TException
    {
        setFile(inputManifestFile);
        eof = false;
        return this;
    }

    /**
     * Enumeration handling
     * @param inputStream input stream to line based manifest file
     * @return Enumeration object
     * @throws TException
     */
    public Enumeration<ManifestRowInf> getRows(InputStream inputStream)
        throws TException
    {
        setInputStream(inputStream);
        eof = false;
        return this;
    }

    /**
     * More manifest lines to be processed
     * @return true=more lines to process, false=no more lines to process
     */
    @Override
    public boolean hasMoreElements()
    {
        boolean isHeaderArea = true;
        try {
            if (eof) return false;
            Vector<String> headers = new Vector<String>(10);
            while (true) {
                line = br.readLine();
                //System.out.println("!!!!: line=" + line);
                //System.out.println("!!!!: prevLine=" + prevLine);
                if (line == null) {
                    eof = true;
                    br.close();
                    rowFactory.handleEOF(prevLine);
                    return false;

                }

                prevLine = line;
                if (line.length() == 0) continue;
                else if (line.length() > 0) {
                    if (line.startsWith("#%")) {
                        headers.add(line);
                        continue;
                    }
                    if (line.substring(0,1).equals("#")) continue;
                    if (StringUtil.isAllBlank(line)) continue;
                }

                if (isHeaderArea) {
                    rowFactory.handleHeaders(headers.toArray(new String[0]));
                    isHeaderArea = false;
                }
                return true;
            }

        } catch (Exception ex) {
            String msg = MESSAGE + "IOError in hasMoreElements:" + ex;
            logger.logError(msg, 0);
            throw new TRuntimeException.INVALID_OR_MISSING_PARM(msg);
        }
    }

    /**
     * Return manifest row handling object
     * @return row handling object
     */
    @Override
    public ManifestRowInf nextElement()
    {
        if (line == null) {
            String msg = MESSAGE + "nextElement called at EOF";
            logger.logError(msg, 0);
            throw new TRuntimeException.INVALID_OR_MISSING_PARM(msg);
        }
        ManifestRowInf newRow = null;
        try {
            newRow = rowFactory.getManifestRow(line);
            return newRow;
            
        } catch (TException tex) {
	    try {
	       br.close();
	    } catch (Exception e) {}
            String msg = MESSAGE + "new row exception:" + tex;
            logger.logError(msg, 0);
            // System.out.println(StringUtil.stackTrace(tex));
            throw new TRuntimeException.INVALID_OR_MISSING_PARM(msg);
        } catch (Exception ex) {
            String msg = MESSAGE + "new row exception:" + ex;
            logger.logError(msg, 0);
            // System.out.println(StringUtil.stackTrace(ex));
            throw new TRuntimeException.INVALID_OR_MISSING_PARM(msg);
        }
    }

    /**
     * Set manifest file as saved buffered reader
     * @param manifestFile manifest file used for manifest processing
     * @throws TException process exception
     */
    protected void setFile(File manifestFile)
        throws TException
    {

        try {
            FileInputStream fstream = new FileInputStream(manifestFile);
            setInputStream(fstream);

        } catch (Exception ex) {
            logger.logError(MESSAGE + "ProcessList - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "ProcessList - Exception:" + ex);
            
        }
    }

    /**
     * Convert input stream to buffered reader
     * @param inStream input stream for manifest processing
     * @throws TException
     */
    protected void setInputStream(InputStream inStream)
        throws TException
    {

        try {
            DataInputStream in = new DataInputStream(inStream);
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));

        } catch (Exception ex) {
            logger.logError(MESSAGE + "ProcessList - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "setInputStream - Exception:" + ex);

        }
    }

    /**
     * Output file to write new manifest
     * @param outputManifestFile write to this file
     * @throws TException process exception
     */
    public void openOutput(File outputManifestFile)
        throws TException
    {
         try {
            FileOutputStream outputStream = new FileOutputStream(outputManifestFile);
            openOutput(outputStream);
            writeHeaders();

        } catch (Exception ex) {
            logger.logError(MESSAGE + "ProcessList - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "openOutput - Exception:" + ex);

        }
    }

    /**
     * Use output stream for writing out manifest
     * @param outputStream output manifest stream
     * @throws TException process exception
     */
    public void openOutput(OutputStream outputStream)
        throws TException
    {
         try {
            m_ow = new OutputStreamWriter(outputStream, "utf-8");

        } catch (Exception ex) {
            logger.logError(MESSAGE + "ProcessList - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "openOutput - Exception:" + ex);

        }
    }

    /**
     * Write a line to output manifest
     * @param row row handler for formatting line for output
     * @throws TException process exception
     */
    public void write(ManifestRowInf row)
        throws TException
    {
        if (row == null) {
            String msg = MESSAGE + "write - missing row.";
            logger.logError(msg, 0);
            throw new TException.INVALID_OR_MISSING_PARM(msg);
        }
        try {
            String localLine = row.getLine();
            localLine += System.getProperty("line.separator");
            m_ow.write(localLine);

        } catch (Exception ex) {
            logger.logError(MESSAGE + "write - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "ProcessList - Exception:" + ex);
        }
    }

    /**
     * Write this line to output
     * @param localLine formatted line to write to manifest
     * @throws TException
     */
    public void write(String localLine)
        throws TException
    {
        if (StringUtil.isEmpty(localLine)) {
            return;
        }
        try {
            m_ow.write(localLine);

        } catch (Exception ex) {
            logger.logError(MESSAGE + "write - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "ProcessList - Exception:" + ex);
        }
    }

    public void writeHeaders()
        throws TException
    {
        writeLines(rowFactory.getOutputHeaders());
    }

    public void writeEOF()
        throws TException
    {
        write(MANIFESTEOF);
    }

    public void writeLines(String[] lines)
        throws TException
    {
        try {
            for(String line: lines) {
                line += System.getProperty("line.separator");
                m_ow.write(line);
            }

        } catch (Exception ex) {
            logger.logError(MESSAGE + "write - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "ProcessList - Exception:" + ex);
        }
    }

    /**
     * close output stream
     */
    public void closeOutput()
    {
        try {
           m_ow.close();

        } catch (Exception ex) {

        }
    }
    
    /**
     * Main method
     */
    public static void main(String args[])
    {
        TFrame framework = null;
        try
        {
            String propertyList[] = {
                "resources/TFrameLocal.properties"};

            framework = new TFrame(propertyList, NAME);
            String inFileName = framework.getProperty(NAME + ".inFileName");
            File inFile = new File(inFileName);
            String outFileName = framework.getProperty(NAME + ".outFileName");
            File outFile = new File(outFileName);
            LoggerInf logger = framework.getLogger();
            testInput("testInput", inFile, logger);
            testCopy(inFile, outFile, logger);
            
        }  catch(Exception e)  {
            if (framework != null)
            {
                framework.getLogger().logError(
                    "Main: Encountered exception:" + e, 0);
                framework.getLogger().logError(
                        StringUtil.stackTrace(e), 10);
            }
        }
    }

    protected static void testInput(String header, File inFile, LoggerInf logger)
        throws Exception
    {
        Manifest omTest = Manifest.getManifest(logger, ManifestRowAbs.ManifestType.add);
        ManifestRowAdd manRow = null;
        Enumeration en = omTest.getRows(inFile);
        while (en.hasMoreElements()) {
            manRow = (ManifestRowAdd)en.nextElement();
            FileComponent fileState = manRow.getFileComponent();
            logger.logMessage(fileState.dump("testInput")
                    , 0);
        }
    }

    protected static void testCopy(File inFile, File outFile, LoggerInf logger)
        throws Exception
    {
        Manifest manIn = Manifest.getManifest(logger, ManifestRowAbs.ManifestType.add);
        Manifest manOut = Manifest.getManifest(logger, ManifestRowAbs.ManifestType.object);
        manOut.openOutput(outFile);
        ManifestRowAdd manRowIn = null;
        ManifestRowObject manRowOut
                = (ManifestRowObject)ManifestRowAbs.getManifestRow(ManifestRowAbs.ManifestType.object, logger);
        Enumeration en = manIn.getRows(inFile);
        while (en.hasMoreElements()) {
            manRowIn = (ManifestRowAdd)en.nextElement();
            FileComponent fileState = manRowIn.getFileComponent();
            manRowOut.setFileComponent(fileState);
            manOut.write(manRowOut);
            logger.logMessage(fileState.dump(MESSAGE), 0);
        }
        manOut.closeOutput();
        testInput("testCopy", outFile, logger);
    }
}
