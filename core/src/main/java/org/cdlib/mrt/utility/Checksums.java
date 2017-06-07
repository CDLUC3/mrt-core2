/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdlib.mrt.utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.security.MessageDigest;
/**
 *
 * @author replic
 */
public class Checksums {
    protected static final int BUFSIZE = 32000;
    protected ArrayList<Digest> digestList = new ArrayList();
    protected long inputSize = 0;
    
    public static Checksums getChecksums(String [] types, File inFile)
        throws TException
    {
        Checksums checksums = new Checksums(types);
        checksums.process(inFile);
        return checksums;
    }
    
    public static Checksums getChecksums(String [] types, InputStream inStream)
        throws TException
    {
        Checksums checksums = new Checksums(types);
        checksums.process(inStream);
        return checksums;
    }
    
    public Checksums(String [] types)
        throws TException
    {
        digestList = new ArrayList();
        for (String checksumType : types) {
            Digest digest = new Digest(checksumType);
            digestList.add(digest);
        }
    }
    
	public static void main(String[] args) {
            LoggerInf logger = new TFileLogger("jtest", 50, 50);
            try {
                String [] types = {
                    "md5",
                    "sha256"
                };
                /*
                String DATA = "/replic/tomcat-28080/webapps/test/"
                + "migrate/dev/fk4126c9k/data/7/producer/brk00011921_31b_j.jpg";
                        */
                String DATA = "/apps/replic/test/sword/big/big.zip";
                File testFile = new File(DATA);
                long startTime = System.currentTimeMillis();
                Checksums checksums = Checksums.getChecksums(types, testFile);
                long endTime = System.currentTimeMillis();
                System.out.println("Process(" + BUFSIZE + ")=" + (endTime-startTime));
                System.out.println("getInputSize=" +  checksums.getInputSize());
                for (String type : types) {
                    String checksum = checksums.getChecksum(type);
                    System.out.println("getChecksum(" + type + "):" + checksum);
                }

             } catch (TException tex) {
                tex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();;
            }
    }
        
    public void process(File testFile)
        throws TException
    {
        
        InputStream inputStream = null; 
        try {
            inputStream = new FileInputStream(testFile);
            process(inputStream);
            
        } catch (TException tex) {
            throw tex;
           
        } catch (Exception ex) {
            throw new TException(ex);
            
        } 
    }
        
    public void process(InputStream inputStream)
        throws TException
    {
        try {
            int len;
            byte[] buf = new byte[BUFSIZE];
            while ((len = inputStream.read(buf)) >= 0) {
                inputSize += len;
                for (Digest digest: digestList) {
                    digest.algorithm.update(buf, 0, len);
                }
            }
            for (Digest digest: digestList) {
                finishDigest(digest);
                digest.inputSize = inputSize;
            } 
            
        } catch (TException tex) {
            throw tex;
           
        } catch (Exception ex) {
            throw new TException(ex);
            
        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {}
        }
    }
    
    public String getChecksum(String checksumType)
        throws TException
    {
        MessageDigestType mdt = getAlgorithm(checksumType);
        if (mdt == null) {
            throw new TException.INVALID_OR_MISSING_PARM("Digest type not found:" + checksumType);
        }
        for (Digest digest: digestList) {
            if (digest.type == mdt) return digest.checksum;
        }
        return null;
    }
    
    public static void finishDigest(Digest inDigest)
        throws TException
    {
        try {
            
            byte[] digest = inDigest.algorithm.digest();
            StringBuffer hexString1 = new StringBuffer();
            for (int i=0;i<digest.length;i++) {
                String val = Integer.toHexString(0xFF & digest[i]);
                if (val.length() == 1) val = "0" + val;
                hexString1.append(val);
            }
            inDigest.checksum = hexString1.toString();
           
        } catch (Exception ex) {
            throw new TException(ex);
            
        }
    }
        
    
    public static MessageDigestType getAlgorithm(String algorithmS)
    {
        if (StringUtil.isEmpty(algorithmS)) {
            return null;
        }
        algorithmS = algorithmS.toLowerCase();
        algorithmS = StringUtil.strip(algorithmS, "-");
        try {
            return MessageDigestType.valueOf(algorithmS);
        } catch (Exception ex) {

        }
        return null;
    }

    public ArrayList<Digest> getDigestList() {
        return digestList;
    }

    public long getInputSize() {
        return inputSize;
    }
    
    public static class Digest {
        public MessageDigestType type =  null;
        public MessageDigest algorithm = null;
        public String checksum = null;
        public String checksumType = null;
        public long inputSize = 0;
        public Digest(String inChecksumType)
            throws TException
        {
            try {
                type = getAlgorithm(inChecksumType);
                if (type == null) {
                    throw new TException.INVALID_OR_MISSING_PARM("Digest type not found:" + inChecksumType);
                }
                this.checksumType = type.getJavaAlgorithm();
                algorithm = MessageDigest.getInstance(checksumType);
                algorithm.reset();
                
            } catch (Exception ex) {
                throw new TException (ex);
            }
        }
        public String dump(String header) {
            String out = header + "\n"
                    + " - checksumType:" + checksumType + "\n"
                    + " - checksum:" + type + "\n"
                    + " - inputSize:" + inputSize + "\n";
            return out;
        }
    }
}
