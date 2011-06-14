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

import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.core.ManifestRowBatch;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Process required for constructing batch and object manifest data for emulating
 * mets feeder
 * @author loy
 */
public class BatchManifest
{

    protected static final String NAME = "BatchManifest";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected String checksumType = null;
    protected Vector<LoaderInfo> extractDirectories = null;
    protected File outputManifestDirectory = null;
    protected URL outputManifestBaseURL = null;
    protected URL componentURLBase = null;
    protected URL outputManifestURL = null;
    
    protected File outputBatchFile = null;
    protected LoggerInf logger = null;
    protected final String BATCHNAME = "batch-manifest.txt";

    public static BatchManifest run(
            String checksumType,
            Vector<LoaderInfo> extractDirectories,
            File outputManifestDirectory,
            URL outputManifestBaseURL,
            URL componentURLBase,
            LoggerInf logger)
        throws TException
    {
        try {
            System.out.println("run entered");
            BatchManifest manifest = new BatchManifest(
                checksumType,
                extractDirectories,
                outputManifestDirectory,
                outputManifestBaseURL,
                componentURLBase,
                logger);
            manifest.run();
            return manifest;

        } catch (TException tex) {
            System.out.println("TException:" + tex);
            throw tex;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public BatchManifest(
            String checksumType,
            Vector<LoaderInfo> extractDirectories,
            File outputManifestDirectory,
            URL outputManifestBaseURL,
            URL componentURLBase,
            LoggerInf logger)
        throws TException
    {
        this.logger = logger;
        this.checksumType = checksumType;
        this.extractDirectories = extractDirectories;
        this.outputManifestDirectory = outputManifestDirectory;
        this.outputManifestBaseURL = outputManifestBaseURL;
        this.componentURLBase = componentURLBase;

        if (StringUtil.isEmpty(checksumType)) {
            checksumType = "SHA-256";
        }
        this.checksumType = checksumType;
        if (extractDirectories == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "extractDirectories missing");
        }
        if (extractDirectories.size() == 0) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "extractDirectories is empty");
        }
        if (outputManifestDirectory == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestDirectory missing");
        }
        if (!outputManifestDirectory.exists()) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestDirectory does not exist");
        }
        if (outputManifestBaseURL == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "outputManifestBaseURL missing");
        }
        if (componentURLBase == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "componentURLBase missing");
        }


        outputBatchFile = new File(outputManifestDirectory, BATCHNAME);
        try {
            outputManifestURL = new URL(outputManifestBaseURL, BATCHNAME);
        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);
        }
    }

    public void run()
        throws TException
    {
        int pcnt = buildContentManifests();
        if (pcnt > 0) {
            buildBatchManifest();
        }
    }

    protected int buildContentManifests()
        throws TException
    {
        System.out.println(MESSAGE + "buildContentManifests size=" + extractDirectories.size());
        int cnt = 0;
        for (int i=0; i < extractDirectories.size(); i++) {
            LoaderManifest manifest = buildLoaderManifest(i);
            if (manifest != null) cnt++;
        }
        return cnt;
    }

    protected LoaderManifest buildLoaderManifest(int item)
        throws TException
    {
        File ingestFile = null;
        URL ingestURL = null;

        LoaderInfo info = extractDirectories.get(item);
        try {
            String manifestName = "manifest." + item + ".txt";
            ingestFile = new File(outputManifestDirectory, manifestName);
            ingestURL = new URL(outputManifestBaseURL.toString() + "/" +  manifestName);
            FileComponent batchComponent = new FileComponent();
            batchComponent.setLocalID(info.localID);
            batchComponent.setIdentifier(info.primaryID);
            batchComponent.setTitle(info.title);
            batchComponent.setCreator(info.creator);
            batchComponent.setURL(ingestURL);
            batchComponent.setIdentifier(manifestName);
            batchComponent.setComponentFile(ingestFile);

            info.loaderManifest = LoaderManifest.run(
                    ManifestRowAbs.ManifestType.ingest,
                    checksumType,
                    batchComponent,
                    info.extractDirectory,
                    ingestFile,
                    componentURLBase,
                    logger);
            return info.loaderManifest;

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

    public int buildBatchManifest()
        throws TException
    {
        ManifestRowAbs.ManifestType manifestType = ManifestRowAbs.ManifestType.batch;
        FileComponent fileComponent = null;
        try {
            ManifestRowBatch rowOut
                    = (ManifestRowBatch)ManifestRowAbs.getManifestRow(manifestType, logger);
            Manifest manifestBatch = Manifest.getManifest(logger, manifestType);
            manifestBatch.openOutput(outputBatchFile);

            int lineCnt = 0;
            for (LoaderInfo info : extractDirectories) {
                if (info.loaderManifest == null) continue;
                LoaderManifest ingestManifest = info.loaderManifest;
                fileComponent = ingestManifest.getBatchComponent();
                rowOut.setFileComponent(fileComponent);
                log("buildBatchManifest-line:" + rowOut.getLine());
                manifestBatch.write(rowOut);
                lineCnt++;
            }
            manifestBatch.writeEOF();
            manifestBatch.closeOutput();
            System.out.println("***MANIFEST***" + FileUtil.file2String(outputBatchFile));
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

    protected void log(String msg)
    {
        logger.logMessage(msg, 10);
        if (DEBUG)
            System.out.println(msg);
    }

    public File getOutputBatchFile() {
        return outputBatchFile;
    }

}
