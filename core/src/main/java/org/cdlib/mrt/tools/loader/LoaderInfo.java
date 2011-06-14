/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.tools.loader;

import java.io.File;
import java.net.URL;
import java.util.Vector;



import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.utility.LinkedHashList;
/**
 *
 * @author dloy
 */
public class LoaderInfo
{
    
    protected static final String NL = System.getProperty("line.separator");
    
    public File extractDirectory = null;
    public String localID = null;
    public String primaryID = null;
    public int versionID = 0;
    public String title = null;
    public String creator = null;
    public LoaderManifest loaderManifest = null;
    public String dump(String header)
    {
        String extractDirectoryS = "none";
        try {
            if (extractDirectory != null) {
                extractDirectoryS = extractDirectory.getCanonicalPath();
            }
        } catch (Exception ex) { extractDirectoryS = ex.toString(); }

        String out = header
                + " - extractDirectory=" + extractDirectory
                + " - localID=" + localID
                + " - primaryID=" + primaryID
                ;
        StringBuffer buf = new StringBuffer(100);
        buf.append(out);
        return buf.toString();
    }
}
