/*
Copyright (c) 2005-2009, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

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
*********************************************************************/
package org.cdlib.mrt.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.core.FileContent;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.ManifestRowAdd;
import org.cdlib.mrt.core.Tika;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;


/**
 * Tool for creating a Storage POST manifest used in addVersion from all files
 * in a specific directory
 * @author  David Loy
 */
public class ManifestBuild
{

    protected static final boolean DEBUG = false;
    protected static final String NAME = "ManifestBuild";
    protected static final String MESSAGE = NAME + ": ";
    protected static LoggerInf logger = null;
    protected TFrame mFrame = null;
    protected PrintStream printOut  = null;
    protected File listFile = null;
    protected File propFile = null;
    protected Properties sizeProp = new Properties();

    public ManifestBuild(TFrame mFrame)
    {
        this.mFrame = mFrame;
    }

    // for API use
    public ManifestBuild() {
        try {
            String propertyList[] = {"resources/TFrameCmd.properties"};
            mFrame = new TFrame(propertyList, NAME);
            logger = mFrame.getLogger();
	} catch (Exception e) {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
	}
    }

    /**
     * Main method
     */
    public static void main(String args[])
    {
        System.out.println(NAME + " entered");
        try
        {
            String propertyList[] = {
                "testresources/TestLocal.properties"};
            TFrame mFrame = new TFrame(propertyList, NAME);
            logger = mFrame.getLogger();
            ManifestBuild test = new ManifestBuild(mFrame);
            test.run();

        }  catch(Exception e)  {
            if (logger != null)
            {
                logger.logError(
                    "Main: Encountered exception:" + e, 0);
                logger.logError(
                        StringUtil.stackTrace(e), 10);
            }
        }
    }

    protected void initialize(TFrame mFrame)
        throws TException
    {

    }

    /**
     * Loop through list of data directories to have manifest generation
     */
    protected void run()
    {
        BufferedReader br = null;
        try {
            String listName = mFrame.getProperty(NAME + ".listName");
            log("listName=" + listName);
            listFile = new File(listName);
            if (!listFile.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "listName missing");
            }
            String propName = mFrame.getProperty(NAME + ".propName");
            log("propName=" + propName);
            if (propName != null) {
                propFile = new File(propName);
            }
            FileInputStream fis = new FileInputStream(listName);
            br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
            String line = null;
            int item = 0;
            while (true) {
                item++;
                line = br.readLine();
                if (line == null) break;
                String[] items = line.split("\\+");
                if ((items == null) || (items.length < 4)) {
                    throw new TException.REQUEST_INVALID(
                            MESSAGE + "entry format invalid: line=" + line
                            + " - linecnt=" + items.length);
                }
                String toManifest = items[0];
                String manifestURLS = items[1];
                File sourceDir = new File(items[2]);
                File postManifest = new File(items[3]);
                log("sourceDir:" + sourceDir.getCanonicalPath());
                log("postManifest:" + postManifest.getCanonicalPath());
                PropInfo propInfo = getPostManifest(manifestURLS, sourceDir, postManifest, logger);
                sizeProp.setProperty("size." + item, "" + propInfo.size);
                sizeProp.setProperty("cnt." + item, "" + propInfo.cnt);
                sizeProp.setProperty("manifest." + item, items[0]);
            }
            String propString = PropertiesUtil.buildLoadProperties(sizeProp);
            if (propFile != null) {
                FileUtil.string2File(propFile, propString);
            }

        }  catch(Exception e)  {
                log("Main: Encountered exception:" + e);
                log(StringUtil.stackTrace(e));
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ex) {}
            }
        }
    }

    /**
     * Build a POST manifest
     * @param fileURLS base URL for deriving manifest fileURLs
     * @param sourceDir directory file containing files for manifest generation
     * @param postManifest output file to contain POST manifest
     * @return accumulated size of files referenced by manifest
     * @throws TException process exception
     */
    public static PropInfo getPostManifest(
            String fileURLS,
            File sourceDir,
            File postManifest)
        throws TException
    {
        return getPostManifest(fileURLS, sourceDir, postManifest, null);
    }

    /**
     * Build a POST manifest
     * @param fileURLS base URL for deriving manifest fileURLs
     * @param sourceDir directory file containing files for manifest generation
     * @param postManifest output file to contain POST manifest
     * @param logger output logger - null=use System.out
     * @return accumulated size of files referenced by manifest
     * @throws TException process exception
     */
    public static PropInfo getPostManifest(
            String fileURLS,
            File sourceDir,
            File postManifest,
            LoggerInf logger)
        throws TException
    {
        PropInfo pInfo = new PropInfo();
        ManifestRowAbs.ManifestType manifestType = ManifestRowAbs.ManifestType.add;
        try {
            if (logger == null) {
                logger = new TFileLogger("ManifestBuild.getPostManifest", 1000, 1000);
            }
            if (StringUtil.isEmpty(fileURLS)) {
                String msg = MESSAGE
                    + "getPOSTManifest - base URL not provided";
                throw new TException.INVALID_OR_MISSING_PARM( msg);
            }
            Vector<File> files = new Vector<File>(1000);
            FileUtil.getDirectoryFiles(sourceDir, files);
            if (files.size() == 0) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(MESSAGE + "No items found for:"
                        + sourceDir.getCanonicalPath());
            }
            Tika tika = Tika.getTika(logger);
            ManifestRowAdd rowOut
                    = (ManifestRowAdd)ManifestRowAbs.getManifestRow(manifestType, logger);
            Manifest manifestDflat = Manifest.getManifest(logger, manifestType);
            manifestDflat.openOutput(postManifest);
            long totSize = 0;
            long cnt = 0;
            for (File file : files) {
                FileContent fileContent = FileContent.getFileContent(file, logger);
                FileComponent fileState = fileContent.getFileComponent();
                String fileName = file.getCanonicalPath();
                String sourceName = sourceDir.getCanonicalPath();
                fileName = fileName.substring(sourceName.length() + 1);
                String urlName = FileUtil.getURLEncodeFilePath(fileName);
                fileName = fileName.replace('\\', '/');
                if (DEBUG) {
                    log("fileName:" + fileName);
                    log("sorsName:" + sourceName);
                    log("urlFName:" + urlName);
                }
                fileState.setIdentifier(fileName);
                URL fileLink = null;
                try {
		    // URL encode link as necessary
                    fileLink = new URL(fileURLS + '/' + urlName);
                } catch (Exception ex) {
                    throw new TException.INVALID_DATA_FORMAT(MESSAGE
                            + "getPOSTManifest"
                            + " - passed URL format invalid: getFileURL=" + fileURLS
                            + " - Exception:" + ex);
                }
                fileState.setURL(fileLink);
                try {
                    String tikaString = tika.getMimeType(file);
                    fileState.setMimeType(tikaString);
                } catch (java.lang.NoSuchMethodError nsme) {
                    nsme.printStackTrace();
                } catch (Exception e) { e.printStackTrace(); }

                rowOut.setFileComponent(fileState);
                if (DEBUG) System.out.println("!!!!line:" + rowOut.getLine());
                manifestDflat.write(rowOut);
                totSize += fileState.getSize();
                cnt++;
            }
            pInfo.size = totSize;
            pInfo.cnt = cnt;
            manifestDflat.writeEOF();
            manifestDflat.closeOutput();
            return pInfo;

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
ex.printStackTrace();
            //logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            //logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    protected static void log(String msg)
    {
        System.out.println(msg);
    }

    public static class PropInfo {
        public long cnt = 0;
        public long size = 0;
    }
}
