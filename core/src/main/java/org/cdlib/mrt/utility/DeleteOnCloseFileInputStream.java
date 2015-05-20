/*
 * Thanks to:
 * http://stackoverflow.com/questions/4693968/is-there-an-existing-fileinputstream-delete-on-close
 */
package org.cdlib.mrt.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author replic
 */
public class DeleteOnCloseFileInputStream 
            extends FileInputStream {
    private File file;

    public DeleteOnCloseFileInputStream(String fileName) throws FileNotFoundException{
        this(new File(fileName));
    }
    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException{
       super(file);
       this.file = file;
    }

    public void close() 
            throws IOException {
        try {
           super.close();
        } finally {
           if(file != null) {
              file.delete();
              file = null;
          }
        }
    }
}
