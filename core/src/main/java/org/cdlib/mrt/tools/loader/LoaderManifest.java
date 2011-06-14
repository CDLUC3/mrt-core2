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
package org.cdlib.mrt.tools.loader;




import java.io.File;
import java.net.URL;
import java.util.Vector;

import org.cdlib.mrt.core.FileContent;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.ManifestRowCheckmAbs;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Process required for constructing batch and object manifest data for emulating
 * mets feeder
 * @author loy
 */
public class LoaderManifest
{

    protected static final String NAME = "LoaderManifest";
    protected static final String MESSAGE = NAME + ": ";

    protected static final boolean DEBUG = true;

    protected FileComponent batchComponent = null;
    protected File dataDir = null;
    protected File loaderManifest = null;
    protected URL componentURLBase = null;
    protected String checksumType = null;
    protected LoggerInf logger = null;
    protected Vector<FileComponent> components = new Vector<FileComponent>(100);
    protected long totSize = 0;
    protected long cnt = 0;

    public static LoaderManifest run(
            ManifestRowAbs.ManifestType manifestType,
            String checksumType,
            FileComponent batchComponent,
            File dataDir,
            File loaderManifestFile,
            URL componentURLBase,
            LoggerInf logger)
        throws TException
    {
        try {
            LoaderManifest manifest = new LoaderManifest(
                checksumType,
                batchComponent,
                dataDir,
                loaderManifestFile,
                componentURLBase,
                logger);
            manifest.run(manifestType);
            return manifest;

        } catch (TException ex) {
            return null;

        } catch (Exception ex) {
            return null;
        }
    }

    public LoaderManifest(
            String checksumType,
            FileComponent batchComponent,
            File dataDir,
            File loaderManifest,
            URL componentURLBase,
            LoggerInf logger)
        throws TException
    {
        this.logger = logger;
        this.dataDir = dataDir;
        this.loaderManifest = loaderManifest;
        this.componentURLBase = componentURLBase;
        this.batchComponent = batchComponent;

        if (StringUtil.isEmpty(checksumType)) {
            checksumType = "SHA-256";
        }
        this.checksumType = checksumType;
        if (dataDir == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "dataDir missing");
        }
        if (!dataDir.exists()) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "dataDir does not exist");
        }
        if (loaderManifest == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "loaderManifest missing");
        }
        if (componentURLBase == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "componentURLBase missing");
        }
        if (batchComponent == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "batchComponent missing");
        }
    }

    public void run(ManifestRowAbs.ManifestType manifestType)
        throws TException
    {
        try {
            long compCnt = setComponents();
            if (compCnt == 0) return;
            int lineCnt = buildManifest(manifestType);
            if (lineCnt == 0) return;
            buildBatchManifestRow();

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    /**
     * Build a POST manifest
     * @param fileURLS base URL for deriving manifest fileURLs
     * @param extractDir directory file containing files for manifest generation
     * @param loaderManifest output file to contain INGEST manifest
     * @return accumulated size of files referenced by manifest
     * @throws TException process exception
     */
    public long setComponents()
        throws TException
    {
        try {
            if (componentURLBase == null) {
                String msg = MESSAGE
                    + "getPOSTManifest - base URL not provided";
                throw new TException.INVALID_OR_MISSING_PARM( msg);
            }
            String fileURLS = componentURLBase.toString();
            Vector<File> files = new Vector<File>(1000);
            FileUtil.getDirectoryFiles(dataDir, files);
            if (files.size() == 0) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(MESSAGE + "No items found for:"
                        + dataDir.getCanonicalPath());
            }

            for (File file : files) {
                FileComponent fileState =  addManifestRow(file, checksumType, fileURLS, dataDir);
                components.add(fileState);
                totSize += fileState.getSize();
                cnt++;
            }
            return cnt;

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    public int buildManifest(ManifestRowAbs.ManifestType manifestType)
        throws TException
    {

        try {
            ManifestRowCheckmAbs rowOut
                    = (ManifestRowCheckmAbs)ManifestRowAbs.getManifestRow(manifestType, logger);
            Manifest manifest = Manifest.getManifest(logger, manifestType);
            manifest.openOutput(loaderManifest);

            int lineCnt = 0;
            for (FileComponent fileComponent : components) {
                rowOut.setFileComponent(fileComponent);
                log("getPostManifest-line:" + rowOut.getLine());
                manifest.write(rowOut);
                lineCnt++;
            }
            manifest.writeEOF();
            manifest.closeOutput();
            System.out.println("***MANIFEST***" + FileUtil.file2String(loaderManifest));
            return lineCnt;

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
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
    public FileComponent addManifestRow(
            File file,
            String checksumType,
            String fileURLS,
            File sourceDir)
        throws TException
    {

        try {
            FileContent fileContent = FileContent.getFileContent(file, checksumType, logger);
            FileComponent fileComponent = fileContent.getFileComponent();
            String fileName = file.getCanonicalPath();
            String sourceName = sourceDir.getCanonicalPath();
            fileName = fileName.substring(sourceName.length() + 1);
            fileName = fileName.replace('\\', '/');
            String manifestURLName = FileUtil.getURLEncodeFilePath(fileName);

            log("fileName:" + fileName);
            log("sorsName:" + sourceName);
            fileComponent.setIdentifier(fileName);
            URL fileLink = null;
            try {
                fileLink = new URL(fileURLS + '/' + manifestURLName);
                //fileLink = new URL(fileURLS + '/' + manifestURLName); // <-BAD FORM for testing ONLY
            } catch (Exception ex) {
                throw new TException.INVALID_DATA_FORMAT(MESSAGE
                        + "getPOSTManifest"
                        + " - passed URL format invalid: getFileURL=" + fileURLS
                        + " - Exception:" + ex);
            }
            fileComponent.setURL(fileLink);
            return fileComponent;

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    /**
     * Build the METS component to be added to the component list for an individual object
     * manifest
     * @param info object manifest information
     * @throws TException
     */
    protected FileComponent buildBatchManifestRow()
        throws TException
    {

        try {
            FileContent fileContent = FileContent.getFileContent(loaderManifest, checksumType, logger);
            FileComponent comp = fileContent.getFileComponent();
            batchComponent.setSize(comp.getSize());
            batchComponent.setMessageDigests(comp.getMessageDigests());
            return batchComponent;


        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    protected void log(String msg)
    {
        logger.logMessage(msg, 10);
        if (DEBUG)
            System.out.println(msg);
    }

    public FileComponent getBatchComponent() {
        return batchComponent;
    }

    public long getCnt() {
        return cnt;
    }

    public Vector<FileComponent> getComponents() {
        return components;
    }

    public long getTotSize() {
        return totSize;
    }

}
