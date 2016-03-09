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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * This routine is used to normalize file names using a provided translation Map
 * If the map is not provided then a default normalization translation is used
 * Default translation is made on x'80' - x'9F'
 * 
 * Example: default
    FileTrans fileTrans = new FileTrans(sourceFile);
    try {
            fileTrans.translate();
            ...
            
    } catch (Exception ex) {
        ...
    }
 * 
 * Example: provided translation
    FileTrans fileTrans = new FileTrans(sourceFile, tranTable);
    try {
            fileTrans.translate();
            ...
            
    } catch (Exception ex) {
        ...
    }
 * 
 * @author dloy
 */
public class FileTrans {
    protected static final String NAME = "FileTrans";
    protected static final String MESSAGE = NAME + ": ";
    protected static final int BUFSIZE = 126000;
    protected static final int DEFAULT_TIMEOUT = 3600000;
    private static Map<Character, Character> tranTable = null;
    private File sourceDir = null;
    
    /**
     * File name normalization using default translation Map
     * @param sourceFile top level directory entry to begin translation
     * @throws TException 
     */
    public FileTrans(File sourceFile) 
        throws TException
    {
        this.sourceDir= sourceFile;
        if (sourceFile == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "sourceFile required");
        }
        this.tranTable = setTran();
    }
    
    /**
     * File name normalization using provided translation Map
     * @param sourceFile top level directory entry to begin translation
     * @param tranTable translation Map 
     * @throws TException 
     */
    public FileTrans(File sourceFile, Map<Character, Character> tranTable) 
        throws TException
    {
        this.sourceDir= sourceFile;
        if (sourceFile == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "sourceFile required");
        }
        this.tranTable = tranTable;
        if (tranTable == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "tranTable required");
        }
    }

    public void translate() 
        throws TException 
    {
        normDirectory(sourceDir, "", tranTable, false);
        deleteEmpty(sourceDir, sourceDir);
    }

    
    
    
    public static void main(String[] args) 
            throws TException 
    {
        Map<Character, Character> tranTable = setTestLower();
        File sourceFile = new File("/replic/test/tasks/160308-move-file/base");
        //File sourceFile = new File("/replic/test/160308-move-file/empty");
        FileTrans fileTrans = new FileTrans(sourceFile, tranTable);
        System.out.println("Run main1");
        TFrame tFrame = null;
        try {
            fileTrans.translate();
            
                    
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("NAME=" + ex.getClass().getName());
            System.out.println("Exception:" + ex);
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            
        }
    }
    
    private static Hashtable<Character, Character> setTran()
    {
        Hashtable<Character, Character> retTran = new Hashtable();
        retTran.put('\u0080', '\u20AC');
        retTran.put('\u0081', '\u0020');
        retTran.put('\u0082', '\u201A');
        retTran.put('\u0083', '\u0192');
        retTran.put('\u0084', '\u201E');
        retTran.put('\u0085', '\u2026');
        retTran.put('\u0086', '\u2020');
        retTran.put('\u0087', '\u2021');
        retTran.put('\u0088', '\u02C6');
        retTran.put('\u0089', '\u2030');
        retTran.put('\u008A', '\u0160');
        retTran.put('\u008B', '\u2039');
        retTran.put('\u008C', '\u0152');
        retTran.put('\u008D', '\u0020');
        retTran.put('\u008E', '\u017D');
        retTran.put('\u008F', '\u0020');
        retTran.put('\u0090', '\u0020');
        retTran.put('\u0091', '\u2018');
        retTran.put('\u0092', '\u2019');
        retTran.put('\u0093', '\u201C');
        retTran.put('\u0094', '\u201D');
        retTran.put('\u0095', '\u2022');
        retTran.put('\u0096', '\u2013');
        retTran.put('\u0097', '\u2014');
        retTran.put('\u0098', '\u02DC');
        retTran.put('\u0099', '\u2122');
        retTran.put('\u009A', '\u0161');
        retTran.put('\u009B', '\u203A');
        retTran.put('\u009C', '\u0153');
        retTran.put('\u009D', '\u0020');
        retTran.put('\u009E', '\u017E');
        retTran.put('\u009F', '\u0178');
        return retTran;
    }

    private static Hashtable<Character, Character> setTestLower()
    {
        Hashtable<Character, Character> retTran = new Hashtable();
        retTran.put('\u0060', '\u0040');
        retTran.put('\u0061', '\u0041');
        retTran.put('\u0062', '\u0042');
        retTran.put('\u0063', '\u0043');
        retTran.put('\u0064', '\u0044');
        retTran.put('\u0065', '\u0045');
        retTran.put('\u0066', '\u0046');
        retTran.put('\u0067', '\u0047');
        retTran.put('\u0068', '\u0048');
        retTran.put('\u0069', '\u0049');
        retTran.put('\u006A', '\u004A');
        retTran.put('\u006B', '\u004B');
        retTran.put('\u006C', '\u004C');
        retTran.put('\u006D', '\u004D');
        retTran.put('\u006E', '\u004E');
        retTran.put('\u006F', '\u004F');
        retTran.put('\u0070', '\u0050');
        retTran.put('\u0071', '\u0051');
        retTran.put('\u0072', '\u0052');
        retTran.put('\u0073', '\u0053');
        retTran.put('\u0074', '\u0054');
        retTran.put('\u0075', '\u0055');
        retTran.put('\u0076', '\u0056');
        retTran.put('\u0077', '\u0057');
        retTran.put('\u0078', '\u0058');
        retTran.put('\u0079', '\u0059');
        retTran.put('\u007A', '\u005A');
        retTran.put('\u007B', '\u005B');
        retTran.put('\u007D', '\u005D');
        retTran.put('\u007E', '\u005E');
        retTran.put('\u007F', '\u005F');
        return retTran;
    }
    
    
    
    private static void normDirectory(
            File baseFile,
            String path, 
            Map<Character, Character> nameTrans,
            boolean overwrite)
        throws TException
    {
        try {
            File sourceLocation = new File(baseFile, path);
            String tranPath = tran(path, nameTrans);
            File targetLocation = null;
            if (sourceLocation.isDirectory()) {
                if (tranPath != null) {
                    targetLocation = new File(baseFile, tranPath);
                }
                if ((targetLocation != null) && !targetLocation.exists()) {
                    targetLocation.mkdir();
                }

                String[] children = sourceLocation.list();
                for (String childS : children) {
                    String cPath = path + '/' + childS;
                    normDirectory(baseFile, cPath, nameTrans, overwrite);
                }
                
            } else {
                if (tranPath == null) return ;
                // tranPath = normExtension(tranPath); // for now leave the extension alone
                targetLocation = new File(baseFile, tranPath);
                if (! overwrite && targetLocation.exists()) {}
                else {
                    InputStream in = new FileInputStream(sourceLocation);
                    FileUtil.stream2File(in, targetLocation);
                    if (sourceLocation.length() != targetLocation.length()) {
                        throw new TException.INVALID_DATA_FORMAT( "copy fails:" 
                                + " - sourceLocation:" + sourceLocation.getCanonicalPath()
                                + " - source length:" + sourceLocation.length()
                                + " - targetLocation:" + targetLocation.getCanonicalPath()
                                + " - target length:" + targetLocation.length()
                        );
                    }
                    System.out.println(MESSAGE + "Move \n"
                                + " - sourceLocation:" + sourceLocation.getCanonicalPath() + "\n"
                                + " - targetLocation:" + targetLocation.getCanonicalPath() + "\n"
                    );
                    sourceLocation.delete() ;
                    
                }
            }

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "copyDirectory - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }
    
    
    /**
     * Remove empty directories
     * @param testFile top directory
     * @param stopDir top directory
     * 
     * @throws TException 
     */
    public static void deleteEmpty(
            File testFile, File stopDir)
        throws TException
    {
        try {
            if (testFile.isDirectory()) {
                String[] children = testFile.list();
                if ((children == null) || (children.length ==  0)) {
                    if (testFile == stopDir) return;
                    FileUtil.deleteEmptyPath(testFile);
                } else {
                    for (String childS : children) {
                        File childFile = new File(testFile, childS) ;
                        deleteEmpty(childFile, stopDir);
                    }
                }
                
            }

        } catch(TException mfe) {
            throw mfe;

        } catch(Exception ex) {
            String err = MESSAGE + "copyDirectory - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }
    
    /**
     * Translate directory name using translation Map
     * @param inName file name to be translated 
     * @param tranTable translation Map
     * @return
     *  null = no translation required
     *  !null = tranlated name
     * @throws Exception 
     */
    public static String tran(String inName, Map<Character, Character> tranTable)
        throws Exception
    {
        if ((tranTable == null) || (tranTable.size() == 0)) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        boolean didTran = false;
        for (int i=0; i < inName.length(); i++) {
            char c = inName.charAt(i);
            Character t = tranTable.get(c);
            if (t != null){
                buf.append(t);
                didTran = true;
                
            } else {
                buf.append(c);
            }
        }
        if (!didTran) return null;
        return buf.toString();
    }
    
    /**
     * Lower case extension if present
     * Note extension is defined here as characters following trailing period with 5 or less characters
     * This test is only performed on files that contain a translation
     * @param inName
     * @return inName with lower case extension
     * @throws Exception 
     */
    public static String normExtension(String inName)
        throws Exception
    {
        if ((inName == null) || (inName.length() == 0)) {
            return null;
        }
        int pos = inName.lastIndexOf('.');
        if (pos >= 0) {
            String ext = inName.substring(pos + 1);
            if (ext.length() <= 5) {
                String nonExt = inName.substring(0,pos + 1);
                ext = ext.toLowerCase();
                inName = nonExt + ext;
            }
        }
        return inName;
    }
}
