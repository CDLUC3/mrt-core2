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

package org.cdlib.mrt.utility;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.cdlib.mrt.utility.TFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * Build an archive (tar, targz, or zip) from a directory
 * @author dloy
 */
public abstract class ArchiveBuilder {
    protected static final String NAME = "ArchiveBuilder";
    protected static final String MESSAGE = NAME + ": ";
    protected static final int BUFSIZE = 102400;


    protected ArchiveType archiveType = null;
    protected LoggerInf logger = null;
    protected File fromDir;
    protected File toArchive;
    protected OutputStream outputStream;


    /**
     * Archive types available
     * Enum constructor: extension, mimeType
     */
    public enum ArchiveType
    {
        tar("tar", "application/x-tar"),
        targz("tar.gz", "application/x-tar-gz"),
        zip("zip", "application/zip");

        protected final String mimeType;
        protected final String extension;

        ArchiveType(String extension, String mimeType) {
            this.extension = extension;
            this.mimeType = mimeType;
        }

        public String getExtension() {
            return extension;
        }

        public String getMimeType() {
            return mimeType;
        }

        public static ArchiveType valueOfExtension(String t)
        {
            if (StringUtil.isEmpty(t)) return null;
            for (ArchiveType p : ArchiveType.values()) {
                if (p.getExtension().equals(t)) {
                    return p;
                }
            }
            return null;
        }

        public static ArchiveType valueOfMimeType(String t)
        {
            if (StringUtil.isEmpty(t)) return null;
            for (ArchiveType p : ArchiveType.values()) {
                if (p.getMimeType().equals(t)) {
                    return p;
                }
            }
            return null;
        }
    }

    /**
     * Contructor used as build method for this class
     * @param fromDir - directory to be archived
     * @param toArchive - archive to this file
     * @param logger - process logging
     * @param archiveType type of archive to create see ArchiveType
     * @throws TException
     */
    public ArchiveBuilder(
            File fromDir,
            File toArchive,
            LoggerInf logger,
            ArchiveType archiveType)
        throws TException
    {
        if ((fromDir == null) || !fromDir.exists()) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "from Directory does not exist");
        }

        if (toArchive == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "archive File not supplied");
        }

        if (logger == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "logger required");
        }

        if (archiveType == null) {
            archiveType = ArchiveType.targz; // default
        }
        this.fromDir = fromDir;
        this.toArchive = toArchive;
        this.logger = logger;
        this.archiveType = archiveType;
        buildArchive();
    }

    /**
     * Contructor used as build method for this class
     * @param fromDir - directory to be archived
     * @param toArchive - archive to this file
     * @param logger - process logging
     * @param archiveType type of archive to create see ArchiveType
     * @throws TException
     */
    public ArchiveBuilder(
            File fromDir,
            OutputStream outputStream,
            LoggerInf logger,
            ArchiveType archiveType)
        throws TException
    {
        if ((fromDir == null) || !fromDir.exists()) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "from Directory does not exist");
        }

        if (outputStream == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "archive File not supplied");
        }

        if (logger == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "logger required");
        }

        if (archiveType == null) {
            archiveType = ArchiveType.targz; // default
        }
        this.fromDir = fromDir;
        this.outputStream = outputStream;
        this.logger = logger;
        this.archiveType = archiveType;
        buildArchive();
    }


    /**
     * Factory routine to get specific ArchiveBuilder for supplied ArchiveType
     * @param fromDir - directory to be archived
     * @param toArchive - archive to this file
     * @param logger - process logging
     * @param archiveType type of archive to create see ArchiveType
     * @return specific type of ArchiveBuilder
     * @throws TException
     */
    public static ArchiveBuilder getArchiveBuilder(
            File fromDir,
            File toArchive,
            LoggerInf logger,
            ArchiveType archiveType)
        throws TException
    {

        if (archiveType == null) {
            archiveType = ArchiveType.targz; // default
        }
        switch (archiveType) {
            case tar:
                return new ArchiveBuilder.Tar(fromDir, toArchive, logger);
            case targz:
                return new ArchiveBuilder.TarGZ(fromDir, toArchive, logger);
            case zip:
                return new ArchiveBuilder.Zip(fromDir, toArchive, logger);
        }
        throw new TException.REQUEST_ELEMENT_UNSUPPORTED(MESSAGE + "archiveType not supported");
    }

    /**
     * Factory routine to get specific ArchiveBuilder for supplied ArchiveType
     * @param fromDir - directory to be archived
     * @param toArchive - archive to this file
     * @param logger - process logging
     * @param archiveType type of archive to create see ArchiveType
     * @return specific type of ArchiveBuilder
     * @throws TException
     */
    public static ArchiveBuilder getArchiveBuilder(
            File fromDir,
            OutputStream outputStream,
            LoggerInf logger,
            ArchiveType archiveType)
        throws TException
    {

        if (archiveType == null) {
            archiveType = ArchiveType.targz; // default
        }
        switch (archiveType) {
            case tar:
                return new ArchiveBuilder.Tar(fromDir, outputStream, logger);
            case targz:
                return new ArchiveBuilder.TarGZ(fromDir, outputStream, logger);
            case zip:
                return new ArchiveBuilder.Zip(fromDir, outputStream, logger);
        }
        throw new TException.REQUEST_ELEMENT_UNSUPPORTED(MESSAGE + "archiveType not supported");
    }

    /**
     * Create archive from constructor supplied values
     * @throws TException
     */
    protected void buildArchive()
        throws TException
    {
        try {
            if (outputStream == null) outputStream = new FileOutputStream(toArchive);
            setOutputStream(outputStream);
            addFiles(fromDir);
            closeArchive();

        } catch (Exception ex) {
            System.out.println("!!!" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "Exception:" + ex);

        } finally {
            closeArchive();
        }
    }

    /**
     * Call back routine for setting archive output stream type: tar, tar.gz, zip
     * @param baseOutputStream
     * @throws TException
     */
    protected abstract void setOutputStream(OutputStream baseOutputStream)
        throws TException;

    /**
     * Call back routinte to write an archive entry to archive output.
     * Archive type specific.
     *
     * @param entry archive file entry
     */
    protected abstract void addItemFile(File entry, LoggerInf logger)
        throws TException;


    /**
     * Call back routine to close archive.
     * Archive type specific.
     */
    protected abstract void closeArchive();

    /**
     * Normalize archive entry name
     * @param addFile file used for generating archive name
     * @return normalized archive name
     * @throws TException
     */
    protected String getEntryName(File addFile)
        throws TException
    {
        try {
            File baseParent = fromDir.getParentFile();
            String baseName = baseParent.getCanonicalPath();
            String addName = addFile.getCanonicalPath();
            String remName = addName.substring(baseName.length() + 1);
            remName = remName.replace('\\', '/');
            //System.out.println("entry Name=" + remName);
            return remName;

        } catch(Exception ex) {
            String err = MESSAGE + "copyDirectory - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    /**
     * Recursive method for finding files and adding to archive
     * @param sourceLocation file or directory to archive
     * @throws TException
     */
    protected void addFiles(File sourceLocation)
        throws TException
    {
        try {
            if (sourceLocation.isDirectory()) {
                File [] children = sourceLocation.listFiles();
                for (int i=0; i<children.length; i++) {
                    File child = children[i];
                    addFiles(child);
                }

            } else {
                addItemFile(sourceLocation, logger);
            }

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "copyDirectory - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    /**
     * Move file to archive
     * @param out       write to this output stream
     * @param entry     file to write to output
     * @throws TException
     */
    protected void setEntry(OutputStream out, File entry)
        throws TException
    {
        if ((entry == null) || !entry.exists()) return;
        String logtext = MESSAGE + "setEntry: name=" + entry.getName();
        InputStream inputStream = null;
        try {
            logger.logMessage(logtext, 5, true);

            inputStream = new FileInputStream(entry);
            byte[] buf = new byte[32768];

            while (true) {
                int readLen = inputStream.read(buf);
                if (readLen < 0) break;
                out.write(buf, 0, readLen);
            }

        } catch (Exception ex) {
            String errMessage = MESSAGE + "addItemFile - Exception:" + ex;
            logger.logError(errMessage, 0);
            logger.logError(StringUtil.stackTrace(ex), 10);
            throw new TException.GENERAL_EXCEPTION(errMessage);

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception lex) {}
        }
    }

    /**
     * Tar archive - with call backs for Tar
     */
    public static class Tar extends ArchiveBuilder
    {
        protected TarOutputStream tarOutputStream = null;

        public Tar(File fromDir, File toArchive, LoggerInf logger)
            throws TException
        {
            super(fromDir, toArchive, logger, ArchiveType.tar);
        }

        public Tar(File fromDir, File toArchive, LoggerInf logger, ArchiveType archiveType)
            throws TException
        {
            super(fromDir, toArchive, logger, archiveType);
        }
        public Tar(File fromDir, OutputStream outputStream, LoggerInf logger)
            throws TException
        {
            super(fromDir, outputStream, logger, ArchiveType.tar);
        }

        public Tar(File fromDir, OutputStream outputStream, LoggerInf logger, ArchiveType archiveType)
            throws TException
        {
            super(fromDir, outputStream, logger, archiveType);
        }


        protected void setOutputStream(OutputStream baseOutputStream)
            throws TException
        {
            try {
                tarOutputStream = new TarOutputStream(baseOutputStream);
                tarOutputStream.setLongFileMode(TarOutputStream.LONGFILE_GNU);
                tarOutputStream.setBigNumberMode(tarOutputStream.BIGNUMBER_STAR);
                System.out.println("***setOutputStream(Tar): LONGFILE_GNU - BIGNUMBER_STAR");
                
                return;

            } catch (Exception ex) {
                throw new TException.GENERAL_EXCEPTION("setOutputStream Exception:" + ex);
            }
        }

        /**
         * write a archive entry to archive output
         *
         * @param entry archive file entry
         */
        protected void addItemFile(File entry, LoggerInf logger)
            throws TException
        {
            if ((entry == null) || !entry.exists()) return;
            String logtext = MESSAGE + "addItemFile: name=" + entry.getName();

            try {
                logger.logMessage(logtext, 5, true);
                long length = entry.length();
                // directory - no data
                if (length == 0) return;
                String entryName = getEntryName(entry);
                TarEntry tarAdd = new TarEntry(entryName);
                tarAdd.setModTime(new Date());
                tarAdd.setName(entryName);
                tarAdd.setSize(length);
                tarOutputStream.putNextEntry(tarAdd);
                setEntry(tarOutputStream, entry);
                tarOutputStream.closeEntry();

            } catch (TException fex) {
                throw fex;

            } catch (Exception ex) {
                ex.printStackTrace();
                String errMessage = MESSAGE + "addItemFile - Exception:" + ex;
                logger.logError(errMessage, 0);
                logger.logError(StringUtil.stackTrace(ex), 10);
                throw new TException.GENERAL_EXCEPTION(errMessage);
            }
        }

        protected void closeArchive()
        {
            try {
                tarOutputStream.close();
            } catch (Exception ex) {}
        }
    }

    /**
     * Tar-gunzip archive - with call backs specific to Tar
     */
    public static class TarGZ extends Tar
    {

        public TarGZ(File fromDir, File toArchive, LoggerInf logger)
            throws TException
        {
            super(fromDir, toArchive, logger, ArchiveType.targz);
        }
        
        public TarGZ(File fromDir, OutputStream outputStream, LoggerInf logger)
            throws TException
        {
            super(fromDir, outputStream, logger, ArchiveType.targz);
        }

        @Override
        protected void setOutputStream(OutputStream baseOutputStream)
            throws TException
        {
            try {
                GZIPOutputStream gzip = new GZIPOutputStream( baseOutputStream );
                tarOutputStream = new TarOutputStream(gzip);
                tarOutputStream.setLongFileMode(TarOutputStream.LONGFILE_GNU);
                tarOutputStream.setBigNumberMode(tarOutputStream.BIGNUMBER_STAR);
                System.out.println("***setOutputStream(TarGZ): LONGFILE_GNU - BIGNUMBER_STAR");
                return;

            } catch (Exception ex) {
                throw new TException.GENERAL_EXCEPTION("setOutputStream Exception:" + ex);
            }
        }
    }


    /**
     * Zip archive - with call backs specific to zip
     */
    public static class Zip extends ArchiveBuilder
    {
        protected ZipOutputStream zipOutputStream = null;

        public Zip(File fromDir, File toArchive, LoggerInf logger)
            throws TException
        {
            super(fromDir, toArchive, logger, ArchiveType.zip);
        }

        public Zip(File fromDir, OutputStream outputStream, LoggerInf logger)
            throws TException
        {
            super(fromDir, outputStream, logger, ArchiveType.zip);
        }

        public Zip(File fromDir, File toArchive, LoggerInf logger, ArchiveType archiveType)
            throws TException
        {
            super(fromDir, toArchive, logger, archiveType);
        }



        protected void setOutputStream(OutputStream baseOutputStream)
            throws TException
        {
            try {
                zipOutputStream = new ZipOutputStream(baseOutputStream);
                return;

            } catch (Exception ex) {
                throw new TException.GENERAL_EXCEPTION("setOutputStream Exception:" + ex);
            }
        }

        /**
         * write a archive entry to archive output
         *
         * @param entry archive file entry
         */
        protected void addItemFile(File entry, LoggerInf logger)
            throws TException
        {
            if ((entry == null) || !entry.exists()) return;
            String logtext = MESSAGE + "addItemFile: name=" + entry.getName();

            try {
                String entryName = getEntryName(entry);
                zipOutputStream.putNextEntry(new ZipEntry(entryName));
                setEntry(zipOutputStream, entry);
                zipOutputStream.closeEntry();

            } catch (TException fex) {
                throw fex;

            } catch (Exception ex) {
                String errMessage = MESSAGE + "addItemFile - Exception:" + ex;
                logger.logError(errMessage, 0);
                logger.logError(StringUtil.stackTrace(ex), 10);
                throw new TException.GENERAL_EXCEPTION(errMessage);
            }
        }

        protected void closeArchive()
        {
            try {
                zipOutputStream.close();
            } catch (Exception ex) {}
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
                "testresources/TestLocal.properties"};

            framework = new TFrame(propertyList, NAME);
            LoggerInf logger = framework.getLogger();
            String directoryName = framework.getProperty(NAME + ".inDirectory");
            File directoryFile = new File(directoryName);
            if (!directoryFile.exists()) {
                System.out.println("directory does not exist:"
                        + directoryFile.getCanonicalFile());
                return;
            }

            String archiveFileName = framework.getProperty(NAME + ".archive");
            File archiveFile = new File(archiveFileName);

            String archiveTypeName = framework.getProperty(NAME + ".type");
            if (StringUtil.isEmpty(archiveTypeName)) {
                System.out.println("type required");
                return;
            }
            archiveTypeName = archiveTypeName.toLowerCase();
            ArchiveType archiveType = ArchiveType.valueOf(archiveTypeName);
            if (archiveType == null) {
                System.out.println("archiveType not defined:" + archiveTypeName);
                return;
            }
            System.out.println("directory=" + directoryName);
            System.out.println("archive=" + archiveFileName);
            System.out.println("archiveTypeName=" + archiveTypeName);

            ArchiveBuilder builder = getArchiveBuilder(directoryFile, archiveFile, logger, archiveType);

        }  catch(Exception ex)  {
            System.out.println(MESSAGE + "Exception:" + ex);
            System.out.println(MESSAGE + "Trace:" + StringUtil.stackTrace(ex));
            if (framework != null)
            {
                framework.getLogger().logError(
                    "Main: Encountered exception:" + ex, 0);
                framework.getLogger().logError(
                        StringUtil.stackTrace(ex), 10);
            }
        }
    }
}
