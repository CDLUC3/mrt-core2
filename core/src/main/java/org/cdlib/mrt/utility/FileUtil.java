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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;
import org.cdlib.mrt.utility.HttpGet;

/**
 * Generalized file utilities
 * @author dloy
 */
public class FileUtil {
    protected static final String NAME = "FileUtil";
    protected static final String MESSAGE = NAME + ": ";
    protected static final int BUFSIZE = 126000;
    protected static final int DEFAULT_TIMEOUT = 3600000;

    /**
     * Get content referenced by a url and save in a file
     * @param m_logger logger
     * @param fileURL link to file to be extracted
     * @param outFile target file to create
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void url2File(LoggerInf m_logger, URL fileURL, File outFile)
        throws TException
    {
        HttpGet.getFile(fileURL, outFile, DEFAULT_TIMEOUT, m_logger);
    }
    
    /**
     * Get content referenced by a url and save in a file
     * @param m_logger logger
     * @param urlS link to file to be extracted
     * @param outFile target file to create
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void url2File(LoggerInf m_logger, String urlS, File outFile)
        throws TException
    {
        URL url = null;
        try {
            url = new URL(urlS);
            
        } catch (Exception ex) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "String URL invalid:" + urlS 
                    + " - Exception:" + ex
            );
        }
        url2File(m_logger, url, outFile);
    }

    /**
     * Get content referenced by a url and save in a file
     * @param m_logger logger
     * @param fileURL link to file to be extracted
     * @param outFile target file to create
     * @param retry number of retry attempts
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void url2File(LoggerInf m_logger, URL fileURL, File outFile, int retry)
        throws TException
    {
        url2File(m_logger, fileURL, outFile);
    }

    /**
     * Get content referenced by a url and save in a file
     * @param m_logger logger
     * @param urlS link to file to be extracted
     * @param outFile target file to create
     * @param retry number of retry attempts
     * @throws org.cdlib.mcur.utility.MException
     */
    public static void url2File(LoggerInf m_logger, String urlS, File outFile, int retry)
        throws TException
    {
        url2File(m_logger, urlS, outFile);
    }
    
    
    /**
     * get remote file
     * @param manifestFile
     * @return
     * @throws org.cdlib.framework.utility.FrameworkException
     */
    public static File url2TempFile(LoggerInf m_logger, String urlS)
        throws TException
    {
        try {
            File tempFile = FileUtil.getTempFile(null, ".txt");
            url2File(m_logger, urlS, tempFile);
            return tempFile;

        } catch( TException tex ) {
            System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "- Exception:" + ex);
        }
    }


    /**
     * Create a file from a stream
     * @param inStream stream used to create file
     * @param outFile file to create
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void stream2File(InputStream inStream, File outFile)
        throws TException
    {

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(outFile);

            byte [] buf = new byte[BUFSIZE];
            int len = 0;
            while ((len = inStream.read(buf)) >= 0) {
                outStream.write(buf, 0, len);
            }
        
        } catch(Exception ex) {
            String err = MESSAGE + "Name:" + outFile.getName();
            throw new TException.GENERAL_EXCEPTION( err, ex);


        } finally {
            try {
                //System.out.println("***FILE CLOSED***");
                inStream.close();
                outStream.close();
                
            } catch (Exception finex) { }
        }

    }
    
    /**
     * Move url response to output stream
     * @param urlS link to file to be extracted
     * @param outStream output stream
     * @param retry number of retry attempts
     * @throws org.cdlib.mcur.utility.MException
     */
    public static void url2OutputStream(String urlS, OutputStream outStream, int retry)
        throws TException
    {

        InputStream inStream = null;
        try {
            inStream = HTTPUtil.getObject(urlS, 120000, retry);
            stream2Stream(inStream, outStream);

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "url2OutputStream - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }
    
    /**
     * Copy input stream to output stream
     * @param inStream - input stream
     * @param outStream - output Stream
     * @throws TException  process exception
     */
    public static void stream2Stream(InputStream inStream, OutputStream outStream)
        throws TException
    {

        try {
            byte [] buf = new byte[BUFSIZE];
            int len = 0;
            while ((len = inStream.read(buf)) >= 0) {
                outStream.write(buf, 0, len);
            }
        
        } catch(Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex);


        } finally {
            try {
                //System.out.println("***FILE CLOSED***");
                inStream.close();
                outStream.close();
                
            } catch (Exception finex) { }
        }

    }

    /**
     * Copy file from one directory to another
     * @param fileName          string name of file (may include some path extensions)
     * @param sourceDirectory   from directory
     * @param targetDirectory   to directory
     * @throws TException
     */
    public static void copyFile(
            String fileName,
            File sourceDirectory,
            File targetDirectory)
        throws TException
    {
        try {
            String path = null;
            String name = fileName;
            File sourceDir = sourceDirectory;
            File targetDir = targetDirectory;
            int pos = fileName.lastIndexOf('/');
            if (pos >= 0) {
                path = fileName.substring(0,pos);
                name = fileName.substring(pos+1);
            }
            if (path != null) {
                sourceDir = new File(sourceDirectory, path);
                sourceDir.mkdirs();
                targetDir = new File(targetDirectory, path);
                targetDir.mkdirs();
            }
            File sourceFile = new File(sourceDirectory, fileName);
            InputStream inStream = new FileInputStream(sourceFile);
            File targetFile = new File(targetDirectory, fileName);
            FileUtil.stream2File(inStream, targetFile);

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            String err = MESSAGE + "copyFile - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }
    
    /**
     * Copy one file to another
     * @param sourceFile input file
     * @param targetFile output file
     * @throws TException service exception
     */
    public static void file2file(
            File sourceFile,
            File targetFile)
        throws TException
    {
        try {
            InputStream inStream = new FileInputStream(sourceFile);
            FileUtil.stream2File(inStream, targetFile);

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            String err = MESSAGE + "copyFile - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    /**
     * Build a URL equivalent path using a java file path
     * This routine maintains all slashes in the file path as
     * a slash in the URL. All other special charecters are url encoded
     * @param filePath file path to convert to a URL form
     * @return URI valid path
     */
    public static String getURLEncodeFilePath(String filePath)
    {
        if (StringUtil.isEmpty(filePath)) return filePath;
        filePath = filePath.replace('\\', '/');
        try {
            String parts[] = filePath.split("/");
            StringBuffer buf = new StringBuffer(100);
            String part = null;
            for (int i=0; i < parts.length; i++) {
                part = parts[i];
                part = URLEncoder.encode(part, "utf-8");
                if (i > 0) {
                    buf.append('/');
                }
                buf.append(part);
            }
            return buf.toString();

        } catch (Exception ex) {
            return null;
        }
    }


    /**
     * Copy files in one directory to another directory based on matchin
     * a regular expression. If a directory matches the expression then it
     * will also be copied
     * @param fileExp regex used for matching the fileName
     * @param sourceDirectory source directory for copy
     * @param targetDirectory target directory for copy
     * @throws TException process exception
     */
    public static void copyReg(
            String fileExp,
            File sourceDirectory,
            File targetDirectory)
        throws TException
    {
        try {
            File [] files = sourceDirectory.listFiles();
            for (int i=0; i < files.length; i++) {
                File file = files[i];
                String name = file.getName();
                if (name.matches(fileExp)) {
                    if (file.isDirectory()) {
                        copyDirectory( file, new File(targetDirectory, name));
                    } else {
                        copyFile(name, sourceDirectory, targetDirectory);
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION( ex);
        }
    }

    /**
     * Create a file containing this string value (utf-8)
     * @param createFile file to create
     * @param fileContent string containing content to save in file
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void string2File(File createFile, String fileContent)
            throws TException
    {
        try {
            if ((createFile == null) || StringUtil.isEmpty(fileContent)) {
                String err = MESSAGE + "buildDflatInfoFile - bad argument";
                throw new TException.INVALID_OR_MISSING_PARM( err);
            }
            ByteArrayInputStream byteStream = new ByteArrayInputStream(fileContent.getBytes("utf-8"));
            stream2File(byteStream, createFile);

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not create Version File - Exception:" + ex + " - name:" + createFile.getName();
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    /**
     * Extract the value of file to a string (utf-8)
     * @param extractFile file to be extracted
     * @return string form of file content
     * @throws org.cdlib.mrt.utility.MException
     */
    public static String file2String(File extractFile)
            throws TException
    {
        return file2String(extractFile, "utf-8");
    }

    /**
     * Extract the value of file to a string (utf-8)
     * @param extractFile file to be extracted
     * @param encType bit to character encoding
     * @return string form of file content
     * @throws org.cdlib.mrt.utility.MException
     */
    public static String file2String(File extractFile, String encType)
            throws TException
    {
        try {
            if ((extractFile == null) || !extractFile.exists()) {
                String err = MESSAGE + "file2String - bad argument";
                throw new TException.INVALID_OR_MISSING_PARM( err);
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte [] buf = new byte [100000];
            FileInputStream inputStream = new FileInputStream(extractFile);
            while (true) {
                int len = inputStream.read(buf);
                if (len < 0) break;
                byteStream.write(buf, 0, len);
            }
            inputStream.close();
            byteStream.close();
            byte [] bytes = byteStream.toByteArray();
            String retValue = new String(bytes, encType);
            return retValue;

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not create String - Exception:" + ex + " - name:" + extractFile.getName();
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }


    /**
     * Create a temp file using prefix and suffix (THREAD SAFE)
     * @param iStream InputStrea of data to be copied
     * @param prefix prefix of temp file name
     * @param suffix suffix of temp file name
     */
    public static File getTempFile(String prefix, String suffix)
        throws TException
    {
        try {
            if ((prefix == null) || (prefix.length() < 3)) prefix = "pre.";
            if (StringUtil.isEmpty(suffix)) suffix = ".txt";
            File file = File.createTempFile(prefix, suffix);
            // file.deleteOnExit(); << should be deprecated
            return file;

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(
                    "unable to return temp file - Exception:" + ex);
        }
    }

    /**
     * Delete file if is temp
     * @param testFile temp file to be deleted
     * @return true=file no longer exists on completion; false=file not deleted
     */
    public static Boolean deleteTempFile(File testFile)
    {
        try {
            if (testFile == null) return true;
            if (!testFile.exists()) return true;
            
            if (isTempFile(testFile)) {
                testFile.delete();
                if (!testFile.exists()) return true;
            }
            return false;
            
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * is this a temp file
     * @param file
     * @return true=is temp; false=is not temp
     */
    public static boolean isTempFile(File file) {
        String tempDir = System.getProperty("java.io.tmpdir");
        return file.getAbsolutePath().startsWith(tempDir);
    }

    /**
     * Gets a temporary directory
     *
     * @return A temporary directory
     * @exception SQLException If a connection can not be provided
     */
    /**
     * Get a temporary directory
     * @param directoryPrefix prefix for temporary file
     * @return temp file
     * @throws TException
     */
    public static File getTempDir(String directoryPrefix)
        throws TException
    {
        return getTempDir(directoryPrefix, null);
    }

    /**
     * Get a temporary directory
     * @param directoryPrefix prefix for temporary file
     * @param logger log file
     * @return temporary directory
     * @throws TException
     */
    public static File getTempDir(String directoryPrefix, LoggerInf logger)
        throws TException
    {
        File file = null;
        try
        {
	    String fileSep = System.getProperty("file.separator");
	    String tempDir = System.getProperty("java.io.tmpdir");

            // Create a directory and mark it for deletion when framework shuts down
            file = File.createTempFile(directoryPrefix, ".dir");
            file.deleteOnExit();
            String fileName = file.getAbsolutePath();
            file.delete();
            if (! file.mkdir()) {
                throw new TException.GENERAL_EXCEPTION("Failed to create a temporary directory");
            }

            if (logger != null)
                logger.logMessage("FileManager_NATIVE: Created new temporary directory: " + fileName, 10);
            return file;
        }
        catch(Exception e)
        {
            if (logger != null)
                logger.logError("FileManager_NATIVE: Failed to create a directory: " +
                file.getName(), 0);
            throw new TException.GENERAL_EXCEPTION(
                "Failed to create a temporary directory");
        }
    }


    /**
     * Copy all files in a directory to another directory (will overwrite)
     * @param sourceLocation source for copy
     * @param targetLocation target for copy
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void copyDirectory(File sourceLocation, File targetLocation)
        throws TException
    {
        copyDirectory(sourceLocation, targetLocation, true);
    }


    /**
     * Copy all files in a directory to another directory (will not overwrite)
     * @param sourceLocation source for copy
     * @param targetLocation target for copy
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void updateDirectory(File sourceLocation, File targetLocation)
        throws TException
    {
        copyDirectory(sourceLocation, targetLocation, false);
    }


    /**
     * Copy all files in a director to another directory
     * @param sourceLocation source for copy
     * @param targetLocation target for copy
     * @param overwrite logical for overwriting existing target file
     * @throws org.cdlib.mrt.utility.MException
     */
    private static void copyDirectory(File sourceLocation , File targetLocation, boolean overwrite)
        throws TException
    {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdir();
                }

                String[] children = sourceLocation.list();
                for (int i=0; i<children.length; i++) {
                    copyDirectory(new File(sourceLocation, children[i]),
                            new File(targetLocation, children[i]), overwrite);
                }
            } else {
                InputStream in = new FileInputStream(sourceLocation);
		if (! overwrite && targetLocation.exists()) {}
                else stream2File(in, targetLocation);
            }

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "copyDirectory - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }



    }

    /**
     * Return the size of all files in Directory
     * @param directory starting directory for accumulating size
     * @return
     */
    public static long getDirectorySize(File directory)
    {
        long size = 0;
        if (directory.isFile()) {
            size = directory.length();
        } else {
            File[] subFiles = directory.listFiles();

            for (File file : subFiles) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }

    /**
     * Return the count and size of all files in Directory
     * @param directory start directory for tally
     */
    public static DirectoryStats getDirectoryStats(File directory)
    {
        DirectoryStats stats = new DirectoryStats();
        getDirectoryStats(directory, stats);
        return stats;
    }

    /**
     * Return the count and size of all files in Directory
     * @param directory start directory for tally
     * @param stats accumulating stats object
     */
    public static void getDirectoryStats(File directory, DirectoryStats stats)
    {
        if (directory.isFile()) {
            stats.fileSize += directory.length();
            stats.fileCnt++;

        } else {
            File[] subFiles = directory.listFiles();

            for (File file : subFiles) {
                getDirectoryStats(file, stats);
            }
        }
    }


    /**
     * Build list of files for a directory
     * @param sourceLocation start directory for extraction
     * @param files list of file contents
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void getDirectoryFiles(File sourceLocation , Vector<File> files)
        throws TException
    {
        try {
            if (sourceLocation.isDirectory()) {
                File [] children = sourceLocation.listFiles();
                for (int i=0; i<children.length; i++) {
                    File child = children[i];
                    getDirectoryFiles(child, files);
                }

            } else {
                files.add(sourceLocation);
            }

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "copyDirectory - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }

    }


    /**
     * Return a temp copy of a passed file
     * @param sourceFile file to be copied
     * @return copied form of sourceFile as temporary file
     * @throws org.cdlib.mrt.utility.MException
     */
    public static File copy2Temp(File sourceFile)
        throws TException
    {
        try {
            if ((sourceFile ==null ) || (!sourceFile.exists())) {
                throw new TException.INVALID_OR_MISSING_PARM(
                        "copy2Temp missing sourcFile");
            }
            String fileName = sourceFile.getName();
            String suffix = null;
            String prefix = null;
            int pos = fileName.lastIndexOf('.');
            if (pos > 0) {
                prefix = fileName.substring(0,pos);
                suffix = fileName.substring(pos);
            } else {
                prefix = fileName;
            }
            File tempFile = getTempFile(prefix + ".", suffix);
            InputStream inputStream = new FileInputStream(sourceFile);
            stream2File(inputStream, tempFile);
            return tempFile;

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "copyDirectory - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }
    
    /**
     * Increment a stored file value and save back to file
     * @param incFile file containing a numeric integer to be incremented
     * @param inc amount to increment
     * @throws org.cdlib.mrt.utility.MException
     */
    public static void incFile(File incFile, int inc)
        throws TException
    {
        if (incFile == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "FileUtil - incFile not supplied");
        }
        int val = 0;
        if (!incFile.exists()) {
            val = inc;

        } else {
            String value = file2String(incFile);
            if (StringUtil.isEmpty(value)) value = "0";
            try {
                val = Integer.parseInt(value);
                val += inc;
            } catch (Exception ex) {
                return;
            }
        }
        string2File(incFile, "" + val);
    }
    /**
     * 
     * @param extFile file containing a numeric value
     * @param dflt default value if not found
     * @return extracted value
     * @throws org.cdlib.mrt.utility.MException
     */
    public static long extractFileCnt(File extFile, long dflt)
        throws TException
    {
        if ((extFile == null) || !extFile.exists()) {
            return dflt;

        } else {
            String value = file2String(extFile);
            try {
                return Long.parseLong(value);
            } catch (Exception ex) {
                throw new TException.INVALID_DATA_FORMAT(
                    "FileUtil - extractFileCnt invalid data format:" + value);
            }
        }
    }



    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    // http://www.exampledepot.com/egs/java.io/DeleteDir.html
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                File deleteFile = new File(dir, children[i]);
                boolean success = deleteDir(deleteFile);
                if (!success) {
                    
                    try {
                    System.out.println("FileUtil: Delete fails:" + deleteFile.getCanonicalPath());
                    } catch (Exception ex) {}
                     
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * Delete up a directory path until a non-empty node is found
     * Basically remove all empty directory levels above this directory.
     * @param dir directory
     */
    public static void deleteEmptyPath(File dir) {
        if ((dir == null) || !dir.exists()) {
            return;
        }
        if (dir.list().length > 0) return;
        boolean deleted = false;
        if (dir.isDirectory()) {
            File parent = dir.getParentFile();
            deleted = dir.delete();
            if (!deleted) return;
            deleteEmptyPath(parent);
        }
    }

    /**
     * Delete up a directory path until a non-empty node is found
     * Basically remove all empty directory levels above this directory.
     * @param dir directory
     * @param stopDir stop at this parent directory
     */
    public static void deleteEmptyPath(File dir, File stopDir) {
        if ((dir == null) || !dir.exists()) return;
        if (dir.equals(stopDir)) return;
        if (dir.list().length > 0) return;
        boolean deleted = false;
        if (dir.isDirectory()) {
            File parent = dir.getParentFile();
            deleted = dir.delete();
            if (!deleted) return;
            deleteEmptyPath(parent, stopDir);
        }
    }

    /**
     * <pre>
     * Create a test directory of form:
     * A
     * AA AB AC
     * ...
     * AAAA.txt AAAB.txt AAAC.txt
     * Note content of .txt file is name of file
     * </pre>
     *
     * @param tempDir this level directory
     * @param lvl level beginning with one indicating level in test tree
     * @param maxlvl maximum level count before file (not directories) are created
     * @throws TException
     */
    public static void populateTest(File tempDir, String name, int lvl, int maxlvl)
        throws TException
    {
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        lvl += 1;
        String outName = null;
        for (int i=0; i<lvl; i++) {
            outName = name + alpha.charAt(i);
            if (lvl >= maxlvl) {
                addTestFile(tempDir, outName);
            } else {
                File newDir = new File(tempDir, outName);
                newDir.mkdir();
                populateTest(newDir, outName, lvl, maxlvl);
            }
        }
    }

    /**
     * Add file at this directory level - ending with .txt
     * Note that file content is name of file.
     * @param tempDir
     * @param fileName
     * @throws TException
     */
    protected static void addTestFile(File tempDir, String fileName)
        throws TException
    {
        try {
            String outName = fileName + ".txt";
            File outFile = new File(tempDir, outName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte [] nameBytes = outName.getBytes("utf-8");
            fos.write(nameBytes);
            fos.close();

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION("Exception:" + ex);
        }
    }



    /**
     * Get a file from a URL value - bug in 1.5
     * @param url to be converted
     * @return file
     */
    public static File fileFromURL(URL url)
    {
        File file;
        try {
            file = new File(url.toURI());

        } catch(Exception e) {
            file = new File(url.getPath());
        }
        return file;
    }


    /**
     * remove line(s) from a text file
     * @param file file to be processed
     * @param lineToRemove line to be removed 
     * @param linePortion begins, ends contains or matches line (BEGIN, END, CONTAIN or MATCH, default: MATCH)
     */
    public static void removeLineFromFile(String file, String lineToRemove, String linePortion) {

    try {

      File inFile = new File(file);
      
      if (!inFile.isFile()) {
        System.out.println("Parameter is not an existing file");
        return;
      }
       
      //Construct the new file that will later be renamed to the original filename.
      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
      
      BufferedReader br = new BufferedReader(new FileReader(file));
      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
      
      String line = null;

      //Read from the original file and write to the new
      //unless content matches data to be removed.
      while ((line = br.readLine()) != null) {
        
        if ( ! ((line.trim().endsWith(lineToRemove) && linePortion.equals("END"))  ||
            (line.trim().startsWith(lineToRemove) && linePortion.equals("BEGIN")) ||
            (line.trim().contains(lineToRemove) && linePortion.equals("CONTAIN")) ||
            (line.trim().equals(lineToRemove)))) {

          pw.println(line);
          pw.flush();
        }
      }
      pw.close();
      br.close();
      
      //Delete the original file
      if (!inFile.delete()) {
        System.out.println("Could not delete file");
        return;
      }
      
      //Rename the new file to the filename the original file had.
      if (!tempFile.renameTo(inFile))
        System.out.println("Could not rename file");
      
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

   /**
    * Return array of nonempty lines from a file
    * @param logger merritt logger
    * @param urlS url to file to be split
    * @return array of lines from original file
    * @throws TException
    */
    public static String[] getLinesFromFile(File splitFile)
        throws TException
    {
        try {
            String fileContent = FileUtil.file2String(splitFile);
            if (StringUtil.isEmpty(fileContent)) return null;
            String [] lines = fileContent.split("[\\n\\r]+");
            if (lines.length == 0) return null;
            Vector<String> list = new Vector(lines.length);
            for (String line: lines) {
                if (StringUtil.isNotEmpty(line)) list.add(line);
            }
            return list.toArray(new String[0]);

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Return array of nonempty lines from url content
     * @param logger merritt logger
     * @param urlS url to file to be split
     * @return array of lines from original file
     * @throws TException
     */
    public static String[] getLinesFromURL(LoggerInf logger, String urlS)
        throws TException
    {
        File tempFile = null;
        try {
            tempFile = url2TempFile(logger, urlS);
            return getLinesFromFile(tempFile);

        } catch (Exception ex) {
            return null;

        } finally {
            if (tempFile != null) {
                try {
                    tempFile.delete();
                } catch (Exception ex) { }
            }
        }
    }
    
    public static void removeFile(File inputFile)
    {
        if (inputFile == null) return;
        if (!inputFile.exists()) return;
        
        try {
            if (false) System.out.println("removeFile before:" + inputFile.length());
            PrintWriter pw = new PrintWriter(inputFile);
            pw.close();
            if (true) System.out.println("removeFile after:" 
                    + " - length:" + inputFile.length()
                    + " - name:" + inputFile.getCanonicalPath() 
            );
        } catch (Exception ex) {
            System.out.println("Writer exception ignored:" + ex) ;
        }
        try {
            inputFile.delete();
        } catch (Exception ex) {
            System.out.println("Delete exception ignored:" + ex) ;
        }
    }
}
