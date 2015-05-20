/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.cloud;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;



import org.cdlib.mrt.cloud.ManInfo;
import org.cdlib.mrt.cloud.VersionMap;
import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.URLEncoder;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;

/**
 *
 * @author dloy
 */
public class VersionMapUtil 
{
    public static final boolean DEBUG = false;
    
    /**
     * Get version map from manifext URL
     * @param urlS string form manifest URL
     * @return version manifest map
     * @throws TException process exception
     */
    public static VersionMap getMap(String urlS)
        throws TException
    {
        ManInfo test = new ManInfo();
        LoggerInf logger = new TFileLogger("TestBuild", 0, 0);
        InputStream inStream =  null;
        File tmpFile = null;
        try {
            tmpFile = FileUtil.url2TempFile(logger, urlS);
            inStream = new FileInputStream(tmpFile);
            return ManifestSAX.buildMap(inStream, logger);
            
        } catch (TException tex) {
            throw tex;
            
        }  catch (Exception ex) {
            System.out.println("Exception:" + ex);
            throw new TException(ex);
            
        } finally {
           if (tmpFile != null) {
               try {
                   tmpFile.delete();
               } catch (Exception ex) { }
           }
        }
    }
    
    /**
     * Get component information from version map
     * @param map version manifest map
     * @param versionID specific version content to be extracted
     * @return version components
     * @throws TException process exception
     */
    public static List<FileComponent> getVersion (VersionMap map, int versionID)
        throws TException
    {   
        try {
            ComponentContent componentContent = map.getVersionContent(versionID);
            if (componentContent == null) return null;
            else return componentContent.getFileComponents();

        } catch(Exception ex) {
            throw new TException(ex);
        }
    }
    
    /**
     * Fill in the outputDir file directory with the content from this version
     * @param map version map
     * @param versionID version identifier of content to be extracted
     * @param baseURL the base URL pre-pended to the file content for extraction
     * @param outputDir directory file for extracted content
     * @return written count
     * @throws TException processing exception
     */
    public static int getVersionFiles (VersionMap map, int versionID, String baseURL, File outputDir)
        throws TException
    {   
        LoggerInf logger = new TFileLogger("TestBuild", 0, 0);
        try {
            ComponentContent componentContent = map.getVersionContent(versionID);
            if (componentContent == null) return 0;
            List<FileComponent> components = componentContent.getFileComponents();
            int cnt = 0;
            for (FileComponent component : components) {
                String urlS = baseURL 
                        + "/" + URLEncoder.encode(map.getObjectID().getValue(), "utf-8")
                        + "/" + versionID
                        + "/" + URLEncoder.encode(component.getIdentifier(), "utf-8")
                        ;
                File outFile = getOutputFile(outputDir, component.getIdentifier());
                if (DEBUG) System.out.println("Output"
                        + " - outFile=" + outFile.getCanonicalPath()
                        + " - urlS=" + urlS
                        );
                FileUtil.url2File(logger, urlS, outFile, 3);
                cnt++;
            }
            return cnt;

        } catch(Exception ex) {
            throw new TException(ex);
        }
    }
    
    public static File getOutputFile (File outDir, String fileID)
        throws TException
    {   
        LoggerInf logger = new TFileLogger("TestBuild", 0, 0);
        try {
            int pos = fileID.lastIndexOf("/");
            if (pos < 0) {
                return new File(outDir, fileID);
            }
            
            String path = fileID.substring(0,pos);
            String name = fileID.substring(pos+1);
            File targetDir = new File(outDir, path);
            targetDir.mkdirs();
            return new File(targetDir, name);

        } catch(Exception ex) {
            throw new TException(ex);
        }
    }
}