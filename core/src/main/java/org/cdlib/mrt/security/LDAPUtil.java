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

package org.cdlib.mrt.security;
import java.io.StringWriter;
import java.util.Vector;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.TException;
/**
 *
 * @author dloy
 */
public class LDAPUtil
{
    private static final String SEP = System.getProperty("line.separator");
    private static final boolean DEBUG = false;

    public static LinkedHashList<String,String> getUserProperties(String host, String uid, String password)
        throws TException
    {
        LinkedHashList list = null;
        String returnedAtts[] = { "cn", "sn", "telephoneNumber", "arkId", "mail", "displayName" };
        String filter = "uid=" + uid;
        String searchBase = "ou=People,ou=uc3,dc=cdlib,dc=org";
        list = search(
                uid,
                password,
                host,
                returnedAtts,
                searchBase,
                filter);
        if (DEBUG) dump("getUserProperties", list);
        return list;
    }


    private static void dump(Attributes attrs, String name, String key)
    {
        if (attrs.get(key) == null) {
            System.out.println("--> " + name + "=EMPTY");
            return;
        }
        try {
            System.out.println("--> " + name + "=" + attrs.get(key).get());
        } catch (Exception ex) {
          System.out.println("Exception:" + ex);
          ex.printStackTrace();
        }
    }


    public static boolean isAuthorized(String host, String uid, String password, String className)
    {
        try {

            LinkedHashList list = null;
            String uniqueMemberKey = "uid=" + uid + ",ou=People,ou=uc3,dc=cdlib,dc=org";
            String returnedAtts[] = { "uniqueMember" };
            String filter = "cn=write";
            String searchBase = "ou=" + className + ",ou=mrt-classes,ou=uc3,dc=cdlib,dc=org";
            list = search(
                    uid,
                    password,
                    host,
                    returnedAtts,
                    searchBase,
                    filter);

            boolean test = match(list, "uniqueMember", uniqueMemberKey);
            return test;

        } catch (Exception ex) {
          System.out.println("Exception:" + ex);
          ex.printStackTrace();
          return false;
        }
    }

    public static String getProfile(String host, String uid, String password, String className)
        throws TException
    {
        try {
            LinkedHashList list = null;
            String returnedAtts[] = { "ou", "submissionProfile" };
            String filter = "ou=" + className;
            String searchBase = "ou=mrt-classes,ou=uc3,dc=cdlib,dc=org";
            list = search(
                    uid,
                    password,
                    host,
                    returnedAtts,
                    searchBase,
                    filter);

            String profile = getProfile(list);
            if (DEBUG) System.out.println("PROFILE=" + profile);
            return profile;

        } catch (Exception ex) {
          System.out.println("Exception:" + ex);
          ex.printStackTrace();
          return null;
        }
    }
    public static LinkedHashList<String,String> search(
            String uid,
            String password,
            String host,
            String returnedAtts[],
            String searchBase,
            String filter)
        throws TException
    {
        try {
            if (DEBUG) System.out.println("search"
                    + " - uid=" + uid + SEP
                    + " - password=" + password + SEP
                    + " - host=" + host + SEP
                    + " - searchBase=" + searchBase + SEP
                    + " - filter=" + filter + SEP
                    );
            LinkedHashList list = null;
            String userKey = "uid=" + uid;
            String user = userKey + ",ou=People,ou=uc3,dc=cdlib,dc=org";
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, host);
            if (DEBUG) System.out.println("searchBase=" + searchBase);
            // Authenticate as S. User and password "mysecret"
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, user);
            env.put(Context.SECURITY_CREDENTIALS, password);

            // Create the initial context
            DirContext ctx = new InitialDirContext(env);

            SearchControls searchCtls = new SearchControls();
            searchCtls.setReturningAttributes(returnedAtts);
            //String filter = "&((cn=write)(uniqueMember=" +uniqueMemberKey + "))";
            NamingEnumeration<SearchResult> results = ctx.search(searchBase, filter, searchCtls);
            //System.out.println("before while - searchBase=" + searchBase);
            list = new LinkedHashList();
            while (results.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) results.next();
                if (DEBUG) System.out.println("FOUND OBJECT : " + searchResult.getName());
                Attributes attrs = searchResult.getAttributes();
                if (attrs != null) {
                    list = attributes2LinkedHash(list, attrs);
                }
            }
            ctx.close();
            return list;

        } catch (Exception ex) {
          if (DEBUG) System.out.println("Exception:" + ex);
          ex.printStackTrace();
          return null;
        }
    }


    private static boolean match(LinkedHashList<String,String> list, String name, String key)
    {
        System.out.println("match "
                + " - name=" + name
                + " - key=" + key
                );
        boolean ret = false;
        if (list == null) return false;
        if (list.get(name) == null) {
            System.out.println("**> " + name + "=EMPTY");
            return false;
        }
        try {
            System.out.println("***> " + name + " for " + key);
            Vector<String> content = list.get(name);
            for (String value : content) {
                System.out.println("== " + value);
                if (value.equals(key)) {
                    System.out.println("MATCH");
                    return true;
                }
            }
            return false;

        } catch (Exception ex) {
          System.out.println("Exception:" + ex);
          ex.printStackTrace();
          return false;
        }
    }
    private static String getProfile(LinkedHashList<String,String> list)
    {
        String name = "submissionProfile";
        if (DEBUG) System.out.println("match "
                + " - name=" + name
                );
        boolean ret = false;
        if (list.get(name) == null) {
            System.out.println("**> " + name + "=EMPTY");
            return null;
        }
        try {
            Vector<String> content = list.get(name);
            if ((content == null) || (content.size() == 0)) return null;
            return content.get(0);

        } catch (Exception ex) {
          System.out.println("getProfile Exception:" + ex);
          ex.printStackTrace();
          return null;
        }
    }

    public static String dump(String header, LinkedHashList<String,String> list)
    {
        StringWriter buf = new StringWriter();
        if ((list == null) || (list.size() == 0)) {
            buf.write(header + ": EMPTY");
            return buf.toString();
        }
        buf.write(header + ":" + SEP);
        for (String key: list.keySet()) {
            buf.write("***>" + key + "<" + SEP);
            for (String value : list.get(key)) {
                buf.write("   ->" + value + "<" + SEP);
            }
        }
        return buf.toString();
    }

    public static LinkedHashList<String,String> attributes2LinkedHash(Attributes attributes)
    {
        if (attributes == null) return null;
        LinkedHashList<String, String> prop = new LinkedHashList();
        try {
            NamingEnumeration e = attributes.getIDs();
            while (e.hasMore()) {
                String key = (String)e.next();
                String value = (String)attributes.get(key).get();
                prop.put(key, value);
                System.out.println("Add:" + key + "=" + value);
            }
            return prop;

        } catch (Exception ex) {
            System.out.println("2Exception:" + ex);
            return null;
        }

    }

    public static LinkedHashList<String,String> attributes2LinkedHash(
            LinkedHashList<String, String> list,
            Attributes attributes)
    {
        if (attributes == null) return null;
        try {
            NamingEnumeration e = attributes.getIDs();
            while (e.hasMore()) {
                String key = (String)e.next();
                Attribute attribute = attributes.get(key);
                NamingEnumeration v = attribute.getAll();
                while (v.hasMore()) {
                    String value = (String)v.next();
                    list.put(key, value);
                    if (DEBUG) System.out.println("Add:" + key + "=" + value);
                }
            }
            return list;

        } catch (Exception ex) {
            System.out.println("attributes2LinkedHash Exception:" + ex);
            return null;
        }

    }    public static boolean isAuthorizedOriginal(String uid, String password, String className)
        throws TException
    {
        try {
            LinkedHashList list = null;
            String userKey = "uid=" + uid;
            String uniqueMemberKey = userKey + ",ou=People,ou=uc3,dc=cdlib,dc=org";

            String user = userKey + ",ou=People,ou=uc3,dc=cdlib,dc=org";
            String url = "ldaps://coot.ucop.edu:1636";
            String searchBase = "ou=" + className + ",ou=mrt-classes,ou=uc3,dc=cdlib,dc=org";
            String returnedAtts[] = { "uniqueMember" };
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, url);

            // Authenticate as S. User and password "mysecret"
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, user);
            env.put(Context.SECURITY_CREDENTIALS, password);

            // Create the initial context
            DirContext ctx = new InitialDirContext(env);

            SearchControls searchCtls = new SearchControls();
            searchCtls.setReturningAttributes(returnedAtts);
            //String filter = "&((cn=write)(uniqueMember=" +uniqueMemberKey + "))";
            String filter = "cn=write";
            NamingEnumeration<SearchResult> results = ctx.search(searchBase, filter, searchCtls);
            //System.out.println("before while - searchBase=" + searchBase);
            list = new LinkedHashList();
            while (results.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) results.next();
                //System.out.println("FOUND OBJECT : " + searchResult.getName());
                Attributes attrs = searchResult.getAttributes();
                if (attrs != null) {
                    list = attributes2LinkedHash(list, attrs);
                }
            }
            boolean test = match(list, "uniqueMember", uniqueMemberKey);

            ctx.close();
            return test;

        } catch (Exception ex) {
          System.out.println("Exception:" + ex);
          ex.printStackTrace();
          return false;
        }
    }
}
