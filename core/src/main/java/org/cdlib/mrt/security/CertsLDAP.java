/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.security;
import org.cdlib.mrt.security.*;
import org.cdlib.mrt.core.*;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import static org.cdlib.mrt.security.LDAPUtil.getProfile;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TFileLogger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.DOMParser;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.HTTPUtil;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

import org.w3c.dom.Document;
/**
 *
 * @author dloy
 */
public class CertsLDAP {

    protected static final String NAME = "CertsLDAP";
    protected static final String MESSAGE = NAME + ": ";
    protected LinkedHashList<String,String> prop = null;
    protected File certFile = null;
    protected String hostName = null;
    protected String ldapName = null;
    
    public CertsLDAP(File certFile, String hostName) 
       throws TException
    {
        this.certFile = certFile;
        this.hostName = hostName;
        setCert();
    }

    private void setCert()
        throws TException
    {
        File outFile = null;
        String lastProp = null;
        try {
            lastProp = System.getProperty("javax.net.ssl.trustStore");
            if (lastProp == null) {
                System.out.println("lastProp IS null");
                outFile = certFile;
                if (outFile.length() > 0) {
                    System.out.println("outFile exists:" + outFile.getCanonicalPath());
                } else {
                    System.out.println("hostName=" + hostName + "\n"
                        + "outFile=" + outFile.getCanonicalPath() + "\n"
                    )   ;
                }
                System.setProperty("javax.net.ssl.trustStore",
                    outFile.getCanonicalPath());
                ImportCert.install(hostName, "1", null, outFile);
                System.out.println("lastProp:" + lastProp);
                
            } else {
                System.out.println("lastProp NOT null- hostName=" + hostName);
            }
            ldapName = "ldaps://" + hostName;
            

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
            
        }
    }


    public LinkedHashList<String,String> find(String user, String pwd)
        throws TException
    {
        
        try {
            prop = LDAPUtil.getUserProperties(
                    ldapName,
                    user,
                    pwd);
            System.out.println(LDAPUtil.dump("TestProperties", prop));
            return prop;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public boolean isAuthorized(
            String uid, 
            String password, 
            String className,
            String searchId)
        throws TException
    {
        
        try {
            boolean authorized = LDAPUtil.isAuthorized(ldapName, uid, password, className, searchId);
            System.out.println("isAuthorized:" + authorized);
            return authorized;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }

    public List<String> getNames(String user, String pwd, String searchId)
        throws TException
    {
        
        try {
            System.out.println("CertsLDAP:"
                    + " - ldapName:" + ldapName
                    + " - user:" + user
                    + " - pwd:" + pwd
                    + " - searchId:" + searchId
            );
            List<String> names = LDAPUtil.getClassNames(ldapName, user, pwd, searchId);
            return names;
            
        } catch (TException tex) {
            System.out.println("getNames TException:" + tex);
            throw tex;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    

    public List<String> extractProfiles(String user, String pwd, String searchId)
        throws TException
    {
        
        try {
            System.out.println("CertsLDAP:"
                    + " - ldapName:" + ldapName
                    + " - user:" + user
                    + " - pwd:" + pwd
                    + " - searchId:" + searchId
            );
            List<String> names = LDAPUtil.extractProfiles(ldapName, user, pwd, searchId);
            return names;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }

    public String getProfile(String user, String pwd, String mnemonic)
        throws TException
    {
        
        try {
            System.out.println("CertsLDAP:"
                    + " - ldapName:" + ldapName
                    + " - user:" + user
                    + " - pwd:" + pwd
                    + " - mnemonic:" + mnemonic
            );
            String profile = LDAPUtil.getProfile(ldapName, user, pwd, mnemonic);
            return profile;
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
}